package com.github.barteksc.sample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.GridView;

import com.github.barteksc.sample.adapter.Book;
import com.github.barteksc.sample.adapter.CacheAdapter;
import com.github.barteksc.sample.adapter.IUserView;

import java.io.File;
import java.util.ArrayList;

import utils.FileUtil;


public class ManageBookActivity extends Activity implements View.OnClickListener,IUserView {
	private static final String TAG = "ManageBookActivity";
	private static final boolean RANDOM_SIGNTIMES = false;
	private static final String BOOK_PATH = "/system/media/ebooks";
	private static final String BOOK_PATH_DEBUG = "/sdcard/ebooks";
	private Context mContext;


	private GridView gridView;
	private CacheAdapter mCacheAdapter;
	private ProgressDialog mProgressDialog;

	private String mCapturePath;

	private int mCountUser=0;

	private File imgFile;
	private ProgressDialog pd1;

	private final static int REQUEST_CODE = 42;
	public static final int PERMISSION_CODE = 42042;
	public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		hideBottomUIMenu();
		setContentView(R.layout.activity_managebook);
		mContext = this;

		initView();
		setListener();

		int permissionCheck = ContextCompat.checkSelfPermission(this,
				READ_EXTERNAL_STORAGE);

		if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(
					this,
					new String[]{READ_EXTERNAL_STORAGE},
					PERMISSION_CODE
			);

			return;
		}

		setBookData();
	}
	/**
	 * 隐藏虚拟按键，并且全屏
	 */
	protected void hideBottomUIMenu() {
		//隐藏虚拟按键，并且全屏
		if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
			View v = this.getWindow().getDecorView();
			v.setSystemUiVisibility(View.GONE);
		} else if (Build.VERSION.SDK_INT >= 19) {
			//for new api versions.
			View decorView = getWindow().getDecorView();
			int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
			decorView.setSystemUiVisibility(uiOptions);
		}
	}
	/**
	 * Listener for response to user permission request
	 *
	 * @param requestCode  Check that permission request code matches
	 * @param permissions  Permissions that requested
	 * @param grantResults Whether permissions granted
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
										   @NonNull int[] grantResults) {
		if (requestCode == PERMISSION_CODE) {
			if (grantResults.length > 0
					&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				setBookData();
			}
		}
	}

	private void setBookData() {
		ArrayList<File> imgFileList= FileUtil.GetPdfFiles(BOOK_PATH);
		if(imgFileList.isEmpty()){
			imgFileList= FileUtil.GetPdfFiles(BOOK_PATH_DEBUG);
		}
		ArrayList<Book> books=new ArrayList<>();
		for (File file:imgFileList){
			Log.d(TAG,"book path="+file.getAbsolutePath());
			Book book=new Book();
			book.setName(file.getName());
			book.setPath(file.getAbsolutePath());
			books.add(book);
		}
		mCacheAdapter.setBooks(books);
		gridView.setAdapter(mCacheAdapter);
	}


	private void setListener() {

	}

	private void initView() {

		gridView = (GridView)findViewById(R.id.gridView);
		DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
		int height=metrics.heightPixels*38/100;
		mCacheAdapter = new CacheAdapter(mContext,R.layout.activity_gridview_item,height*3/4,height);
		//mCacheAdapter.setBooks();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void itemClick(int index) {
		Log.d(TAG,"itemClick "+index);
		Intent intent = new Intent();
//		intent.setClass(mContext, PDFViewActivity.class);
		intent.setClass(mContext, PdfActivity.class);
		intent.putExtra("pdfindex",index);
		intent.putExtra("path",MyApplication.getInstance().urls.get(index).getPath());
		mContext.startActivity(intent);
	}


	@Override
	public void onClick(View view) {

	}
}
