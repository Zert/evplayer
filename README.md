; -*- Mode: Markdown; -*-

# EvPlayer

EvPlayer is just a launcher for internal Android mediaplayer for specified stream, based on MediaPlayerDemo_Video.java from SDK.

## Getting Started

Since EvPlayer written in Scala, you must prepare build environment:

 1. Put in *tool/* directory following jar-files:
    - proguard.jar
    - retrace.jar
    - scala-compiler.jar
    - scala-library.jar
 2. Copy following files in toplevel directory, then edit it, or just make symlinks:
    - tools/build/build.properties
    - tools/build/build.xml
    - tools/build/default.properties
    - tools/build/local.properties
 3. Plug your Android device or run emulator
 4. Run `ant install`
