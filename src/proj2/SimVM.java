package proj2;

import java.util.ArrayList;

public class SimVM {

	public static void main(String[] args) throws InterruptedException {
		System.out.println("[Main Thread] has started.");

		int frameCount = 100;
		int frameSize = 300;
		int pageCount = 20;
		int threadCount = 10;

		if (args != null && args.length == 4) {
			// use the args to find S, P, and F
			// F - Number of Frames
			// S - Size of each frame
			// P number of pages
			try {
				frameSize = Integer.parseInt(args[0]);
				pageCount = Integer.parseInt(args[1]);
				frameCount = Integer.parseInt(args[2]);
				threadCount = Integer.parseInt(args[3]);
			} catch (Exception ex) {
				System.out
						.println("[Main Thread] Error determining inputs.  Default values will be used.");
			}
		}
		
		System.out.println("[Main Thread] Frame Size is set to " + frameSize);
		System.out.println("[Main Thread] Frame Count is set to " + frameCount);
		System.out.println("[Main Thread] Page Count is set to " + pageCount);
		System.out.println("[Main Thread] Thread Count is set to " + threadCount);

		ArrayList<IStoppable> thingsToStop = new ArrayList<IStoppable>();

		PhysicalMemory memory = new PhysicalMemory(frameCount, frameSize);

		PageFaultHandler pageFaultHandler = new PageFaultHandler(memory);

		MemoryManager memoryManager = new MemoryManager(pageCount, memory,
				pageFaultHandler);

		new Thread(memoryManager).start();
		new Thread(pageFaultHandler).start();

		// Below is my test code / simulation.
		/**
		 * int threadID = 0; for (int j = 0; j < 30; j++) { for (int i = 0; i <
		 * 20; i++) { UserThread ut = new UserThread(threadID++, memoryManager);
		 * thingsToStop.add(ut); new Thread(ut).start(); }
		 * 
		 * Thread.sleep(4000);
		 * 
		 * for (IStoppable i : thingsToStop) { i.stop(); } Thread.sleep(1000); }
		 **/

		// Based on the document, N is the amount of files.
		for (int i = 0; i < threadCount; i++) {
			UserThread ut = new UserThread(i, memoryManager, "address_" + i
					+ ".txt");
			thingsToStop.add(ut);
			new Thread(ut).start();
		}
		Thread.sleep(60000); // Run for 1 minute, then kill everything.
		for (IStoppable i : thingsToStop) {
			i.stop();
		}
		Thread.sleep(1000);
		memoryManager.stop();
		pageFaultHandler.stop();
		System.out.println("[Main Thread] has stopped.");
	}
}
