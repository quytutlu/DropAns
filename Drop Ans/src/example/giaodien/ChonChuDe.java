package example.giaodien;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.Object.Topic;

public class ChonChuDe extends ActionBarActivity {
	ListView lv;
	TextView ten;
	ArrayList<Topic> arrayList;
	ArrayAdapter<Topic> arrayAdapter;
	String url = "http://svtl.net/service/svtl.ashx?cmd=getsubject";
	private static String TAG_SUBJECTID = "SubjectId";
	private static String TAG_TITLE = "Title";
	ProgressDialog dialog;
	String User;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.chonchude);
		ten = (TextView) findViewById(R.id.ten);
		Intent t = getIntent();
		User = t.getStringExtra("UserName");
		if (User.equals("unknow")) {
			ten.setText("Bạn chưa đăng nhập");
		} else {
			ten.setText("Xin chào: " + User);
		}
		Init();
		new ParseJSONTask().execute();
	}

	
	private void Init() {
		lv = (ListView) findViewById(R.id.lvTopic);
		arrayList = new ArrayList<Topic>();
		arrayAdapter = new ArrayAdapter<Topic>(this,
				android.R.layout.simple_list_item_1, arrayList);
		lv.setAdapter(arrayAdapter);
		dialog = new ProgressDialog(this);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int id = arrayList.get(arg2).getSubjectId();
				Intent intent = new Intent(ChonChuDe.this,
						example.giaodien.BXH.class);
				intent.putExtra("SUBJECTID", id + "");
				intent.putExtra("UserName", User);
				startActivity(intent);
			}
		});
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
					Topic temp = new Topic();
					temp.setSubjectId(jsonObject.getInt(TAG_SUBJECTID));
					temp.setTitle(jsonObject.getString(TAG_TITLE));
					arrayList.add(temp);
					arrayAdapter.notifyDataSetChanged();
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
			if(result==false){
				AlertDialog.Builder builder=new AlertDialog.Builder(ChonChuDe.this);
				builder.setTitle("Lỗi!");
				builder.setMessage("Kiểm tra kết nối mạng");
				builder.show();
				return;
			}
			updateUI();
		}
	}
}
