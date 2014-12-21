package proj2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UserThread implements Runnable, IStoppable {

	private int _id;
	private MemoryManager _mem;
	private Boolean _stop;
	private String _file;
	private ConcurrentLinkedQueue<Integer> _addresses;

	public UserThread(int id, MemoryManager mm, String filePath){
		_id = id;
		_mem = mm;
		_stop = false;
		_file = filePath;
		_addresses = new ConcurrentLinkedQueue<Integer>();
	}
	
	public UserThread(int id, MemoryManager mm) {
		_id = id;
		_mem = mm;
		_stop = false;
		_addresses = new ConcurrentLinkedQueue<Integer>();
	}

	@Override
	public void run() {
		if(_file != null){
			try{
				BufferedReader bf = new BufferedReader(new FileReader(_file));
				String line = bf.readLine();
				while(line != null){
					_addresses.add(Integer.parseInt(line));
					line = bf.readLine();
				}
				bf.close();
			} catch (IOException ioExc){
				System.out.println("[Process " + _id + "] has encountered an error: Could not open the file: " + _file);
				System.out.println("[Process " + _id + "] will simulate addresses instead.");
				simulate();
			}
		} else {
			simulate();
		}
		for(int addr : _addresses){
			request(addr);
		}
		freeMem();
		System.out.println("[Process " + _id + "] has stopped.");
	}

	
	/**
	 * Used this as test code
	 */
	public synchronized void simulate(){
		Random rand = new Random();
		for(int i = 0; i < 10; i++){
			if(shouldStop()){
				break;
			}
			_addresses.add(Math.abs(rand.nextInt()) % 1000);
		}
	}
	
	/**
	 * Requests the logical address
	 * @param logicalAddress
	 */
	private synchronized void request(int logicalAddress) {
		_mem.requestAddress(_id, logicalAddress);
	}
	
	/**
	 * Frees all addresses from this thread
	 */
	private synchronized void freeMem(){
		_mem.freeThread(this._id);
	}

	/**
	 * ID of this user process
	 * @return
	 */
	public int id() {
		return _id;
	}

	@Override
	public int hashCode() {
		return _id; // unique identifier
	}

	/**
	 * Checks to see if the value has changed, and if this thread should terminate
	 * @return
	 */
	private boolean shouldStop(){
		boolean val = false;
		synchronized(_stop){
			val = _stop;
		}
		return val;
	}
	
	@Override
	public void stop() {
		synchronized(_stop){
			_stop = true;
		}
	}

}
