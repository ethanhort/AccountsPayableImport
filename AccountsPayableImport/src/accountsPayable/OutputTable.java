package accountsPayable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList; 
import java.util.List; 

public class OutputTable {
	
	private List<OutputRow> report;
	private DistCodeReport distCodes; 
	private AllocationReport allocation;
	private String glCode; 
	
	public OutputTable(DistCodeReport distCodes, AllocationReport allocation) {
		report = new ArrayList<OutputRow>();
		this.allocation = allocation; 
		this.distCodes = distCodes; 
	}
	
	public OutputTable(DistCodeReport distCodes, BigDecimal amt, String glCode) {
		report = new ArrayList<OutputRow>(); 
		this.distCodes = distCodes;
		this.glCode = glCode; 
		
		int numCodes = distCodes.countCodes();
		BigDecimal amtPerCode = amt.divide(new BigDecimal(numCodes), 2, RoundingMode.HALF_UP); 
		BigDecimal difference = amt.subtract(amtPerCode.multiply(new BigDecimal(numCodes))); 
		
		populateAllocatedReport(amtPerCode, difference); 
		
	}
	
	public void populateAllocatedReport(BigDecimal amtPerCode, BigDecimal difference) {
		BigDecimal tempTotal = amtPerCode.add(difference); 
		int index = 0;
		String curr = distCodes.get(index).getDistCode(); 
		BigDecimal total = new BigDecimal(0); 
		
		List<OutputRow> tempList = new ArrayList<OutputRow>(); 
		
		while (distCodes.get(index).getDistCode().equals(curr)) {
			String program, grant, gl, loanNum, fas; 
			BigDecimal debit, credit; 
			
			//static information pulled from distribution code file
			program = distCodes.get(index).getProgram(); 
			grant = distCodes.get(index).getGrant();
			gl = glCode; 
			loanNum = distCodes.get(index).getLoanNum();
			fas = distCodes.get(index).getFas(); 
			
			debit = tempTotal.multiply(distCodes.get(index).getPercent()).setScale(2, RoundingMode.HALF_UP);
			credit = BigDecimal.ZERO.setScale(2);  
			
			tempList.add(new OutputRow(program, grant, gl, loanNum, fas, debit, credit)); 
			total = total.add(debit); 
			index++; 
		}
		
		if (total.compareTo(tempTotal) != 0) {
			tempList = adjustDebit(tempList, tempTotal.subtract(total)); 
		}
		
		report.addAll(tempList);
		
		//reset temporary values
		tempList = new ArrayList<OutputRow>(); 
		total = BigDecimal.ZERO; 
		
		curr = distCodes.get(index).getDistCode(); 
		
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
		report.addAll(tempList);
	}
	
	public void populateUnallocatedReport() {
		
	}
	
	public List<OutputRow> adjustDebit(List<OutputRow> initList, BigDecimal adjustment) {
		List<OutputRow> list = initList; 
		int max = 0; 
		
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getDebit().compareTo(list.get(max).getDebit()) > 0) {
				max = i; 
			}
		}
		
		list.get(max).addDebit(adjustment);
		
		return list; 
	}
	
	public int size() {
		return report.size(); 
	}
	
	public OutputRow get(int i) {
		return report.get(i); 
	}
	
	@Override
	public String toString() { 
		String str = "";
		for (int i = 0; i < report.size(); i++) {
			str += report.get(i) + "\n";
		}
		return str; 
	}
}
