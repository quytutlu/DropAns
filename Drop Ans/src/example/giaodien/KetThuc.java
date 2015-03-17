package example.giaodien;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class KetThuc extends Activity {
	TextView tvdiem, notidiem, notisum;
	String User;
	Handler handler;
	private int diem;
	private int SumTime;
	private String BatNhac;
	ProgressDialog dialog;
	private int _playid;
	private String sid;
	private String url;
	private static String TAG_PLAYID = "PlayId";

	@Override
	public void onBackPressed() {
		if (User.equals("unknow")) {
			Intent intent = new Intent(getApplicationContext(),
					example.giaodien.DangNhap.class);
			intent.putExtra("Kill", "false");
			intent.putExtra("flag", "flase");
			intent.putExtra("LanDau", "false");
			intent.putExtra("BatNhac", BatNhac);
			startActivity(intent);
		} else {
			Intent intent = new Intent(getApplicationContext(),
					example.giaodien.Menu.class);
			intent.putExtra("UserName", User);
			intent.putExtra("BatNhac", BatNhac);
			startActivity(intent);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ket_thuc);
		tvdiem = (TextView) findViewById(R.id.diem);
		Typeface typeface = Typeface.createFromAsset(getAssets(),
				"font/thongbao.ttf");
		notidiem = (TextView) findViewById(R.id.notiDiem);
		notisum = (TextView) findViewById(R.id.notisum);
		notidiem.setTypeface(typeface);
		notisum.setTypeface(typeface);
		notidiem.setText("Điểm của bạn ");

		Intent intent = getIntent();
		diem = Integer.parseInt(intent.getStringExtra("Diem"));
		SumTime = Integer.parseInt(intent.getStringExtra("SumTime"));
		User = intent.getStringExtra("UserName");
		notisum.setText("Thời gian chơi: " + SumTime + "s");
		BatNhac = intent.getStringExtra("BatNhac");
		sid=intent.getStringExtra("SUBJECTID");
		
		if (BatNhac.equals("true")) {
			MainActivity.player.start();

		} else {
			MainActivity.player.pause();
		}
		TaoTuyen();
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

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.returnmain:
			if (User.equals("unknow")) {
				Intent intent = new Intent(getApplicationContext(),
						example.giaodien.DangNhap.class);
				intent.putExtra("Kill", "false");
				intent.putExtra("flag", "flase");
				intent.putExtra("LanDau", "false");
				intent.putExtra("BatNhac", BatNhac);
				startActivity(intent);
			} else {
				Intent intent = new Intent(getApplicationContext(),
						example.giaodien.Menu.class);
				intent.putExtra("UserName", User);
				intent.putExtra("BatNhac", BatNhac);
				startActivity(intent);
			}
			break;
		case R.id.choilai:
			if (User.equals("unknow")) {
				Intent temp = new Intent(KetThuc.this,
						example.giaodien.ChoiThu.class);
				temp.putExtra("Disable", "false");
				temp.putExtra("LanDau", "true");
				temp.putExtra("UserName", "unknow");
				temp.putExtra("Playid", "1");
				temp.putExtra("BatNhac", BatNhac);
				startActivity(temp);
			} else {
				url = "http://svtl.net/service/svtl.ashx?cmd=playversion&subjectid=";
				url += sid;
				url += "&username=";
				url += User;
				url += "&version=A.2.3";
				dialog=new ProgressDialog(this);
				new ParseJSONTask().execute();
			}
			break;
		default:
			break;
		}
	}

	@SuppressLint("HandlerLeak")
	public void TaoTuyen() {
		handler = new Handler() {
			public void handleMessage(Message msg) {
				tvdiem.setText(msg.arg1 + "");
			}
		};
		doStart();
	}

	public void doStart() {
		final Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i <= diem; i++) {
					SystemClock.sleep(100);
					Message msg = handler.obtainMessage();
					msg.arg1 = i;
					handler.sendMessage(msg);
				}
				return;
			}
		});

		th.start();
	}
	
	private void updateUI() {
		Intent aIntent = new Intent(KetThuc.this, example.giaodien.BatDau.class);
		aIntent.putExtra("Disable", "false");
		aIntent.putExtra("LanDau", "true");
		aIntent.putExtra("Playid", _playid + "");
		aIntent.putExtra("UserName", User);
		aIntent.putExtra("BatNhac", BatNhac);
		aIntent.putExtra("SUBJECTID", sid);
		startActivity(aIntent);
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
				JSONObject jstemp = new JSONObject(jsonstr);
				_playid = jstemp.getInt(TAG_PLAYID);
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
				AlertDialog.Builder builder=new AlertDialog.Builder(KetThuc.this);
				builder.setTitle("Lỗi!");
				builder.setMessage("Kiểm tra kết nối mạng");
				builder.show();
				return;
			}
			updateUI();
		}
	}
}
