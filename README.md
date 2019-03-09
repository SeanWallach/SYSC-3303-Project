# SYSC-3303-Project
Group project for SYSC 3303
Running Instructions:
Run classes in order: Scheduler.java->Elevator.java->floor.java
To change inputs into the sysyem: Open input.txt and add or remove commants in same format as shown. 
"##:##:##:### # up/down #"

Standard Java Libraries Required;


Responsibilities:
Floor.java - Sean
Sets up 10 floors. Reads from input file and sends requests to scheduler

Scheduler.java - Mav, Andrew
Receives requests from floor and status updates from elevators. Forwards requests to select elevator based on statuses

Elevator.java, Elev.java - Seb, Andrew
Receives requests from scheduler and manages queues for each elevator. Sends status updates from al elevators to scheduler

Break.java, Seb
randomly selects which elevator jams and which shutsdown at pseudo random times.

FaultTimer.java - Andrew, Mav
creates and updates timers based off scheduler needs and shuts down elevators is necessary

Diagrams, testing, Error correction and modifications for all classes - Andrew


documentation - everyone


Errors and Limitaions:
There is a 3 second delay between each request from the floors to make sure there is no overlapping in requets
cause one elevator to take on more than it should. (Shown in RequestSendingError.png) 

Break creates errors in the system. Most importantly shuts off an elevator which will stop sending updates to the scheduler.
The Scheduler's timer will go off if it doesnt receive updates from an elevator in motion or has a just been sent a request.
Once the timer goes off the scheduler will stop sending the shutdown elevator requests.
 
