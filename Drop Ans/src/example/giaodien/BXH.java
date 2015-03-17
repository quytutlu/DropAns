package example.giaodien;

import java.io.FileOutputStream;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BXH extends Activity {
	String id;
	Button Top10, BatDau;
	TextView display;
	TextView ten;
	ImageView sound;
	String url;
	String User;
	String GoiChuDe;
	ProgressDialog dialog;
	private static String TAG_PLAYID = "PlayId";
	int playid;
	private String BatNhac;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.bxh);
		ten = (TextView) findViewById(R.id.ten);
		Intent intent = getIntent();
		id = intent.getStringExtra("SUBJECTID");
		User = intent.getStringExtra("UserName");
		GoiChuDe = intent.getStringExtra("GoiChuDe");
		BatNhac = intent.getStringExtra("BatNhac");
		ten.setText("Xin chào: " + User);
		init();
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
		Intent intent = new Intent(BXH.this, Menu.class);
		intent.putExtra("BatNhac", BatNhac);
		intent.putExtra("UserName", User);
		startActivity(intent);
	}

	private void init() {
		Top10 = (Button) findViewById(R.id.top10);
		BatDau = (Button) findViewById(R.id.batdau);
		Typeface typeface = Typeface.createFromAsset(getAssets(),
				"font/XepHang.ttf");
		display = (TextView) findViewById(R.id.goichude);
		display.setTypeface(typeface);
		display.setText(GoiChuDe);
		dialog = new ProgressDialog(this);
		sound = (ImageView) findViewById(R.id.sound);
		if (BatNhac.equals("true")) {
			MainActivity.player.start();
			sound.setImageResource(R.drawable.on1);

		} else {
			MainActivity.player.pause();
			sound.setImageResource(R.drawable.off1);
		}

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.thongke:
			Intent intent = new Intent(BXH.this, BXHTop10.class);
			intent.putExtra("SUBJECTID", id);
			intent.putExtra("UserName", User);
			intent.putExtra("GoiChuDe", GoiChuDe);
			intent.putExtra("BatNhac", BatNhac);
			startActivity(intent);
			break;
		case R.id.sound:
			if (BatNhac.equals("true")) {
				sound.setImageResource(R.drawable.off1);
				MainActivity.player.pause();
				BatNhac = "false";
			} else {
				sound.setImageResource(R.drawable.on1);
				MainActivity.player.start();
				BatNhac = "true";
			}
			GhiFile(BatNhac);
			break;
		case R.id.batdau:
			url = "http://svtl.net/service/svtl.ashx?cmd=playversion&subjectid=";
			url += id;
			url += "&username=";
			url += User;
			url += "&version=A.2.3";
			new ParseJSONTask().execute();
			break;
		default:
			break;
		}
	}

	private void updateUI() {
		Intent aIntent = new Intent(BXH.this, example.giaodien.BatDau.class);
		aIntent.putExtra("Disable", "false");
		aIntent.putExtra("LanDau", "true");
		aIntent.putExtra("Playid", playid + "");
		aIntent.putExtra("UserName", User);
		aIntent.putExtra("BatNhac", BatNhac);
		aIntent.putExtra("SUBJECTID", id);
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
			if (jsonstr == null) {
				return false;
			}
			try {
				JSONObject jstemp = new JSONObject(jsonstr);
				playid = jstemp.getInt(TAG_PLAYID);
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
			if (result == false) {
				AlertDialog.Builder builder = new AlertDialog.Builder(BXH.this);
				builder.setTitle("Lỗi!");
				builder.setMessage("Kiểm tra kết nối mạng");
				builder.show();
				return;
			}
			updateUI();
		}
	}

}
