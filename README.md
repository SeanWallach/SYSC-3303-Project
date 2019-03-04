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

Scheduler.java - Mav
Receives requests from floor and status updates from elevators. Forwards requests to select elevator based on statuses

Elevator.java, Elev.java - Seb
Receives requests from scheduler and manages queues for each elevator. Sends status updates from al elevators to scheduler

Diagrams, testing, Error correction and modifications for all classes - Andrew

documentation - everyone


Errors and Limitaions:
Doors and floor lamps are not included in this iteration due to last minute issues with some information sent between 
programs. There is a 3 second delay between each request from the floors to make sure there is no overlapping in requets
cause one elevator to take on more than it should. (Shown in Timing Diagram.png)
 
