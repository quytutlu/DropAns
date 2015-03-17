package example.giaodien;

import java.io.FileOutputStream;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class DangNhap extends Activity {
	EditText User;
	EditText Pass;
	Button login;
	ProgressDialog dialog;
	boolean flag;
	private String TAG_SUCESS = "Sucess";
	String url = "http://svtl.net/service/svtl.ashx?cmd=login&username=";
	String Urltemp;
	String user;
	public static MediaPlayer player;
	private DBAdapter myDb;
	private String status;
	private String _acc;
	private String _pass;
	private CheckBox GhiNho;
	private ImageView sound;
	private String BatNhac="";
	private String Kill;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dangnhap);
		Init();
		Intent intent = getIntent();
		Kill=intent.getStringExtra("Kill");
		if(Kill.equals("true")){
			Intent intent1 = new Intent(DangNhap.this, MainActivity.class);
			intent1.putExtra("KT", "KetThuc");
			intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent1);
			finish();
		}
		String flag;
		BatNhac= intent.getStringExtra("BatNhac");
		if(BatNhac.equals("true")){
			sound.setImageResource(R.drawable.on);
			MainActivity.player.start();
		}else{
			sound.setImageResource(R.drawable.off2);
			MainActivity.player.pause();
		}
		
		flag = intent.getStringExtra("flag");
		if (flag.equals("true")) {
			User.setText(intent.getStringExtra("User"));
			Pass.setText(intent.getStringExtra("Pass"));
			return;
		}
		flag = intent.getStringExtra("LanDau");
		if (flag.equals("true")) {
		}
		GetAccount();
		if (status.equals("false")) {
			return;
		}
		GhiNho.setChecked(true);
		User.setText(_acc);
		Pass.setText(_pass);
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
				Intent intent = new Intent(DangNhap.this, MainActivity.class);
				intent.putExtra("KT", "KetThuc");
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
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
	
	private void GhiFile(String NoiDung) {
		try {
			FileOutputStream out = openFileOutput("CauHinh.txt",MODE_PRIVATE);
			out.write(NoiDung.getBytes());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	private void GetAccount() {
		myDb.open();
		Cursor cursor = myDb.getRow(1);
		if (!cursor.moveToFirst()) {
			status = "false";
			return;
		}
		status = cursor.getString(DBAdapter.COL_STATUS);
		_acc = cursor.getString(DBAdapter.COL_ACC);
		_pass = cursor.getString(DBAdapter.COL_PASS);
		myDb.close();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
	protected void onStop() {
		super.onStop();
	}

	private void Init() {
		User = (EditText) findViewById(R.id.user);
		Pass = (EditText) findViewById(R.id.pass);
		login = (Button) findViewById(R.id.btlogin);
		dialog = new ProgressDialog(this);
		GhiNho = (CheckBox) findViewById(R.id.check);
		sound=(ImageView) findViewById(R.id.sound);
		myDb = new DBAdapter(this);
	}

	public void Onclick(View v) {
		switch (v.getId()) {
		case R.id.btlogin:
			user = User.getText().toString();
			String pass_ = Pass.getText().toString();
			Urltemp = url;
			Urltemp += user;
			Urltemp += "&pass=";
			Urltemp += pass_;
			new ParseJSONTask().execute();
			break;
		case R.id.demo:
			Intent temp = new Intent(DangNhap.this,
					example.giaodien.ChoiThu.class);
			temp.putExtra("Disable", "false");
			temp.putExtra("LanDau", "true");
			temp.putExtra("UserName", "unknow");
			temp.putExtra("Playid", "1");
			temp.putExtra("BatNhac", BatNhac);
			startActivity(temp);
			break;
		case R.id.dangky:
			Intent aIntent = new Intent(DangNhap.this,
					example.giaodien.DangKy.class);
			aIntent.putExtra("BatNhac", BatNhac);
			startActivity(aIntent);
			break;
		case R.id.sound:
			if(BatNhac.equals("true")){
				sound.setImageResource(R.drawable.off2);
				MainActivity.player.pause();
				BatNhac="false";
			}else{
				sound.setImageResource(R.drawable.on);
				MainActivity.player.start();
				BatNhac="true";
			}
			GhiFile(BatNhac);
			break;
		}

	}

	private void updateUI() {
		if (flag) {
			myDb.open();
			Cursor cursor = myDb.getRow(1);
			if (GhiNho.isChecked() && !cursor.moveToFirst()) {
				myDb.insertRow("true", User.getText().toString(), Pass
						.getText().toString());
			} else if (GhiNho.isChecked() && cursor.moveToFirst()) {
				myDb.updateRow(1, "true", User.getText().toString(), Pass
						.getText().toString());
			} else if (!GhiNho.isChecked() && cursor.moveToFirst()) {
				myDb.updateRow(1, "false", User.getText().toString(), Pass
						.getText().toString());
			} else if (!GhiNho.isChecked() && !cursor.moveToFirst()) {
				myDb.insertRow("false", User.getText().toString(), Pass
						.getText().toString());
			}
			myDb.close();
			User.setText("");
			Pass.setText("");
			Toast.makeText(getApplicationContext(), "Đăng nhập thành công!",
					Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(DangNhap.this,
					example.giaodien.Menu.class);
			intent.putExtra("UserName", user);
			intent.putExtra("BatNhac", BatNhac);
			startActivity(intent);
		} else {
			Pass.setText("");
			Toast.makeText(getApplicationContext(), "Đăng nhập thất bại!",
					Toast.LENGTH_SHORT).show();
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
			String jsonstr = webServiceHandler.getJSONData(Urltemp);
			if (jsonstr == null) {
				return false;
			}
			try {
				JSONObject jsonObject = new JSONObject(jsonstr);
				flag = jsonObject.getBoolean(TAG_SUCESS);
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
				AlertDialog.Builder builder = new AlertDialog.Builder(
						DangNhap.this);
				builder.setTitle("Lỗi!");
				builder.setMessage("Kiểm tra kết nối mạng");
				builder.show();
				return;
			}
			updateUI();
		}
	}

}
