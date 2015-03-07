#"BikeSmart" Vision Statement

This document describes the project, design, strategy, and goals of the "BikeSmart" Project - designed and implemented by the *Treadsetters* team.

####*Treadsetters*

Saili Raje, Lead

Joel Dick

Chris Karcher

Duncan Sommer

Oliver Townsend

---

###Problem
There is no existing platform that allows a bicycle to communicate with other devices via the internet. Because there is no embedded system to gather, relay, and ultimately interpret data from a bicycle, the rider is unable to take advantage of the potential associated with the Internet of Things/Internet of Everything (IoT) having a “connected bike”. We believe that this system will provide a basis for developers to create profitable applications for users ranging from the casual commuter to the professional cyclist.

###Importance
While certain aspects of placing a bike in the IoT may already be feasible simply through smart phone information, there are distinct upstream and downstream data aspects that require a distinct embedded system as part of the bike.

###Solution on Today's Market
There is no de facto solution for putting a bike on the Internet as of today.
There are aspects of an Internet bike ecosystem which include the following:
http://bitlock.co/ allows for electronic unlocking of a bike within 3ft proximity. Does not currently allow for remote unlock or good sharing protocol.
Various fitness apps/wearables track activity and surface that information
Google Location Services are getting better and google is making more of the information available to the user.

<br> 
### Desired Outcome
This project will act as a proof of concept for further development in the integration of bikes into the IoT.
This project will provide a service for connecting a bike to the IoT.
This project will include an application to demonstrate the service and proof of concept.
This project will solve power and connectivity issues related to an embedded system on a bike.

###Initial Project Milestones 
Install basic sensors on bike capable of uploading data to database. Gather various metrics, including: GPS location, speed, altitude, and acceleration. We will also create a simple app that is able to retrieve the data and present it in some useful fashion. We will focus on the connectivity and conservation of device's power, and demonstrate this via an application. 
 Specifically, we will:

- Solve issues regarding a mobile embedded system
	- Design within power efficiency constraints
	- Design within connectivity constraints
	- Install additional sensors to gather data about data about the bike and rider's environment
- Create a service to host information gathered from the bike
	- Collect and interpret location data
	- Collect and interpret sensor data
	- Share data between service and web 
	- Take into account the following design constraints:
		- Security
		- Cost
		- Ease of Use
- Create an app to demonstrate created service and add additional functionality, based on the following open ended examples:
	- Bike Sharing
	- App Suggestions
	- Theft Detection
	- Find my Bike
	- Bike activity/fitness tracker

<br><br><br><br><br><br>

###Stretch goals
Ideally, we hope to create several applications that build off the SmartBike platform and showcase the enormous potential in such a system. Additional app development for demonstration may build off the following design ideas:

- Bike Sharing
- Peer to Peer (Ex. LTE Direct or through carrier)
- Further sensor integration.

###Team Strategy
In order to achieve our design goals, we plan to adhere to the following strategy as we design and build our product:

1. Break down our project from the 3 stages we have outlined into purposeful tasks that fit into 2 week sprints.
2. Investigate, evaluate and integrate technologies necessary to our design.
3. Re-evaluate our progress and solutions at regular intervals making changes as needed.
4. Be flexible!

We will be utilizing the Agile/Scrum process model to achieve our milestones by having a quick daily online scrum “standing meeting” via google hangouts or after class as well as a weekly meeting with our Mentor to keep him updated and to make sure we’re on track with the tasks required of us. We’ve set up a pivotal tracker project to act as a virtual scrum board and a github repository that will store our code and meeting minutes. Our main mode of communication is a Slack domain that allows us to communicate via different channels (for different topics) and lets us directly embed services like Google Docs into the threads. We will constantly re-evaluate our goals and strategy in order to design a 


###Technologies
Through our initial research into the implementation of our product, we compiled a mutable list of technologies that we expect to explore and utilize:

- Carrier enabled phone to prototype bike embedded system.
- Parse Platform(Backend) as a System
- Google Location Services API or similar to get location of devices
- External sensors to feed information to embedded system
- Android application to utilize information from service


