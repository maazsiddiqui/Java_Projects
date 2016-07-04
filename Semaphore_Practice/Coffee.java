
public class Coffee {
	
	static Semaphore s1 = new Semaphore(1);		// initially unlocked
	static Semaphore s2 = new Semaphore(0);		// initially locked
	static int TOTAL_SEATS = 5;
	static int SEAT_COUNTER = 0;

	public static void main(String[] args) {
		
        if (args.length != 1) {
            printUsage();
        }
        
        int totalCustomers = 0;
        try {
        	totalCustomers = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException e) {
            printUsage();
        }
               
        Thread[] custs = new Thread[totalCustomers];
      
        for (int i = 0; i < totalCustomers; i++) {
            
        	custs[i] = new CustomerThread(i);

            s1.acquire();	// lock for entering in sequence

        	checkSeats();	// checking if we hit seat limit

        	if (SEAT_COUNTER < TOTAL_SEATS) {
        		SEAT_COUNTER++;
        		custs[i].start();
        	}
        	else SEAT_COUNTER--;
       
        }
        
        for (int i = 0; i < totalCustomers; i++) {
            try {
                custs[i].join();
            }
            catch (InterruptedException e) {
            }
        }

        System.out.println("\nPROGRAM FINISHED SUCCESSFULLY");

        System.exit(0);

	}


	public static void checkSeats() {
		
		if (SEAT_COUNTER > TOTAL_SEATS-1) {
			System.out.println("\n===================\nTABLE LIMIT REACHED\n"
								+ "===================\n");

			s2.acquire();	// lock if all 5 seats are full
		}
	}

	private static void printUsage() {
        System.out.println("Invalid Arguments");
        System.exit(-1);
    }
    
    public static void randomSleep(int max) {
        try {
            Thread.sleep((int) (Math.random() * max));
        }
        catch (InterruptedException e) {
        }
    }

}



