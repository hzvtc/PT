package com.as.pt.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.File;

/**
 * Created by FJQ on 2017/2/8.
 * 建立app名/logs/ 日志保存路径
 */
public class FUtil {
    public static File getLogDir(Context context){
        File dir = new File(getAppDir(context), "logs");
        if(!dir.exists()) {
            dir.mkdirs();
        }

        return dir;
    }

    public static File getAppDir(Context context){
        File dir = null;
        if(Environment.getExternalStorageState().equals("mounted")) {
            dir = new File(Environment.getExternalStorageDirectory().getPath(), getAppName(context));
            if(!dir.exists()) {
                dir.mkdirs();
            }
        } else {
            dir = context.getFilesDir();
        }

        return dir;
    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context)
    {
        try
        {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
