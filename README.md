# AsyncFolderCompare

[![Travis build status](https://travis-ci.org/patgrosse/AsyncFolderCompare.svg?branch=master)](https://travis-ci.org/patgrosse/AsyncFolderCompare)
[![Gitlab build status](https://gitlab.com/patgrosse/AsyncFolderCompare/badges/master/build.svg)](https://gitlab.com/patgrosse/AsyncFolderCompare/commits/master)

A simple folder comparisation tool that lets you sync folders at different times in different places

## Supported OS
* Windows (32 and 64 bit)
* Mac OS X
* Linux (x86 32 and 64 bit, amd64)

## Build instructions

### Required software
* JDK 8
* Maven (only tested with v3)

### Build commands
Run the following command to build AsyncFolderCompare manually:
```bash
mvn package
```
This will create two runnable JAR files:
* `target/asyncfoldercompare-x-gui.jar`: graphical frontend application
* `target/asyncfoldercompare-x-cli.jar`: command line application