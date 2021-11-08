package accountsPayable;

import java.math.BigDecimal;

public class DistCodeRow {
	String distCode, program, grant, loanNum, fas; 
	BigDecimal percent; 
	
	public DistCodeRow(String distCode, String program, String grant, String loanNum, String fas, String percent) {
		this.distCode = distCode; 
		this.program = program; 
		this.grant = grant; 
		this.loanNum = loanNum; 
		this.fas = fas; 
		this.percent = BigDecimal.valueOf(Double.valueOf(percent.replace("%", "")));  
		this.percent = this.percent.divide(new BigDecimal(100)); 
	}
	
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
	public String toString() {
		return "DistCode: " + distCode + ", Program: " + program + ", Grant: " + grant + ", LoanNum: " + loanNum + ", fas: " + fas + ", Percent: " + percent; 
	}
}

