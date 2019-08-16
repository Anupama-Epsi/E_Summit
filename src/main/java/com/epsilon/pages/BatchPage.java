package com.epsilon.pages;

import java.io.*;
import java.sql.ResultSet;
import com.epsilon.DBUtils.*;
import com.epsilon.Utilities.DataGeneration;
import com.epsilon.Utilities.DataProperty;
import com.epsilon.Utilities.Excel;
import com.epsilon.Utilities.Reporting;
import com.epsilon.configuration.ReadPropertiesFile;
import com.google.common.io.Files;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import com.epsilon.configuration.Keywords;
import com.jcraft.jsch.*;

public class BatchPage {

	public Reporting logger = new Reporting();
	ReadPropertiesFile read = new ReadPropertiesFile();
	DBConfiguration db = new DBConfiguration();
	public CommonMethods common = new CommonMethods();
	public Keywords actions = new Keywords();
	public DataGeneration dataGenerate = new DataGeneration();
	private DataProperty dataProp = new DataProperty();

	public String getDataValue(String DataKey, String MethodName, String ValueHeader) {
		String outputValue = "";
		try {
			String[] Data = Excel.getData(DataKey);
			String[] Locators = Excel.getLocatorData(DataKey, MethodName);
			for (int i = 0; i < Data.length; i++) {
				if (Data[i] != null && !Data[i].equals("")) {
					if (Locators[i] != null && Locators[i].equalsIgnoreCase(ValueHeader)) {
						outputValue = Data[i];
						break;
					}
				}
			}
		} catch (Exception e) {
			logger.logFail("Failed to get DataValue from Excel due to exception " + e.getMessage());
		}
		return outputValue;
	}



	public List<String> getRowValueFromInputFile(String filePath) {
		List<String> inputRowValue = new ArrayList<String>();
		try {
			Scanner inputFile = new Scanner(new File(filePath)).useDelimiter(System.getProperty("line.separator"));
			while (inputFile.hasNext()) {
				String inputFileTemp = inputFile.next();
				inputRowValue.add(inputFileTemp);
			}
			inputFile.close();
		} catch (Exception e) {
			logger.logFail("Failed to get value from Input File due to exception " + e.getMessage());
		}
		return inputRowValue;
	}
	
	public String[] readValuefromInputFile(String filePath) {
		String[] InputStringValue= new String[100];
		try {
			Scanner scanIn = new Scanner(new BufferedReader(new FileReader(filePath)));
			int newcol = 0;
			int rowc = 0;
			while(scanIn.hasNextLine())
			{   
				boolean flag = false;
				String Inputline = scanIn.nextLine();
				String [] InArray = Inputline.split("\\,",-1);			
				if(InArray[0].startsWith("0")) {
					for (int col=0;col<InArray.length;col++){
						if(flag){
						InputStringValue[newcol] = InArray[col];
						//System.out.println("File contents skip Header : "+InputStringValue[newcol]);
						newcol ++;
						}
				        else {
							flag = true;
						}
					}	
					rowc++;
				}
				else {
					for (int col=0;col<InArray.length;col++) {
						InputStringValue[newcol] = InArray[col];
						//System.out.println("File content : "+InputStringValue[newcol]);
						newcol ++;
					}	
					rowc++;
				}
			 }
		  } catch (Exception e) {
			  System.out.println("Failed to read the iputfile due to execption:" +e.getMessage());
		  }
		return InputStringValue;
	}
	

	// Connection to WINSCP via SFTP

	public void sftpconnection(String DataKey, String MethodName) {
		JSch jSch = new JSch();
		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;
		try {
			/*
			 * Below we have declared and defined the SFTP HOST, PORT, USER and Local
			 * private key from where you will make connection
			 */
			logger.logPass("Running sftpconnection method", "No");
			String SFTPHOST = read.readRunProperties("SFTPHOST").trim();
			int SFTPPORT = Integer.parseInt(read.readRunProperties("SFTPPORT").trim());
			String SFTPUSER = read.readRunProperties("SFTPUSER").trim();
			

			System.out.println("SFTPHOST " + SFTPHOST + " " + " SFTPPORT " + SFTPPORT + " SFTPUSER " + SFTPUSER);

			String privateKey = System.getProperty("user.dir") + "\\Utils\\PPK_WinSCP\\D1VSEN_Batch_Server.ppk";
			System.out.println(privateKey);
			jSch.addIdentity(privateKey);
			logger.logPass("Private Key Added.", "No");
			System.out.println("Private Key Added.");
			session = jSch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
			logger.logPass("session created.", "No");

			Properties config = new Properties();
			session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			logger.logPass("channel connected....", "No");
			channelSftp = (ChannelSftp) channel;
			String inputfilename = getDataValue(DataKey, MethodName, "Batch_FileName");
			channelSftp.get("/Input/" + inputfilename,
					System.getProperty("user.dir") + "\\src\\test\\resources\\BatchFiles");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (channelSftp != null) {
				channelSftp.disconnect();
				channelSftp.exit();
			}
			if (channel != null)
				channel.disconnect();

			if (session != null)
				session.disconnect();
		}
	}
		
	public void createFileToSpecifiedPath(String DataKey,String MethodName) {
		logger.writeMethodName(actions.getMethodName());
		String FilePath = dataGenerate.randomDataGenerator
				(common.getDataValue(DataKey, actions.getMethodName(), "InputFile"), actions.getMethodName());
		System.out.println("FilePath: "+FilePath);
		File file = new File(FilePath);
		try {
			if(!file.exists()) {
				file.createNewFile();
				logger.logPass("File Created Sucessfully : "+FilePath, "No");
			}
			else { logger.logInfo("File already exists: "+FilePath);}
		} catch (IOException e) {
				e.printStackTrace();
				logger.logFail("Failed to create Batch Job due to exception : " + e.getMessage());
		}
			
	}
	
	public void sftpconnection_sample(String DataKey, String MethodName) {
		JSch jSch = new JSch();
		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;
		try {		
			logger.logPass("Running sftpconnection method", "No");
			String SFTPHOST = read.readRunProperties("SFTPHOST").trim();
			int SFTPPORT = Integer.parseInt(read.readRunProperties("SFTPPORT").trim());
			String SFTPUSER = read.readRunProperties("SFTPUSER").trim();
			String SFTPPSWD = read.readRunProperties("SFTPPSWD").trim();
			Properties config = new Properties();
			System.out.println("SFTPHOST " + SFTPHOST + " " + " SFTPPORT " + SFTPPORT + " SFTPUSER " + SFTPUSER+ " SFTPPSWD "+SFTPPSWD);

			session = jSch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(SFTPPSWD);
			session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
	        session.connect();
			logger.logPass("Session Connected.", "No");
			channel = session.openChannel("sftp");
			channel.connect();
			logger.logPass("channel connected....", "No");		
			channelSftp = (ChannelSftp) channel;
			String inputfilename = "ProductHierarchyImport9.txt";
			System.out.println("inputfilename "+inputfilename);
			System.out.println("Local Directory" +System.getProperty("user.dir")+ "\\src\\test\\resources\\BatchFiles");
			channelSftp.get("/Input/" + inputfilename,
					System.getProperty("user.dir") + "\\src\\test\\resources\\BatchFiles\\ProductHierarchyImport9.txt");
			logger.logPass("File transfered successfully to host.","No");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (channelSftp != null) {
				channelSftp.disconnect();
				channelSftp.exit();
			}
			if (channel != null)
				channel.disconnect();

			if (session != null)
				session.disconnect();
		}
	}

	
	
	public void VerifyProfileUpdateBatch(String DataKey, String Screenshot)
	{
		try {
			logger.writeMethodName(actions.getMethodName());
			actions.waitForPageToLoad(60);
			String InputfilePath = System.getProperty("user.dir")+getDataValue(DataKey, actions.getMethodName(), "LocalInputFile");
			String QueryToFetchTables = dataGenerate.randomDataGenerator(common.getDataValue(DataKey, actions.getMethodName(), "FiletoRaw_Query"),actions.getMethodName());
			System.out.println("QueryToFetchTables:"+QueryToFetchTables);
			String[] InputStringValue = readValuefromInputFile(InputfilePath);
			VerifyBatchFiletoDB(QueryToFetchTables,InputStringValue, Screenshot);
		 } catch (Exception e) {
			 System.out.println("VerifyProfileUpdateBatch is failed due to Exception:" +e.getMessage());
		  }
	}	
	
	public void VerifyBatchFiletoDB(String DBQuery, String[] InputValue, String Screenshot) {
		try {
			ResultSet rs = db.executeQuery(DBQuery);
			while (rs.next()) {
				for (int k = 0; k < rs.getMetaData().getColumnCount(); k++) {
					try {		
						  if((InputValue[k]==null ||InputValue[k].isEmpty() &&(rs.getString(k + 1)==null))) {									  						   
									logger.logPass( "File Value: " + InputValue[k] + "null is equal to DB result value :"+ rs.getString(k + 1), Screenshot);	
						  } else if (!InputValue[k].equals("") && (rs.getString(k + 1).trim().equalsIgnoreCase(InputValue[k]))) {
									logger.logPass( "File Value: " + InputValue[k] + " is equal to DB result value :"+ rs.getString(k + 1), Screenshot);
						  } else if (!rs.getString(k + 1).trim().equalsIgnoreCase(InputValue[k])) {
											logger.logFail("File Value: " + InputValue[k] + "  is not equal to DB result value :"+ rs.getString(k + 1));
					      }
					  } catch (Exception e) {
						   System.out.println("Failed to read DB data due to exception:" +e.getMessage());
					 }
				}
			} 
		} catch (Exception e) {
			System.out.println("Failed to verify Batch File to DB data due to exception :" +e.getMessage());
		}						  
    }
	
}

