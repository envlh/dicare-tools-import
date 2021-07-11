## Description

`dicare-tools-import` is a Java project that handles the first step of import from Wikidata JSON dumps to *Related Properties* and *Related Projects*, generating CSV files using Wikidata Toolkit.

## Installation

Use Maven to generate the binary `target/wdtk-import.jar`:

    mvn clean install

## Configuration

There is no configuration file. If you want to change the directories used by this software, you will need to modify the lines at the beginning of `src/main/java/org/dicare/tools/wdtk/Main.java` and then regenerate a binary (see § Installation):

    private static final String DUMP_DIRECTORY = "/home/wikidata/";
    private static final String TMP_DIRECTORY = "/tmp/";

## Copyright

This project is under AGPLv3 license. See LICENSE and NOTICE files.

## See also

* [dicare-tools](https://github.com/envlh/dicare-tools)
