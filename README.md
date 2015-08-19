# BugClerk

## Goals

### What it is ? What would it help or solve ?

*   A tool to ensure **BZ data and metada are compliant to Red Hat processes and expectations**, ex:

*   If a BZ is switched to POST, it will check that a PR has been referenced
*   or simply look at for a counterexample [BZ1160715](https://bugzilla.redhat.com/show_bug.cgi?id=1160715)
**   It leverages our convention and process to implement a sort of quality control on the Bugzilla entry
*   It's called BugClerk to help refering to it (rather the "tool"), and uses the image of a clerk checking your files and folder when submitting a request

### What it is NOT ? What it won't aim to resolve ?

*   It's not a workflow engine - BZ and/or JIRA has their own workflow engine, so it won't for instance change a BZ status
*   it won't solve the tracking of fixes across several version (at least not the first implementation)

## State of the art

I've googled a bit and ask around, and for now, I have not find any existing project or solution already aiming at that. Some people have obviously mentioned that bug tracker have **some workflow mechanism**, but also some feature, like the [JIRA custom field](https://confluence.atlassian.com/display/JIRA/Configuring+a+Custom+Field) and validation, that could implements the use cases I've mentioned.

Of course, if you do find some tools or library that , please feel free to update this section !_

### Component / Frameworks

* [jboss-set/pull-shared &middot; GitHub](https://github.com/jboss-set/pull-shared "https://github.com/jboss-set/pull-shared")&zwnj;
* [jboss-set/pull-request-processor &middot; GitHub](https://github.com/jboss-set/pull-request-processor "https://github.com/jboss-set/pull-request-processor")
* [Drools - Drools - Business Rules Management System (Java&trade;, Open Source)](http://www.drools.org/ "http://www.drools.org/")
* [Java A&zwnj;PI to acess Bugzilla](http://stackoverflow.com/questions/630095/is-there-a-java-api-to-access-bugzilla)

## How to run it ?

You need to have credentials to access BugZilla and file it in the provided example file (remove the .example) extension.

You also need to add the file 'org/jboss/jbossset/bugclerk/smtp/config.properties' to your classpath to configure sending report email. If not the properties are missing, BugClerk will simply NOT send email.

Once you have all of this here is how you can run BugClerk on a specific Bugzilla entry:

$ java -cp ${BUGCLERK_HOME}/bugclerk-${VERSION}.jar org.jboss.jbossset.bugclerk.cli.BugClerkCLI 1199194  -u 'https://bugzilla.redhat.com/show_bug.cgi?id='

Or you can simply retrieve all the BZs in a filter and have Bugclerk analyze them:

$ java -jar ${BUGCLERK_HOME}/bugclerk-${VERSION}.jar -H https://bugzilla.redhat.com/index.cgi -f 'https://bugzilla.redhat.com/buglist.cgi?cmdtype=runnamed&list_id=3525838&namedcmd=bz-created-on-EAP-in-the-last-hour&ctype=csv'

For commodity purpose, some launch scripts have been provided in src/main/bash.

## How to build it ?

As usual, just run "mvn install".

## How to release ?

Using the [Maven Release Plugin](http://maven.apache.org/maven-release/maven-release-plugin/), just follow its documentation. Then test using the run.sh and filter-based-run.sh scripts to ensure the resulting JAR is working properly. If new checks have been added, run the script update-rules-in-readme.sh and update the list at the bottom of this readme file.

## **Checks**

_Pretty much like a [Checkstyle]() or [PMD](http://github.com/pmd/pmd) configuration, BugClerk values resides in the number of things it can checks. So, please to do hesitate to post "check request" on the [issue tracker](https://github.com/jboss-set/bug-clerk/issues/)._


* [BZShouldHaveDevAckFlag](https://github.com/jboss-set/bug-clerk/tree/master/src/main/resources/org/jboss/jbossset/bugclerk/BZShouldHaveDevAckFlag.drl)
* [BZShouldHaveQaAckFlag](https://github.com/jboss-set/bug-clerk/tree/master/src/main/resources/org/jboss/jbossset/bugclerk/BZShouldHaveQaAckFlag.drl)
* [ComponentUpgradeMissingFixList](https://github.com/jboss-set/bug-clerk/tree/master/src/main/resources/org/jboss/jbossset/bugclerk/ComponentUpgradeMissingFixList.drl)
* [IssueNotAssigned](https://github.com/jboss-set/bug-clerk/tree/master/src/main/resources/org/jboss/jbossset/bugclerk/IssueNotAssigned.drl)
* [PostMissingPR](https://github.com/jboss-set/bug-clerk/tree/master/src/main/resources/org/jboss/jbossset/bugclerk/PostMissingPR.drl)
* [ReleaseVersionMismatch](https://github.com/jboss-set/bug-clerk/tree/master/src/main/resources/org/jboss/jbossset/bugclerk/ReleaseVersionMismatch.drl)
* [TargetRelease](https://github.com/jboss-set/bug-clerk/tree/master/src/main/resources/org/jboss/jbossset/bugclerk/TargetRelease.drl)
