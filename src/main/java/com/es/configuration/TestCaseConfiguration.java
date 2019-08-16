package com.es.configuration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import org.apache.log4j.Logger;

import com.es.Utilities.Excel;

public class TestCaseConfiguration {

	private static Excel excel = new Excel();
	private static FileWriter writer;
	private static File file = null;
	private static String executableTestPath = System.getProperty("user.dir")+"/src/test/java/com/es/testCases/ExecutableTest.java";
	private static ReadPropertiesFile read =  new ReadPropertiesFile();
	private static String testRunnerSheet = read.readRunProperties("TestRunnerSheet").trim();
	public static Logger l4jlogger = Logger.getLogger("E_Summit");
	
	public String run(){
		String[] allTestCases = null;
		String classname = null;
		int PriorityCounter = 0;
		System.out.println("*****************************************************************************************************");
		System.out.println("					AUTOMATION EXECUTION");
		System.out.println("*****************************************************************************************************");
		File fileCreated = createTestCaseFile();
		classname = file.getName().split("\\.")[0];
		writePackageAndImportsAndClassName(classname);
		System.out.println("Compilation Started!.....");
		String[] multipleSheet = null;
		
	   if(testRunnerSheet.equalsIgnoreCase("All")) {
	     multipleSheet = removeNullValues(Excel.getSheetName("TR_"));
	   }else {
		   multipleSheet = testRunnerSheet.split(",");
	   }
		for(int j=0; j<multipleSheet.length; j++) {
			String currentTC = multipleSheet[j].trim();
			allTestCases = excel.getTestCaseName(currentTC);
			allTestCases = removeNullValues(allTestCases);
			for(int i=0; i<allTestCases.length; i++) {	
				String[] allTestSteps = excel.getTestSteps(currentTC,allTestCases[i]);
				allTestSteps = removeNullValues(allTestSteps);
				if(allTestSteps.length!=0) {
					writeMethodTemplate(String.valueOf(PriorityCounter),allTestCases[i]);
					PriorityCounter++;
					for(int k=0; k<allTestSteps.length; k++) {
						String[] testStep = allTestSteps[k].split(",");
						if(testStep.length>1)
							writeTestSteps(testStep[0],testStep[1],testStep[2]);
					}
					closeBracket();
				}
			}
		}
		closeBracket();
		closeWriter();
		compileExecutableTest();
		System.out.println("Compilation Completed!");
		return classname;
	}

	public void compileExecutableTest() {
		try {
			File srcFile = new File(executableTestPath);
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
			File directory1 = new File(System.getProperty("user.dir")+"\\target\\test-classes");
			if(!directory1.exists())
				directory1.mkdir();
			File parentDirectory = directory1;
			fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(parentDirectory));
			Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(srcFile));
			compiler.getTask(null, fileManager, null, null, null, compilationUnits).call();
			fileManager.close();
		}catch(Exception e) {
			l4jlogger.info("Failed to compile the executable test java file due to exeception "+e.getMessage());
		}
	}

	public static File createTestCaseFile() {
		try {
			file = new File(executableTestPath);
			writer = new FileWriter(file);
		}catch(Exception e) {
			System.err.println("Failed to create testcase file due to exception "+e.getMessage());
		}
		return file;
	}

	public static String[] removeNullValues(String[] arrayString) {
		try {
			ArrayList<String> list = new ArrayList<String>();
			for (String s : arrayString) {
				if (s==null)
					s=null;
				else
					list.add(s);
			}
			arrayString = list.toArray(new String[list.size()]);
		}catch(Exception e) {
			l4jlogger.info("Failed to remove null values from String[] due to exception "+ e.getMessage());
		}
		return arrayString;
	}

	public static void writePackageAndImportsAndClassName(String className) {
		try {
			writer.write("package com.es.testCases;\n");
			writer.write("import org.testng.annotations.Test;\n");
			writer.write("import com.es.configuration.Global;\n");
			writer.write("public class "+className+" extends Global{\n");
		} catch (IOException e) {
			l4jlogger.info("writePackageAndImportsAndClassName");
			e.printStackTrace();
		}
	}

	public static void writeMethodTemplate(String PriorityCounter , String testCaseName) {
		try {
			writer.write("@Test(priority = "+PriorityCounter+")\n");
			writer.write("public void "+testCaseName+"() {\n");
		} catch (IOException e) {
			l4jlogger.info("writeMethodTemplate");
			e.printStackTrace();
		}
	}

	public static void writeTestSteps(String MethodName, String DataKey, String Screenshot_Flag) {
		try {
			if(DataKey.equals("")) {
				writer.write("appMethods."+MethodName+"(\""+Screenshot_Flag+"\");\n");
			}
			else {
				writer.write("appMethods."+MethodName+"(\""+DataKey+"\",\""+Screenshot_Flag+"\");\n");
			}
		} catch (IOException e) {
			l4jlogger.info("writeTestSteps");
			e.printStackTrace();
		}
	}

	public static void closeBracket() {
		try {
			writer.write("}\n\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void closeWriter() {
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
