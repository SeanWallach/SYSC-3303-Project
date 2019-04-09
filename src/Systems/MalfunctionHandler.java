package Systems;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MalfunctionHandler extends Thread {
	public ArrayList<Integer> Requests = new ArrayList<Integer>();
	public Scheduler s;
	public DatagramPacket sendPacket;
	public DatagramSocket sentSocket;
	


	public MalfunctionHandler(Scheduler sched, ArrayList<Integer> list) {
		//get scheduler and list of requests from elevator that broke down
		s = sched;
		Requests.addAll(list);
	}

	public void run() {
		//System.out.println("\n\n\n\n\nFIXING\n\n\n\n");
		byte[] msg = new byte[3];
		
		//for each request in the queue decode it into bytes
		for (int i = 0; i < Requests.size(); i++) {
			msg[0] = 5;
			if (Requests.get(i) < 10) {
				msg[1] = 0;
				int num = Requests.get(i);
				msg[2] = (byte) num;
			} else if (Requests.get(i) >= 10 && Requests.get(i) < 20) {
				msg[1] = 1;
				int num = Requests.get(i);
				msg[2] = (byte) (num - 10);
			} else {
				msg[1] = 2;
				int num = Requests.get(i);
				msg[2] = (byte) (num - 20);
			}
			
			
			//send the message 
			try {
				sentSocket = new DatagramSocket();
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				sendPacket = new DatagramPacket(msg, msg.length, InetAddress.getLocalHost(), 238);
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				sentSocket.send(sendPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
