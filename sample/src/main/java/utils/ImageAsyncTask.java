package utils;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.pdf.PdfRenderer;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.ImageView;


import com.github.barteksc.sample.MyApplication;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

// This class actually downloads bitmaps.

public class ImageAsyncTask {
	private static final String TAG = "ImageAsyncTask";
	private MyApplication singleton;
	private BitmapDownloaderTask task;
	private String imgPath, fileName;
	
	public ImageAsyncTask() {
		this.singleton = MyApplication.getInstance();
	}
	
	public void download(ImageView imageView, int position,int width,int height,int witchPage) {
		
		// taskCache saves task related url.
		// If task is not null, remove the task and create new task. 
		// After then start downloading bitmap image.

		imgPath = ""+singleton.urls.get(position).getPath()+witchPage;
		task = singleton.taskCache.get(imgPath);
		fileName = singleton.keyToFilename(imgPath);
		Log.d(TAG,"文件名: "+fileName);
		if(task != null) {
			task.cancel(true);
			singleton.taskCache.remove(imgPath);
		}
		
		this.task = new BitmapDownloaderTask(imageView,width,height);
		task.execute(position,witchPage);
		singleton.taskCache.put(imgPath, task);
	}
	
	public void cancel(boolean cancel) {	
		task.cancel(cancel);
		singleton.taskCache.remove(imgPath);
	}

	public boolean isCancelled() {
		if(task != null) {
			return task.isCancelled();
		}
		
		return true;
	}
	
  public class BitmapDownloaderTask extends AsyncTask<Integer, Integer, Bitmap> {
    private int index;
	private int witchpage;
	private int mWidth = 360;
	private int mHeight = 240;
    private final WeakReference<ImageView> imageViewReference;
	  private String url;


	  public BitmapDownloaderTask(ImageView imageView,int width,int height) {
		mWidth = width;
		mHeight = height;
        imageViewReference = new WeakReference<ImageView>(imageView);
    }

    @Override
    protected Bitmap doInBackground(Integer... params) {
        index = (Integer)params[0];
		witchpage = (Integer)params[1];
        // Before downloading a bitmap, find a bitmap using url in the memory Cache.
        // If you can't find int the memory Cache, find it in the disk Cache.
        // If you can't find in the disk Cache, then you really download.
		url = singleton.urls.get(index).getPath()+witchpage;
      	if(CacheContainer.getMemory(url) != null) {
     // 		return (Bitmap) CacheContainer.getMemory(url);
      	}

      	if(CacheContainer.getDisk(fileName) != null) {
     		return (Bitmap) CacheContainer.getDisk(fileName);
      	}

        return getBitmap(index,mWidth,mHeight,witchpage);
    }

		@Override
    protected void onProgressUpdate(Integer... progress) {
    }

		@Override
    protected void onPostExecute(Bitmap bitmap) {
			if(bitmap != null) {
				ImageView imageView = imageViewReference.get();
				imageView.setImageBitmap(bitmap);

				// Save the bitmap into memory and disk cache.
/*
				synchronized (CacheContainer.getMemoryCache()) {
			     if (CacheContainer.getMemory(url) == null) {
			    	 Log.d(TAG, "Here is Memory Put");
			    	 CacheContainer.putMemory(url, bitmap);
			     }			     
				}
	*/
				synchronized (CacheContainer.getDiskCache()) {
			     if (CacheContainer.getDisk(fileName) == null) {
			    	 Log.d(TAG, "Here is Disk Put");
			    	 CacheContainer.putDisk(url, singleton.directory + "/" + fileName);
			     }
				}
				
			}
    }
  }

	/**
	 * 添加倒影
	 * @param originalImage
	 * @return
	 */
	public static Bitmap createReflectedImage(Bitmap originalImage) {

		final int reflectionGap = 4;

		int width = originalImage.getWidth();
		int height = originalImage.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
				height / 2, width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);

		canvas.drawBitmap(originalImage, 0, 0, null);

		Paint defaultPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0,
				originalImage.getHeight(), 0, bitmapWithReflection.getHeight()
				+ reflectionGap, 0x70ffffff, 0x00ffffff,
				Shader.TileMode.MIRROR);

		paint.setShader(shader);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		return bitmapWithReflection;
	}

//	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
//	public Bitmap getBitmap(Integer index, int width, int height,int witch) {
//		String url=singleton.urls.get(index).getPath();
//		Log.w(TAG,"url="+url+"  getBitmap~~~~~~"+index);
//		System.setProperty("http.keepAlive", "false");
//		try {
//
//			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//				ParcelFileDescriptor mFileDescriptor=null;
//				PdfRenderer mPdfRenderer = null;//singleton.urls.get(index).getmPdfRenderer();
//				if(mPdfRenderer==null){
//					Log.w(TAG,"mPdfRenderer~~~~is null");
//
//					File openFile = new File(url);
//					if(openFile.exists()){
//						Log.w(TAG,"File.exists~~~");
//						mFileDescriptor = ParcelFileDescriptor.open(openFile, ParcelFileDescriptor.MODE_READ_ONLY);
//					}else {
//						Log.w(TAG,"File erro!!!!");
//						mFileDescriptor = singleton.getAssets().openFd("sample.pdf").getParcelFileDescriptor();
//					}
//					mPdfRenderer = new PdfRenderer(mFileDescriptor);
//					//singleton.urls.get(index).setmPdfRenderer(mPdfRenderer);
//				}
//
//				int countPage = mPdfRenderer.getPageCount();
//				Log.d(TAG,"countPage="+countPage+" witch="+witch);
//				if(witch>=countPage)
//					witch = countPage-1;
//
//				singleton.urls.get(index).setPages(countPage);
//				PdfRenderer.Page mCurrentPage = mPdfRenderer.openPage(witch);
//				//Bitmap必须是ARGB，不可以是RGB
//				Bitmap bitmap = Bitmap.createBitmap(width, height,
//						Bitmap.Config.ARGB_8888);
//				bitmap.eraseColor(0);
//
//				mCurrentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
//
//				if (null != mCurrentPage) {
//					mCurrentPage.close();
//				}
//				if (null != mPdfRenderer) {
//					mPdfRenderer.close();
//				}
//				if (null != mFileDescriptor) {
//					mFileDescriptor.close();
//				}
//
//				if(bitmap==null){
//					Log.e(TAG,"bitmpa is null!!!!");
//					return bitmap;
//				}
//
//
//				InputStream is = Bitmap2IS(bitmap);
//				BufferedInputStream bis = new BufferedInputStream(is);
//
//				File file = new File(singleton.directory + "/" + fileName);
//				OutputStream out = null;
//				try {
//					out = new FileOutputStream(file);
//					try {
//						byte[] buffer = new byte[1024 * 1024];
//						int bytesRead = 0;
//						while ((bytesRead = bis.read(buffer, 0, buffer.length)) >= 0) {
//							out.write(buffer, 0, bytesRead);
//						}
//					} finally {
//						out.close();
//					}
//				}
//				catch (FileNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//				bis.close();
//				is.close();
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return showImage(singleton.directory + "/" + fileName);
//	}


	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public Bitmap getBitmap(Integer index, int width, int height,int pageNum) {
		String url=singleton.urls.get(index).getPath();
		ParcelFileDescriptor fd = null;
		File openFile = new File(url);
		if(openFile.exists()){
			Log.w(TAG,"File.exists~~~");
			try {
				fd = ParcelFileDescriptor.open(openFile, ParcelFileDescriptor.MODE_READ_ONLY);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}else {
			Log.e(TAG,"File erro!!!!");
			return null;
		}

		PdfiumCore pdfiumCore = new PdfiumCore(singleton.getApplicationContext());
		try {
			PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
			if(pageNum>=singleton.urls.get(index).getPages()){
				return null;
			}
			if(pageNum<0)
				pageNum=0;

			pdfiumCore.openPage(pdfDocument, pageNum);

			//int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNum);
			//int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNum);

			Bitmap bitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			pdfiumCore.renderPageBitmap(pdfDocument, bitmap, pageNum, 0, 0,
					width, height);

			if(fd!=null){
				fd.close();
			}

			InputStream is = Bitmap2IS(bitmap);
			BufferedInputStream bis = new BufferedInputStream(is);

			File file = new File(singleton.directory + "/" + fileName);
			OutputStream out = null;
			try {
				out = new FileOutputStream(file);
				try {
					byte[] buffer = new byte[1024 * 1024];
					int bytesRead = 0;
					while ((bytesRead = bis.read(buffer, 0, buffer.length)) >= 0) {
						out.write(buffer, 0, bytesRead);
					}
				} finally {
					out.close();
				}
			}
			catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			bis.close();
			is.close();

			//printInfo(pdfiumCore, pdfDocument);

			pdfiumCore.closeDocument(pdfDocument); // important!
		} catch(IOException ex) {
			ex.printStackTrace();
		}
		return showImage(singleton.directory + "/" + fileName);
	}
	/**
	 * 获取视频缩略图
	 * @param videoPath
	 * @param width
	 * @param height
	 * @param kind
	 * @return
	 */
	private Bitmap getVideoThumbnail(String videoPath, int width , int height, int kind){
		Bitmap bitmap = null;
		Log.d(TAG,"videoPath="+videoPath+" width="+width+" height="+height);
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	private InputStream Bitmap2IS(Bitmap bm){
		//Log.d(TAG,"转前bm="+bm.getHeight());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		InputStream sbs = new ByteArrayInputStream(baos.toByteArray());
		return sbs;
	}
  public static Bitmap showImage(String dir) {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inInputShareable = true;
    options.inDither=false;
	options.inPreferredConfig = Bitmap.Config.RGB_565;
	options.inTempStorage=new byte[32 * 1024];
    options.inPurgeable = true;
    options.inJustDecodeBounds = false;
    options.inSampleSize = 1;//不要缩放
    
    File file = new File(dir);
    FileInputStream fs=null;
    try {
        fs = new FileInputStream(file);
    } catch (FileNotFoundException e) {
        //TODO do something intelligent
        e.printStackTrace();
    }
    
    Bitmap bm = null;

    try {
        if(fs!=null) bm= BitmapFactory.decodeFileDescriptor(fs.getFD(), null, options);
    } catch (IOException e) {
        //TODO do something intelligent
        e.printStackTrace();
    } finally{ 
        if(fs!=null) {
            try {
                fs.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
	 // Log.d(TAG,"转后bm="+bm.getHeight());
    return bm;
  }    
    
}
