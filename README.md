Maven plugin to package IntelliJ plugins
----------------------------------------

This plugin provides a new packaging type, "ij-plugin". This packaging is almost the same as
"jar" packaging, but with both mojo added to default jar lifecycle.

The following mojos are provided by this plugin:

* copy-manifest copies plugin manifest (META-INF/plugin.xml by default) into the target/classes directory
  (so it is automatically packaged into JAR)
* package-plugin create .zip file with IntelliJ plugin (packages plugin JAR and dependent JARs into lib/ directory)

ijdeploy.sh
-----------

Script to automatically deploy IntelliJ SDK JARs to the Nexus.

Building and releasing
----------------------

This project is created according to the [Maven Best Practices](http://wiki/index.php/Maven/Best_Practices).

