# Documentum xCP Plugin

 Allows for the configuration of build and/or deployment of Documentum xCP Applications in [jenkins](http://jenkins-ci.org/)

## Supported versions

 This plugin was only tested with xCP 2.1 . However, it should also work with early versions.
 
## Quick Installation

 Generate hpi package (*mvn package*) and upload the generated *documentum-xcp-plugin.hpi* file to your jenkins instance.

### Development

 Just download the project and import it as Maven Project to your favorite IDE.

 You can use the jenkins plugin tutorial to setup maven (info [here] [plugin_tutorial_envsetup]) settings.

### Debugging

 Just execute goal *hpi:run*
 ```sh
 $ mvn hpi:run
 ```

### TODO's

 - Generate test scripts.

License
----

TBD

[plugin_tutorial_envsetup]:https://wiki.jenkins-ci.org/display/JENKINS/Plugin+tutorial#Plugintutorial-SettingUpEnvironment