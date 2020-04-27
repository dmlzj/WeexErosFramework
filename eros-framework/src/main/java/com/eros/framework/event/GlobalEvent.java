package com.eros.framework.event;

import android.app.Activity;
import android.content.Context;

import com.eros.framework.activity.AbstractWeexActivity;
import com.eros.framework.adapter.router.RouterTracker;
import com.eros.framework.constant.WXEventCenter;
import com.eros.framework.manager.ManagerFactory;
import com.eros.framework.manager.impl.GlobalEventManager;
import com.eros.framework.manager.impl.ParseManager;
import com.eros.framework.model.BaseEventBean;
import com.eros.wxbase.EventGate;
import com.taobao.weex.WXSDKInstance;

/**
 * Created by liuyuanxiao on 18/4/9.
 */

public class GlobalEvent extends EventGate {


    @Override
    public void perform(Context context, BaseEventBean eventBean) {

        switch (eventBean.type){
            case WXEventCenter.EVENT_PUSHMANAGER:
                eventPushMessage(context, eventBean);
                break;
            case WXEventCenter.EVENT_STARTAPP:
                eventStartApp(context, eventBean);
                break;
        }
    }

    private void eventPushMessage(Context context, BaseEventBean eventBean) {
        Activity activity = RouterTracker.peekActivity();
        if (activity instanceof AbstractWeexActivity) {
            WXSDKInstance instance = ((AbstractWeexActivity) activity).getWXSDkInstance();
            ParseManager parseManager = ManagerFactory.getManagerService(ParseManager.class);
            GlobalEventManager.pushMessage(instance, parseManager.parseObject(eventBean.param));
        }
    }
    private void eventStartApp(Context context, BaseEventBean eventBean) {
        Activity activity = RouterTracker.peekActivity();
        if (activity instanceof AbstractWeexActivity) {
            WXSDKInstance instance = ((AbstractWeexActivity) activity).getWXSDkInstance();
            ParseManager parseManager = ManagerFactory.getManagerService(ParseManager.class);
            GlobalEventManager.startApp(instance, parseManager.parseObject(eventBean.param));
        }
    }
}
