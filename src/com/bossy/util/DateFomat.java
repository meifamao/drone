package com.bossy.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFomat {
	
	public static String format(Date date,int...key){
		SimpleDateFormat sf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
		String time= sf.format(date);
		if(key!=null&&key.length>0){
			switch (key[0]) {
				case 1: time =  sf.format(date).substring(0, 10); break;
				case 2: time =  sf.format(date).substring(0,16);  break;
			}
		}
		return time;
	}
	/**
	 * 时间类型装换 Date转String
	 * @param date
	 * @param format
	 * @return
	 */
	public static String dataformat(Date date,String format){
		SimpleDateFormat sf = new SimpleDateFormat(format);
		String time= sf.format(date);
		return time;
	}
	
	
	public static Date stringFormatDate(String dateString,String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);  
		 Date date=null;
		try {
			date = sdf.parse(dateString);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}  
		return date;
	}
	/*public static void main(String[] args) {
	}*/
	
	public static Date StrToDate(String str) {
		   
	    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    Date date = null;
	    try {
	     date = format.parse(str);
	    } catch (ParseException e) {
	     e.printStackTrace();
	    }
	    return date;
	 }
	
	public static Date getCurrDate() {
	    SimpleDateFormat sf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
	    Date date = null;
	    try {
	    	String tempDate = sf.format(new Date());
	    	date = sf.parse(tempDate);
	    } catch (ParseException e) {
	    	e.printStackTrace();
	    }
	    return date;
	 }
	
	/**
	 * 判断时间是否在时间段内
	 * 
	 * @param date
	 *            当前时间 yyyy-MM-dd HH:mm:ss
	 * @param strDateBegin
	 *            开始时间 00:00:00
	 * @param strDateEnd
	 *            结束时间 00:05:00
	 * @return
	 */
	public static boolean isInDate(Date date, String strDateBegin, String strDateEnd) {
		//当前时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strDateYear = sdf.format(date);
		
		//开始时间
		//new Date(), "23:40:00", "23:59:59"
		String tempDateBegin = strDateYear + " "+strDateBegin;
		Date dateBegin = null;
		try {
			dateBegin = format.parse(tempDateBegin);
		} catch (ParseException e) {
			return false;
		}
		//结束时间
		String tempDateEnd = strDateYear + " "+strDateEnd;
		Date dateEnd = null;
		try {
			dateEnd = format.parse(tempDateEnd);
		} catch (ParseException e) {
			return false;
		}
		
		// 判断时间是否在时间段内  
	    if (date.getTime() >= dateBegin.getTime() && date.getTime() <= dateEnd.getTime()) {  
	        return true;  
	    }else{
	    	return false;  
	    }
	}
	
	public static void main(String[] args) {
		//boolean t = isInDate(new Date(), "13:00:00", "13:30:00");
		//System.out.println("T: "+t);
		
		//处理时间（如 01时02分03秒 会转换成 10203，前面0会丢掉）
		//处理时间（如 00时00分03秒 会转换成 3，前面0会丢掉）
		String time = "3";
		//while(time.length() < 6){
		while(time.length() < 6){
			time = "0"+time;
		}
		String date = "20161022";
		Date tempDt = DateFomat.stringFormatDate(date+time, "yyyyMMddHHmmss");
		String tmc = DateFomat.dataformat(tempDt, "yyyy-MM-dd HH:mm:ss");
		
		System.out.println("tmc :"+tmc);
	}
}
