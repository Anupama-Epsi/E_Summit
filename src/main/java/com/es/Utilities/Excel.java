package com.es.Utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.es.configuration.ReadPropertiesFile;

public class Excel {
	private static ReadPropertiesFile read =  new ReadPropertiesFile();
	static String testCaseFile = read.readRunProperties("TestCaseFileName").trim();
	static String filePath;
	public static Reporting logger = new Reporting();

	public static XSSFWorkbook getDataWorkBook() {
		XSSFWorkbook ExcelWBook = null;
		FileInputStream ExcelFile = null;
		if(testCaseFile!=null&& testCaseFile.contains("xls"))
			filePath = System.getProperty("user.dir")+"\\"+testCaseFile;
		else if (testCaseFile!=null) {
			filePath = System.getProperty("user.dir")+"\\"+testCaseFile+".xlsx";
			try {
				ExcelFile = new FileInputStream(filePath);
			}catch(Exception e) {
				filePath = System.getProperty("user.dir")+"\\"+testCaseFile+".xlsm";
				try {
					ExcelFile = new FileInputStream(filePath);
				}catch(Exception e1) {
					System.out.println("File specified in the Run.properties is not available in the path");
				}
			}
		}
		else
			logger.logFail("Unspecified TestCase File Name in Run.properties file");
		try {
			ExcelFile = new FileInputStream(filePath);
			ExcelWBook = new XSSFWorkbook(ExcelFile);
		} catch (IOException e) {
			System.out.println("Failed to get the workbook mentioned in the path due to exception : "+e.getMessage());
		}
		return ExcelWBook;
	}

	public static XSSFWorkbook getLocatorWorkBook() {
		XSSFWorkbook ExcelWBook = null;

		String filePath = System.getProperty("user.dir")+"\\LocatorsMap.xlsx";
		try {
			FileInputStream ExcelFile = new FileInputStream(filePath);
			ExcelWBook = new XSSFWorkbook(ExcelFile);
		} catch (IOException e) {
			System.out.println("Failed to get the workbook mentioned in the path due to exception : "+e.getMessage());
		}
		return ExcelWBook;
	}

	public static String[] getData(String key) {  
		String SheetName = "TestData";

		String[] tabArray = null ;
		XSSFSheet ExcelWSheet;
		try {
			String Key = key;
			if(key.contains(".")) {
				String[] test = key.split("\\.");
				SheetName = key.split("\\.")[0];
				Key = key.split("\\.")[1];
			}
			ExcelWSheet = getDataWorkBook().getSheet(SheetName);
			//System.out.println("Data Cell number : "+ExcelWSheet.getRow(5).getLastCellNum());
			int startRow = 1;
			int startCol = 1;
			int totalRows = ExcelWSheet.getLastRowNum();
			int tb = 0;
			String rowValue = null;
			for (int i=startRow;i<=totalRows;i++) {   
				rowValue = getCellData(ExcelWSheet,i,0).trim();
				if(rowValue.trim().equalsIgnoreCase(Key)&&(rowValue.trim()!=null)) {
					int columnNumber = ExcelWSheet.getRow(i).getLastCellNum();
					tabArray= new String[columnNumber];
					for (int j=startCol;j<columnNumber;j++) {
						String tempVariable = getCellData(ExcelWSheet,i,j).trim();
						if(tempVariable==null||tempVariable=="") {
							tabArray[tb] = null;
						}
						else {
							tabArray[tb] = tempVariable;
						}
						tempVariable=null;
						tb++;
					}
				}
			}
		}
		catch (Exception e){
			System.out.println("Could not read the Excel sheet due to exception "+e.getMessage());
		}
		return tabArray;
	}

	public static String[] getLocatorData(String key, String methodName) {  
		String SheetName = "TestData";
		String Key = methodName;
		if(key.contains(".")) {
			SheetName = key.split("\\.")[0];
		} 
		String[] tabArray = null ;
		XSSFSheet ExcelWSheet;
		try {
			ExcelWSheet = getDataWorkBook().getSheet(SheetName);
			//System.out.println("Locator Cell number : "+ExcelWSheet.getRow(5).getLastCellNum());
			int startRow = 1;
			int startCol = 1;
			int totalRows = ExcelWSheet.getLastRowNum();
			int tb = 0;
			String rowValue = null;
			for (int i=startRow;i<=totalRows;i++) {   
				rowValue = getCellData(ExcelWSheet,i,0).trim();
				if(rowValue.trim().equalsIgnoreCase(Key)&&(rowValue.trim()!=null)) {
					int columnNumber = ExcelWSheet.getRow(i).getLastCellNum();
					tabArray= new String[columnNumber];
					for (int j=startCol;j<columnNumber;j++) {
						String tempVariable = getCellData(ExcelWSheet,i,j).trim();
						if(tempVariable==null||tempVariable=="") {
							tabArray[tb] = null;
						}
						else {
							tabArray[tb] = tempVariable;
						}
						tempVariable=null;
						tb++;
					}
				}
			}
		}
		catch (Exception e){
			System.out.println("Could not read the Excel sheet due to exception "+e.getMessage());
		}
		return tabArray;
	}

	public static String getCellData(XSSFSheet ExcelWSheet,int RowNum, int ColNum) throws Exception {	
		String CellData = "";
		String CellString = null;
		try{
			XSSFCell Cell = ExcelWSheet.getRow(RowNum).getCell(ColNum);

			try {
				CellData = Cell.getStringCellValue();
			}catch(Exception e) {
				long CellIntData = (long)Cell.getNumericCellValue();
				CellString = String.valueOf(CellIntData);
				CellData = CellString;
			}
			if(CellData==null||CellData=="") {
				CellData="";
			}
		}catch (Exception e){
			if(CellData==null||CellData=="") {
				CellData="";
			}else {
				System.out.println("Failed to get Cell Data due to exception "+e.getMessage());
				throw (e);
			}
		}
		return CellData;
	}

	public String[] getTestCaseName(String runnerSheet) {		
		XSSFSheet RunnerSheet = getDataWorkBook().getSheet(runnerSheet);
		int totalRowCount=RunnerSheet.getLastRowNum();
		String[] TestCaseNames = new String[totalRowCount];
		String tempVariable = null;
		int ta = 0;
		try {
			int startRow = 0;
			int totalRows = RunnerSheet.getLastRowNum();
			for (int i=startRow;i<totalRows;i++) { 
				String rowValue = getCellData(RunnerSheet,i,0);
				if(rowValue.contains("TestCaseName")) {
					tempVariable = rowValue.split(":")[1].replace(" ", "");
					TestCaseNames[ta] = tempVariable;
					ta++;
					tempVariable=null;
				}
			}
		}catch(Exception e) {
			System.out.println("Failed to get Testcase name from the excel Runner due to exception "+e.getMessage());
			e.printStackTrace();
		}
		return TestCaseNames;
	}

	public String[] getTestSteps(String runnerSheet,String testCaseName) {
		String[] TestSteps = new String [25];

		XSSFSheet RunnerSheet = getDataWorkBook().getSheet(runnerSheet);
		int ta = 0;
		try {
			int firstRow = getFirstRowNumberOfTestStep(runnerSheet,testCaseName);
			int lastRow = getLastRowNumberOfTestStep(runnerSheet,firstRow);
			for (int i=firstRow+1;i<=lastRow;i++) { 
				if(getCellData(RunnerSheet,i,1).contains("Y")&&!getCellData(RunnerSheet,i,0).contains("TestCaseName")) {
					String ScreenShot_Flag;
					String TestCaseParameter;
					if(getCellData(RunnerSheet,i,3).equals(null)||getCellData(RunnerSheet,i,3).equals("")) {
						ScreenShot_Flag = "N";
					}else {
						ScreenShot_Flag = getCellData(RunnerSheet,i,3);
					}
					if(getCellData(RunnerSheet,i,2).equals(null)||getCellData(RunnerSheet,i,2).equals("")) {
						TestCaseParameter = "null";
					}else {
						TestCaseParameter = getCellData(RunnerSheet,i,2);
					}
					TestSteps[ta] = getCellData(RunnerSheet,i,0)+","+TestCaseParameter+","+ScreenShot_Flag;
					ta++;
				}
			}
		}catch(Exception e) {
			System.out.println("Failed to get Test Steps due to exception "+e.getMessage());
		}
		return TestSteps;
	}

	public int getFirstRowNumberOfTestStep(String runnerSheet, String testCaseName) {
		XSSFSheet RunnerSheet = getDataWorkBook().getSheet(runnerSheet);
		int startRow = 0;
		int firstRowNum = 0; 
		int totalRows = RunnerSheet.getLastRowNum();
		try {
			for (int i=startRow;i<=totalRows;i++) { 
				String rowValue;
				rowValue = getCellData(RunnerSheet,i,0);
				String[] rowVal = rowValue.split(":");
				if(rowVal[0].trim().equalsIgnoreCase("TestCaseName") 
						&& rowVal[1].trim().equals(testCaseName.trim())) {
					firstRowNum= i;
					break;
				}
			}
		} catch (Exception e) {
			System.out.println("Failed to get row number due to exception "+e.getMessage());
		}
		return firstRowNum;
	}

	public int getLastRowNumberOfTestStep(String runnerSheet, int fromNumber) {
		XSSFSheet RunnerSheet = getDataWorkBook().getSheet(runnerSheet);
		int startRow = fromNumber+1;
		int lastRowNum = 0; 
		int totalRows = RunnerSheet.getLastRowNum();
		try {
			for (int i=startRow;i<=totalRows;i++) { 
				String rowValue;
				rowValue = getCellData(RunnerSheet,i,0);
				if(rowValue.contains("TestCaseName")) {
					lastRowNum= i;
					break;
				}
			}
			if(lastRowNum==0) {
				lastRowNum = totalRows;
			}
		} catch (Exception e) {
			System.out.println("Failed to get row number due to exception "+e.getMessage());
		}
		return lastRowNum;
	}

	public static String getLocator(String locatorKey) {  
		String locator = null;
		XSSFSheet ExcelWSheet;
		try {
			ExcelWSheet = getLocatorWorkBook().getSheet("Locators");
			int startRow = 1;
			int totalRows = ExcelWSheet.getLastRowNum();
			String rowValue = null;
			for (int i=startRow;i<=totalRows;i++) {   
				rowValue = getCellData(ExcelWSheet,i,0);
				if(rowValue.trim().equals(locatorKey)) {
					locator = getCellData(ExcelWSheet,i,1);
					break;
				}
			}
		}
		catch (Exception e){
			System.out.println("Could not read the Excel sheet due to exception "+e.getMessage());
		}
		return locator;
	}

	public static String getQuery(String queryNameLinkedInExcel) {
		String query = null;
		XSSFSheet ExcelWSheet;
		try {
			ExcelWSheet = getLocatorWorkBook().getSheet("Query");
			int startRow = 1;
			int totalRows = ExcelWSheet.getLastRowNum();
			String rowValue = null;
			for (int i=startRow;i<=totalRows;i++) {   
				rowValue = getCellData(ExcelWSheet,i,0);
				if(rowValue.trim().equalsIgnoreCase(queryNameLinkedInExcel)) {
					query = getCellData(ExcelWSheet,i,1);
					break;
				}
			}
		}
		catch (Exception e){
			System.out.println("Could not read the Excel sheet due to exception "+e.getMessage());
		}
		return query;

	}

	public static String getApiCalls(String queryNameLinkedInExcel) {
		String query = null;
		XSSFSheet ExcelWSheet;
		try {
			ExcelWSheet = getDataWorkBook().getSheet("APICalls");
			int startRow = 1;
			int totalRows = ExcelWSheet.getLastRowNum();
			String rowValue = null;
			for (int i=startRow;i<=totalRows;i++) {   
				rowValue = getCellData(ExcelWSheet,i,0);
				if(rowValue.trim().equalsIgnoreCase(queryNameLinkedInExcel)) {
					query = getCellData(ExcelWSheet,i,1);
					break;
				}
			}
		}
		catch (Exception e){
			System.out.println("Could not read the Excel sheet due to exception "+e.getMessage());
		}
		return query;

	}

	public static String getApiHeaderData(String key,String headerName) {  
		String SheetName = "TestData";

		String returnData = "" ;
		XSSFSheet ExcelWSheet;
		try {
			String Key = key;
			if(key.contains(".")) {
				String[] test = key.split("\\.");
				SheetName = key.split("\\.")[0];
				Key = key.split("\\.")[1];
			}
			ExcelWSheet = getDataWorkBook().getSheet(SheetName);
			int startRow = 1;
			int startCol = 1;
			int totalRows = ExcelWSheet.getLastRowNum();
			int tb = 0;
			String rowValue = null;
			for (int i=startRow;i<=totalRows;i++) {   
				rowValue = getCellData(ExcelWSheet,i,0).trim();
				if(rowValue.trim().equalsIgnoreCase(Key)&&(rowValue.trim()!=null)) {
					int columnNumber = ExcelWSheet.getRow(i).getLastCellNum();
					for (int j=startCol;j<columnNumber;j++) {
						String tempVariable = getCellData(ExcelWSheet,i,j).trim();
						if(tempVariable.equalsIgnoreCase(headerName)) {
							returnData = getCellData(ExcelWSheet,i+1,j).trim();
							break;
						}

					}
				}
			}
		}
		catch (Exception e){
			System.out.println("Could not read the Excel sheet due to exception "+e.getMessage());
		}
		return returnData;
	}

	public static String[] getSheetName(String sheetContains) {
		int sheetcount = getDataWorkBook().getNumberOfSheets();
		String[] Sheetnames = new String[sheetcount];
		String[] tempArray = new String[sheetcount];
		try {
			for (int i = 0; i <sheetcount ; i++) {
				tempArray[i]=getDataWorkBook().getSheetName(i);
			}
			for (int i = 0; i < sheetcount; i++) {
				if(tempArray[i].startsWith(sheetContains)) {
					Sheetnames[i]=tempArray[i];
				}
			}			
		} catch (Exception e) {
			System.out.println("Could not get SheetNames form the ExcelWorkBook sheet due to exception "+e.getMessage());
		}
		return Sheetnames;
	}
	
	public static Map<String, String> getApiHeader(String key) {  
		String SheetName = "TestData";

		Map<String, String> returnData = new HashMap<String, String>() ;
		XSSFSheet ExcelWSheet;
		try {
			String Key = key;
			if(key.contains(".")) {
				SheetName = key.split("\\.")[0];
				Key = key.split("\\.")[1];
			}
			ExcelWSheet = getDataWorkBook().getSheet(SheetName);
			int startRow = 1;
			int startCol = 1;
			int totalRows = ExcelWSheet.getLastRowNum();
			String rowValue = null;
			for (int i=startRow;i<=totalRows;i++) {   
				rowValue = getCellData(ExcelWSheet,i,0).trim();
				if(rowValue.trim().equalsIgnoreCase(Key)&&(rowValue.trim()!=null)) {
					int columnNumber = ExcelWSheet.getRow(i).getLastCellNum();
					for (int j=startCol;j<columnNumber;j++) {
						String header = getCellData(ExcelWSheet,i,j).trim();
						if(header!=null || !header.equals("")) {
							String dataForHeader = getCellData(ExcelWSheet,i+1,j).trim();
							returnData.put(header, dataForHeader);
						}
					}
				}
			}
		}
		catch (Exception e){
			System.out.println("Could not read the Excel sheet due to exception "+e.getMessage());
		}
		return returnData;
	}
}



