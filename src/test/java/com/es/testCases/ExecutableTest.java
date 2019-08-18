package com.es.testCases;
import org.testng.annotations.Test;
import com.es.configuration.Global;
public class ExecutableTest extends Global{
@Test(priority = 0)
public void Login() {
appMethods.Login("Login_id","Y");
}

}

