package example.giaodien;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

@SuppressLint("HandlerLeak")
public class MainActivity extends ActionBarActivity {

	ProgressBar bar;
	Handler handler;
	AtomicBoolean isrunning = new AtomicBoolean(false);
	public static MediaPlayer player;
	private DBAdapter myDb;
	private String status;
	private String _acc;
	private String BatNhac="true";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		Intent intent = getIntent();
		String kill = intent.getStringExtra("KT");
		if (kill != null) {
			finish();
			return;
		}
		openDB();
		DocFile();
		InsertMusic();
		GetAccount();
		TaoTuyen();
	}
	
	@Override
	public void onBackPressed() {
	}

	private void DocFile() {
		try {
			FileInputStream in = openFileInput("CauHinh.txt");
			byte[] buffer = new byte[in.available()];
			in.read(buffer);
			BatNhac = new String(buffer);
			if(BatNhac==null){
				BatNhac="true";
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		player.pause();
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	public void onClick(View v) {
		Chuyen();
	}

	private void GetAccount() {
		Cursor cursor = myDb.getRow(1);
		if (!cursor.moveToFirst()) {
			status = "false";
			return;
		}
		status = cursor.getString(DBAdapter.COL_STATUS);
		_acc = cursor.getString(DBAdapter.COL_ACC);
		myDb.close();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void openDB() {
		myDb = new DBAdapter(this);
		myDb.open();
	}

	private void InsertMusic() {
		AssetFileDescriptor afd;
		player = new MediaPlayer();
		try {
			afd = getAssets().openFd("nhacnen.mp3");

			player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
					afd.getLength());
			player.setLooping(true);
			player.prepare();
			player.start();
			if(BatNhac.equals("false")){
				player.pause();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void Chuyen() {
		if (status.equals("true")) {
			Intent intent = new Intent(MainActivity.this,
					example.giaodien.Menu.class);
			intent.putExtra("UserName", _acc);
			intent.putExtra("BatNhac", BatNhac);
			startActivity(intent);
		} 
		else if (status.equals("false") || status == null) {
			Intent intent = new Intent(this, example.giaodien.DangNhap.class);
			intent.putExtra("Kill", "false");
			intent.putExtra("flag", "flase");
			intent.putExtra("LanDau", "false");
			intent.putExtra("BatNhac", BatNhac);
			startActivity(intent);
		}
	}

	public void TaoTuyen() {
		handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.arg1 == 100) {
					Chuyen();
				}
			}
		};
		doStart();
	}

	public void doStart() {
		final Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i <= 100; i++) {
					SystemClock.sleep(25);
					Message msg = handler.obtainMessage();
					msg.arg1 = i;
					handler.sendMessage(msg);
				}
				return;
			}
		});
		isrunning.set(true);
		th.start();
	}

}