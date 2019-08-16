package com.es.configuration;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Properties;

public class ReadPropertiesFile {
 
	//public static LinkedHashMap<String, String> readMap= new LinkedHashMap<String, String>();
	
  public LinkedHashMap<String, String> getRunPropertiesFile() {
		Properties prop = new Properties();
		InputStream input = null;
		LinkedHashMap<String, String> readMap= new LinkedHashMap<String, String>();
		try {
			input = new FileInputStream(System.getProperty("user.dir") + "/Run.properties");
			// load a properties file
			prop.load(input);
			// get the property value and print it out

			Object[] keySet = prop.keySet().toArray();

			for (int i = 0; i < keySet.length; i++) {
				readMap.put((String) keySet[i], prop.getProperty((String) keySet[i]));
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
        return readMap;
	}
  
  public LinkedHashMap<String, String> getDataPropertiesFile() {
		Properties prop = new Properties();
		InputStream input = null;
		LinkedHashMap<String, String> readMap= new LinkedHashMap<String, String>();
		try {
			input = new FileInputStream(System.getProperty("user.dir") + "\\src\\main\\resources\\dataFile.properties");
			// load a properties file
			prop.load(input);
			// get the property value and print it out

			Object[] keySet = prop.keySet().toArray();

			for (int i = 0; i < keySet.length; i++) {
				readMap.put((String) keySet[i], prop.getProperty((String) keySet[i]));
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
      return readMap;
	}
	
	public String readRunProperties(String skey){
		LinkedHashMap<String, String> readMap= getRunPropertiesFile();
		 String keyValue =readMap.get(skey);
		 return keyValue;
	}
	
	public String readDataProperties(String skey){
		LinkedHashMap<String, String> readMap= getDataPropertiesFile();
		 String keyValue =readMap.get(skey);
		 return keyValue;
	}
}
