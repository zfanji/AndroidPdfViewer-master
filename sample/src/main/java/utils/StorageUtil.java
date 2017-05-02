package utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;

public class StorageUtil {
	public static final String TAG = "StorageUtil"; 
	public static final boolean DEBUG = false;
	public static final String IMAGE_ROOT = "DCIM/FaceTest";
	private static final String IMAGE_PATH_DIR = "image";
	private static final String THUMB_PATH_DIR = "thumb";
	private static final String SAVE_PATH_DIR = "save";
	public static String mStrSaveName = null;
	
	
	public static final String IMG_PATH_ABS = StorageUtil.getPictureStoragePath() + File.separator;
	public static final String THUMB_PATH_ABS = StorageUtil.getThumbStoragePath() + File.separator;
	//public static final String SAVE_PATH_ABS = StorageUtil.getSaveStoragePath() + File.separator;
	
    public static File getExternalStorage(){   	
        return Environment.getExternalStorageDirectory();
    }
    
    public static String getExternalStoragePath(){
    	if(DEBUG){
    		Log.d(TAG, "getExternalStoragePictureDirectory = " + Environment.getExternalStorageDirectory().getAbsolutePath());
    	}
    	return Environment.getExternalStorageDirectory().getAbsolutePath();
    }
    
    public static String getPictureStorageRootPath(){
    	if(DEBUG){
    		Log.d(TAG, "getPictureStorageRootPath = " + getExternalStoragePath() + File.separator + IMAGE_ROOT);
    	}
    	return getExternalStoragePath() + File.separator + IMAGE_ROOT;
    }
    
    public static String getPictureStoragePath(){
    	if(DEBUG){
    		Log.d(TAG, "getPictureStoragePath = " + getPictureStorageRootPath() + File.separator + IMAGE_PATH_DIR);
    	}
    	return getPictureStorageRootPath() + File.separator + IMAGE_PATH_DIR;
    }
    
    public static String getThumbStoragePath(){
    	if(DEBUG){
    		Log.d(TAG, "getThumbStoragePath = " + getPictureStorageRootPath() + File.separator + THUMB_PATH_DIR);
    	}
    	return getPictureStorageRootPath() + File.separator + THUMB_PATH_DIR;
    }
}
