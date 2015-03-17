package example.giaodien;

import java.io.FileOutputStream;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
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
import android.widget.Toast;

import com.Object.Answer;

@SuppressLint("HandlerLeak")
public class BatDau extends Activity {
	Handler handler;
	TextView time, ch, tv, sum, ten;
	Button A, B, C, D;
	int DapAnDung;
	int CauHoi = 0;
	int id;
	int SunTime = 2;
	boolean CoDong = false;
	boolean KetThuc = false;
	boolean key = false;
	int i;
	String NoiDung, DapAnA, DapAnB, DapAnC, DapAnD;
	private static String TAG_QUESTIONCONTENT = "QuestionContent";
	private static String TAG_QUESTIONID = "QuestionId";
	private static String TAG_LISTANSWER = "ListAnswer";
	private static String TAG_ANSWERCONTENT = "AnswerContent";
	private static String TAG_ISCORRECT = "IsCorrect";
	private static String TAG_PLAYDETAILID = "PlayDetailId";
	private static String TAG_MEDIAURL = "MediaUrl";
	String url = "http://svtl.net/service/svtl.ashx?cmd=getquestionbyplayid&playid=";
	String URL;
	String urlGhiDiem;
	String User;
	int playdetailid = -1;
	String playid;
	String LanDau;
	String TraLoiDung;
	String urlim;
	ImageView sound;
	ProgressDialog dialog;
	private String BatNhac;
	private String Disable;
	private String sid;
	private boolean KhoaQuay = false;
	private int[] a;
	private Answer answer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.batdau);

		Init();
		HideView();
		Intent intent = getIntent();
		time.setText(": 30s");
		sum.setText(": 0s");
		Disable = intent.getStringExtra("Disable");
		if (Disable.equals("true")) {
			Khoa();
		} else {
			Mo();
		}
		LanDau = intent.getStringExtra("LanDau");
		if (LanDau.equals("false")) {
			CauHoi = Integer.parseInt(intent.getStringExtra("CauHoi"));
			playdetailid = Integer.parseInt(intent
					.getStringExtra("playdetailid"));
			TraLoiDung = intent.getStringExtra("TraLoiDung");
			SunTime = Integer.parseInt(intent.getStringExtra("SunTime"));
			SunTime += 2;
			time.setText(": 30s");
			sum.setText(": " + SunTime + "s");
			i = 29;
		}
		User = intent.getStringExtra("UserName");
		playid = intent.getStringExtra("Playid");
		BatNhac = intent.getStringExtra("BatNhac");
		if (BatNhac.equals("true")) {
			sound.setImageResource(R.drawable.on);
			MainActivity.player.start();

		} else {
			sound.setImageResource(R.drawable.off2);
			MainActivity.player.pause();
		}
		sid = intent.getStringExtra("SUBJECTID");
		url += playid;
		url += "&playdetailid=";

		ten.setText("Xin chào: " + User);

		ChuyenCauHoi();
		TaoTuyen();
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

	private void Mo() {
		A.setEnabled(true);
		B.setEnabled(true);
		C.setEnabled(true);
		D.setEnabled(true);
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
		MainActivity.player.pause();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (BatNhac.equals("true")) {
			MainActivity.player.start();
		}
	}

	private void HienThongBaoDung() {
		KhoaQuay = true;
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
			builder.setNegativeButton("Có", new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					KetThuc = true;
					Intent intent = new Intent(BatDau.this,
							example.giaodien.Menu.class);
					intent.putExtra("UserName", User);
					intent.putExtra("BatNhac", BatNhac);
					startActivity(intent);
				}
			});
			builder.setPositiveButton("Không", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();

				}
			});
			builder.show();
		}
	}

	public void ChuyenCauHoi() {
		CauHoi++;
		URL = url;
		URL += playdetailid;
		CoDong = false;
		key = true;
		new ParseJSONTask().execute();
	}

	// khởi tạo biến
	public void Init() {
		a = new int[4];
		answer = new Answer();
		TaoSo();
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

	public void HideView() {
		tv.setVisibility(View.GONE);
		A.setVisibility(View.GONE);
		B.setVisibility(View.GONE);
		C.setVisibility(View.GONE);
		D.setVisibility(View.GONE);
	}

	public void ShowView() {
		tv.setVisibility(View.VISIBLE);
		A.setVisibility(View.VISIBLE);
		B.setVisibility(View.VISIBLE);
		C.setVisibility(View.VISIBLE);
		D.setVisibility(View.VISIBLE);
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

	private void Push() {
		key = false;
		urlGhiDiem = "http://svtl.net/service/svtl.ashx?cmd=finish&playid=";
		urlGhiDiem += playid;
		urlGhiDiem += "&totaltime=";
		urlGhiDiem += SunTime;
		urlGhiDiem += "&playdetailid=";
		urlGhiDiem += playdetailid;
		new ParseJSONTask().execute();
		Intent temp = new Intent(getApplicationContext(),
				example.giaodien.KetThuc.class);
		temp.putExtra("Diem", (CauHoi - 1) + "");
		temp.putExtra("SumTime", SunTime + "");
		temp.putExtra("UserName", User);
		temp.putExtra("BatNhac", BatNhac);
		temp.putExtra("Playid", playid);
		temp.putExtra("SUBJECTID", sid);
	}

	// bắt sự kiện
	public void ChonA(View v) {
		if (answer.isCorrect(0)) {
			A.setTextColor(Color.parseColor("#00FFFF"));
			HienThongBaoDung();
		} else {
			A.setTextColor(Color.parseColor("#FF0000"));
			HienThongBaoSai();
		}
	}

	public void ChonB(View v) {
		if (answer.isCorrect(1)) {
			B.setTextColor(Color.parseColor("#00FFFF"));
			HienThongBaoDung();
		} else {
			B.setTextColor(Color.parseColor("#FF0000"));
			HienThongBaoSai();
		}
	}

	public void ChonC(View v) {
		if (answer.isCorrect(2)) {
			C.setTextColor(Color.parseColor("#00FFFF"));
			HienThongBaoDung();
		} else {
			C.setTextColor(Color.parseColor("#FF0000"));
			HienThongBaoSai();
		}
	}

	public void ChonD(View v) {
		if (answer.isCorrect(3)) {
			D.setTextColor(Color.parseColor("#00FFFF"));
			HienThongBaoDung();
		} else {
			D.setTextColor(Color.parseColor("#FF0000"));
			HienThongBaoSai();
		}
	}

	private void TaoSo() {
		Random ran = new Random();
		int count = 0;
		int temp;
		boolean co;
		do {
			co = false;
			temp = ran.nextInt(4);
			for (int i = 0; i < count; i++) {
				if (a[i] == temp) {
					co = true;
					break;
				}
			}
			if (!co) {
				a[count++] = temp;
			}
		} while (count < 4);
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

	private void updateUI() {
		if (!urlim.equals("http://nhch.svtl.net")) {
			// Toast.makeText(this, DapAnDung + "", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(this, HinhAnh.class);
			intent.putExtra("CauHoi", CauHoi + "");
			intent.putExtra("playdetailid", playdetailid + "");
			intent.putExtra("SunTime", SunTime + "");
			intent.putExtra("BatNhac", BatNhac);
			intent.putExtra("UserName", User);
			intent.putExtra("Playid", playid);
			intent.putExtra("SUBJECTID", sid);
			intent.putExtra("MediaUrl", urlim);
			intent.putExtra("NoiDung", NoiDung);
			intent.putExtra("DapAnA", answer.getAnswerContent(0));
			intent.putExtra("DapAnB", answer.getAnswerContent(1));
			intent.putExtra("DapAnC", answer.getAnswerContent(2));
			intent.putExtra("DapAnD", answer.getAnswerContent(3));
			intent.putExtra("DapAnDung", DapAnDung + "");
			startActivity(intent);
			return;
		}
		Toast.makeText(this, DapAnDung + "", Toast.LENGTH_SHORT).show();
		ShowView();
		CoDong = true;
		ch.setText(CauHoi + "");
		tv.setText(NoiDung);
		A.setText(answer.getAnswerContent(0));
		B.setText(answer.getAnswerContent(1));
		C.setText(answer.getAnswerContent(2));
		D.setText(answer.getAnswerContent(3));
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
			if (key) {
				WebServiceHandler webServiceHandler = new WebServiceHandler();
				String jsonstr = webServiceHandler.getJSONData(URL);
				if (jsonstr == null) {
					return false;
				}
				try {
					JSONObject jsonObject = new JSONObject(jsonstr);
					playdetailid = jsonObject.getInt(TAG_PLAYDETAILID);
					NoiDung = jsonObject.getString(TAG_QUESTIONCONTENT);
					urlim = jsonObject.getString(TAG_MEDIAURL);
					id = jsonObject.getInt(TAG_QUESTIONID);
					JSONArray jsonArray = jsonObject
							.getJSONArray(TAG_LISTANSWER);
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject temp = jsonArray.getJSONObject(i);
						answer.setAnswerContent(
								temp.getString(TAG_ANSWERCONTENT), a[i]);
						answer.setIsCorrect(temp.getBoolean(TAG_ISCORRECT),
								a[i]);
						if (answer.isCorrect(a[i])) {
							DapAnDung = a[i] + 1;
						}
					}
					return true;
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "Lá»—i",
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			} else {
				WebServiceHandler webServiceHandler = new WebServiceHandler();
				String jsonstr = webServiceHandler.getJSONData(urlGhiDiem);
				if (jsonstr == null) {
					return false;
				}
				return true;
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
				AlertDialog.Builder builder = new AlertDialog.Builder(
						BatDau.this);
				builder.setTitle("Lỗi!");
				builder.setMessage("Kiểm tra kết nối mạng");
				builder.show();
				return;
			}
			updateUI();
		}
	}
}
