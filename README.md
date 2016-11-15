[![Build Status](https://travis-ci.org/gressy/gressy-entities.svg?branch=master)](https://travis-ci.org/gressy/gressy-entities)

Gressy-Entities
===============

Part of the Gressy Project.

This module provides a few goodies required for the auto-panel functionality:
- Base Entity class that all other entities should subclass
- Base User class, needed for the panel auth
- Panel Controller which provides basic CRUD for all entity types
- User Controller for panel login/logout
- Some misc helpers needed internally, which can be useful for your API too


Usage
-----

You can see an usage example at [gressy-demo](https://github.com/gressy/gressy-demo).


Developing Gressy
-----------------

Checklist before publishing new versions:
- Test the new release locally with the other projects by adding it to the local repository:

  > activator clean compile publish-local

- Check your environment and make sure you have the required credentials for Maven Central:
  - Check that an authorized PGP key is available

    > gpg --list-keys

  - Check for access credentials at local sbt config

    > cat ~/.sbt/0.13/sonatype.sbt

- Check that the version number has been updated and is correct.

- Do the actual publication.

  > activator clean compile publishSigned sonatypeRelease

And you're set! The new release should show in the search engine in an hour or two.

For more details, check the following resources:
- [sbt-sonatype](https://github.com/xerial/sbt-sonatype) plugin documentation
- [OSSRH Guide](http://central.sonatype.org/pages/ossrh-guide.html)
