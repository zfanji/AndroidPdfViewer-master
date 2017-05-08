package com.github.barteksc.sample.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.github.barteksc.sample.R;

import java.util.ArrayList;
import java.util.List;

import utils.ImageDownloader;


public class FlipAdapter extends BaseAdapter implements OnClickListener {

	private ImageDownloader downloader;
	private int itemWidth;
	private int itemHeight;
	private int pdfIndex;

	public interface Callback{
		public void onPageRequested(int page);
	}
	
	static class Item {
		static long id = 0;
		
		long mId;
		
		public Item() {
			mId = id++;
		}
		
		long getId(){
			return mId;
		}
	}
	
	private LayoutInflater inflater;
	private Callback callback;

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
		this.notifyDataSetChanged();
	}

	private List<Item> items = new ArrayList<Item>();
	
	public FlipAdapter(Context context,int pdfindex,int width,int height) {
		this.downloader = new ImageDownloader();
		this.pdfIndex = pdfindex;
		this.itemWidth = width;
		this.itemHeight = height;
		inflater = LayoutInflater.from(context);
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return items.get(position).getId();
	}
	
	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.page, parent, false);
			
			holder.img_left = (ImageView) convertView.findViewById(R.id.imageview_left);
            holder.img_right = (ImageView) convertView.findViewById(R.id.imageview_right);

			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}



		downloader.download(holder.img_left, pdfIndex,itemWidth,itemHeight,position*2);
		downloader.download(holder.img_right, pdfIndex, itemWidth, itemHeight, position * 2 + 1);

		return convertView;
	}

	static class ViewHolder{
		ImageView img_left;
        ImageView img_right;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){

		}
	}

	public void addItems(int amount) {
		for(int i = 0 ; i<amount ; i++){
			items.add(new Item());
		}
		notifyDataSetChanged();
	}

	public void addItemsBefore(int amount) {
		for(int i = 0 ; i<amount ; i++){
			items.add(0, new Item());
		}
		notifyDataSetChanged();
	}

}
