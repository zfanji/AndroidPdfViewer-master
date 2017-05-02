package utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraUtil {

    private static String TAG="CameraUtil";

    //requestCode
    public static final int REQUEST_IMAGE_CAPTURE_THUMB = 1;
    public static final int REQUEST_IMAGE_CAPTURE_FULL = 2;
    public static final int REQUEST_IMAGE_PICK = 3;
    public static final int REQUEST_CATEGORY_OPENABLE_ANY= 4;
    public static final int REQUEST_CATEGORY_OPENABLE_IMG= 5;


    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    private static final String CAMERA_DIR = "/dcim/";
    private static final String albumName ="CameraSample";

    public static final int OPEN_ANYTHING= 1;
    public static final int OPEN_IMG= 2;

    public static void sendOpenFolderInten(Activity v,int type){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if(type==OPEN_ANYTHING){
            intent.setType("file/");
            v.startActivityForResult(intent,REQUEST_CATEGORY_OPENABLE_ANY);
        }else if (type==OPEN_IMG){
            intent.setType("image/*");
            v.startActivityForResult(intent,REQUEST_CATEGORY_OPENABLE_IMG);
        }
    }
    //获得文件路径,这里以public为例

    private static File getPhotoDir(){
        File storDirPrivate = null;
        File storDirPublic = null;

        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){

            //private,只有本应用可访问
            storDirPrivate = new File (
                    Environment.getExternalStorageDirectory()
                            + CAMERA_DIR
                            + albumName
            );

            //public 所有应用均可访问
            storDirPublic = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    albumName);

            if (storDirPublic != null) {
                if (! storDirPublic.mkdirs()) {
                    if (! storDirPublic.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }
        }else {
            Log.v(TAG, "External storage is not mounted READ/WRITE.");
        }

        return storDirPublic;//或者return storDirPrivate;

    }

    public static File createFile() throws IOException {
        File photoFile = null;

        String fileName;
        //通过时间戳区别文件名
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        fileName = JPEG_FILE_PREFIX+timeStamp+"_";

        photoFile = File.createTempFile(fileName,JPEG_FILE_SUFFIX,getPhotoDir());

        return photoFile;
    }


    public static void snedCameraIntent(Activity v, File photoFile) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(photoFile==null){
            v.startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE_THUMB);
            //       if(resultCode == RESULT_OK){
//        Bundle extras = data.getExtras();
//        Bitmap imageBitmap = (Bitmap) extras.get("data");

        }else{
             takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            v.startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE_FULL);
            //       if(resultCode == RESULT_OK){
//        setPic();
//        galleryAddPic();
        }
    }

    public static void sendGetImageIntent(Activity v){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        v.startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    public static Bitmap getBitmap(File photoFile,int targetW,int targetH) {

        //获得图像的尺寸
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFile.getAbsolutePath(),bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH =bmOptions.outHeight;

        //计算缩放
        int scaleFactor = 1;
        if((targetW>0)||(targetH>0)){
            scaleFactor = Math.min(photoW/targetW,photoH/targetH);
        }

        //将保存的文件解码
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;


        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), bmOptions);
        return bitmap;
    }


    //将图片文件添加至相册（便于浏览）
    public static void galleryAddPic(Context context,File photoFile) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photoFile);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }
}
