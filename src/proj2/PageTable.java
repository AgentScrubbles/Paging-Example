package proj2;


/**
 * Page table that implements the Least Recently Used algorithm
 * @author Robert
 *
 */
public class PageTable {
	
	private long[] counters;
	private int[] processOwnership; //Will determine if this is the correct process accessing
	private Frame[] pages;
	
	
	public PageTable(int size){
		pages = new Frame[size];
		counters = new long[size];
		processOwnership = new int[size];
		for(int i = 0; i < size; i++){
			pages[i] = null;
			counters[i] = 0;
			processOwnership[i] = -1;
		}
	}
	
	public Frame getFrame(int index, int processID) throws PageFault{
		if(processOwnership[index] != processID){
			throw new PageFault(processID, index);
		}
		counters[index] = System.currentTimeMillis();
		return pages[index]; 
	}
	
	public int putFrame(Frame frame, int processID){
		//Find the oldest counter and replace it
		long currentMin = Long.MAX_VALUE;
		int oldestIndex = -1;
		for(int i = 0; i < counters.length; i++){
			if(counters[i] <= currentMin){
				currentMin = counters[i];
				oldestIndex = i;
			}
		}
		
		//Output the information
		if(pages[oldestIndex] == null){
			System.out.println("[Process " + processID + "] finds a free frame in main memory (frame number = " + oldestIndex + ").");
		} else if(oldestIndex == -1){
			System.out.println("No more memory!");
		} else {
			System.out.println("[Process " + processID + "] replaces a frame (frame number = " + oldestIndex + ") from the main memory");
		}
		
		Frame oldFrame = pages[oldestIndex];
		
		pages[oldestIndex] = frame;
		counters[oldestIndex] = System.currentTimeMillis();
		processOwnership[oldestIndex] = processID;
		
		//Simulate swap time
		try {
			System.out.println("[Process " + processID + "] issues an I/O operation to swap in demanded page (page number = " + oldestIndex + ").");
			Thread.sleep(100);
			System.out.println("[Process " + processID + "] demanded page (page number = " + oldestIndex + " ) has been swapped in main memory (frame number = " + oldFrame + ").");

		} catch (InterruptedException e) {
			System.out.println("Error sleeping.");
			System.out.println(e.getMessage());
		}
		
		return oldestIndex;
	}
	
	public int size(){
		return pages.length;
	}
}
