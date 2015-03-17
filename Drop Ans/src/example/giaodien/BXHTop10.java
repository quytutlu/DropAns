package example.giaodien;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.Object.DiemCao;

public class BXHTop10 extends Activity {
	String id;
	TextView display;
	String url;
	String User;
	ProgressDialog dialog;
	ListView lv;
	String GoiChuDe;

	private static String TAG_USERNAME = "UserName";
	private static String TAG_SCORE = "Score";
	private static String TAG_ENDEDTIME = "EndedTime";
	ArrayList<DiemCao> arrayList;
	BXHMyArrayAdapter adapter;
	private boolean flag;
	private String BatNhac;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.top10);
		Intent intent = getIntent();
		id = intent.getStringExtra("SUBJECTID");
		
		User = intent.getStringExtra("UserName");
		GoiChuDe=intent.getStringExtra("GoiChuDe");
		BatNhac=intent.getStringExtra("BatNhac");
		init();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		MainActivity.player.pause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(BatNhac.equals("true")){
			MainActivity.player.start();
		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(BXHTop10.this, BXH.class);
		intent.putExtra("SUBJECTID", id);
		intent.putExtra("UserName", User);
		intent.putExtra("GoiChuDe", GoiChuDe);
		intent.putExtra("BatNhac", BatNhac);
		startActivity(intent);
	}

	private void init() {
		Typeface typeface=Typeface.createFromAsset(getAssets(), "font/XepHang.ttf");
		display = (TextView) findViewById(R.id.tvtop10);
		display.setTypeface(typeface);
		display.setText("Xếp hạng");
		dialog = new ProgressDialog(this);
		lv = (ListView) findViewById(R.id.lv);
		arrayList = new ArrayList<DiemCao>();
		adapter = new BXHMyArrayAdapter(this, R.layout.mylistviewbxh, arrayList);
		lv.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		url = "http://svtl.net/service/svtl.ashx?cmd=gethightscore&subjectid=";
		url += id;
		flag=true;
		
		if(BatNhac.equals("true")){
			MainActivity.player.start();
		}else{
			MainActivity.player.pause();
		}
		new ParseJSONTask().execute();
	}

	public void onClick(View v){
		switch (v.getId()) {
		case R.id.top10:
			arrayList = new ArrayList<DiemCao>();
			adapter = new BXHMyArrayAdapter(this, R.layout.mylistviewbxh, arrayList);
			lv.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			url = "http://svtl.net/service/svtl.ashx?cmd=gethightscore&subjectid=";
			url += id;
			flag=true;
			new ParseJSONTask().execute();
			break;
		case R.id.lichsu:
			arrayList = new ArrayList<DiemCao>();
			adapter = new BXHMyArrayAdapter(this, R.layout.mylistviewbxh, arrayList);
			lv.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			url = "http://svtl.net/service/svtl.ashx?cmd=getmyhightscore&username=";
			url += User;
			url += "&subjectid=";
			url += id;
			flag=false;
			new ParseJSONTask().execute();
			break;
		default:
			break;
		}
	}
	
	private void updateUI() {
	}

	private class ParseJSONTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Loading...");
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			WebServiceHandler webServiceHandler = new WebServiceHandler();
			String jsonstr = webServiceHandler.getJSONData(url);
			if(jsonstr==null){
				return false;
			}
			try {
				if(flag){
					JSONArray jsonArray = new JSONArray(jsonstr);
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						DiemCao temp = new DiemCao();
						temp.setThoiDiem((i+1)+". "+jsonObject.getString(TAG_USERNAME));
						temp.setDiem(jsonObject.getInt(TAG_SCORE));
						arrayList.add(temp);
					}
				}else{
					JSONArray jsonArray = new JSONArray(jsonstr);
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						DiemCao temp = new DiemCao();
						temp.setThoiDiem((i+1)+". "+jsonObject.getString(TAG_ENDEDTIME));
						temp.setDiem(jsonObject.getInt(TAG_SCORE));
						arrayList.add(temp);
					}
				}
				return true;
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						"Không kết nối được server", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			adapter.notifyDataSetChanged();
			if(result==false){
				AlertDialog.Builder builder=new AlertDialog.Builder(BXHTop10.this);
				builder.setTitle("Lỗi!");
				builder.setMessage("Kiểm tra kết nối mạng");
				builder.show();
				return;
			}
			updateUI();
		}
	}

}