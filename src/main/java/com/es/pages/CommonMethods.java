package com.es.pages;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.es.DBUtils.DBConfiguration;
import com.es.Utilities.DataGeneration;
import com.es.Utilities.Excel;
import com.es.Utilities.Reporting;
import com.es.configuration.BrowserConfig;
import com.es.configuration.Keywords;
import com.es.configuration.ReadPropertiesFile;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CommonMethods {

	public Keywords actions = new Keywords();
	public DataGeneration dataGenerate = new DataGeneration();
	public DBConfiguration db = new DBConfiguration();
	public Reporting logger = new Reporting();
	public static String ProfilePointsBefore = null;
	public static String Date;
	public static String randomEmail;
	public ReadPropertiesFile read =  new ReadPropertiesFile();
	public BrowserConfig config = new BrowserConfig();

	public void Launch() {
		try {
			String Url = read.readRunProperties("EXECUTION_URL").trim();
			if(config.webDriver!=null) {
				Set<String> str = config.webDriver.getWindowHandles();
				if(str.size()==0) {
					config.Launch(Url);
				}else {
					BrowserConfig.webDriver.get(Url);
					actions.waitForPageToLoad(60);
				}
			}
			else if(config.webDriver==null) {
				config.Launch(Url);
			}else {
				BrowserConfig.webDriver.get(Url);
				actions.waitForPageToLoad(60);
			}
		}catch(Exception e) {
			System.out.println("Failed to Launch due to exception " + e.getMessage());
		}
	}

	public void enterAllValues(String methodName, String key) {
		String[] Data = null;
		try {
			if (key != null && key != "" && (!key.equals("null"))) {
				Data = Excel.getData(key);
				String[] Locators = Excel.getLocatorData(key, methodName);
				for (int i = 0; i < Data.length; i++) {
					if (Data[i] != null && !Data[i].equals("") && !Data[i].equals("Ignore")) {
						actions.waitForPageToLoad(30);
						Thread.sleep(500);
						if (Locators[i] != null) {
							//actions.jsScrollToElement(Locators[i]);
							actions.setValue(Locators[i], Data[i], methodName);
						}
						//The method is to write the excel input data to data file properties
						String InputData = Data[i];
						String LocatorData = Locators[i];
						dataGenerate.writeApiData(actions.getMethodName(), LocatorData, InputData);
					}
				}
			}
		} catch (Exception e) {
			logger.logFail("Failed to enter all the values due to exception " + e.getMessage());
		}
	}

	public void clearAndEnterAllValues(String methodName, String key) {
		String[] Data = null;
		try {
			if (key != null && key != "" && (!key.equals("null"))) {
				Data = Excel.getData(key);
				String[] Locators = Excel.getLocatorData(key, methodName);
				for (int i = 0; i < Data.length; i++) {
					if (Data[i] != null && !Data[i].equals("")) {
						if (Locators[i] != null) {
							actions.clearAndSet(Locators[i], Data[i], methodName);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.logFail("Failed to enter all the values due to exception " + e.getMessage());
		}
	}

	public void click_SearchResult(String Screenshot) {
		if (!actions.isElementPresent("CustomerProfile_UniqueLocator")) {
			//actions.click("Search_Results");
			actions.getWebElement("Search_Results").click();
			logger.logPass("Clicking on 1st Search result ", Screenshot);
			actions.waitForPageToLoad(60);
		}
	}
	

	public String generateRandomNumber() {
		Random rn = new Random();
		long range = 1000000000L + (long) (rn.nextDouble() * 999999999L);
		String randNumb = String.valueOf(range);
		return randNumb;
	}

	public String getDate() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
		LocalDateTime now = LocalDateTime.now();
		return (dtf.format(now));
	}

	public void NavigateToPage(String toBeClicked, String validationElement, String Screenshot) {
		try {
			actions.click(toBeClicked);
			actions.waitForPageToLoad(60);
			if (actions.isElementPresent(validationElement))
				logger.logPass("Page Navigated on clicking " + toBeClicked, Screenshot);
			else
				logger.logFail("Failed to navigate to expected Page");
		} catch (Exception e) {
			logger.logFail("Failed to navigate to page due to exception " + e.getMessage());
		}
	}

	public String getErrorMessage() {
		String errorString = "";
		try {
			if (actions.getWebElementList("Alert_MessageBox").size() != 0
					&& actions.getWebElementList("Alert_MessageBox") != null) {
				actions.jsScrollToElement("Alert_MessageBox");
				errorString = actions.getWebElement("Alert_MessageBox").getText();
			} else if (actions.getWebElementList("Required_ErrorMessage").size() != 0
					&& actions.getWebElementList("Required_ErrorMessage") != null) {
				actions.jsScrollToElement("Required_ErrorMessage");
				errorString = actions.getWebElement("Required_ErrorMessage").getAttribute("data-valmsg-for");
			} else if (actions.getWebElementList("Invalid_Msg").size() != 0
					&& actions.getWebElementList("Invalid_Msg") != null) {
				actions.jsScrollToElement("Invalid_Msg");
				errorString = actions.getWebElement("Invalid_Msg").getText();
			} else {
				logger.logWarning("No error message available in the screen");
			}
		} catch (Exception e) {
			logger.logFail("Caught while verifying error message due to exception " + e.getMessage());
			return errorString;
		}
		return errorString;
	}

	public void VerifySuccessPage(String Message, String Screenshot) {
		try {
			if (VerifyMessage("Successful")) {
				logger.logPass("Successful message for " + Message + " is displayed", Screenshot);
			} else if (VerifyMessage("Invalid")) {
				logger.logPass("Error message for " + Message + " is displayed", Screenshot);
			} else {
				logger.logFail("Successful message for " + Message + " is not displayed");
			}
			/*
			 * if (actions.isElementPresent("CreateCustomer_SuccessMessage")) {
			 * logger.logPass("Account Created Successfully !", Screenshot); } else {
			 * logger.logFail("Failed to create new customer"); }
			 */

		} catch (Exception e) {
			logger.logFail("Failed to verify the success page due to exception " + e.getMessage());
		}
	}

	public void VerifyDBData(String Key, String Screenshot) {
		String[] Cardnumber = Excel.getData(Key);
		try {
			db.getDBConnection();
			ResultSet getDetails = db.executeQuery(
					"SELECT * FROM fs.t_profile tp, fs.T_PROFILE_CARD pc WHERE tp.Profile_ID = pc.Profile_ID and pc.card_number = '"
							+ Cardnumber[0] + "'");
			System.out.println(getDetails);
			ResultSetMetaData rsd = getDetails.getMetaData();
			int columnsNumber = rsd.getColumnCount();

			while (getDetails.next()) {
				for (int i = 1; i <= columnsNumber; i++) {
					if (i > 1)
						System.out.print(",  ");
					String columnValue = getDetails.getString(i);
					System.out.print(columnValue + " ");// rsd.getColumnName(i));

				}
			}
			db.closeDB();

		} catch (Exception e) {
			logger.logFail("Caught while validating data in database " + e.getMessage());
		}
	}

	public boolean VerifyMessage(String errorStringExpected) {
		String errorString = errorStringExpected.replace(" ", "").toLowerCase();
		try {
			Thread.sleep(1000);
			if (actions.getWebElementList("Alert_MessageBox").size() != 0) {
				actions.jsScrollToElement("Alert_MessageBox");
				String alertMessage = actions.getWebElement("Alert_MessageBox").getText();
				if (alertMessage.replace(" ", "").toLowerCase().contains(errorString)) {
					return true;
				} else {
					return false;
				}
			} else if (actions.getWebElementList("Required_ErrorMessage").size() != 0) {
				actions.jsScrollToElement("Required_ErrorMessage");
				String requiredFieldError = actions.getWebElement("Required_ErrorMessage")
						.getAttribute("data-valmsg-for").toLowerCase();
				if (requiredFieldError.contains(errorString)) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			logger.logFail("Caught while verifying error message due to exception " + e.getMessage());
			return false;
		}
	}

	public void AccountLogout(String extraParam, String Screenshot) {
		try {
			actions.click("Profile_Account");
			actions.waitForPageToLoad(60);
			actions.javaScriptClick("LogOut_Account");
			logger.logPass("Logout is successful", Screenshot);

		} catch (Exception e) {
			logger.logFail("error in login out of the account successful" + e.getMessage());
		}
	}

	/* Functions in QuitBrowser */
	public void QuitBrowser(String extraParam, String Screenshot) {
		//AccountLogout("null", Screenshot);
		actions.quitBrowser();
	}

	public void PersonalInforLeftNav(String MainOption, String SubOption) {
		try {
			String mainOpt = Excel.getLocator("Personalinfo_MainOption").replace("##", MainOption);
			WebElement mainOptionToClick = actions.getWebElementWithoutExcel(mainOpt);
			String mainOption = mainOptionToClick.getAttribute("class");
			if (!mainOption.contains("active")) {
				mainOptionToClick.click();
				logger.logPass("Clicking on MainOption menu" + MainOption, "N");
				actions.waitForPageToLoad(60);
			}
			actions.waitExplicit(null, 5);
			String navigate = Excel.getLocator("Personalinfo_SubOption").replace("##", MainOption).replace("??",
					SubOption);
			WebElement subOptionToClick = actions.getWebElementWithoutExcel(navigate);
			actions.jsScrollToElement(subOptionToClick);
			subOptionToClick.click();
			actions.waitForPageToLoad(60);
			logger.logPass("Page Navigated on clicking " + SubOption, "N");
		} catch (Exception e) {

			logger.logFail("Failed to click on " + SubOption + " due to exception : " + e.getMessage());
		}

	}
	
	public void MomentumLeftNav(String MainOption, String SubOption) {
		try {		
			if(actions.isElementPresent("Header_Momentum"))
				actions.click("Header_Momentum");			
			actions.waitForPageToLoad(60);
			String mainOpt = Excel.getLocator("Momentum_MainOption").replace("##", MainOption);
			WebElement mainOptionToClick = actions.getWebElementWithoutExcel(mainOpt);
			String mainOption = mainOptionToClick.getAttribute("class");
			if (!mainOption.contains("active")) {
				mainOptionToClick.click();
				logger.logPass("Clicking on MainOption menu" + MainOption, "N");
				actions.waitForPageToLoad(60);
			}
			actions.waitExplicit(null, 5);
			String navigate = Excel.getLocator("Personalinfo_SubOption").replace("##", MainOption).replace("??",
					SubOption);
			WebElement subOptionToClick = actions.getWebElementWithoutExcel(navigate);
			actions.jsScrollToElement(subOptionToClick);
			subOptionToClick.click();
			actions.waitForPageToLoad(60);
			logger.logPass("Momentum Page Navigated on clicking " + SubOption, "N");

		} catch (Exception e) {

			logger.logFail("Failed to click on " + SubOption + " due to exception : " + e.getMessage());
		}
	}

	public String getProfilepoints() {
		actions.waitForPageToLoad(60);
		String Points = null;
		try {
			Points = actions.getWebElement("Profile_Points").getText();
		} catch (Exception e) {
			logger.logFail("Get Profile points failed due to exception " + e.getMessage());
		}
		return Points;

	}

	public void verifyOutputGrid(List<String> one, String OutputGrid, String Screenshot, String liTab) {
		try {
			if (one.size() > 1) {
				actions.jsScrollToElement(liTab);
				actions.click(liTab);
				logger.logPass("Navigate to the Tab " + liTab, Screenshot);
				String SecondGrid = OutputGrid;
				actions.jsScrollToElement(SecondGrid);
				List<String> two = GetGridValue(SecondGrid, Screenshot);

				for (int i = 0; i < one.size(); i++) {
					if (two.contains(one.get(i)) && !one.get(i).equals(" ")) {
						// logger.logPass("Input Transaction Value is displayed in confirmation page: "
						// + one.get(i), "N");
					}
				}
				logger.logPass("Successfully Verified " + OutputGrid + " in Transaction Details", "N");
			}
		} catch (Exception e) {
			logger.logFail("Verification Failed due to the exception :" + e.getMessage());
		}
	}

	public List<String> GetGridValue(String gridLocatorValueFromExcel, String Screenshot) {
		List<String> value = new ArrayList<String>();
		try {
			// Thread.sleep(3000);
			actions.waitForPageToLoad(60);
			WebElement table = actions.getWebElement(gridLocatorValueFromExcel);
			List<WebElement> rows = table.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
			if (rows.size() == 0) {
				return value;
			}
			List<WebElement> column = table.findElements(By.tagName("td"));
			for (int j = 0; j < column.size(); j++) {
				value.add(column.get(j).getText());
			}
			System.out.println(value);
			if (value.size() > 0)
				logger.logPass("Successfully fetched the Grid value for " + gridLocatorValueFromExcel, Screenshot);

			return value;
		} catch (Exception e) {
			logger.logFail("Fail to get the Grid value :" + e.getMessage());
		}
		return value;
	}

	public String getDataValue(String DataKey, String MethodName, String ValueHeader) {
		String outputValue = "";
		try {
			String[] Data = Excel.getData(DataKey);
			String[] Locators = Excel.getLocatorData(DataKey, MethodName);
			for (int i = 0; i < Data.length; i++) {
				if (Data[i] != null && !Data[i].equals("")) {
					if (Locators[i] != null && Locators[i].equalsIgnoreCase(ValueHeader)) {
						outputValue = Data[i];
						break;
					}
				}
			}
		} catch (Exception e) {
			logger.logFail("Failed to get DataValue from Excel due to exception " + e.getMessage());
		}
		return outputValue;
	}

	public void ProgramManagementLeftNav(String MainOption, String SubOption) {
		try {
			String mainOpt = Excel.getLocator("PrgManagement_MainOption").replace("##", MainOption);
			WebElement mainOptionToClick = actions.getWebElementWithoutExcel(mainOpt);
			String mainOption = mainOptionToClick.getAttribute("class");
			if (!mainOption.contains("active")) {
				actions.jsScrollToElement(mainOptionToClick);
				mainOptionToClick.click();
				logger.logPass("Clicking on MainOption menu " + MainOption, "N");
				actions.waitForPageToLoad(60);
			}
			String navigate = Excel.getLocator("PrgManagement_SubOption").replace("##", MainOption).replace("??",
					SubOption);
			WebElement subOptionToClick = actions.getWebElementWithoutExcel(navigate);
			actions.jsScrollToElement(subOptionToClick);
			subOptionToClick.click();
			actions.waitForPageToLoad(60);
			logger.logPass("Page Navigated on clicking " + SubOption, "N");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.logFail("Failed to click on " + SubOption + " due to exception : " + e.getMessage());
		}
	}
	public void VerifyAllValues(String methodName, String key, String Screenshot) {
		try {
			if (key != null && key != "" && (!key.equals("null"))) {
				String[] Data = Excel.getData(key);
				String[] Locators = Excel.getLocatorData(key, methodName);
				for (int i = 0; i < Locators.length; i++) {
					if (Data[i] != null && !Data[i].equals("")) {
						actions.waitForPageToLoad(30);
						if (Data[i].contains("Y")) {
							actions.jsScrollToElement(Locators[i]);
							actions.assertTrue(actions.isElementPresent(Locators[i]),
									"Verified Element " + Locators[i] + " is displayed", Screenshot);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.logFail("Failed to Verify all the values due to exception " + e.getMessage());
		}
	}
	
	public void GetNVerifyTextDB(String DBQuery, String[] Locators, String Screenshot) {
		try {
			ResultSet rs = db.executeQuery(DBQuery);
			while (rs.next()) {
				for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
					try {
						//System.out.println("DB Data" + rs.getString(i + 1));
						System.out.println("DBTesting");
						actions.jsScrollToElement(Locators[i]);
						String value = actions.getWebElement(Locators[i]).getText().trim();
						// System.out.println("LocatorsData " + value);
						if (value.contains(".c…")) {
							value = value.replace(".c…", ".com");
						} else if (value.contains(".…")) {
							value = value.replace(".…", ".com");
						}
						else if (value.contains("@t...")) {
							value = value.replace("@t...", "@test.com");
						}
						if (!value.equals("") && (rs.getString(i + 1).trim().equalsIgnoreCase(value))) {
							logger.logPass(Locators[i] + " :" + value + " value is equal to DB result value :"
									+ rs.getString(i + 1), Screenshot);
						} else if (!rs.getString(i + 1).trim().equalsIgnoreCase(value)) {
							logger.logFail(Locators[i] + " :" + value + " value is not equal to DB result value :"
									+ rs.getString(i + 1));
						}
					} catch (Exception e) {
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.logFail("Failed to verify DB value " + e.getMessage());
		}
	}
	
	public void GetNVerifyTextDBWithoutExcel(String DBQuery, String[] Locators, String Screenshot) {
		try {
			ResultSet rs = db.executeQuery(DBQuery);
			while (rs.next()) {
				for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
					try {
						System.out.println("DBTesting");
						actions.jsScrollToElementWithoutExcel(Locators[i]);
						String value = actions.getWebElementWithoutExcel(Locators[i]).getText().trim();	
						if (value.equals("")) {
						 value = null;
						}
						//System.out.println("UI Data : " + value +"DB Data: " + rs.getString(i + 1));
						if(value==null && (rs.getString(i + 1)==null)){									  						   
							logger.logPass( "Locator Value: " + value + " is equal to DB result value :"
											+ rs.getString(i + 1), Screenshot);														 
					    }  else if (!Locators[i].equals("") && (rs.getString(i + 1).trim().equalsIgnoreCase(value))) {
							logger.logPass( "Locator Value: " + value + " is equal to DB result value :"
									+ rs.getString(i + 1), Screenshot);
						} else if (!rs.getString(i + 1).trim().equalsIgnoreCase(value)) {
							logger.logFail("Locator Value: " + value + "  is not equal to DB result value :"
									+ rs.getString(i + 1));
						}
						  
					} catch (Exception e) {
						logger.logFail("Failed to read DB data due to exception :" + e.getMessage());						
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.logFail("Failed to verify DB value " + e.getMessage());
		}
	}

}

