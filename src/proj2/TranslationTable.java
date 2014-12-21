package proj2;

import java.util.HashMap;

public class TranslationTable {
	
	HashMap<Integer, HashMap<Integer, Integer>> dictionary;
	
	public TranslationTable(){
		dictionary = new HashMap<Integer, HashMap<Integer,Integer>>();
	}
	
	/**
	 * Adds the virtual address and actual address to the process table
	 * @param processID
	 * 		Process that is adding the address
	 * @param virtualAddress
	 * 		What the process thinks the address is
	 * @param actualAddress
	 * 		What the actual address is
	 */
	
	public void put(int processID, int virtualAddress, int actualAddress){
		HashMap<Integer, Integer> currentProcessMap = dictionary.get(processID);
		if(currentProcessMap == null){
			currentProcessMap = new HashMap<Integer, Integer>();
			dictionary.put(processID, currentProcessMap);
		}
		currentProcessMap.put(virtualAddress, actualAddress);
	}
	
	public int getActual(int processID, int virtualAddress){
		
		HashMap<Integer, Integer> processMap = dictionary.get(processID);
		if(processMap == null){
			//Might as well add it now
			processMap = new HashMap<Integer,Integer>();
			dictionary.put(processID, processMap);
			return -1;
		}
		Integer actual = processMap.get(virtualAddress);
		if(actual == null){
			return -1;
		}
		return actual;
	}
	
}
