package com.epsilon.testCases;
import org.testng.annotations.Test;
import com.epsilon.configuration.Global;
public class ExecutableTest extends Global{
@Test(priority = 0)
public void LoginAndSearchProfile() {
appMethods.Login("Login_id_okta","Y");
appMethods.Search("Search_FN","Y");
}

}

