import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MeasurementOutput extends Thread{
	
	private String filename = ".//MeasurementOutput.txt";
	public Scheduler scheduler;	
	private BufferedWriter writer;
	private ArrayList<Long> arrivalT = new ArrayList<Long>();
	private ArrayList<Long> floorT = new ArrayList<Long>();
	
	public MeasurementOutput(Scheduler scheduler) throws IOException{
		this.scheduler = scheduler;
		writer = new BufferedWriter(new FileWriter(filename));
		writer.write("");
	}
	
	
	
	public void run(){
			try {
				sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			arrivalT.addAll(scheduler.arrivalTimes);		
			floorT.addAll(scheduler.floorBTimes);
			System.out.println("\n*\n*\n****PRINTING TO FILE*** \n*\n*\n");
			try {
				print();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
	}
	
	private void print()throws IOException {
		writer.write("***Arrival Times***\n");
		for(int i = 0; i<arrivalT.size(); i++) {
			writer.write("TIME:  "+ arrivalT.get(i) + "");
			if(i<3) writer.write("   Time from start up");
			writer.write("\n");
		}
		writer.write("\n\n***Floor Button Times***\n");
		for(Long time: floorT) {
			writer.write("TIME:  "+ time + "\n");
		}
		writer.close();
		
	}

}
