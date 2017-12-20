JSR 305 Checkstyle Plugin [![Build Status](https://travis-ci.org/bjrke/JSR305CheckstylePlugin.svg?branch=master)](https://travis-ci.org/bjrke/JSR305CheckstylePlugin)
=====================================================================================================================================================================

A checkstyle plugin which ensures nullness annotations on methods and constructors.

Requirements
============

* JDK 8
* checkstyle 6.16.1+

Usage
=====

Copy JSR305Checkstyle-x.x.x.jar into your eclipse dropins directory or add it to the classpath of your checkstyle task.

Add this to your checkstyle.xml:

    <module name="Jsr305Annotations">
        <property name="packages" value="your.package.path.to.check,another.package.path.to.check"/>
    </module>

and enjoy


Development
===========

It is a gradle project with included gradle wrapper.

To develop, import project to eclipse with default settings after typing:

    ./gradlew eclipse

To build the jar type:

    ./gradlew jar

Release
=======

This project uses an annotated git tag to get the version number. To release you have to:

- create an annotated tag

      git tag -a X.Y.Z

  where X.Y.Z is the version number to be released

- push tags to https://github.com/bjrke/JSR305CheckstylePlugin/tree/master (the master branch)

      git push --tags origin master

  You can also do this by draft a release through https://github.com/bjrke/JSR305CheckstylePlugin/releases

- build the release by calling

      ./dist

  This will run a docker build environment which ensures a common JDK is used (currently OpenJDK8).

- upload the result to

  https://github.com/bjrke/JSR305CheckstylePlugin/releases
