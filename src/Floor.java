//This class is the floor for the project
//Last edited 3/23/2019

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
	static int SCHEDULER_PORT = 219, SELFPORT = 238;
	static String temp;
	static List<String> allLines;       // input file content
	
	Floor Floors[];
	
	int currentFloor;            // the floor of the current object. 2 is lobby
	int elevatorDirection; 		// 0 is stop, 1 up, 2 down
	int floorButton;      		// 1 is going up, 0 is going down
	int floorLamp;              // 1 is waiting, 0 is not waiting
	int directionLamp;          // 2 is for off, 1 is for going up, 0 is going down
	
	// Constructor with custom floor level
	public Floor(int currFloor, int port) {
	   try {
	      // Construct a datagram socket and bind it to any available port on the local host machine
	      sendReceiveSocket = new DatagramSocket(port);
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
	
	
	public void sendInstructions(String line) throws InterruptedException {
	// For testing, prints line of the input file
		System.out.println("Request made: " + line);	

		 String[] splitted = line.split("\\s+");

		 byte msg[] = new byte[5];	// Bit 0 - Direction 	Bit 1,2 - initial Floor   Bit 3,4 - des Floor
	
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

		 
		 if(splitted[3].length()>1) {//destination floor is double digits
		     byte d1 = (byte) (Integer.parseInt(splitted[3])/10);	// get first digit of string
		     byte d2 = (byte) (Integer.parseInt(splitted[3])%10);	// get last digit of string
		     msg[3] = d1;
		     msg[4] = d2; 
		 }
		 else {
			 byte d = (byte) (Integer.parseInt(splitted[3]));
			 msg[3] = 0;
			 msg[4] = d;
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
		 
		 
		 try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println("cant Sleep");
		}
	}

	
	// For iteration 5
	public void receiveMessage() {
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
		  Floors[i-1] = new Floor(i, SELFPORT + i);
	   }
		  
	// Reads input files for elevator calls
	   Path path = Paths.get(fileName); 
	   try {
	 	  allLines = Files.readAllLines(path);
	 	  int firstFloor = 0;
	 	  int lastTime = 0;
	 	  for (String line : allLines) {
				temp = line;
				String[] splitted = line.split("\\s+");		// Splits line
				int floorNum = Integer.parseInt(splitted[1]);	// Get the floor number from line
				
				// if its not the first floor thats read from input.txt, we subtract the lastTime from the current time to find out how long to wait
				if (firstFloor != 0) {
					 // split splitted[0] by : to get ms, sec, etc
					 String[] timeDelay = splitted[0].split(":");
					 int msToWait = 0;
					 msToWait += Integer.parseInt(timeDelay[3]);	// deals with ms
					 msToWait += (1000*Integer.parseInt(timeDelay[2]));		// deals with s
					 msToWait += (1000*60*Integer.parseInt(timeDelay[1]));		// deals with minutes
					 msToWait += (1000*60*60*Integer.parseInt(timeDelay[0])); 	// deals with hours
					 System.out.println("Time to wait: " + (msToWait - lastTime)/1000 + " seconds");
					 Thread.sleep(msToWait - lastTime);
					 lastTime = msToWait;
				} else {
					// split splitted[0] by : to get ms, sec, etc
					String[] timeDelay = splitted[0].split(":");
					lastTime += Integer.parseInt(timeDelay[3]);
					lastTime += (1000*Integer.parseInt(timeDelay[2]));
					lastTime += (1000*60*Integer.parseInt(timeDelay[1]));
					lastTime += (1000*60*60*Integer.parseInt(timeDelay[0]));
					System.out.println("Time to wait: 0 seconds");
					firstFloor = 1;
				}
				
				Floors[floorNum].sendInstructions(temp);
				
	     }
	   } catch (IOException e) {
	  	e.printStackTrace();  
	   } catch (InterruptedException e) {
		e.printStackTrace();
	}
	   
	}
}