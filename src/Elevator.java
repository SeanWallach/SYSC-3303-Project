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

	private int topFloor;
	private int bottomFloor;
	private int[] buttons;
	private ArrayList<Integer> lamp;
	private int motor; //0==stop 1==up 2==down
	private boolean door; //false=closed true=open
	private int elevatorNumber;
	private int currentFloor; //2 is lobby
	private Receiver link;
	private int SEND_PORT_NUMBER = 219; //schedualer port
	private DatagramSocket sendSocket;
	
	private ArrayList<Integer> serviceQueue; //floors that will be serviced in organized order
	 
	
	public Elevator(int floors, int elevatorNum) {
		this.bottomFloor = 1;
		this.topFloor = floors;
		//for(int i=1;i<=floors;i++) {
			//buttons[i]=i;
			
	//	}
		this.elevatorNumber = elevatorNum;
		this.motor = 0x0;
		this.door = false;
		this.currentFloor = 0x2;	
		lamp = new ArrayList<Integer>();
	
	}
	
	public int getMotor() {return motor;}
	public boolean getDoor() {return door;}
	public int getElevatorNumber() {return elevatorNumber;}
	public int getFloor() {return currentFloor;}
	
	public void organizeQueue() { //organizes the queue in to a sequential servicing order 
		//ArrayList<Floor> temp = link.
		if(link.newFloor.isEmpty()) {
				this.motor = 0;
		}
		else {
			for(int i=0;i<link.newFloor.size();i++) {  //for now all request in newFloor are added to service
				this.serviceQueue.add(link.newFloor.get(i).getFloor());
			}
			
			if(this.serviceQueue.get(0)>this.currentFloor) {
				Collections.sort(serviceQueue); //sorts list from smallest to largest
			//	this.motor = 1;
			}
			else {
				Collections.sort(serviceQueue);
				Collections.reverse(serviceQueue); //sorts list from largest to smallest
			//	this.motor = 2;
				}
		}
	}
	
	public void service() { //moves the elevator through queue to service requests 
		
		while(!this.serviceQueue.isEmpty()) {
			System.out.println("servicing floor request");
			if(this.currentFloor == this.serviceQueue.get(0)) {
				this.motor = 0;
				this.door = true;
				//this.displayButtons();
				//Thread.sleep(1000);
				this.door = false;
				this.serviceQueue.remove(0);
			}
			else if(this.serviceQueue.get(0)>this.currentFloor){
				this.currentFloor++;
			}
			else {this.currentFloor--;}
		}
		this.motor = 0;
		System.out.println("Arrived at destination");
		this.sendRequest(this.currentFloor, this.motor);
	}
	
	/*public void displayButtons() throws IOException { //will display buttons for gui, but act as stud for new passengers boarding
		//display button as gui
		Random rand = new Random();
		int newF;
		int dir;
		if(this.serviceQueue.get(0)>this.currentFloor) {
		newF = rand.nextInt(this.topFloor-this.currentFloor+1)+this.currentFloor; //generate random int between current and top floor;
		dir = 1; //up
		}
		else {
		newF = rand.nextInt(currentFloor);
		dir = 2; //down 
		}
		this.sendRequest(this.currentFloor, this.motor);
	}*/
	
	public void sendRequest(int currFloor,int direction) { //send new internal requests to the scheduler data-> ID,direction,floor,floor
		
		byte data[] = new byte[4];
		data[0] = (byte) direction;
		data[1] = (byte) currFloor;

		
		try {
	         
			
			DatagramPacket sendPacket = new DatagramPacket(data, data.length, SEND_PORT_NUMBER);
			sendSocket.send(sendPacket);
	      } catch (SocketException se) {   // Can't create the socket.
	    	  sendSocket.close();
	         se.printStackTrace();
	         System.exit(1);
	      } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)  {
		System.out.println("elev starting");
		Receiver link = new Receiver(69);
		link.start();
		Elevator elevator = new Elevator(6,2);
		try {
		elevator.sendSocket = new DatagramSocket();
		}catch(Exception e) {
			System.out.println(e);
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(true) {
			elevator.organizeQueue();
			elevator.service();
		}

	}
		
	
}
