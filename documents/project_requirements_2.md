#"BikeSmart" Project Requirements Draft 2

This document describes the architecture, requirements, and design of the "BikeSmart" Project - designed and implemented by the *Treadsetters* team.

####*Treadsetters*

Saili Raje, Lead

Joel Dick

Chris Karcher

Duncan Sommer

Oliver Townsend

---

###Revision History 
| Version Number |      Primary Author(s)|  Version Description | Date Completed | 
|:----------:|:-------------:|:------:|:------:|
| 1.0 |  Saili Raje, Joel Dick, Duncan Sommer, Oliver Townsend, Chris Karcher | Initial Version with user stories | 1/23/15|
| 1.1|   Saili Raje, Joel Dick, Duncan Sommer, Oliver Townsend, Chris Karcher   |   Added system architecture and glossary  |1/29/15 | 
| 1.2|   Saili Raje, Joel Dick, Duncan Sommer, Oliver Townsend, Chris Karcher   |   Added user stories and updated system architecture overview |2/22/15 | 
| 1.3|   Saili Raje, Joel Dick, Duncan Sommer, Oliver Townsend, Chris Karcher   |   Updated glossary. Added "bike circle" feature  |2/22/15 | 
| 1.4|   Saili Raje, Joel Dick, Duncan Sommer, Oliver Townsend, Chris Karcher   |   Updated list of user stories and added wireframe mockups |2/22/15 | 
| 2.0|   Saili Raje, Joel Dick, Duncan Sommer, Oliver Townsend, Chris Karcher   |   Current Revision |2/22/15 | 

---
###Table of Contents
###Introduction 
There is no existing platform that allows a bicycle to communicate with other devices via the Internet. Such a system will need to solve power and connectivity issues related to an embedded system on a bike. Our team is designing BikeSmart, a database for mobile bike information to enable developers to create profitable applications for users ranging from the casual commuter to the professional cyclist. This project will also act as a proof of concept for further integration of bikes into the Internet of Things. 


###Glossary of Terms
**Embedded System** - an embedded system is a computer system with a dedicated function within a larger mechanical system, such as a bicycle. For this project, for prototyping purposes, we will be using a Motorola Moto G smartphone. 

**Internet of Things (IoT)** -  the interconnection of uniquely identifiable embedded computing devices within the existing Internet infrastructure.

**Parse** - a cloud based application engine that allows developers to receive and distribute information and messages to devices on the internet.

**Bike Circle** - a group that contains users and bikes. If you join a bike circle, or approve /someone to join a circle, you/that person automatically gets permission to request to use your bike and gets access to your bikes information (location, past rides, etc.)
 
###Requirements and user stories
![Alt text](./image1.png =500x500)
![Alt text](./image2.png =500x500)
![Alt text](./image3.png =400x500)
![Alt text](./image4.png =400x400)


###System architecture overview
This system will be comprised of:

- An Android service that runs on an embedded system simulated by a mobile phone that captures data from the bike, stores it locally, and sends it  to a cloud database intermittently while minimizing power consumption.

- A backend database running on a remote server that will receive and process data, then distribute the data to remote clients. 

- At least one specialized mobile application that will utilize the data provided by the database to deliver content to a Bikesmart user.

- A frontend web interface which gathers content from the remote server and presents it in a clear and organized manner to users.

![Alt text](./image7.png =470x400)


###Mockups/Wireframes
![Alt text](./image5.png =500x800)
![Alt text](./image6.png =470x400)

###Potential Obstacles
A foreseeable obstacle is how to standardize human and bike activity. Ideally, we’ll be pushing almost any kind of data imaginable to users’ cloud accounts and maybe even from bike to bike. Bike to bike communication is certainly going to be possible in the near future with Qualcomm’s LTE direct technology on the horizon. Assuming other smart bike competitor companies will begin to emerge, it will be useful to have our bike be able to interact with a bike or bike database built by another company. How would we ensure stable and reliable communication between two or more bikes/bike networks without any form of standardization in data communication?

Another obstacle, of course, is privacy. At a high level, all connections made with the Parse database are made with HTTPS and SSL and it will reject all non-HTTPS connections. This completely absolves any need to worry about man in the middle attacks. At a lower level, we will be taking advantage of Parse’s Access Control Lists (ACLs). ACLs allow developers to control who can access which sets of data through these lists. Lists contain objects and each object has a list of users and roles including what permissions that user or role has.

Finally, another hurdle we might not be able to tackle very elegantly is the inclusion of a physical lock. For the purposes of the project, we will most likely just have a boolean variable that controls whether or not one can unlock/use the phone screen that’s attached to the bike. The bike will have to be unlocked using the user app through a secure login of the owner of that particular bike.

###Prototyping code and test cases (Source Code) 
https://github.com/sraje/CAPSTONE


###Appendices/Technologies Used
 
- Parse Application Engine and API
- Google Location API
- Android SDK
- Pivotal Tracker 
- Github


