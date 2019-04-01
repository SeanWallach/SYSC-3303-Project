import java.util.Date;
public class FaultTimer extends Thread{
	private Scheduler s;
	private int preState, preFloor, elevator;
	public FaultTimer(Scheduler s, int elev) {
		this.s = s;
		elevator = elev;
	}
	public void run() {
		while(true) {
			//System.out.println("FaultScheduler for elevator " + elevator + " Checking at: " + new Date());
			try {
				if(elevator == 1) {
					preState = s.elevatorState1;
					preFloor = s.elevatorFloor1;
				}
				else if(elevator == 2) {
					preState = s.elevatorState2;
					preFloor = s.elevatorFloor2;
				}
				else {
					preState = s.elevatorState3;
					preFloor = s.elevatorFloor3;
				}
				Thread.sleep(5000);

				if(elevator == 1) {
					//System.out.println("ELEVATOR 1: preFloor: " + preFloor + ", State: " + s.elevatorState1 + " current Floor: " + s.elevatorFloor1);
					if(s.elevatorState1 != 0 &&  preFloor == s.elevatorFloor1) {
						System.out.println("smt broke");
						s.elevatorState1 = 3;
						s.isActive1 = false;
						System.out.println("^^^ERROR: ELEVATOR 1 BROKEN^^^^");
					}
				}
				else if(elevator == 2) {
					//System.out.println("ELEVATOR2: preFloor: " + preFloor + ", State: " + s.elevatorState2 + " current Floor: " + s.elevatorFloor2);
					if(s.elevatorState2 != 0 &&  preFloor == s.elevatorFloor2) {
						System.out.println("smt broke");
						s.elevatorState2 = 3;
						s.isActive2 = false;
						System.out.println("^^^ERROR: ELEVATOR 2 BROKEN^^^^");

					}
				}
				else if (elevator == 3){ //elevator 3
					//sSystem.out.println("ELEVATOR 3: preFloor: " + preFloor + ", State: " + s.elevatorState3 + " current Floor: " + s.elevatorFloor3);
					if(s.elevatorState3 != 0 && preFloor == s.elevatorFloor3) {
						System.out.println("smt broke");
						s.elevatorState3 = 3;
						s.isActive3 = false;
						System.out.println("^^^ERROR: ELEVATOR 3 BROKEN^^^^");

					}
					
				}
				else {
					System.out.println("Elevator " + elevator + " appears to be working as expected");
				}
				//So elevator is attached @ creation... checks elevator state which better update if it returns as fault 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
