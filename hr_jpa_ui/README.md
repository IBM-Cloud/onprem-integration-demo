# Vaadin JPA CRUD

This is an example application that shows how your can build rich UIs for your Bluemix backed execution environment (Liberty server and DB2 database) with the opensource Vaadin Framework.

### Automatic deployment to Bluemix

[![Deploy to Bluemix](https://bluemix.net/deploy/button.png)](https://bluemix.net/deploy)

When you click this button, Bluemix will clone this repository to a private Bluemix DevOps Services project, create a pipeline to compile the source, create the necessary database service (see `manifest.yml`) and then push the application.

### Manual deployment to Bluemix

Before you start, make sure you have Java SDK 1.7 (or higher) and Maven installed. Also [install cloudfoundry command line tools](https://www.ng.bluemix.net/docs/#cli/index.html#cli) and configure them for Bluemix.

To build the project, just execute the following commands in order:

```
git clone https://hub.jazz.net/git/vaadin/vaadin-jpa-app
cd vaadin-jpa-app
mvn install
```

or execute the same goal via your IDE.

In Bluemix you need to prepare an execution environment, that provides a Java EE 6 server and a database mapped to "jdbc/vaadindb". The easiest way to accomplish this, is to is to use the Vaadin boilerplate provided from within the Bluemix Catalog. Just follow the "}>" logo in [Bluemix](http://bluemix.net/) catalog. Manually, you can create an SQLDB service with the name "vaadindb". Naturally you can also use a different name, but then you'll need to modify persistence.xml in src/main/resources/META-INF accordingly.

Once you have the execution environment ready, *DELETE* the `manifest.yml` file and push the application to Bluemix. Replace <app-name> with the name of the Bluemix execution environment you created in the previous step.
```
rm manifest.yml
cf push <app-name> -p target/vaadin-jpa-application.war
```
... and you have your first Vaadin app deployed to Bluemix!

### Local development

If you want to develop/debug the application locally, you'll just need to introduce the data source in your local WAS Liberty Profile development server and deploy it there e.g. via your favorite IDE. Virtually any DB works, so if you are e.g. using Mac as you development environment, and can't start DB2, you can still debug the application locally. E.g. an in memory Derby server works just fine, simple instructions below.

* Download and place a derby.jar file to usr/shared/resources/derby/derby.jar into your Liberty server directory.
* Enable required features and a Derby based datasource by configuring your development server's server.xml (most likely usr/servers/defaultServer/server.xml in your Liberty server directory). It could look like this:
```
<server description="new server">
  <!-- Enable features, jpa-2.0, cdi-1.0 and servlet-3.0 are required, but handier to just
       enable the whole webProfile specification -->
  <featureManager>
    <feature>localConnector-1.0</feature>
    <feature>webProfile-6.0</feature>
  </featureManager>
  <!-- To access this server from a remote client add a host attribute to 
		the following element, e.g. host="*" -->
  <httpEndpoint httpPort="9080" httpsPort="9443" id="defaultHttpEndpoint"/>
  <!-- JDBC Driver configuration -->
  <jdbcDriver id="DerbyEmbedded" libraryRef="DerbyLib"/>
  <library filesetRef="DerbyFileset" id="DerbyLib"/>
  <fileset dir="${shared.resource.dir}/derby" id="DerbyFileset" includes="derby.jar"/>
  <!-- Configure an in-memory db for the vaadin app configuration -->
  <dataSource id="jdbc/vaadindb" jdbcDriverRef="DerbyEmbedded" jndiName="jdbc/vaadindb" transactional="true">
    <properties createDatabase="create" databaseName="memory:jpasampledatabase"/>
  </dataSource>
</server>
```

If you are using Eclipse, it might be bit picky about configuring the project properly based on the pom.xml. Via IntelliJ the deployment works usually easier. Couple of Eclipse related tips to setup the project:
 
 * Import to your workspace using File->Import->Maven->Existing Maven project
 * If Eclipse creates an "ear project" in addition to the war project, you can just delete the ear project
 * Make one full build with "Run as-> Maven install" after import to get client side resources prepared
 * In case Eclipse asks to modify server.xml during deployment, just ignore it. For some reason Eclipse may "detect" that project needs services it really don't need. webProfile-6.0 in server xml (and a connector) is enough.
 * In case it still don't work, get a fresh Eclipse, install only latest Liberty Profile plugins and try again. Some Eclipse plugins may disturb the process.

### Troubleshooting

**The application doesn't build properly** 

If you have [Maven](https://maven.apache.org/download.cgi) and Java 7 or later installed, the most common problem is that you are using a Macintosh and your JAVA_HOME environment variable still points to version 1.6 of Java. An easy way to fix this is executing: 
```export JAVA_HOME=`/usr/libexec/java_home -v 1.7` ```
and/or adding that to your .bash_profile file.

Also note, that if you haven't used Maven before, the build may take several minutes during the first run as Maven downloads several dependencies used by the application itself and the build.
 
**The deployment fails**

Deploying and setting up the database may take a while with a slow network connection, so be patient. In some cases there might happen an error due to e.g. network communication. Canceling the deployment with CTRL-C and trying again usually fixes the issue.