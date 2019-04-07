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
		System.out.println("FaultScheduler Checking at: " + new Date());
		try {
			if(elevator == 1) {
				preState = s.elevatorState1;
				preFloor = s.elevatorFloor1;
			}
			else if(elevator == 2) {
				preState = s.elevatorState2;
				preFloor = s.elevatorFloor2;
			}
			else if(elevator == 3){
				preState = s.elevatorState3;
				preFloor = s.elevatorFloor3;
			}
			else {
				preState = s.elevatorState4;
				preFloor = s.elevatorFloor4;
			}
			Thread.sleep(10000);
			
			if(elevator == 1) {
				if(s.elevatorState1 != 0 && preFloor == s.elevatorFloor1) {
					s.elevatorState1 = 3;
					System.out.println("^^^ERROR: ELEVATOR 1 BROKEN^^^^");
				}
				else {
					System.out.println("Elevator 1 appears to be working as expected");
				}
			}
			else if(elevator == 2) {
				if(s.elevatorState2 != 0 && preFloor == s.elevatorFloor2) {
					s.elevatorState2 = 3;
					System.out.println("^^^ERROR: ELEVATOR 2 BROKEN^^^^");
					
				}
				else {
					System.out.println("Elevator 2 appears to be working as expected");
				}
			}
			else if (elevator == 3){ //elevator 3
				if(s.elevatorState3 != 0 && preFloor == s.elevatorFloor3) {
					s.elevatorState3 = 3;
					System.out.println("^^^ERROR: ELEVATOR 3 BROKEN^^^^");
					
				}
				else {
					System.out.println("Elevator 3 appears to be working as expected");
				}
				
			}
			else if(elevator == 4) {
				if(s.elevatorState3 != 0 && preFloor == s.elevatorFloor3) {
					s.elevatorState3 = 3;
					System.out.println("^^^ERROR: ELEVATOR 3 BROKEN^^^^");
					
				}
				else {
					System.out.println("Elevator 4 appears to be working as expected");
				}
			}
			
			//So elevator is attached @ creation... checks elevator state which better update if it returns as fault 
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
}
