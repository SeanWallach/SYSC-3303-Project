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
		System.out.println("\n\n\n\n *****JAM TIME****** \n\n\n");
		if (elevNum == 1) {
			this.elev1.jam = true;
			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("\n\n\n UNJAMED  \n\n\n");
			this.elev1.jam = false;
		}

		else if (elevNum == 2) {
			this.elev2.jam = true;
			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("\n\n\n UNJAMED  \n\n\n");
			this.elev2.jam = false;
		}

		else {
			this.elev3.jam = true;
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("\n\n\n UNJAMED  \n\n\n");
			this.elev2.jam = false;
		}

	}

	public void stopElev(int elevNum) {

		if (elevNum == 1) {
			this.elev1.functioning = false;
			System.out.println("\n\n **** E1 failure ***\n\n");
		}

		else if (elevNum == 2) {
			this.elev2.functioning = false;
			System.out.println("\n\n **** E2 failure ***\n\n");
		}

		else {
			this.elev3.functioning = false;
			System.out.println("\n\n **** E3 failure ***\n\n");
		}
		go = false;

	}

	public void chaos() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Random ran = new Random();
		for (int i = 0; i < 2; i++) {
			int chosen = ran.nextInt(3) + 1; // 1-3
			int time = (ran.nextInt(8) + 1) * 1000; // 1-10 seconds

			if (i == 1) {//create jam

				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 this.jam(chosen);
			}

			else if (i==0) {//shut down elevator

				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				this.stopElev(chosen);

			}

		}
	}

	public void run() {
		while (go) {
			this.chaos();
		}
	}

}
