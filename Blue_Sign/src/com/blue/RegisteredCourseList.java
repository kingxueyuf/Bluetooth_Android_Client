package com.blue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class RegisteredCourseList extends Activity implements
		OnItemClickListener {
	ListView course_list;
	String[] namearray = new String[] { "coursename_teachername", "courseno" };
	int[] idarray = { R.id.coursename_teachername, R.id.courseno };
	Map allData;
	List<Map<String, Object>> list;
	Button exit;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registered_course_list);
		// ���ؼ�
		exit = (Button) findViewById(R.id.back_courseSelect);
		exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		Log.v("tag", "RegisteredCourseList here��");
		course_list = (ListView) findViewById(R.id.listView);
		SimpleAdapter adapter = new SimpleAdapter(this, getData(),
				R.layout.registered_course_list_item, namearray, idarray);
		course_list.setAdapter(adapter);
		course_list.setOnItemClickListener(RegisteredCourseList.this);
	}

	private List<Map<String, Object>> getData() {
		list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map;// listview��map

		Context myContext = RegisteredCourseList.this;
		SharedPreferences sp = myContext.getSharedPreferences("CourseData",
				MODE_PRIVATE);
		allData = sp.getAll();
		if (allData.size() != 0) {

			Iterator it = allData.entrySet().iterator();// allData
														// (key:�γ̺�----->value:�γ���_��ʦ��)

			while (it.hasNext()) {
				// ��itȡ���γ̺ţ��γ���_��ʦ��
				Map.Entry entry = (Map.Entry) it.next();
				String key = (String) entry.getKey();// �γ̺�
				String value = (String) entry.getValue();// �γ���_��ʦ��
				// ȡ��
				// ��listview��map��ֵ
				map = new HashMap<String, Object>();
				Log.v("tag", value);// ����
				Log.v("tag", key);// ����
				map.put("coursename_teachername", value);
				map.put("courseno", key);
				list.add(map);
			}
		}
		return list;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// arg2��ʾ��ѡ����
		Log.v("tag", "listview��ѡ��");
		Map map = list.get(arg2);
		String coursename_teachername = (String) map
				.get("coursename_teachername");
		String coursename = coursename_teachername.substring(0,
				coursename_teachername.indexOf("_"));
		String teachername = coursename_teachername
				.substring(coursename_teachername.indexOf("_") + 1);
		String courseno = (String) map.get("courseno");
		Intent intent = new Intent();
		intent.putExtra("coursename", coursename);
		intent.putExtra("teachername", teachername);
		intent.putExtra("courseno", courseno);
		intent.setClass(RegisteredCourseList.this, TeacherInfo.class);
		startActivity(intent);
	}

}
