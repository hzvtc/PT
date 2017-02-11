package com.as.pt.util;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import cn.jesse.nativelogger.NLogger;
import cn.jesse.nativelogger.formatter.SimpleFormatter;
import cn.jesse.nativelogger.logger.LoggerLevel;
import cn.jesse.nativelogger.logger.base.IFileLogger;
import cn.jesse.nativelogger.util.CrashWatcher;

/**
 *
 */
public class PalLog {
    private static final String TAG = "PalLog";
    private static Context context;
    private static String path;//设置日志输出目录
    private static int period = 1;//设置文件的过期时间
    private static boolean enable = true;//是否开启文件日志
    //    private LoggerLevel level = LoggerLevel.DEBUG;
    public static final String SUFFIX_TXT = ".txt";
    private static final String TEMPLATE_DATE = "yyyy-MM-dd";
    //判断是否NativeLogger是否初始化配置
    private static boolean isConfig;
    private static Integer generate;

    public static void setPeriod(int periodTime) {
        period = periodTime;
    }

    public static void setFileLoggerEnable(boolean flag) {
        enable = flag;
    }

    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(TEMPLATE_DATE);
        return sdf.format(System.currentTimeMillis());
    }

    public static File getVmsDir(Context context) {
        File dir = null;
        if (Environment.getExternalStorageState().equals("mounted")) {
            dir = new File(Environment.getExternalStorageDirectory().getPath(), "vms");
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } else {
            dir = context.getFilesDir();
        }

        return dir;
    }


    public static File getLogDir(Context context) {
        File dir = new File(getVmsDir(context), "logs");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir;
    }

    //初始化NLogger配置
    public static void init(Context context) {
        generate=0;
        isConfig = false;
        path = getLogDir(context).getPath();
        NLogger.getInstance()
                .builder()
                .loggerLevel(LoggerLevel.DEBUG)
                .fileLogger(enable)
                .fileDirectory(path)
                .fileFormatter(new SimpleFormatter())//日志文件名格式
                .expiredPeriod(period)
                .catchException(true, new CrashWatcher.UncaughtExceptionListener() {
                    @Override
                    public void uncaughtException(Thread thread, Throwable ex) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                })
                .build();
//        renameFile();
        NLogger.zipLogs(new IFileLogger.OnZipListener() {
            @Override
            public void onZip(boolean succeed, String target) {
                if (succeed) {
                    //会输出txt文件 lck文件 zip文件会压缩该文家下的所有txt文件
                    NLogger.i(TAG, "succeed : " + target);
                }

            }
        });
        isConfig = true;
    }

    private static void renameFile() {
        File logFile = new File(path + File.separator + getCurrentDate());
        File targetLogFile = new File(path + File.separator + getCurrentDate() + SUFFIX_TXT);
        if (logFile.exists() && logFile.isFile() && !logFile.getName().endsWith(SUFFIX_TXT)) {
            logFile.renameTo(targetLogFile);
        }
    }

    public static void i(String msg) {
        checkStorageLog();
        NLogger.i(msg);
    }

    public static void i(String tag, String msg) {
        checkStorageLog();
        NLogger.i(tag, msg);
    }

    public static void i(String tag, String format, Object arg) {
        checkStorageLog();
        NLogger.i(tag, format, arg);
    }

    public static void i(String tag, String format, Object argA, Object argB) {
        checkStorageLog();
        NLogger.i(tag, format, argA, argB);
    }

    public static void i(String tag, String format, Object... args) {
        checkStorageLog();
        NLogger.i(tag, format, args);
    }

    public static void i(String tag, Throwable ex) {
        checkStorageLog();
        NLogger.i(tag, ex);
    }

    public static void d(String msg) {
        checkStorageLog();
        NLogger.d(msg);
    }

    public static void d(String tag, String msg) {
        checkStorageLog();
        NLogger.d(tag, msg);
    }

    public static void d(String tag, String format, Object arg) {
        checkStorageLog();
        NLogger.d(tag, format, arg);
    }

    public static void d(String tag, String format, Object argA, Object argB) {
        checkStorageLog();
        NLogger.d(tag, format, argA, argB);
    }

    public static void d(String tag, String format, Object... args) {
        checkStorageLog();
        NLogger.d(tag, format, args);
    }

    public static void d(String tag, Throwable ex) {
        checkStorageLog();
        NLogger.d(tag, ex);
    }

    public static void w(String msg) {
        checkStorageLog();
        NLogger.w(msg);
    }

    public static void w(String tag, String msg) {
        checkStorageLog();
        NLogger.w(tag, msg);
    }

    public static void w(String tag, String format, Object arg) {
        checkStorageLog();
        NLogger.w(tag, format, arg);
    }

    public static void w(String tag, String format, Object argA, Object argB) {
        checkStorageLog();
        NLogger.w(tag, format, argA, argB);
    }

    public static void w(String tag, String format, Object... args) {
        checkStorageLog();
        NLogger.w(tag, format, args);
    }

    public static void w(String tag, Throwable ex) {
        checkStorageLog();
        NLogger.w(tag, ex);
    }

    public static void e(String msg) {
        checkStorageLog();
        NLogger.e(msg);
    }

    public static void e(String tag, String msg) {
        checkStorageLog();
        NLogger.e(tag, msg);
    }

    public static void e(String tag, String format, Object arg) {
        checkStorageLog();
        NLogger.e(tag, format, arg);
    }

    public static void e(String tag, String format, Object argA, Object argB) {
        checkStorageLog();
        NLogger.e(tag, format, argA, argB);
    }

    public static void e(String tag, String format, Object... args) {
        checkStorageLog();
        NLogger.e(tag, format, args);
    }

    public static void e(String tag, Throwable ex) {
        checkStorageLog();
        NLogger.e(tag, ex);
    }

    public static void json(LoggerLevel level, String msg) {
        checkStorageLog();
        NLogger.json(level, msg);
    }

    public static void json(LoggerLevel level, String subTag, String msg) {
        checkStorageLog();
        NLogger.json(level, subTag, msg);
    }


    //检查日志压缩文件是否存在 不存在重新初始化配置
    public static void checkStorageLog() {
//        Log.e(TAG, "" + generate);
        if (isConfig){
                if (generate==0){
                    File targetLogFile = new File(path + File.separator + getCurrentDate());
//                    Log.e(TAG, "" + targetLogFile.exists());
                    if (!targetLogFile.exists()) {
                        generate++;
                        init(context);
                    }
                }
                else {
                    File targetLogFile = new File(path + File.separator + getCurrentDate()+"."+generate);
//                    Log.e(TAG, "" + targetLogFile.exists());
                    if (!targetLogFile.exists()) {
                        generate++;
                        init(context);
                    }
                }
            }


        }

    }

