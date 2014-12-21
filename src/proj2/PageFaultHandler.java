package proj2;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class PageFaultHandler implements Runnable, IStoppable{

	private PhysicalMemory _mainMemory;
	private ConcurrentLinkedQueue<Integer> requestQueue;
	private ConcurrentHashMap<Integer, Integer> completedRequests;
	
	private Semaphore request;
	private Semaphore complete;
	private Boolean _stop;
	
	
	public PageFaultHandler(PhysicalMemory mainMemory){
		_mainMemory = mainMemory;
		requestQueue =  new ConcurrentLinkedQueue<Integer>();
		completedRequests = new ConcurrentHashMap<Integer, Integer>();
		request = new Semaphore(1);
		complete = new Semaphore(1);
		_stop = false;
		try {
			complete.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		while(!shouldStop()){
			try {
				request.acquire();
				Integer processID = requestQueue.poll();
				if(processID == null){
					
					continue;
				}
				int openFrame = findFreeFrame(processID);
				completedRequests.put(processID, openFrame);
				complete.release();
			} catch (InterruptedException e) {
				complete.release();
			}
			
		}
		System.out.println("[Page Fault Handler] has stopped.");
	}
	
	private int findFreeFrame(int processID){
		int freeFrame = _mainMemory.requestFreeFrame();
		synchronized(_mainMemory){
			_mainMemory.setFrame(freeFrame, new Frame(_mainMemory.frameSize(), processID)); //sets the process ID associated with it
		}
		return freeFrame;
	}
	
	public int requestFreeFrame(int processID){
		requestQueue.add(processID);
		request.release();
		try {
			complete.acquire();
			int openFrame = completedRequests.get(processID);
			completedRequests.remove(processID);
			return openFrame;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public void stop(){
		synchronized(_stop){
			_stop = true;
		}
	}
	
	private boolean shouldStop(){
		boolean val = false;
		synchronized(_stop){
			val = _stop;
		}
		request.release();
		return val;
	}

}
