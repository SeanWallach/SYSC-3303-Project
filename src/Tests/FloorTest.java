package Tests;
import Systems.Floor;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import Systems.Floor;

class FloorTest {
	Floor f = new Floor(22, 238);

	@Test
	void test() {
		
		testInitial();
		testSend();
		f.close();
		
	}
	
	void testInitial() {
		int portNum =f.sendReceiveSocket.getLocalPort();
		assertEquals(portNum, 238);
		assertEquals(f.numOfFloors, 22);
	}
	
	void testSend() {
		f.sendInstructions(14, 1);
		byte[] expected = {1, 1, 4};
		byte[] msg = f.sendPacket.getData();
		for(int i = 0; i<3; i++) {
			assertEquals(msg[i], expected[i]);
		}
		
		f.sendInstructions(9, 2);
		expected[0]=2; expected[1]=0; expected[2]=9;
		msg = f.sendPacket.getData();
		for(int i = 0; i<3; i++) {
			assertEquals(msg[i], expected[i]);
		}
	}
	//verify and receiveMessage are tested with the connection with scheduler in Connectiontest.java
	
	

}
