import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Elevator {

	private int SEND_PORT_NUMBER = 219; // schedualer port
	private DatagramSocket receive;

	private static Elev elev1;
	private static Elev elev2;
	private static Elev elev3;
	

	public Elevator() {
		// create nmuber of elev starting at prot 69
		/*
		 * for(int i =0;i<numElev;i++) {
		 * 
		 * }
		 */
		

		try {
			receive = new DatagramSocket(68);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // wait to receive on porty 68
	}

	public void receiver() {
		DatagramPacket receivePacket;
		byte[] message = new byte[25];
		receivePacket = new DatagramPacket(message, message.length);
		try {
			System.out.println("Waiting...\n"); // so we know we're waiting
			receive.receive(receivePacket);
		} catch (IOException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Elevator: Packet received:");
		/*System.out.println("From host: " + receivePacket.getAddress());
		System.out.println("Host port: " + receivePacket.getPort());
		int len = receivePacket.getLength();
		System.out.println("Length: " + len);
		*/System.out.print("Request for (Initial, destination): ");

		// decode packet and get data and run proper elev
		byte[] temp = receivePacket.getData();
		int initial, destination;
		if (temp[2] >= 10) {
			initial = temp[3] + 10;
		} else {
			initial = temp[3];
		}

		if (temp[4] >= 10) {
			destination = temp[5] + 10;
		} else {
			destination = temp[5];
		}

		System.out.println("(" + initial + ", " + destination + ")");

		if (temp[0] == 1) {
			System.out.println("------Adding to Elevator 1-------");
			elev1.requestWaiting=true;
			elev1.addRequest(initial, destination);
		} else if (temp[0] == 2) {
			System.out.println("------Adding to Elevator 2-------");
			elev2.requestWaiting=true;
			elev2.addRequest(initial, destination);
		} else if (temp[0] == 3) {
			System.out.println("------Adding to Elevator 3-------");
			elev3.requestWaiting=true;
			elev3.addRequest(initial, destination);
		}

	}

	public void run() {
		this.receiver();
	}

	public static void main(String[] args) {
		// create new elevator then wait to receive as receiver, then run proper
		// elevator set by skeddy
		Elevator elevator = new Elevator();
		elev1 = new Elev(1, 22, 69, elevator);
		elev2 = new Elev(2, 22, 70, elevator);
		elev3 = new Elev(3, 22, 71, elevator);
		elev1.start();
		elev2.start();
		elev3.start();
		Break destruction = new Break(elev1,elev2,elev3);			////////////////////////////////////////////////////////////////////
		//destruction.start();										////////////////////////////////////////////////////////////////////
		while (true) {
			elevator.run();
		}
	}
}
