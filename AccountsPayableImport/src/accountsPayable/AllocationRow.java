package accountsPayable;

import java.math.BigDecimal;

public class AllocationRow {
	
	private BigDecimal amount;
	private String distCode, glCode; 
	
	public AllocationRow(BigDecimal amount, String distCode, String glCode) {
		this.amount = amount;
		this.distCode = distCode;
		this.glCode = glCode; 
	}
	
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
	public String toString() {
		return "Amount: " + amount + ", DistCode: " + distCode + ", GLCode: " + glCode; 
	}
}
