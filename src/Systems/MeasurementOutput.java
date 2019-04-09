package Systems;

import java.io.BufferedWriter;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

@SuppressWarnings("unused")
public class MeasurementOutput extends Thread {
	private final int ELEVPERIOD = 50*1000, FLOORPERIOD = 200*000;
	private String filename = ".//MeasurementOutput.txt";
	public Scheduler scheduler;
	private BufferedWriter writer;
	private ArrayList<Long> elevExe = new ArrayList<Long>();
	private ArrayList<Long> floorExe = new ArrayList<Long>();
	int firstP, secondP, thirdP, fourthP; // used to track which elevator Request has the highest priority level using
											// Rate Monotonic Analysis

	public MeasurementOutput(Scheduler s) throws IOException {
		scheduler = s;
		writer = new BufferedWriter(new FileWriter(filename));
		writer.write("");
	}

	public void run() {
		// 60 second period for analysis
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e1) {

			e1.printStackTrace();
		}
		// protected access to shared memory
		this.elevExe.addAll(scheduler.elevTime);
		this.floorExe.addAll(scheduler.floorButtons);

		try {
			if (!elevExe.isEmpty() && !floorExe.isEmpty()) {
				print();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void print() throws IOException {
		long elevSum = 0, floorSum = 0;
		// will now print the results of Total, mean and median to the txt file

		writer.write("***Elevator Request Time***\n");
		for (int i = 0; i < elevExe.size(); i++) {
			elevSum += elevExe.get(i);
			writer.write("Elevator Request " + i + ": " + elevExe.get(i) + "\n");

		}
		Collections.sort(elevExe);
		writer.write("\nElevator Request Total Time: " + elevSum + "\n");
		writer.write("Elevator Request Median: " + elevExe.get(elevExe.size() / 2));
		long elevMean = elevSum / elevExe.size();
		writer.write("\nElevator Request Mean: " + elevMean + "\n");
		elevSum = 0;


		writer.write("\n\n***Floor Request Request Time***\n");
		for (int i = 0; i < floorExe.size(); i++) {
			floorSum += floorExe.get(i);
			writer.write("Request " + i + ": " + floorExe.get(i) + "\n");

		}
		Collections.sort(floorExe);
		writer.write("Floor Request Total Time: " + floorSum + "\n");
		writer.write("Floor Request Median: " + floorExe.get(floorExe.size() / 2));
		long floorMean = floorSum / floorExe.size();
		writer.write("\nFloor Request Mean: " + floorMean + "\n");
		floorSum = 0;

		// Mean, median, min and max have been sorted and found
		// Using the given Periods from Iteration 4 does the system meet its deadline
		/*
		writer.write("\n\n\n***RATE MONOTONIC ANALYSIS***\n\n\nElevator Request: Period = 100\n");
		// Elev analysis (mean to period and max to period)
		if (elevMean <= ELEVPERIOD) {
			writer.write("Elevator meets its deadline with execution mean of " + elevMean + "\n");
		} else {
			writer.write("SCHEDULING ERROR: ELEVATOR Request EXCEEDING PERIOD\n");
		}
		if (Collections.max(elevExe) < ELEVPERIOD) {
			writer.write("Max edge case is within max period system works as expected\n");
		} else {
			writer.write("WARNING: Elevator Request has events exceeding period more analysis needed\n");
		}

		// floor Request analysis
		writer.write("Floor Request: Period = 200\n");
		if (floorMean <= FLOORPERIOD) {
			writer.write("Floor Request meets its deadline with execution mean of " + floorMean + "\n");
		} else {
			writer.write("SCHEDULING ERROR: FLOOR Request EXCEEDING PERIOD\n");
		}
		if (Collections.max(floorExe) <= FLOORPERIOD) {
			writer.write("Max floor Request time is within allowed period\n");
		} else {
			writer.write("WARNING: FLOOR Request MAY EXCEED ALLOWED PERIOD\n");
		}
		// done this periods analysis: Clear arrays for next period*/
		elevExe.clear();
		floorExe.clear();
		writer.close(); // lk wont use it again

	}
}