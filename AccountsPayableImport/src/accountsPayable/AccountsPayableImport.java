package accountsPayable;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

public class AccountsPayableImport {

	//dist code spreadsheet indices
	private static final int DIST_START_INDEX = 2; 
	private static final int CODE_INDEX = 0;
	private static final int PROGRAM_INDEX = 2; 
	private static final int GRANT_INDEX = 3; 
	private static final int LOAN_INDEX = 4;
	private static final int FAS_INDEX = 5;
	private static final int PERCENT_INDEX = 6; 


	private static String[] userInputs, form1099Inputs;
	private static String distCodeFilePath, allocFilePath, glCode; 
	private static boolean is1099, allocEqual;
	private static BigDecimal invoiceAmt; 

	public static void populateDataFromUI(UIHandler ui) {
		userInputs = ui.getUserInputs(); 
		distCodeFilePath = ui.getDistCodeFilePath();
		is1099 = ui.is1099();
		allocEqual = ui.isAllocEqual(); 

		if(is1099) {
			form1099Inputs = ui.get1099Inputs();			
		} else {
			form1099Inputs = new String[0]; 
		}

		if(allocEqual) {
			invoiceAmt = BigDecimal.valueOf(Double.parseDouble(ui.getInvoiceAmt()));
			allocFilePath = ""; 
			glCode = ui.getGLCode();
		} else {
			allocFilePath = ui.getAllocFilePath(); 
			invoiceAmt = new BigDecimal(0);
			glCode = "";
		}
	}

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

		populateDataFromUI(ui); 

		DistCodeReport distCodes = new DistCodeReport(distCodeFilePath, DIST_START_INDEX, CODE_INDEX, PROGRAM_INDEX, GRANT_INDEX, LOAN_INDEX, FAS_INDEX, PERCENT_INDEX);

		
		//for testing purposes only
		
//		System.out.println(distCodes);
//		System.out.println("User Inputs: ");
//		for(int i = 0; i < userInputs.length; i++) {
//			System.out.println(userInputs[i]);
//		}
//		System.out.println("");
//		System.out.println("1099 Inputs:");
//		for(int i = 0; i < form1099Inputs.length; i++) {
//			System.out.println(form1099Inputs[i]);
//		}
//		System.out.println("");
//		System.out.println("DistCode Filepath: " + distCodeFilePath);
//		System.out.println("Alloc Filepath: " + allocFilePath);
//		System.out.println("Invoice Amount: " + invoiceAmt); 
//		System.out.println("GL Code: " + glCode);
//		System.out.println("Equal: " + allocEqual + ", 1099: " + is1099);

	}

}
