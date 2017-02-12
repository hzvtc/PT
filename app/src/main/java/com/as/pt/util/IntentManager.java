package com.as.pt.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.as.pt.R;
import com.as.pt.activity.GameActivity;

/**
 * Created by FJQ on 2017/2/12.
 * startActivity不能那个使用ApplicationContext
 */
public class IntentManager {
    private static IntentManager mInstance;
    private Context context;

    private IntentManager(Context context) {
        this.context = context;
    }

    public static IntentManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new IntentManager(context);
        }
        return mInstance;
    }

    public void startAnotherActivity(Class<?> clazz,Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(context, clazz);
        if (bundle!=null){
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
        //设置切换动画，从右边进入，左边退出
        ((Activity)context).overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }
}
