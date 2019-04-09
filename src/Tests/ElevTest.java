package Tests;

import static org.junit.jupiter.api.Assertions.*;
import Systems.Elev;

import org.junit.jupiter.api.Test;

class ElevTest {
	public Elev e;

	@Test
	void test() {
		e = new Elev(1, 22, 70, null);
		testAdd();
		
	}
	
	void testAdd() {
		//test when motor is up
		e.setMotor(1);
		e.addRequest(10, 0);
		int result = e.serviceQueue.get(0);
		assertEquals(result, 10);
		e.addRequest(5, 0);
		result = e.serviceQueue.get(0);
		assertEquals(result, 5);
		result = e.serviceQueue.get(1);
		assertEquals(result, 10);
		
		e.serviceQueue.clear();
		
		//test for down 
		e.setMotor(2);
		e.addRequest(5, 0);
		result = e.serviceQueue.get(0);
		assertEquals(result, 5);
		e.addRequest(10, 0);
		result = e.serviceQueue.get(0);
		assertEquals(result, 10);
		result = e.serviceQueue.get(1);
		assertEquals(result, 5);
		
	}

}
