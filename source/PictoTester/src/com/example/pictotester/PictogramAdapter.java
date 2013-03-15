package com.example.pictotester;
import dk.aau.cs.giraf.pictogram.Pictogram;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class PictogramAdapter extends BaseAdapter{
	
	private final Pictogram pictolol;
	private final Context context;
	
	public PictogramAdapter(Context context, Pictogram img) {
		super();
		this.pictolol = img;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		return null;
	}

}
