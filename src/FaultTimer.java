import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
public class FaultTimer extends TimerTask {
	private Elevator elevator;
	private int lastState, currentState;
	public FaultTimer(Elevator e) {
		elevator = e;
	}
	@Override
	public void run() {
		System.out.println("Checking for faults at : " + new Date());
		completeTask();
		System.out.println("Fault Checking Over : " + new Date());
		
	}
	public void completeTask() { //should be used to check itself Maybe construct with it own elevator to check over
		try {
			Thread.sleep(2000);
			//So elevator is attached @ creation... checks elevator state which better update if it returns as fault 
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
