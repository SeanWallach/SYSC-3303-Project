//This class is the floor for the project
//Last edited 3/23/2019

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class Floor {
	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendReceiveSocket;
	
	static String fileName = ".//input.txt";
	
	static int SCHEDULER_PORT = 219, SELFPORT = 238;
	static String temp;
	static List<String> allLines;       // input file content
	
	Floor_Gui gui;
	
	
	int elevatorDirection; 		// 0 is stop, 1 up, 2 down
	int numOfFloors;
	ArrayList<Integer> request;
	
	// Constructor with custom floor level
	public Floor(int floor, int port) {
		request = new ArrayList<Integer>();
		numOfFloors = floor;
		gui = new Floor_Gui(floor,this);
		
	   try {
	      // Construct a datagram socket and bind it to any available port on the local host machine
	      sendReceiveSocket = new DatagramSocket(port);
	      
	   } catch (SocketException se) {   // Can't create the socket.
	      se.printStackTrace();
	      System.exit(1);
	   }
	   
	   this.receiveMessage();
	}
	
	
	public void sendInstructions(int floor, int direction) {
		
		 this.request.add(floor);
		 byte msg[] = new byte[5];	// Bit 0 - Direction 	Bit 1,2 - destination Floor   
		 msg[0] = (byte)direction;
		 if(floor>=20) {
			 msg[1]=2;
			 msg[2] = (byte)(floor-20);
		 }
		 else if(floor>=10) {
			 msg[1]=1;
			 msg[2] = (byte)(floor-10);
		 }
		 else {
			 msg[1]=0;
			 msg[2] = (byte)floor;
		 }
		msg[3] =0;
		msg[4] =0;
		 System.out.print(msg[0]+" "+msg[1]+" "+msg[2]+"\n");
		 try {
		    sendPacket = new DatagramPacket(msg, msg.length,InetAddress.getLocalHost(), SCHEDULER_PORT);
		    sendReceiveSocket.send(sendPacket);
		 } catch (IOException e) {
		    e.printStackTrace();
		    System.exit(1);
		 }

		 System.out.print("Contents sent: " );
		 for (int i = 0; i < msg.length; i++) {	// Printing Byte array contents
		    System.out.print(msg[i]);
		 }
		 System.out.println("\nElevator request sent.\n");
		 
	}

	public void verify(int floor) {
		int count=0;
		System.out.println("verify: "+floor);
		for(int fl: this.request) {
			if(floor==fl) {
				this.request.remove(count);
			}
			count++;
		}
		this.receiveMessage();
	}
	
	// For iteration 5
	public void receiveMessage() {
		// Now receiving
		 byte temp[] = new byte[3];
		 receivePacket = new DatagramPacket(temp, temp.length);
		 try {
		    // Block until a datagram is received via sendReceiveSocket.  
		    sendReceiveSocket.receive(receivePacket);
		 } catch(IOException e) {
		    e.printStackTrace();
		    System.exit(1);
		 }
		 System.out.print(temp[0]+" "+temp[1]+" "+temp[2]+"\n");
		 int eleFloor = 0;
		 	if(temp[1]>=2) {
		 		eleFloor = temp[2] + 20;
			}
			else if (temp[1] >= 1) {
				eleFloor = temp[2] + 10;
			} else {
				eleFloor = temp[2];
			}
		 	System.out.print(eleFloor);
		this.verify(eleFloor);///////////////////////////////////////////////////
		 
		 System.out.print("Received content containing: ");
		 // Form a String from the byte array.
		 String received = new String(temp,0,receivePacket.getLength());   
		 System.out.println(received);
	}
	
	public static void main(String args[])
	{
	   Floor Floors = new Floor(22,SELFPORT);
	   
	}
}
