package com.thundertouch.utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Logger {
	public static void write(String message) {
		GregorianCalendar cal = new GregorianCalendar();
		int year = cal.get(Calendar.YEAR);// 得到当前年份
		int month = cal.get(Calendar.MONTH) + 1;// 得到当前月份
		File file = new File("D:\\serverLog\\log_" + year + "_" + month
				+ ".txt");
		FileWriter fw = null;
		try {
			// 如果文件存在，日志追加到文件末尾，否则创建新的文件
			fw = new FileWriter(file, true);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String starttime = sdf.format(new Date());
			fw.write("[" + starttime + "]: " + message + "\r\n");
			System.out.println("[" + starttime + "]: " + message);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (fw != null) {
					fw.close();
				}
			} catch (IOException ex1) {
				ex1.printStackTrace();
			}
		}
	}
}