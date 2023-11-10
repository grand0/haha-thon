# haha-thon
### Participants
Lobanov Nikita, Asadullin Salavat, Ponomarev Ilya, Arifulina Regina

### Artifacts
Miro: https://miro.com/app/board/uXjVNQjw6YU=/?share_link_id=185416119705

draw.io: https://drive.google.com/file/d/1NuK_CbQT-p5B-9OBJNdKm13NfofXHLe5/view?usp=sharing  

### About project
#### Data Model
Our data model for describing the registered sensors within a smart home primarily provides for scalability without compromising the integrity of the database. You can see a detailed description of the model at the link to <a href="https://miro.com/app/board/uXjVNQjw6YU=/?share_link_id=185416119705">miro</a>, visual representation in <a href="https://drive.google.com/file/d/1NuK_CbQT-p5B-9OBJNdKm13NfofXHLe5/view?usp=sharing">draw.io</a>
#### Data Generator
The data generator, designed to send sensor measurements every 10 seconds, for each registered sensor (all registered sensors are contained in **sensors** table) in case it is active (boolean field **state** in **sensors** table), sends the required values in a separate thread for each sensor. Like the model, the generator can be extended if there are new types of sensors or measurements.
#### Web App
Our team wrote code in several programming languages such asJava and JavaScript. The back-end part consists of generating random data in a certain interval and sending this data to the client part. The client part is a one-page website with several minimalistic cards with all the necessary information - what kind of sensor it is, what state it is in and the last time it updated its data.
### Screencast: 
https://github.com/grand0/haha-thon/assets/53438383/62a45c7e-aaac-42dc-a940-f519d4d8288f

