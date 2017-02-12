package com.as.pt.util;

import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;

/**
 * Created by FJQ on 2017/2/12.
 */
public class LocalActivityManager {
    private static final String TAG = "LocalActivityMgr";
    ArrayList<Activity> mSocket = new ArrayList();
    private static LocalActivityManager mInstance;
    private Context context;

    private LocalActivityManager(Context context) {
        this.context = context;
    }

    public static LocalActivityManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new LocalActivityManager(context.getApplicationContext());
        }
        return mInstance;
    }

    public void pushActivity(Activity activity) {
        this.mSocket.add(activity);
    }

    public void popActivity(Activity activity) {
        if(this.mSocket.contains(activity)) {
            int index = this.mSocket.indexOf(activity);
            if(this.mSocket.size() > 0 && index < this.mSocket.size()) {
                Activity screen = (Activity)this.mSocket.get(index);
                screen.finish();
                this.mSocket.remove(activity);
            }
        }

    }

    public Activity getTopActivity() {
        if(this.mSocket.size() > 0) {
            Activity screen = (Activity)this.mSocket.get(this.mSocket.size() - 1);
            return screen;
        } else {
            return null;
        }
    }

    public int getActivityCount() {
        return this.mSocket.size();
    }

    public void removeActivity(Activity activity) {
        if(this.mSocket.contains(activity)) {
            this.mSocket.remove(activity);
        }

    }

    public void popActivity() {
        if(this.mSocket.size() > 0) {
            Activity screen = (Activity)this.mSocket.remove(this.mSocket.size() - 1);
            screen.finish();
        }

    }

    public void clearActivity() {
        Activity screen = null;

        while(this.mSocket.size() > 0) {
            screen = (Activity)this.mSocket.remove(this.mSocket.size() - 1);
            screen.finish();
        }

    }

    public ArrayList<Activity> getAllActivity() {
        return this.mSocket;
    }


}
