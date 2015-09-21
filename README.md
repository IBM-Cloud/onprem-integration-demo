# Bluemix On-premises Integration Demo
Model example of using Bluemix as a System of Engagement to connect back to a System of Record using a secure gateway connection.

For many large customers the core data that drives their business lives in established databases, accessed through classic middleware (i.e. Oracle Database accessed with .Net software).

While this system sustains their business, it is typically evolved in a monolithic way that doesn't promote the creation of nimble, devoted and focused apps that employees, or customers of the business would love to be able to use 'yesterday'.

Cloud computing platforms, microservices and the 12 factor app can connect to the core data (system of record) to reveal a system of engagement by quickly, lightly, and (crucially) safely surfacing their data so that it can be mixed with modern services and beautiful web, mobile or wearable interfaces.

![alt tag](https://raw.githubusercontent.com/IBM-Bluemix/onprem-integration-demo/master/Architecture.png?token=AFP3905kOoeJUFAYGzPbQgMuU_Q4RImlks5WBcJvwA%3D%3D)

## Installation
There are many different components of this demo, which make the installation not so simple. 

### Overview

1) Setup an OpenStack VM with a database to simulate a Enterprise Data Center.
2) Create a Secure Gateway connection to the database running in the VM.
3) Push Vaadin JPA Liberty application to Bluemix, connecting to Secure Gateway endpoint.

TODO: Watson, weather, APIm, mobile, ...

### Step 1: Create a Bluemix VM with sample HR data to simulate an Enterprise Data Center. 
**Create and log in to VM**

- Login to Bluemix and create a VM. Note the VM public IP
- ssh with key
- sudo apt-get update

**Install MySQL**
- sudo apt-get install mysql-server
- sudo vi /etc/mysql/my.cnf 
    Comment out this line: bind-address
- Open 3306 port in firewall

**Seed with sample data**

`    wget https://raw.githubusercontent.com/IBM-Bluemix/onprem-integration-demo/master/db.sql?token=AFP396gx7396eE_EhAt0ap-J6vKnvuJcks5WCUYGwA%3D%3D > db.sql`

`mysql -u root -p -t < db.sql`

### Step 2: Create a Secure Gateway connection from Bluemix to VM
- In Bluemix Catalog, create a Secure Gateway service
- Create gateway for VM-PUBLIC-IP:3306
- Copy docker command.

**Install Docker and run Secure Gateway container**
- sudo apt-get install curl
- curl -sSL https://get.docker.com/ | sh
- Run command given by bluemix in previous step

### Step 3: Deploy Vaadin Java Liberty application

TODO

### Links to more information

[IBM Redbook: Secure Cloud-to-Mainframe Connectivity with IBM Bluemix] (http://www.redbooks.ibm.com/redpapers/pdfs/redp5243.pdf)
