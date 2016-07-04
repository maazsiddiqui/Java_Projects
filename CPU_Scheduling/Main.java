import java.io.*;
import java.util.*;

/**
 * Project 1: CPU Scheduling
 * 
 * This project is to simulate and evaluate the performance
 * of three CPU scheduling algorithms:
 * 
 * - First Come First Serve (FCFS)
 * - Shortest Job First (SJF)
 * - Round Robin (RR)
 * 
 * @author Maaz Siddiqui & Ahmar Mohammed
 * Due Date: 3/30/2016
 *
 */

public class Main {

	// global variables, easy to update if required
	static int CPU_CLOCK = 0;
	static final int DegreeOfProg = 10;
	static final int IOTime = 10;
	static int printCounter = 200;	// will help print statistics every 200 CPU cycles
	static int timeQuantum;

	public static void main(String[] args) throws FileNotFoundException {

		Queue<String> JobQueue;
		Queue<PCB> BlockedQueue = new LinkedList<PCB>();
		Queue<PCB> CompletedQueue = new LinkedList<PCB>();

		
		// verifying command line arguments and
		// starting requested short term schedulers

		if ((args.length == 2) && (args[0].toUpperCase().equals("FCFS"))) {		
			JobQueue = new LinkedList<String>(queueBuilder(args, 1));
			startFCFS(args, JobQueue, BlockedQueue, CompletedQueue);
			simulationReportPrint(args, CompletedQueue);
		}

		else if ((args.length == 2) && (args[0].toUpperCase().equals("SJF"))) {		
			JobQueue = new PriorityQueue<String>(queueBuilder(args, 1));
			startSJF(args, JobQueue, BlockedQueue, CompletedQueue);
			simulationReportPrint(args, CompletedQueue);
		}

		else if ((args.length == 3) && (args[0].toUpperCase().equals("RR"))) {
			JobQueue = new LinkedList<String>(queueBuilder(args, 2));
			timeQuantum = Integer.parseInt(args[1]);
			startRR(args, JobQueue, BlockedQueue, CompletedQueue);
			simulationReportPrint(args, CompletedQueue);
		}

		else {
			System.out.println("Improper command line arguments.");
			return;
		}
		
		return;

	} // end of main

	
	

	//	ALGORITHMS
	
	/**
	 * FCFS Scheduler
	 * 
	 * @param args
	 * @param JobQueue
	 * @param BlockedQueue
	 * @param CompletedQueue
	 */
	
	public static void startFCFS(String[] args, Queue<String> JobQueue, Queue<PCB> BlockedQueue,
								Queue<PCB> CompletedQueue) {
		
		Queue<PCB> ReadyQueue = new LinkedList<PCB>();

		while ((!JobQueue.isEmpty()) || (!BlockedQueue.isEmpty()) || (!ReadyQueue.isEmpty())) {

			blockedToReady(BlockedQueue, ReadyQueue);	// first priority given to Blocked Queue

			jobToReady(JobQueue, ReadyQueue);	// second priority given to Job Queue

			PCB eachPCB = ReadyQueue.element(); // peek into first PCB
			eachPCB.setCurrCPU_Burst(eachPCB.removeCPU_Bursts());	// set current CPU burst
			eachPCB.setState("RUNNING");
			eachPCB.setProcessingTime(eachPCB.getProcessingTime() + eachPCB.getCurrCPU_Burst());
			CPU_CLOCK += eachPCB.getCurrCPU_Burst();

			PCB getHeadPCB = ReadyQueue.remove();	//	extract Ready Queue Head PCB

			//	send PCB to IO Queue if more CPU bursts left
			if (getHeadPCB.getnCPU_Bursts() > 0) {
				getHeadPCB.setState("BLOCKED");
				getHeadPCB.setIOCompleteTime(CPU_CLOCK + IOTime);
				BlockedQueue.add(getHeadPCB);
			}
			//	if no CPU bursts left then send to Completed Queue
			else {
				readyToCompleted(CompletedQueue, getHeadPCB);	// send PCB to Completed Queue
			}

			statPrint(BlockedQueue, CompletedQueue, ReadyQueue);	// print statistics
		}

	} // end of FCFS



	/**
	 * SJF Scheduler
	 * 
	 * @param args
	 * @param JobQueue
	 * @param BlockedQueue
	 * @param CompletedQueue
	 */
	
	public static void startSJF(String[] args, Queue<String> JobQueue, Queue<PCB> BlockedQueue,
								Queue<PCB> CompletedQueue) {
		
		PriorityQueue<PCB> ReadyQueue = new PriorityQueue<PCB>();

		while ((!JobQueue.isEmpty()) || (!BlockedQueue.isEmpty()) || (!ReadyQueue.isEmpty())) {

			blockedToReady(BlockedQueue, ReadyQueue);	// first priority given to Blocked Queue

			jobToReady(JobQueue, ReadyQueue);	// second priority given to Job Queue

			PCB eachPCB = ReadyQueue.element(); // peek into first PCB
			eachPCB.setCurrCPU_Burst(eachPCB.removeCPU_Bursts());	// set current CPU burst
			eachPCB.setState("RUNNING");
			eachPCB.setProcessingTime(eachPCB.getProcessingTime() + eachPCB.getCurrCPU_Burst());
			CPU_CLOCK += eachPCB.getCurrCPU_Burst();

			PCB getHeadPCB = ReadyQueue.remove();	//	extract Ready Queue's Head PCB

			//	send PCB to IO Queue if more CPU bursts left
			if (getHeadPCB.getnCPU_Bursts() > 0) {
				getHeadPCB.setState("BLOCKED");
				getHeadPCB.setIOCompleteTime(CPU_CLOCK + IOTime);
				BlockedQueue.add(getHeadPCB);
			}
			//	if no CPU bursts left then send to Completed Queue
			else {
				readyToCompleted(CompletedQueue, getHeadPCB);	// send PCB to Completed Queue
			}

			statPrint(BlockedQueue, CompletedQueue, ReadyQueue);	// print statistics
		}

	} // end of SJF

	

	/**
	 * Round Robin Scheduler
	 * 
	 * @param args
	 * @param JobQueue
	 * @param BlockedQueue
	 * @param CompletedQueue
	 */
	
	public static void startRR(String[] args, Queue<String> JobQueue, Queue<PCB> BlockedQueue,
								Queue<PCB> CompletedQueue) {

		Queue<PCB> ReadyQueue = new LinkedList<PCB>();

		while ((!JobQueue.isEmpty()) || (!BlockedQueue.isEmpty()) || (!ReadyQueue.isEmpty())) {

			blockedToReady(BlockedQueue, ReadyQueue);	// first priority given to Blocked Queue

			jobToReady(JobQueue, ReadyQueue);	// second priority given to Job Queue

			PCB eachPCB = ReadyQueue.element(); // look first PCB in Ready Queue
			eachPCB.setCurrCPU_Burst(eachPCB.getCPU_Bursts().element()); // set current burst
			eachPCB.setState("RUNNING"); // change state to running

			if (eachPCB.getCPU_Bursts().element() >= timeQuantum) {
				eachPCB.setProcessingTime(eachPCB.getProcessingTime() + timeQuantum); // add time quantum in processing time
				CPU_CLOCK += timeQuantum; // update CPU Clock by time Quantum
			}
			else {
				eachPCB.setProcessingTime(eachPCB.getProcessingTime() + eachPCB.getCPU_Bursts().element()); // add time quantum in processing time
				CPU_CLOCK += eachPCB.getCPU_Bursts().element(); // update CPU Clock by time Quantum
			}

			ReadyQueue.element().setCPU_Bursts(ReadyQueue.element().updateCPUBurstHead(ReadyQueue.element().getCPU_Bursts()));

			PCB getHeadPCB = ReadyQueue.remove();	//	extract Ready Queue Head PCB

			if (getHeadPCB.getCPU_Bursts().element() > 0) {
				ReadyQueue.add(getHeadPCB);
			}
			
			else {				
				getHeadPCB.removeCPU_Bursts();
				
				//	send PCB to IO Queue if more CPU bursts left
				if (getHeadPCB.getnCPU_Bursts() > 0) {
					getHeadPCB.setState("BLOCKED");
					getHeadPCB.setIOCompleteTime(CPU_CLOCK + IOTime);
					BlockedQueue.add(getHeadPCB);
				}
				//	if no CPU bursts left then send to Completed Queue
				else {
					readyToCompleted(CompletedQueue, getHeadPCB);	// send PCB to Completed Queue
				}
			}

			statPrint(BlockedQueue, CompletedQueue, ReadyQueue);	// print statistics
		}

	} // end of RR



	/**
	 * move PCBs from Ready to Completed Queue and Job Termination Summary
	 * 
	 * @param CompletedQueue
	 * @param somePCB
	 */
	
	public static void readyToCompleted(Queue<PCB> CompletedQueue, PCB somePCB) {
		somePCB.setState("COMPLETED");
		somePCB.setCompletionTime(CPU_CLOCK);
		CompletedQueue.add(somePCB);
		System.out.println("Job Termination Summary");
		System.out.println("=======================");
		somePCB.printPCB();
		System.out.println();
	}


	
	/**
	 * move PCBs from Job to Ready Queue
	 * 
	 * @param JobQueue
	 * @param ReadyQueue
	 */
	
	public static void jobToReady(Queue<String> JobQueue, Queue<PCB> ReadyQueue) {
		
		// second priority given to Ready Queue
		// also working as long term scheduler
		
		while ((ReadyQueue.size() < DegreeOfProg) && (!JobQueue.isEmpty())) {
			if(new PCB(JobQueue.element()).getArrivalTime() <= CPU_CLOCK) {
				ReadyQueue.add(new PCB(JobQueue.remove()));
			}
			else {
				CPU_CLOCK++; // increase CPU cycle to prevent stall
			}
		}
	}



	/**
	 * check if any jobs in ready blocked queue are done
	 * and move them in ready queue if ready queue allows
	 * 
	 * @param BlockedQueue
	 * @param ReadyQueue
	 */
	
	public static void blockedToReady(Queue<PCB> BlockedQueue, Queue<PCB> ReadyQueue) {
		// first priority given to Blocked Queue

		if (((ReadyQueue.size() < DegreeOfProg) || (ReadyQueue.isEmpty())) && (!BlockedQueue.isEmpty())) {

			Iterator<PCB> iter = BlockedQueue.iterator();
			
			while (iter.hasNext()) {

				PCB eachPCB = iter.next();

				// check if IO burst is completed and Ready Queue can accept process
				if (CPU_CLOCK >= eachPCB.getIOCompleteTime() && (ReadyQueue.size() < DegreeOfProg)) {
					eachPCB.setState("READY");
					eachPCB.setProcessingTime(eachPCB.getProcessingTime() + IOTime); // adding IO time to processing time 
					ReadyQueue.add(eachPCB); // send PCB to Ready Queue
					iter.remove();
				}
				// if job queue is empty and PCB's are only in Blocked Queue
				else if (ReadyQueue.isEmpty()) { 
					eachPCB.setState("READY");
					eachPCB.setProcessingTime(eachPCB.getProcessingTime() + IOTime);
					CPU_CLOCK += eachPCB.getIOCompleteTime() - CPU_CLOCK ;
					ReadyQueue.add(eachPCB);
					iter.remove();
				}
			}

		}
	}


	

	/**
	 * 	command line file reader and job queue builder
	 * 	reading file line by line and adding in queue
	 */

	static Queue<String> queueBuilder(String[] inputFile, int index) throws FileNotFoundException {

		Queue<String> someQueue = new LinkedList<String>();
		Scanner dataFile = new Scanner(new File(inputFile[index]));

		while (dataFile.hasNextLine()) {
			String line = dataFile.nextLine().trim();
			someQueue.add(line);				
		}

		dataFile.close(); // input file closed
		return someQueue;
	}

	
	// printing and debugging methods
	
	public static void statPrint(Queue<PCB> BlockedQueue, Queue<PCB> CompletedQueue, Queue<PCB> ReadyQueue) {
		if (CPU_CLOCK > printCounter) {
			System.out.println("Statistics");
			System.out.println("==========");
			System.out.println("Jobs in Ready Queue: " + ReadyQueue.size());
			System.out.println("Jobs in Blocked Queue: " + BlockedQueue.size());
			System.out.println("Jobs in Completed Queue: " + CompletedQueue.size());
			System.out.println();
			printCounter += 200;
		}
	}
	
	public static void simulationReportPrint(String[] args, Queue<PCB> CompletedQueue) {
		System.out.println("Simulation Report");
		System.out.println("=================");
		System.out.println("Algorithm Used: " + args[0].toUpperCase());
		System.out.println("CPU Cycle: " + CPU_CLOCK);

		int avgProcessing = 0;
		int avgWaitingTime = 0;
		int avgturnaroundTime = 0;

		for(PCB eachPCB : CompletedQueue) { 
			avgProcessing += eachPCB.getProcessingTime();
			avgWaitingTime += eachPCB.getWaitingTime();
			avgturnaroundTime += eachPCB.getTurnaroundTime();
		}

		System.out.println("Average Processing Time: " + (avgProcessing / CompletedQueue.size()));
		System.out.println("Average Waiting Time: " + (avgWaitingTime / CompletedQueue.size()));
		System.out.println("Average Turnaround Time: " + (avgturnaroundTime / CompletedQueue.size()));
	}
	
	static void printQueue(Queue<PCB> someQueue) {
		for(PCB eachPCB : someQueue) { 
			eachPCB.printPCB();
			System.out.println();
		}
	}

	static void printPriorityQueue(PriorityQueue<PCB> someQueue) {
		while (!someQueue.isEmpty()) {
			someQueue.remove().printPCB();
			System.out.println();
		}
	}
	
	static void printJobQueue(Queue<String> JobQueue) {
		for(String eachJob : JobQueue) { 
			System.out.println(eachJob);
		}
	}

}
