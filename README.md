# SYSC-3303-Project
Group project for SYSC 3303

Running Instructions:
Run classes as java applications in the  order: Scheduler.java->Elevator.java->floor.java
To change inputs into the system: Open input.txt and add or remove commants in same format as shown. 
"##:##:##:### # up/down #"

The measured values of the scheduler tasks is in the file MeasurmentOutput.txt
All times are in nano seconds. The file is cleared and re written each time program starts.
There is a 60s delay 

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

FaultTimer.java - Mav
creates and updates timers based off scheduler needs and shuts down elevators is necessary

measuring - 2 ways done by Mav and Andrew
 Andrew - wait 60s to measure various requests in scheduler. Then print time to complete each request along with median and mode
 in a file
 
 Mav - Have an array size 10, every 10 request of each type print out the mean and clear the array

Diagrams, testing, Error correction and modifications for all classes - Andrew
  ArrivalMeasureing.png, FloorButtonMeasuring.png, ElevatorShutDownError.jpg, RequestSendingError.png - timing diagrams, explainations
   for each timing diagram provided in each file
 
  UML Class.png - updated UML Class diagram for all systems
 
  State machine diagram - state machine diagram from iteration 1


documentation - everyone


Errors and Limitaions:
There is a 3 second delay between each request from the floors to make sure there is no overlapping in requets
cause one elevator to take on more than it should. (Shown in RequestSendingError.png) 

**** We do NOT measure the elevator button interface because we have a queue of all tasks for each elevator in their local systems. So when an elevator button is pushed the scheduler will not know. When the elevator moves after a push then our regular update from elevator will be sent to scheduler. That time is of the the update is measured along with the arrival sensor.
