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

/**
 * representation of the pre allocated file for use when user selects to use a file rather than allocating equally
 * serves as a data structure representing any of several files that can be used with this application
 * 
 * given file must start on row 3 and contain a dollar amount in col A, dist code in col B, and gl number in col C
 * @author Ethan Horton
 *
 */
public class AllocationReport {

	private List<AllocationRow> report; 

	/**
	 * constructor initializes report array list and fills the report with data from the allocated file
	 * @param filepath local file path of allocated file
	 * @param startIndex line number (0-indexed) of first line of useful data
	 * @param amtIndex col number (0-indexed) of dollar amount
	 * @param distIndex col number ('') of distribution code 
	 * @param glIndex col number ('') of gl number
	 */
	public AllocationReport (String filepath, int startIndex, int amtIndex, int distIndex, int glIndex) {
		report = new ArrayList<AllocationRow>(); 

		try {

			//apache poi representation of an excel spreadsheet
			Workbook wb = null; 
			FileInputStream fis = new FileInputStream(filepath); 
			wb = new XSSFWorkbook(fis); 
			Sheet sheet = wb.getSheetAt(0);

			//iterate through excel spreadsheet
			for (int i = startIndex; i <= sheet.getLastRowNum(); i++) {
				String distCode, glCode; 
				BigDecimal amount; 

				//determines if a particular row of the allocated file contains useful information and then extracts data if it is
				if (sheet.getRow(i) != null && sheet.getRow(i).getCell(amtIndex) != null && sheet.getRow(i).getCell(amtIndex).getNumericCellValue() != 0.0) {
					amount = new BigDecimal(sheet.getRow(i).getCell(amtIndex).getNumericCellValue());
					amount = amount.setScale(2, RoundingMode.HALF_UP);

					//dist code might be a string or a number -- conditional sanitizes both to strings
					if (sheet.getRow(i).getCell(distIndex).getCellType() == CellType.STRING)  {
						distCode = sheet.getRow(i).getCell(distIndex).getStringCellValue().toUpperCase(); 						
					} else {
						distCode = String.valueOf(sheet.getRow(i).getCell(distIndex).getNumericCellValue());
						distCode = distCode.substring(0, distCode.indexOf(".")); 
					}

					//gl code is technically a double in excel file so when converted to a string, it has decimal places
					//this removes the decimals before adding it to the report
					glCode = String.valueOf(sheet.getRow(i).getCell(glIndex).getNumericCellValue()).substring(0, 4);
					report.add(new AllocationRow(amount, distCode, glCode)); 
				}
			}

			wb.close();

		} catch (FileNotFoundException e) {
			UIHandler.handleError("Cannot find file containing pre-allocated file at specified location");
		} catch (IOException e) {
			UIHandler.handleError("Something bad happened. Please try again.");
		}
	}

	/**
	 * access a row of the allocation report from a given index
	 * @param i index of row to be returned
	 * @return row of allocation report
	 */
	public AllocationRow get(int i) {
		return report.get(i); 
	}

	/**
	 * determines size of allocation report array list
	 * @return number of items in report array list
	 */
	public int size() {
		return report.size(); 
	}

	@Override
	/**
	 * String representation of the pre allocated excel file
	 */
	public String toString() { 
		String str = "";
		for (int i = 0; i < report.size(); i++) {
			str += report.get(i) + "\n";
		}
		return str; 
	}
}
