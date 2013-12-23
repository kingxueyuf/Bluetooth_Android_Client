package com.blue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.bean.student;
import com.util.System_Data_Interface;
import com.util.call_webservice;
import com.util.parseXML;
import com.util.writeXML;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

public class SignIn extends Activity {
	/** Called when the activity is first created. */
	BlueApp app;
	String result; // EditText��ʾ����
	EditText text = null;
	String classNum_checkId; // webservice��õ� �༶����_ǩ����
	String ID; // ǩ����
	int stuNum = 0; // �༶����
	String mac = null; // �豸��mac
	String name = null; // �豸��name
	Long startTime; // ��ʼ��������������ʱ��
	String[] paraName = { "one", "two", "three", "four", "five", "six",
			"seven", "eight", "nine" };
	call_webservice call; // util����

	Set<String> all_mac = null; // ���е�mac����
	Set<String> nine_mac = null; // ÿ9��mac
	final int capacity = 1; // nine_mac������
	String[] nine_mac_clone = null; // nine_mac�ĸ���

	final String method = "�˴���дwebservice�ĵ��÷���";
	myHandler myHandler = new myHandler();;
	ArrayList<student> absent_students;
	String[] absent_stu_name;
	String[] absent_stu_no;
	ProgressDialog dialog; // ���յ�absent student info ���ڽ���

	BluetoothAdapter mBluetoothAdapter;

	BroadcastReceiver mReceiver;

	Boolean SearhIsNotAlive = false;
	IntentFilter filter;

	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// // TODO Auto-generated method stub
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// SearhIsNotAlive = true;//�ر������߳�
	// finish();
	// mBluetoothAdapter.cancelDiscovery();
	// this.unregisterReceiver(mReceiver);
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_in);
		app = (BlueApp) this.getApplication();
		this.classNum_checkId = app.classNum_checkId;
		Toast.makeText(SignIn.this, classNum_checkId, Toast.LENGTH_LONG).show();
		/*
		 * �ָ��ַ��� classNum_checkId 50_1003 int_String
		 */
		stuNum = Integer.valueOf(classNum_checkId.split("_")[0]);
		ID = classNum_checkId.split("_")[1];

		Log.v("ID", ID);

		Button search = (Button) findViewById(R.id.searchBlueTooth);
		text = (EditText) findViewById(R.id.text);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mBluetoothAdapter == null) {
			// �豸û������
			Toast.makeText(SignIn.this, "�����豸û������", Toast.LENGTH_LONG).show();

		} else if (!mBluetoothAdapter.isEnabled()) {
			// ������ û�п�����
			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			this.startActivity(intent);// ������
			// mBluetoothAdapter.getB
		}
		// ��ʼ��mac���� װString��mac
		all_mac = new HashSet<String>();
		nine_mac = new HashSet<String>();

		search.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mBluetoothAdapter == null) {
					// �豸û������
					Toast.makeText(SignIn.this, "�����豸û������", Toast.LENGTH_LONG)
							.show();
				} else {
					if (!mBluetoothAdapter.isEnabled()) {
						// ������ û�п�����
						Intent intent = new Intent(
								BluetoothAdapter.ACTION_REQUEST_ENABLE);
						startActivity(intent);// ������
						// mBluetoothAdapter.getB
					} else if (isConnect(SignIn.this) == false) {
						Toast.makeText(SignIn.this, "����������ݿ���",
								Toast.LENGTH_LONG).show();
					} else {
						dialog = new ProgressDialog(SignIn.this);
						dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// ���ý�������񣬷��Ϊԭ�ͣ���ת��
						dialog.setMessage("�������������ն�...");
						dialog.setIcon(android.R.drawable.ic_dialog_map);// ���ñ���ͼ��
						dialog.setCancelable(false);// �����ؼ�ȡ��
						dialog.show();// ��ʾ

						startTime = System.currentTimeMillis();
						Log.v("tag", "1");
						new Thread() {
							public void run() {
								while (true) {
									/*
									 * ���ٳ�û�����豸 ��map��ʣ����豸��Ϣ���� 120��=2����
									 */
									if (SearhIsNotAlive) {
										break;
									}
									if (System.currentTimeMillis() - startTime >= 25000) {
										// if (nine_mac.size() != 0) {
										try {
											Thread.sleep(3000);// ��3���ٻ�ȡ����
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										SignIn.this
												.unregisterReceiver(mReceiver);// very
																				// important!!!!
										send_final(nine_mac);// ʱ�䵽���������ļ���
										// }
										// else{
										// send_final();
										// }
										Log.v("tag", "4");
										break;// ������ѭ��
									}
									// ��������
									mBluetoothAdapter.startDiscovery();
									Log.v("tag", "2");
									try {
										Thread.sleep(1000);// 2��һ��
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
						}.start();
					}
				}
			}
		});

		mReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				Log.v("tag", "findBluetoothDeviece");
				// �������豸
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					// ��ø��豸�Ķ�����
					BluetoothDevice device = intent
							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					/*
					 * ��ȡ�ѻ񵽵��ֻ�mac��name
					 */
					mac = device.getAddress();
					name = device.getName();
					Log.v("tag", mac + name);

					if (!all_mac.contains(mac)) // ����û������������豸
					{
						/*
						 * 1.��all_mac��nine_mac���� 2.�ж�nine_mac��size == 9�� 2.1
						 * nine_mac.size()==9 1)��nine_mac�������дxml 2)��webservice
						 * 2.2 nine_mac.size()<i 1)������ѭ��
						 */
						all_mac.add(mac);
						nine_mac.add(mac);
						text.append(name + "��mac��" + mac + "\n");
						if (nine_mac.size() == capacity) {
							nine_mac_clone = new String[capacity];
							clone_mac(nine_mac, nine_mac_clone);
							new Thread() {
								public void run() {
									send_nine_mac(nine_mac_clone);// дxml+��webservice
								}
							}.start();
							nine_mac.clear();
						}
					}
				} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
						.equals(action)) {
					Log.v("tag",
							"blue discover finished!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				}

			}
		};
		// ע�� BroadcastReceiver
		filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter);
		Log.v("tag", "bluetooth register success");// Don't forget to unregister
													// during onDestroy
	}

	public void clone_mac(Set body, String[] copy) {
		Iterator<String> it = body.iterator();
		int i = 0;
		while (it.hasNext()) {
			copy[i] = it.next();
			i++;
		}
	}

	public void send_nine_mac(String[] nine_mac_clone) {
		/*
		 * дxml ���װ��xml��string
		 */
		writeXML xml = new writeXML();
		xml.write_Parent_Start_Tag("bluetooth_s_xml");
		xml.write_SonTag("CheckIn_ID", ID);
		xml.write_SonTag("Mac_Num", String.valueOf(capacity));
		for (int i = 0; i < capacity; i++) {
			xml.write_SonTag("BtMac" + (i + 1), nine_mac_clone[i]);
		}
		xml.write_Parent_End_Tag("bluetooth_s_xml");
		String mac_xml = xml.finish();
		Log.v("tag", mac_xml);
		/*
		 * call_webservice
		 */
		System_Data_Interface data_interface = new System_Data_Interface();
		data_interface.getResponse("insert_blue_mac", "bluetooth_xml", mac_xml);
	}

	public void send_final(Set<String> mac_capacity) {
		/*
		 * ����Ͳ���9����mac
		 */
		// ��дxml
		writeXML xml = new writeXML();
		xml.write_Parent_Start_Tag("bluetooth_end_xml");
		xml.write_SonTag("CheckIn_ID", ID);
		xml.write_SonTag("end", "1");
		xml.write_SonTag("need_record_time",
				String.valueOf(mac_capacity.size()));
		Iterator<String> it = mac_capacity.iterator();
		int i = 1;
		while (it.hasNext()) {
			xml.write_SonTag("BtMac" + i, it.next());
			i++;
		}
		xml.write_Parent_End_Tag("bluetooth_end_xml");
		/*
		 * final_macװ��xml
		 */
		String final_mac = xml.finish();
		Log.v("tag", final_mac);
		// /*
		// * ��webservice
		// */
		System_Data_Interface data_interface = new System_Data_Interface();
		String absent_info = data_interface.getResponse("insert_end_blue_mac",
				"bluetooth_end_xml", final_mac);
		Message msg = new Message();
		if (absent_info == null) {
			msg.arg1 = 2;// �����쳣
		} else {
			/*
			 * ������õ�absent_info
			 */
			parseXML parse = new parseXML();
			absent_students = parse.Parse_Xml(absent_info);
			if (absent_students == null) {
				msg.arg1 = 3;//�����쳣
			} else {
				msg.arg1 = 1;//�����ɹ�
			}
		}
		myHandler.sendMessage(msg);
	}

	// public void send_final()
	// {
	// System_Data_Interface data_interface = new System_Data_Interface();
	// String absent_info =
	// data_interface.getResponse("send_end_signal","final_signal","final_signal");
	// Log.v("tag", absent_info);
	// parseXML parse = new parseXML();
	// absent_students = parse.Parse_Xml(absent_info);
	// Message msg = new Message();
	// msg.arg1 = 1;
	// myHandler.sendMessage(msg);
	// }

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

	class myHandler extends Handler {
		public void handleMessage(Message msg) {
			Log.d("MyHandler", "handleMessage......");
			// �˴����Ը���UI
			if (msg.arg1 == 1) {
				//
				// dialog = new ProgressDialog(SignIn.this);
				// dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//
				// ���ý�������񣬷��Ϊԭ�ͣ���ת��
				// dialog.setMessage("���δǩ��ѧ������,���ڽ���...");
				// dialog.setIcon(android.R.drawable.ic_dialog_map);// ���ñ���ͼ��
				// dialog.setCancelable(true);// �����ؼ�ȡ��
				// dialog.show();// ��ʾ
				dialog.dismiss();
				mBluetoothAdapter.cancelDiscovery();
				SearhIsNotAlive = true;
				Intent intent = new Intent(SignIn.this, AbsentStudentInfo.class);
				int absent_num = absent_students.size();
				// test
				Log.v("tag", "size" + absent_students.size() + " ");
				// test
				absent_stu_no = new String[absent_num];
				absent_stu_name = new String[absent_num];
				for (int i = 0; i < absent_num; i++) {
					absent_stu_no[i] = absent_students.get(i).getStuNo();
					absent_stu_name[i] = absent_students.get(i).getStuName();
				}
				intent.putExtra("absent_stu_name", absent_stu_name);
				intent.putExtra("absent_stu_no", absent_stu_no);
				intent.putExtra("absent_num", absent_num);
				intent.putExtra("ID", ID);
				SignIn.this.startActivity(intent);
				finish();
			} else if (msg.arg1 == 1) {
				dialog.dismiss();
				Toast.makeText(SignIn.this, "�����쳣", Toast.LENGTH_LONG).show();
			}else
				if(msg.arg1 ==3){
					dialog.dismiss();
					Toast.makeText(SignIn.this, "�����쳣", Toast.LENGTH_LONG).show();
				}
		}
	}
}