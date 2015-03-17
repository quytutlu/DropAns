package example.giaodien;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class HinhAnh extends Activity {
	Handler handler;
	TextView time, ch, tv, sum, ten;
	ImageView logo;
	Button A, B, C, D;
	int DapAnDung;
	int CauHoi = 0;
	int SunTime = 2;
	boolean CoDong = false;
	boolean KetThuc = false;
	boolean key = false;
	int i;
	String NoiDung, DapAnA, DapAnB, DapAnC, DapAnD;
	String urlim;
	String urlGhiDiem;
	String User;
	int playdetailid = -1;
	String playid;
	ProgressDialog dialog;
	protected String BatNhac;
	protected String sid;
	private boolean KhoaQuay;
	ImageView sound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.hinhanh);
		Init();
		HideView();
		Intent intent = getIntent();
		CauHoi = Integer.parseInt(intent.getStringExtra("CauHoi"));
		playdetailid = Integer.parseInt(intent.getStringExtra("playdetailid"));
		SunTime = Integer.parseInt(intent.getStringExtra("SunTime"));
		time.setText(": 30s");
		sum.setText(": " + SunTime + "s");
		ch.setText(CauHoi+"");
		i = 29;
		BatNhac = intent.getStringExtra("BatNhac");
		if(BatNhac.equals("true")){
			sound.setImageResource(R.drawable.on);
			MainActivity.player.start();
		}else{
			sound.setImageResource(R.drawable.off2);
			MainActivity.player.pause();
		}
		User = intent.getStringExtra("UserName");
		playid = intent.getStringExtra("Playid");
		sid=intent.getStringExtra("SUBJECTID");
		urlim=intent.getStringExtra("MediaUrl");
		NoiDung=intent.getStringExtra("NoiDung");
		DapAnA=intent.getStringExtra("DapAnA");
		DapAnB=intent.getStringExtra("DapAnB");
		DapAnC=intent.getStringExtra("DapAnC");
		DapAnD=intent.getStringExtra("DapAnD");
		DapAnDung=Integer.parseInt(intent.getStringExtra("DapAnDung"));
		ten.setText("Xin chào: " + User);
		LoadLogo();
		TaoTuyen();
	}

	public void Init() {
		// openDB();
		i = 27;
		ten = (TextView) findViewById(R.id.ten);
		ch = (TextView) findViewById(R.id.cauhoi);
		time = (TextView) findViewById(R.id.time);
		tv = (TextView) findViewById(R.id.noidung);
		tv.setMovementMethod(new ScrollingMovementMethod());
		sum = (TextView) findViewById(R.id.sum);
		A = (Button) findViewById(R.id.A);
		B = (Button) findViewById(R.id.B);
		C = (Button) findViewById(R.id.C);
		D = (Button) findViewById(R.id.D);
		sound = (ImageView) findViewById(R.id.sound);
		logo=(ImageView) findViewById(R.id.logo);
		dialog = new ProgressDialog(this);

		Typeface typeface = Typeface.createFromAsset(getAssets(),
				"font/upcib.ttf");
		Typeface typeface2 = Typeface.createFromAsset(getAssets(),
				"font/Chococooky.ttf");
		time.setTypeface(typeface);
		sum.setTypeface(typeface);
		ch.setTypeface(typeface);
		tv.setTypeface(typeface2);
		A.setTypeface(typeface2);
		B.setTypeface(typeface2);
		C.setTypeface(typeface2);
		D.setTypeface(typeface2);

	}
	
	public void HideView(){
		tv.setVisibility(View.GONE);
		A.setVisibility(View.GONE);
		B.setVisibility(View.GONE);
		C.setVisibility(View.GONE);
		D.setVisibility(View.GONE);
		logo.setVisibility(View.GONE);
	}
	
	public void ShowView(){
		tv.setVisibility(View.VISIBLE);
		A.setVisibility(View.VISIBLE);
		B.setVisibility(View.VISIBLE);
		C.setVisibility(View.VISIBLE);
		D.setVisibility(View.VISIBLE);
		logo.setVisibility(View.VISIBLE);
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

	private void Khoa() {
		A.setEnabled(false);
		B.setEnabled(false);
		C.setEnabled(false);
		D.setEnabled(false);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (BatNhac.equals("true")) {
		}
	}

	public void onClick(View v) {
		if (BatNhac.equals("true")) {
			sound.setImageResource(R.drawable.off2);
			BatNhac = "false";
			MainActivity.player.pause();
		} else {
			sound.setImageResource(R.drawable.on);
			BatNhac = "true";
			MainActivity.player.start();
		}
		GhiFile(BatNhac);
	}

	private void HienThongBaoDung() {
		Khoa();
		KetThuc = true;
		Intent intent = new Intent(this, Dung.class);
		intent.putExtra("LanDau", "false");
		intent.putExtra("CauHoi", CauHoi + "");
		intent.putExtra("playdetailid", playdetailid + "");
		intent.putExtra("TraLoiDung", "true");
		intent.putExtra("SunTime", SunTime + "");
		intent.putExtra("UserName", User);
		intent.putExtra("Playid", playid);
		intent.putExtra("ChoiThu", "false");
		intent.putExtra("BatNhac", BatNhac);
		intent.putExtra("SUBJECTID", sid);
		startActivity(intent);
	}

	private void HienThongBaoSai() {
		KhoaQuay = true;
		Khoa();
		KetThuc = true;
		Push();
		Intent intent = new Intent(this, Sai.class);
		intent.putExtra("LanDau", "false");
		intent.putExtra("CauHoi", CauHoi + "");
		intent.putExtra("playdetailid", playdetailid + "");
		intent.putExtra("TraLoiDung", "false");
		intent.putExtra("SunTime", SunTime + "");
		intent.putExtra("UserName", User);
		intent.putExtra("Playid", playid);
		intent.putExtra("ChoiThu", "false");
		intent.putExtra("BatNhac", BatNhac);
		intent.putExtra("SUBJECTID", sid);
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		if (!KhoaQuay) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(android.R.drawable.ic_dialog_alert);
			builder.setTitle("Xác nhận");
			builder.setMessage("Bạn có chắc chắc muốn thoát không?");
			builder.setPositiveButton("Có", new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					KetThuc = true;
					Intent intent = new Intent(HinhAnh.this,
							example.giaodien.Menu.class);
					intent.putExtra("UserName", User);
					intent.putExtra("BatNhac", BatNhac);
					startActivity(intent);
				}
			});
			builder.setNegativeButton("Không", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();

				}
			});
			builder.show();
		}
	}

	private void Push() {
		urlGhiDiem = "http://svtl.net/service/svtl.ashx?cmd=finish&playid=";
		urlGhiDiem += playid;
		urlGhiDiem += "&totaltime=";
		urlGhiDiem += SunTime;
		urlGhiDiem += "&playdetailid=";
		urlGhiDiem += playdetailid;
		new ParseJSONTask().execute();
	}

	// bắt sự kiện
	public void ChonA(View v) {
		if (DapAnDung == 1) {
			A.setTextColor(Color.parseColor("#00FFFF"));
			HienThongBaoDung();
		} else {
			A.setTextColor(Color.parseColor("#FF0000"));
			HienThongBaoSai();
		}
	}

	public void ChonB(View v) {
		if (DapAnDung == 2) {
			B.setTextColor(Color.parseColor("#00FFFF"));
			HienThongBaoDung();
		} else {
			B.setTextColor(Color.parseColor("#FF0000"));
			HienThongBaoSai();
		}
	}

	public void ChonC(View v) {
		if (DapAnDung == 3) {
			C.setTextColor(Color.parseColor("#00FFFF"));
			HienThongBaoDung();
		} else {
			C.setTextColor(Color.parseColor("#FF0000"));
			HienThongBaoSai();
		}
	}

	public void ChonD(View v) {
		if (DapAnDung == 4) {
			D.setTextColor(Color.parseColor("#00FFFF"));
			HienThongBaoDung();
		} else {
			D.setTextColor(Color.parseColor("#FF0000"));
			HienThongBaoSai();
		}
	}

	// tọa tuyến chạy song song
	public void TaoTuyen() {
		handler = new Handler() {
			public void handleMessage(Message msg) {
				time.setText(": " + msg.arg1 + "s");
				SunTime++;
				sum.setText(": " + SunTime + "s");
				if (msg.arg1 == 0 && CoDong) {
					Push();
					Intent temp = new Intent(getApplicationContext(),
							example.giaodien.KetThuc.class);
					temp.putExtra("Diem", (CauHoi - 1) + "");
					temp.putExtra("SumTime", SunTime + "");
					temp.putExtra("UserName", User);
					temp.putExtra("BatNhac", BatNhac);
					temp.putExtra("Playid", playid);
					temp.putExtra("SUBJECTID", sid);
					startActivity(temp);
				}
			}
		};
		doStart();
	}

	public void doStart() {
		final Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				for (; i >= 0; i--) {
					if (KetThuc) {
						return;
					}
					while (!CoDong) {
						SystemClock.sleep(1000);
					}
					SystemClock.sleep(1000);
					Message msg = handler.obtainMessage();
					msg.arg1 = i;
					handler.sendMessage(msg);
				}
				return;
			}
		});
		th.start();
	}

	private void LoadLogo() {
		CoDong = false;
		new LoadImage().execute(urlim);
	}

	private class LoadImage extends AsyncTask<String, String, Bitmap> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected Bitmap doInBackground(String... args) {
			Bitmap bitmap = null;
			try {
				bitmap = BitmapFactory.decodeStream((InputStream) new URL(
						args[0]).getContent());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return bitmap;
		}

		protected void onPostExecute(Bitmap image) {
			if (image != null) {
				logo.setImageBitmap(image);
				tv.setText(NoiDung);
				A.setText(DapAnA);
				B.setText(DapAnB);
				C.setText(DapAnC);
				D.setText(DapAnD);
				CoDong = true;
				ShowView();
			} else {
				CoDong=true;
				KetThuc = true;
				tv.setText("");
				A.setText("");
				B.setText("");
				C.setText("");
				D.setText("");
				AlertDialog.Builder builder = new AlertDialog.Builder(
						HinhAnh.this);
				builder.setTitle("Lỗi!");
				builder.setMessage("Lỗi kết nối!");
				builder.show();
			}
		}
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
			String jsonstr = webServiceHandler.getJSONData(urlGhiDiem);
			if (jsonstr == null) {
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			if (result == false) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						HinhAnh.this);
				builder.setTitle("Lỗi!");
				builder.setMessage("Kiểm tra kết nối mạng");
				builder.show();
				return;
			}
		}
	}

}
