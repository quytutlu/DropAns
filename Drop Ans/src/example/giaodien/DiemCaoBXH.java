package example.giaodien;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.Object.DiemCao;

public class DiemCaoBXH extends Activity {
	String id;
	TextView display;
	String url;
	String User;
	ProgressDialog dialog;
	ListView lv;
	String GoiChuDe;
	
	private static String TAG_SCORE = "Score";
	private static String TAG_ENDEDTIME = "EndedTime";
	ArrayList<DiemCao> arrayList;
	BXHMyArrayAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.diemcao);
		Intent intent = getIntent();
		id = intent.getStringExtra("SUBJECTID");
		User = intent.getStringExtra("UserName");
		GoiChuDe=intent.getStringExtra("GoiChuDe");
		init();
	}
	
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(DiemCaoBXH.this, BXH.class);
		intent.putExtra("SUBJECTID", id);
		intent.putExtra("UserName", User);
		intent.putExtra("GoiChuDe", GoiChuDe);
		startActivity(intent);
	}

	private void init() {
		display = (TextView) findViewById(R.id.tvdiemcao);
		display.setText("Điểm cao của bạn là");
		dialog = new ProgressDialog(this);
		
		lv = (ListView) findViewById(R.id.lv);
		
		arrayList = new ArrayList<DiemCao>();
		adapter = new BXHMyArrayAdapter(this, R.layout.mylistviewbxh, arrayList);
		lv.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		url = "http://svtl.net/service/svtl.ashx?cmd=getmyhightscore&username=";
		url += User;
		url += "&subjectid=";
		url += id;
		System.out.println(url);
		new ParseJSONTask().execute();
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

				JSONArray jsonArray = new JSONArray(jsonstr);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					DiemCao temp = new DiemCao();
					temp.setThoiDiem(jsonObject.getString(TAG_ENDEDTIME));
					temp.setDiem(jsonObject.getInt(TAG_SCORE));
					arrayList.add(temp);
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
				AlertDialog.Builder builder=new AlertDialog.Builder(DiemCaoBXH.this);
				builder.setTitle("Lỗi!");
				builder.setMessage("Kiểm tra kết nối mạng");
				builder.show();
				return;
			}
			updateUI();
		}
	}

}
