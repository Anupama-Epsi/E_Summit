package com.es.pages;

import java.util.List;

import com.es.DBUtils.APICommon;

public class ApplicationMethods {
	// Methods for Login Page
	public void LaunchAndLogin(String Key, String Screenshot) {
		new LoginPage().LaunchAndLogin(Key, Screenshot);
	}

	public void Login(String Key, String Screenshot) {
		new LoginPage().Login(Key, Screenshot);
	}

	public void QuitBrowser(String extraParam, String Screenshot) {
		new LoginPage().QuitBrowser(extraParam, Screenshot);
	}

	public void AccountLogout(String extraParam, String Screenshot) {
		new LoginPage().AccountLogout(extraParam, Screenshot);
	}

	public void VerifyIgniteFromLogin(String Key, String Screenshot) {
		new LoginPage().VerifyIgniteFromLogin(Key, Screenshot);
	}

	public void Search(String SearchKey, String Screenshot) {
		new SearchPage().Search(SearchKey, Screenshot);
	}
	
	public void VerifyDBData(String datakey, String Screenshot) {
		new CommonMethods().VerifyDBData(datakey, Screenshot);
	}

	public void API_AddTransaction(String datakey, String Screenshot) {
		new APICommon().API_AddTransaction(datakey, Screenshot);
	}

	public void CustomerAdvanceSearch(String datakey, String Screenshot) {
		new SearchPage().CustomerAdvanceSearch(datakey, Screenshot);
	}

	public void SearchAndSelectProfile(String datakey, String Screenshot) {
		new SearchPage().SearchAndSelectProfile(datakey, Screenshot);
	}


	public void VerifyProfileSearch(String dataKey, String Screenshot) {
		new SearchPage().VerifyProfileSearch(dataKey, Screenshot);
	}

	public void ProfileIcon(String Key, String Screenshot) {
		new LoginPage().ProfileIcon(Key, Screenshot);
	}

	public void VerifySearch(String datakey, String Screenshot) {
		new SearchPage().VerifySearch(datakey, Screenshot);
	}
	
	public void API_GetProfileAndVerify(String datakey, String Screenshot) {
		new APIVerification().API_GetProfileAndVerify(datakey, Screenshot);
		}
	
	public void API_GetMergeAndVerify(String datakey, String Screenshot) {
		new APIVerification().API_GetMergeAndVerify(datakey, Screenshot);
		}
	public void API_Post(String Key, String Screenshot) {
		new APICommon().API_Post(Key, Screenshot);
	}
	
	public void API_Get(String Key, String Screenshot) {
		new APICommon().API_Get(Key, Screenshot);
	}
	
	public void API_GetAndVerify(String dataKey, String Screenshot) {
		new APICommon().API_GetAndVerify(dataKey, Screenshot);
	}
	
	public void VerifyProfileUpdateBatch(String SearchKey, String Screenshot) {
		new BatchPage().VerifyProfileUpdateBatch(SearchKey, Screenshot);
	}
	
	public void createFileToSpecifiedPath(String datakey, String Screenshot) {
		new BatchPage().createFileToSpecifiedPath(datakey, Screenshot);
	}
	
	public void ProfileSummaryDBValidation(String datakey, String Screenshot) {
		new SamplePage().ProfileSummaryDBValidation(datakey, Screenshot);
	}
	
}
