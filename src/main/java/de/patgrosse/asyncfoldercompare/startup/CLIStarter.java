package de.patgrosse.asyncfoldercompare.startup;

import de.patgrosse.asyncfoldercompare.entities.filesystem.real.RootRealFolder;
import de.patgrosse.asyncfoldercompare.entities.filesystem.result.ResultFolder;
import de.patgrosse.asyncfoldercompare.entities.storage.Credentials;
import de.patgrosse.asyncfoldercompare.entities.storage.LastSettings;
import de.patgrosse.asyncfoldercompare.gui.compare.AFCTreeGUI;
import de.patgrosse.asyncfoldercompare.utils.FileTreeComparator;
import de.patgrosse.asyncfoldercompare.utils.GsonUtils;
import de.patgrosse.asyncfoldercompare.utils.VFSUtils;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.vfs2.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CLIStarter {
    private static final Logger LOG = LoggerFactory.getLogger(CLIStarter.class);

    public static void main(String[] args) throws Exception {
        AFCEnviroment.initJVM();

        CommandLineParser parser = new DefaultParser();
        Options options = new Options();

        OptionGroup actions = new OptionGroup();
        actions.addOption(Option.builder("gui").desc("Show the GUI").build());
        actions.addOption(Option.builder("mapjson").desc("Map to a temporary JSON file").build());
        actions.addOption(Option.builder("dummycompare").desc("Dummy compare to check core functionality").build());
        actions.setRequired(true);

        OptionGroup oldJSON = new OptionGroup();
        oldJSON.addOption(Option.builder("oldJSONFile").hasArg().desc("The path to the old mapped JSON file").build());
        oldJSON.addOption(Option.builder("oldFolderPath").hasArg().desc("The path to the old folder").build());
        oldJSON.setRequired(true);

        OptionGroup newJSON = new OptionGroup();
        newJSON.addOption(Option.builder("newJSONFile").hasArg().desc("The path to the new mapped JSON file").build());
        newJSON.addOption(Option.builder("newFolderPath").hasArg().desc("The path to the new folder").build());

        options.addOptionGroup(actions);
        options.addOptionGroup(oldJSON);
        options.addOptionGroup(newJSON);
        CommandLine line;
        try {
            line = parser.parse(options, args);
        } catch (ParseException e) {
            LOG.error("CLI parsing error", e);
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("javaCommand", options, true);
            return;
        }

        LastSettings lastSettings = GsonUtils.readLastSettings();

        if (line.hasOption("gui")) {
            FileTreeComparator tc = VFSUtils.createTreeComparator();
            Pair<RootRealFolder, FileObject> oldFolder = parseFolderFromCLI(tc, line, "old",
                    lastSettings.getCredentialsOld());
            Pair<RootRealFolder, FileObject> newFolder = parseFolderFromCLI(tc, line, "new",
                    lastSettings.getCredentialsNew());
            LOG.info("Starting compare");
            ResultFolder resFolder = tc.compareFolders(oldFolder.getLeft(), newFolder.getLeft());
            LOG.info("Finished compare");
            AFCTreeGUI.startGUI(tc, resFolder, oldFolder.getRight(), newFolder.getRight());
        } else if (line.hasOption("mapjson")) {
            FileTreeComparator tc = VFSUtils.createTreeComparator();
            Pair<RootRealFolder, FileObject> oldFolder = parseFolderFromCLI(tc, line, "old",
                    lastSettings.getCredentialsOld());
            LOG.info("Starting mapping");
            GsonUtils.saveFolderToJSON(tc, oldFolder.getLeft(), null, lastSettings.getCredentialsNew());
            LOG.info("Finished mapping");
        } else if (line.hasOption("dummycompare")) {
            FileTreeComparator tc = VFSUtils.createTreeComparator();
            Pair<RootRealFolder, FileObject> oldFolder = parseFolderFromCLI(tc, line, "old",
                    lastSettings.getCredentialsOld());
            Pair<RootRealFolder, FileObject> oldFolder2 = parseFolderFromCLI(tc, line, "old",
                    lastSettings.getCredentialsOld());
            LOG.info("Starting compare");
            ResultFolder resFolder = tc.compareFolders(oldFolder.getLeft(), oldFolder2.getLeft());
            LOG.info("Finished compare");
            if (LOG.isInfoEnabled()) {
                LOG.info("Result: " + resFolder.getCompareResult());
            }
        }
    }

    private static Pair<RootRealFolder, FileObject> parseFolderFromCLI(FileTreeComparator comp, CommandLine line,
                                                                       String paramPrefix, Credentials cred) throws Exception {
        if (line.hasOption(paramPrefix + "JSONFile")) {
            return VFSUtils.parseUserInput(comp, line.getOptionValue(paramPrefix + "JSONFile"), true, cred);
        } else if (line.hasOption(paramPrefix + "FolderPath")) {
            return VFSUtils.parseUserInput(comp, line.getOptionValue(paramPrefix + "FolderPath"), false, cred);
        } else {
            throw new Exception();
        }
    }

}
