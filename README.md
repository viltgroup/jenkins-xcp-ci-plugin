# Documentum xCP Plugin

 This plugin allows configuring build and/or deployment of Documentum xCP Applications in [jenkins](http://jenkins-ci.org/).

## Supported versions

 This plugin was only tested with xCP 2.1 . However, it should also work with new versions.
 
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

## License

This plugin is licensed under [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

## More Info

Check the [wiki](../../wikis/home) for more details on how to use the plugin.


[plugin_tutorial_envsetup]:https://wiki.jenkins-ci.org/display/JENKINS/Plugin+tutorial#Plugintutorial-SettingUpEnvironment