package com.epsilon.pages;

import org.openqa.selenium.WebElement;

import com.epsilon.DBUtils.DBConfiguration;
import com.epsilon.Utilities.DataGeneration;
import com.epsilon.Utilities.Excel;
import com.epsilon.Utilities.Reporting;
import com.epsilon.configuration.BrowserConfig;
import com.epsilon.configuration.Keywords;

public class SearchPage {

	public Keywords actions = new Keywords();
	public DBConfiguration db = new DBConfiguration();
	public Reporting logger = new Reporting();
	public CommonMethods common = new CommonMethods();
	public DataGeneration dataGenerate = new DataGeneration();

	public void VerifySearch(String SearchKey, String Screenshot) {
		try {
			common.click_SearchResult(Screenshot);
			actions.waitForPageToLoad(60);
			String[] Data = Excel.getData(SearchKey);
			Data = actions.removeNullValues(Data);
			int flag = 0;
			//for(int i = 0; i<Data.length; i++) {
			//	if(actions.webDriver.getPageSource().contains(Data[i])) {
			//		flag+=1;
			//	}
			//}
			for(int i = 0; i<Data.length; i++) {
				String dataVerify = dataGenerate.randomDataGenerator(Data[i], actions.getMethodName());
				if(actions.webDriver.getPageSource().contains(dataVerify)) {
					flag+=1;
				}
			}
			actions.assertTrue(flag==Data.length, "Verification of Searched attributes", Screenshot);
		}catch(Exception e) {
			logger.logFail("Failed to Verify search results due to exception : " + e.getMessage());
		}
	}

	public void Search(String SearchKey, String Screenshot) {
		try {
			actions.waitExplicit(null, 1);
			logger.writeMethodName(actions.getMethodName());
			//BrowserConfig.webDriver.navigate().refresh();
			actions.waitForPageToLoad(30);
			actions.waitExplicit("Header_Customer", 10);			
			actions.click("Header_Customer");
			actions.waitForPageToLoad(10);
			if(!actions.isElementPresent("Search_Clear"))
				actions.click("Banner_BackLink");
			actions.waitForPageToLoad(60);
			actions.click("Search_Clear");
			actions.waitForPageToLoad(60);
			BrowserConfig.webDriver.navigate().refresh();
			actions.waitForPageToLoad(60);
			actions.click("Search_AdvanceLink");
			actions.waitForPageToLoad(60);
			common.enterAllValues(actions.getMethodName(), SearchKey);
			actions.click("Search_SearchButton");
			logger.logPass("Clicked on Search button in Search page", Screenshot);
			if (actions.getWebElementList("No_Search_Item").size() > 0) {
				logger.logWarning("No Search Profile displayed");
			}else if (actions.getWebElementList("Victim_Search").size() > 0) {
				logger.logWarning("Victim Profile displayed");
			}else {
				actions.waitForPageToLoad(60);
				//searchAllValues("Search", SearchKey, Screenshot);
			}
		} catch (Exception e) {
			logger.logFail("Failed to Search/Search results due to exception : " + e.getMessage());
		}
	}

	public void ClearSearch(String Screenshot) {
		try {
			actions.click("Search_Clear");
			actions.waitForPageToLoad(60);
		} catch (Exception e) {
			logger.logFail("Failed to clear search due to exception : " + e.getMessage());
		}
	}

	public void CustomerAdvanceSearch(String extraParam, String Screenshot) {
		try {
			logger.writeMethodName(actions.getMethodName());

			//Verify On clicking Customer section Search Page displays
			actions.click("Header_Customer");
			actions.waitForPageToLoad(30);
			actions.assertTrue(actions.isElementPresent("Search_PageValidation"), "Clicking On 'Customer' tab opens Search Page", Screenshot);
			actions.assertTrue(actions.getWebElement("Search_SearchButton").getAttribute("type").contains("button"), "Search should be displayed as a button", Screenshot);

			//Verify Clear button
			actions.assertTrue(actions.isElementPresent("Search_ClearButton"), "Clear link should be displayed in Search Page", Screenshot);

			//New Customer button should be available in the Search Page
			actions.assertTrue(actions.isElementPresent("NewEntity_Button"), "New Customer button should be present in top right", Screenshot);

			//On clicking Advance Link Expanded section is displayed
			int CordinateX = actions.getWebElement("Search_AdvanceLink").getLocation().getX();
			int CordinateY = actions.getWebElement("Search_AdvanceLink").getLocation().getY();

			int CordinateX_FN = actions.getWebElement("Search_FirstName").getLocation().getX();
			int CordinateY_FN = actions.getWebElement("Search_FirstName").getLocation().getY();
			actions.assertTrue(CordinateX==CordinateX_FN&&CordinateY>CordinateY_FN, "Advance Link is below the first search box on left side ", Screenshot);

			actions.click("Search_AdvanceLink");
			actions.waitExplicit(null,1);
			actions.assertTrue(actions.isElementPresent("Search_PostingKeyName"), "Advance link displays expanded Advance section ", Screenshot);

			//On clicking Hide Advance Link section should be collapsed
			actions.click("HideAdvanced_Link");
			actions.waitExplicit(null,1);
			actions.assertFalse(actions.isElementPresent("Search_PostingKeyName"), "Hide Advance link colapse Advance section ", Screenshot);
			actions.click("Search_AdvanceLink");
			actions.waitExplicit(null,1);

			//Verify If Posting key name has dropdown
			actions.click("Search_PostingKeyName");
			actions.assertTrue(actions.isElementPresent("Advanced_Posting_Key_Name"), "Posting Key field is a dropdown ", Screenshot);
			actions.click("Search_PostingKeyName");
			actions.waitExplicit(null,1);

			//Verify if Posting key name and value is available in page
			actions.assertTrue(actions.isElementPresent("Search_PostingKeyName"), "Verify Posting key Name Field in Search screen", Screenshot);
			actions.assertTrue(actions.isElementPresent("Search_PostingKey_Checkbox"), "Verify Checkbox present below Psoting Key Value ", Screenshot);
			actions.assertTrue(actions.getWebElement("Search_PostingKey_CheckboxText").getText().contains("Include all history for this posting key"), 
					"Text 'Include all history for this posting key' should be present against the checkbox", Screenshot);
			actions.assertTrue(actions.isElementPresent("Search_PostingKeyValue"), "Verify Posting key Value Field in Search screen", Screenshot);

		}catch(Exception e) {
			logger.logFail("Failed to Verify Customer Advance Search due to exception "+e.getMessage());
		}
	}

	public void SearchAndSelectProfile (String dataKey, String Screenshot) {
		try {
			logger.writeMethodName(actions.getMethodName());

			//Verify after logging in the user is on Search Page for Admin user
			actions.assertTrue(actions.isElementPresent("Search_PageValidation"), "After logging in admin user should be on Search Page", Screenshot);

			//Verify that clicking on a searched row selects and opens a customer
			Search(dataKey, Screenshot);
			actions.waitExplicit(null, 2);
			actions.assertTrue(actions.isElementPresent("Search_Results"), "Search result should be displayed on Searching", Screenshot);
			actions.waitExplicit(null, 2);
			common.click_SearchResult(Screenshot);
			actions.assertTrue(actions.isElementPresent("Txt_ProgramInformation"), " User 'Personal Info' Page is displayed on clicking the searched result", Screenshot);

			//Verify Back button functionality 
			actions.assertTrue(actions.isElementPresent("Profile_CloseButton"), "Back Button should be displayed in Personal Info Page", Screenshot);
			actions.click("Profile_CloseButton");
			actions.waitForPageToLoad(30);
			actions.assertTrue(actions.isElementPresent("Search_PageValidation"), "Clicking on Back button should navigate user back from Personal Info Page", Screenshot);	
		}catch(Exception e) {
			logger.logFail("Failed to Search And Select Profile due to exception "+e.getMessage());
		}
	}

	public void VerifyProfileSearch(String extraParam, String Screenshot) {
		try {
			logger.writeMethodName(actions.getMethodName());

			//Verify Search Page appears after navigating from different page
			if(!actions.isElementPresent("Search_Clear")) {
				actions.click("Header_Customer");
				logger.logPass("Clicked on Customer Tab", Screenshot);
			}
			actions.click("ProgramManagement_Tab");
			actions.waitForPageToLoad(30);
			logger.logPass("Clicked on ProgramManagement Tab", Screenshot);
			actions.click("Header_Customer");
			actions.waitForPageToLoad(30);
			logger.logPass("Clicked on Customer Tab again", Screenshot);
			actions.assertTrue(actions.isElementPresent("Search_PageValidation"), "Regular customer UI for Search Page is displayed", Screenshot);
		}catch(Exception e) {
			logger.logFail("Failed to verify Profile Search due to exception "+e.getMessage());
		}
	}

}
