package com.bossy.util;

import org.apache.mina.core.buffer.IoBuffer;

@SuppressWarnings("static-access")
public class MinaUtils {

	public static byte[] getDataByIoBuffer(IoBuffer buffer) {
		buffer.allocate(2048).setAutoExpand(true);
        byte[] data = new byte[buffer.limit()];
        buffer.get(data);//将buffer传给data
        buffer.sweep();//清除缓存区
        return data;
	}
	
	public static IoBuffer getIoBufferByData(byte[] data) {
		IoBuffer buff = IoBuffer.allocate(data.length);
		buff.put(data);
		buff.flip();
		return buff;
	}
	
}
