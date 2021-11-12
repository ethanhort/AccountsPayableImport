package accountsPayable;

import java.math.BigDecimal;

/**
 * Representation of one row of the output report.
 * output row consists of a program string, grant string, gl string, loan number string, fas string, debit dollar value, and credit dollar value
 * 
 * class performs no calculation -- serves only as data structure that provides storage and ease of access 
 * @author Ethan Horton
 *
 */
public class OutputRow {

	private String program, grant, gl, loanNum, fas;
	private BigDecimal debit, credit; 

	/**
	 * save parameters as instance variables
	 * @param program program string from distribution code file
	 * @param grant grant string from distribution code file 
	 * @param gl gl string from pre allocated file or UI
	 * @param loanNum loan number from distribution code file
	 * @param fas fas string from distribution code file
	 * @param debit dollar amount calculated from dist code percentages
	 * @param credit dollar amount calculated from dist code percentages
	 */
	public OutputRow (String program, String grant, String gl, String loanNum, String fas, BigDecimal debit, BigDecimal credit) {
		this.program = program;
		this.grant = grant;
		this.gl = gl; 
		this.loanNum = loanNum;
		this.fas = fas; 
		this.debit = debit; 
		this.credit = credit; 
	}

	/**
	 * method allows for adjustment of the debit field
	 * @param arg amount to adjust debit by
	 */
	public void addDebit(BigDecimal arg) {
		debit = debit.add(arg);
	}

	/**
	 * method allows for adjustment of the credit field
	 * @param arg amount to adjust credit by
	 */
	public void addCredit(BigDecimal arg) {
		credit = credit.add(arg); 
	}

	/**
	 * methods to access individual instance variables of the output row
	 * @return
	 */
	public String getProgram() {
		return program;
	}

	public String getGrant() {
		return grant; 
	}

	public String getGL() {
		return gl; 
	}

	public String getLoanNum() {
		return loanNum;
	}

	public String getFas() {
		return fas;
	}

	public BigDecimal getDebit() {
		return debit;
	}

	public BigDecimal getCredit() {
		return credit; 
	}

	@Override 
	/**
	 * String representation of one row of the output report
	 */
	public String toString() {
		return "Program: " + program + ", Grant: " + grant + ", GL: " + gl + ", LoanNum: " + loanNum + ", FAS: " + fas + ", Debit: " + debit + ", Credit: " + credit;
	}
}
