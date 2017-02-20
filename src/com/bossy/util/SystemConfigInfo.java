package com.bossy.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

public class SystemConfigInfo {
	
	public static ExecutorService executor = Executors.newCachedThreadPool();
	
	//默认DeBug调试UDP端口
	//用于调试程序时，根据UDP信息返回客户端相关的调试提示信息、数据
	public static String DEBUG_PORT = null;
	
	//监听接收数据端口
	public static String LOCAL_PORT_PRIMARY = null;
	
	//监听客户端连接端口
	public static String LOCAL_PORT_SECOND = null;
	
	//客户端连接用户列表
	public static List<IoSession> CLIENT_SESSION_LIST = null;
	
	//Debug Session用户列表
	public static List<IoSession> DEBUG_SESSION_LIST = null;
	
	//转发IP
	public static String [] FORWAED_IP = null;
	//转发端口
	public static String [] FORWAED_PORT = null;
	//转发过滤头标识
	public static String [] FORWAED_HEAD = null;
	
	//远程连接IP
	public static String REMOTE_IP = null;
	//远程连接端口
	public static String REMOTE_PORT = null;
	
	public static void initSysConfigParams() {
		InputStream stream = SystemConfigInfo.class.getResourceAsStream("/SystemConfig.properties");
		Properties properties = null;
		try {
			properties = new Properties();
			properties.load(stream);
			
			
			//远程连接IP
			SystemConfigInfo.LOCAL_PORT_PRIMARY = properties.getProperty("LOCAL_PORT_PRIMARY");
			
			//客户端连接用户列表
			SystemConfigInfo.CLIENT_SESSION_LIST = new ArrayList<IoSession>();
			//Debug Session用户列表
			SystemConfigInfo.DEBUG_SESSION_LIST = new ArrayList<IoSession>();
			
			System.out.println("读取配置文件SystemConfig.properties成功！");
			
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("读取配置文件SystemConfig.properties错误！[com.bossy.util.SystemConfigInfo.java]");
		}finally{
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	


	
	//发送消息(字节码)到Client连接客户端
	public static void sendMessageToClientSessionListByByte(byte[] message) {
		try {
			for (int i = 0; i < CLIENT_SESSION_LIST.size(); i++) {
				IoSession clientSession = CLIENT_SESSION_LIST.get(i);
				if (clientSession != null) {
					IoBuffer buffer = IoBuffer.allocate(message.length);
					buffer.put(message);
					buffer.flip();
					clientSession.write(buffer);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//发送消息（字符串）到Client连接客户端
	public static void sendMessageClientSessionListByStr(String message) {
		try {
			for (int i = 0; i < CLIENT_SESSION_LIST.size(); i++) {
				IoSession clientSession = CLIENT_SESSION_LIST.get(i);
				if (clientSession != null) {
					byte[] sendByte = message.getBytes("UTF-8");
					IoBuffer buffer = IoBuffer.allocate(sendByte.length);
					buffer.put(sendByte);
					buffer.flip();
					clientSession.write(buffer);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
