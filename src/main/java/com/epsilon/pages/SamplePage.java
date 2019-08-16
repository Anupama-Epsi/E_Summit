package com.epsilon.pages;

import java.util.List;
import java.util.Date;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import com.epsilon.DBUtils.DBConfiguration;
import com.epsilon.Utilities.DataGeneration;
import com.epsilon.Utilities.DataProperty;
import com.epsilon.Utilities.Excel;
import com.epsilon.Utilities.Reporting;
import com.epsilon.configuration.Keywords;


public class SamplePage {
	
	public Keywords actions = new Keywords();
	public DBConfiguration db = new DBConfiguration();
	public Reporting logger = new Reporting();
	public CommonMethods common = new CommonMethods();
	public DataGeneration dataGenerate = new DataGeneration();
	public DataProperty dataProperty = new DataProperty();
	
	public void ValidateCustomerCustomAttributes(String datakey, String Screenshot) {
		try {
			logger.writeMethodName(actions.getMethodName());
			common.click_SearchResult(Screenshot);
			actions.waitForPageToLoad(30);			
			common.PersonalInforLeftNav("Account", "Custom Attributes");
			actions.waitForPageToLoad(60);			
			String[] locator = Excel.getLocatorData(datakey, actions.getMethodName());
			String[] Data = Excel.getData(datakey);															
				for (int i = 0; i < locator.length-1; i++) {			      
				    actions.jsScrollToElement(locator[i]);									
				 	if ((Data[i] != null ||!Data[i].equals("")) && (!Data[i].trim().equalsIgnoreCase("Ignore"))) {
						String locatorValue = null;			
						try {
							String locValue = actions.getWebElement(locator[i]).getAttribute("value");	
							if (locValue != null || !locValue.equals("")) {
								locatorValue = locValue;
							}
						}catch(Exception e) {
							locatorValue = actions.getWebElement(locator[i]).getText().trim();	
						}				
						String datavalue = dataGenerate.randomDataGenerator(Data[i],actions.getMethodName());	
						if (actions.getWebElement(locator[i]).isDisplayed()) {						
							logger.logPass("Success Message "+locator[i]+" value : "+locatorValue+" is equal to Data Sheet : ", Screenshot);
						} else {
							logger.logFail("Error Message "+locator[i]+" value : "+locatorValue+" is not equal to Data Sheet : ");
						}
					}
				}
		
		} catch (Exception e) {
			logger.logFail(
					"Validate Customer Custom Attributes page Functionality Failed due to exception :" + e.getMessage());
		}
	}	

	public void ProfileSummaryDBValidation(String DataKey, String Screenshot) {
		try {
			logger.writeMethodName(actions.getMethodName());
			String[] Locators = Excel.getLocatorData(DataKey, actions.getMethodName());
			String FirstName = actions.getWebElement("ProfileSearch_GetFirstName").getText();
			common.click_SearchResult(Screenshot);
			actions.waitForPageToLoad(60);
			common.PersonalInforLeftNav("Account", "Profile Summary");		
			actions.waitForPageToLoad(60);
			//String address = actions.getWebElement("ProfileSummary_AddrText").getText();
			//logger.logPass("address value: "+address, Screenshot);
			String QueryToFetchTables = dataGenerate.randomDataGenerator(
					common.getDataValue(DataKey, actions.getMethodName(), "ProfileSummaryQuery"),
					actions.getMethodName()).replace("##", FirstName);
			//System.out.println(QueryToFetchTables);
			common.GetNVerifyTextDB(QueryToFetchTables, Locators, Screenshot);
		} catch (Exception e) {
				logger.logFail("Failed to verify Profile Summary due to exception : " + e.getMessage());
			}
		}

	public void UpdateProfile(String Key, String Screenshot) {
		try {
			logger.writeMethodName(actions.getMethodName());
			common.click_SearchResult(Screenshot);
			actions.waitForPageToLoad(60);
			if (actions.getWebElementList("Victim").size() == 0) {
				if (actions.getWebElementList("Txt_ProgramInformation").size() == 0) {
					actions.click("Link_PersonalInfo");
				}
				if (Key != null && Key != "" && (!Key.equals("null"))) {
					String[] Data = Excel.getData(Key);
					String[] Locators = Excel.getLocatorData(Key, "UpdateProfile");
					for (int i = 0; i < Data.length; i++) {
						Thread.sleep(500);
						if (Locators[i] != null) {
							if (Data[i] == null || Data[i] == "") {
								Data[i] = "";
							}
							if (!Data[i].trim().equalsIgnoreCase("Ignore")) {
								actions.clearAndSet(Locators[i], Data[i], actions.getMethodName());
							}
							//the method is to write the excel input data to data file properties
							String InputData = Data[i];
							String LocatorData = Locators[i];
							dataGenerate.writeApiData(actions.getMethodName(), LocatorData, InputData);
						}
					}
					actions.scrollToView("Update_SaveButton");
					Thread.sleep(2000);
					actions.scroll(0, 100);
					actions.click("Update_SaveButton");
					actions.waitForPageToLoad(60);
					if (common.VerifyMessage("Profile saved successfully")) {
						logger.logPass("Updated the profile successfully", Screenshot);
					} else if (!common.getErrorMessage().equals("")) {
						logger.logWarning("Failed to Updated Customer Information due to error '"
								+ common.getErrorMessage() + "'");
					}
				}

			} else if (actions.getWebElementList("Victim").size() > 0) {
				logger.logWarning("Cannot perform Update Enrollment action on the victim Profile");
			}
		} catch (InterruptedException e) {
			logger.logFail("Failed to update profile due to exception " + e.getMessage());
		}
	}


	public void VerifyProfilePage(String dataKey, String Screenshot) {
		try {
			logger.writeMethodName(actions.getMethodName());
			common.click_SearchResult(Screenshot);
			actions.waitForPageToLoad(60);
			String Name = common.getDataValue(dataKey, actions.getMethodName(), "Name").toUpperCase();
			actions.assertTrue(actions.getWebElement("ProfileHeader_Name").getText().contains(Name), "Profile Info Page should be displayed for searched User", Screenshot);
		}catch(Exception e) {
			logger.logFail("Failed to verify Profile page due to exception " + e.getMessage());
		}
	}

	public void ProfileBanner(String datakey, String Screenshot) {
		try {
			logger.writeMethodName(actions.getMethodName());	
			String[] ProfileSearchLocatorValue = new String[8] ;
			String[] ProfileSearchLocator = { "ProfileSearch_GetFirstName","ProfileSearch_GetLastName","ProfileSearch_GetProgram", "ProfileSearch_GetCardNumb", "ProfileSearch_GetProfileStatus",
					"ProfileSearch_GetEnrollStatus", "ProfileSearch_GetEmailAddress","ProfileSearch_GetPhoneNumber"};
			for (int i = 0; i < ProfileSearchLocator.length; i++) {
			ProfileSearchLocatorValue[i] = actions.getWebElement(ProfileSearchLocator[i]).getText().trim();
			logger.logPass("Profile Search Locator "+ProfileSearchLocator[i]+" has value : "+ProfileSearchLocatorValue[i], Screenshot);
			}		
			common.click_SearchResult(Screenshot);
			actions.waitForPageToLoad(60);
			actions.waitExplicit("Banner_TableView", 5);
			actions.assertTrue(actions.isElementPresent("Banner_TableView"), "ProfileBanner Is Displayed", Screenshot);			
			actions.click("Banner_CollapseExpand");
			actions.waitExplicit(null, 1);
			actions.assertFalse(actions.isElementPresent("Banner_TableView"), "ProfileBanner Should Collapse Displayed", Screenshot);
			actions.click("Banner_CollapseExpand");			
			actions.waitExplicit(null, 1);
			String[] BannerLocator = {"Banner_FirstName","Banner_LastName","Banner_Program", "Banner_CardNumber", "Banner_ProfileStatus",
					"Banner_EnrollmentStatus", "Banner_EmailAddress","Banner_PhoneNumber"};
			for (int j = 0; j < BannerLocator.length; j++) {
				if (ProfileSearchLocatorValue[j] != null || !ProfileSearchLocatorValue[j].equals("") || !ProfileSearchLocatorValue[j].isEmpty()) {
				   //logger.logPass("ProfileSearchLocatorValue inside ifs loop : " +ProfileSearchLocatorValue[j], Screenshot); 
			       WebElement VerifyBannerLocator = actions.getWebElementWithoutExcel(Excel.getLocator(BannerLocator[j]).replace("##", ProfileSearchLocatorValue[j]));
			       actions.assertTrue(actions.isElementPresent(VerifyBannerLocator), "Customer Search Result : " +ProfileSearchLocatorValue[j]+ " Displayed in Profile Banner", Screenshot);
			      } 
				else { logger.logFail("Customer Search Result : " +ProfileSearchLocatorValue[j]+ " Not displayed in Profile Banner", Screenshot);
		         } 
			  }
		   }  catch(Exception e) {
			logger.logFail("Failed to verify Profile banner due to exception " + e.getMessage());
		 }	  
	 }

	
	public void AddTransactionHeader(String DataKey, String Screenshot) {

		try {
			logger.writeMethodName(actions.getMethodName());
			common.click_SearchResult(Screenshot);
			actions.waitForPageToLoad(30);
			if (actions.getWebElementList("Victim").size() == 0) {
				actions.waitForPageToLoad(30);
				common.PersonalInforLeftNav("Activity", "Transactions");
				actions.waitForPageToLoad(30);
				actions.click("Transaction_AddNewButton");
				actions.waitForPageToLoad(30);
				common.enterAllValues(actions.getMethodName(), DataKey);
			} else if (actions.getWebElementList("Victim").size() > 0) {
				logger.logFail("Cannot perform any Transaction action on the victim Profile");
			}

		} catch (Exception e) {
			logger.logFail("Clicking on Save trancaction failed due to exception " + e.getMessage());
		}
	}
	public void SaveTransactions(String extraParam, String Screenshot) {
		try {
			logger.writeMethodName(actions.getMethodName());
			Thread.sleep(1000);
			actions.jsScrollToElement("Save_Button");
			Thread.sleep(1000);
			actions.click("Save_Button");
			logger.logPass("Clicking on the Transaction Save button", "Y");
			actions.waitForPageToLoad(30);
			common.VerifySuccessPage("Transaction added", Screenshot);
		} catch (Exception e) {
			logger.logFail("Clicking on Save transaction failed due to exception " + e.getMessage());
		}
	}

	public void SaveTransaction(String extraParam, String Screenshot) {
		try {
			logger.writeMethodName(actions.getMethodName());
			Thread.sleep(1000);
			// This is used to verify if the AddCoupon is Added or not
			List<String> firstCouponOutputGridList = common.GetGridValue("CouponGrid", Screenshot);

			// This is used to verify if the LineItem is Added or not
			List<String> LineItemGridList = common.GetGridValue("AddLineItem_Grid", Screenshot);

			// This is to verify if Tender is added or not
			List<String> TenderGridList = common.GetGridValue("TenderGrid", Screenshot);

			// This is to verify if Certificate is added or not
			List<String> CertificateGridList = common.GetGridValue("CertificateGrid", Screenshot);
			// CommonMethods.ProfilePointsBefore= getProfilepoints();
			actions.jsScrollToElement("Save_Button");
			Thread.sleep(1000);
			actions.click("Save_Button");
			logger.logPass("Clicking on the Transaction Save button", "Y");
			actions.waitForPageToLoad(30);
			common.VerifySuccessPage("Transaction added", Screenshot);
			// ProfilePointsAfter = common.getProfilepoints();
			if (common.VerifyMessage("Successful")) {
			// This is used to verify the Coupon output Transaction Grid
			common.verifyOutputGrid(firstCouponOutputGridList, "Coupon_Details", Screenshot, "CouponsTab");

			// This is used to verify the Tender output Transaction Grid
			common.verifyOutputGrid(TenderGridList, "Tender_Details", Screenshot, "TendersTab");

			// This is used to verify the LineItem output Transaction Grid
			common.verifyOutputGrid(LineItemGridList, "AddLineItem_Details", Screenshot, "LineItemsTab");

			// This is used to verify the Certificate output Transaction Grid
			common.verifyOutputGrid(CertificateGridList, "Certificate_Details", Screenshot, "CertificatesTab");
			}
			else if (common.VerifyMessage("Invalid")) {
				logger.logPass("Transaction Details are empty since the enrollment status is not Active", "N");
			}		
			
		} catch (Exception e) {
			logger.logFail("Clicking on Save transaction failed due to exception " + e.getMessage());
		}

	}

	public void VerifySuspendTrxn(String DataKey, String Screenshot) {
		try {
			logger.writeMethodName(actions.getMethodName());
			actions.waitForPageToLoad(30);
			Thread.sleep(2000);
			common.click_SearchResult(Screenshot);
			actions.waitForPageToLoad(30);
			if (actions.getWebElementList("Victim").size() == 0) {
				actions.waitForPageToLoad(30);
				common.PersonalInforLeftNav("Activity", "Transactions");
				if(actions.getWebElement("collaspseExpand").getAttribute("class").equals("fa fa-minus"))
				{
				actions.javaScriptClick("collaspseExpand");
				}
				actions.click("Trn_Suspend_Tab");
				actions.waitForPageToLoad(30);
				actions.click("Txn_Suspend_Clear");
				actions.waitForPageToLoad(30);
				common.enterAllValues(actions.getMethodName(), DataKey);
				actions.jsScrollToElement("Txn_Suspend_Search");
				actions.click("Txn_Suspend_Search");
				logger.logPass("Clicking on the Search Button", Screenshot);
				Thread.sleep(3000);

				if (actions.getWebElementList("Txn_Suspend_Result").size() > 0) {
					logger.logPass("Suspend Transaction Created	successfully", Screenshot);
				} else if (actions.getWebElementList("No_Search_Item").size() > 0) {
					logger.logWarning("No element displayed under Suspended Transaction");
				}
			} else if (actions.getWebElementList("Victim").size() > 0) {
				logger.logWarning("Cannot Verify Suspend Transaction action on the victim Profile");
			}
		} catch (Exception e) {
			logger.logFail("Verify Suspend transaction failed due to exception " + e.getMessage());
		}
	}

	
	public void VerifyTransaction(String DataKey, String Screenshot) {
		try {
			logger.writeMethodName(actions.getMethodName());
			Thread.sleep(2000);
			common.click_SearchResult(Screenshot);
			if (actions.getWebElementList("Victim").size() == 0) {
				common.PersonalInforLeftNav("Activity", "Transactions");
				actions.click("Trn_Tab");
				actions.waitForPageToLoad(30);
				if(actions.getWebElement("collaspseExpand").getAttribute("class").equals("fa fa-minus"))
				{
				actions.javaScriptClick("collaspseExpand");
				}
				actions.click("Trxn_Clear");
				actions.waitForPageToLoad(30);
				common.enterAllValues(actions.getMethodName(), DataKey);
				actions.jsScrollToElement("Txn_Search");
				actions.click("Txn_Search");
				logger.logPass("Clicking on Search Transaction ", Screenshot);
				Thread.sleep(1000);
				actions.waitForPageToLoad(30);
				if (actions.getWebElementList("Txn_Search_Result").size() > 0) {
					logger.logPass("Transaction Created	successfully", Screenshot);
					actions.waitForPageToLoad(30);
					actions.jsScrollToElement("Trxn_Result_ID");
					actions.javaScriptClick("Trxn_Result_ID");
					actions.waitForPageToLoad(30);
					logger.logPass("Navigating to the Transaction Details", "Y");
				} else if (actions.getWebElementList("No_Search_Item").size() > 0) {
					// System.out.println("Element is displayed :" +
					// actions.getWebElement("No_Search_Item").isDisplayed());
					logger.logWarning("No element displayed under Transaction");
				}
			} else if (actions.getWebElementList("Victim").size() > 0) {
				logger.logWarning("Cannot perform any action on the victim Profile");
			}

		} catch (Exception e) {
			logger.logFail("Verify Transaction failed due to exception " + e.getMessage());
		}
	}


	public int GetTabCount(String Locator) {
		WebElement element = actions.getWebElement(Locator);
		int spacePosB = element.getText().indexOf("(");
		int spacePosE = element.getText().indexOf(")");
		return Integer.parseInt(element.getText().substring(spacePosB + 1, spacePosE));

	}

	public int RowCount(String gridLocatorValueFromExcel) {
		WebElement table = actions.getWebElement(gridLocatorValueFromExcel);
		List<WebElement> rows = table.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
		return rows.size();
	}

	public void TransactionResultTabs(String DataKey, String Screenshot) {
		try {
			logger.writeMethodName(actions.getMethodName());
			int TendersTab = GetTabCount("TendersTab");
			int CouponsTab = GetTabCount("CouponsTab");
			int LineItemsTab = GetTabCount("LineItemsTab");
			int CertificatesTab = GetTabCount("CertificatesTab");
			int MomentsTab = GetTabCount("MomentsTab");
			int PointsTab = GetTabCount("PointsTab");

			int LineItemsTabRow = RowCount("AddLineItem_Details");
			actions.click("TendersTab");
			int TendersTabRow = RowCount("Tender_Details");
			actions.click("CouponsTab");
			int CouponsTabRow = RowCount("Coupon_Details");
			actions.click("CertificatesTab");
			int CertificatesTabRow = RowCount("Certificate_Details");
			actions.click("MomentsTab");
			int MomentsTabRow = RowCount("Moments_Grid");
			actions.click("PointsTab");
			int PointsTabRow = RowCount("Point_Grid");

			actions.assertTrue(TendersTab == TendersTabRow,
					"Tender Tab Count :" + TendersTab + " Tenders Record Count :" + TendersTabRow, Screenshot);
			actions.assertTrue(CouponsTab == CouponsTabRow,
					"Coupon Tab Count :" + CouponsTab + " Coupon Record Count :" + CouponsTabRow, Screenshot);
			actions.assertTrue(LineItemsTab == LineItemsTabRow,
					"Line Item Tab Count :" + LineItemsTab + " Line Items Record Count :" + LineItemsTabRow,
					Screenshot);
			actions.assertTrue(CertificatesTab == CertificatesTabRow,
					"Certificates Tab Count :" + CertificatesTab + " Certificates Record Count :" + CertificatesTabRow,
					Screenshot);
			actions.assertTrue(MomentsTab == MomentsTabRow,
					"Moment Tab Count :" + MomentsTab + " Moment Record Count :" + MomentsTabRow, Screenshot);
			actions.assertTrue(PointsTab == PointsTabRow,
					"Point Tab Count :" + PointsTab + " Points Record Count :" + PointsTabRow, Screenshot);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	
	
	
}
