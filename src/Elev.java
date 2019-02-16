import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Elev {

	private int elevatorNumber;
	private int motor; //0==stop 1==up 2==down
	private int topFloor;
	private boolean door; //false=closed true=open
	private int[] buttons;
	private int currentFloor; //2 is lobby
	private ArrayList<Integer> serviceQueue; //floors that will be serviced in organized order
	
	private int SEND_PORT_NUMBER = 219; //schedualer port
	private DatagramSocket sendSocket;
	private int myPort;
	
	private boolean passenger;
	
	public Elev(int elevNum, int floors,int port) {
		
		buttons = new int[floors];
		for(int i=1;i<=floors;i++) {
			buttons[i-1]=i;
		}
		elevatorNumber = elevNum;
		door = false;
		topFloor = floors;
		motor = 0;
		currentFloor = 2;
		serviceQueue = new ArrayList<Integer>();
		myPort = port;
		passenger = false;
		try {
			sendSocket = new DatagramSocket(myPort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.sendRequest(currentFloor, motor);
		
	}
	
	public void addRequest(int request) {
		this.serviceQueue.add(request);
		if(this.serviceQueue.get(0)>this.currentFloor) {
			Collections.sort(serviceQueue); //sorts list from smallest to largest
		
		}
		else {
			Collections.sort(serviceQueue);
			Collections.reverse(serviceQueue); //sorts list from largest to smallest
		
			}
		this.service();
	}
	
	
	
	
public void service() { //moves the elevator through queue to service requests 
		
		while(!this.serviceQueue.isEmpty()) {
			System.out.println("Elevator "+this.elevatorNumber+" servicing floor request");
			if(this.currentFloor == this.serviceQueue.get(0)) {
				this.motor = 0;
				this.door = true;
				this.door = false;
				this.serviceQueue.remove(0);
				//this.displayButtons();
			}
			else if(this.serviceQueue.get(0)>this.currentFloor){
				System.out.println("going up, current floor: " + currentFloor);
				this.currentFloor++;
				motor = 1;
			}
			else {
				System.out.println("going down");
				this.currentFloor--;
				motor = 2;
			}
		}
		this.motor = 0;
		System.out.println("Arrived at destination");
		this.sendRequest(this.currentFloor, this.motor);
	}

/*public void displayButtons() { //will display buttons for gui, but act as stud for new passengers boarding
	//display button as gui
	
	if(this.passenger == false) {            //if there wasnt a passenger, a new one boarded 
		Random rand = new Random();
		int next = rand.nextInt(topFloor) + 1;
		this.addRequest(next);
		passenger = true;
		if(next > currentFloor) {
			this.sendRequest(currentFloor, 1); //going up
		}
		else {
			this.sendRequest(currentFloor, 2); //going down 
		}
	}
	else {
		passenger = false;      //if there was a passenger, then they got off
	}
}
*/
	
public void sendRequest(int currFloor,int direction) { //send new internal requests to the scheduler data-> ID,direction,floor,floor
	
	byte data[] = new byte[4];
	data[0] = (byte) elevatorNumber;
	data[1] = (byte) direction;
	if(currFloor >= 10) {
		data[2] = (byte)1;
		data[3] = (byte)(currFloor - 10);
	}
	else {
		data[2] = (byte) 0;
		data[3] = (byte) currFloor;
	}
	
	System.out.print("Sending packet containing: "+ data.toString());	
	try {
		DatagramPacket sendPacket = new DatagramPacket(data, data.length,InetAddress.getLocalHost(), SEND_PORT_NUMBER);
		sendSocket.send(sendPacket);
      } catch (SocketException se) {   // Can't create the socket.
    	  sendSocket.close();
         se.printStackTrace();
         System.exit(1);
      } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	System.out.println("Packet sent to Scheduler");
}

	
	
	
	
	
}
