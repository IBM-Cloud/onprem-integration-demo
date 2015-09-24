# on-premises-integration Demo
This project is a model example of using the public cloud as a System of Engagement to connect back to an existing System of Record. By using a [secure gateway][secure_gateway_docs] connection, our Node.js app on [Bluemix][bluemix_url] is able to utilize data from an on-premises MySQL instance.

For many large customers, the core data that drives their business lives in established databases behind their firewall, accessed through classic middleware (i.e. Oracle Database accessed with .Net software). While this system sustains their business, its evolution is generally slow to non-existent. This structure does not promote the creation of nimble and engaging apps that employees and customers of the business require.

Through connecting apps on cloud platforms to these on-prem systems, we are able to quickly create quality user experiences while safely surfacing the data that we need. Read on to find out how anyone can quickly model this entire process using IBM Bluemix.

![alt tag](https://raw.githubusercontent.com/IBM-Bluemix/onprem-integration-demo/master/Architecture.png?token=AFP3905kOoeJUFAYGzPbQgMuU_Q4RImlks5WBcJvwA%3D%3D)

## Installation
There are several components to setting up the system that supports this demo. To get everything set up, we estimate it will take you **XX** minutes. For convenience, we have split everything up into phases:

### Overview

1) Instantiate an OpenStack VM - Host a MySQL DB instance to simulate an enterprise data center.

2) Create a Secure Gateway -  Connect to the database running in the VM.

3) Create the app - Push our Vaadin JPA Liberty app to Bluemix and connect it to Secure Gateway endpoint.

TODO: Watson, weather, APIm, mobile, ...

### Phase 1: Create a Bluemix VM
We will use a VM in this demo to represent our enterprise data center. We will host a MySQL instance in this "data center" and use it as our application's system of record.

1. Create a Bluemix Account

    [Sign up for Bluemix][bluemix_signup_url] or use an existing account.
    
2. Create a VM from the console dashboard.

	**Note**: If you do not yet have access to the Bluemix VM beta, complete your request and wait for your confirmation email. This may take up to a few days, so please be patient!

	a) Select the `Ubunto 14.04` image for your VM  
	b) Give the VM group any name. We suggest something that identifies it as your "enterprise data center"  
	c) Select the `m1.small` size, equivalent to 1.5 GB memory and 1 CPU  
	d) Create an SSH key for securely connecting to your VM. For instructions on how to do this, check out the [documentation][vm_ssh_key_docs]  
	e) Default to the `private` network  
	f) Click Create to launch your VM. Once it has started, take not of your public IP address on the VM dashboard

3. SSH into your new VM using the following command, using your private key filename and public IP address:

	```
	$ ssh -i ./testkey.pem ibmcloud@XXX.XX.XXX.XX
	```
4. Resynch your VM's package index files from their sources:

	```
	$ sudo apt-get update
	```
	**Note**: You may see a warning in your console stating `sudo: unable to resolve host vm-###` when running `sudo` root commands. You can safely ignore these.

5. Install MySQL on your VM:

	```
	$ sudo apt-get install mysql-server
	```
	Assign a password to your VM and be sure to keep it handy for the rest of the setup.
	
6. Comment out the `bind-address` line in your MySQL options file using `vim`:

	```
	$ sudo vi /etc/mysql/my.cnf
	```
	For reference, here is a [vim commands cheatsheet][vim_cheatsheet_url].

7. Open up port 3306 in the VM's firewall:

	```
	$ sudo ufw allow 3306/tcp
	```

8. Grant remote access to your MySQL DB instance using your `root` password and then restart the mysql service:

	```
	$ mysql -u root -p
	Enter password: <PASSWORD>
	mysql> GRANT ALL ON *.* to root@'%' IDENTIFIED BY '<PASSWORD>';
	mysql> flush privileges;
	mysql> exit
	$ service mysql restart
	```

9. Seed your MySQL DB with sample data from this repo:

	```
	$ wget https://raw.githubusercontent.com/IBM-Bluemix/onprem-integration-demo/master/db.sql?token=AFP396gx7396eE_EhAt0ap-J6vKnvuJcks5WCUYGwA%3D%3D > db.sql
	$ mysql -u root -p -t < db.sql
	```

### Phase 2: Create a Secure Gateway Connection
Create a secure connection between your Bluemix app and the database running in your VM.

1. Navigate to the Bluemix catalog and select the new [Secure Gateway service][secure_gateway_catalog_url]. Choose `Leave Unbound` for now and click Create.

2. Add a new gateway in the service console

	a) Give your gateway a name and choose to add a destination  
	b) Give the destination a name, your VM's public IP, port 3306, keep TCP selected, and click the `+` button
	c) Click 'Connect It' to retrieve the command you will need to establish the secure connection from your VM

3. SSH back into your VM and install Docker

	```
	$ sudo apt-get install curl
	$ curl -sSL https://get.docker.com/ | sh
	```
4. Run the following command given in step 2c, entering your unique Secure Gateway ID:

	```
	$ sudo docker run -d -it ibmcom/secure-gateway-client XXX_prod_ng
	```
	This command downloads a Docker image of the Secure Gateway client and runs it as a daemon on your VM

### Phase 3: Deploy the Bluemix App
Now that we have a connection to our MySQL instance established, we need an application to ingest this data. We will create our own instance of the proivded sample app in this phase, completing the system.

1. Clone the repo, navigate to the app folder, and install the Maven dependencies:

	```
	$ git clone https://github.com/IBM-Bluemix/onprem-integration-demo.git
	$ cd onprem-integration-demo/hr_jpa_ui/
	```
2. Download and install [Apache Maven][maven_download_url] if you have not already.

3. Build your app .war file using Maven:

	```
	mvn install
	```
4. Update the `manifest.yml` file with a unique host name for your new app.

5. Download and install the [Cloud Foundry CLI][cloud_foundry_url] tool if you have not already.

6. Connect to Bluemix using the CLI and follow the prompts to log in.

  ```
  $ cf api https://api.ng.bluemix.net
  $ cf login
  ```
7. Push your app to Bluemix:

	```
	cf push -p target/vaadin-jpa-application.war
	```
8. Create a user provided service to broker communication to your MySQL DB:

	```
	cf cups mysql-connect -p '{
	"jdbcUrl": "jdbc:mysql://cap-sg-prd-3.integration.ibmcloud.com:15302/employees",
	"uri": "mysql://cap-sg-prd-3.integration.ibmcloud.com:15302/employees?reconnect=true",
	"name": "employees",
	"hostname": "cap-sg-prd-3.integration.ibmcloud.com",
	"port": "15302",
	"user": "root",
	"password": "password"
	}'
	```
	Now bind the service to your app.

  ```
  $ cf bind-service <APPNAME> mysql-connect
  ```

9. Finally, we need to restage our app to ensure these env variables changes took effect.

  ```
  $ cf restage <APPNAME>
  ```
 
## Decomposition Instructions
<Instructions on how a developer/architect would take the sample application and extract the relevant code for reuse.>

## API documentation
<If one or more of the apps in the demo exposes an API, provide a short explanation and how it is used in the sample. Link out to the published REST API documentation.>

## Contribute
<Note how you would like developers to contribute and/or log issues to your project. Also give steps for making pull requests. If your contribution directions become lengthy, break them out into a file called ‘CONTRIBUTING.md’ and link to this.>

## Troubleshooting

The primary source of debugging information for your Bluemix app is the logs. To see them, run the following command using the Cloud Foundry CLI:

  ```
  $ cf logs <application-name> --recent
  ```
For more detailed information on troubleshooting your application, see the [Troubleshooting section](https://www.ng.bluemix.net/docs/troubleshoot/tr.html) in the Bluemix documentation.

### Links to more information

[IBM Redbook: Secure Cloud-to-Mainframe Connectivity with IBM Bluemix][cloud_mainframe_redbook_url]

[bluemix_url]: https://console.ng.bluemix.net/?cm_mmc=Display-SampleApp-_-BluemixSampleApp-CapitalWeather-_-Node-WeatherChannel-_-BM-DevAd
[bluemix_signup_url]: https://ibm.biz/on-prem-integration-signup
[maven_download_url]: https://maven.apache.org/download.cgi
[cloud_foundry_url]: https://github.com/cloudfoundry/cli
[secure_gateway_docs]: https://www.ng.bluemix.net/docs/#services/SecureGateway/index.html
[vm_ssh_key_docs]: https://www.ng.bluemix.net/docs/virtualmachines/vm_index.html#vm_ssh_key
[vim_cheatsheet_url]: http://www.fprintf.net/vimCheatSheet.html
[secure_gateway_catalog_url]: https://console.ng.bluemix.net/catalog/secure-gateway/
[cloud_mainframe_redbook_url]: http://www.redbooks.ibm.com/redpapers/pdfs/redp5243.pdf
