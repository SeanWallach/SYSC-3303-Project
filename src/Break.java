import java.util.Random;

public class Break extends Thread {

	Elev elev1;
	Elev elev2;
	Elev elev3;
	boolean go;

	public Break(Elev one, Elev two, Elev three) {
		elev1 = one;
		elev2 = two;
		elev3 = three;
		go = true;
	}

	public void jam(int elevNum) {

		if (elevNum == 1) {
			this.elev1.jam = true;
		}

		else if (elevNum == 2) {
			this.elev2.jam = true;
		}

		else {
			this.elev3.jam = true;
		}

	}

	public void stopElev(int elevNum) {
		
		//Set the functioning to false preventing service from running
		if (elevNum == 1) {
			this.elev1.functioning = false;
			System.out.println("\n\n **** E1 failure - Break ***\n\n");
		}

		else if (elevNum == 2) {
			this.elev2.functioning = false;
			System.out.println("\n\n **** E2 failure - Break  ***\n\n");
		}
		
		else if (elevNum == 4) {
			this.elev2.functioning = false;
			System.out.println("\n\n **** E4 failure - Break  ***\n\n");
		}

		else {
			this.elev3.functioning = false;
			System.out.println("\n\n **** E3 failure - Break***\n\n");
		}
		go = false;

	}

	public void chaos() {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//randomly select which elevator to jam and shutdown and when
		Random ran = new Random();
		for (int i = 0; i < 2; i++) {
			int chosen = ran.nextInt(4) + 1; // 1-3
			int time = (ran.nextInt(8) + 1) * 1000; // 1-10 seconds

			if (i == 1) {//create jam

				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 //this.jam(chosen);
			}

			else if (i==0) {//shut down elevator

				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				this.stopElev(1);

			}

		}
	}

	public void run() {
	//	while (go) {
			this.chaos();
	//	}
	}

}
