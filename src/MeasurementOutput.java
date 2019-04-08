import java.io.BufferedWriter;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;

public class MeasurementOutput extends Thread {

	private String filename = ".//MeasurementOutput.txt";
	public Scheduler scheduler;
	private BufferedWriter writer;
	private ArrayList<Long> elev1 = new ArrayList<Long>();
	private ArrayList<Long> elev2 = new ArrayList<Long>();
	private ArrayList<Long> elev3 = new ArrayList<Long>();
	private ArrayList<Long> elev4 = new ArrayList<Long>();
	private ArrayList<Long> floorButtons = new ArrayList<Long>();
	int firstP, secondP, thirdP, fourthP; // used to track which elevator process has the highest priority level using
											// Rate Monotonic Analysis

	public MeasurementOutput(Scheduler s) throws IOException {
		scheduler = s;
		writer = new BufferedWriter(new FileWriter(filename));
		writer.write("");
	}

	public void run() {
		// 60 second period for analysis
		try {
			sleep(60000);
		} catch (InterruptedException e1) {

			e1.printStackTrace();
		}
		// protected access to shared memory
		this.elev1.addAll(scheduler.getList(1));
		this.elev2.addAll(scheduler.getList(2));
		this.elev3.addAll(scheduler.getList(3));
		this.elev4.addAll(scheduler.getList(4));
		this.floorButtons.addAll(scheduler.getList(5));

		try {
			print();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void print() throws IOException {
		//System.out.println("\n\n\n___________________PRINTING_____________________\n\n\n");
		long sum1 = 0, sum2 = 0, sum3 = 0, sum4 = 0, sumFloor=0;
		// will now print the results of Total, mean and median to the txt file
		// Will go by elevator in order
		
		//elev 1 process
		writer.write("***Elevator 1 Process Time***\n");
		for (int i = 0; i < elev1.size(); i++) {
			sum1 += elev1.get(i);
			writer.write("Requst " + i + ": " + elev1.get(i) + "\n");

		}
		Collections.sort(elev1);
		writer.write("Elevator 1 Task Total Time: " + sum1 + "\n");
		writer.write("Elevator 1 Task Median: " + elev1.get(elev1.size() / 2));
		long mean1 = sum1 / elev1.size();
		writer.write("\nElevator 1 Task Mean: " + mean1 + "\n");


		//elev 2 process
		writer.write("***\n\nElevator 2 Process Time***\n");
		for (int i = 0; i < elev2.size(); i++) {
			sum2 += elev2.get(i);
			writer.write("Request " + i + ": " + elev2.get(i) + "\n");

		}
		Collections.sort(elev2);
		writer.write("Elevator 2 Task Total Time: " + sum2 + "\n");
		writer.write("Elevator 2 Task Median: " + elev2.get(elev2.size() / 2));
		long mean2 = sum2 / elev2.size();
		writer.write("\nElevator 2 Task Mean: " + mean2 + "\n");


		//elev 3 process
		writer.write("***\n\nElevator 3 Process Time***\n");

		for (int i = 0; i < elev3.size(); i++) {
			sum3 += elev3.get(i);
			writer.write("Request " + i + ": " + elev3.get(i) + "\n");

		}
		Collections.sort(elev3);
		writer.write("Elevator 3 Task Total Time: " + sum3 + "\n");
		writer.write("Elevator 3 Task Median: " + elev3.get(elev3.size() / 2));
		long mean3 = sum1 / elev3.size();
		writer.write("\nElevator 3 Task Mean: " + mean3 + "\n");

		
		//Elev 4 process
		writer.write("**\n\n*Elevator 4 Process Time***\n");

		for (int i = 0; i < elev4.size(); i++) {
			sum4 += elev4.get(i);
			writer.write("Request " + i + ": " + elev4.get(i) + "\n");

		}
		Collections.sort(elev4);
		writer.write("Elevator 4 Task Total Time: " + sum4 + "\n");
		writer.write("Elevator 4 Task Median: " + elev4.get(elev4.size() / 2));
		long mean4 = sum4 / elev4.size();
		writer.write("\nElevator 4 Task Mean: " + mean4 + "\n");


		//PRINTING FLOOR TIME
		writer.write("***\n\nFloor Button Process Time***\n");

		for (int i = 0; i < floorButtons.size(); i++) {
			sumFloor += floorButtons.get(i);
			writer.write("Request " + i + ": " + floorButtons.get(i) + "\n");

		}
		Collections.sort(floorButtons);
		writer.write("Floor Button task Total Time: " + sumFloor + "\n");
		writer.write("Floor Button Median: " + floorButtons.get(floorButtons.size() / 2));
		long mean5 = sumFloor / floorButtons.size();
		writer.write("\nFloor Button Mean: " + mean5 + "\n");

		
		writer.write("\n\nTotal Elevator Time: " + (sum1+sum2+sum3+sum4));		// Mean, median, total have been established and stored in the text file.
		// Now use these to analyse the system using Rate Monotonic Analysis
		// Assuming the Period T of each process will be the mean of each.

		/*
		ArrayList<Long> temp = new ArrayList<Long>(4);
		temp.add(mean1);
		temp.add(mean2);
		temp.add(mean3);
		temp.add(mean4);

		temp.sort(Collections.reverseOrder());
		// Means are now sorted by reverse order.
		long tlong = 0;
		ArrayList<Long> outTemp = new ArrayList<Long>(4);
		tlong = temp.get(0); // get the largest mean (IE lowest priority)

		if (tlong != 0) {
			if (tlong == mean1) {
				firstP = 1;
				outTemp.add(mean1);

			} else if (tlong == mean2) {
				firstP = 2;
				outTemp.add(mean2);
			} else if (tlong == mean3) {
				firstP = 3;
				outTemp.add(mean3);
			} else if (tlong == mean4) {
				firstP = 4;
				outTemp.add(mean4);
			}
		}
		// Second Priority

		tlong = temp.get(1);

		if (tlong != 0) {
			if (tlong == mean1) {
				secondP = 1;
				outTemp.add(mean1);

			} else if (tlong == mean2) {
				secondP = 2;
				outTemp.add(mean2);
			} else if (tlong == mean3) {
				secondP = 3;
				outTemp.add(mean3);
			} else if (tlong == mean4) {
				secondP = 4;
				outTemp.add(mean4);
			}
		}
		// Third Period

		tlong = temp.get(2);

		if (tlong != 0) {
			if (tlong == mean1) {
				thirdP = 1;
				outTemp.add(mean1);

			} else if (tlong == mean2) {
				thirdP = 2;
				outTemp.add(mean2);
			} else if (tlong == mean3) {
				thirdP = 3;
				outTemp.add(mean3);
			} else if (tlong == mean4) {
				thirdP = 4;
				outTemp.add(mean4);
			}
		}

		tlong = temp.get(3);

		if (tlong != 0) {
			if (tlong == mean1) {
				fourthP = 1;
				outTemp.add(mean1);

			} else if (tlong == mean2) {
				fourthP = 2;
				outTemp.add(mean2);
			} else if (tlong == mean3) {
				fourthP = 3;
				outTemp.add(mean3);
			} else if (tlong == mean4) {
				fourthP = 4;
				outTemp.add(mean4);
			}
		}

		// All priorities have been assigned. Each process priority should have a unique
		// process.
		// Implementation allows for duplicates in the case of two P having the same
		// mean (period) but
		// should be negligent in the system being worked on at the moment.

		writer.write("\n****RATE MONOTONIC ANALYSIS OF SYSTEM****\n");
		// note outTemp array holds the mean from highest to lowest meaning lowest to
		// highest priority
		writer.write("*NOTE* Priorities are in descending order (1 is lowest priority)\n");
		writer.write("Lowest Priority is Process " + firstP + " with Period of " + outTemp.get(0) / 1000 + "us\n");
		writer.write("Second Priority is Process " + secondP + " with Period of " + outTemp.get(1) / 1000 + "us\n");
		writer.write("Third Priority is Process " + thirdP + " with Period of " + outTemp.get(2) / 1000 + "us\n");
		// writer.write("Highest Priority is Process " + fourthP + " with Period of " +
		// outTemp.get(3)/1000 + "us");
		System.out.println("\n\n\n____________DONE___________\n\n\n");
		*/
		sum1=0; sum2=0; sum3=0; sum4=0; sumFloor=0;
		writer.close();
		// system has ran for 60s. Analyze collected data
	}
}
