 package com.epsilon.configuration;

import java.lang.reflect.Method;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import com.epsilon.DBUtils.DBConfiguration;
import com.epsilon.Utilities.Email;
import com.epsilon.Utilities.Reporting;
import com.epsilon.pages.CommonMethods;
import com.epsilon.pages.ApplicationMethods;
import com.epsilon.configuration.Global;

public class Global {
	Keywords actions = new Keywords();
	Reporting logger = new Reporting();
	DBConfiguration db = new DBConfiguration();
	Email email = new Email();
    CommonMethods common = new CommonMethods();
   public ApplicationMethods appMethods = new ApplicationMethods();

	@BeforeTest
	public void beforeTest() {
		logger.beforeTestReporting();
		logger.beforeTestExtentReport();
		db.getDBConnection();
	}

	@BeforeMethod
	public void beforeMethod(Method methodName) {
		logger.methodLevelReporting(methodName);
		common.Launch();
		logger.beforeMethodExtentReport(methodName);
		actions.deleteDataPropFile();
	}

	@AfterMethod
	public void afterMethod(ITestResult result) {
		logger.afterMethodExtentReport(result);
	}

	@AfterTest
	public void afterTest() {
		logger.closeReporting();
		logger.afterTestExtentReport();
		db.closeDB();
		email.sendEmail();
		actions.quitBrowser();
	}

}
