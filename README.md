# SYSC-3303-Project
Group project for SYSC 3303

Running Instructions:
Run classes as java applications in the  order: Scheduler.java->Elevator.java->floor.java
Use Gui to use the systems. 

Responsibilities for Iter 5:
Andrew: Diagrams, Testing, refactoring all classes
Seb: created GUI’s and updated floor and elevator classes to function with them
Maveric: Updates to Scheduler to handle 22 floors and 4 elevators
Sean: Diagrams and report, Test Classes



Errors and Limitaions:
**** We do NOT measure the elevator button interface because we have a queue of all tasks for each elevator in their local 
systems. So when an elevator button is pushed the scheduler will not know. When the elevator moves after a push then our 
regular update from elevator will be sent to scheduler. That time is of the the update is measured along with the arrival 
sensor.
