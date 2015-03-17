package example.giaodien;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.Window;

@SuppressLint("HandlerLeak")
public class Sai extends Activity {
	Handler handler;
	@SuppressWarnings("unused")
	private String LanDau;
	private int CauHoi;
	@SuppressWarnings("unused")
	private int playdetailid;
	@SuppressWarnings("unused")
	private String TraLoiDung;
	private int SunTime;
	private String User;
	private String playid;
	private String BatNhac;
	private MediaPlayer player;
	private String sid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sai);
		player=new MediaPlayer();
		Intent intent = getIntent();
		LanDau = intent.getStringExtra("LanDau");
		CauHoi = Integer.parseInt(intent.getStringExtra("CauHoi"));
		playdetailid = Integer.parseInt(intent.getStringExtra("playdetailid"));
		TraLoiDung = intent.getStringExtra("TraLoiDung");
		SunTime = Integer.parseInt(intent.getStringExtra("SunTime"));
		User = intent.getStringExtra("UserName");
		playid = intent.getStringExtra("Playid");
		BatNhac = intent.getStringExtra("BatNhac");
		sid=intent.getStringExtra("SUBJECTID");
		if(BatNhac.equals("true")){
			InsertMusic();
		}
		TaoTuyen();
	}

	public void Chuyen() {
		Intent temp = new Intent(getApplicationContext(),
				example.giaodien.KetThuc.class);
		temp.putExtra("Diem", (CauHoi - 1) + "");
		temp.putExtra("SumTime", SunTime + "");
		temp.putExtra("UserName", User);
		temp.putExtra("Playid", playid + "");
		temp.putExtra("BatNhac", BatNhac);
		temp.putExtra("SUBJECTID", sid);
		startActivity(temp);
		player.stop();
		player = null;
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

	private void InsertMusic() {
		AssetFileDescriptor afd;
		try {
			afd = getAssets().openFd("tinhieusai.mp3");
			player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
					afd.getLength());
			player.prepare();
			player.start();
			if(BatNhac.equals("flase")){
				player.pause();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void TaoTuyen() {
		handler = new Handler() {
			public void handleMessage(Message msg) {
				if(msg.arg1==1){
					if(BatNhac.equals("true")){
						MainActivity.player.start();
					}
					finish();
				}
				if (msg.arg1 == 0) {
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
				for (int i = 1; i >= 0; i--) {
					if(i==1){
						SystemClock.sleep(1000);
					}else{
						SystemClock.sleep(2000);
					}
					Message msg = handler.obtainMessage();
					msg.arg1 = i;
					handler.sendMessage(msg);
				}
				return;
			}
		});
		th.start();
	}
}
