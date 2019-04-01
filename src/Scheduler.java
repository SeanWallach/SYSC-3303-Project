// Scheduler.java
// Maveric Garde 101031617
// This class is the Intermediate of a Client/Server UDP client on
// UDP/IP. The server receives from a client (elevator button/user) or server (Elevator) a packet 
// containing a data array with floor and direction, then forwards it to the other client or server.
// Last edited Feb 9th 2019

import java.io.*;
import java.net.*;
import java.util.*;

public class Scheduler {

	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendSocket, receiveSocket;
	Thread t1, t2, t3; //threads for monitoring elevators
	Date currentDate; 
	Boolean isActive1 = true, isActive2 = true, isActive3 = true; //To keep track of which elevators are talking to the scheduler
	int elevatorState1, elevatorState2, elevatorState3, //will have to turn these into thread safe ----- 0 is idle 1 is up 2 is down
		elevatorFloor1, elevatorFloor2, elevatorFloor3; //collections, ArrayList? 
	static int ELEVATORPORT1 = 69, ELEVATORPORT2 = 70, ELEVATORPORT3 = 71, 
			PACKETSIZE = 25, SELFPORT = 219, FLOORPORT = 238;
	public ArrayList<Long> arrivalTimes = new ArrayList<Long>(); 
	public ArrayList<Long> floorBTimes = new ArrayList<Long>(); 
	private static boolean measuring = true;
	private long aStartTime, fStartTime;

	public Scheduler()
	{
		elevatorState1 = 0; elevatorState2 = 0; elevatorState3 = 0; elevatorFloor1 = 0; elevatorFloor2 = 0; elevatorFloor3 = 0; //all elevators should be idle at startup
	  t1 = new Thread(new FaultTimer(this, 1));

		t2 = new Thread(new FaultTimer(this, 2));
		t3 = new Thread(new FaultTimer(this, 3));
		try {
			// Construct a datagram socket and bind it to any available 
			// port on the local host machine. This socket will be used to
			// send UDP Datagram packets.
			sendSocket = new DatagramSocket();

			// Construct a datagram socket and bind it to port 23 
			// on the local host machine. This socket will be used to
			// receive UDP Datagram packets from the client
			receiveSocket = new DatagramSocket(SELFPORT);
			
			// to test socket timeout (2 seconds)
			//receiveSocket.setSoTimeout(2000);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		} 
		t1.start();
		t2.start();
		t3.start();
	}

	private int getBestElevator(int toFloor, int direction) {
		//from current data which elevator is best to send
		//check to see if any elevator status is marked as idle. Easy Out;
		if(elevatorState1 == 0 && isActive1) return 1;
		if(elevatorState2 == 0 && isActive2) return 2;
		if(elevatorState3 == 0 && isActive3) return 3;
		
		//No Elevator idle Check other specs to find best a case
		
		if((elevatorState1 == 1 && direction == 1 && isActive1) && toFloor >= elevatorFloor1)
			return 1;
		if((elevatorState2 == 1 && direction == 1 && isActive2) && toFloor >= elevatorFloor2)
			return 2;
		if((elevatorState3 == 1 && direction == 1 && isActive3) && toFloor >= elevatorFloor3)
			return 3;
		if((elevatorState1 == 2 && direction == 2 && isActive1) && toFloor <= elevatorFloor1)
			return 1;
		if((elevatorState2 == 2 && direction == 2 && isActive2) && toFloor <= elevatorFloor2)
			return 2;
		if((elevatorState3 == 2 && direction == 2 && isActive3) && toFloor <= elevatorFloor3)
			return 3;
		if(!isActive1 && !isActive2 && !isActive3) {
			System.out.println("All elevators broken shutting down for maintenance!");
			shutdownSystem();
		}
		//Implementation should prevent any situation where all of these fail; however recall function		
		return getBestElevator(toFloor, direction);

	}

	private void sendElevator(int elev, int floor, byte msg[]) {
		//from the best elevator create the correct packet and send 
		//correct data
		int toPort;
		//assign proper port
		switch(elev) {
		case(3):
			toPort = ELEVATORPORT3;
			elevatorState3=msg[1];
		case(2):
			toPort = ELEVATORPORT2;
			elevatorState2=msg[1];
		default:
			toPort = ELEVATORPORT1;
			elevatorState1=msg[1];
		}
		
		//add to floor times
		
		sendPacket = new DatagramPacket(msg, msg.length,
				receivePacket.getAddress(), 68);
		// Send the datagram packet to the client via the send socket. 
		try {
			sendSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		if(measuring) floorBTimes.add(System.nanoTime() - fStartTime);//packet send measure elapsed time
		System.out.println("Server: packet sent");

	}
	public void receiveAndSend()
	{
		// Construct a DatagramPacket for receiving packets up 
		// to 100 bytes long (the length of the byte array).
		updateDate();
		int toFloor = 0;
		byte data[] = new byte[PACKETSIZE];
		receivePacket = new DatagramPacket(data, data.length);
		System.out.println("Scheduler: Waiting for Packet.\n");

		// Block until a datagram packet is received from receiveSocket.
		try {        
			System.out.println("Waiting..."); // so we know we're waiting
			receiveSocket.receive(receivePacket);

			//catch IO exception and print stack trace
		} catch (IOException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}

		// Process the received datagram.
		System.out.println("Scheduler: Packet received:");
		System.out.println("From host: " + receivePacket.getAddress());
		System.out.println("Host port: " + receivePacket.getPort());
		int fromPort = receivePacket.getPort();
		int len = receivePacket.getLength();
		System.out.println("Length: " + len);
		System.out.print("Containing: " );
		System.out.println(this.receivePacket.getData());
		

		//decode request and assign toFloor as the floor that will be sent to elevator
		if(fromPort == ELEVATORPORT1 || fromPort == ELEVATORPORT2 || fromPort == ELEVATORPORT3) { //received from elevator
			if(measuring&&data[1]==0) {aStartTime = System.nanoTime();}//start time for arrival
			System.out.println("Received from elevator");
			int elevatorNumber = data[0];
			int floorDecode = data[2];
			int currFloor;
			if(floorDecode == 0) {
				currFloor = data[3];
			}
			else {
				currFloor = data[3] + 10;
			}
			//update the correct elevator
			if(elevatorNumber==1) {
				//direction: 0 = stop; 1 = up; 2 = down
				if(data[1] == 4) {
					System.out.println("Elevator Door Jammed Waiting for Fix");
					if(!isActive1) { //door was already jammed elevator reporting its fixed
						isActive1 = true;
					}
					isActive1 = false;
					return;
				}
				elevatorState1 = data[1];
				if(data[1]==0 && measuring) arrivalTimes.add(System.nanoTime()-aStartTime);//end time for arrival
				elevatorFloor1 = currFloor;
				System.out.println("\n Updating E1: "+ elevatorState1+ ", "+elevatorFloor1+ "\n");
				if(elevatorState1 == 4)
					System.out.println("Elevator1 Jammed::: ERROR");
			}
			else if(elevatorNumber==2) {
				if(data[1] == 4) {
					System.out.println("Elevator Door Jammed Waiting for Fix");
					if(!isActive2) { //door was already jammed elevator reporting its fixed
						isActive2 = true;
					}
					isActive2 = false;
					return;
				}
				elevatorState2 = data[1];
				if(data[1]==0 && measuring) arrivalTimes.add(System.nanoTime()-aStartTime);//end time for arrival
				elevatorFloor2 = currFloor;
				System.out.println("\n Updating E2: "+ elevatorState2+ ", "+elevatorFloor2+ "\n");
				if(elevatorState2 == 4)
					System.out.println("Elevator2 Jammed::: ERROR");
			}
			else if (elevatorNumber == 3){
				if(data[1] == 4) {
					System.out.println("Elevator Door Jammed Waiting for Fix");
					if(!isActive3) { //door was already jammed elevator reporting its fixed
						isActive3 = true;
					}
					isActive3 = false;
					return;
				}
				elevatorState3 = data[1];
				if(data[1]==0 && measuring) arrivalTimes.add(System.nanoTime()-aStartTime);//end time for arrival
				elevatorFloor3 = currFloor;
				System.out.println("\n Updating E3: "+ elevatorState3+ ", "+elevatorFloor3+ "\n");
				if(elevatorState3 == 4)
					System.out.println("Elevator3 Jammed::: ERROR");
			}
			
			
			


		}
		else { //1 is arbitrary (from client/Button)
			if(measuring) fStartTime = System.nanoTime();//start for arrival time
			System.out.println("recieved from floor");
			byte msg[] = new byte[PACKETSIZE];
			int direction = data[0];
			
			msg[1] = (byte)direction; //direction
			int floorRequest0 = data[1];
			int floorRequest1 = data[2];
			if(floorRequest0 == 0) {
				toFloor = floorRequest1;
			}
			else {
				toFloor = floorRequest1 + 10;
			}
			msg[2] = data[1];
			msg[3] = data[2];
			
			msg[4] = data[3];
			msg[5] = data[4];
			
			int toElevator = getBestElevator(toFloor, direction);
			msg[0] = (byte)toElevator;
			sendElevator(toElevator, toFloor, msg);
			System.out.println( "Server: Sending packet:");
			System.out.println("To host: " + sendPacket.getAddress());
			System.out.println("Destination host port: " + sendPacket.getPort());
			len = sendPacket.getLength();
			System.out.println("Length: " + len);
			System.out.print("Containing: ");
			System.out.println(new String(sendPacket.getData(),0,len));
			System.out.println(this.receivePacket.getData() + "\n");
			// or (as we should be sending back the same thing)
			// System.out.println(received);
		}
		//decode data packet from elevator and update status bars for elevators
		

		/* Slow things down (wait 2 seconds)
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e ) {
			e.printStackTrace();
			System.exit(1);
		}*/




		//Identify and get best elevators port for send pack		
	}
	


	@SuppressWarnings("deprecation")
	void shutdownSystem() {
		receiveSocket.close();
		sendSocket.close();
		t1.stop();
		t2.stop();
		t3.stop();
		
		System.exit(1);
	}
	void updateDate() {
		currentDate = new Date();
	}

	public static void main( String args[] ) throws IOException 
	{
		Scheduler a = new Scheduler();
		if(measuring) new MeasurementOutput(a).start(); //run measuring
		while(true) {
			a.receiveAndSend();
		}
	}
}
