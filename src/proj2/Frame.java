package proj2;

public class Frame {
	private Object[] data;
	private Integer processID;
	
	public Frame(int size, int processID){
		data = new Object[size];
		this.processID = processID;
	}
	
	public int processID(){
		return processID;
	}
	
	public Object get(int offsetAddress){
		return data[offsetAddress];
	}
	
	
}
