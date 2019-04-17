package com.stylefeng.guns.rest.modular.alipay.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix="ftp")
public class FTPUtil {
	
//	private String hostName=Const.HOSTNAME;
//	
//	private Integer port=Const.PORT;
//	
//	private String userName=Const.USERNAME;
//	
//	private String password=Const.PASSWORD;
	
	private String hostName;
	
	private Integer port;
	
	private String userName;
	
	private String password;
	
	private String remotePath;

	private FTPClient ftpClient;
	
//	启动ftp服务器
	public void initFTPServer() {
		try {
			ftpClient = new FTPClient();
			ftpClient.connect(hostName, port);
			ftpClient.login(userName, password);
		} catch (Exception e) {
			log.error(e.getMessage() + "FTP服务器初始化失败");
		}
	}
	
//	根据文件名读取出文件内容，组装成字符串
	public String readFileByFileName(String fileName) {
		this.initFTPServer();
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(
					new InputStreamReader(ftpClient.retrieveFileStream(fileName)));
			ftpClient.setControlEncoding("utf-8");
			
			StringBuffer stringBuffer = new StringBuffer();
			while(true) {
				String string = bufferedReader.readLine();
				if(string != null) {
					stringBuffer.append(string);
				}
				else {
					break;
				}
			}
			
			ftpClient.logout();
			return stringBuffer.toString();		
			
		} catch (Exception e) {
			try {
				bufferedReader.close();
			} catch (IOException e1) {
				log.error(e1.getMessage()+"流关闭失败");
			}
			log.error(e.getMessage()+"文件读取失败");
		}
		
		return null;
	}
	
	
	
	public boolean uploadFile(List<File> fileList) throws IOException {
//		初始化ftp服务器
		this.initFTPServer();
		log.info("开始上传文件");
		
		boolean uploaded = true;
		FileInputStream fis = null;
				
		try {
			//ftpClient的一些设置
			ftpClient.changeWorkingDirectory(remotePath);
			ftpClient.setBufferSize(1024);
			ftpClient.setControlEncoding("UTF-8");
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.enterLocalPassiveMode();
			
			for(File fileItem : fileList) {
				fis = new FileInputStream(fileItem);
				ftpClient.storeFile(fileItem.getName(), fis);
			}
		} catch (IOException e) {
			log.error("文件上传出错", e);
			uploaded = false;
		} finally {
			fis.close();
			ftpClient.disconnect();
		}
		
		log.info("上传状态为：{}", uploaded);

		return uploaded;
	}
	
	
	public static void main(String[] args) {
		FTPUtil ftpUtil = new FTPUtil();
		String name = "seats/123214.json";
		int index = name.indexOf("/");
		name = name.substring(index + 1);
		System.out.println(name);
		
		String str = ftpUtil.readFileByFileName(name);
		System.out.println(str);
	}

}
