package com.thundertouch.utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Logger {
	public static void write(String message) {
		GregorianCalendar cal = new GregorianCalendar();
		int year = cal.get(Calendar.YEAR);// �õ���ǰ���
		int month = cal.get(Calendar.MONTH) + 1;// �õ���ǰ�·�
		File file = new File("D:\\serverLog\\log_" + year + "_" + month
				+ ".txt");
		FileWriter fw = null;
		try {
			// ����ļ����ڣ���־׷�ӵ��ļ�ĩβ�����򴴽��µ��ļ�
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