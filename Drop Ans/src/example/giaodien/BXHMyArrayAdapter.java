package example.giaodien;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.Object.DiemCao;

public class BXHMyArrayAdapter extends ArrayAdapter<DiemCao> {

	private Context context;
	@SuppressWarnings("unused")
	private int layoutId;
	ArrayList<DiemCao> arrDiemCaos;

	public BXHMyArrayAdapter(Context context, int layoutId,
			ArrayList<DiemCao> arrDiemCaos) {
		super(context, layoutId, arrDiemCaos);
		this.context = context;
		this.layoutId = layoutId;
		this.arrDiemCaos = arrDiemCaos;

	}

	@SuppressLint("ViewHolder") @Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.mylistviewbxh, parent, false);
		
		TextView tvThoiDiem = (TextView) rowView.findViewById(R.id.tvThoiDiem);
		TextView tvDiem = (TextView) rowView.findViewById(R.id.tvDiem);
		
		tvThoiDiem.setText(arrDiemCaos.get(position).getThoiDiem());
		tvDiem.setText(arrDiemCaos.get(position).getDiem()+" điểm");

		return rowView;
	}
}
