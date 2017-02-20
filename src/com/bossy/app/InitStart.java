package com.bossy.app;


import javax.servlet.http.HttpServlet;

import com.bossy.util.SystemConfigInfo;

/*
 * 客户端启动
 */
@SuppressWarnings("serial")
public class InitStart extends HttpServlet {

	public void init() {
		try {
			//初始化读取配置文件
			SystemConfigInfo.initSysConfigParams();
			
//			//接受转发数据 线程
			ServerThread primaryThread = new ServerThread("Primary",SystemConfigInfo.LOCAL_PORT_PRIMARY);

//			//启动接受转发数据线程
			primaryThread.run();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("初始化启动失败！");
		}
	}
	

}
