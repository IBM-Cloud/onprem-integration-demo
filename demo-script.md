#### Disclaimer

This is **work in progress** and not all steps and/or technical details are fully thought out or implemented yet.

Contact (for now):

Uwe Fassnacht (Bluemix Product Manager):
<uwe.fassnacht@de.ibm.com>


## Preparation before the demo

#### Deploy the demo app into your Bluemix account

Use the "Deploy to Bluemix" button (found in the Readme.MD) file to deploy the web application into your Bluemix account. To do that, you will need:

- A Bluemix account (if you don't already have one, sign up for a free trial account [here](https://console.ng.bluemix.net/registration))
- A modern web browser

After a succesful deploy, make sure you can access the running application through your web browser.

#### Test the connectivity to the secure gateway

To make sure no network issues stand between you and the gateway, you can test connectivity to it by:

- ...
- ...

## Sequence of Steps

### 1. Set the stage

Start by explaining the concepts of [System-of-Record (SoR)](https://en.wikipedia.org/wiki/System_of_record) vs. [System-of-Engagement (SoE)](https://en.wikipedia.org/wiki/Systems_of_Engagement).

Typically, an enterprise customer has data in his on-premise SoR and does not want to store it in a public cloud. Maybe he is worried about security, maybe the data is too large to efficiently move or maybe there are government regulations that do not allow it.

Even though the data resides on-premise, the customer wants to do something interesting with that data ... using advanced techqnuies most easily obtained from from the cloud:

- run advanced analytics (maybe even cognitive)
- allow employees, partners or even the public to use (parts of) the data it in a controlled way
- monetize the data by exposing it through a metered API (concept of API economy)
- ...

### 2. Start with the on-premise data base

The data would most probably be in an on-premise business system. Maybe in a SAP installation or a data base of some sort (DB2, Oracle, ...).

For this demo, we are using a HR system that has employee information in it. But this is only illustrative, it could be any type of information (machine data, logs, stock levels in a warehouse, ...).

What we would like to accomplish, is to bring relevant data from our HR system into the cloud. We can then run modern analytics on, or just display it in a beautiful user interface on mobile devices or in a web browser. Think of a use case like an HR dashboard for executives.

To make this solution truly enterprise grade we need to:

- secure the connection between the on-premise datacenter and the cloud application
- tightly control who has access to which part of the dataset
- detect who is using the data and how much of it
- be able to scale this solution easily up and down, based on demand

All this can be accomplished rather quickly and easily by using services available in the Bluemix catalog.

### 3. Explain the concept of a secure gateway and it's implementation

Explain (using slides) how the secure gateway is installed and initiated from within the firewall of the on-premise datacenter.

Show a slide with a rough diagram:

- a data center (maybe a SystemZ or Power box inside) with
- a data base installed on it the system
- a firewall
- a docker container, which initiates the secure gateway

For the purpose of this demo, we've already done that at an IBM datacenter.

### 4. Introduce the web application that we would like to connect with the on-premise data base

Log into Bluemix

Bring up the dashboard and click on the running application

Explain that this a java app running on the Bluemix cloud, **emphasize that it was developed in a very short time frame, based on existing services in the catalog**.

Show the application's UI. Depending on the demo environment, either on a mobile device or on the laptop.

The application neees to be visually well designed and a dashboard of some sort. Maybe show employee profiles Pictures, where he/she works in the organization, skills, .... Also has to be mobile responsive!

At this point, the dashboard has no data. Only empty frames are visible.


### 5. Explanation of the secure gateway

Re-iterate that:

- one the one side we have an app that was built with modern technologies (12-factor, microservices, ...) in the cloud.
- on the other side we have data in an on-premise HR system, residing on premise in a data center.

We would now like to show "live on stage" how to connect the two.

While, technically, we could instantiate the secure gateway service live, it would involve logging into the SoR and starting it from a container. While it takes only a few minutes, it would be too long for the demo.

Instead, we have have done this already and can now connect to the endpoint of the service.

Explain that we could now access data from the HR SoR if we'd connect the service to our java application running on Bluemix.

### 6. Introduce the concept of API Management

But before we do that, we need to ensure that:

- only authenticated users can access the data
- we are able to expose only certain parts of the data
- we can measure how the data is being used

Explain that we do the above using another service from the Bluemix catalog ... API Management.

### 7. Live instantiation of the API Management service

Bring up the API Management service and (while it comes up) explain what it does. Emphasize control and versioning. Time permitting, this would be a great time to talk about the API economy.

Next connect the running API Management service to the secure gateway.

Maybe mask out certain data that we do not want to expose? This would emphasize control.

### 8. Introduce the concept of a private service in the Bluemix catalog and instantiate it live

Then use the API Management service to generate a private service in the catalog.

Explain that we have just created a controlled API that would be visible to developers internally, but not to the world. Of course the latter could be done as well...

### 9. Connect the web application with the new private service

Now we use the newly generated private service and connect it to the java application.

Boom! The dashboard comes to live ... lot's HR data shown in pretty ways ... as on-premise data is flowing through the secure connector, the API Management service and then (exposed as a private service in the catalog) into the application itself. Long pause (as the crowd goes wild).

### 10. Re-iterate (maybe with a slide) what we have just done

After the applause dies down, re-iterate what we did during the demo. Ideally using a slide (with animation) that walks through the various steps again.

Java web app <---> private service <---> API Management <---> Secure Gateway <---> On premise data base

## Links to more information

Here are a few links that describe similar scenarios and the technology in more detail.

#### Bluemix in general


#### The secure gateway service


#### The API Management service


#### The Vaadin framework and UI elements
