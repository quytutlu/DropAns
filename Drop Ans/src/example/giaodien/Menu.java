package example.giaodien;

import java.io.FileOutputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.Object.Topic;

public class Menu extends Activity {

	String User;
	TextView ten;
	ListView lv;
	ArrayList<Topic> arrayList;
	TopicMyArrayAdapter adapter;
	String url = "http://svtl.net/service/svtl.ashx?cmd=getsubject";
	private static String TAG_SUBJECTID = "SubjectId";
	private static String TAG_TITLE = "Title";
	private static String TAG_ICONPATH = "IconPath";
	ProgressDialog dialog;
	private String BatNhac;
	ImageView sound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.menu);
		ten = (TextView) findViewById(R.id.ten);
		Intent intent = getIntent();
		User = intent.getStringExtra("UserName");
		BatNhac = intent.getStringExtra("BatNhac");
		ten.setText("Xin chào: " + User);
		Init();

		new ParseJSONTask().execute();
	}

	private void GhiFile(String NoiDung) {
		try {
			FileOutputStream out = openFileOutput("CauHinh.txt", MODE_PRIVATE);
			out.write(NoiDung.getBytes());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		MainActivity.player.pause();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (BatNhac.equals("true")) {
			MainActivity.player.start();
		}
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle("Xác nhận");
		builder.setMessage("Bạn có chắc chắn muốn thoát không?");
		builder.setNegativeButton("Có", new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Intent intent = new Intent(getApplicationContext(),
						example.giaodien.DangNhap.class);
				intent.putExtra("Kill", "true");
				intent.putExtra("flag", "false");
				intent.putExtra("LanDau", "false");
				intent.putExtra("BatNhac", BatNhac);
				startActivity(intent);
				MainActivity.player.stop();
			}
		});
		builder.setPositiveButton("Không", new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				dialog.cancel();
			}
		});
		builder.show();

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.logout:
			Intent intent = new Intent(getApplicationContext(),
					example.giaodien.DangNhap.class);
			intent.putExtra("Kill", "false");
			intent.putExtra("flag", "false");
			intent.putExtra("LanDau", "false");
			intent.putExtra("BatNhac", BatNhac);
			startActivity(intent);
			break;
		case R.id.sound:
			if (BatNhac.equals("true")) {
				sound.setImageResource(R.drawable.off2);
				MainActivity.player.pause();
				BatNhac = "false";
			} else {
				sound.setImageResource(R.drawable.on);
				MainActivity.player.start();
				BatNhac = "true";
			}
			GhiFile(BatNhac);
			break;
		}
	}

	private void Init() {
		lv = (ListView) findViewById(R.id.lvTopic);
		arrayList = new ArrayList<Topic>();
		adapter = new TopicMyArrayAdapter(this, R.layout.mylistviewtopic,
				arrayList);
		lv.setAdapter(adapter);
		dialog = new ProgressDialog(this);
		sound = (ImageView) findViewById(R.id.sound);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int id = arrayList.get(arg2).getSubjectId();
				Intent intent = new Intent(Menu.this,
						example.giaodien.BXH.class);
				intent.putExtra("SUBJECTID", id + "");
				// intent.putExtra("SUBJECTID","5");
				intent.putExtra("UserName", User);
				intent.putExtra("GoiChuDe", arrayList.get(arg2).getTitle());
				intent.putExtra("BatNhac", BatNhac);
				startActivity(intent);
			}
		});
		if (BatNhac.equals("true")) {
			sound.setImageResource(R.drawable.on);
			MainActivity.player.start();

		} else {
			sound.setImageResource(R.drawable.off2);
			MainActivity.player.pause();
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
			if (jsonstr == null) {
				return false;
			}
			try {
				JSONArray jsonArray = new JSONArray(jsonstr);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					Topic temp = new Topic();
					temp.setSubjectId(jsonObject.getInt(TAG_SUBJECTID));
					temp.setTitle(jsonObject.getString(TAG_TITLE));
					temp.setUrlicon(jsonObject.getString(TAG_ICONPATH));
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
			if (result == false) {
				AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
				builder.setTitle("Lỗi!");
				builder.setMessage("Kiểm tra kết nối mạng");
				builder.show();
				return;
			}
			updateUI();
		}
	}
}
