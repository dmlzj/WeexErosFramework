package com.eros.framework;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.DebugUtils;

import com.eros.framework.activity.AbstractWeexActivity;
import com.eros.framework.activity.MainActivity;
import com.eros.framework.adapter.router.RouterTracker;
import com.eros.framework.constant.Constant;
import com.eros.framework.debug.ws.DebuggerWebSocket;
import com.eros.framework.extend.adapter.DefaultTypefaceAdapter;
import com.eros.framework.manager.ManagerFactory;
import com.eros.framework.manager.impl.GlobalEventManager;
import com.eros.framework.manager.impl.LifecycleManager;
import com.eros.framework.update.VersionChecker;
import com.eros.framework.utils.DebugableUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.qiyukf.unicorn.api.ImageLoaderListener;
import com.qiyukf.unicorn.api.SavePowerConfig;
import com.qiyukf.unicorn.api.StatusBarNotificationConfig;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.UnicornImageLoader;
import com.qiyukf.unicorn.api.YSFOptions;
import com.taobao.weex.WXSDKInstance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by Carry on 2017/9/4.
 */

public class BMWXApplication extends Application {
    private static BMWXApplication mInstance;
    private WXSDKInstance mMediator;
    private VersionChecker mVersionChecker;
    private DebuggerWebSocket debugSocket;
    private DefaultTypefaceAdapter typefaceAdapter;
//    private RefWatcher mWatcher;
    private StatusBarNotificationConfig mStatusBarNotificationConfig;

    /**
     * 是否全屏显示
     */
    public boolean IS_FULL_SCREEN = false;

    @Override
    public void onCreate() {
        super.onCreate();
        if (shouldInit()) {
            mInstance = this;
            initWeex();
            mVersionChecker = new VersionChecker(this);
            registerLifecycle();
            initDebugSocket();
//            mWatcher = DebugableUtil.isDebug() ? LeakCanary.install(this) : RefWatcher.DISABLED;
            mStatusBarNotificationConfig=new StatusBarNotificationConfig();
            mStatusBarNotificationConfig.notificationEntrance= MainActivity.class;
            // 七鱼客服初始化
            Unicorn.init(this, "d0272648f6cee6970423077219de0505", options(), new FrescoImageLoader(this));
            if (inMainProcess(this)) {
                int memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 8);
                ImageLoader.getInstance().init(new ImageLoaderConfiguration.Builder(this)
                        .memoryCacheSize(memoryCacheSize)
                        .diskCacheSize(50 * 1024 * 1024)
                        .build());
                Fresco.initialize(this);
            }
        }
    }
    public static boolean inMainProcess(Context context) {
        String mainProcessName = context.getApplicationInfo().processName;
        String processName = getProcessName();
        return TextUtils.equals(mainProcessName, processName);
    }
    /**
     * 获取当前进程名
     *
     * @return 进程名
     */
    private static String getProcessName() {
        BufferedReader reader = null;
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            reader = new BufferedReader(new FileReader(file));
            return reader.readLine().trim();
        } catch (IOException e) {
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


//    public RefWatcher getWatcher() {
//        return mWatcher;
//    }
    // 如果返回值为null，则全部使用默认参数。
    private YSFOptions options() {
        YSFOptions options = new YSFOptions();
        options.statusBarNotificationConfig = mStatusBarNotificationConfig;
        options.statusBarNotificationConfig.vibrate=false;
        options.savePowerConfig = new SavePowerConfig();
        return options;
    }

    /**
     * 设置点击Notification消息后进入的页面
     * @param activity
     */
    public void setServiceEntranceActivity(Class<? extends Activity> activity){
        mStatusBarNotificationConfig.notificationEntrance = activity;
    }

    private void initDebugSocket() {
        debugSocket = new DebuggerWebSocket(this);
        debugSocket.init();
    }

    public DefaultTypefaceAdapter getTypefaceAdapter() {
        return typefaceAdapter;
    }

    public void setTypefaceAdapter(DefaultTypefaceAdapter typefaceAdapter) {
        this.typefaceAdapter = typefaceAdapter;
    }


    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(base);
        super.attachBaseContext(base);
    }


    private void registerLifecycle() {
        LifecycleManager lifecycleManager = ManagerFactory.getManagerService(LifecycleManager
                .class);
        lifecycleManager.register(this).setOnTaskSwitchListenner(new LifecycleManager
                .OnTaskSwitchListener() {


            @Override
            public void onTaskSwitchToForeground() {
                Activity activity = RouterTracker.peekActivity();
                if (activity != null) {
                    GlobalEventManager.appActive(((AbstractWeexActivity) activity)
                            .getWXSDkInstance());
                }
                //app resume  try check version
                if (mVersionChecker != null) {
                    mVersionChecker.checkVersion();
                }
            }

            @Override
            public void onTaskSwitchToBackground() {
                Activity activity = RouterTracker.peekActivity();
                if (activity != null) {
                    GlobalEventManager.appDeactive(((AbstractWeexActivity) activity)
                            .getWXSDkInstance());
                }
            }
        });
    }


    private void initWeex() {
        BMWXEngine.initialize(this, new BMInitConfig.Builder().isActiceInterceptor(Constant
                .INTERCEPTOR_ACTIVE).build());

    }


    public static BMWXApplication getWXApplication() {
        return mInstance;
    }

    public VersionChecker getVersionChecker() {
        return mVersionChecker;
    }
}
