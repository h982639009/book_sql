package com.example.books;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class GridViewAdapter extends CommonAdapter<String> {

	public GridViewAdapter(Context context, List<String> list, int layoutId) {
		super(context, list, layoutId);
	}

	@Override
	public void convert(CommonViewHolder holder, String data) {
		// TODO Auto-generated method stub
		TextView textView=holder.getViewById(R.id.item_gv_txt);
		textView.setText(data);
//		EditText editText=holder.getViewById(R.id.item_gv_ed);
//		editText.setText(data);
	}

	

}
