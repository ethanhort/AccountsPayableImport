package accountsPayable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;  
import org.apache.poi.ss.usermodel.Workbook;  
import org.apache.poi.xssf.usermodel.XSSFWorkbook;  

public class AllocationReport {
	
	private List<AllocationRow> report; 
	
	public AllocationReport (String filepath, int startIndex, int amtIndex, int distIndex, int glIndex) {
		report = new ArrayList<AllocationRow>(); 
		
		try {
			Workbook wb = null; 
			FileInputStream fis = new FileInputStream(filepath); 
			wb = new XSSFWorkbook(fis); 
			Sheet sheet = wb.getSheetAt(0);
			
			for (int i = startIndex; i <= sheet.getLastRowNum(); i++) {
				String distCode, glCode; 
				BigDecimal amount; 
				
				if (sheet.getRow(i) != null && sheet.getRow(i).getCell(amtIndex) != null && sheet.getRow(i).getCell(amtIndex).getNumericCellValue() != 0.0) {
					amount = new BigDecimal(sheet.getRow(i).getCell(amtIndex).getNumericCellValue());
					amount = amount.setScale(2, RoundingMode.HALF_UP);
					if (sheet.getRow(i).getCell(distIndex).getCellType() == CellType.STRING)  {
						distCode = sheet.getRow(i).getCell(distIndex).getStringCellValue().toUpperCase(); 						
					} else {
						distCode = String.valueOf(sheet.getRow(i).getCell(distIndex).getNumericCellValue());
						distCode = distCode.substring(0, distCode.indexOf(".")); 
					}
					glCode = String.valueOf(sheet.getRow(i).getCell(glIndex).getNumericCellValue()).substring(0, 4);
					report.add(new AllocationRow(amount, distCode, glCode)); 
				}
			}
			
			wb.close();
			
		} catch (FileNotFoundException e) {
			UIHandler.handleError("Cannot find file containing distributuion codes at specified location");
		} catch (IOException e) {
			UIHandler.handleError("Something bad happened. Please try again.");
		}
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
