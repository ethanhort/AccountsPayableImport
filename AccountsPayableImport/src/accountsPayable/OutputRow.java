package accountsPayable;

import java.math.BigDecimal;

public class OutputRow {

	private String program, grant, gl, loanNum, fas;
	private BigDecimal debit, credit; 
	
	public OutputRow (String program, String grant, String gl, String loanNum, String fas, BigDecimal debit, BigDecimal credit) {
		this.program = program;
		this.grant = grant;
		this.gl = gl; 
		this.loanNum = loanNum;
		this.fas = fas; 
		this.debit = debit; 
		this.credit = credit; 
	}
	
	public void addDebit(BigDecimal arg) {
		debit = debit.add(arg);
	}
	
	public void addCredit(BigDecimal arg) {
		credit = credit.add(arg); 
	}
	
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
	public String toString() {
		return "Program: " + program + ", Grant: " + grant + ", GL: " + gl + ", LoanNum: " + loanNum + ", FAS: " + fas + ", Debit: " + debit + ", Credit: " + credit;
	}
}
