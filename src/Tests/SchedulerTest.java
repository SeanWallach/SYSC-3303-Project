package Tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import Systems.Scheduler;

class SchedulerTest {
	public Scheduler s; 
	
	@Test
	void test() {
		s=new Scheduler();
		s.measure.stop();//halt the measure thread for testing
		testBestElevator();
		testSendElevator();

	}
	
	void testBestElevator() {
		
		assertEquals(s.getBestElevator(15, 2), 1);
		s.elevatorState1=1;
		assertEquals(s.getBestElevator(16,  1), 2);
		
		
	}
	
	void testSendElevator(){
		
		byte[] msg = {2, 1, 1, 6}; //elev 2, direction 1, floor 16;
		s.sendElevator(2, 16, msg);
		byte[] result = s.sendPacket.getData(); 
		for(int i=0; i<4; i++) {
			assertEquals(result[1], msg[1]);
		}
		
		msg[0]=3; msg[1]=2; msg[2]=0; msg[3]=4;
		s.sendElevator(3, 4, msg);
		for(int i=0; i<4; i++) {
			assertEquals(result[1], msg[1]);
		}
	}
	
	

}
