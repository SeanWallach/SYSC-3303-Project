import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Elevator {

	
	
	
	private int SEND_PORT_NUMBER = 219; //schedualer port
	private DatagramSocket receive;
	
	private Elev elev1;
	private Elev elev2;
	private Elev elev3;
	
	 
	
	public Elevator(int numElev) {
		//create nmuber of elev starting at prot 69
/*		for(int i =0;i<numElev;i++) {
			
		} */
		elev1 = new Elev(1,10,69);
		elev2 = new Elev(1,10,70);
		elev3 = new Elev(1,10,71);
		
		try {
			receive = new DatagramSocket(68);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //wait to receive on porty 68
	}
	
	
	
	public void receiver() {
		DatagramPacket receivePacket;
		byte[] message = new byte[25];
		receivePacket = new DatagramPacket(message, message.length);
		try {        
	         System.out.println("Waiting..."); // so we know we're waiting
	         receive.receive(receivePacket);
	      } catch (IOException e) {
	         System.out.print("IO Exception: likely:");
	         System.out.println("Receive Socket Timed Out.\n" + e);
	         e.printStackTrace();
	         System.exit(1);
	      }
		System.out.println("Elevator: Packet received:");
		System.out.println("From host: " + receivePacket.getAddress());
		System.out.println("Host port: " + receivePacket.getPort());
		int len = receivePacket.getLength();
		System.out.println("Length: " + len);
		System.out.print("Request for (Initial, destination): " );
		
		
		
		//decode packet and get data and run proper elev
		byte[] temp = receivePacket.getData();
		int initial, destination;
		if(temp[2] >= 10) {
			initial = temp[3] +10;
		}
		else {initial = temp[3];}
		
		if(temp[4] >= 10) {
			destination = temp[5] +10;
		}
		else {destination = temp[5];}
		
		System.out.println("("+ initial+ ", " + destination+ ")");
		
		if(temp[0] == 1) {
			System.out.println("------Adding to Elevator 1-------");
			elev1.addRequest(initial);
			elev1.addRequest(destination);
		}
		else if(temp[0] == 2) {
			System.out.println("------Adding to Elevator 2-------");
			elev2.addRequest(initial);
			elev2.addRequest(destination);
		}
		else if(temp[0] == 3) {
			System.out.println("------Adding to Elevator 3-------");
			elev3.addRequest(initial);
			elev3.addRequest(destination);
		}
		
	}


		
	
	
	
	public void run() {
		this.receiver();
	}
	
	
	
	
	public static void main(String[] args)  {
		//create new elevator then wait to receive as receiver, then run proper elevator set by skeddy
		Elevator elevator = new Elevator(3);
		while(true) {
			elevator.run();
		}
	}
}
