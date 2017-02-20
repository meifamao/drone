package com.bossy.util;

import java.math.BigDecimal;

public class DoubleAndByte {
	/*
	 * 解析4个字节中的浮点数据
	 */
	public static double BytesToDouble(byte[] data) {
		long s = 0;// 浮点数的符号
		double f = 0;// 浮点数
		long e = 0;// 指数
		if ((data[3] & 0xff) >= 128) {// 求s
			s = -1;
		} else {
			s = 1;
		}
		long temp = 0;// 指数位的最后一位
		if ((data[2] & 0xff) >= 128) {
			temp = 1;
		} else
			temp = 0;
		e = ((data[3] & 0xff) % 128) * 2 + temp;// 求e
		// f=((data[2]&0xff)-temp*128+128)/128+(data[1]&0xff)/(128*256)+(data[0]&0xff)/(128*256*256);
		double[] data2 = new double[3];
		data2[0] = data[0] & 0xff;
		data2[1] = data[1] & 0xff;
		data2[2] = data[2] & 0xff;
		f = (data2[2] - temp * 128 + 128) / 128 + data2[1] / (128 * 256)
				+ data2[0] / (128 * 256 * 256);
		double result = 0;
		if (e == 0 && f != 0) {// 次正规数
			result = (double) (s * (f - 1) * Math.pow(2, -126));
			return result;
		}
		if (e == 0 && f == 0) {// 有符号的0
			result = (double) 0.0;
			return result;
		}
		if (s == 0 && e == 255 && f == 0) {// 正无穷大
			result = (double) 1111.11;
			return result;
		}
		if (s == 1 && e == 255 && f == 0) {// 负无穷大
			result = (double) -1111.11;
			return result;
		} else {
			result = (double) (s * f * Math.pow(2, e - 127));
			return result;
		}

	}
	
	/*
	 * 转换成4个字节中的表示的数据
	 */
	public static byte[] DoubleToBytes(double a) {
		byte[] data = new byte[4];
		if (a == 0) {
			for (int i = 0; i < 4; i++) {
				data[i] = 0x00;
			}
			return data;
		}
		Long[] intdata = {new Long(0), new Long(0), new Long(0), new Long(0)};
		a = Math.abs(a);
		// 首先将浮点数转化为二进制浮点数
		double doublepart = a % 1;
		long intpart = (long) (a / 1);
		// 将整数部分化为2进制,并转化为string类型
		String intString = "";
		String doubleString = "";
		String result = "";
		String subResult = "";
		int zhishu = 0;
		if (intpart == 0) {
			intString += "0";
		}
		while (intpart != 0) {
			intString = intpart % 2 + intString;
			intpart = intpart / 2;
		}
		while (doublepart != 0) {
			doublepart *= 2;
			if (doublepart >= 1) {
				doubleString += "1";
				doublepart -= 1;
			} else {
				doubleString += "0";
			}
		}

		result = intString + doubleString;
		intpart = (long) (a / 1);
		if (intpart > 0) {// 整数部分肯定有1，且以1开头..这样的话，小数点左移
			zhishu = intString.length() - 1;
		} else {// 整数位为0，右移
			for (int i = 0; i < doubleString.length(); i++) {
				zhishu--;
				if (doubleString.charAt(i) == '1') {
					break;
				}
			}
			// while(doubleString.charAt(index)){}
		}
		// 对指数进行移码操作

		if (zhishu >= 0) {
			subResult = result.substring(intString.length() - zhishu);
		} else {
			subResult = doubleString.substring(-zhishu);
		}
		zhishu += 127;
		if (subResult.length() <= 7) {// 若长度
			for (int i = 0; i < 7; i++) {
				if (i < subResult.length()) {
					intdata[1] = intdata[1] * 2 + subResult.charAt(i) - '0';
				} else {
					intdata[1] *= 2;
				}
			}

			if (zhishu % 2 == 1) {// 如果质数是奇数，则需要在这个最前面加上一个‘1’
				intdata[1] += 128;
			}
			data[1] = intdata[1].byteValue();
		} else if (subResult.length() <= 15) {// 长度在（7,15）以内
			int i = 0;
			for (i = 0; i < 7; i++) {// 计算0-7位，最后加上第一位
				intdata[1] = intdata[1] * 2 + subResult.charAt(i) - '0';
			}
			if (zhishu % 2 == 1) {// 如果质数是奇数，则需要在这个最前面加上一个‘1’
				intdata[1] += 128;
			}
			data[1] = intdata[1].byteValue();

			for (i = 7; i < 15; i++) {// 计算8-15位
				if (i < subResult.length()) {
					intdata[2] = intdata[2] * 2 + subResult.charAt(i) - '0';
				} else {
					intdata[2] *= 2;
				}
			}
			data[2] = intdata[2].byteValue();
		} else {// 长度大于15
			int i = 0;
			for (i = 0; i < 7; i++) {// 计算0-7位，最后加上第一位
				intdata[1] = intdata[1] * 2 + subResult.charAt(i) - '0';
			}
			if (zhishu % 2 == 1) {// 如果质数是奇数，则需要在这个最前面加上一个‘1’
				intdata[1] += 128;
			}
			data[1] = intdata[1].byteValue();

			for (i = 7; i < 15; i++) {// 计算8-15位
				intdata[2] = intdata[2] * 2 + subResult.charAt(i) - '0';
			}
			data[2] = intdata[2].byteValue();

			for (i = 15; i < 23; i++) {// 计算8-15位
				if (i < subResult.length()) {
					intdata[3] = intdata[3] * 2 + subResult.charAt(i) - '0';
				} else {
					intdata[3] *= 2;
				}

			}
			data[3] = intdata[3].byteValue();
		}

		intdata[0] = (long)zhishu / 2;
		if (a < 0) {
			intdata[0] += 128;
		}
		data[0] = intdata[0].byteValue();
		byte[] data2 = new byte[4];// 将数据转移，目的是倒换顺序
		for (int i = 0; i < 4; i++) {
			data2[i] = data[3 - i];
		}
		return data2;
	}
	
	//整数转换成4字节数组
	public static byte[] IntToByte4(int i) {
		byte[] targets = new byte[4];
		targets[3] = (byte) (i & 0xFF);
		targets[2] = (byte) ((i >> 8) & 0xFF);
		targets[1] = (byte) ((i >> 16) & 0xFF);
		targets[0] = (byte) ((i >> 24) & 0xFF);
		return targets;
	}

    //byte数组转换为int整数（从指定位置开始）
    public static int Byte4ToInt(byte[] bytes, int off) {  
        int b0 = bytes[off] & 0xFF;
        int b1 = bytes[off + 1] & 0xFF;  
        int b2 = bytes[off + 2] & 0xFF;  
        int b3 = bytes[off + 3] & 0xFF;  
        return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;  
    }  
    
    public static String ScientificNotationToString(double b) {
		/*科学计数法  ObjectId需要*/
		BigDecimal db = new BigDecimal(b);
		String ii = db.toPlainString();
		return ii;
    }
}