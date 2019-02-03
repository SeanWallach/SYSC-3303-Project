

//This class is the floor for the project
//Last edited January 21st, 2016

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class Floor {
	
	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendReceiveSocket;
	
	static String fileName = ".//input.txt";
	static int numOfFloors = 10;
	static int SCHEDULER_PORT = 219;
	
	List<String> allLines;       // input file content
	Floor Floors[];
	
	int currentFloor;            // the floor of the current object 
	
	int elevatorDirection; 		// 0 is stop, 1 up, 2 down
	int floorButton;      		// 1 is going up, 0 is going down
	int floorLamp;              // 1 is waiting, 0 is not waiting
	int directionLamp;          // 2 is for off, 1 is for going up, 0 is going down
	
	// Constructor with custom floor level
	public Floor(int currFloor) {
	   try {
	      // Construct a datagram socket and bind it to any available port on the local host machine
	      sendReceiveSocket = new DatagramSocket();
	      if (currFloor > numOfFloors) {
	    	  System.out.println("This floor is too high");
	    	  System.exit(1);
	      }
	      this.currentFloor = currFloor;
	      this.floorLamp = 0;
	      this.directionLamp = 2;
	      System.out.println("Floor " + currentFloor + " created.");
	   } catch (SocketException se) {   // Can't create the socket.
	      se.printStackTrace();
	      System.exit(1);
	   }
	}
	
	// Reads input files for elevator calls
	public void readFile(String fileName) {
	   Path path = Paths.get(fileName); 
	   try {
	 	  allLines = Files.readAllLines(path);
	 	  for (String line : allLines) {
				String temp = line;
				sendInstructions(temp);
	     }
	   } catch (IOException e) {
				e.printStackTrace();  
	   }
	}
	
	public void sendInstructions(String line) {
			// For testing, prints line of the input file
			System.out.println("Request made: " + line);	
		   
		   String[] splitted = line.split("\\s+");
		   
		   byte msg[] = new byte[3];	// Bit 0 - Direction 	Bit 1,2 - current Floor
		   
		   // Direction
		   if (splitted[2].equals("up")) {
			   msg[0] = 1;
			   this.directionLamp = 1;
			   this.floorButton = 1;
		   } else if (splitted[2].equals("down")) {
			   msg[0] = 0;
			   this.directionLamp = 0;
			   this.floorButton = 0;
		   } else {
			   System.out.println("Input file format incorrect");
			   System.exit(1);
		   }
		   
		   // Current floor
		   if (splitted[1].length() > 1) {	// if floor is in double digits
			   byte n1 = (byte) (Integer.parseInt(splitted[1])/10);	// get first digit of string
			   byte n2 = (byte) (Integer.parseInt(splitted[1])%10);	// get last digit of string
			   msg[1] = n1;
			   msg[2] = n2;
		   } else {
			   byte n = (byte) (Integer.parseInt(splitted[1]));
			   msg[1] = 0;
			   msg[2] = n;
		   }
		   
		   try {
		      sendPacket = new DatagramPacket(msg, msg.length,
		                                    InetAddress.getLocalHost(), SCHEDULER_PORT);
		   } catch (UnknownHostException e) {
		      e.printStackTrace();
		      System.exit(1);
		   }
		   
		   try {
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
		   
		   // Now receiving
		   byte data[] = new byte[3];
		   receivePacket = new DatagramPacket(data, data.length);
		
		   try {
		      // Block until a datagram is received via sendReceiveSocket.  
		      sendReceiveSocket.receive(receivePacket);
		   } catch(IOException e) {
		      e.printStackTrace();
		      System.exit(1);
		   }
		   
		   System.out.print("Received content containing: ");
		
		   // Form a String from the byte array.
		   String received = new String(data,0,receivePacket.getLength());   
		   System.out.println(received);
		
	}
	

	public static void main(String args[])
	{
		  Floor Floors[] = new Floor[numOfFloors]; 
		  for (int i = 1; i <= numOfFloors; i++) {
			  Floors[i-1] = new Floor(i);
		  }
	   Floors[0].readFile(fileName);
	   
	}
}
