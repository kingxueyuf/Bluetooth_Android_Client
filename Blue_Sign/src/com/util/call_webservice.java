package com.util;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

public class call_webservice {

	private static final String NAMESPACE = "urn:check/";// http://map.com/
	private static String URL = "http://210.42.151.54:8000/checkin/server_check";// http://192.168.57.1:8080/GetBaiduData8/GetLocalResultPoiPort?wsdl
	private static String METHOD_NAME;// getBusLine
	private String SOAP_ACTION;// http://map.com/getBusLine

	String response = null;
	SoapObject request;

	public call_webservice() {

	}

	// public void initialWebserviceForCheck(String method)
	// {
	// this.URL = "http://210.42.151.54:8000/checkin/server_check";
	// this.METHOD_NAME=method;
	// this.SOAP_ACTION="urn:check/"+method;
	//
	// }
	/*
	 * ���� ����webservice�� 1�������� 2��SOAP_ACTION�� 3��ʵ����SoapOjbect()
	 */
	public void initialzeWebservice(String methodname) {
		this.METHOD_NAME = methodname;
		this.SOAP_ACTION = "urn:check/" + methodname;
		request = new SoapObject(NAMESPACE, METHOD_NAME);
	}

	/*
	 * ��webserviece���ݲ��� 1��arg1 ������ 2��arg2 ����ֵ ���Ͳ���
	 */
	public void addProperty(String arg1, int arg2) {
		request.addProperty(arg1, arg2);
	}

	/*
	 * ��webserviece���ݲ��� 1��arg1 ������ 2��arg2 ����ֵ �ַ��Ͳ���
	 */
	public void addProperty(String arg1, String arg2) {
		request.addProperty(arg1, arg2);
		// request.addAttribute(arg1, arg2);
	}

	/*
	 * ��ʼ����webservice
	 */
	public void send_to_server() {
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER10);
		envelope.dotNet = false;
		envelope.bodyOut = request;
		
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			SoapPrimitive resultsRequestSOAP = (SoapPrimitive) envelope
					.getResponse();
			if (resultsRequestSOAP == null) {
				Log.v("tag", "54");
			}
			response = resultsRequestSOAP.toString();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Boolean hasResponse() {

		if (response != null) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * ����webservcie���
	 */
	public String getResponse() {
		return this.response;
	}
}
