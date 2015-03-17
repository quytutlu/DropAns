package example.giaodien;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class DangKy extends Activity {

	EditText TenDangNhap, MatKhau, GoLaiMatKhau, SoDienThoai, Email;
	boolean flag = false;
	private static String TAG_SUCCESS = "success";
	private static String TAG_MESSAGE = "message";
	String Message;
	String urltemp = "http://svtl.net/service/svtl.ashx?cmd=register&username=";
	String url;
	ProgressDialog dialog;
	private String BatNhac;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dangky);
		Intent intent=getIntent();
		BatNhac=intent.getStringExtra("BatNhac");
		Init();
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(getApplicationContext(),
				example.giaodien.DangNhap.class);
		intent.putExtra("Kill", "false");
		intent.putExtra("flag", "false");
		intent.putExtra("LanDau","false");
		intent.putExtra("BatNhac", BatNhac);
		startActivity(intent);
	}

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
	
	private void Init() {
		TenDangNhap = (EditText) findViewById(R.id.tendangnhap);
		MatKhau = (EditText) findViewById(R.id.matkhau);
		GoLaiMatKhau = (EditText) findViewById(R.id.golaimatkhau);
		SoDienThoai = (EditText) findViewById(R.id.sodienthoai);
		Email = (EditText) findViewById(R.id.email);
		dialog = new ProgressDialog(this);
		if(BatNhac.equals("true")){
			MainActivity.player.start();
		}else{
			MainActivity.player.pause();
		}
	}

	private boolean KiemTra() {
		String TenDangNhapStr, EmailStr;
		TenDangNhapStr = TenDangNhap.getText().toString();
		char[] tendangnhap = TenDangNhapStr.toCharArray();
		if (TenDangNhap.getText().toString().equals("")
				|| MatKhau.getText().toString().equals("")
				|| GoLaiMatKhau.getText().toString().equals("")
				|| SoDienThoai.getText().toString().equals("")
				|| Email.getText().toString().equals("")) {
			Toast.makeText(getApplicationContext(),
					"Bạn phải điền đầy đủ thông tin", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		for (int i = 0; i < tendangnhap.length; i++) {
			if (tendangnhap[i] == ' ') {
				Toast.makeText(getApplicationContext(),
						"Tên đăng nhập không chứa dấu cách", Toast.LENGTH_SHORT)
						.show();
				TenDangNhap.setText("");
				return false;
			}
		}
		if (!MatKhau.getText().toString()
				.equals(GoLaiMatKhau.getText().toString())) {
			Toast.makeText(getApplicationContext(),
					"Mật khẩu không trùng nhau", Toast.LENGTH_SHORT).show();
			GoLaiMatKhau.setText("");
			return false;
		}
		if (!(SoDienThoai.getText().toString().length() == 10 || SoDienThoai
				.getText().toString().length() == 11)) {
			Toast.makeText(getApplicationContext(), "Số điện thoại không đúng",
					Toast.LENGTH_SHORT).show();
			SoDienThoai.setText("");
			return false;
		}
		EmailStr = Email.getText().toString();
		char[] email = EmailStr.toCharArray();
		boolean flag1 = false, flag2 = false;
		for (int i = 0; i < email.length; i++) {
			if (email[i] == '@') {
				flag1=true;
			}
			if (email[i] == '.') {
				flag2 = true;
			}
		}
		if (!(flag1 && flag2)) {
			Toast.makeText(getApplicationContext(), "Email không đúng",
					Toast.LENGTH_SHORT).show();
			Email.setText("");
			return false;
		}
		return true;
	}

	public void LamMoi() {
		TenDangNhap.setText("");
		MatKhau.setText("");
		GoLaiMatKhau.setText("");
		SoDienThoai.setText("");
		Email.setText("");
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dangky:
			if (KiemTra()) {
				url = urltemp;
				url += TenDangNhap.getText().toString();
				url += "&pass=";
				url += MatKhau.getText().toString();
				url += "&email=";
				url += Email.getText().toString();
				url += "&mobile=";
				url += SoDienThoai.getText().toString();
				// System.out.println(url);
				new ParseJSONTask().execute();
			}
			break;

		default:
			break;
		}
	}

	private void updateUI() {
		if (flag) {
			Toast.makeText(getApplicationContext(), "Đăng ký thành công",
					Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(getApplicationContext(),
					example.giaodien.DangNhap.class);
			intent.putExtra("Kill", "falase");
			intent.putExtra("flag", "true");
			intent.putExtra("User", TenDangNhap.getText().toString());
			intent.putExtra("Pass", MatKhau.getText().toString());
			intent.putExtra("LanDau","false");
			intent.putExtra("BatNhac", BatNhac);
			startActivity(intent);
		} else {
			if (Message.equals("DuplicateEmail")) {
				Toast.makeText(getApplicationContext(), "Email đã tồn tại",
						Toast.LENGTH_SHORT).show();
				Email.setText("");
			}
			if (Message.equals("DuplicateUserName")) {
				Toast.makeText(getApplicationContext(),
						"Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show();
				TenDangNhap.setText("");
			}
			if (Message.equals("InvalidPassword")) {
				Toast.makeText(getApplicationContext(), "Mật khẩu quá ngắn",
						Toast.LENGTH_SHORT).show();
				MatKhau.setText("");
				GoLaiMatKhau.setText("");
			}
			if (Message.equals("Unknown")) {
				Toast.makeText(getApplicationContext(),
						"Tài khoản không được chấp nhận", Toast.LENGTH_SHORT)
						.show();
				LamMoi();
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
			String jsonstr = webServiceHandler.getJSONData(url);
			if(jsonstr==null){
				return false;
			}
			try {
				JSONObject jsonObject = new JSONObject(jsonstr);
				flag = jsonObject.getBoolean(TAG_SUCCESS);
				Message = jsonObject.getString(TAG_MESSAGE);
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
				AlertDialog.Builder builder=new AlertDialog.Builder(DangKy.this);
				builder.setTitle("Lỗi!");
				builder.setMessage("Kiểm tra kết nối mạng");
				builder.show();
				return;
			}
			updateUI();
		}
	}

}