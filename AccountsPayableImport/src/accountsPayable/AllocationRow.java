package accountsPayable;

import java.math.BigDecimal;

/**
 * representation of one row of a pre allocated file for use in this application. 
 * each row must contain a dollar amount, a distribution code, and a general ledger code
 * 
 * Class performs no calculations -- exists solely as a data structure for easy storage and accessibility. 
 * @author ethan horton
 *
 */
public class AllocationRow {
	
	private BigDecimal amount;
	private String distCode, glCode; 
	
	/**
	 * save parameters as instance variables so that they may be returned later
	 * @param amount
	 * @param distCode
	 * @param glCode
	 */
	public AllocationRow(BigDecimal amount, String distCode, String glCode) {
		this.amount = amount;
		this.distCode = distCode;
		this.glCode = glCode; 
	}
	
	
	/**
	 * methods access various instance variables associated with this row
	 * @return 
	 */
	public BigDecimal getAmount() {
		return amount;
	}
	
	public String distCode() {
		return distCode;
	}
	
	public String glCode() {
		return glCode; 
	}
	
	@Override
	/**
	 * string representation of one row of this report
	 */
	public String toString() {
		return "Amount: " + amount + ", DistCode: " + distCode + ", GLCode: " + glCode; 
	}
}
