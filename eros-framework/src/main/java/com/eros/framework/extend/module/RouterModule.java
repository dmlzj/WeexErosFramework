package com.eros.framework.extend.module;


import android.app.Activity;

import com.alibaba.fastjson.JSONObject;
import com.eros.framework.adapter.router.RouterTracker;
import com.eros.framework.constant.Constant;
import com.eros.framework.constant.WXEventCenter;
import com.eros.framework.manager.StorageManager;
import com.eros.framework.model.WeexEventBean;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;
import com.eros.framework.manager.ManagerFactory;
import com.eros.framework.manager.impl.dispatcher.DispatchEventManager;
import com.eros.framework.constant.WXConstant;

import java.util.ArrayList;
import qiu.niorgai.StatusBarCompat;

/**
 * Created by Carry on 17/1/11.
 */

public class RouterModule extends WXModule {

    @JSMethod(uiThread = true)
    public void open(String params, JSCallback backCallback, JSCallback resultCallback) {
        WeexEventBean eventBean = new WeexEventBean();
        eventBean.setKey(WXEventCenter.EVENT_OPEN);
        eventBean.setJsParams(params);
        ArrayList<JSCallback> callBacks = new ArrayList<>();
        callBacks.add(backCallback);
        callBacks.add(resultCallback);
        eventBean.setCallbacks(callBacks);
        eventBean.setContext(mWXSDKInstance.getContext());
        ManagerFactory.getManagerService(DispatchEventManager.class).getBus().post(eventBean);
    }


    @JSMethod(uiThread = true)
    public void getParams(JSCallback callback) {
        WeexEventBean weexEventBean = new WeexEventBean();
        weexEventBean.setContext(mWXSDKInstance.getContext());
        weexEventBean.setJscallback(callback);
        weexEventBean.setKey(WXEventCenter.EVENT_GETPARAMS);
        ManagerFactory.getManagerService(DispatchEventManager.class).getBus().post(weexEventBean);
    }

    @JSMethod(uiThread = true)
    public void back(String params, JSCallback callback) {
        WeexEventBean weexEventBean = new WeexEventBean();
        weexEventBean.setKey(WXEventCenter.EVENT_BACK);
        weexEventBean.setContext(mWXSDKInstance.getContext());
        weexEventBean.setJsParams(params);
        weexEventBean.setJscallback(callback);
        ManagerFactory.getManagerService(DispatchEventManager.class).getBus().post(weexEventBean);
    }

    @JSMethod(uiThread = true)
    public void finishPage(String params, JSCallback callback) {
        WeexEventBean weexEventBean = new WeexEventBean();
        weexEventBean.setKey(WXEventCenter.EVENT_FINISH);
        weexEventBean.setContext(mWXSDKInstance.getContext());
        weexEventBean.setJsParams(params);
        weexEventBean.setJscallback(callback);
        ManagerFactory.getManagerService(DispatchEventManager.class).getBus().post(weexEventBean);
    }

    @JSMethod(uiThread = true)
    public void finish(String params, JSCallback callback) {
        finishPage(params, callback);
    }


    @JSMethod(uiThread = true)
    public void getBackParams(JSCallback callback) {
        WeexEventBean weexEventBean = new WeexEventBean();
        weexEventBean.setKey(WXEventCenter.EVENT_GETBACKPARAMS);
        weexEventBean.setContext(mWXSDKInstance.getContext());
        weexEventBean.setJscallback(callback);
        ManagerFactory.getManagerService(DispatchEventManager.class).getBus().post(weexEventBean);
    }


    @JSMethod
    public void refreshWeex(JSCallback callback) {
        WeexEventBean weexEventBean = new WeexEventBean();
        weexEventBean.setKey(WXEventCenter.EVENT_REFRESH);
        weexEventBean.setContext(mWXSDKInstance.getContext());
        weexEventBean.setJscallback(callback);
        ManagerFactory.getManagerService(DispatchEventManager.class).getBus().post(weexEventBean);
    }


    @JSMethod
    public void toMap(String destination) {
        WeexEventBean weexEventBean = new WeexEventBean();
        weexEventBean.setKey(WXEventCenter.EVENT_TOMAP);
        weexEventBean.setContext(mWXSDKInstance.getContext());
        weexEventBean.setJsParams(destination);
        ManagerFactory.getManagerService(DispatchEventManager.class).getBus().post(weexEventBean);
    }

    @JSMethod
    public void toWebView(String params) {
        WeexEventBean weexEventBean = new WeexEventBean();
        weexEventBean.setKey(WXEventCenter.EVENT_TOWEBVIEW);
        weexEventBean.setContext(mWXSDKInstance.getContext());
        weexEventBean.setJsParams(params);
        ManagerFactory.getManagerService(DispatchEventManager.class).getBus().post(weexEventBean);
    }


    @JSMethod(uiThread = true)
    public void openBrowser(String params, JSCallback callback) {
        WeexEventBean weexEventBean = new WeexEventBean();
        weexEventBean.setKey(WXEventCenter.EVENT_OPENBROWSER);
        weexEventBean.setContext(mWXSDKInstance.getContext());
        weexEventBean.setJsParams(params);
        weexEventBean.setJscallback(callback);
        ManagerFactory.getManagerService(DispatchEventManager.class).getBus().post(weexEventBean);
    }

    @JSMethod
    public void setHomePage(String params) {
        WeexEventBean weexEventBean = new WeexEventBean();
        weexEventBean.setKey(WXEventCenter.EVENT_SET_HOMEPAGE);
        weexEventBean.setContext(mWXSDKInstance.getContext());
        weexEventBean.setJsParams(params);
        ManagerFactory.getManagerService(DispatchEventManager.class).getBus().post(weexEventBean);
//
//        JSONObject jsonObject = new JSONObject(params);
//        JSONObject jsonObject = JSONObject.parseObject(params);
////        JSONObject jsonobject = JSONObject.fromObject(params);
//        boolean refresh = (boolean) jsonObject.get("refresh");
//        String urlpath = (String) jsonObject.get("path");
//
//        WeexEventBean weexEventBean = new WeexEventBean();
//        weexEventBean.setKey(WXEventCenter.EVENT_SET_HOMEPAGE);
//        weexEventBean.setContext(mWXSDKInstance.getContext());
//        weexEventBean.setJsParams(urlpath);
//        if(refresh){
//            ManagerFactory.getManagerService(DispatchEventManager.class).getBus().post(weexEventBean);
//        }

    }
    @JSMethod(uiThread = true)
    /**
     * @params flag:true设置字体等为黑色，false：设置为白色
     * @params hide:是否隐藏半透明灰色statusBar
     */
    public void setStatusBar(boolean flag, boolean hide){
        Activity activity = RouterTracker.peekActivity();
        StatusBarCompat.translucentStatusBar(activity, hide);
        if (flag) {
            StatusBarCompat.changeToLightStatusBar(activity);
        } else {
            StatusBarCompat.cancelLightStatusBar(activity);
        }
    }
    @JSMethod(uiThread = true)
    public void setFullScreen() {
        Activity activity = RouterTracker.peekActivity();
        StatusBarCompat.translucentStatusBar(activity);
    }
    @JSMethod(uiThread = true)
    public void nav(String params) {
        WeexEventBean weexEventBean = new WeexEventBean();
        weexEventBean.setKey(WXEventCenter.EVENT_NAV);
        weexEventBean.setContext(mWXSDKInstance.getContext());
        weexEventBean.setJsParams(params);
        ManagerFactory.getManagerService(DispatchEventManager.class).getBus().post(weexEventBean);
    }

    @JSMethod
    public void clearHomePage() {
        StorageManager storageManager = ManagerFactory.getManagerService(StorageManager.class);
        storageManager.setData(mWXSDKInstance.getContext(), Constant.SP.SP_HOMEPAGE_URL, "");
    }


}
