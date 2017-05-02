package utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtil {
	public static Bitmap createImageThumbnail(String imagePath, int width,
			int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false;
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}
	public static void saveBitmap(String savePath, Bitmap bitmap) {
		if (null == bitmap)// 容错处理
			return;
		File f = new File(savePath);
		FileOutputStream out = null;
		try {
			f.createNewFile();
			out = new FileOutputStream(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static Bitmap drawableToBitmap(Drawable drawable) {



		Bitmap bitmap = Bitmap.createBitmap(

				drawable.getIntrinsicWidth(),

				drawable.getIntrinsicHeight(),

				drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

						: Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmap);

		//canvas.setBitmap(bitmap);

		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

		drawable.draw(canvas);

		return bitmap;

	}



}
