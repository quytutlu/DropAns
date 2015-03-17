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

@SuppressLint("HandlerLeak") public class Dung extends Activity {
	Handler handler;
	@SuppressWarnings("unused")
	private String LanDau;
	private int CauHoi;
	private int playdetailid;
	@SuppressWarnings("unused")
	private String TraLoiDung;
	private int SunTime;
	private String User;
	private String playid;
	private String ChoiThu;
	private MediaPlayer player;
	private String BatNhac;
	private String sid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dung);
		player=new MediaPlayer();
		Intent intent = getIntent();
		LanDau = intent.getStringExtra("LanDau");
		CauHoi = Integer.parseInt(intent.getStringExtra("CauHoi"));
		playdetailid = Integer.parseInt(intent.getStringExtra("playdetailid"));
		TraLoiDung = intent.getStringExtra("TraLoiDung");
		SunTime = Integer.parseInt(intent.getStringExtra("SunTime"));
		User = intent.getStringExtra("UserName");
		playid = intent.getStringExtra("Playid");
		ChoiThu = intent.getStringExtra("ChoiThu");
		BatNhac=intent.getStringExtra("BatNhac");
		sid=intent.getStringExtra("SUBJECTID");
		if(BatNhac.equals("true")){
			InsertMusic();
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
		if(BatNhac.equals("true")){
			MainActivity.player.start();
		}
	}

	public void Chuyen() {
		Intent intent;
		if(ChoiThu.equals("true")){
			intent= new Intent(this, ChoiThu.class);
		}else{
			 intent= new Intent(this, BatDau.class);
			 intent.putExtra("SUBJECTID", sid);
		}
		intent.putExtra("Disable", "false");
		intent.putExtra("LanDau", "false");
		intent.putExtra("CauHoi",CauHoi+"");
		intent.putExtra("playdetailid",playdetailid+"");
		intent.putExtra("TraLoiDung","true");
		intent.putExtra("SunTime",SunTime+"");
		intent.putExtra("UserName",User);
		intent.putExtra("Playid",playid);
		intent.putExtra("BatNhac", BatNhac);
		startActivity(intent);
		player.stop();
		player = null;
	}

	@SuppressLint("HandlerLeak") private void InsertMusic() {
		AssetFileDescriptor afd;
		try {
			afd = getAssets().openFd("Tiengvotay.mp3");
			player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
					afd.getLength());
			player.prepare();
			player.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void TaoTuyen() {
		handler = new Handler() {
			public void handleMessage(Message msg) {
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
					SystemClock.sleep(800);
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
