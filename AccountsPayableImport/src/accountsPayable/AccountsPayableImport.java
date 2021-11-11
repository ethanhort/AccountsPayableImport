package accountsPayable;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

public class AccountsPayableImport {

	//UI indices
	private static final int ID_TEXT = 0; 
	private static final int SESS_DATE_TEXT = 1; 
	private static final int SESS_DESCRIPTION_TEXT = 2;
	private static final int DOC_NUM_TEXT = 3;
	private static final int DOC_DATE_TEXT = 4;
	private static final int DOC_DESCRIPTION_TEXT = 5;
	private static final int EFFECTIVE_DATE_TEXT = 6;
	private static final int DUE_DATE_TEXT = 7; 
	private static final int VENDOR_ID_TEXT = 8; 

	private static final int TYPE_INDEX  = 0; 
	private static final int BOX_INDEX = 1;

	//dist code spreadsheet indices
	private static final int DIST_START_INDEX = 2; 
	private static final int CODE_INDEX = 0;
	private static final int PROGRAM_INDEX = 2; 
	private static final int GRANT_INDEX = 3; 
	private static final int LOAN_INDEX = 4;
	private static final int FAS_INDEX = 5;
	private static final int PERCENT_INDEX = 6; 

	//allocation spreadsheet indices
	private static final int ALLOC_START_INDEX = 2; 
	private static final int ALLOC_AMT_INDEX = 0; 
	private static final int ALLOC_DIST_INDEX = 1;
	private static final int ALLOC_GL_INDEX = 2; 


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
			form1099Inputs = new String[2];
			form1099Inputs[0] = ""; 
			form1099Inputs[1] = ""; 
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
		OutputTable output; 
		if (!allocEqual) {
			AllocationReport alloc = new AllocationReport(allocFilePath, ALLOC_START_INDEX, ALLOC_AMT_INDEX, ALLOC_DIST_INDEX, ALLOC_GL_INDEX); 			
			output = new OutputTable(distCodes, alloc);
		} else {
			output = new OutputTable(distCodes, invoiceAmt, glCode); 
		}

		try {
			FileWriter writer = new FileWriter("accounts_payable_import.csv");
			for (int i = 0; i < output.size(); i++) {
				writer.write(userInputs[ID_TEXT] + ", " + userInputs[SESS_DATE_TEXT] + ", " + userInputs[SESS_DESCRIPTION_TEXT] + ", " + userInputs[DOC_NUM_TEXT] 
						+ ", " + userInputs[DOC_DATE_TEXT] + ", " + userInputs[DOC_DESCRIPTION_TEXT] + ", " + userInputs[EFFECTIVE_DATE_TEXT] + ", "
								+ userInputs[DUE_DATE_TEXT] + ", BS, API, " + userInputs[VENDOR_ID_TEXT] + ", V, " + form1099Inputs[TYPE_INDEX] + ", Main, N, "
								+ output.get(i).getProgram() + ", " + output.get(i).getGrant() + ", " + output.get(i).getGL() + ", " + output.get(i).getLoanNum()
								+ ", " + output.get(i).getFas() + ", " + form1099Inputs[BOX_INDEX] + ", " + output.get(i).getDebit() + ", "
								+ output.get(i).getCredit() + "\n");
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			UIHandler.handleError("Problem writing to file. Please try again.");
		}

		//for testing purposes only

		//		System.out.println(output);
		//		System.out.println(distCodes);
		//				System.out.println("User Inputs: ");
		//				for(int i = 0; i < userInputs.length; i++) {
		//					System.out.println(userInputs[i]);
		//				}
		//		System.out.println("");
		//				System.out.println("1099 Inputs:");
		//				for(int i = 0; i < form1099Inputs.length; i++) {
		//					System.out.println(form1099Inputs[i]);
		//				}
		//		System.out.println("");
		//		System.out.println("DistCode Filepath: " + distCodeFilePath);
		//		System.out.println("Alloc Filepath: " + allocFilePath);
		//		System.out.println("Invoice Amount: " + invoiceAmt); 
		//		System.out.println("GL Code: " + glCode);
		//		System.out.println("Equal: " + allocEqual + ", 1099: " + is1099);

	}

}
