package utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.PermissionChecker;
import android.util.Log;

/**
 * Created by peytonpeng on 2016/11/14.
 * 检查权限的工具类
 */

public class PermissionCheckerUtils {
    private final Context mContext;

    int targetSdkVersion;

    public PermissionCheckerUtils(Context context) {
        mContext = context.getApplicationContext();
        try {
            final PackageInfo info = mContext.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    // 判断权限集合
    public boolean lacksPermissions(String... permissions) {
        for (String permission : permissions) {
            if (selfPermissionGranted(permission)) {
                return true;
            }
        }
        return false;
    }


    // 判断是否缺少权限
    public boolean selfPermissionGranted(String permission) {
        // For Android < Android M, self permissions are always granted.
        boolean result ;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("selfPermissionGranted", "if---Build.VERSION.SDK_INT = " + Build.VERSION.SDK_INT);

            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can
                // use Context#checkSelfPermission
                result = mContext.checkSelfPermission(permission)
                        == PackageManager.PERMISSION_GRANTED;
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(mContext, permission)
                        == PermissionChecker.PERMISSION_GRANTED;
            }
        }else{
            result = PermissionChecker.checkSelfPermission(mContext, permission)
                    == PermissionChecker.PERMISSION_GRANTED;
            Log.d("selfPermissionGranted", "else---Build.VERSION.SDK_INT = " + Build.VERSION.SDK_INT + " ; permission = " + permission + "; result = " + result);
        }

        return result;
    }

}
