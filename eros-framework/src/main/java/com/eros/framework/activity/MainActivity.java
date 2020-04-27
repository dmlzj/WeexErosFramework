package com.eros.framework.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.eros.framework.BMWXApplication;
import com.eros.framework.BMWXEnvironment;
import com.eros.framework.R;
import com.eros.framework.constant.Constant;
import com.eros.framework.event.TabbarEvent;
import com.eros.framework.manager.ManagerFactory;
import com.eros.framework.manager.StorageManager;
import com.eros.framework.manager.impl.GlobalEventManager;
import com.eros.framework.model.RouterModel;
import com.eros.framework.model.TabbarBadgeModule;
import com.eros.framework.model.WeexEventBean;
import com.eros.framework.utils.SharePreferenceUtil;
import com.eros.framework.view.TableView;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.ProductDetail;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.WXSDKInstance;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.nimlib.sdk.NimIntent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import bolts.Bolts;
//import com.qiyukf.unicorn.api.msg.

public class MainActivity extends AbstractWeexActivity {
    private FrameLayout layout_container;
    private ViewStub viewStub_tabView;
    private TableView tableView;
    private BroadcastReceiver mReloadReceiver;
    private RouterModel routerModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        String url = getIntent().getData().toString();
//        if (url.startsWith("oneone2846")){
//         getIntent().setData(Uri.parse(BMWXEnvironment.mPlatformConfig.getUrl().getJsServer()+"/dist/js/pages/auth/start.js"));
//        }

        Intent intent = getIntent();
        Uri pageUri = intent.getData();
        if (pageUri.getScheme().startsWith(Constant.SCHEME) && pageUri.getQueryParameterNames().size()>0){
           HashMap<String,String> params = new HashMap<>();
           Iterator<String> n= pageUri.getQueryParameterNames().iterator();

           while (n.hasNext()) {
               String name = n.next();
               params.put(name, pageUri.getQueryParameter(name));
           }

           intent.putExtra(Constant.ROUTERPARAMS,new RouterModel(pageUri.getPath(),"PUSH",params,"",false,"Default",true));
        }

        setIntent(intent);

        super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_main);
//        AndroidBug5497Workaround.assistActivity(this);
            routerModel = (RouterModel) getIntent().getSerializableExtra(Constant.ROUTERPARAMS);

            if (routerModel != null && Constant.TABBAR.equals(routerModel.url)) {
                initTabView();
            } else {
                layout_container = (FrameLayout) findViewById(R.id.layout_container);
                initView();
                renderPage();
            }
            initReloadReceiver();

            statusBarHidden(BMWXApplication.getWXApplication().IS_FULL_SCREEN);
            //七鱼客服
            parseIntent();
            ((BMWXApplication) getApplication()).setServiceEntranceActivity(getClass());

    }
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        parseIntent();
    }
    public static void consultService(final Context context, String uri, String title, ProductDetail productDetail) {
        // 启动聊天界面
        ConsultSource source = new ConsultSource(uri, title, null);
        source.productDetail = productDetail;
        Unicorn.openServiceActivity(context, staffName(), source);
    }
    private static String staffName() {
        return "客服";
    }
    /**
     * 七鱼需要
     */
    private void parseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
            consultService(this, null, null, null);
            // 最好将intent清掉，以免从堆栈恢复时又打开客服窗口
            setIntent(new Intent());
        }
    }

    private void initReloadReceiver() {
        mReloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!Constant.TABBAR.equals(routerModel.url)) {
                    renderPage();
                }
            }
        };
        LocalBroadcastManager.getInstance(BMWXEnvironment.mApplicationContext).registerReceiver(mReloadReceiver, new
                IntentFilter(WXSDKEngine.JS_FRAMEWORK_RELOAD));
    }

    private void initView() {
        mContainer = (ViewGroup) findViewById(R.id.layout_container);
    }

    private void initTabView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        viewStub_tabView = (ViewStub) findViewById(R.id.vs_tabView);
        viewStub_tabView.inflate();
        tableView = (TableView) findViewById(R.id.tabView);
        tableView.setData(BMWXEnvironment.mPlatformConfig.getTabBar());

    }


    @Override
    public boolean navigationListenter(WeexEventBean weexEventBean) {
        if (tableView != null) {
            return tableView.setNaigation(weexEventBean);
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (isHomePage() && BMWXEnvironment.mPlatformConfig.isAndroidIsListenHomeBack()) {
                WXSDKInstance wxsdkInstance = getWXSDkInstance();
                if (wxsdkInstance != null) {
                    GlobalEventManager.homeBack(wxsdkInstance);
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public WXSDKInstance getWXSDkInstance() {
        return (tableView != null) ? tableView.getWXSDKInstance() : super.getWXSDkInstance();

    }

    @Override
    public void refresh() {
        if (tableView != null) {
            tableView.refresh();
        } else {
            super.refresh();
        }
    }

    public void setBadge(TabbarBadgeModule module) {
        if (tableView != null) {
            tableView.setBadge(module);
        }
    }

    public void hideBadge(int index) {
        if (tableView != null) {
            tableView.hideBadge(index);
        }
    }

    public void openPage(int index) {
        if (tableView != null) {
            tableView.openPage(index);
        }
    }

    public int getPageIndex() {
        if (tableView != null) {
            return tableView.getCurrentIndex();
        }
        return -1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(BMWXEnvironment.mApplicationContext).unregisterReceiver(mReloadReceiver);
        ManagerFactory.getManagerService(StorageManager.class).deleteData(this,"isload");
    }
}
