
public class CustomerThread extends Thread {
	
    private int id;

    public CustomerThread(int id) {
        this.id = id;
    }	
	
    public void run() {

    	arriveAtShop();
    	System.out.println("Customers in shop: " + Coffee.SEAT_COUNTER);
    	Coffee.s1.release();	// release lock for entering in sequence
   		
   		drinkCoffee();
   		leaveShop();
   		
   		Coffee.SEAT_COUNTER--;
   		
   		if (Coffee.SEAT_COUNTER == 0) {
   			Coffee.s2.release();	// release lock for 5 seats
   			System.out.println();
   		}

    }
    
    private void arriveAtShop() {
        System.out.println("+ Customer " + id + " arrived");
    }
	
    private void drinkCoffee() {
        System.out.println("Customer " + id + " drinking coffee");
        Coffee.randomSleep(1000);
    }
    
    private void leaveShop() {
    	System.out.println("- Customer " + id + " leaves shop");
    }
}