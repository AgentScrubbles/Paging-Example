package proj2;

public class PageFault extends Exception{

	private String message;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1038057299111574178L;

	public PageFault(int processID, int pageNumber){
		message = "[Process " + processID + "] fails to access page number " + pageNumber;
	}
	
	public PageFault(int processID, int address, int pageNumber, int pageOffset){
		message = "[Process " + processID + "] accesses address " + address + " (page number = " + pageNumber
				+ ", page offset = " + pageOffset + ") not in main memory";
	}
	
	
	
	@Override
	public String getMessage(){
		return message;
	}
	
	
}
