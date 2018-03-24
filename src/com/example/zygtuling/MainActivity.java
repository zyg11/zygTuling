package com.example.zygtuling;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.string;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends ActionBarActivity implements HttpGetDataListener,OnClickListener{

	private HttpData httpData;
	private List<ListData> lists;
	private ListView lv;
	private EditText sendText;
	private Button send_btn;
	private String content_str;
	private TextAdapter adapter;
	private String[] welcome_array;
	private double currentTime,oldTime=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initview();

	}
	private void initview(){
		lv=(ListView) findViewById(R.id.lv);
		sendText=(EditText) findViewById(R.id.sendText);
		send_btn=(Button) findViewById(R.id.send_btn);
		lists=new ArrayList<ListData>();
		send_btn.setOnClickListener(this);
		adapter=new TextAdapter(lists, this);
		lv.setAdapter(adapter);
		ListData listData;
		listData=new ListData(GetRandWelcomeTips(),ListData.RECEIVER,getteime());
		lists.add(listData);
	}
	private String GetRandWelcomeTips(){
		String welcome_tip=null;
		welcome_array=this.getResources().getStringArray(R.array.welcome_tips);
		int index=(int) (Math.random()*(welcome_array.length-1));
		welcome_tip=welcome_array[index];
		return welcome_tip;
	}
	@Override
	public void getDataUrl(String data) {
//		System.out.println(data);
//		Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
		parseText(data);
		
	}
	public void parseText(String str){
		
		try {
			JSONObject jb=new JSONObject(str);
//			System.out.println(jb.getString("code"));
//			System.out.println(jb.getString("text"));
			ListData listData;
			listData=new ListData(jb.getString("text"),ListData.RECEIVER,getteime());
			lists.add(listData); 
			adapter.notifyDataSetChanged();//重新适配
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public void onClick(View arg0) {
		getteime();
		content_str=sendText.getText().toString();
		sendText.setText("");
		String dropk=content_str.replace(" ","");
		String droph=dropk.replace("\n","");
		ListData listData;
		listData=new ListData(content_str,ListData.SEND,getteime());
		lists.add(listData);
		if (lists.size()>30) {
			for (int i = 0; i <lists.size(); i++) {
				lists.remove(i);
			}
		}
		adapter.notifyDataSetChanged();
		httpData=(HttpData) new HttpData("http://www.tuling123.com/openapi/api?key=fb4ccddb379b45659c92bc0101bc4e67&info="
	+droph,this).execute();
		
	}
	public String getteime(){
		currentTime=System.currentTimeMillis();
		SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		Date curDate=new Date();
		String str=format.format(curDate);
		if ((currentTime-oldTime)>=5*60*1000) {
			oldTime=currentTime;
			return str;
		}else {
			return "";
		}
		
	}

}
