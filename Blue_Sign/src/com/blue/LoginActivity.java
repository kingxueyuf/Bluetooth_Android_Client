package com.blue;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import com.util.System_Data_Interface;
import com.util.parseXML;
import com.util.writeXML;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/*
 * Date 2012/10/12
 * Author robin-xue
 * 
 */
public class LoginActivity extends Activity {

	EditText ID;
	EditText password;
	Button submit;
	ProgressDialog dialog;

	String Id;
	String Password;
	String OperatorName;
	myHandler handler = new myHandler();
	// sharedPreferences
	Context myContext;
	SharedPreferences sp;
	Editor editor;

	String[] user_info;

	BlueApp app;
	Thread thread;

	Boolean stopThread = false;

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		stopThread = true;
		finish();
		return super.onKeyDown(keyCode, event);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (BlueApp) LoginActivity.this.getApplication();// ��ȡapplication����
		// ��sharedPreferences������
		myContext = LoginActivity.this;
		sp = myContext.getSharedPreferences("LoginData", MODE_PRIVATE);
		editor = sp.edit();
		Id = sp.getString("ID", "none");
		Password = sp.getString("password", "none");

		/*
		 * ������绷��
		 */
		if (isConnect(this) == false) {
			new AlertDialog.Builder(this)
					.setTitle("�W�j�e�`")
					.setMessage("�W·�B��ʧ����Ո�_�J�W�j�B��")
					.setPositiveButton("ȷ��",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									finish();
								}
							}).show();
			setContentView(R.layout.login);// ��¼����
		} else {
			/*
			 * if(��һ�ε�¼�� {...} else() {...}
			 */
			if (Id.equals("none") || Password.equals("none")) {
				// ��һ�ε�¼
				setContentView(R.layout.login);// ��¼����
				ID = (EditText) findViewById(R.id.formlogin_userid);
				password = (EditText) findViewById(R.id.formlogin_pwd);
				submit = (Button) findViewById(R.id.formlogin_btsubmit);
				submit.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						Id = ID.getText().toString();// ��ȡ��¼����
						Password = password.getText().toString();
						// ������
						dialog = new ProgressDialog(LoginActivity.this);
						dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// ���ý�������񣬷��Ϊԭ�ͣ���ת��
						dialog.setMessage("������֤...");
						dialog.setIcon(android.R.drawable.ic_dialog_map);// ���ñ���ͼ��
						dialog.setCancelable(false);// �����ؼ�ȡ��
						dialog.show();// ��ʾ
						// ��֤��¼
						thread = new Thread() {
							public void run() {
								/*
								 * webservice��֤��¼
								 */
								System_Data_Interface data_Interface = new System_Data_Interface();
								String result = data_Interface
										.getResponseForCheck("check", "ID", Id,
												"password", Password);
								// <user_info>
								// <CourseTeacher></CourseTeacher>
								// <IdentiType></IdentiType>
								// <OperatorID></OperatorID>
								// <OperatorName></OperatorName>
								// <OperatorIdenti></OperatorIdenti>
								// </user_info>
								Message msg = new Message();
								if (result == null) 
								{
									msg.arg1 = 4;// ���糬ʱ
									Log.v("tag", "result is null");
								}else if (result.equals("0")) 
								{// ��¼��֤ʧ��
									msg.arg1 = 0;
								} else 
								{
									OperatorName = result.substring(2);// "1_OperatorName"
									app.OperatorName = OperatorName;
									msg.arg1 = 1;
								}
								handler.sendMessage(msg);
							}
						};
						thread.start();
					}

				});
			} else {
				// ֮ǰ��¼��
				Log.v("login", "login before");
				setContentView(R.layout.login_progress);
				dialog = new ProgressDialog(LoginActivity.this);
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// ���ý�������񣬷��Ϊԭ�ͣ���ת��
				dialog.setMessage("������֤...");
				dialog.setIcon(android.R.drawable.ic_dialog_map);// ���ñ���ͼ��
				dialog.setCancelable(false);// �����ؼ�ȡ��
				dialog.show();// ��ʾ
				thread = new Thread() {
					public void run() {
						/*
						 * webservice��֤��¼
						 */
						System_Data_Interface data_Interface = new System_Data_Interface();
						String result = data_Interface.getResponseForCheck(
								"check", "ID", Id, "password", Password);
						Message msg = new Message();
						if (result == null) {
							msg.arg1 = 4;// �����쳣
							Log.v("tag", "result is null");
						} else {
							/*
							 * ����result
							 */
							OperatorName = result.substring(2);
							app.OperatorName = OperatorName;
							msg.arg1 = 3;
						}
						handler.sendMessage(msg);
					}
				};
				thread.start();
			}
		}
	}

	class myHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			dialog.dismiss();
			if (msg.arg1 == 1) {
				/*
				 * ��¼�ɹ� ���û�������˻�������ʹ��sharedPreferencesд��xml��
				 */
				// ��������
				editor = sp.edit();
				editor.putString("ID", Id);
				editor.putString("password", Password);
				editor.commit();
				// ����
				Log.v("log", sp.getString("ID", "none"));
				Log.v("log", sp.getString("password", "none"));
				app.ID = Id;
				// ��ת
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, MetroActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_bottom,
						R.anim.out_to_top);
				finish();// important���������ٵ�ǰactivity������
			} else if (msg.arg1 == 0) {
				Toast.makeText(LoginActivity.this, "��¼ʧ�ܣ���������ȷ���˻�������",
						Toast.LENGTH_LONG).show();
			} else if (msg.arg1 == 3) {
				dialog.dismiss();
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, MetroActivity.class);
				app.ID = Id;
				startActivity(intent);
				finish();// important���������ٵ�ǰactivity������
			} else if (msg.arg1 == 4) {
				dialog.dismiss();
				Toast.makeText(LoginActivity.this, "���糬ʱ��������������",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		// the activiy is no longer visible

		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// Another Activity comes in front of the activity

		super.onResume();
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
