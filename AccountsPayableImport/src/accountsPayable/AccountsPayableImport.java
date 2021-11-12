package accountsPayable;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * this application serves to generate a csv that can be used with MIP accounting software based on info given by the user, a file of distribution codes, 
 * and potentially a pre-allocated file containing distribution codes and dollar amounts. 
 * @author Ethan Horton
 *
 */
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

	//instance variables saving information from UI
	private static String[] userInputs, form1099Inputs;
	private static String distCodeFilePath, allocFilePath, glCode; 
	private static boolean is1099, allocEqual;
	private static BigDecimal invoiceAmt; 

	/**
	 * extract data from the UI handler class for use in calculating invoice allocation
	 * @param ui ui handler object that contains all info input by user
	 */
	public static void populateDataFromUI(UIHandler ui) {
		userInputs = ui.getUserInputs(); 
		distCodeFilePath = ui.getDistCodeFilePath();
		is1099 = ui.is1099();
		allocEqual = ui.isAllocEqual(); 

		//user may choose to include 1099 form. if they do not, then output file contains empty strings for those fields
		if(is1099) {
			form1099Inputs = ui.get1099Inputs();			
		} else {
			form1099Inputs = new String[2];
			form1099Inputs[0] = ""; 
			form1099Inputs[1] = ""; 
		}

		//user may choose to allocate equally or include a pre-allocated file
		if(allocEqual) {

			//if user allocates equally, they must include a total invoice amount and a standard gl code but no extra file
			invoiceAmt = BigDecimal.valueOf(Double.parseDouble(ui.getInvoiceAmt()));
			allocFilePath = ""; 
			glCode = ui.getGLCode();
		} else {

			//if user does not allocate equally, they must include a file path for pre-allocated file
			allocFilePath = ui.getAllocFilePath(); 
			invoiceAmt = new BigDecimal(0);
			glCode = "";
		}
	}

	/**
	 * main controls ui and file writing 
	 * @param args
	 */
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

		//gather data user via GUI
		populateDataFromUI(ui); 

		//create distribution code report
		DistCodeReport distCodes = new DistCodeReport(distCodeFilePath, DIST_START_INDEX, CODE_INDEX, PROGRAM_INDEX, GRANT_INDEX, LOAN_INDEX, FAS_INDEX, PERCENT_INDEX);
		OutputTable output; 

		//create allocation report only if user chooses to not allocate equally -- otherwise create output with given invoice amt and gl code
		if (!allocEqual) {
			AllocationReport alloc = new AllocationReport(allocFilePath, ALLOC_START_INDEX, ALLOC_AMT_INDEX, ALLOC_DIST_INDEX, ALLOC_GL_INDEX); 		
			output = new OutputTable(distCodes, alloc);
		} else {
			output = new OutputTable(distCodes, invoiceAmt, glCode); 
		}

		//write to file in specified order
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

		//		BigDecimal debit = BigDecimal.ZERO;
		//		BigDecimal credit = BigDecimal.ZERO;
		//		for(int i = 0; i < output.size(); i++) {
		//			debit = debit.add(output.get(i).getDebit());
		//			credit = credit.add(output.get(i).getCredit()); 
		//		}
		//		System.out.println(debit);
		//		System.out.println(credit);

		//				System.out.println(output);
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
