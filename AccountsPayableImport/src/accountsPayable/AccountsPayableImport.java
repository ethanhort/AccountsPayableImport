package accountsPayable;

import java.util.concurrent.TimeUnit;

public class AccountsPayableImport {

	public static void main(String[] args) {
		UIHandler ui = new UIHandler(); 
		
		//wait for user to finish inputting data into UI. could probably be done with listeners but... this works 
				while (!ui.isFinished()) {
					try {
						TimeUnit.SECONDS.sleep(1);  				
					}
					catch (Exception e) {

					}
				}

	}

}
