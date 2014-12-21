package proj2;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class MemoryManager implements Runnable, IStoppable {

	private PhysicalMemory mainMemory;
	private PageFaultHandler pageFaultHandler;
	private int pageSize;
	private Semaphore request;
	private Semaphore completed;
	private Boolean _stop;

	private PageTable pageTable;
	private TranslationTable translationTable;

	private ConcurrentLinkedQueue<Integer> currentRequests;
	private ConcurrentHashMap<Integer, AddressRequest> currentRequestLookup;
	
	private ConcurrentHashMap<Integer, Integer> completedRequests; //All completed ones will go here
	private ConcurrentLinkedQueue<Integer> finishedThreads; //Threads that are done and freeing their memory
	
	
	public MemoryManager(int pageCount, PhysicalMemory memory,
			PageFaultHandler pgh) {
		pageTable = new PageTable(pageCount);
		translationTable = new TranslationTable();
		mainMemory = memory;
		currentRequests = new ConcurrentLinkedQueue<Integer>();
		currentRequestLookup = new ConcurrentHashMap<Integer, AddressRequest>();
		completedRequests = new ConcurrentHashMap<Integer, Integer>();
		finishedThreads = new ConcurrentLinkedQueue<Integer>();
		pageFaultHandler = pgh;
		pageSize = memory.frameSize();
		request = new Semaphore(1);
		completed = new Semaphore(1);
		_stop = false;
		synchronized(_stop){
			_stop = false;
		}
	}

	
	/**
	 * Synchronous methods
	 * 
	 * @throws PageFault
	 **/
	private int request(int processID, int logicalAddress) {
		// 1. Translate logical Address
		int virtualPageNumber = logicalAddress / pageSize;
		int virtualOffset = logicalAddress % pageSize;

		// 2. Lookup virtualPageNumber in the translation table
		int pageAddress = translationTable.getActual(processID,
				virtualPageNumber);
		// If pageAddress = -1, then the address was not found. Call the
		// pageFaultThread to add a new one
		if (pageAddress == -1) {
			int freeFrameID = pageFaultHandler.requestFreeFrame(processID);
			synchronized (mainMemory) {
				pageAddress = pageTable.putFrame(mainMemory
						.getFrame(freeFrameID),
						processID);
			}
			translationTable.put(processID, virtualPageNumber, pageAddress);
		}
		// 3. Retrieve the frame from the pageTable
		// Throws pagefault if not found
		try {
			pageTable.getFrame(pageAddress, processID);
		} catch (PageFault e) {
			// 3.5 Notify pagefault thread to swap in new one.

		}

		// 4. If successful, output the sucesessful read.
		System.out.println("[Process " + processID + "] accesses address "
				+ logicalAddress + " (page number = " + virtualPageNumber
				+ ", page offset=" + virtualOffset
				+ ") in main memory (frame number = " + pageAddress + ").");

		return 0;
	}

	public synchronized Integer requestAddress(int processID, int logicalAddress) {
		try {
			currentRequests.add(processID);
			currentRequestLookup.put(processID, new AddressRequest(processID,
					logicalAddress));
			request.release();
			completed.acquire();
			Integer requested = completedRequests.get(processID);
			completedRequests.remove(processID);
			return requested;

		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}

	}
	
	private void freeProcessMem(int processID){
		synchronized(mainMemory){
			
		}
	}

	/**
	 * Enqueue all processes that have ended
	 * @param processID
	 */
	public synchronized void freeThread(int processID){
		finishedThreads.add(processID);
	}
	
	@Override
	public void run() {
		try {
			while (!shouldStop()) {
				request.acquire();
				Integer requestingID = currentRequests.poll(); // Get the top
																// process that
																// is
																// requesting.
				if (requestingID == null) {
					completed.release();
					continue;
				}
				AddressRequest req = currentRequestLookup.get(requestingID);
				if (req == null) {
					completed.release();
					continue;
				}
				int index = request(req.processID, req.logicalAddress);
				completedRequests.put(req.processID, index);
				completed.release();
				/** Free old process memory **/
				for(int pID : finishedThreads){
					freeProcessMem(pID);
					finishedThreads.remove(pID);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("[Memory Manager] has stopped.");
	}

	private class AddressRequest {
		public AddressRequest(int processID, int logicalAddress) {
			this.processID = processID;
			this.logicalAddress = logicalAddress;
		}


		int processID;
		int logicalAddress;
	}
	
	public void stop(){
		synchronized(_stop){
			_stop = true;
		}
		//Release the holds.
		request.release();
		
	}
	
	private boolean shouldStop(){
		boolean val = false;
		synchronized(_stop){
			val = _stop;
		}
		return val;
	}

}
