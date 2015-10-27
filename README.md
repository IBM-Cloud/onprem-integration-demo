# Integrating a Bluemix app with data residing in an on-premises data center

This project shows how a modern web application (running on [Bluemix](https://www.bluemix.net)) integrates with a data base that is located in a customers data center. It is meant to illustrate an application in the public cloud is used as a ["System of Engagement"](https://en.wikipedia.org/wiki/Systems_of_Engagement), while sensitive data remains in a ["System of Record"](https://en.wikipedia.org/wiki/System_of_record) and inside a customers firewall.

We have chosen to implement the Bluemix application in node.js and are using the [secure gateway service][secure_gateway_docs_url] to implement an encrypted connection between it and the on-premise [MySQL](https://en.wikipedia.org/wiki/MySQL) database.

For many large customers, the core data that drives their business resides in established database systems behind their firewall, accessed through classic middleware (i.e., an Oracle or DB2 database accessed with .Net software). While this system sustains their business, its evolution is generally slow to non-existent. This structure does not promote the creation of nimble and engaging apps that employees and customers require.

By connecting modern cloud applications to these on-prem systems, we are able to quickly create quality user experiences, while safely surfacing the data that we need. Read on to find out how anyone can quickly do this by using IBM Bluemix.

![](https://raw.githubusercontent.com/IBM-Bluemix/onprem-integration-demo/master/Architecture.png?token=AFP3905kOoeJUFAYGzPbQgMuU_Q4RImlks5WBcJvwA%3D%3D)

## Installation
There are several components that need to be set up before being able to give this demo. These are all related to setting up a "mock" back-end system of record that our cloud application will connect to. We estimate it will take you **45 minutes** to run through the steps, be aware that **you only have to do this once per Bluemix account**. The "mock" back-end can then be re-used for future demos.

**Important Note**: During the following steps, you may see frequent warnings in your console stating `sudo: unable to resolve host vm-###` when running `sudo` root commands. You can safely ignore them.

For convenience, we have split the steps up into 4 phases:

### Overview

**Phase 1:** Instantiate an OpenStack virtual machine (VM), which will simulate an on-premises data center.

**Phase 2:** Install a MySQL database instance and seed it with records. This will simulate a database filled with human resource (HR) data.

**Phase 3:** Create a Secure Gateway and connect it to the database running in the VM.

**Phase 4:** Create the app (based on [Vaadin] (https://vaadin.com/home) and JPA [Liberty] (https://en.wikipedia.org/wiki/IBM_WebSphere_Application_Server)), deploy it to Bluemix, then connect it to the Secure Gateway endpoint.

### Phase 1: Create a Bluemix Virtual Machine (VM)

We will use a VM in this demo to represent our on-premises data center and will host a MySQL instance in it. This represents our "System of Record".

1. Create a Bluemix Account

    [Sign up for Bluemix][bluemix_signup_url] or use an existing account.

2. Create a VM from the console dashboard by clicking on `Run Virtual Machines`

	**Note**: If you do not yet have access to the Bluemix VM beta, complete your request and wait for your confirmation email. This may take up to a few days, so please be patient!

	a) Select the `Ubuntu 14.04` image for your VM  

	b) Give the VM group a name. We suggest something that identifies it as your "on-premises data center". Alphanumeric or "_-." characters are allowed but no spaces.

	c) Select `SINGLE` as type.

	d) Select the `m1.small` size, equivalent to 1.5 GB memory and 1 CPU

	e) Toggle `Assign pubic IP address` to make the VM accessible from outside Bluemix. Note that this public IP address is needed only to administrate the VM directly from your computer.

	f) Create an SSH key for securely connecting to your VM. For instructions on how to do this, check out the [documentation][vm_ssh_key_docs]  

	g) Default to the `private` network  

	h) Click `CREATE` to create and launch your VM. Once it has started, take note of your public IP address on the VM dashboard. The IP address is needed during some steps later on.

3. Open a terminal and make sure that your private key file is in your working directory. It needs to have the correct permissions, to set them use the command:

	```sh
	$ chmod 700 ./NameOfMyPrivateKeyFile.pem
	```

4. Use the ssh command to log into your newly created VM. Make sure to substitute the public IP address of your VM (it should start with 129) for XXX.XX.XXX.XX.

	```sh
	$ ssh -i ./NameOfMyPrivateKeyFile.pem ibmcloud@XXX.XX.XXX.XX
	```

	If you receive a "No route to host" error, it is an indicator that the network fabric has not yet completed the setup. Wait a minute or two and retry.

5. Resync your VM's package index files from their sources:

	```sh
	$ sudo apt-get update
	```

### Phase 2: Install a MySQL database instance and seed it with records

6. Install MySQL on your VM:

	```sh
	$ sudo apt-get install mysql-server
	```

	**Note**: During the installation process you will be asked to assign a password to your MySQL server. Make sure to write it down, you will need it for database access.

7. Open up port 3306 in the VM's firewall. It is used by MySQL to listen for incoming requests:

	```
	$ sudo ufw allow 3306/tcp
	```

8. Grant remote access to your MySQL DB instance (by using the password you assigned in step 1 above) and then restart the mysql service:

	```
	$ mysql -u root -p
	Enter password: <PasswordFromStep1>

	mysql> GRANT ALL ON *.* to root@'%' IDENTIFIED BY '<PasswordFromStep1>';
	mysql> flush privileges;
	mysql> exit

	$ sudo service mysql restart
	```

9. Seed your MySQL DB with sample data from this repo:

	```
	$ wget https://raw.githubusercontent.com/IBM-Bluemix/onprem-integration-demo/master/db.sql

	$ mysql -u root -p -t < db.sql
	```

### Phase 3: Create a Secure Gateway Connection
Create a secure connection between your Bluemix app and the database running in your VM.

1. Navigate to the Bluemix catalog and select the new [Secure Gateway service][secure_gateway_catalog_url]. Choose `Leave Unbound` for now and click `CREATE`.

2. In the secure gateway console choose `ADD GATEWAY` and:

	a) Give your gateway a name, toggle `Enforce security token on client` so that it is not active, and then click on `ADD DESTINATION`  
	b) Give the destination a name, enter `127.0.0.1` as IP address, port 3306, keep TCP selected, and click the `+` button. The loopback ip address (127.0.0.1) is used because the Secure Gateway client connects to the MySQL server locally.

	c) Click `CONNECT IT` to retrieve the command you will need to establish the secure connection from your VM. There are options for the native installer (IBM Installer), running a docker image or using IBM DataPower.
	Select `IBM Installer`, the screen should look as shown. Note down the Gateway ID. It is needed in step 3b. ![](https://raw.githubusercontent.com/IBM-Bluemix/onprem-integration-demo/master/screenshots/sg-native-installer.png)


3. Install the Secure Gateway client into your VM using the native installer

	a) Within the terminal of your VM download the native installer using the `wget` command. The file name needs to match the one shown in step 2c.
	```
	$ wget https://sgmanager.ng.bluemix.net/installers/ibm-securegateway-client-1.3.1+client_amd64.deb
	```
	b) Create an Access Control List (ACL) to allow access to the tcp port used by MySQL. The following first creates the needed directory, then the file "sgacl.conf" and populates it with the rule `acl allow :3306` - allow access to port 3306.
	```
	$ sudo mkdir /etc/ibm

	$ sudo bash -c 'echo "acl allow :3306" > /etc/ibm/sgacl.conf'

	```
	c) Install and configure the Secure Gateway using the native installer.
	```
	$ sudo dpkg -i ibm-securegateway-client-1.3.1+client_amd64.deb
	```
	During the install process you are prompted several times for input.
	* For "Stop and restart the client..." type in "y".
	* When prompted for the "gateway ID", enter the id obtained in step 2c above.
	* When asked to "supply an ACL File", specify "/etc/ibm/sgacl.conf" - the file created above.
	* You can just hit enter and go with the defaults for everythin else.
	Once the install process is finished, the Secure Gateway client should start automatically.

	d) Verify the Secure Gateway client is connected
	```
	$ cat /var/log/securegateway/client_console.log

	```
	The output should indicate that "The Secure Gateway tunnel is connected".

4. The Bluemix dashboard should also indicate that the Secure Gateway is now connected. It can be checked in the secure gateway console:

	![](https://raw.githubusercontent.com/IBM-Bluemix/onprem-integration-demo/master/screenshots/gateway-connected.jpg)

### Phase 4: Deploy the Bluemix App
Now that we have a connection to our MySQL data base (serving as a stand-in for our on-premises SoR) established, we will turn to our System-of-Engagment application. It will ingest (through the secure gateway) and then display the data.

The have chosen a modern web application, written in Java and making use of the Vaadin user interface library.

As a prerequisite, you need Java to be installed on your local machine. If you have not done so, follow [these instructions][java_install_url].

To clone, build and deploy the app on Bluemix, follow these steps:

1. Clone the  github code repository, navigate to the app folder, and install the Maven dependencies:

	```
	$ git clone https://github.com/IBM-Bluemix/onprem-integration-demo.git

	$ cd onprem-integration-demo/hr_jpa_ui/
	```

2. We will use [Apache Maven][maven_home_url] as our build tool for Java. If you have not done so already, you need to [download][maven_download_url] and [install it][maven_install_url].

3. Build your app .war file using Maven:

	```
	mvn install
	```

4. Update the `manifest.yml` file with a unique host name for your new app (which we will refer to as APPNAME in the remaining steps).

5. Download and install the [Cloud Foundry CLI][cloud_foundry_url] tool if you have not already.

6. Connect to Bluemix using the CLI and follow the prompts to log in.

	```
	$ cf login -a https://api.ng.bluemix.net
	```

7. Push your app to Bluemix:

	```
	cf push <APPNAME> -p target/vaadin-jpa-application.war
	```

8. Create a user provided service to broker communication to your MySQL DB:

	```
	cf cups mysql-mine -p '{
	"jdbcUrl": "jdbc:mysql://cap-sg-prd-y.integration.ibmcloud.com:xxxxx/employees",
	"uri": "mysql://cap-sg-prd-y.integration.ibmcloud.com:xxxxx/employees?reconnect=true",
	"name": "employees",
	"hostname": "cap-sg-prd-y.integration.ibmcloud.com",
	"port": "xxxxx",
	"user": "root",
	"password": "password"
	}'
	```
	The "password" is the administrator password you chose during the MySQL setup. The port number and the exact hostname can be obtained from the Secure Gateway dashboard by clicking on the "i" (info) icon as shown:

	![](https://raw.githubusercontent.com/IBM-Bluemix/onprem-integration-demo/master/screenshots/BluemixSecureGateway_CloudHost.png)

9. Now bind the service to your app.

	```
	$ cf bind-service <APPNAME> mysql-mine
	```

10. Finally, we need to restage our app to ensure the environment variables changes took effect.

	```
	$ cf restage <APPNAME>
	```

## Decomposition Instructions
<Instructions on how a developer/architect would take the sample application and extract the relevant code for reuse.>

## API documentation
<If one or more of the apps in the demo exposes an API, provide a short explanation and how it is used in the sample. Link out to the published REST API documentation.>

## Contribute
We are more than happy to accept external contributions to this project, be it in the form of issues and pull requests. If you find a bug, please report it via the [Issues section][issues_url] or even better, fork the project and submit a pull request with your fix! Pull requests will be evaulated on an individual basis based on value add to the sample application.

## Troubleshooting

The primary source of debugging information for your Bluemix app is the logs. To see them, run the following command using the Cloud Foundry CLI:

```
$ cf logs <APPNAME> --recent
```
For more detailed information on troubleshooting your application, see the [Troubleshooting section](https://www.ng.bluemix.net/docs/troubleshoot/tr.html) in the Bluemix documentation.

### Links to more information

[IBM Redbook: Secure Cloud-to-Mainframe Connectivity with IBM Bluemix][cloud_mainframe_redbook_url]


[bluemix_url]: https://console.ng.bluemix.net/?cm_mmc=Display-SampleApp-_-BluemixSampleApp-CapitalWeather-_-Node-WeatherChannel-_-BM-DevAd

[bluemix_signup_url]: https://ibm.biz/on-prem-integration-signup

[java_install_url]: https://www.java.com/en/download/help/index_installing.xml

[maven_home_url]: https://maven.apache.org/index.html

[maven_download_url]: https://maven.apache.org/download.cgi

[maven_install_url]: https://maven.apache.org/install.html

[cloud_foundry_url]: https://github.com/cloudfoundry/cli

[secure_gateway_docs_url]: https://www.ng.bluemix.net/docs/#services/SecureGateway/index.html

[vm_ssh_key_docs]: https://www.ng.bluemix.net/docs/virtualmachines/vm_index.html#vm_ssh_key

[vim_cheatsheet_url]: http://www.fprintf.net/vimCheatSheet.html

[secure_gateway_catalog_url]: https://console.ng.bluemix.net/catalog/secure-gateway/

[cloud_mainframe_redbook_url]: http://www.redbooks.ibm.com/redpapers/pdfs/redp5243.pdf
