package com.as.pt.application;

import android.app.Application;

import com.as.pt.util.FUtil;

import cn.jesse.nativelogger.Logger;
import cn.jesse.nativelogger.NLogger;
import cn.jesse.nativelogger.formatter.SimpleFormatter;
import cn.jesse.nativelogger.logger.LoggerLevel;
import cn.jesse.nativelogger.logger.base.IFileLogger;
import cn.jesse.nativelogger.util.CrashWatcher;

/**
 * Created by FJQ on 2017/2/8.
 */
//@Logger(tag = "StandardApp", level = Logger.INFO)
public class StandardApp extends Application {

    private static final String TAG="StandardApp";
    @Override
    public void onCreate() {
        super.onCreate();
        //NLogger基础设置
//        NLogger.init(this);
        NLogger.getInstance()
                .builder()
//                .tag(TAG)//设置标签
                .loggerLevel(LoggerLevel.DEBUG)//设置LEVEL
                .fileLogger(true)//是否开启文件日志
                .fileDirectory(FUtil.getLogDir(this).getPath())//设置日志输出目录
                .fileFormatter(new SimpleFormatter())//日志文件名格式
                .expiredPeriod(1)//设置文件的过期时间
                .catchException(true, new CrashWatcher.UncaughtExceptionListener() {
                    @Override
                    public void uncaughtException(Thread thread, Throwable ex) {
                        //补货异常
                        NLogger.e(TAG, ex);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                })
                .build();
//        NLogger.i(TAG,"fileDirectory:"+FUtil.getLogDir(this).getPath());
        NLogger.zipLogs(new IFileLogger.OnZipListener() {
            @Override
            public void onZip(boolean succeed, String target) {
                if (succeed){
                    //会输出txt文件 lck文件 zip文件会压缩该文家下的所有txt文件
                    NLogger.i(TAG, "succeed : " + target);
                }

            }
        });
    }
}
