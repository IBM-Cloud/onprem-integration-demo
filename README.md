# onprem-integration-demo
Model example of using Bluemix as a System of Engagement to connect back to a System of Record using a secure gateway connection.

For many large customers the core data that drives their business lives in established databases, accessed through classic middleware (i.e. Oracle Database accessed with .Net software).

While this system sustains their business, it is typically evolved in a monolithic way that doesn't promote the creation of nimble, devoted and focused apps that employees, or customers of the business would love to be able to use 'yesterday'.

Cloud computing platforms, microservices and the 12 factor app can connect to the core data (system of record) to reveal a system of engagement by quickly, lightly, and (crucially) safely surfacing their data so that it can be mixed with modern services and beautiful web, mobile or wearable interfaces.

This project is an example.

![alt tag](https://raw.githubusercontent.com/IBM-Bluemix/onprem-integration-demo/master/Architecture.png?token=AFP3905kOoeJUFAYGzPbQgMuU_Q4RImlks5WBcJvwA%3D%3D)

## Installation

Overview

1) Create a Bluemix VM to simulate a Enterprise Data Center

2) Set up the VM with a MySQL database with HR data.

3) Create a Secure Gateway connection from Bluemix to VM

4) Push Vaadin JPA Liberty application to Bluemix, connecting to Secure Gateway endpoint

TODO: Watson, weather, APIm, mobile, ...




### Links to more information

[IBM Redbook: Secure Cloud-to-Mainframe Connectivity with IBM Bluemix] (http://www.redbooks.ibm.com/redpapers/pdfs/redp5243.pdf)
