Install the plugin via

$ camel plugin add forage --command='forage' --description='Forage Camel JBang Plugin' --artifactId='camel-jbang-plugin-forage' --groupId='org.apache.camel.forage' --version='1.0-SNAPSHOT' --gav='org.apache.camel.forage:camel-jbang-plugin-forage:1.0-SNAPSHOT'

and then, use the plugin

$ camel forage
Hello from Camel Forage!


Delete the plugin via

$ camel plugin delete forage

or remove the plugin from ~/.camel-jbang-plugins.json