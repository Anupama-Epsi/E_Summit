package com.es.Utilities;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.text.DecimalFormat;

import com.es.DBUtils.DBConfiguration;
import com.es.configuration.ReadPropertiesFile;
import com.es.pages.CommonMethods;

public class DataGeneration{

	private DBConfiguration db = new DBConfiguration();
	private Reporting logger = new Reporting();
	private ReadPropertiesFile read = new ReadPropertiesFile();
	DataProperty dataProp = new DataProperty();

	public String generateRandomNumber(String numberSize) {
		String MinSize = "1";
		String MinString = "0";
		String MaxSize = "9";
		String MaxString = "9";
		int numbSize = Integer.parseInt((numberSize));
		for (int i=1; i<numbSize ; i++) {
			MinSize +=MinString;
		}
		for (int i=1; i<numbSize ; i++) {
			MaxSize +=MaxString;
		}
		String minSizeString = String.valueOf(MinSize);
		String maxSizeString = String.valueOf(MaxSize);
		long maxnumber = Long.parseLong(maxSizeString);
		long minnumber = Long.parseLong(minSizeString);
		long random = minnumber + (long)(Math.random() * ((maxnumber - minnumber) + 1));
		String randNumb = String.valueOf(random);
		return randNumb;
	}


	public String generateTimeStamp() {
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		String timestamp = ""+ts.getTime();
		return timestamp;
	}
	
	public String generateDateTimeStamp() {
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyymmddHHmmss");
		String Datetimestamp = ""+sdf.format(ts);
		System.out.println("Datetimestamp : "+Datetimestamp);
		return Datetimestamp;
	}

	public String getDate(String pastOrFuture, String date) {
		String returnDate = "";
		int dateCount = Integer.parseInt(date);
		if(pastOrFuture.equalsIgnoreCase("past")) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_YEAR, -dateCount);
			Date pastDate = calendar.getTime();
			returnDate = new SimpleDateFormat("MM/dd/yyyy HH:mm").format(pastDate); 
		}else if(pastOrFuture.equalsIgnoreCase("future")) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_YEAR, dateCount);
			Date futureDate = calendar.getTime();
			returnDate = new SimpleDateFormat("MM/dd/yyyy HH:mm").format(futureDate); 
		}
		return returnDate;
	}
	
	public String generateDate(String pastOrFuture, String dateCnt, String dateOrMonthOrYear,String dateformat) {
		String returnDate = "";
		int dateCount = Integer.parseInt(dateCnt);
		if(pastOrFuture.equalsIgnoreCase("past") && dateOrMonthOrYear.equalsIgnoreCase("day")) {
			Calendar calendar = Calendar.getInstance();	
			calendar.add(Calendar.DAY_OF_YEAR, -dateCount);
			calendar.add(Calendar.MINUTE, 630); //added 10hr:30min to align from RDC to Local time
			Date pastDate = calendar.getTime();
			returnDate = new SimpleDateFormat(dateformat).format(pastDate); 
		}else if(pastOrFuture.equalsIgnoreCase("future") && dateOrMonthOrYear.equalsIgnoreCase("day")) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_YEAR, dateCount);
			calendar.add(Calendar.MINUTE, 630); //added 10hr:30min to align from RDC to Local time
			Date futureDate = calendar.getTime();
			returnDate = new SimpleDateFormat(dateformat).format(futureDate); 
		}else if(pastOrFuture.equalsIgnoreCase("past") && dateOrMonthOrYear.equalsIgnoreCase("month")) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MONTH, -dateCount);
			calendar.add(Calendar.MINUTE, 630); //added 10hr:30min to align from RDC to Local time
			Date pastDate = calendar.getTime();
			returnDate = new SimpleDateFormat(dateformat).format(pastDate); 
		}else if(pastOrFuture.equalsIgnoreCase("future") && dateOrMonthOrYear.equalsIgnoreCase("month")) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MONTH, dateCount);
			calendar.add(Calendar.MINUTE, 630); //added 10hr:30min to align from RDC to Local time
			Date futureDate = calendar.getTime();
			returnDate = new SimpleDateFormat(dateformat).format(futureDate); 
		}else if(pastOrFuture.equalsIgnoreCase("past") && dateOrMonthOrYear.equalsIgnoreCase("year")) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.YEAR, -dateCount);
			calendar.add(Calendar.MINUTE, 630); //added 10hr:30min to align from RDC to Local time
			Date pastDate = calendar.getTime();
			returnDate = new SimpleDateFormat(dateformat).format(pastDate); 
		}else if(pastOrFuture.equalsIgnoreCase("future") && dateOrMonthOrYear.equalsIgnoreCase("year")) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.YEAR, dateCount);
			calendar.add(Calendar.MINUTE, 630); //added 10hr:30min to align from RDC to Local time
			Date futureDate = calendar.getTime();
			returnDate = new SimpleDateFormat(dateformat).format(futureDate); 
		}
        
		return returnDate;
	}

	public String generateCardNumber() {
		String alphabet =  new String("0123456789abcdefghijklmnopqrstuvwxyz"); //9
		int n = alphabet.length();
		String cardNumb = new String(); 
		Random r = new Random();
		for (int i=0; i<8; i++) {
			if(i==5) {
				cardNumb = cardNumb + "-";	
			}
			cardNumb = cardNumb + alphabet.charAt(r.nextInt(n));
		}
		cardNumb = cardNumb+"-"+generateTimeStamp();
		return cardNumb;

	}

	public String generateAlphaNumeric(String count) {
		String alphabet =  new String("ABCDEFGHIJKLMNOPQRSTUVWXYZ"); //9
		String numbers = new String("0123456789");
		int n = alphabet.length();
		int a = numbers.length();
		String alpha = new String(); 
		Random r = new Random();
		for (int i=0; i<Integer.parseInt(count); i++) {
			if(i==1||i==3) {
				alpha = alpha + numbers.charAt(r.nextInt(a));
			}else{
				alpha = alpha + alphabet.charAt(r.nextInt(n));
			}
		}
		return alpha;
	}

	public String generateType() {
		String alphabet =  new String("ABCDEFGHIJKLMNOPQRSTUVWXYZ"); //9
		String numbers = new String("0123456789");
		int n = alphabet.length();
		int a = numbers.length();
		String afftype = new String(); 
		Random r = new Random();
		for (int i=0; i<5; i++) {
			if(i==1||i==3) {
				afftype = afftype + numbers.charAt(r.nextInt(a));
			}
			afftype = afftype + alphabet.charAt(r.nextInt(n));
		}
		return afftype;

	}

	public String generateAlphabets(String count) {
		String alphabet =  new String("ABCDEFGHIJKLMNOPQRSTUVWXYZ"); //9
		int n = alphabet.length();
		String alpha = new String(); 
		Random r = new Random();
		for (int i=0; i<Integer.parseInt(count); i++) {
			alpha = alpha + alphabet.charAt(r.nextInt(n));
		}
		return alpha;
	}

	public String concatData(String Data, String methodName) {
		String returnData = "";
		String data = Data.toLowerCase();
		String[] StringList = new String[10];
		try {
			if(data.startsWith("concat(")&& data.contains(")")) {
				String sessionData = data.replace("concat(","");
				String actualData = sessionData.replaceAll("[)]$", "");
				String[] datalist = actualData.split("\\,");
				for(int i=0; i<datalist.length ;i++) {
					/*if(datalist[i].toCharArray().length==1 && (datalist[i].startsWith("'")&& datalist[i].endsWith("'"))) {
						StringList[i] = ",";
						i++;
					}else*/ 
					if(datalist[i].trim().startsWith("'") && datalist[i].trim().endsWith("'")) {
						String splittedData = datalist[i].trim().replace("'", "");
						StringList[i] = dataGenerator(splittedData,methodName);
					}else {
						StringList[i] = dataGenerator(datalist[i],methodName);
					}
				}
			}
			for(String concatData : StringList) {
				if(concatData!=null)
					returnData += concatData;
			}
			String dataToBeSaved = (methodName+"."+"concat").toLowerCase();
			dataProp.writeData(dataToBeSaved , returnData);
		}catch(Exception e) {
			logger.logFail("Failed to concat the data due to exception "+e.getMessage());
		}
		return returnData;
	}

	public String dataGenerator(String Data, String methodName) {
		String returnData = "";
		String keyName = "";
		try {
			String data = Data.toLowerCase();
			if(data.contains("random")||data.contains("current")) {
				if(data.contains("cardnumber")) {
					returnData = generateCardNumber();
					keyName = "cardnumber";
				}else if(data.contains("email")) {
					returnData = "auto_test_" +generateTimeStamp()+"@test.com";
					keyName = "email";
				}else if(data.contains("name")) {
					returnData = "Auto_Test_User"+generateTimeStamp();
					keyName = "name";	
				}else if (data.contains("file")) {
					returnData = generateDateTimeStamp()+".dat";
					keyName = "file";
				}else if(data.contains("number")) {
					if(data.contains("(")&& data.contains(")")){
						String[] sessionData = data.split("\\(");
						String sessionData1 = sessionData[1].split("\\)")[0];
						returnData = generateRandomNumber(sessionData1);
					}else {
						returnData = generateRandomNumber("10");
					}
					keyName = "number";
				}else if(data.contains("alphanumeric")) {
					if(data.contains("(")&& data.contains(")")){
						String[] sessionData = data.split("\\(");
						String sessionData1 = sessionData[1].split("\\)")[0];
						returnData = generateAlphaNumeric(sessionData1);
					}else {
						returnData = generateAlphaNumeric("5");
					}
					keyName = "alphanumeric";
				}else if(data.contains("type")) {
					returnData = "AFF" + generateAlphaNumeric("5");
					keyName = "afftype";
				}else if(data.contains("alphabets")) {
					if(data.contains("(")&& data.contains(")")){
						String[] sessionData = data.split("\\(");
						String sessionData1 = sessionData[1].split("\\)")[0];
						returnData = generateAlphabets(sessionData1);
					}else {
						returnData = generateAlphabets("5");
					}
					keyName = "alphabets";
				}
				else if(data.contains("currentdate")) {
					if(data.contains("+")) {
						String[] splitCommand = data.split("\\+");
						returnData = getDate("future",splitCommand[1]);
					}else if(data.contains("-")) {
						String[] splitCommand = data.split("\\-");
						returnData = getDate("past",splitCommand[1]);
					}
					else {
						returnData = getDate("future","0");
					}
					keyName = "date";
				}else if(data.contains("currenttimestampui")) {
					if(data.contains("+")) {			
						String[] splitCommand1 = data.split("\\+");				
						String[] splitCommand2 = splitCommand1[1].split(":");				
						returnData = generateDate("future",splitCommand2[0],splitCommand2[1],"MM/dd/yyyy HH:mm");
						
					}else if(data.contains("-")) {
						String[] splitCommand1 = data.split("-");
						String[] splitCommand2 = splitCommand1[1].split(":");
						returnData = generateDate("past",splitCommand2[0],splitCommand2[1],"MM/dd/yyyy HH:mm");
					}
					else {
						returnData = generateDate("future","0","day","MM/dd/yyyy HH:mm");
					}
					keyName = "date";
				}else if(data.contains("currenttimestampapi")) {
					if(data.contains("+")) {					
						String[] splitCommand1 = data.split("\\+");				
						String[] splitCommand2 = splitCommand1[1].split(":");				
						returnData = generateDate("future",splitCommand2[0],splitCommand2[1],"yyyy-MM-dd'T'HH:mm:ss.SSSSS'Z'");
						
					}else if(data.contains("-")) {			
						String[] splitCommand1 = data.split("-");
						String[] splitCommand2 = splitCommand1[1].split(":");
						returnData = generateDate("past",splitCommand2[0],splitCommand2[1],"yyyy-MM-dd'T'HH:mm:ss.SSSSS'Z'");						
					}
					else {
						returnData = generateDate("future","0","day","yyyy-MM-dd'T'HH:mm:ss.SSSSS'Z'");
					}
					keyName = "date";
				}
				else {
					System.out.println("Given random data is not the given type");
				}
			}else if(data.startsWith("db.")) {
				//String sQuery = data.split(".")[1];
				returnData = getProfile(data);
				keyName ="db";
			}
			else if(data.startsWith("get(")&& data.contains(")")) {
				String[] sessionData = data.split("\\(");
				String sessionData1 = sessionData[1].split("\\)")[0];
				returnData = read.readDataProperties(sessionData1).trim();
			}
			else {
				return Data;
			}
			int n = 1;
			String dataToBeSaved = (methodName+"."+keyName).toLowerCase();
			String duplicateData = read.readDataProperties(dataToBeSaved);
			if(duplicateData!=(null)) {
				while(duplicateData!=(null)) {
					dataToBeSaved = (methodName+"."+keyName+n).toLowerCase();
					duplicateData = read.readDataProperties(dataToBeSaved);
					n=n+1;
				}
			}
			dataProp.writeData(dataToBeSaved, returnData);
		}catch(Exception e) {
			logger.logFail("Failed to create data due to exception "+e.getMessage());
		}
		return returnData;
	}

	public String randomDataGenerator(String Data, String methodName) {
		String returnData ="";
		try {
			if(Data.contains("concat") && Data.contains("(") && Data.contains(")")) {
				returnData = concatData(Data, methodName);
			}else {
				returnData = dataGenerator(Data, methodName);
			}
		}catch(Exception e) {
			logger.logFail("Failed to create random data due to exception "+e.getMessage());
		}
		return returnData;
	}
	
	public void writeData(String keyName, String methodName, String valueToWrite) {
		int n = 1;
		String dataToBeSaved = (methodName+"."+keyName).toLowerCase();
		String duplicateData = read.readDataProperties(dataToBeSaved);
		if(duplicateData!=(null)) {
			while(duplicateData!=(null)) {
				dataToBeSaved = (methodName+"."+keyName+n).toLowerCase();
				duplicateData = read.readDataProperties(dataToBeSaved);
				n=n+1;
			}
		}
		dataProp.writeData(dataToBeSaved, valueToWrite);
	}
	
	public void writeApiData(String uniqueIdentifier,String keyName, String valueToWrite) {
		int n = 1;
		String dataToBeSaved = (uniqueIdentifier+"."+keyName).toLowerCase();
		String duplicateData = read.readDataProperties(dataToBeSaved);

		if(duplicateData!=(null) && !duplicateData.equals(valueToWrite)) {
			while(duplicateData!=(null)) {
				dataToBeSaved = (uniqueIdentifier+"."+keyName+n).toLowerCase();
				duplicateData = read.readDataProperties(dataToBeSaved);
				n=n+1;
			}
		}
		if(duplicateData==null || !duplicateData.equals(valueToWrite)) {
			dataProp.writeData(dataToBeSaved, valueToWrite);
		}
	}

	public String getProfile(String sQuery) {
		String activeProfileCardNumber = "";
		try {
			ResultSet rs = db.executeQuery(Excel.getQuery(sQuery));
			if (rs.next()) {
				activeProfileCardNumber = (rs.getString(1));
			}
		}catch(Exception e) {
			logger.logFail("Failed to fetch Active profile from DB due to exception "+e.getMessage());
		}
		return activeProfileCardNumber;
	}
	
	public String GetQueryResultWithoutExcel(String sQuery) {
		String activeProfileCardNumber = "";
		try {
			if (!sQuery.equalsIgnoreCase("Ignore")) {
				ResultSet rs = db.executeQuery(sQuery);
				if (rs.next()) {
					activeProfileCardNumber = (rs.getString(1));
				}
			}
			else {
				return sQuery;
			}
		}catch(Exception e) {
			logger.logFail("Failed to fetch data from DB due to exception "+e.getMessage());
		}
		return activeProfileCardNumber;
	}
}