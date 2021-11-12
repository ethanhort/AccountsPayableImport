package accountsPayable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList; 
import java.util.List; 

/**
 * representation of the output report that gets transcribed into the output file
 * 
 * output table pulls strings from input files and calculates debit and credit values based on info entered in the UI
 * @author ethan horton
 *
 */
public class OutputTable {

	private List<OutputRow> report; //arraylist instance variable 
	private DistCodeReport distCodes; //dist code report passed in
	private AllocationReport allocation; //allocation report passed in only if user selects to not allocate equally
	private String glCode; //generic gl from user only if user selects to allocate equally

	/**
	 * constructor saves reports as instances variables in the case that class is instantiated
	 * with an allocation report (i.e. user does not allocate equally) 
	 * @param distCodes distribution code report containing percenteges, dist codes, etc. 
	 * @param allocation allocation report containing dollar amounts 
	 */
	public OutputTable(DistCodeReport distCodes, AllocationReport allocation) {
		report = new ArrayList<OutputRow>();
		this.allocation = allocation; 
		this.distCodes = distCodes; 

		populateUnallocatedReport(); 
	}

	/**
	 * overloaded constructor saves dist codes as instance variable and begins distributing invoice amount 
	 * if user selects to allocate equally
	 * @param distCodes distribution codes containing percentages, dist codes, etc
	 * @param amt dollar amount to be allocated equally
	 * @param glCode generic gl code for use when allocating equally
	 */
	public OutputTable(DistCodeReport distCodes, BigDecimal amt, String glCode) {
		report = new ArrayList<OutputRow>(); 
		this.distCodes = distCodes;
		this.glCode = glCode; 

		//calculate the number of unique distribution codes, divide the total amount by that number, and determine the rounding error
		int numCodes = distCodes.countCodes();
		BigDecimal amtPerCode = amt.divide(new BigDecimal(numCodes), 2, RoundingMode.HALF_UP); 
		BigDecimal difference = amt.subtract(amtPerCode.multiply(new BigDecimal(numCodes))); 

		populateAllocatedReport(amtPerCode, difference); 

	}

	/**
	 * primary method that creates the report if the user chooses to allocate the invoice amount equally
	 * @param amtPerCode total invoice amount divided by the number of unique distribution codes
	 * @param difference rounding error from dividing the invoice amount equally
	 */
	public void populateAllocatedReport(BigDecimal amtPerCode, BigDecimal difference) {
		BigDecimal tempTotal = amtPerCode.add(difference); //rounding error added to the dollar amt per code to use full invoice amt
		int index = 0;
		String curr = distCodes.get(index).getDistCode(); //current distribution code tracked so that we know when we get to the next dist code
		BigDecimal total = new BigDecimal(0);  //running total of calculated debit/credit so that we can acct for rounding error

		List<OutputRow> tempList = new ArrayList<OutputRow>(); //temporary list used to hold outputs from current distribution code

		//first loop distributes the temporary amount (accounting for rounding) per code to the first distribution code
		while (distCodes.get(index).getDistCode().equals(curr)) {
			String program, grant, gl, loanNum, fas; //string values needed for output rows
			BigDecimal debit, credit; //dollar values needed for output rows

			//static information pulled from distribution code file
			program = distCodes.get(index).getProgram(); 
			grant = distCodes.get(index).getGrant();
			gl = glCode; 
			loanNum = distCodes.get(index).getLoanNum();
			fas = distCodes.get(index).getFas(); 

			//calculate debit with percentages from distribution code
			debit = tempTotal.multiply(distCodes.get(index).getPercent()).setScale(2, RoundingMode.HALF_UP);

			//when distributing equally, all credits are 0
			credit = BigDecimal.ZERO.setScale(2);  

			//add output rows to the temporary list so that rounding can be accounted for
			tempList.add(new OutputRow(program, grant, gl, loanNum, fas, debit, credit)); 
			total = total.add(debit); //track total of each row to calculate rounding error
			index++; 
		}

		//determine if rounding error needs to be accounted for
		if (total.compareTo(tempTotal) != 0) {
			tempList = adjustDebit(tempList, tempTotal.subtract(total)); 
		}

		//after accounting for rounding error, add temporary rows to permanent report
		report.addAll(tempList);

		//reset temporary values
		tempList = new ArrayList<OutputRow>(); 
		total = BigDecimal.ZERO; 

		curr = distCodes.get(index).getDistCode(); 

		//iterate through remainder of distribution code report, doing the same as above
		while (index < distCodes.size()) {
			if (distCodes.get(index).getDistCode().equals(curr)) {
				String program, grant, gl, loanNum, fas; 
				BigDecimal debit, credit; 

				//static information pulled from distribution code file
				program = distCodes.get(index).getProgram(); 
				grant = distCodes.get(index).getGrant();
				gl = glCode; 
				loanNum = distCodes.get(index).getLoanNum();
				fas = distCodes.get(index).getFas(); 

				debit = amtPerCode.multiply(distCodes.get(index).getPercent()).setScale(2, RoundingMode.HALF_UP);
				credit = BigDecimal.ZERO.setScale(2);  

				tempList.add(new OutputRow(program, grant, gl, loanNum, fas, debit, credit)); 
				total = total.add(debit); 
				index++; 
			} else {

				//adjust for rounding from previous dist code
				if (total.compareTo(amtPerCode) != 0) {
					tempList = adjustDebit(tempList, amtPerCode.subtract(total)); 
				}

				//add rows from previous dist code
				report.addAll(tempList);

				//reset temp info to begin next dist code
				tempList = new ArrayList<OutputRow>(); 
				total = BigDecimal.ZERO; 
				curr = distCodes.get(index).getDistCode(); 
			}
		}

		//must add to final report one last time to account for final dist code
		report.addAll(tempList);
	}

	/**
	 * primary method that creates the report if the user chooses to use a pre allocated file rather than 
	 * allocating equally
	 */
	public void populateUnallocatedReport() {
		AllocationRow row; //current row of allocation report
		String distCode; //dist code to be found
		ArrayList<DistCodeRow> relevantRows; //list of dist code rows with current dist code

		//values for creating output row
		String program, grant, gl, loanNum, fas; 
		BigDecimal debit, credit; 

		//iterate through entire allocation report
		for(int i = 0; i < allocation.size(); i++) {
			row = allocation.get(i);
			distCode = row.distCode(); 

			//find rows with current dist code
			relevantRows  = distCodes.matchingCodes(distCode);

			//gl code is in allocation report so it can be accessed here
			gl = row.glCode();

			//temporary list and running total for correcting rounding error
			List<OutputRow> tempList = new ArrayList<OutputRow>(); 
			BigDecimal total = BigDecimal.ZERO; 

			//some dist codes are grant numbers -- deal with those here
			if (relevantRows.size() == 0) {

				//program is first digit of grant + 0
				program = distCode.substring(0, 1) + "0"; 
				grant = distCode; 
				loanNum = "9999"; //default loan number
				fas = "1"; //default fas 

				//if amt in allocation file is negative, its a credit -- positive is debit (other is always 0) 
				if (row.getAmount().compareTo(BigDecimal.ZERO) < 0) {
					credit = row.getAmount().multiply(new BigDecimal(-1)); 
					debit = BigDecimal.ZERO.setScale(2); 
				} else {
					debit = row.getAmount(); 
					credit = BigDecimal.ZERO.setScale(2); 
				}

				//nothing gets multiplied here so no need to account for rounding
				report.add(new OutputRow(program, grant, gl, loanNum, fas, debit, credit)); 
			}

			//account for credits in allocation file
			else if (row.getAmount().compareTo(BigDecimal.ZERO) < 0) {

				BigDecimal amount = row.getAmount().abs(); 

				//iterate through the rows with same dist code 
				for (int j = 0; j < relevantRows.size(); j++) {
					program = relevantRows.get(j).getProgram();
					grant = relevantRows.get(j).getGrant();
					loanNum = relevantRows.get(j).getLoanNum(); 
					fas = relevantRows.get(j).getFas();
					debit = BigDecimal.ZERO.setScale(2); 

					//multiply credit by percentages
					credit = relevantRows.get(j).getPercent().multiply(amount).setScale(2, RoundingMode.HALF_UP);

					total = total.add(credit); 
					tempList.add(new OutputRow(program, grant, gl, loanNum, fas, debit, credit)); 
				}

				//account for rounding error
				if (total.compareTo(row.getAmount()) != 0) {
					tempList = adjustCredit(tempList, amount.subtract(total)); 
				}

			}

			//account for debits in allocation file
			else {

				//iterate through rows with correct dist code
				for (int j = 0; j < relevantRows.size(); j++) {
					program = relevantRows.get(j).getProgram();
					grant = relevantRows.get(j).getGrant();
					loanNum = relevantRows.get(j).getLoanNum(); 
					fas = relevantRows.get(j).getFas();
					credit = BigDecimal.ZERO.setScale(2); 

					//calculate debit
					debit = relevantRows.get(j).getPercent().multiply(row.getAmount()).setScale(2, RoundingMode.HALF_UP); 

					total = total.add(debit); 				
					tempList.add(new OutputRow(program, grant, gl, loanNum, fas, debit, credit));
				}

				//account for rounding error 
				if (total.compareTo(row.getAmount()) != 0) {
					tempList = adjustDebit(tempList, row.getAmount().subtract(total)); 
				}
			}

			//add to final report
			report.addAll(tempList); 

		}
	}

	/**
	 * given a list of output rows that correspond to one distribution code, adjust the debit
	 * amount of the largest value to account for the rounding error after multiplying 
	 * @param initList list of output rows from a unique distribution code
	 * @param adjustment rounding error to be corrected
	 * @return adjusted list of output rows to be added to final report
	 */
	public List<OutputRow> adjustDebit(List<OutputRow> initList, BigDecimal adjustment) {
		List<OutputRow> list = initList; 
		int max = 0; 

		//find row with maximum vale 
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getDebit().compareTo(list.get(max).getDebit()) > 0) {
				max = i; 
			}
		}

		//add rounding error (might be negative) to the max value
		list.get(max).addDebit(adjustment);

		return list; 
	}

	/**
	 * given a list of output rows that correspond to one distribution code, adjust the credit amount
	 * of the largest value to account for the rounding error after multiplying
	 * @param initList list of output rows from one distribution code
	 * @param adjustment rounding error to be corrected
	 * @return adjusted list of output rows to be added to final report
	 */
	public List<OutputRow> adjustCredit(List<OutputRow> initList, BigDecimal adjustment) {
		List<OutputRow> list = initList; 
		int max = 0; 

		//find row with the maximum value 
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getCredit().compareTo(list.get(max).getCredit()) > 0) {
				max = i;
			}
		}

		//add the rounding error (might be negative) to the row with max value
		list.get(max).addCredit(adjustment);

		return list; 
	}

	/**
	 * determines the number of rows in the output report
	 * @return number of rows in output table
	 */
	public int size() {
		return report.size(); 
	}

	/**
	 * access a given row of the output report
	 * @param i index of row to be returned
	 * @return output row
	 */
	public OutputRow get(int i) {
		return report.get(i); 
	}

	@Override
	/**
	 * String representation of the report
	 */
	public String toString() { 
		String str = "";
		for (int i = 0; i < report.size(); i++) {
			str += report.get(i) + "\n";
		}
		return str; 
	}
}
