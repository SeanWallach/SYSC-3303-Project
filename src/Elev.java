import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Elev extends Thread{

	private int elevatorNumber;
	private int motor; // 0==stop 1==up 2==down
	private int topFloor;
	private boolean door; // false=closed true=open
	
	private int[] buttons;
	private int currentFloor; // 1 is default
	private ArrayList<Integer> serviceQueue; // floors that will be serviced in organized order
	public boolean requestWaiting;

	private int SEND_PORT_NUMBER = 219; // schedualer port
	private DatagramSocket sendSocket;
	private int myPort;
	private Elevator contorller;

	
	public boolean jam; //door jam sensor output
	public boolean functioning;	//service state 
	public ArrayList<Integer> elevLamp;
	
	//new this itteration
	private boolean passenger; ///////////////////////////////////////////////////////////////////////
	
	public Elev(int elevNum, int floors, int port, Elevator thisController) {
		this.contorller = thisController;
		requestWaiting=false;
		buttons = new int[floors];
		for (int i = 1; i <= floors; i++) {
			buttons[i - 1] = i;
		}
		
		elevatorNumber = elevNum;
		door = false;
		topFloor = floors;
		motor = 0;
		currentFloor = 1;
		serviceQueue = new ArrayList<Integer>();
		elevLamp = new ArrayList<Integer>(); 	
		myPort = port;
		jam = false; 						
		functioning = true; 
		passenger = false; ///////////////////////////////////////////////////////////////////////
		try {
			sendSocket = new DatagramSocket(myPort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.sendRequest(currentFloor, motor);

	}

	public void addRequest(int initial, int desination) {
		synchronized(this) {
			this.serviceQueue.add(initial);
	//		this.serviceQueue.add(desination);
			if (motor==1) {
				System.out.print("E"+ this.elevatorNumber+ " queue: ");
				Collections.sort(serviceQueue); // sorts list from smallest to largest
				for(Integer i: this.serviceQueue) System.out.print(i+" ");
	
			} else if (motor==2){
				System.out.print("E"+ this.elevatorNumber+ " queue: ");
				Collections.sort(serviceQueue);
				Collections.reverse(serviceQueue); // sorts list from largest to smallest
				for(Integer i: this.serviceQueue) System.out.print(i+" ");
	
			}
			elevLamp = serviceQueue;  //the lamp displays all floors to be visited
			
			//display lamp 								
			String temp ="";							
			for(Integer i: elevLamp) {					
				temp +=" "+i;							
			}											
			//System.out.println("Elevator " +this.elevatorNumber + " visiting:"+ temp + "\n"); 
			
			
			requestWaiting=false;
			notifyAll();
		}
	}

	public void service()throws InterruptedException { // moves the elevator through queue to service requests
		while(true) {
			synchronized(this) {
				while(requestWaiting || this.serviceQueue.isEmpty() )
				{
					wait();
				}
				if(functioning && !door) {
				//System.out.println("^^^^^^^Elevator " + this.elevatorNumber + "^^^^^^");
				if (this.currentFloor == this.serviceQueue.get(0)) {
					this.serviceQueue.remove(0);
					elevLamp = this.serviceQueue;
					if(this.serviceQueue.isEmpty()) this.motor=0;
					this.sendRequest(this.currentFloor, this.motor);
					System.out.println("\n****Elevator" +this.elevatorNumber + " at Des: "+ this.currentFloor+"****\n");
					
					//display lamp 								
					String temp ="";							
					for(Integer i: elevLamp) {					
						temp +=" "+i;							
					}											
					//System.out.println("Elevator " +this.elevatorNumber + " visiting:"+ temp + "\n"); 
					
					this.open_Close();   					
					Thread.sleep(3000);
					this.displayButtons();      ////////////////////////////////////////////////////////
					this.open_Close();						
				} else if (this.serviceQueue.get(0) > this.currentFloor) {
					System.out.println("E"+this.elevatorNumber+" going up, current floor: " + currentFloor+ "\n");
					this.currentFloor++;
					this.motor = 1;
					this.sendRequest(this.currentFloor, this.motor);
					Thread.sleep(1000);
				} else if(this.serviceQueue.get(0) < this.currentFloor){
					System.out.println("E"+ this.elevatorNumber+" going down, current floor: " + currentFloor+"\n");
					this.currentFloor--;
					this.motor = 2;
					this.sendRequest(this.currentFloor, this.motor);
					Thread.sleep(1000);
				}
			}
			}
		}

	}
	
	public int getCurrentFLoor() {			
		return this.currentFloor;
	}
	
	
	
	public void open_Close() {                                           
		if(door) {   //if door open
			System.out.println("Elevator "+ this.elevatorNumber + " closing doors");
			
			if(jam) {
				this.sendRequest(this.currentFloor, 4);
			
			while(jam == true) {
				System.out.println("Elevator "+ this.elevatorNumber + " Door jamed\nFixing Jam....");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				System.out.println("Elevator "+ this.elevatorNumber + "Jam Fixed");
				jam=false;
				System.out.println("Elevator "+ this.elevatorNumber + "closing doors");
						
			}
			this.sendRequest(this.currentFloor, 4);
			}
			door = false; //close door
		}
		else {  //if door closed
			System.out.println("Elevator "+ this.elevatorNumber + " Opening doors");
			if(jam) {
				this.sendRequest(this.currentFloor, 4);
			while(jam == true) {
				System.out.println("Elevator "+ this.elevatorNumber + " Door jamed\nFixing Jam....");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				System.out.println("Elevator "+ this.elevatorNumber + "Jam Fixed");
				jam=false;
				System.out.println("Elevator "+ this.elevatorNumber + " Opening doors");						
			}
			this.sendRequest(this.currentFloor, 4);
			}
			door = true; //close door
		}
	}		

	
	  public void displayButtons() { //will display buttons for gui, but act as //////////////////////////////////////////////////
		 // stud for new passengers boarding //display button as gui
	  
		  if(this.passenger == false) { //if there wasnt a passenger, a new one boarded
			  Random rand = new Random(); int next = rand.nextInt(topFloor) + 1;
			  this.addRequest(next,0); passenger = true; 
			  if(next > currentFloor) {
				  this.sendRequest(currentFloor, 1); //going up 
				  } else {
				  this.sendRequest(currentFloor, 2); //going down 
				  } 
			  System.out.println("\n\n\n Elevator "+ this.elevatorNumber + " adding "+ next + "\n\n\n");
			  } else { passenger = false;
	  //if there was a passenger, then they got off 
			  } 
		  
		  }
	 

	public void sendRequest(int currFloor, int direction) { // send new internal requests to the scheduler data->
															// ID,direction,floor,floor

		byte data[] = new byte[4];
		data[0] = (byte) elevatorNumber;
		data[1] = (byte) direction;
		if (currFloor >= 10) {
			data[2] = (byte) 1;
			data[3] = (byte) (currFloor - 10);
		} else {
			data[2] = (byte) 0;
			data[3] = (byte) currFloor;
		}

		//System.out.println("Sending packet containing: " + data.toString());
		try {
			DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(),
					SEND_PORT_NUMBER);
			sendSocket.send(sendPacket);
		} catch (SocketException se) { // Can't create the socket.
			sendSocket.close();
			se.printStackTrace();
			System.exit(1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//System.out.println("Packet sent to Scheduler");
	}
	
	public void run() {
		try {
			this.service();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
