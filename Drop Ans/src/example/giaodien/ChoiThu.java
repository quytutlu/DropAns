package example.giaodien;

import java.io.FileOutputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class ChoiThu extends Activity {

	Handler handler;
	TextView time, ch, tv, sum, ten;
	Button A, B, C, D;
	int DapAnDung;
	int CauHoi = 0;
	int id;
	int i;
	int SunTime = 2;
	boolean CoDong = false;
	boolean KetThuc = false;
	String NoiDung, DapAnA, DapAnB, DapAnC, DapAnD;
	private static String TAG_QUESTIONCONTENT = "QuestionContent";
	private static String TAG_QUESTIONID = "QuestionId";
	private static String TAG_LISTANSWER = "ListAnswer";
	private static String TAG_ANSWERCONTENT = "AnswerContent";
	private static String TAG_ISCORRECT = "IsCorrect";
	String User;
	ProgressDialog dialog;
	private String LanDau;
	private int playdetailid;
	@SuppressWarnings("unused")
	private String TraLoiDung;
	private String playid;
	private String BatNhac;
	ImageView sound;
	private String Disable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choithu);
		Init();
		time.setText(": 30s");
		sum.setText(": 0s");
		Intent intent = getIntent();
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
		intent.putExtra("ChoiThu", "true");
		intent.putExtra("BatNhac", BatNhac);
		intent.putExtra("SUBJECTID","-1");
		startActivity(intent);
	}

	private void HienThongBaoSai() {
		Khoa();
		KetThuc = true;
		Intent intent = new Intent(this, Sai.class);
		intent.putExtra("LanDau", "false");
		intent.putExtra("CauHoi", CauHoi + "");
		intent.putExtra("playdetailid", playdetailid + "");
		intent.putExtra("TraLoiDung", "false");
		intent.putExtra("SunTime", SunTime + "");
		intent.putExtra("UserName", User);
		intent.putExtra("Playid", playid);
		intent.putExtra("ChoiThu", "true");
		intent.putExtra("BatNhac", BatNhac);
		intent.putExtra("SUBJECTID","-1");
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle("Xác Nhận");
		builder.setMessage("Bạn có chắc chắn muốn thoát không?");
		builder.setNegativeButton("Có", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				KetThuc = true;
				Intent intent = new Intent(ChoiThu.this,
						example.giaodien.DangNhap.class);
				intent.putExtra("Kill", "false");
				intent.putExtra("flag", "false");
				intent.putExtra("LanDau", "false");
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

	public void ChuyenCauHoi() {
		CauHoi++;
		CoDong = false;
		new ParseJSONTask().execute();
	}

	// Khởi tạo biến
	public void Init() {
		i = 27;
		ch = (TextView) findViewById(R.id.cauhoi);
		time = (TextView) findViewById(R.id.time);
		ten = (TextView) findViewById(R.id.ten);
		ten.setText("Bạn chưa đăng nhập");
		tv = (TextView) findViewById(R.id.noidung);
		sum = (TextView) findViewById(R.id.sum);
		A = (Button) findViewById(R.id.A);
		B = (Button) findViewById(R.id.B);
		C = (Button) findViewById(R.id.C);
		D = (Button) findViewById(R.id.D);
		dialog = new ProgressDialog(this);
		sound = (ImageView) findViewById(R.id.sound);
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

	// bắtt sự kiện
	public void ChonA(View v) {
		if (DapAnDung == 1) {
			HienThongBaoDung();
		} else {
			HienThongBaoSai();
		}
	}

	public void ChonB(View v) {
		if (DapAnDung == 2) {
			HienThongBaoDung();
		} else {
			HienThongBaoSai();
		}
	}

	public void ChonC(View v) {
		if (DapAnDung == 3) {
			HienThongBaoDung();
		} else {
			HienThongBaoSai();
		}
	}

	public void ChonD(View v) {
		if (DapAnDung == 4) {
			HienThongBaoDung();
		} else {
			HienThongBaoSai();
		}
	}

	// tạo tuyến chạy song song
	public void TaoTuyen() {
		handler = new Handler() {
			public void handleMessage(Message msg) {
				time.setText(": " + msg.arg1 + "s");
				ch.setText(CauHoi + "");
				SunTime++;
				sum.setText(": " + SunTime + "s");
				if (msg.arg1 == 0 && CoDong) {
					Intent temp = new Intent(getApplicationContext(),
							example.giaodien.KetThuc.class);
					temp.putExtra("Diem", (CauHoi - 1) + "");
					temp.putExtra("SumTime", SunTime + "");
					temp.putExtra("UserName", User);
					temp.putExtra("BatNhac", BatNhac);
					temp.putExtra("SUBJECTID", "-1");
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
		CoDong = true;
		ch.setText(CauHoi + "");
		tv.setText(NoiDung);
		A.setText(DapAnA);
		B.setText(DapAnB);
		C.setText(DapAnC);
		D.setText(DapAnD);
	}

	public class ParseJSONTask extends AsyncTask<Void, Void, Boolean> {

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
			String jsonstr = webServiceHandler
					.getJSONData("http://svtl.net/service/svtl.ashx?cmd=getquestion&sessionid=2");
			if (jsonstr == null) {
				return false;
			}
			try {
				JSONObject jsonObject = new JSONObject(jsonstr);
				NoiDung = jsonObject.getString(TAG_QUESTIONCONTENT);
				id = jsonObject.getInt(TAG_QUESTIONID);
				JSONArray jsonArray = jsonObject.getJSONArray(TAG_LISTANSWER);
				for (int i = 0; i < jsonArray.length(); i++) {
					boolean flag = false;
					JSONObject temp = jsonArray.getJSONObject(i);
					switch (i) {
					case 0:
						DapAnA = temp.getString(TAG_ANSWERCONTENT);
						flag = temp.getBoolean(TAG_ISCORRECT);
						if (flag) {
							DapAnDung = 1;
						}
						break;
					case 1:
						DapAnB = temp.getString(TAG_ANSWERCONTENT);
						flag = temp.getBoolean(TAG_ISCORRECT);
						if (flag) {
							DapAnDung = 2;
						}
						break;
					case 2:
						DapAnC = temp.getString(TAG_ANSWERCONTENT);
						flag = temp.getBoolean(TAG_ISCORRECT);
						if (flag) {
							DapAnDung = 3;
						}
						break;
					case 3:
						DapAnD = temp.getString(TAG_ANSWERCONTENT);
						flag = temp.getBoolean(TAG_ISCORRECT);
						if (flag) {
							DapAnDung = 4;
						}
						break;
					default:
						break;
					}
				}
				return true;
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "Lỗi",
						Toast.LENGTH_SHORT).show();
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
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ChoiThu.this);
				builder.setTitle("Lỗi!");
				builder.setMessage("Kiểm tra kết nối mạng");
				builder.show();
				return;
			}
			updateUI();
		}
	}

}
