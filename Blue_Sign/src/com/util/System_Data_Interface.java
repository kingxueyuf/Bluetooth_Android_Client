package com.util;

import android.util.Log;

public class System_Data_Interface {

	private Boolean Debug = false;
	private String result;

	public String getResponse(String methodName, String propertyName,
			String propertyValue) {
		if (Debug) {
			result = getDataLocal(methodName, propertyName, propertyValue);

		} else {
			result = getDataFromNet(methodName, propertyName, propertyValue);
		}
		return result;
	}

	public String getResponseForCheck(String methodName,
			String propertyNameOne, String propertyValueOne,
			String propertyNameTwo, String propertyValueTwo) {
		// writeXML writer = new writeXML();
		// writer.write_Parent_Start_Tag("user_info");
		// writer.write_SonTag("CourseTeacher", "����");
		// writer.write_SonTag("IdentiType", "��ʦ");
		// writer.write_SonTag("OperatorID", "001");
		// writer.write_SonTag("OperatorName", "fatMonkey");
		// writer.write_SonTag("OperatorIdenti", "T");
		// writer.write_Parent_End_Tag("user_info");
		// return writer.finish();

		call_webservice call = new call_webservice();
		call.initialzeWebservice(methodName);
		call.addProperty(propertyNameOne, propertyValueOne);
		call.addProperty(propertyNameTwo, propertyValueTwo);
		call.send_to_server();
		if(call.hasResponse())
		{
			Log.v("result", call.getResponse());
			return call.getResponse();
		}else
		{
			return null;
		}
		
		// return "1_����";
	}

	/*
	 * 2012/09/25 robin ��������
	 */
	public String getDataLocal(String method, String proName, String proValue) {
		if (method == "course") {
			System.out.println("course");
			writeXML write = new writeXML();
			write.write_Parent_Start_Tag("course_return");
			write.write_SonTag("flag", "1");
			write.write_SonTag("CourseName", "����ϵͳ");
			write.write_SonTag("CourseTeacher", "����");
			write.write_Parent_End_Tag("course_return");
			Log.v("content of xml", write.finish());
			return write.finish();
		} else if (method == "get_class_id") {
			return "10_1009"; // Ӧ������_ǩ����
		} else if (method == "insert_blue_mac") {
			return "success";
		} else if (method == "insert_end_blue_mac") {
			writeXML write = new writeXML();
			write.write_Parent_Start_Tag("absent_info");
			write.write_SonTag("StuNo", "10095098");
			write.write_SonTag("StuName", "Ѧ�");
			write.write_SonTag("StuNo", "10061414");
			write.write_SonTag("StuName", "������");
			write.write_SonTag("StuNo", "10061312");
			write.write_SonTag("StuName", "����");
			write.write_Parent_End_Tag("absent_info");
			return write.finish();
		} else if (method == "send_end_signal") {
			writeXML write = new writeXML();
			write.write_Parent_Start_Tag("absent_info");
			write.write_SonTag("CheckIn_ID", "10");
			write.write_SonTag("StuNo", "10095098");
			write.write_SonTag("StuName", "Ѧ�");
			write.write_SonTag("StuNo", "10061414");
			write.write_SonTag("StuName", "������");
			write.write_SonTag("StuNo", "10061312");
			write.write_SonTag("StuName", "����");
			write.write_Parent_End_Tag("absent_info");
			return write.finish();
		} else if (method == "single_insert") {
			return "insert_single_success!";
		}
		return null;
	}

	/*
	 * 2012/09/25 robin ���webservice����
	 */
	public String getDataFromNet(String methodName, String propertyName,
			String propertyValue) {
		call_webservice call = new call_webservice();
		call.initialzeWebservice(methodName);
		if (propertyName != null) {
			call.addProperty(propertyName, propertyValue);
		}
		call.send_to_server();
		return call.getResponse();
	}
}