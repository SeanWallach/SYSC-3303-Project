
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
	Date currentDate;
	// Elevator Uno, Dos, Tres;
	int elevatorState1, elevatorState2, elevatorState3, // will have to turn these into thread safe ----- 0 is idle 1 is
														// up 2 is down
			elevatorFloor1, elevatorFloor2, elevatorFloor3; // collections, ArrayList?
	boolean e1Function, e2Function, e3Function;

	static int ELEVATORPORT1 = 69, ELEVATORPORT2 = 70, ELEVATORPORT3 = 71, PACKETSIZE = 25, SELFPORT = 219,
			FLOORPORT = 238;
	public FaultTimer E1Timer, E2Timer, E3Timer;

	public Scheduler() {
		elevatorState1 = 0;
		elevatorState2 = 0;
		elevatorState3 = 0;
		elevatorFloor1 = 0;
		elevatorFloor2 = 0;
		elevatorFloor3 = 0; // all elevators should be idle at startup
		e1Function = true;
		e2Function = true;
		e3Function = true;
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
			// receiveSocket.setSoTimeout(2000);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}

	private int getBestElevator(int toFloor, int direction) {
		// from current data which elevator is best to send
		// check to see if any elevator status is marked as idle. Easy Out;
		if (elevatorState1 == 0 && e1Function)
			return 1;
		if (elevatorState2 == 0 && e2Function)
			return 2;
		if (elevatorState3 == 0 && e3Function)
			return 3;

		// No Elevator idle Check other specs to find best a case

		if (elevatorState1 == 1 && direction == 1 && e1Function && toFloor >= elevatorFloor1)
			return 1;
		else if (elevatorState2 == 1 && direction == 1 && e2Function && toFloor >= elevatorFloor2)
			return 2;
		else if (elevatorState3 == 1 && direction == 1 && e3Function && toFloor >= elevatorFloor3)
			return 3;
		else if (elevatorState1 == 2 && direction == 2 && e1Function && toFloor <= elevatorFloor1)
			return 1;
		else if (elevatorState2 == 2 && direction == 2 && e2Function && toFloor <= elevatorFloor2)
			return 2;
		else if (elevatorState3 == 2 && direction == 2 && e3Function && toFloor <= elevatorFloor3)
			return 3;
		else {
			System.out.println("ALL ELEVATORS BROKEN SHUTTING DOWN");
			shutdownSystem();
		}

		// Implementation should prevent any situation where all of these fail; however
		// recall function
		return getBestElevator(toFloor, direction);
	}

	private void sendElevator(int elev, int floor, byte msg[]) throws IllegalStateException {
		// from the best elevator create the correct packet and send
		// correct data
		int toPort;
		// assign proper port
		switch (elev) {
		// set Elevator in msg and start timer for elevator to respond
		//timer starts or updates
		case (3):
			toPort = ELEVATORPORT3;
			elevatorState3 = msg[1];
			E3Timer.time();
		case (2):
			toPort = ELEVATORPORT2;
			elevatorState2 = msg[1];
			E2Timer.time();

		default:
			toPort = ELEVATORPORT1;
			elevatorState1 = msg[1];
			E1Timer.time();
			
		}

		sendPacket = new DatagramPacket(msg, msg.length, receivePacket.getAddress(), 68);
		// Send the datagram packet to the client via the send socket.
		try {
			sendSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Server: packet sent");

	}

	public void receiveAndSend() {
		// Construct a DatagramPacket for receiving packets up
		// to 100 bytes long (the length of the byte array).
		updateDate();
		int toFloor = 0;
		byte data[] = new byte[PACKETSIZE];
		receivePacket = new DatagramPacket(data, data.length);
		// System.out.println("Scheduler: Waiting for Packet.\n");

		// Block until a datagram packet is received from receiveSocket.
		try {
			// System.out.println("Waiting..."); // so we know we're waiting
			receiveSocket.receive(receivePacket);

			// catch IO exception and print stack trace
		} catch (IOException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}

		// Process the received datagram.
		/*
		 * System.out.println("Scheduler: Packet received:");
		 * System.out.println("From host: " + receivePacket.getAddress());
		 * System.out.println("Host port: " + receivePacket.getPort());
		 */
		int fromPort = receivePacket.getPort();
		/*
		 * int len = receivePacket.getLength(); System.out.println("Length: " + len);
		 * System.out.print("Containing: " );
		 * System.out.println(this.receivePacket.getData());
		 */

		// decode request and assign toFloor as the floor that will be sent to elevator
		if (fromPort == ELEVATORPORT1 || fromPort == ELEVATORPORT2 || fromPort == ELEVATORPORT3) { // received from
																									// elevator
			System.out.println("Received from elevator");
			int elevatorNumber = data[0];
			int floorDecode = data[2];
			int currFloor;
			if (floorDecode == 0) {
				currFloor = data[3];
			} else {
				currFloor = data[3] + 10;
			}
			// update the correct elevator
			if (elevatorNumber == 1) {
				// direction: 0 = stop; 1 = up; 2 = down
				if (data[1] != 0) {
					E1Timer.time();
				} else if (data[1] == 0) {
					E1Timer.stopTimer();
				}
				elevatorState1 = data[1];
				elevatorFloor1 = currFloor;
				System.out.println("\n Updating E1: " + elevatorState2 + ", " + elevatorFloor2 + "\n");
			} 
			
			else if (elevatorNumber == 2) {
				if (data[1] != 0) {
					E2Timer.time();
				} else if (data[1] == 0) {
					E2Timer.stopTimer();
				}
				elevatorState2 = data[1];
				elevatorFloor2 = currFloor;
				System.out.println("\n Updating E2: " + elevatorState2 + ", " + elevatorFloor2 + "\n");
			}
			
			else {
				if (data[1] != 0) {
					E3Timer.time();
				} else if (data[1] == 0) {
					E3Timer.stopTimer();
				}

				elevatorState3 = data[1];
				elevatorFloor3 = currFloor;
				System.out.println("\n Updating E3: " + elevatorState3 + ", " + elevatorFloor3 + "\n");
			}

		} else { // 1 is arbitrary (from client/Button)
			System.out.println("recieved from floor");
			byte msg[] = new byte[PACKETSIZE];
			int direction = data[0];

			msg[1] = (byte) direction; // direction
			int floorRequest0 = data[1];
			int floorRequest1 = data[2];
			if (floorRequest0 == 0) {
				toFloor = floorRequest1;
			} else {
				toFloor = floorRequest1 + 10;
			}
			msg[2] = data[1];
			msg[3] = data[2];

			msg[4] = data[3];
			msg[5] = data[4];

			int toElevator = getBestElevator(toFloor, direction);
			msg[0] = (byte) toElevator;
			sendElevator(toElevator, toFloor, msg);
			/*
			 * System.out.println( "Server: Sending packet:");
			 * System.out.println("To host: " + sendPacket.getAddress());
			 * System.out.println("Destination host port: " + sendPacket.getPort()); len =
			 * sendPacket.getLength(); System.out.println("Length: " + len);
			 * System.out.print("Containing: "); System.out.println(new
			 * String(sendPacket.getData(),0,len));
			 * System.out.println(this.receivePacket.getData() + "\n");
			 */
			// or (as we should be sending back the same thing)
			// System.out.println(received);
		}
		// decode data packet from elevator and update status bars for elevators

		/*
		 * Slow things down (wait 2 seconds) try { Thread.sleep(2000); } catch
		 * (InterruptedException e ) { e.printStackTrace(); System.exit(1); }
		 */

		// Identify and get best elevators port for send pack
	}

	void shutdownSystem() {
		receiveSocket.close();
		sendSocket.close();
		System.exit(1);
	}

	void updateDate() {
		currentDate = new Date();
	}

	private void setUpFailTimers() {
		E1Timer = new FaultTimer(this, 1);
		E2Timer = new FaultTimer(this, 2);
		E3Timer = new FaultTimer(this, 3);
		System.out.println("Timers and fail tasks setup");

	}

	public static void main(String args[]) {

		Scheduler a = new Scheduler();
		a.setUpFailTimers();
		while (true) {
			a.receiveAndSend();
		}
	}
}
