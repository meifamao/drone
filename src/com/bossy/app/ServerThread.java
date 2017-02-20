package com.bossy.app;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;

import com.bossy.util.DateFomat;
import com.bossy.util.MinaUtils;
import com.bossy.util.SendUdpData;
import com.bossy.util.SystemConfigInfo;

/**
 * 服务端监
 * 监听本地端口、接收消息、处理消息
 * 2017-02-17
 */
public class ServerThread extends IoHandlerAdapter implements Runnable{
	
	static Logger logger = Logger.getLogger(ServerThread.class);
	private String udpPort;
	//根据账号密码找到配对
	private ConcurrentHashMap<String,HashSet<IoSession>> p2pSessions;
	//根据会话找到账号密码
	private ConcurrentHashMap<IoSession,String> p2pNP;
	
	//构造方法
	public ServerThread(String actionType,String udpPort) {
		this.udpPort = udpPort;
	}
	
	//启动线程方法
	@Override
	public void run() {
		try {
			//启动UDP
			initAcceptUDP();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//启动UDP监听
	public void initAcceptUDP() throws Exception {
		NioDatagramAcceptor udpAcceptor = new NioDatagramAcceptor();
		DefaultIoFilterChainBuilder chain = udpAcceptor.getFilterChain();
		chain.addLast("executor", new ExecutorFilter(SystemConfigInfo.executor));
		chain.addLast("logger", new LoggingFilter());
		p2pSessions = new ConcurrentHashMap<String,HashSet<IoSession>>(16);
		p2pNP = new ConcurrentHashMap<IoSession,String>(16);
		DatagramSessionConfig dcfg = udpAcceptor.getSessionConfig();
		dcfg.setReadBufferSize(2048);
		dcfg.setReceiveBufferSize(2048);
		dcfg.setSendBufferSize(1024);
		dcfg.setReuseAddress(true);
		udpAcceptor.setHandler(this);
		udpAcceptor.bind(new InetSocketAddress(Integer.parseInt(udpPort)));
		System.out.println("UDP服务器监听端口启动成功!" + udpPort);
	}
	
	//数据接收转发
	public void actionPrimary(IoSession session, Object message) throws Exception {
		
		//将从通道中拿出来的数据转换成字节码
		byte[] receivedData = MinaUtils.getDataByIoBuffer((IoBuffer) message);
		String mgsStr = new String(receivedData,"UTF-8");
		String np = getNP(mgsStr);
		try{
			if(np!=null&&np!=""){
				p2pNP.put(session, np);
				if(p2pSessions.get(np)==null){
					    //账号密码还没有存任何会话
					p2pSessions.put(np, new HashSet<IoSession>());
					p2pSessions.get(np).add(session);
				}else{
					    //还没有完成配对则加入集合
					if(p2pSessions.get(np).size()==1){
						p2pSessions.get(np).add(session);
						IoSession peer = getPeer(session,np);
						SendUdpData.proxy_Message(receivedData,peer);
					}
					else if(p2pSessions.get(np).size()==2){
						//配对完成,转发到配对的机器
						IoSession peer = getPeer(session,np);
						SendUdpData.proxy_Message(receivedData,peer);
					}
					else {
						removeSession(session);
						throw new Exception("sessions exceed two!");
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			logger.info(" Time："+DateFomat.dataformat(new Date(),"yyyy-MM-dd HH:mm:ss")+"Recieved："+mgsStr);
		}
		
	}
	
	
	//Mina接收数据
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		actionPrimary(session, message);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		super.exceptionCaught(session, cause);
	}

	@Override
	public void inputClosed(IoSession session) throws Exception {
		super.inputClosed(session);
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		super.messageSent(session, message);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		removeSession(session);
		super.sessionClosed(session);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		super.sessionIdle(session, status);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		super.sessionOpened(session);
	}
	
	@SuppressWarnings("unchecked")
	private IoSession getPeer(IoSession session, String np) {
		// TODO Auto-generated method stub
		Set<IoSession> cp = (Set<IoSession>) p2pSessions.get(np).clone();
		cp.remove(session);
		return cp.iterator().next();
	}

	private String getNP(String mgsStr) {
         Pattern pattern = Pattern.compile("npSTART(.*?)npEND");  
		 Matcher matcher = pattern.matcher(mgsStr); 
		 boolean matched = matcher.find();
		 return matched?matcher.group(1):"abc&abc";
	}
	
	private void removeSession(IoSession session) throws Exception {
		//删除np与session的所有对应缓存关系
		String np = p2pNP.remove(session);
		p2pSessions.get(np).remove(session);
	}
}
