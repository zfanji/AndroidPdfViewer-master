package com.github.barteksc.sample.adapter;

/**
 * Created by Micheal on 2016/5/11.
 */

import android.content.Context;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.barteksc.sample.MyApplication;
import com.github.barteksc.sample.R;

import java.io.File;
import java.io.IOException;
import java.util.List;

import utils.CacheContainer;
import utils.ImageDownloader;

/**
 * 使用列表缓存过去的Item
 * @author hellogv
 *
 */
public class CacheAdapter extends BaseAdapter {
    private static final String TAG = "CacheAdapter";
    private final IUserView mUserView;
    private CacheContainer cacheContainer;

    private static MyApplication singleton;
    private ImageDownloader downloader;

    private Context mContext;
    private LayoutInflater inflater;
    private int mItemLayoutID;

    public int itemWidth = 120;
    public int itemHeight = 120;

    public CacheAdapter(Context c,int layoutID,int width,int height) {
        this.mContext = c;
        this.mUserView = (IUserView) c;
        this.downloader = new ImageDownloader();
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mItemLayoutID = layoutID;
        itemWidth = width;
        itemHeight = height;
        this.singleton = MyApplication.getInstance().getInstance();
        this.cacheContainer = new CacheContainer(mContext);

    }
    public void clearCache() {
        cacheContainer.clear();
    }

    @Override
    public int getCount() {
        return singleton.urls.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return singleton.urls.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public void setBooks(List<Book> books) {
        this.singleton.urls.clear();
        for(Book book:books){
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ParcelFileDescriptor mFileDescriptor=null;
                    PdfRenderer mPdfRenderer = book.getmPdfRenderer();
                    if(mPdfRenderer==null){
                        String url=book.getPath();
                        File openFile = new File(url);
                        if(openFile.exists()){
                            Log.d(TAG,"File.exists~~~");
                            mFileDescriptor = ParcelFileDescriptor.open(openFile, ParcelFileDescriptor.MODE_READ_ONLY);
                        }else {
                            Log.e(TAG,"File erro!!!!");
                            mFileDescriptor = singleton.getAssets().openFd("sample.pdf").getParcelFileDescriptor();
                        }
                        mPdfRenderer = new PdfRenderer(mFileDescriptor);
                        book.setmPdfRenderer(mPdfRenderer);

                    }

                    int countPage = mPdfRenderer.getPageCount();

                    if (null != mPdfRenderer) {
                        mPdfRenderer.close();
                    }
                    if (null != mFileDescriptor) {
                        mFileDescriptor.close();
                    }
                //    book.setmPdfRenderer(mPdfRenderer);
                    book.setPages(countPage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.singleton.urls.add(book);
        }
        notifyDataSetChanged();
    }


    private class ViewHolder {
        TextView id;
        TextView name;
        ImageView icon;
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
       // Log.d(TAG,"getView="+position);
        convertView = this.singleton.urls.get(position).getSaveView();
        if(convertView==null){
        //    Log.d(TAG,"convertView is null----> "+position);
            ViewHolder holder = new ViewHolder();

            convertView = inflater.inflate(mItemLayoutID, null);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.name.setText(singleton.urls.get(position).getName());


            holder.icon = (ImageView) convertView.findViewById(R.id.grdiViewimageView);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUserView.itemClick(position);
                }
            });

         //   Log.d(TAG,""+itemWidth+"--"+itemHeight);
            downloader.download(holder.icon, position,itemWidth,itemHeight,0);

            this.singleton.urls.get(position).setSaveView(convertView);
        }

        return convertView;
    }

    public void addItem(Book obj){
        Log.d(TAG,"add "+obj.getName());

    }

    public void removeItem(Book obj){
        Log.d(TAG,"remove "+obj.getName());

    }

    public void removeAllItem(){
        this.singleton.urls.clear();
    }
}