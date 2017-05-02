package com.github.barteksc.sample;

import android.app.Application;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.github.barteksc.sample.adapter.Book;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import utils.ContextHolder;
import utils.ImageAsyncTask;

public class MyApplication extends Application {

	public static final boolean ENCRYPTED = false;
	private static MyApplication singleton;

	protected DisplayMetrics metrics;
	protected int widthSize;
	protected int widthPixel;
	protected float pixelRate;
	public String directory;
	public LinkedList<Book> urls;
	public LinkedHashMap<String, ImageAsyncTask.BitmapDownloaderTask> taskCache;
	public LinkedHashMap<ImageView, ImageAsyncTask> imageTaskCache;//加载图片任务的请求列表


	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		singleton = this;
		initializeInstance();
	}

	public static MyApplication getInstance() {
		return singleton;
	}


	protected void initializeInstance() {
		metrics = getApplicationContext().getResources().getDisplayMetrics();
		pixelRate = (float) (metrics.densityDpi / 160.0);
		widthSize = (metrics.widthPixels * 160 / metrics.densityDpi - 24)/2;
		widthPixel = (int)(widthSize * pixelRate);

		directory = getFilesDir().getAbsolutePath();
		taskCache = new LinkedHashMap<String, ImageAsyncTask.BitmapDownloaderTask>();
		imageTaskCache = new LinkedHashMap<ImageView, ImageAsyncTask>();
		urls = new LinkedList<Book>();

		File file = new File(directory);
		if(!file.exists()) {
			file.mkdir();
		}
	}
	// This method replaces url to filename.
	// Url has special characters, so We have to change these characters
	// to save bitmap file into device cache directory.
//
	public String keyToFilename(String key) {
		String filename = key.replace(":", "_");
		filename = filename.replace("/", "_s_");
		filename = filename.replace("\\", "_bs_");
		filename = filename.replace("&", "_bs_");
		filename = filename.replace("*", "_start_");
		filename = filename.replace("?", "_q_");
		filename = filename.replace("|", "_or_");
		filename = filename.replace(">", "_gt_");
		filename = filename.replace("<", "_lt_");
		return filename;
	}
}
