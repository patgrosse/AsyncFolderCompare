package de.patgrosse.asyncfoldercompare.utils;

import de.patgrosse.asyncfoldercompare.constants.CompleteObjectCompareResult;
import de.patgrosse.asyncfoldercompare.entities.compareresults.CompleteFileCompareResultHolder;
import de.patgrosse.asyncfoldercompare.entities.compareresults.PluginFileCompareResultHolder;
import de.patgrosse.asyncfoldercompare.entities.filesystem.real.RealFile;
import de.patgrosse.asyncfoldercompare.entities.filesystem.real.RealFolder;
import de.patgrosse.asyncfoldercompare.entities.filesystem.real.RootRealFolder;
import de.patgrosse.asyncfoldercompare.entities.filesystem.result.ResultFile;
import de.patgrosse.asyncfoldercompare.entities.filesystem.result.ResultFolder;
import de.patgrosse.asyncfoldercompare.entities.filesystem.result.RootResultFolder;
import de.patgrosse.asyncfoldercompare.entities.storage.ScanSession;
import de.patgrosse.asyncfoldercompare.filter.Filter;
import de.patgrosse.asyncfoldercompare.filter.FilterHelper;
import de.patgrosse.asyncfoldercompare.matcher.MatchCallback;
import de.patgrosse.asyncfoldercompare.matcher.files.FileMatcher;
import de.patgrosse.asyncfoldercompare.matcher.folders.FolderMatcher;
import de.patgrosse.asyncfoldercompare.plugins.ComparePlugin;
import de.patgrosse.asyncfoldercompare.plugins.entities.CompareCheck;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class FileTreeComparator {
    private static Logger LOG = LoggerFactory.getLogger(FileTreeComparator.class);
    private Map<String, ComparePlugin> enabledPlugins;
    private FileMatcher fileMatcher;
    private FolderMatcher folderMatcher;
    private Filter<RealFile> fileFilter;
    private Filter<RealFolder> folderFilter;

    public FileTreeComparator(FileMatcher fileMatcher, FolderMatcher folderMatcher, Filter<RealFile> fileFilter,
                              Filter<RealFolder> folderFilter) {
        if (fileMatcher == null || folderMatcher == null || fileFilter == null || folderFilter == null) {
            throw new IllegalArgumentException();
        }
        this.enabledPlugins = new HashMap<>();
        this.fileMatcher = fileMatcher;
        this.folderMatcher = folderMatcher;
        this.fileFilter = fileFilter;
        this.folderFilter = folderFilter;
    }

    public void enablePlugin(ComparePlugin plugin) {
        enabledPlugins.put(plugin.getName(), plugin);
    }

    public boolean disablePlugin(String name) {
        return enabledPlugins.remove(name) != null;
    }

    public ScanSession createScanSession(RootRealFolder folder) {
        List<String> pluginNames = new LinkedList<>();
        pluginNames.addAll(enabledPlugins.keySet());
        return new ScanSession(folder, pluginNames);
    }

    public List<Pair<ComparePlugin, CompareCheck>> getPluginCompareResultColumns() {
        List<Pair<ComparePlugin, CompareCheck>> allCheckNames = new LinkedList<>();
        for (ComparePlugin plugin : enabledPlugins.values()) {
            for (CompareCheck pluginCheck : plugin.getCheckNames()) {
                allCheckNames.add(Pair.of(plugin, pluginCheck));
            }
        }
        return allCheckNames;
    }

    public RootRealFolder mapFolder(FileObject directory) throws FileSystemException {
        RootRealFolder rfolder = new RootRealFolder();
        LOG.info("Started mapping root folder " + directory.getName().getPath());
        mapFolder(rfolder, directory, new LinkedList<>());
        LOG.info("Finished mapping root folder " + directory.getName().getPath());
        return rfolder;
    }

    public ResultFolder compareFolders(RootRealFolder folderOld, RootRealFolder folderNew) {
        ResultFolder resultFolder = createResultFolder(folderOld, folderNew);
        compareFolder(resultFolder, folderOld, folderNew);
        return resultFolder;
    }

    private void mapFolder(RealFolder mapToFolder, FileObject directory, List<String> relativePath)
            throws FileSystemException {
        if (directory == null) {
            throw new IllegalArgumentException();
        }
        LOG.debug("Collecting data for folder " + directory.getName().getPath());
        for (FileObject file : directory.getChildren()) {
            if (file.equals(directory)) {
                continue;
            }
            if (file.isFolder()) {
                RealFolder rfolder = new RealFolder(file.getName().getBaseName());
                rfolder.setRelativePath(relativePath);
                if (folderFilter.isObjectFiltered(rfolder)) {
                    continue;
                }
                mapToFolder.addContainedFolder(rfolder);
                List<String> newRelativePath = new LinkedList<>(relativePath);
                newRelativePath.add(file.getName().getBaseName());
                mapFolder(rfolder, file, newRelativePath);
            } else {
                LOG.debug("Collecting data for file " + file.getName().getPath());
                RealFile rfile = new RealFile(file.getName().getBaseName());
                rfile.setRelativePath(relativePath);
                if (fileFilter.isObjectFiltered(rfile)) {
                    continue;
                }
                FileAttributeCollector collector = new FileAttributeCollector();
                for (ComparePlugin plugin : enabledPlugins.values()) {
                    try {
                        plugin.generateDataForFile(file, collector);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                FileAttributeStorage dataStorage = new FileAttributeStorage();
                dataStorage.inputFromCollector(collector);
                rfile.setDataStorage(dataStorage);
                mapToFolder.addContainedFile(rfile);
            }
        }
    }

    private ResultFolder createResultFolder(RealFolder oldFolder, RealFolder newFolder) {
        ResultFolder resultFolder = new RootResultFolder();
        createResultFolder(resultFolder, oldFolder, newFolder);
        return resultFolder;
    }

    private void createResultFolder(final ResultFolder joinTo, final RealFolder oldFolder, final RealFolder newFolder) {
        if (joinTo == null || (oldFolder == null && newFolder == null)) {
            throw new IllegalArgumentException();
        }
        // Create temporary collections for unmatched files
        final Collection<RealFile> unmatchedOldFiles, unmatchedNewFiles;
        if (oldFolder != null) {
            unmatchedOldFiles = FilterHelper.filterCollection(oldFolder.getContainedFiles(), fileFilter);
        } else {
            unmatchedOldFiles = new HashSet<>();
        }
        if (newFolder != null) {
            unmatchedNewFiles = FilterHelper.filterCollection(newFolder.getContainedFiles(), fileFilter);
        } else {
            unmatchedNewFiles = new HashSet<>();
        }
        // Match all files
        if (oldFolder != null && newFolder != null) {
            MatchCallback<RealFile> fileCallback = (matchName, oldFile, newFile) -> {
                ResultFile copy = new ResultFile(matchName);
                copy.setCorrespondingOld(oldFile);
                copy.setCorrespondingNew(newFile);
                copy.setRelativePath(new LinkedList<>(oldFile.getRelativePath()));
                joinTo.addContainedFile(copy);
                unmatchedOldFiles.remove(oldFile);
                unmatchedNewFiles.remove(newFile);
            };
            fileMatcher.matchObjects(Collections.unmodifiableCollection(oldFolder.getContainedFiles()),
                    Collections.unmodifiableCollection(newFolder.getContainedFiles()), fileCallback);
        }
        // Add all unmatched files
        for (RealFile unmatchedFile : unmatchedOldFiles) {
            ResultFile copy = new ResultFile(unmatchedFile.getName());
            copy.setCorrespondingOld(unmatchedFile);
            copy.setRelativePath(new LinkedList<>(unmatchedFile.getRelativePath()));
            joinTo.addContainedFile(copy);
        }
        for (RealFile unmatchedFile : unmatchedNewFiles) {
            ResultFile copy = new ResultFile(unmatchedFile.getName());
            copy.setCorrespondingNew(unmatchedFile);
            copy.setRelativePath(new LinkedList<>(unmatchedFile.getRelativePath()));
            joinTo.addContainedFile(copy);
        }
        // Create temporary collections for unmatched files
        final Collection<RealFolder> unmatchedOldFolders, unmatchedNewFolders;
        if (oldFolder != null) {
            unmatchedOldFolders = FilterHelper.filterCollection(oldFolder.getContainedFolders(), folderFilter);
        } else {
            unmatchedOldFolders = new HashSet<>();
        }
        if (newFolder != null) {
            unmatchedNewFolders = FilterHelper.filterCollection(newFolder.getContainedFolders(), folderFilter);
        } else {
            unmatchedNewFolders = new HashSet<>();
        }
        // Match all folders
        if (oldFolder != null && newFolder != null) {
            MatchCallback<RealFolder> folderCallback = (matchName, oldFolder1, newFolder1) -> {
                ResultFolder copy = new ResultFolder(matchName);
                copy.setCorrespondingOld(oldFolder1);
                copy.setCorrespondingNew(newFolder1);
                copy.setRelativePath(new LinkedList<>(oldFolder1.getRelativePath()));
                joinTo.addContainedFolder(copy);
                unmatchedOldFolders.remove(oldFolder1);
                unmatchedNewFolders.remove(newFolder1);
            };
            folderMatcher.matchObjects(Collections.unmodifiableCollection(oldFolder.getContainedFolders()),
                    Collections.unmodifiableCollection(newFolder.getContainedFolders()), folderCallback);
        }
        // Add all unmatched folders
        for (RealFolder unmatchedFolder : unmatchedOldFolders) {
            ResultFolder copy = new ResultFolder(unmatchedFolder.getName());
            copy.setCorrespondingOld(unmatchedFolder);
            copy.setRelativePath(new LinkedList<>(unmatchedFolder.getRelativePath()));
            joinTo.addContainedFolder(copy);
        }
        for (RealFolder unmatchedFolder : unmatchedNewFolders) {
            ResultFolder copy = new ResultFolder(unmatchedFolder.getName());
            copy.setCorrespondingNew(unmatchedFolder);
            copy.setRelativePath(new LinkedList<>(unmatchedFolder.getRelativePath()));
            joinTo.addContainedFolder(copy);
        }
        // Recursive match all
        for (ResultFolder createdFolder : joinTo.getContainedFolders()) {
            createResultFolder(createdFolder, createdFolder.getCorrespondingOld(), createdFolder.getCorrespondingNew());
        }
    }

    private void compareFolder(ResultFolder resultFolder, RealFolder oldFolder, RealFolder newFolder) {
        if (oldFolder == null || newFolder == null) {
            throw new IllegalArgumentException();
        }
        boolean foldersDiffer = false;
        for (ResultFile rfile : resultFolder.getContainedFiles()) {
            if (rfile.getCorrespondingOld() == null) {
                rfile.setCompareResult(CompleteObjectCompareResult.NEW);
                foldersDiffer = true;
            } else if (rfile.getCorrespondingNew() == null) {
                rfile.setCompareResult(CompleteObjectCompareResult.DELETED);
                foldersDiffer = true;
            } else {
                rfile.setFullResult(compareFiles(rfile.getCorrespondingOld(), rfile.getCorrespondingNew()));
                if (rfile.getCompareResult() != CompleteObjectCompareResult.MATCH) {
                    foldersDiffer = true;
                }
            }
        }
        for (ResultFolder rfolder : resultFolder.getContainedFolders()) {
            if (rfolder.getCorrespondingOld() == null) {
                setRecursiveCompareResult(rfolder, CompleteObjectCompareResult.NEW);
                foldersDiffer = true;
            } else if (rfolder.getCorrespondingNew() == null) {
                setRecursiveCompareResult(rfolder, CompleteObjectCompareResult.DELETED);
                foldersDiffer = true;
            } else {
                compareFolder(rfolder, rfolder.getCorrespondingOld(), rfolder.getCorrespondingNew());
                if (rfolder.getCompareResult() != CompleteObjectCompareResult.MATCH) {
                    foldersDiffer = true;
                }
            }
        }
        resultFolder.setCompareResult(
                foldersDiffer ? CompleteObjectCompareResult.DIFFER : CompleteObjectCompareResult.MATCH);
    }

    private void setRecursiveCompareResult(ResultFolder folder, CompleteObjectCompareResult resultType) {
        if (folder == null) {
            throw new IllegalArgumentException();
        }
        folder.setCompareResult(resultType);
        for (ResultFile containedFile : folder.getContainedFiles()) {
            containedFile.setCompareResult(resultType);
        }
        for (ResultFolder containedFolder : folder.getContainedFolders()) {
            setRecursiveCompareResult(containedFolder, resultType);
        }
    }

    private CompleteFileCompareResultHolder compareFiles(RealFile fileOld, RealFile fileNew) {
        CompleteFileCompareResultHolder fullResult = new CompleteFileCompareResultHolder();
        for (ComparePlugin plugin : enabledPlugins.values()) {
            PluginFileCompareResultHolder results = plugin.compareFiles(
                    new FileAttributeDisposer(fileOld.getDataStorage().getAllData()),
                    new FileAttributeDisposer(fileNew.getDataStorage().getAllData()));
            fullResult.setPluginResults(plugin.getName(), results);
        }
        fullResult.calculateTotal();
        return fullResult;
    }
}
