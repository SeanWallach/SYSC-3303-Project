package Tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import Systems.Elev;
import Systems.Elevator;
import Systems.Floor;
import Systems.Scheduler;

class Connectiontest {


	
	@Test
	void test() {
		floorToScheduler();
		ElevToSched();

		
	}
	
	void floorToScheduler() {
		Scheduler s = new Scheduler();
		Floor f = new Floor(22, 238);
		byte[] test = {1, 1, 2, 0, 0};
		f.sendInstructions(12, 1);
		s.receiveAndSend();	
		byte[] rTest = s.receivePacket.getData();
		for(int i=0; i<5; i++) {
			assertTrue(rTest[i]==test[i]);
			System.out.println(rTest[i] + " = " + test[i]);
		}		
		s.close();
		f.close();
		s=null;
		f=null;
	}
	
	void ElevToSched(){
		Elevator E = new Elevator();
		Scheduler s= new Scheduler();
		Elev e = new Elev(1, 22, 69, E);
		s.receiveAndSend();
		byte[] test = {1, 0, 0, 1};
		byte[] rTest = s.receivePacket.getData();
		for(int i=0; i<4; i++) {
			assertTrue(rTest[i]==test[i]);
			System.out.println(rTest[i] + " = " + test[i]);
		}	
		e.close();
		E.close();
		s.close();
		s=null;
		e=null;
		E=null;
	}
	

}
