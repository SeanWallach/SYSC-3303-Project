//@Author Andrew Dybka, Maveric
//Create timers to make sure elevators are runn



import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class FaultTimer {
	private Scheduler thisSched;
	private int elevatorNumber;
	private Timer timer;
	private boolean running;

	public FaultTimer(Scheduler s, int eNum) {
		thisSched = s;
		elevatorNumber = eNum;
		running = false;
	}

	private void startTimer() {//create a new timer
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				if (elevatorNumber == 1) {
					thisSched.e1Function = false;
					System.out.println("^^^^Elevator 1 shutting down^^^^");
				}
				if (elevatorNumber == 2) {
					thisSched.e2Function = false;
					System.out.println("^^^^Elevator 2 shutting down^^^^");
				}
				if (elevatorNumber == 3) {
					thisSched.e3Function = false;
					System.out.println("^^^^Elevator 3 shutting down^^^^");
				}
			}
		};
		//
		//create a new timer and set running to true
		
		timer = new Timer();
		timer.schedule(timerTask, 3000);
		running = true;

	}

	private void update() {//update a started timer
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				if (elevatorNumber == 1) {
					thisSched.e1Function = false;
					System.out.println("^^^^Elevator 1 shutting down^^^^");
				}
				if (elevatorNumber == 2) {
					thisSched.e2Function = false;
					System.out.println("^^^^Elevator 2 shutting down^^^^");
				}
				if (elevatorNumber == 3) {
					thisSched.e3Function = false;
					System.out.println("^^^^Elevator 3 shutting down^^^^");
				}
			}
		};
		//clear old timer and create new with task
		
		timer.cancel();
		timer = new Timer();
		timer.schedule(timerTask, 3000);
	}

	public void stopTimer() {
		if(running) {
			timer.cancel();
			running = false;
		}

	}

	public void time() {
		if (running) {//if already runnign update timer
			update();
		} else {//if not timeing start a new timer
			startTimer();
		}
	}

}
