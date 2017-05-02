package utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class FileUtil {
	public static final String MODEL_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
	public static final String ATTRIBUTE_MODEL_NAME = "attribute.model";
	public static final String VERIFY_MODEL_NAME = "verify.model";
	public static final String VERIFY_DB_NAME = "verify_faces.db";


	public static final String MODEL_FOLDERNAME = "";
	public static final String MODEL_ATTRIBUTE_PATH = MODEL_ROOT +"/"+ MODEL_FOLDERNAME +"/" +ATTRIBUTE_MODEL_NAME;
	public static final String MODEL_VERIFY_PATH = MODEL_ROOT +"/"+ MODEL_FOLDERNAME +"/" +VERIFY_MODEL_NAME;
	public static final String VERIFY_DB_PATH = MODEL_ROOT +"/"+ MODEL_FOLDERNAME +"/" +VERIFY_DB_NAME;

	public static final String ASSERT_PATH = "testpictures";
	public static final FileComparator sFileComparator = new FileComparator();
	public static final int THUMB_WIDTH = 400;
	public static final int THUMB_HEIGHT = 300;
	public static final String IMG_NAME = "img_path";
	public static final String THUMB_NAME = "thumb_path";


	/*
	 *  read all sourceimg time. like this  结果like this:
	 *  ############################################################
        # SenseTime License
        # License Product: FaceSdk
        # Expiration: 20000101~20990101
        # License SN: 89c1ad09-5d79-4d14-b08d-f4141ae653b9
        ############################################################
        sGfdd5sxA8NCweDGA+vU2qzOgOjTn64wtsOftvbVw+MzmQXlL9gGE+JsM0nU
        ........................

              由于cv_face_init_license_config(license)函数在处理license串时以\n为分隔符，所以保留。
	 */
	public static String readFileFromSDCard2(Context context, File path,String filename) {
		String result = null;
		try {
			File file = new File(path, filename);
			if (!file.exists()) {
				//QueryErrorCode.showToast(context, null, filename + "文件不存在");
				return null;
			}
			FileInputStream inputStream = new FileInputStream(file);
			byte[] b = new byte[inputStream.available()];
			inputStream.read(b);
			result = new String(b);
			Log.i(TAG, "readFileFromSDCard2");
			Log.i(TAG, result);
		} catch (Exception e) {
			//Toast.makeText(context, "读取失败", Toast.LENGTH_SHORT).show();
		}
		return result;
	}

	public static String readFileFromAssets(Context context, String fileName) {
		String res = "";
		try {
			InputStream in = context.getResources().getAssets().open(fileName);
			int length = in.available();
			byte[] buffer = new byte[length];

			in.read(buffer);
			in.close();
			res = new String(buffer,"UTF-8");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * 复制单个文件
	 * @param oldPath String 原文件路径 如：c:/fqf.txt
	 * @param newPath String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 */
	public static void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { //文件存在时
				InputStream inStream = new FileInputStream(oldPath); //读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ( (byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; //字节数 文件大小
				//	System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		}
		catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}

	}

	/**
	 * 复制整个文件夹内容
	 * @param oldPath String 原文件路径 如：c:/fqf
	 * @param newPath String 复制后路径 如：f:/fqf/ff
	 * @return boolean
	 */
	public static void copyFolder(String oldPath, String newPath) {

		try {
			(new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
			File a=new File(oldPath);
			String[] file=a.list();
			File temp=null;
			for (int i = 0; i < file.length; i++) {
				if(oldPath.endsWith(File.separator)){
					temp=new File(oldPath+file[i]);
				}
				else{
					temp=new File(oldPath+File.separator+file[i]);
				}

				if(temp.isFile()){
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath + "/" +
							(temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ( (len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if(temp.isDirectory()){//如果是子文件夹
					copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]);
				}
			}
		}
		catch (Exception e) {
			System.out.println("复制整个文件夹内容操作出错");
			e.printStackTrace();

		}

	}

	public static void copyVerifyModelToSDCard()
	{
		File file = new File(MODEL_ROOT);
		if(file.exists() && file.isDirectory())
		{
			File modelFile = new File(MODEL_VERIFY_PATH);
			Log.d(TAG,"copyVerifyModelToSDCard modelFile="+modelFile.getPath());
			if(modelFile.exists() && modelFile.isFile())
			{
				Log.w(TAG,"copyVerifyModelToSDCard file is exist");
				return;
			}
		}
		file.mkdirs();
		try {
			copyBigDataToSDCard(VERIFY_MODEL_NAME,MODEL_VERIFY_PATH);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static void copyAttributeModelToSDCard()
	{
		File file = new File(MODEL_ROOT);
		if(file.exists() && file.isDirectory())
		{
			File modelFile = new File(MODEL_ATTRIBUTE_PATH);
			Log.d(TAG,"copyAttributeModelToSDCard modelFile="+modelFile.getPath());
			if(modelFile.exists() && modelFile.isFile())
			{
				Log.w(TAG,"copyAttributeModelToSDCard file is exist");
				return;
			}
		}
		file.mkdirs();
		try {
			copyBigDataToSDCard(ATTRIBUTE_MODEL_NAME,MODEL_ATTRIBUTE_PATH);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public static void copyBigDataToSDCard(String sourseName,String strOutFileName) throws IOException {  
        InputStream myInput;  
        OutputStream myOutput = new FileOutputStream(strOutFileName);  
        myInput = ContextHolder.getContext().getAssets().open(sourseName);  
        byte[] buffer = new byte[1024];  
        int length = myInput.read(buffer);
        while(length > 0){
            myOutput.write(buffer, 0, length); 
            length = myInput.read(buffer);
        }
        
        myOutput.flush();  
        myInput.close();  
        myOutput.close();        
    }
	public static final void clearImageStore(){
		clearFiles(StorageUtil.getPictureStorageRootPath());
	}
	public static void clearFiles(String path){//删除/storage/emulated/0/DCIM/Examples_facegroup下的image和thumb
		File file = new File(path);
		if(!file.exists() || !file.isDirectory()){
			return;
		}
		File[] fileList = file.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			File delfile = fileList[i];
			if (!delfile.isDirectory()) {
				delfile.delete();
			} else if (delfile.isDirectory()) {
				clearFiles(fileList[i].getPath());
			}
		}
	}
	public static void copyFilesToLocalIfNeed(boolean delete) {
		File pictureDir = new File(StorageUtil.getPictureStoragePath());//image
		if (!pictureDir.exists() || !pictureDir.isDirectory()) {
			pictureDir.mkdirs();
		}
		try {
			String[] fileNames = ContextHolder.getContext().getAssets()
					.list(ASSERT_PATH);

			if (fileNames.length == 0)
				return;
			for (int i = 0; i < fileNames.length; i++) {
				File file = new File(StorageUtil.IMG_PATH_ABS + fileNames[i]);
				if (file.exists() && file.isFile()){
					file.delete();
				}
				InputStream is = ContextHolder.getContext().getAssets()
						.open(ASSERT_PATH + File.separator + fileNames[i]);//核心代码
				int size = is.available();
				byte[] buffer = new byte[size]; 
												
				is.read(buffer); 
				is.close(); 
				String mypath = StorageUtil.IMG_PATH_ABS + fileNames[i];
				FileOutputStream fop = new FileOutputStream(mypath);//核心代码
				fop.write(buffer);
				fop.flush();
				fop.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static List<HashMap<String, String>> getThumbImage(String imgPath, String thumbPath) {
		List<HashMap<String, String>> mapList = new ArrayList<HashMap<String,String>>();
		if (FileUtil.initDir(imgPath) && FileUtil.initDir(thumbPath)) {
			List<String> srcImg = FileUtil.loadImages(imgPath);
			if(null == srcImg){
				return null;
			}
			for (String p : srcImg) {
				if(null == p)//容错处理
					return null;
				String name = FileUtil.getFileName(p);
				String thumb = thumbPath + name;
				if (!FileUtil.existFile(thumb)) {
					Bitmap bitmap = BitmapUtil.createImageThumbnail(p, THUMB_WIDTH, THUMB_HEIGHT);//核心代码    创建缩略图
					//
					BitmapUtil.saveBitmap(thumb, bitmap);//保存图片到thumb里
				}
				//if (!p.contains(Util.prefix)) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put(IMG_NAME, p);
					map.put(THUMB_NAME, thumb);
					mapList.add(map);
				//}
			}
		}
		return mapList;
	}


	public static String saveImage(String srcImg, String thumbPath) {
		String thumb = null;
		if(FileUtil.initDir(thumbPath)){
			if(null == srcImg){
				return null;
			}

			if(null == srcImg)//容错处理
				return null;
			String name = FileUtil.getFileName(srcImg);
			thumb = thumbPath + name;
			if (!FileUtil.existFile(thumb)) {
				Bitmap bitmap = PhotoUtil.getScaledPic(srcImg,400,300);
				BitmapUtil.saveBitmap(thumb, bitmap);//保存图片到thumb里
			}
		}

		return thumb;
	}

    public static String saveThumbImage(String srcImg, String thumbPath) {
        String thumb = null;
        if(FileUtil.initDir(thumbPath)){
            if(null == srcImg){
                return null;
            }

            if(null == srcImg)//容错处理
                return null;
            String name = FileUtil.getFileName(srcImg);
            thumb = thumbPath + name;
            if (!FileUtil.existFile(thumb)) {
                Bitmap bitmap = BitmapUtil.createImageThumbnail(srcImg, THUMB_WIDTH, THUMB_HEIGHT);//核心代码    创建缩略图
                //
                BitmapUtil.saveBitmap(thumb, bitmap);//保存图片到thumb里
            }
        }

        return thumb;
    }

	public static boolean initDir(String path) {
		File dirFile = new File(path);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return dirFile.mkdirs();
		}
		return true;
	}
	public static List<String> loadImages(String path) {
		List<String> imgList = new ArrayList<String>();
		File file = new File(path);//此file是文件夹
		if (!file.exists()) {
			return imgList;
		}
		File[] files = file.listFiles();
		List<File> allFiles = new ArrayList<File>();
		for (File img : files) {
			allFiles.add(img);
		}
		Collections.sort(allFiles,sFileComparator);
		for (File img : allFiles) {
			imgList.add(img.getAbsolutePath());
		}
		return imgList;
	}
	public static class FileComparator implements Comparator<File> {	
		public int compare(File file1, File file2) {
			if (file1.lastModified() < file2.lastModified()){
				return -1;
			} else{
				return 1;
			}
		}
	}
	public static String getFileName(String path) {
		return new File(path).getName();
	}
	public static boolean existFile(String path) {
		return new File(path).exists();
	}
	final static String TAG = "FileUtil";

	/*
	 *  read one line every time，结果like this
	 *  ############################################################# SenseTime License# License Product: FaceSdk# Expiration: 20000101~20990101# License SN: 89c1ad09-5d79-4d14-b08d-.......................................
	 */
	public static String readFileFromSDCard(Context context, File path,
			String filename) {
		StringBuffer sb = null;
		try {
			File file = new File(path, filename);
			if (!file.exists()) {
				//QueryErrorCode.showToast(context, null, "找不到" + filename + "文件");
				return null;
			}
			BufferedReader br = new BufferedReader(new FileReader(file));
			String readline = "";
			sb = new StringBuffer();
			while ((readline = br.readLine()) != null) {
				//System.out.println("readline:" + readline);
				sb.append(readline);
			}
			br.close();
			Log.i(TAG, "license:" + sb.toString());
			return sb.toString();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/*
	 *  read all a time. like this  结果like this:
	 *  ############################################################
        # SenseTime License
        # License Product: FaceSdk
        # Expiration: 20000101~20990101
        # License SN: 89c1ad09-5d79-4d14-b08d-f4141ae653b9
        ############################################################
        sGfdd5sxA8NCweDGA+vU2qzOgOjTn64wtsOftvbVw+MzmQXlL9gGE+JsM0nU
        ........................
        
              由于cv_face_init_license_config(license)函数在处理license串时以\n为分隔符，所以保留。
	 */
	public static String readFileFromSDCard2ByPath(Context context, File path,String filename) {
		String result = null;
		try {
			File file = new File(path, filename);
			if (!file.exists()) {
				//QueryErrorCode.showToast(context, null, filename + "文件不存在");
				return null;
			}
			FileInputStream inputStream = new FileInputStream(file);
			byte[] b = new byte[inputStream.available()];
			inputStream.read(b);
			result = new String(b);
			Log.i(TAG, "readFileFromSDCard2");
			Log.i(TAG, result);
		} catch (Exception e) {
			//Toast.makeText(context, "读取失败", Toast.LENGTH_SHORT).show();
		}
		return result;
	}

	public static String readFileFromAssetsByPath(Context context, String fileName) {
		String res = "";
		try {
			InputStream in = context.getResources().getAssets().open(fileName);
			int length = in.available();
			byte[] buffer = new byte[length];

			in.read(buffer);
			in.close();
			res = new String(buffer,"UTF-8");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public static ArrayList<File> GetImageFiles(String Path){
		ArrayList<File> lstFile = new ArrayList<File>();
		File[] files = new File(Path).listFiles();

		for (int i = 0; i < files.length; i++){
			File f = files[i];
			if (f.isFile()){
				if (f.getPath().substring(f.getPath().length() - "jpg".length()).equals("jpg")||
						f.getPath().substring(f.getPath().length() - "png".length()).equals("png")||
						f.getPath().substring(f.getPath().length() - "bmp".length()).equals("bmp")){
					lstFile.add(f);
				}
			}
		}
		return lstFile;
	}

	public static ArrayList<File> GetPdfFiles(String Path){
		ArrayList<File> lstFile = new ArrayList<File>();
		File dirFile=new File(Path);
		if(!dirFile.exists()){
			return lstFile;
		}

		File[] files = dirFile.listFiles();

		for (int i = 0; i < files.length; i++){
			File f = files[i];
			if (f.isFile()){
				if (f.getPath().substring(f.getPath().length() - "pdf".length()).equals("pdf")){
					lstFile.add(f);
				}
			}
		}
		return lstFile;
	}
	/*
 * Java文件操作 获取文件扩展名
 *
 */
	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot >-1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return filename;
	}
	/*
     * Java文件操作 获取不带扩展名的文件名
     */
	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot >-1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}
}
