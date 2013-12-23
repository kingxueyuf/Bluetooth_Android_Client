package com.blue;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import com.util.System_Data_Interface;
import com.util.parseXML;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MetroActivity extends Activity {
	Button register;
	Button signin;
	Button fankui;
	Button map;
	Button set;
	Button about;
	Intent jump;
	BlueApp app;
	Builder dialog;
	Builder exit_dialog;
	EditText Input_CourseClassNo;
	String CourseClassNo;
	String course_return;
	String[] course_return_parse_array;
	myHandler handler = new myHandler();
	ProgressDialog progressdialog;
	parseXML parser;
	Thread thread;

	EditText one;
	EditText two;
	EditText three;
	EditText four;
	EditText five;
	EditText six;

	Context metro;

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		exit_dialog = new AlertDialog.Builder(MetroActivity.this);
		exit_dialog.setTitle("��_��Ҫ�˳�ϵ�y?");
		exit_dialog.setIcon(android.R.drawable.ic_dialog_info);
		exit_dialog.setPositiveButton("ȷ��", // ȷ����ť�����Ϳγ̺�
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
		exit_dialog.setNegativeButton("ȡ��", null);
		exit_dialog.show();
		return super.onKeyDown(keyCode, event);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.metro_main);
		app = (BlueApp) this.getApplication();

		register = (Button) findViewById(R.id.register);// ע�ᰴť
		signin = (Button) findViewById(R.id.signin);
		set = (Button) findViewById(R.id.set);
		about = (Button) findViewById(R.id.about);

		Toast.makeText(MetroActivity.this,
				"��ӭʹ��  ���������ѧ��������ϵͳ	 " + app.OperatorName + " ��ʦ",
				Toast.LENGTH_LONG).show();

		/*
		 * ע�ᰴť ���û�����γ̺�
		 */
		register.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// �γ̺�dialog�����ļ���ʼ
				LayoutInflater inflater = getLayoutInflater();
				View layout = inflater.inflate(R.layout.course_class_no_dialog,
						(ViewGroup) findViewById(R.id.dialog)); // dialog�����ļ�
				one = (EditText) layout.findViewById(R.id.one);
				two = (EditText) layout.findViewById(R.id.two);
				three = (EditText) layout.findViewById(R.id.three);
				four = (EditText) layout.findViewById(R.id.four);
				five = (EditText) layout.findViewById(R.id.five);
				six = (EditText) layout.findViewById(R.id.six);
				// dialog�����ļ�����
				dialog = new AlertDialog.Builder(MetroActivity.this);
				dialog.setView(layout);// ����dialog�Զ���layout
				dialog.setTitle("������γ̺�");
				dialog.setIcon(android.R.drawable.ic_dialog_info);
				dialog.setPositiveButton("ȷ��", // ȷ����ť�����Ϳγ̺�
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								// ������
								progressdialog = new ProgressDialog(
										MetroActivity.this);
								progressdialog
										.setProgressStyle(ProgressDialog.STYLE_SPINNER);// ���ý�������񣬷��Ϊԭ�ͣ���ת��
								progressdialog.setMessage("������֤...");
								progressdialog
										.setIcon(android.R.drawable.ic_dialog_map);// ���ñ���ͼ��
								progressdialog.setCancelable(false);// �����ؼ�ȡ��
								progressdialog.show();// ��������ʾ
								// ��ȡ�û����뿪ʼ (2012-2013-1)-00032204-3018907-1
								CourseClassNo = "(" + one.getText().toString()
										+ "-" + two.getText().toString() + "-"
										+ three.getText().toString() + ")-"
										+ four.getText().toString() + "-"
										+ five.getText().toString() + "-"
										+ six.getText().toString();
								Log.v("tag", CourseClassNo);// ����
								// �ж��Ƿ���sharedpreference�ﱣ���˸�����
								Context myContext = MetroActivity.this;
								SharedPreferences sp = myContext
										.getSharedPreferences("CourseData",
												MODE_PRIVATE);
								String coursename_teacher = sp.getString(
										CourseClassNo, "none");
								Log.v("tag", coursename_teacher);
								if (coursename_teacher.equals("none")) {
									/*
									 * ����CourseClassNo
									 */
									thread = new Thread() {
										public void run() {
											Message msg = new Message();
											if (isConnect(MetroActivity.this) == false) {
												msg.arg1 = 2;// �����쳣
											} else {
												System_Data_Interface data = new System_Data_Interface();
												course_return = data
														.getResponse("course",
																"course_no",
																CourseClassNo);// method,property,value
												Log.v("tag", course_return);// ����
												if (course_return == null) {
													msg.arg1 = 2;// �����쳣
												} else {
													parser = new parseXML();
														parser.Parse_Xml_course_return(course_return);
													if (parser
															.isCourse_return()) {// �û������Ϊ������CourseClassNo
														// course_return_parse_array
														// =
														// parser.getcourse_return();
														// app.CourseClassNo =
														// CourseClassNo;
														msg.arg1 = 1;
													} else {
														msg.arg1 = 0;
													}
												}
											}
											handler.sendMessage(msg);
										}
									};
									thread.start();
								} else {
									progressdialog.dismiss();
									// sharedpreference���Ѿ����иÿγ̺�
									AlertDialog.Builder builder = new Builder(
											MetroActivity.this);
									builder.setMessage("�γ��ѱ�ע�ᣬ�����Ե���������ǩ����ť����ǩ��");
									builder.setTitle("��ʾ");
									builder.setPositiveButton(
											"ȷ��",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													dialog.dismiss();
												}
											});
								}
							}
						});
				dialog.setNegativeButton("ȡ��", null);
				dialog.show();

			}
		});

		signin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// jump.setClass(MetroActivity.this, SignIn.class);
				// MetroActivity.this.startActivity(jump);
				jump = new Intent();
				jump.setClass(MetroActivity.this, RegisteredCourseList.class);
				startActivity(jump);

			}
		});

		set.setOnClickListener(new OnClickListener() {
			// �˳���¼
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				app.metro = MetroActivity.this;
				intent.setClass(MetroActivity.this, SetActivity.class);
				startActivity(intent);
			}
		});

		about.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MetroActivity.this, AboutActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}
		});
	}

	class myHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.arg1) {
			case 1:
				// ����courseNo�Ϸ�
				progressdialog.dismiss();
				Intent intent = new Intent();
				intent.putExtra("course_return", parser.getcourse_return());
				intent.putExtra("CourseClassNo", CourseClassNo);
				intent.setClass(MetroActivity.this, course_return_info.class);
				startActivity(intent);
				break;
			case 0:
				progressdialog.dismiss();
				Toast.makeText(MetroActivity.this, "������Ŀγ̺Ų�����",
						Toast.LENGTH_LONG).show();
				break;
			case 2:
				progressdialog.dismiss();
				Toast.makeText(MetroActivity.this, "�����쳣", Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	public boolean isConnect(Context context) {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// ��ȡ�������ӹ���Ķ���
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// �жϵ�ǰ��·�Ƿ��Ѿ�����
					if (info.getState() == NetworkInfo.State.CONNECTED)
						return true;
				}

			}
		} catch (Exception e) {
			Log.v("tag", e.toString());
		}
		return false;
	}
}
