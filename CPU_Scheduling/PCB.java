import java.util.*;

/**
 * 	PCB data structure
 * 	@author Maaz Siddiqui, Ahmar Mohammed
 *
 */

public class PCB implements Comparable<PCB> {

	private int jobID;
	private int arrivalTime;
	private String state;
	private int pCounter;
	private int nCPU_Bursts;
	private Queue<Integer> CPU_Bursts;
	private int currCPU_Burst;
	private int IOCompleteTime;
	private int waitingTime;
	private int processingTime;
	private int completionTime;
	private int turnaroundTime;
	private int totalCPUBurstUnits;
	private int totalIOBurstUnits;


	/**
	 * 	PCB constructor
	 * 	Argument: accept single string and split  to generate PCB
	 * 
	 * 	@param line
	 */

	PCB(String line) {

		Scanner lineScanner = new Scanner(line);

		lineScanner.useDelimiter(" ");

		this.jobID = Integer.parseInt(lineScanner.next());
		this.arrivalTime = Integer.parseInt(lineScanner.next());
		this.nCPU_Bursts = Integer.parseInt(lineScanner.next());
		this.CPU_Bursts = new LinkedList<Integer>();

		// generating queue for CPU bursts
		while (lineScanner.hasNext()) {
			CPU_Bursts.add(Integer.parseInt(lineScanner.next()));
		}

		lineScanner.close();

		currCPU_Burst = this.CPU_Bursts.element();
		this.state = "READY"; 	// PCB only generated when ready has space

		// calculated total IO and CPU bursts
		
		for(int s : CPU_Bursts) { 
			totalCPUBurstUnits += s;		    
		}

		totalIOBurstUnits = (CPU_Bursts.size() - 1 ) * Main.IOTime;		    

	}
	

	/**
	 * 	print PCB in desired format
	 */
	
	public void printPCB() {
		System.out.println("Job ID: " + jobID);
		System.out.println("Arrival Time: " + arrivalTime);
		System.out.println("Completion Time: " + completionTime);
		System.out.println("Processing Time: " + processingTime);
		System.out.println("Waiting Time: " + (completionTime - totalCPUBurstUnits
							- arrivalTime - totalIOBurstUnits));
		System.out.println("Turnaround Time: " + completionTime);
		return;
	}

	
	/**
	 * 	compare if two CPU Burst heads have equal values
	 * 
	 */
	
	public boolean equals(PCB other) {
		return this.CPU_Bursts.element() == other.CPU_Bursts.element();
	}

	
	/**
	 * 	required method of comparable interface
	 * 	
	 * 	only compares head's CPU burst value
	 * 	can't use this method to compare two PCB's
	 */
	
	public int compareTo(PCB other) {
		if(this.equals(other))
			return 0;
		else if (this.CPU_Bursts.element() > other.CPU_Bursts.element()) {
			return 1;
		}
		else
			return -1;
	}

	
	/**
	 *	method to update head's CPU burst value
	 *	only used in Round Robin 
	 */
	
	public Queue<Integer> updateCPUBurstHead(Queue<Integer> oldQueue) {

		Queue<Integer> newQueue = new LinkedList<Integer>();
		int headValue = this.removeCPU_Bursts();

		if (headValue >= Main.timeQuantum) {
			newQueue.add(headValue - Main.timeQuantum);
		}
		else 
			newQueue.add(0);

		while (!oldQueue.isEmpty()) {
			newQueue.add(this.CPU_Bursts.remove());
		}	
		
		return newQueue;
	}

	
	// getters and setters

	public int getJobID() {
		return jobID;
	}

	public void setJobID(int jobID) {
		this.jobID = jobID;
	}

	public int getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(int arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getpCounter() {
		return pCounter;
	}

	public void setpCounter(int pCounter) {
		this.pCounter = pCounter;
	}

	public int getnCPU_Bursts() {
		return CPU_Bursts.size();
	}

	public void setnCPU_Bursts(int nCPU_Bursts) {
		this.nCPU_Bursts = nCPU_Bursts;
	}

	public int removeCPU_Bursts() {
		int temp = CPU_Bursts.remove();
		nCPU_Bursts = CPU_Bursts.size();
		return temp;
	}

	public Queue<Integer> getCPU_Bursts() {
		return this.CPU_Bursts;
	}

	public void setCPU_Bursts(Queue<Integer> cPU_Bursts) {
		CPU_Bursts = cPU_Bursts;
	}

	public int getCurrCPU_Burst() {
		return currCPU_Burst;
	}

	public void setCurrCPU_Burst(int currCPU_Burst) {
		this.currCPU_Burst = currCPU_Burst;
	}

	public int getIOCompleteTime() {
		return IOCompleteTime;
	}

	public void setIOCompleteTime(int IOCompleteTime) {
		this.IOCompleteTime = IOCompleteTime;
	}

	public void setWaitingTime(int waitingTime) {
		this.waitingTime = waitingTime;
	}

	public int getProcessingTime() {
		return processingTime;
	}

	public void setProcessingTime(int processingTime) {
		this.processingTime = processingTime;
	}

	public int getCompletionTime() {
		return completionTime;
	}

	public void setCompletionTime(int completionTime) {
		this.completionTime = completionTime;
	}

	public int getTurnaroundTime() {
		return completionTime;
	}

	public void setTurnaroundTime(int turnaroundTime) {
		this.turnaroundTime = turnaroundTime;
	}
	
	public int getWaitingTime() {
		return completionTime - totalCPUBurstUnits - arrivalTime - totalIOBurstUnits;
	}

}
