package com.epsilon.pages;

import java.util.Set;

import org.testng.Assert;

import com.epsilon.DBUtils.DBConfiguration;
import com.epsilon.Utilities.Excel;
import com.epsilon.Utilities.Reporting;
import com.epsilon.configuration.BrowserConfig;
import com.epsilon.configuration.Keywords;

public class LoginPage {

	public Keywords actions = new Keywords();
	public DBConfiguration db = new DBConfiguration();
	public Reporting logger = new Reporting();
	public CommonMethods common = new CommonMethods();

	public void LaunchAndLogin(String Key, String Screenshot) {
		logger.writeMethodName(actions.getMethodName());
		BrowserConfig config = new BrowserConfig();
		String[] Data = Excel.getData(Key);
		String[] Locators = Excel.getLocatorData(Key, "LaunchAndLogin");
		if (Locators[0].equals("Url")) {
			config.Launch(Data[0]);
			logger.logPass("Application Launched successfully", Screenshot);
		}
		try {
			for (int i = 1; i < Locators.length; i++) {
				if (Locators[i] != null && Data[i] != null && Data[i] != "") {
					if (Locators[i] != null) {
						actions.setValue(Locators[i], Data[i], actions.getMethodName());
					}
				}
			}
			actions.click("Login_LoginButton");
			logger.logPass("Clicked on Login button", Screenshot);
			actions.waitForPageToLoad(60);
		} catch (Exception e) {
			logger.logFail("Failed to launch and login to application due to exception " + e.getMessage());
		}
	}
	
	public void Login(String datakey, String Screenshot) {
		try {
		logger.writeMethodName(actions.getMethodName());
		if(actions.getWebElementList("Login_LoginButton").size()>0) {
		    common.enterAllValues(actions.getMethodName(), datakey);
			actions.click("Login_LoginButton");
			logger.logPass("Clicked on Login button", Screenshot);
			actions.waitForPageToLoad(60);
		}else {
			logger.logInfo("User is already logged in");
		}
	}
		catch (Exception e) {
			logger.logFail("Failed to Login to application due to exception " + e.getMessage());
		}
	}

	/* Functions in QuitBrowser */
	public void QuitBrowser(String extraParam, String Screenshot) {
		logger.writeMethodName(actions.getMethodName());
		AccountLogout("null", Screenshot);
		actions.quitBrowser();
	}

	/*
	 * Account Logout it is used to logout
	 */
	public void AccountLogout(String extraParam, String Screenshot) {
		try {
			actions.waitForPageToLoad(60);
			actions.javaScriptClick("Profile_Account");
			actions.waitForPageToLoad(60);
			actions.javaScriptClick("SignOut");
			logger.logPass("Logout is successful", Screenshot);

		} catch (Exception e) {
			logger.logFail("error in login out of the account successful" + e.getMessage());
		}
	}

	public void ProfileIcon(String DataKey, String Screenshot) {
		try {
			logger.writeMethodName(actions.getMethodName());
			actions.click("Profile_Account");
			logger.logPass("Navigating to Profile Account section", Screenshot);
			actions.waitForPageToLoad(60);

			// Validation Welcome User
			String user = common.getDataValue(DataKey, "LaunchAndLogin", "Login_Username").toUpperCase();
			String Welcome = Excel.getLocator("WelcomeUser").replace("##", user);
			System.out.println(Welcome);
			actions.assertTrue(actions.isElementPresent(actions.getWebElementWithoutExcel(Welcome)),
					"Validating the Welcome User", Screenshot);

			// Validating the Change Password
			actions.assertTrue(actions.isElementPresent("ChangePwd"), "Validating the Change Password", Screenshot);

			// Validating the SignOut
			actions.assertTrue(actions.isElementPresent("SignOut"), "Validating the SignOut", Screenshot);

			actions.click("Profile_Account");
//			actions.javaScriptClick("SignOut");
//			logger.logPass("Logout is successful", Screenshot);
		} catch (Exception e) {
			logger.logFail("error in login out of the account successful" + e.getMessage());
		}
	}

	public void VerifyIgniteFromLogin(String extraParam, String Screenshot) {
		try {
			logger.writeMethodName(actions.getMethodName());

			actions.waitForPageToLoad(60);
			// get the window handles
			actions.click("Help_DropDown");
			actions.assertTrue(actions.isElementPresent("Version_Text"), "Version text is present in the dropdown",
					Screenshot);
			actions.click("Ignite_Link");
			actions.waitForPageToLoad(60);
			Set<String> handles = actions.getWindowHandles();
			String parentWindow = actions.getWindowHandle();
			handles.remove(parentWindow);
			// Iterate to the windows
			String handle = handles.iterator().next();
			actions.switchToWindowHandle(handle);
			// Validate the Ignite window logo
			actions.assertTrue(actions.isElementPresent("Ignite_Logo"), "Ignite Logo is present in the new window",
					Screenshot);
			actions.closeWindow();
			actions.switchToWindowHandle(parentWindow);

		} catch (Exception e) {
			logger.logFail("error navigating to Ignite" + e.getMessage());
		}
	}

}
