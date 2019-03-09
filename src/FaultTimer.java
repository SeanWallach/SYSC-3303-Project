

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

	private void startTimer() {
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
		timer = new Timer();
		timer.schedule(timerTask, 3000);
		running = true;

	}

	private void update() {
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
		if (running) {
			update();
		} else {
			startTimer();
		}
	}

}
