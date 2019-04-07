import java.io.BufferedWriter;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
@SuppressWarnings("unused")
public class MeasurementOutput extends Thread{
	private final int ELEVPERIOD = 100, FLOORPERIOD = 200;
	private String filename = ".//MeasurementOutput.txt";
	public Scheduler scheduler;
	private BufferedWriter writer;
	private ArrayList<Long> elevExe = new ArrayList<Long>();
	private ArrayList<Long> floorExe = new ArrayList<Long>();
	int firstP, secondP, thirdP, fourthP; //used to track which elevator process has the highest priority level using Rate Monotonic Analysis
	public MeasurementOutput(Scheduler s) throws IOException{
		scheduler = s;
		writer = new BufferedWriter(new FileWriter(filename));
		writer.write("");
	}
	
	public void run() {
		//60 second period for analysis
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e1) {
			
			e1.printStackTrace();
		}
		//protected access to shared memory
		this.elevExe.addAll(scheduler.elevTime);
		this.floorExe.addAll(scheduler.floorTime);
		
		
		try {
			if(!elevExe.isEmpty() && !floorExe.isEmpty()) {
				print();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void print() throws IOException{
		long elevSum = 0, floorSum = 0;
		//will now print the results of Total, mean and median to the txt file
		
		writer.write("\n***Elevator Process Time***\n");
		for(int i = 0; i < elevExe.size(); i++) {
			elevSum += elevExe.get(i);
			writer.write("Elevator Process " + i + ": " + elevExe.get(i) + "\n");
			
		}
		Collections.sort(elevExe);
		writer.write("\n Elevator Proces Period: " + ELEVPERIOD);
		writer.write("Elevator Process Total Time: " + elevSum + "\n");
		writer.write("Elevator Process Median: " + elevExe.get(elevExe.size()/2));
		long elevMean = elevSum/elevExe.size();
		writer.write("\nElevator Process Mean: " + elevMean + "\n");
		elevSum = 0;
		
		for(int i = 0; i < floorExe.size(); i++) {
			floorSum += floorExe.get(i);
			writer.write("Process " + i + ": " + floorExe.get(i) + "\n");
			
		}
		writer.write("***Floor Process Process Time***\n");
		Collections.sort(floorExe);
		writer.write("Floor Process Total Time: " + floorSum + "\n");
		writer.write("Floor Process Median: " + floorExe.get(floorExe.size()/2));
		long floorMean = floorSum/floorExe.size();
		writer.write("\nFloor Process Mean: " + floorMean + "\n");
		floorSum = 0;
		
		//Mean, median, min and max have been sorted and found
		//Using the given Periods from Iteration 4 does the system meet its deadline
		
		writer.write("\n\n\n***RATE MONOTONIC ANALYSIS***\n\n\nElevator Process: Period = 100\n");
		//Elev analysis (mean to period and max to period)
		if(elevMean <= ELEVPERIOD) {
			writer.write("Elevator meets its deadline with execution mean of " + elevMean +"\n");
		}
		else {
			writer.write("SCHEDULING ERROR: ELEVATOR PROCESS EXCEEDING PERIOD\n");
		}
		if(Collections.max(elevExe) < ELEVPERIOD) {
			writer.write("Max edge case is within max period system works as expected\n");
		}
		else {
			writer.write("WARNING: Elevator Process has events exceeding period more analysis needed\n");
		}
		
		//floor process analysis
		writer.write("Floor Process: Period = 200\n");
		if(floorMean <= FLOORPERIOD) {
			writer.write("Floor Process meets its deadline with execution mean of " + floorMean + "\n");
		}
		else {
			writer.write("SCHEDULING ERROR: FLOOR PROCESS EXCEEDING PERIOD\n");
		}
		if(Collections.max(floorExe) <= FLOORPERIOD) {
			writer.write("Max floor process time is within allowed period\n");
		}
		else {
			writer.write("WARNING: FLOOR PROCESS MAY EXCEED ALLOWED PERIOD\n");
		}
		//done this periods analysis: Clear arrays for next period 
		elevExe.clear();
		floorExe.clear();
		writer.close();	 //lk wont use it again
		
			}
}
