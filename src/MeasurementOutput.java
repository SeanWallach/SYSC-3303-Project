import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MeasurementOutput extends Thread{
	
	private String filename = ".//MeasurementOutput.txt";
	public Scheduler scheduler;	
	private BufferedWriter writer;
	private ArrayList<Long> arrivalT = new ArrayList<Long>();
	private ArrayList<Long> floorT = new ArrayList<Long>();
	
	public MeasurementOutput(Scheduler scheduler) throws IOException{
		this.scheduler = scheduler;
		writer = new BufferedWriter(new FileWriter(filename));
		writer.write("");//clear output file
	}
	
	
	
	public void run(){
			try {
				sleep(60000);// wait 60s to get sample size
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			arrivalT.addAll(scheduler.arrivalTimes);		
			floorT.addAll(scheduler.floorBTimes);
			//System.out.println("\n*\n*\n****PRINTING TO FILE*** \n*\n*\n");
			try {
				print();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
	}
	
	private void print()throws IOException {
		long sum=0;
		writer.write("***Arrival Times***\n");
		for(int i = 0; i<arrivalT.size(); i++) {
			sum+=arrivalT.get(i);
			writer.write("TIME:  "+ arrivalT.get(i) + "");
			if(i<3) writer.write("   Time from start up");// mention times for inital packet from elevators on start up
			writer.write("\n");
		}
		//get median and mean
		Collections.sort(arrivalT);
		writer.write("Median: "+ arrivalT.get(arrivalT.size()/2));
		writer.write("\nMean: "+ sum/arrivalT.size());
		
		sum=0;
		writer.write("\n\n***Floor Button Times***\n");
		for(Long time: floorT) {
			sum+=time;
			writer.write("TIME:  "+ time + "\n");
		}
		//get median and mean
		Collections.sort(floorT);
		writer.write("Median: "+ floorT.get(floorT.size()/2));
		writer.write("\nMean: "+ sum/floorT.size());
		writer.close();
		
	}

}
