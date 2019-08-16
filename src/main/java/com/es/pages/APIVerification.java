package com.es.pages;

import static com.jayway.restassured.RestAssured.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.accessibility.AccessibleAction;
import javax.swing.Action;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.MalformedURLException;

import com.es.DBUtils.APICommon;
import com.es.DBUtils.DBConfiguration;
import com.es.Utilities.DataGeneration;
import com.es.Utilities.DataProperty;
import com.es.Utilities.Excel;
import com.es.Utilities.Reporting;
import com.es.configuration.Keywords;
import com.es.configuration.ReadPropertiesFile;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Headers;
import com.jayway.restassured.response.Response;


public class APIVerification {

	public static DataGeneration dataGenerate = new DataGeneration();
	public DBConfiguration db = new DBConfiguration();
	public Reporting logger = new Reporting();
	private ReadPropertiesFile read = new ReadPropertiesFile();
	private String API_User = read.readRunProperties("API_USER").trim();
	private String API_Password = read.readRunProperties("API_PASSWORD").trim();
	private String API_Url = read.readRunProperties("API_URL").trim();
	private DataProperty dataProp = new DataProperty();
	private Keywords actions = new Keywords();
	private APICommon api = new APICommon();
	public CommonMethods common = new CommonMethods();

	public void API_GetProfileAndVerify(String dataKey, String extraParam) {
		logger.logInfo("API Get and Verify Add Profile Method");
		int passFlag = 0;
		String Key = dataKey;
		if (dataKey.contains(".")) {
			Key = dataKey.split("\\.")[1];
		}
		Headers apiheader = api.getHeaders(dataKey);
		String sURL = dataGenerate.randomDataGenerator(Excel.getApiHeaderData(dataKey, "URL"), actions.getMethodName());
		String VerifyWith = Excel.getApiHeaderData(dataKey, "VerifyWith");
		try {
			Response response = (Response) given().headers(apiheader).when().get(new URL(sURL));
			logger.logPass("Obtained response : " + response.asString(), "N");
			api.writeResponseToData(response, Key);
			// Get the two comparision into the Map
			Map<Object, Object> postMap = dataProp.getToMap("input" + VerifyWith.toLowerCase());
			Map<Object, Object> getMap = dataProp.getToMap(Key.toLowerCase());
			for (Object pm : postMap.keySet()) {
				if (getMap.containsKey(pm)) {
					if (getMap.get(pm).toString().equalsIgnoreCase(postMap.get(pm).toString())) {
						logger.logPass("Compare Value. Posted Value: " + postMap.get(pm).toString() + " Get Value: "
								+ getMap.get(pm).toString() + " for the key " + pm, "N");
						passFlag += 1;
					} else {
						logger.logFail("Failed to compare. Posted Value: " + postMap.get(pm).toString() + " Get Value: "
								+ getMap.get(pm).toString() + " for the key " + pm, "N");
					}
				} else {
					logger.logFail("Obtained Response doesnot have attribute " + pm.toString().toUpperCase(), "N");
				}
			}
			if (passFlag == postMap.keySet().size()) {
				logger.logPass("Verified " + VerifyWith + " with get call successfully", "N");
			} else {
				logger.logFail("Verification failed for " + VerifyWith + " with get call", "N");
			}

		} catch (Exception e) {
			logger.logFail("Failed to Post " + e.getMessage(), "N");
		}
	}

	public void API_GetMergeAndVerify(String dataKey, String extraParam) {
		logger.logInfo("API Get and Verify Merge Method");
		Headers apiheader = api.getHeaders(dataKey);
		String sURL = dataGenerate.randomDataGenerator(Excel.getApiHeaderData(dataKey, "URL"), actions.getMethodName());
		try {
			Response response = (Response) given().headers(apiheader).when().get(new URL(sURL));
			logger.logPass("Obtained response : " + response.asString(), "N");
			api.writeResponseToData(response, actions.getMethodName());
			verifyAttributes(dataKey,actions.getMethodName());
		}

		catch (Exception e) {
			logger.logFail("Failed to Post " + e.getMessage(), "N");
		}
	}

	public  void verifyAttributes(String dataKey, String methodName) {
		XSSFSheet ExcelWSheet;
		String SheetName = "";
		Object[] header = null;
		Object[] dataForHeader = null;
		String Key = dataKey;
		if (dataKey.contains(".")) {
			SheetName = dataKey.split("\\.")[0];
			Key = dataKey.split("\\.")[1];
			ExcelWSheet = Excel.getDataWorkBook().getSheet(SheetName);
			try {

				for (CellRangeAddress range : ExcelWSheet.getMergedRegions()) {
					int firstRowValue = range.getFirstRow();
					int firstColumn = range.getFirstColumn();
					int lastRowValue = range.getLastRow();
					if (firstColumn == 0 && Key.equalsIgnoreCase(Excel.getCellData(ExcelWSheet, firstRowValue, 0))) {
						int columnNumber = ExcelWSheet.getRow(firstRowValue + 2).getLastCellNum();
						header =new Object[columnNumber];
						dataForHeader =new Object[columnNumber];
						{
							for(int j = firstColumn + 3,k=0; j < columnNumber; j++) {
								String attribute = null;
								String attributevalue = null;
								try {
									attribute=Excel.getCellData(ExcelWSheet, firstRowValue + 2, j).trim();
									if (attribute != null && !attribute.equals(""))
									{
										attributevalue = Excel.getCellData(ExcelWSheet, firstRowValue + 3,j).trim();
										header[k]=dataGenerate.randomDataGenerator("get("+methodName+"."+attribute+")",methodName);
										dataForHeader[k]=dataGenerate.randomDataGenerator(attributevalue, methodName);
										k++;
									}
								} catch (Exception e) {
									logger.logFail( attribute+" Data is not present in data properties ", "N");
								}
							}break;
						}
					}
				} 
				Object [] headerList=actions.removeNullValues(header);
				Arrays.sort(headerList);
				Object [] dataForHeaderList=actions.removeNullValues(dataForHeader);
				Arrays.sort(dataForHeaderList);
				if(Arrays.equals(headerList, dataForHeaderList))
				{
					logger.logPass("Array comparission of data is passed:"+Arrays.toString(headerList), "N");
					logger.logPass("Array comparission of data is passed"+Arrays.toString(dataForHeaderList), "N");
				}
				else
				{
					logger.logFail("Arrays comparission of data failed"+Arrays.toString(headerList), "N");
					logger.logFail("Arrays comparission of data failed"+Arrays.toString(dataForHeaderList), "N");

				}
			}catch (Exception e) {
				System.out.println(e);
			}

		}

	}
}
