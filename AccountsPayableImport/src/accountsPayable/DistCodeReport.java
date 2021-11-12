package accountsPayable;

import java.util.ArrayList;
import java.util.List; 
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import org.apache.poi.ss.usermodel.Sheet;  
import org.apache.poi.ss.usermodel.Workbook;  
import org.apache.poi.xssf.usermodel.XSSFWorkbook;  

/**
 * representation of the distribution code report excel file. Serves as a data structure for storage and ease of access.
 * @author Ethan Horton
 *
 */
public class DistCodeReport {
	private List<DistCodeRow> report; 
	private int startIndex; 

	/**
	 * fundamentally just an array list with a few added features
	 * @param filepath local file path of dist code file
	 * @param startIndex line number (0-indexed) of first line of useful data
	 * @param distCodeIndex column number (0-indexed) of distribution code string
	 * @param programIndex column number ('') of program string
	 * @param grantIndex column number ('') of grant string
	 * @param loanIndex column number ('') of loan number string
	 * @param fasIndex column number ('') of fas string
	 * @param percentIndex column number ('') of percent string
	 */
	public DistCodeReport(String filepath, int startIndex, int distCodeIndex, int programIndex, int grantIndex, int loanIndex, int fasIndex, int percentIndex) {
		report = new ArrayList<DistCodeRow>(); 
		this.startIndex = startIndex; 

		try {

			//create apache poi representation of spreadsheet
			Workbook wb = null; 
			FileInputStream fis = new FileInputStream(filepath); 
			wb = new XSSFWorkbook(fis); 
			Sheet sheet = wb.getSheetAt(0); 

			//fill array list with data from excel sheet
			populateReport(sheet, startIndex, distCodeIndex, programIndex, grantIndex, loanIndex, fasIndex, percentIndex); 

			wb.close();

		} catch (FileNotFoundException e) {
			UIHandler.handleError("Cannot find file containing distributuion codes at specified location");
		} catch (IOException e) {
			UIHandler.handleError("Something bad happened. Please try again.");
		}
	}

	/**
	 * method iterates line by line through the excel spreadsheet pulling the program, dist code, grant, loan, etc. from each line to create
	 * the application's report representation
	 * @param sheet poi excel spreadsheet
	 * @param startIndex line number (0-indexed) of first line of useful data
	 * @param distCodeIndex column number (0-indexed) of distribution code string
	 * @param programIndex column number ('') of program string
	 * @param grantIndex column number ('') of grant string
	 * @param loanIndex column number ('') of loan number string
	 * @param fasIndex column number ('') of fas string
	 * @param percentIndex column number ('') of percent string
	 */
	public void populateReport(Sheet sheet, int startIndex, int distCodeIndex, int programIndex, int grantIndex, int loanIndex, int fasIndex, int percentIndex) {
		for (int i = startIndex; i < sheet.getLastRowNum(); i++) {
			String distCode, program, grant, loan, fas, percent;
			if (sheet.getRow(i) != null && sheet.getRow(i).getCell(programIndex) != null && !sheet.getRow(i).getCell(programIndex).getStringCellValue().trim().equals("")) {
				distCode = sheet.getRow(i).getCell(distCodeIndex).getStringCellValue().toUpperCase(); 
				program = sheet.getRow(i).getCell(programIndex).getStringCellValue(); 
				grant = sheet.getRow(i).getCell(grantIndex).getStringCellValue(); 
				loan = sheet.getRow(i).getCell(loanIndex).getStringCellValue(); 
				fas = sheet.getRow(i).getCell(fasIndex).getStringCellValue(); 
				percent = sheet.getRow(i).getCell(percentIndex).getStringCellValue(); 
				report.add(new DistCodeRow(distCode, program, grant, loan, fas, percent)); 
			}
		}
	}

	/**
	 * determines number of rows in the dist code report representation
	 * @return size of report array list
	 */
	public int size() {
		return report.size(); 
	}

	/**
	 * determines the index of the first line of useful data in excel spreadsheet (don't actually need this. not sure why I wrote it) 
	 * @return index of first line of useful data
	 */
	public int getStart() {
		return startIndex; 
	}

	/**
	 * accesses the specified row of the dist code report
	 * @param i index of row to be returned
	 * @return row of dist code report
	 */
	public DistCodeRow get(int i) {
		return report.get(i); 
	}

	/**
	 * given a distribution code, return all lines from distribution code file containing that code
	 * @param distCode distribution code to be found
	 * @return list of lines from distribution code file matching given distribution code 
	 */
	public ArrayList<DistCodeRow> matchingCodes(String distCode) {
		ArrayList<DistCodeRow> list = new ArrayList<DistCodeRow>(); 

		//iterate through report, saving each line with a matching dist code
		for (int i = 0; i < report.size(); i++) {
			if (report.get(i).getDistCode().equals(distCode)) {
				list.add(report.get(i)); 
			}
		}

		return list; 
	}

	/**
	 * determines the number of unique codes in the distribution code report.
	 * for use in determining how many ways to split total dollar amount when allocating equally
	 * @return number of unique codes in report
	 */
	public int countCodes() {
		String currCode; 
		int count = 1; 
		currCode = report.get(0).getDistCode(); 
		for (int i = 0; i < report.size(); i++) {
			if (!report.get(i).getDistCode().equals(currCode)) {
				currCode = report.get(i).getDistCode();
				count++; 
			}
		}

		return count; 
	}


	@Override
	/**
	 * string representation of distribution code report
	 */
	public String toString() {
		String str = "";
		for (int i = 0; i < report.size(); i++) {
			str += report.get(i) + "\n";
		}
		return str; 
	}

}
