package proj2;

public class PhysicalMemory {
	
	
	private Frame[] memory;
	private int frameSize;
	
	public PhysicalMemory(int frameCount, int frameSize){
		memory = new Frame[frameCount];
		this.frameSize = frameSize;
		
	}
	
	public synchronized Frame getFrame(int index){
		return memory[index];
	}
	
	public synchronized void setFrame(int index, Frame frame){
		memory[index] = frame;
	}
	
	public synchronized void freeMem(int processID){
		for(int i = 0; i < memory.length; i++){
			if(memory[i].processID() == processID){
				memory[i] = null; //Free this frame
			}
		}
	}
	
	public synchronized int requestFreeFrame(){
		for(int i = 0; i < memory.length; i++){
			if(memory[i] == null){
				
				return i; //Finds first open block
			}
		}
		//Since this is the 'real' memory, I'm going to expand it.  LRU is done in the PageTable, this can be as big as it needs to. 
		//Maybe not in the real world, but in this one, it will expand.
		int oldLength = memory.length;
		Frame[] next = new Frame[memory.length * 2];
		for(int i = 0; i < memory.length; i++){
			next[i] = memory[i];
		}
		memory = next;
		System.out.println("[Memory Manager] Memory has expanded");
		return oldLength + 1; //No available memory
	}

	public int frameSize() {
		return this.frameSize;
	}
	
}
