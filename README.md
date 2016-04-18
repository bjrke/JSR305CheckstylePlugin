JSR 305 Checkstyle Plugin [![Build Status](https://travis-ci.org/bjrke/JSR305CheckstylePlugin.svg?branch=master)](https://travis-ci.org/bjrke/JSR305CheckstylePlugin)
=====================================================================================================================================================================

A checkstyle plugin which ensures nullness annotations on methods and constructors.

Requirements
============

JDK 7+
checkstyle 6.2+

Notice: checkstyle 6.14 and 6.16 are known as broken, use 6.14.1, 6.16.1 or any new version

Usage
=====

To use copy JSR305Checkstyle-x.x.x.jar into your eclipse dropins directory or add it as compile lib to the classpath of the checkstyle task.

Add this to your checkstyle.xml:

    <module name="Jsr305Annotations">
        <property name="packages" value="your.package.path.to.check,another.package.path.to.check"/>
    </module>

and enjoy


Development
===========

It is a gradle project with included gradle wrapper.

to develop import project to eclipse with default settings after typing

    ./gradlew eclipse

to build the jar type

    ./gradlew jar
