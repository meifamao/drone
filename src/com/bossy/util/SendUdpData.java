package com.bossy.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

public class SendUdpData {
	
	static Logger logger = Logger.getLogger(SendUdpData.class);
	

	public static void proxy_Message(byte[] receivedData, IoSession peer) {
		DatagramSocket ds = null;
		DatagramPacket dp = null;
		SocketAddress remoteAddress = peer.getRemoteAddress();
		try {
			ds = new DatagramSocket();
			dp = new DatagramPacket(receivedData, 0, receivedData.length,remoteAddress);
			ds.send(dp);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
}
