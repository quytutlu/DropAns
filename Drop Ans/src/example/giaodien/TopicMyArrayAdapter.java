package example.giaodien;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.Object.Topic;

@SuppressLint("ViewHolder") public class TopicMyArrayAdapter extends ArrayAdapter<Topic> {

	private Context context;
	@SuppressWarnings("unused")
	private int layoutId;
	ArrayList<Topic> arrTopics;

	public TopicMyArrayAdapter(Context context, int layoutId,ArrayList<Topic> arrTopics) {
		super(context, layoutId, arrTopics);
		this.context = context;
		this.layoutId = layoutId;
		this.arrTopics = arrTopics;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.mylistviewtopic, parent, false);
		ImageView imageview = (ImageView) rowView.findViewById(R.id.iv_logo);
		Typeface typeface=Typeface.createFromAsset(context.getAssets(), "font/topic.TTF");
		TextView tvTitle = (TextView) rowView.findViewById(R.id.tvTitle);
		tvTitle.setTypeface(typeface);
		
		imageview.setImageResource(R.drawable.tlu);
		new LoadImage(imageview).execute(arrTopics.get(position).getUrlicon());
		//new LoadImage(imageview).execute("http://www.google.com/ig/images/weather/rain.gif");
		tvTitle.setText(arrTopics.get(position).getTitle());

		return rowView;
	}

	private class LoadImage extends AsyncTask<String, String, Bitmap> {
		ImageView iv;

		public LoadImage(ImageView iv) {
			this.iv = iv;
		}

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
				iv.setImageBitmap(image);
			}else{
				iv.setImageResource(R.drawable.tlu);
			}
		}
	}

}
