package accountsPayable;

import java.math.BigDecimal;

/**
 * data structure that represents one row of the distribution code file. each  line of the file contains a distribution code,
 * program number, grant number, loan number, fas number, and percentage
 * 
 * class performs no calculations -- just exists as a logical representation of a row of this particular report
 * @author ethan horton
 *
 */
public class DistCodeRow {
	String distCode, program, grant, loanNum, fas; 
	BigDecimal percent; 

	/**
	 * save values as instance variables so that they may be accessed at a later time. 
	 * 
	 * does convert percentage from a string into a decimal (and divides by 100 so that it may be used conventionally)
	 * @param distCode
	 * @param program
	 * @param grant
	 * @param loanNum
	 * @param fas
	 * @param percent
	 */
	public DistCodeRow(String distCode, String program, String grant, String loanNum, String fas, String percent) {
		this.distCode = distCode; 
		this.program = program; 
		this.grant = grant; 
		this.loanNum = loanNum; 
		this.fas = fas; 
		this.percent = BigDecimal.valueOf(Double.valueOf(percent.replace("%", "")));  
		this.percent = this.percent.divide(new BigDecimal(100)); 
	}


	/**
	 * methods return the various fields of the data structure when required
	 * @return
	 */
	public String getDistCode() {
		return distCode; 
	}

	public String getProgram() {
		return program; 
	}

	public String getGrant() {
		return grant; 
	}

	public String getLoanNum() {
		return loanNum; 
	}

	public String getFas() {
		return fas; 
	}

	public BigDecimal getPercent() {
		return percent; 
	}

	@Override
	/**
	 * string representation of distribution code row
	 */
	public String toString() {
		return "DistCode: " + distCode + ", Program: " + program + ", Grant: " + grant + ", LoanNum: " + loanNum + ", fas: " + fas + ", Percent: " + percent; 
	}
}

