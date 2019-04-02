// Scheduler.java
// Maveric Garde 101031617
// This class is the Intermediate of a Client/Server UDP client on
// UDP/IP. The server receives from a client (elevator button/user) or server (Elevator) a packet 
// containing a data array with floor and direction, then forwards it to the other client or server.
// Last edited Feb 9th 2019
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.io.*;
import java.net.*;
import java.util.*;

public class Scheduler {
    //ConcurrentLinkedQueue q;
    
	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendSocket, receiveSocket;
	long[] q1, q2; 
	Thread t1, t2, t3, t4; //threads for monitoring elevators
	Date currentDate; 
	Boolean isActive1 = true, isActive2 = true, isActive3 = true, isActive4 = true; //To keep track of which elevators are talking to the scheduler
	Elevator Uno, Dos, Tres;
	int elevatorState1, elevatorState2, elevatorState3, elevatorState4,//will have to turn these into thread safe ----- 0 is idle 1 is up 2 is down
		elevatorFloor1, elevatorFloor2, elevatorFloor3, elevatorFloor4,
		counter1, counter2;//collections, ArrayList? 
	static int ELEVATORPORT1 = 69, ELEVATORPORT2 = 70, ELEVATORPORT3 = 71, ELEVATORPORT4 = 72,
			PACKETSIZE = 25, SELFPORT = 219, FLOORPORT = 238;

	public ArrayList<Long> arrivalTimes = new ArrayList<Long>(); 	
	public ArrayList<Long> floorBTimes = new ArrayList<Long>(); 	
	private static boolean measuring = true;	
	private long aStartTime, fStartTime;
	
	public Scheduler()
	{
		elevatorState1 = 0; elevatorState2 = 0; elevatorState3 = 0; elevatorFloor1 = 0; 
		elevatorFloor2 = 0; elevatorFloor3 = 0; counter1 = 0; counter2 = 0;
		elevatorState4 = 0; elevatorFloor4 = 0;//all elevators should be idle at startup
		q1 = new long[10];
		q2 = new long[10];
		t1 = new Thread(new FaultScheduler(this, 1));
		t2 = new Thread(new FaultScheduler(this, 2));
		t3 = new Thread(new FaultScheduler(this, 3));
		t4 = new Thread(new FaultScheduler(this, 4));
		//t4?? Will fault scheduling be needed?
		//q = new ConcurrentLinkedQueue();
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
		t4.start();
	}

	private int getBestElevator(int toFloor, int direction) {
		//from current data which elevator is best to send
		//check to see if any elevator status is marked as idle. Easy Out;
		if(elevatorState1 == 0 && isActive1) return 1;
		if(elevatorState2 == 0 && isActive2) return 2;
		if(elevatorState3 == 0 && isActive3) return 3;
		if(elevatorState4 == 0 && isActive4) return 4;
		
		
		//No Elevator idle Check other specs to find best a case
		
		if((elevatorState1 == 1 && direction == 1 && isActive1) && toFloor >= elevatorFloor1)
			return 1;
		if((elevatorState2 == 1 && direction == 1 && isActive2) && toFloor >= elevatorFloor2)
			return 2;
		if((elevatorState3 == 1 && direction == 1 && isActive3) && toFloor >= elevatorFloor3)
			return 3;
		if((elevatorState4 == 1 && direction == 1 && isActive4 && toFloor >= elevatorFloor4))
			return 4;
		if((elevatorState1 == 2 && direction == 2 && isActive1) && toFloor <= elevatorFloor1)
			return 1;
		if((elevatorState2 == 2 && direction == 2 && isActive2) && toFloor <= elevatorFloor2)
			return 2;
		if((elevatorState3 == 2 && direction == 2 && isActive3) && toFloor <= elevatorFloor3)
			return 3;
		if((elevatorState4 == 2 && direction == 2 && isActive4) && toFloor <= elevatorFloor4)
			return 4;
		if(!isActive1 && !isActive2 && !isActive3 && !isActive4) {
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
		case(4):
			toPort = ELEVATORPORT4;
			elevatorState4 = msg[1];
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
		
		sendPacket = new DatagramPacket(msg, msg.length,
				receivePacket.getAddress(), 68);
		// Send the datagram packet to the client via the send socket. 
		try {
			sendSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		if(measuring) {
			floorBTimes.add(System.nanoTime() - fStartTime);//packet sent measure elapsed time
		}
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
		if(fromPort == ELEVATORPORT1 || fromPort == ELEVATORPORT2 || fromPort == ELEVATORPORT3 || fromPort == ELEVATORPORT4) {
			//long start = System.nanoTime();
			//received from elevator
			if(measuring&&data[1]==0) {
				aStartTime = System.nanoTime();
				}
			System.out.println("Received from elevator");
			int elevatorNumber = data[0];
			int floorDecode = data[2];
			int currFloor;
			if(floorDecode == 0) {
				currFloor = data[3];
			}
			else if(floorDecode == 1) {
				currFloor = data[3] + 10;
			}
			else {
				currFloor = data[3] + 20;
			}
			//update the correct elevator
			if(elevatorNumber==1) {
				//direction: 0 = stop; 1 = up; 2 = down
				if(data[1] == 4) {
					System.out.println("Elevator Door Jammed Waiting for Fix");
					if(!isActive1) { //door was already jammed elevator reporting its fixed
						isActive1 = true;
					}
					else {
						isActive1 = false;
					}
					return;
				}
				elevatorState1 = data[1];
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
					else {
						isActive2 = false;
					}
					return;
				}
				elevatorState2 = data[1];
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
					else {
						isActive3 = false;
					}
					return;
				}
				elevatorState3 = data[1];
				elevatorFloor3 = currFloor;
				System.out.println("\n Updating E3: "+ elevatorState3+ ", "+elevatorFloor3+ "\n");
				if(elevatorState3 == 4)
					System.out.println("Elevator3 Jammed::: ERROR");
			}
			else if(elevatorNumber==4) {
				//direction: 0 = stop; 1 = up; 2 = down
				if(data[1] == 4) {
					System.out.println("Elevator Door Jammed Waiting for Fix");
					if(!isActive4) { //door was already jammed elevator reporting its fixed
						isActive4 = true;
					}
					else{
						isActive4 = false;
					}
					return;
				}
				elevatorState1 = data[1];
				elevatorFloor1 = currFloor;
				System.out.println("\n Updating E4: "+ elevatorState4+ ", "+elevatorFloor4+ "\n");
				if(elevatorState1 == 4)
					System.out.println("Elevator4 Jammed::: ERROR");
			}	

			if(data[1]==0 && measuring) {
				arrivalTimes.add(System.nanoTime()-aStartTime);//end time for arrival
			}
		}
		else { //1 is arbitrary (from client/Button)
			long start = System.nanoTime();
			System.out.println("received from floor");
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
			long end = System.nanoTime();
			q2[counter2] = end - start;
			if(counter2 == 10) {
				long temp = 0;
				for(int i = 0; i< 10; i++) {
					temp = temp + q2[i];
				}
				System.out.println("Mean of last 10 Button updates is: " + temp/10/1000000 + "ms");
			}
		}
			
	}
	


	@SuppressWarnings("deprecation")
	void shutdownSystem() {
		receiveSocket.close();
		sendSocket.close();
		t1.stop();
		t2.stop();
		t3.stop();
		t4.stop();
		System.exit(1);
	}
	void updateDate() {
		currentDate = new Date();
	}

	public static void main( String args[] )
	{
		if(measuring) {
			new MeasurementOutput.start(); //run measuring
		}
		Scheduler a = new Scheduler();
		while(true) {
			a.receiveAndSend();
		}
	}
}

