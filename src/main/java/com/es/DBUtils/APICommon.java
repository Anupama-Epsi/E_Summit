package com.es.DBUtils;

import static com.jayway.restassured.RestAssured.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.MalformedURLException;

import com.es.Utilities.DataGeneration;
import com.es.Utilities.DataProperty;
import com.es.Utilities.Excel;
import com.es.Utilities.Reporting;
import com.es.configuration.Keywords;
import com.es.configuration.ReadPropertiesFile;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.ObjectMapperConfig;
import com.jayway.restassured.mapper.ObjectMapper;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Headers;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

public class APICommon {

	public static DataGeneration dataGenerate = new DataGeneration();
	public DBConfiguration db = new DBConfiguration();
	public Reporting logger = new Reporting();
	private ReadPropertiesFile read = new ReadPropertiesFile();
	private String API_User = read.readRunProperties("API_USER").trim();
	private String API_Password = read.readRunProperties("API_PASSWORD").trim();
	private String API_Url = read.readRunProperties("API_URL").trim();
	private DataProperty dataProp = new DataProperty();
	private Keywords actions = new Keywords();

	public String getAccessToken() {
		String accessToken = null;
		try {
			String body = "grant_type=password&username=" + API_User + "&password=" + API_Password
					+ "&response_type=token";
			Response response = (Response) given().header("Content-Type", "application/x-www-form-urlencoded")
					.header("Accept-Language", "en-US").header("Authorization", "Basic V0VCQVBJX0tFWTpwOTlFeDk5RDk5")
					.body(body).when().post(new URL(API_Url));
			JsonPath jsonPathEvaluator = response.jsonPath();
			accessToken = jsonPathEvaluator.get("AccessToken");
			System.out.println("Access Token received from Response " + accessToken);
		} catch (Exception e) {
			System.out.println(e);
		}
		return accessToken;
	}

	public Headers getHeaders(String dataKey) {
		Headers header = new Headers();
		try {
			String accessToken = getAccessToken();
			Map<String, String> HeadersMap = Excel.getApiHeader(dataKey);
			List<Header> headerList = new ArrayList<Header>();
			for(String key : HeadersMap.keySet()) {
				if((!key.equalsIgnoreCase("URL") && !key.equals("")) &&
						(!key.equalsIgnoreCase("VerifyWith") && !key.equals("")) )
					headerList.add(new Header(key, dataGenerate.randomDataGenerator(HeadersMap.get(key),"getheaders")));
				//System.out.println(key);
				//System.out.println(dataGenerate.randomDataGenerator(HeadersMap.get(key),"getheaders"));
			}
			headerList.add(new Header("Authorization","OAuth "+accessToken));
			header = new Headers(headerList);
		}
		catch(Exception e) {
			System.out.println("Failed to get headers due to exception " + e.getMessage());
		}
		return header;
	}

	public String readJsonFile(String FilePath) {
		String jsonRead = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(FilePath));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			jsonRead = sb.toString();
			br.close();
		}catch(Exception e) {
			logger.logFail("Failed to read Json from given file due to exception "+e.getMessage());
		}
		return jsonRead;
	}

	public void API_Post(String dataKey, String extraParam) {
		//logger.logInfo("API Post Method");
		String jsonBody = "";
		String Key = dataKey;
		if(dataKey.contains(".")) {
			Key = dataKey.split("\\.")[1];
		}
		logger.logInfo("API Post"+ " "+Key+ " "+"Method");
		Headers apiheader = getHeaders(dataKey);
		String sURL = dataGenerate.randomDataGenerator(Excel.getApiHeaderData(dataKey,"URL"),actions.getMethodName());
		String jsonFile = Excel.getApiHeaderData(dataKey, "JsonFile").trim();
		String jsonBodyGiven = Excel.getApiHeaderData(dataKey, "JsonBody").trim();
		if(jsonFile!=null&&(!jsonFile.equals(""))) {
			String jsonFilePath = System.getProperty("user.dir")+"\\JsonFolder\\"+jsonFile;
			jsonBody = readJsonFile(jsonFilePath);
		}else if(jsonBodyGiven!=null&&(!jsonBodyGiven.equals(""))) {
			jsonBody = jsonBodyGiven;
		}else {
			jsonBody = dataGenerate.randomDataGenerator(generateJsonADCS(dataKey, actions.getMethodName()),actions.getMethodName());		
		}
		try {
			logger.logPass("Created Json Body is : '"+jsonBody+"'","N");
			Response response = (Response) given().headers(apiheader).body(jsonBody).when().post(new URL(sURL));
			logger.logInfo("Obtained response : "+ response.asString());
			writeResponseToData(response, Key);
		} catch (Exception e) {
			logger.logFail("Failed to Post " + e.getMessage());
		}
	}

	public void API_Get(String dataKey, String extraParam) {
		logger.logInfo("API Get Method");
		String Key = dataKey;
		if(dataKey.contains(".")) {
			Key = dataKey.split("\\.")[1];
		}
		Headers apiheader = getHeaders(dataKey);
		apiheader.get(dataKey);
		logger.logPass("api header :" +apiheader,"N");
		String sURL = dataGenerate.randomDataGenerator(Excel.getApiHeaderData(dataKey,"URL"),actions.getMethodName());		
		logger.logPass("generated URL :" +sURL,"N");
		try {
			Response response = (Response) given().headers(apiheader).when().get(new URL(sURL));
			//Response response = (Response) given().headers(apiheader).body(jsonBody).when().post(new URL(sURL));
			logger.logPass("Obtained response : "+ response.asString(),"N");
			writeResponseToData(response, Key);
		} catch (Exception e) {
			logger.logFail("Failed to Post " + e.getMessage());
		}
	}

	public void API_GetAndVerify(String dataKey, String extraParam) {
		logger.logInfo("API Get and Verify Method");
		int passFlag = 0;
		String Key = dataKey;
		if(dataKey.contains(".")) {
			Key = dataKey.split("\\.")[1];
		}
		Headers apiheader = getHeaders(dataKey);
		String sURL = dataGenerate.randomDataGenerator(Excel.getApiHeaderData(dataKey,"URL"),actions.getMethodName());
		String VerifyWith =Excel.getApiHeaderData(dataKey,"VerifyWith");
		try {
			Response response = (Response) given().headers(apiheader).when().get(new URL(sURL));
			logger.logPass("Obtained response : "+ response.asString(),"N");
			writeResponseToData(response, Key);
			//Get the two comparision into the Map 
			Map<Object,Object> postMap = dataProp.getToMap("input"+VerifyWith.toLowerCase());
			Map<Object,Object> getMap = dataProp.getToMap(Key.toLowerCase());
			for(Object pm : postMap.keySet()) {
				if(getMap.containsKey(pm)) {
					if(getMap.get(pm).toString().equalsIgnoreCase(postMap.get(pm).toString())){
						logger.logPass("Compare Value. Posted Value: "+postMap.get(pm).toString()+" Get Value: "+getMap.get(pm).toString()+" for the key "+pm,"N");
						passFlag += 1;
					}else {
						logger.logFail("Failed to compare. Posted Value: "+postMap.get(pm).toString()+" Get Value: "+getMap.get(pm).toString()+" for the key "+pm,"N");
					}
				}else {
					logger.logFail("Obtained Response doesnot have attribute "+ pm.toString().toUpperCase(),"N");
				}
			}
			if(passFlag==postMap.keySet().size()) {
				logger.logPass("Verified "+VerifyWith+ " with get call successfully", "N");
			}else {
				logger.logFail("Verification failed for "+VerifyWith+ " with get call", "N");
			}

		} catch (Exception e) {
			logger.logFail("Failed to Post " + e.getMessage(),"N");
		}
	}

	public Map<Object,Object> convertSimpleJsonToMap(String jsonStr) {
		String[] temp = new String[2];
		Map<Object,Object> resultMap = new HashMap<Object,Object>();
		String splitString1 = jsonStr.substring(1);
		String splitString2 = splitString1.replaceAll("[}]$", "");
		String[] splitString = splitString2.split(", ");
		try {
			for(String str : splitString) {
				if(str.contains("=")) {
					temp = str.split("\\="); 
					if(temp.length>1) {
						resultMap.put(temp[0], temp[1]);
					}
					temp = null;
				}
			}
		} catch (Exception e) {
			logger.logFail("Failed to convert json String to map due to exception "+e.getMessage());
		}
		return resultMap;
	}

	public void writeResponseToData(Response response, String methodName) {
		try {
			Map<Object,Object> responseMap = new HashMap<Object, Object>();
			JsonPath jsonPathEvaluator = response.jsonPath();
			Object jsonObject = jsonPathEvaluator.get().getClass();
			if(jsonObject.toString().equals("class java.util.ArrayList"))
			{
				ArrayList<Object> jsonList= jsonPathEvaluator.get();
				for(Object elist:jsonList) {
					responseMap = convertSimpleJsonToMap(elist.toString());;
					writeLoop(response, responseMap, methodName);
				}
			}else {
				responseMap = jsonPathEvaluator.get();
				writeLoop(response, responseMap, methodName);
			}
		}catch(Exception e) {
			logger.logFail("Failed to write response to data properties due to exception "+e.getMessage());
		}
	}
	
	public void writeLoop(Response response,Map<Object,Object> responseMap,String methodName) {
		try {
			for(Object key : responseMap.keySet()) {
				String responseKey = key.toString();
				String responseValue = responseMap.get(key).toString();
				if((responseValue.contains("{")||responseValue.contains("["))&&!responseValue.equals("[]")) {
					int childObjects = 0, tempFlag=0;
					Map<Object, Object> childNodes = new HashMap<Object, Object>();
					childNodes = response.jsonPath().getMap(responseKey+"["+childObjects+"]");
					childObjects +=1;
					while(tempFlag==0) {
						if(childNodes==null)
							childNodes = response.jsonPath().getMap(responseKey);
						for(Object childKey : childNodes.keySet()) {
							String child_responseKey = childKey.toString();
							String child_responseValue = childNodes.get(childKey).toString();

							if((child_responseValue.contains("{")||child_responseValue.contains("["))&&!child_responseValue.equals("[]")) {
								Map<Object, Object> subchildNodes = new HashMap<Object, Object>();
								subchildNodes = convertSimpleJsonToMap(child_responseValue);
								for(Object childKey1 : subchildNodes.keySet()) {
									String child_responseKey1 = childKey1.toString();
									String child_responseValue1 = subchildNodes.get(childKey1).toString();
									dataGenerate.writeApiData(methodName, child_responseKey1, child_responseValue1);
									dataGenerate.writeApiData(methodName, child_responseKey+child_responseKey1, child_responseValue1);
								}
							}else {
								dataGenerate.writeApiData(methodName, responseKey+child_responseKey, child_responseValue);
							}
						}
						try {
							childNodes = response.jsonPath().getMap(responseKey+"["+childObjects+"]");
							childObjects +=1;
							if(childNodes==null)
								tempFlag+=1;
						}catch(Exception ex) {
							tempFlag+=1;
						}
					}
				}else {
					dataGenerate.writeApiData(methodName, responseKey, responseValue);
				}
			}
		}catch(Exception e) {
			logger.logFail("Failed to write loop due to exception "+e.getMessage());
		}
	}

	public Map<Object, Object> getAllChildNodesToMap(Response response, String responseKey) {
		int childObjects = 0, tempFlag=0;
		Map<Object, Object> childNodes = new HashMap<Object, Object>();
		Map<Object, Object> tempMap = new HashMap<Object, Object>();
		do {
			try {
				tempMap = response.jsonPath().getMap(responseKey+"["+childObjects+"]");
				childObjects +=1;
				childNodes.putAll(tempMap);
			}catch(Exception ex) {
				tempFlag+=1;
			}
		}while(tempFlag==0);
		return childNodes;
	}

	public void API_AddTransaction(String Key, String extraParam) {
		String ContentType = Excel.getApiHeaderData(Key, "Content-Type");
		String AcceptLanguage = Excel.getApiHeaderData(Key, "Accept-Language");
		String sURL = Excel.getApiHeaderData(Key, "URL");
		String accessToken = getAccessToken();
		try {
			String transactionBody = generateJson(Key, actions.getMethodName());
			System.out.println(transactionBody);
			Response response = (Response) given().header("Content-Type", ContentType)
					.header("Accept-Language", AcceptLanguage).header("Authorization", "OAuth " + accessToken)
					.body(transactionBody)

					.when().post(new URL(sURL));
			JsonPath jsonPathEvaluator = response.jsonPath();
			String TransactionId = jsonPathEvaluator.get("TransactionId");
			System.out.println("Transaction ID received from Response " + TransactionId);
		} catch (MalformedURLException e) {
			System.out.println(e);
		}
	}

	public void API_GetProfile(String Key, String extraParam) {
		String ProgramCode = Excel.getApiHeaderData(Key, "Program-Code");
		String ContentType = Excel.getApiHeaderData(Key, "Content-Type");
		String AcceptLanguage = Excel.getApiHeaderData(Key, "Accept-Language");
		String accessToken = getAccessToken();
		String sURL = Excel.getApiHeaderData(Key, "URL");
		String primaryId = Excel.getApiHeaderData(Key, "PrimaryId");
		primaryId = dataGenerate.randomDataGenerator(primaryId, actions.getMethodName());
		String actualURL = sURL.replace("{profileID}",
				primaryId);
		System.out.println(actualURL);
		try {

			Response response = (Response) given().header("Content-Type", ContentType)
					.header("Accept-Language", AcceptLanguage).header("Authorization", "OAuth " + accessToken)
					.header("Program-Code", ProgramCode)
					.when().get(new URL(actualURL));
			/*.then()
					.assertThat()
					.body("TransactionId", co*/
			JsonPath jsonPathEvaluator = response.jsonPath();
			jsonPathEvaluator.get("TransactionId");
			List<String> TransId = jsonPathEvaluator.get("TransactionId");
			if(TransId.size()>=2) {
				System.out.println("Below are the transactions available in the Parent Profile("+primaryId+") after merging");
				for(int i=0; i<TransId.size();i++) {
					System.out.println("Transaction"+i+" : "+ TransId.get(i));
				}
			}

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public String generateJson(String key, String methodName) {
		String apiString = "";
		String SheetName = "";
		if (key.contains(".")) {
			String[] test = key.split("\\.");
			SheetName = key.split("\\.")[0];
			key = key.split("\\.")[1];
		}
		XSSFSheet ExcelWSheet = Excel.getDataWorkBook().getSheet(SheetName);
		try {
			//Takes all the merged cell into an array and adds to a for loop.
			for (CellRangeAddress range : ExcelWSheet.getMergedRegions()) {
				//Get firstRow, lastRow and firstColumn of merged cell picked at each loop
				int firstRowValue = range.getFirstRow();
				int firstColumn = range.getFirstColumn();
				int lastRowValue = range.getLastRow();
				//Condition to check if the key is in 1st coloumn, and the value should be as given in the data sheet
				if (firstColumn == 0 && key.equalsIgnoreCase(Excel.getCellData(ExcelWSheet, firstRowValue, 0))) {
					//Json generation starts from 4th column
					int columnNumber = ExcelWSheet.getRow(firstRowValue + 3).getLastCellNum();
					apiString += "{";
					//Column starts from 4
					for (int j = 3; j < columnNumber; j++) {
						//(firstRowValue + 2) is parent key and (firstRowValue + 3) is parent key's value 
						//j is the column number
						String jsonKey = Excel.getCellData(ExcelWSheet, firstRowValue + 2, j);
						String jsonValue = Excel.getCellData(ExcelWSheet, firstRowValue + 3, j);
						jsonValue = dataGenerate.randomDataGenerator(jsonValue, methodName);
						if (!(jsonKey.toLowerCase().contains("jsonexternaldata"))) {
							dataGenerate.writeApiData(key,jsonKey,jsonValue);
							dataGenerate.writeApiData("input"+key,jsonKey,jsonValue);
						}

						if (jsonKey != ("") && !jsonKey.equals("")) {
							if (jsonKey.endsWith("_Open") && jsonValue.equalsIgnoreCase("Y")) {
								if (j > 3) {
									apiString += ",";
								}
								apiString += "\"" + jsonKey.replace("_Open", "") + "\": {";
								String jsonKey_jsonExternal = jsonKey;
								j++;
								int n = 1;
								//jsonExternal data works with open and close condition
								while (!jsonKey_jsonExternal.endsWith("_Close")) {
									jsonKey_jsonExternal = Excel.getCellData(ExcelWSheet, firstRowValue + 2, j);
									String jsonValue_jsonExternal = Excel.getCellData(ExcelWSheet, firstRowValue + 3,j);
									jsonValue_jsonExternal = dataGenerate.randomDataGenerator(jsonValue_jsonExternal, methodName);
									if (!(jsonKey_jsonExternal.toLowerCase().contains("jsonexternaldata"))) {
										dataGenerate.writeApiData(key,jsonKey_jsonExternal,jsonValue_jsonExternal);
										dataGenerate.writeApiData("input"+key,"jsonexternaldata"+jsonKey_jsonExternal,jsonValue_jsonExternal);
									}
									if (n > 1) {
										apiString += ",";
									}
									n++;
									apiString += "\"" + jsonKey_jsonExternal + "\" : \"" + jsonValue_jsonExternal
											+ "\"";
									j++;
									jsonKey_jsonExternal = Excel.getCellData(ExcelWSheet, firstRowValue + 2, j);
								}
								apiString += "}";
							} else if (jsonKey.endsWith("_Open")) {
								String jsonKey_jsonExternal = jsonKey;
								j++;
								while (!jsonKey_jsonExternal.endsWith("_Close")) {
									j++;
									jsonKey_jsonExternal = Excel.getCellData(ExcelWSheet, firstRowValue + 2, j);
								}
							} else {
								if (j > 3) {
									apiString += ",";
								}
								apiString += "\"" + jsonKey + "\" : \"" + jsonValue + "\"";
							}
						}
					}
					//The child loop
					for (CellRangeAddress range1 : ExcelWSheet.getMergedRegions()) {
						int firstRowValueB = range1.getFirstRow();
						int firstColumnB = range1.getFirstColumn();
						int lastRowValueB = range1.getLastRow();
						if (firstColumnB == 3 && lastRowValue >= firstRowValueB && firstRowValueB >= firstRowValue) {
							//Fetches the parent header name from merged rows
							String parentBlockName = Excel.getCellData(ExcelWSheet, firstRowValueB, 3);
							apiString += ",\"" + Excel.getCellData(ExcelWSheet, firstRowValueB, 3) + "\" :[";
							for (int i = firstRowValueB; i < lastRowValueB; i++) {
								int colnum = ExcelWSheet.getRow(i).getLastCellNum();
								apiString += "{";
								for (int j = 4; j < colnum; j++) {
									String jsonChildKey = Excel.getCellData(ExcelWSheet, firstRowValueB, j);
									String jsonChildValue = Excel.getCellData(ExcelWSheet, i + 1, j);
									jsonChildValue = dataGenerate.randomDataGenerator(jsonChildValue, methodName);
									if (!(jsonChildKey.toLowerCase().contains("jsonexternaldata"))) {
										dataGenerate.writeApiData(key, jsonChildKey, jsonChildValue);
										dataGenerate.writeApiData("input"+key,parentBlockName+jsonChildKey,jsonChildValue);
									}
									if (jsonChildKey != ("") && jsonChildKey != (null)) {
										if (jsonChildKey.endsWith("_Open")
												&& jsonChildValue.equalsIgnoreCase("Y")) {
											if (j > 4) {
												apiString += ",";
											}
											apiString += "\"" + jsonChildKey.replace("_Open", "") + "\": {";
											String jsonChildKey_jsonExternal = jsonChildKey;
											j++;
											int n = 3;
											while (!jsonChildKey_jsonExternal.endsWith("_Close")) {
												jsonChildKey_jsonExternal = Excel.getCellData(ExcelWSheet,firstRowValueB, j);
												String jsonChildValue_jsonExternal = Excel.getCellData(ExcelWSheet,i + 2, j);
												jsonChildValue_jsonExternal = dataGenerate.randomDataGenerator(jsonChildValue_jsonExternal, methodName);
												if (!(jsonChildKey_jsonExternal.toLowerCase().contains("jsonexternaldata"))) {
													dataGenerate.writeApiData(key, jsonChildKey, jsonChildValue);
													dataGenerate.writeApiData("input"+key,"jsonexternaldata"+jsonChildKey,jsonChildValue);
												}
												if (n > 3) {
													apiString += ",";
												}
												n++;
												apiString += "\"" + jsonChildKey_jsonExternal + "\" : \""
														+ jsonChildValue_jsonExternal + "\"";
												j++;
												jsonChildKey_jsonExternal = Excel.getCellData(ExcelWSheet,
														firstRowValueB, j);
											}
											apiString += "}";
										} else if (jsonChildKey.endsWith("_Open")) {
											String jsonChildKey_jsonExternal = jsonChildKey;
											j++;
											while (!jsonChildKey_jsonExternal
													.endsWith("_Close")) {
												j++;
												jsonChildKey_jsonExternal = Excel.getCellData(ExcelWSheet,
														firstRowValueB, j);
											}
										} else {
											if (j > 4) {
												apiString += ",";
											}
											apiString += "\"" + jsonChildKey + "\" : \"" + jsonChildValue + "\"";
										}
									}
								}
								apiString += "}";
								if (i != (lastRowValueB - 1)) {
									apiString += ",";
								}

							}
							apiString += "]";
						}

					}
				}
			}
			apiString += "}";
		} catch (Exception e) {
			System.out.println("Failed to create json " + e.getMessage());
		}
		return apiString;
	}

	public String generateJsonADCS(String key, String methodName) {
		String apiString = "";
		String SheetName = "";
		String Json_CustAtt_Node = "";
		String Json_CustAtt_Node_Val = "";
		if (key.contains(".")) {
			String[] test = key.split("\\.");
			SheetName = key.split("\\.")[0];
			key = key.split("\\.")[1];
		}
		XSSFSheet ExcelWSheet = Excel.getDataWorkBook().getSheet(SheetName);
		try {
			//Takes all the merged cell into an array and adds to a for loop.
			for (CellRangeAddress range : ExcelWSheet.getMergedRegions()) {
				//Get firstRow, lastRow and firstColumn of merged cell picked at each loop
				int firstRowValue = range.getFirstRow();
				int firstColumn = range.getFirstColumn();
				int lastRowValue = range.getLastRow();
				//Condition to check if the key is in 1st coloumn, and the value should be as given in the data sheet
				if (firstColumn == 0 && key.equalsIgnoreCase(Excel.getCellData(ExcelWSheet, firstRowValue, 0))) {
					int columnNumber = ExcelWSheet.getRow(firstRowValue + 2).getLastCellNum();
					apiString += "{";
					//Json generation starts from 1st column
					for (int j = 1; j < columnNumber; j++) {
						String jsonKey = Excel.getCellData(ExcelWSheet, firstRowValue + 2, j);
						String jsonValue = Excel.getCellData(ExcelWSheet, firstRowValue + 3, j);
						jsonValue = dataGenerate.randomDataGenerator(jsonValue, methodName);
						
						//Write the data in Data Properties File
						if (!(jsonKey.toLowerCase().contains("jsonexternaldata"))) {
							//	dataGenerate.writeApiData(key,jsonKey,jsonValue);
							dataGenerate.writeApiData("input."+key,jsonKey,jsonValue);
						}
						//jsonExternalData identified with 'jsonExternalData' and values as Y
						if (jsonKey != ("") && !jsonKey.equals("")) {
							if (jsonKey.equalsIgnoreCase("jsonExternalData") && jsonValue.equalsIgnoreCase("Y")) {
								if (j > 1) {
									apiString += ",";
								} 
								j++;
								Json_CustAtt_Node=Excel.getCellData(ExcelWSheet, firstRowValue + 2, j);
								Json_CustAtt_Node_Val=Excel.getCellData(ExcelWSheet, firstRowValue + 3, j);
								// Add Child level based on CustomAttributes tag
								if (Json_CustAtt_Node.equalsIgnoreCase("CustomAttributes") && Json_CustAtt_Node_Val.equalsIgnoreCase("Y"))
										{apiString += "\"" + jsonKey + "\": {" + "\"" + Json_CustAtt_Node + "\": {";}
								else
									{apiString += "\"" + jsonKey + "\": {";}
								
								String jsonKey_jsonExternal = jsonKey;
								j++;
								int n = 1;
								//Generate JSON for jsonExternalData till the end of close tag
								while (!jsonKey_jsonExternal.equalsIgnoreCase("jsonExternalData_Close")) 
								{								
									jsonKey_jsonExternal = Excel.getCellData(ExcelWSheet, firstRowValue + 2, j);								
									String jsonValue_jsonExternal =dataGenerate.randomDataGenerator(Excel.getCellData(ExcelWSheet, firstRowValue + 3,j), actions.getMethodName()) ; 
									if (!jsonKey_jsonExternal.equalsIgnoreCase("jsonExternalData_Close") && !jsonKey_jsonExternal.equalsIgnoreCase("CustomAttributes_Close") )
									{
										if (n > 1) 
										{
											apiString += ",";
										}
										n++;
										apiString += "\"" + jsonKey_jsonExternal + "\" : \"" + jsonValue_jsonExternal
												+ "\"";
										//Write the data in Data Properties File
										//dataGenerate.writeApiData(key, jsonKey_jsonExternal, jsonValue_jsonExternal);
										dataGenerate.writeApiData("input."+key,"jsonexternaldata."+jsonKey_jsonExternal,jsonValue_jsonExternal);
										
										jsonKey_jsonExternal = Excel.getCellData(ExcelWSheet, firstRowValue + 2, j);
									}
									else if (jsonKey_jsonExternal.equalsIgnoreCase("CustomAttributes_Close") && jsonValue_jsonExternal.equalsIgnoreCase("Y"))
									{apiString += "}";}
									j++;
								}
								apiString += "}";
								
							} else if (jsonKey.equalsIgnoreCase("jsonExternalData")) {
								String jsonKey_jsonExternal = jsonKey;
								j++;
								while (!jsonKey_jsonExternal.equalsIgnoreCase("jsonExternalData_Close")) {
									j++;
									jsonKey_jsonExternal = Excel.getCellData(ExcelWSheet, firstRowValue + 2, j);
								}
							} else {
								if (j > 1) {
									apiString += ",";
								}
								apiString += "\"" + jsonKey + "\" : \"" + jsonValue + "\"";
																
							}
						}
					}
					// Retrieve Data from Child loop like Phone , Email and Address
					for (CellRangeAddress range1 : ExcelWSheet.getMergedRegions()) {
						int firstRowValueB = range1.getFirstRow();
						int firstColumnB = range1.getFirstColumn();
						int lastRowValueB = range1.getLastRow();
						String parentBlockName = Excel.getCellData(ExcelWSheet, firstRowValueB, 1);
						if (firstColumnB == 1 && lastRowValue >= firstRowValueB && firstRowValueB >= firstRowValue) {
							apiString += ",\"" + Excel.getCellData(ExcelWSheet, firstRowValueB, 1) + "\" :[";
							for (int i = firstRowValueB; i < lastRowValueB; i++) {
								int colnum = ExcelWSheet.getRow(i).getLastCellNum();
								apiString += "{";
								for (int j = 2; j < colnum; j++) {
									String jsonChildKey = Excel.getCellData(ExcelWSheet, firstRowValueB, j);
									String jsonChildValue = Excel.getCellData(ExcelWSheet, i + 1, j);
									jsonChildValue = dataGenerate.randomDataGenerator(jsonChildValue, methodName);
									//Write the data in Data Properties File
									if (!(jsonChildKey.toLowerCase().contains("jsonexternaldata"))) {
										//dataGenerate.writeApiData(key, jsonChildKey, jsonChildValue);
										dataGenerate.writeApiData("input."+key,parentBlockName+"."+jsonChildKey,jsonChildValue);
									}
									//Validate if Child loop has JSON  external data
									if (jsonChildKey != ("") && jsonChildKey != (null)) {
										if (jsonChildKey.equalsIgnoreCase("jsonExternalData")
												&& jsonChildValue.equalsIgnoreCase("Y")) {
											if (j > 2) {
												apiString += ",";
											}
											apiString += "\"" + jsonChildKey + "\": {";
											String jsonChildKey_jsonExternal = jsonChildKey;
											j++;
											int n = 1;
											while (!jsonChildKey_jsonExternal
													.equalsIgnoreCase("jsonExternalData_Close")) {
												jsonChildKey_jsonExternal = Excel.getCellData(ExcelWSheet,
														firstRowValueB, j);
												String jsonChildValue_jsonExternal = Excel.getCellData(ExcelWSheet,
														i + 1, j);
												//Write the data in Data Properties File
												if (!(jsonChildKey_jsonExternal.toLowerCase().contains("jsonexternaldata"))) {
													//dataGenerate.writeApiData(key, jsonChildKey, jsonChildValue);
													dataGenerate.writeApiData("input."+key,"jsonexternaldata."+jsonChildKey,jsonChildValue);
												}
												
												if (n > 1) {
													apiString += ",";
												}
												n++;
												apiString += "\"" + jsonChildKey_jsonExternal + "\" : \""
														+ jsonChildValue_jsonExternal + "\"";
												j++;
												jsonChildKey_jsonExternal = Excel.getCellData(ExcelWSheet,
														firstRowValueB, j);
											}
											apiString += "}";
										} else if (jsonChildKey.equalsIgnoreCase("jsonExternalData")) {
											String jsonChildKey_jsonExternal = jsonChildKey;
											j++;
											while (!jsonChildKey_jsonExternal
													.equalsIgnoreCase("jsonExternalData_Close")) {
												j++;
												jsonChildKey_jsonExternal = Excel.getCellData(ExcelWSheet,
														firstRowValueB, j);
											}
										} else {
											if (j > 2) {
												apiString += ",";
											}
											apiString += "\"" + jsonChildKey + "\" : \"" + jsonChildValue + "\"";
										}
									}
								}
								apiString += "}";
								if (i != (lastRowValueB - 1)) {
									apiString += ",";
								}

							}
							apiString += "]";
						}

					}
				}
			}
			apiString += "}";
		} catch (Exception e) {
			System.out.println("Failed to create json " + e.getMessage());
		}
		return apiString;
	}


}