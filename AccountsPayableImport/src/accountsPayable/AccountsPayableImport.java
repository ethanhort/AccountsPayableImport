package accountsPayable;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

public class AccountsPayableImport {

	public static void main(String[] args) {
		UIHandler ui = new UIHandler(); 
		String[] userInputs, form1099Inputs;
		String distCodeFilePath, allocFilePath; 
		boolean is1099, allocEqual;
		BigDecimal invoiceAmt; 

		//wait for user to finish inputting data into UI. could probably be done with listeners but... this works 
		while (!ui.isFinished()) {
			try {
				TimeUnit.SECONDS.sleep(1);  				
			}
			catch (Exception e) {

			}
		}
		
		userInputs = ui.getUserInputs(); 
		distCodeFilePath = ui.getDistCodeFilePath();
		is1099 = ui.is1099();
		allocEqual = ui.isAllocEqual(); 
		
		if(is1099) {
			form1099Inputs = ui.get1099Inputs();			
		}
		
		if(allocEqual) {
			invoiceAmt = BigDecimal.valueOf(Double.parseDouble(ui.getInvoiceAmt()));
		} else {
			allocFilePath = ui.getAllocFilePath(); 
		}
		
		

	}

}
