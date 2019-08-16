package com.es.MainRun;

import java.util.ArrayList;
import java.util.List;
import java.text.*;
import org.testng.TestNG;
import org.testng.annotations.Test;

import com.es.configuration.*;

public class mainFile {

	public static void main(String[] args){
		new TestCaseConfiguration().run();		
		mainFile mf = new mainFile();
		mf.runTestNgXML();	
	}
	
	@Test
	public void mavenRun(){
		new TestCaseConfiguration().run();		
		mainFile mf = new mainFile();
		mf.runTestNgXML();	
	}
	
	public void runTestNgXML() {
		TestNG runner=new TestNG();
		try {
			List<String> suitefiles=new ArrayList<String>();
			suitefiles.add(System.getProperty("user.dir")+"\\src\\test\\resources\\testng.xml");
			runner.setTestSuites(suitefiles);
			runner.run();
		}catch(Exception e) {
			System.out.println("Exception : "+e.getMessage());
			e.printStackTrace();
		}
	}

}
