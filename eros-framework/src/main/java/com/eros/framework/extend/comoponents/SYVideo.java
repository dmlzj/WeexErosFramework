package com.eros.framework.extend.comoponents;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.eros.framework.extend.comoponents.view.VideoView;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.dom.WXDomObject;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXComponentProp;
import com.taobao.weex.ui.component.WXVContainer;

import java.util.Timer;

import cn.jzvd.JzvdStd;

public class SYVideo extends WXVContainer<VideoView> {
    private Timer timer;
    private String url = "";
    private String title = "";

    private VideoView videoView;
    private JzvdStd jzvdStd;
    public SYVideo(WXSDKInstance instance, WXDomObject dom, WXVContainer parent) {
        super(instance, dom, parent);
    }

    @Override
    protected VideoView initComponentHostView(@NonNull Context context) {

        VideoView mVideoView = new VideoView(context);
        mVideoView.instace = getInstance();
        return mVideoView;
    }
//    @WXComponentProp(name = "img")
//    public void setImg(String src) {
//        src = eeuiPage.rewriteUrl(getInstance(), src);
//        if (getHostView() != null) {
//            Glide.with((Activity) getContext()).load(src).into(getHostView().getCoverImageView());
//        }
//    }

    @WXComponentProp(name = "liveMode")
    public void setLiveMode(boolean live) {
        getHostView().liveMode = live;
        if (live) {
            getHostView().showChangeViews();
        }
    }


    @WXComponentProp(name = "autoPlay")
    public void setAutoPlay(boolean auto) {
        if (auto) {
            if (getHostView().getUrl() != null) {
                this.play();
            }
        }
    }

    @WXComponentProp(name = "pos")
    public void setPosition(int position) {
        if (getHostView() != null) {
            getHostView().seekTo(position);
        }
    }


    @WXComponentProp(name = "src")
    public void setSrc(String src) {
//        this.url = eeuiPage.rewriteUrl(getInstance(), src);
        this.url = src;
        getHostView().setUp(this.url, this.title + "");
    }

    @WXComponentProp(name = "title")
    public void setTitle(String title) {
        this.title = title;
        getHostView().setUp(this.url, this.title + "");
    }

    @JSMethod
    public void seek(int sec) {
        getHostView().seekTo(sec);
    }


    @JSMethod
    public void play() {
        getHostView().play();
    }

    @JSMethod
    public void pause() {
        getHostView().pause();
    }

    @JSMethod
    public void fullScreen() {
        getHostView().enterWindowFullscreen();
    }

    @JSMethod
    public void quitFullScreen() {
        getHostView().quitWindowFullscreen();

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
//                ((Activity) getContext()).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        firePlaying(false);
//                    }
//                });

            }
            super.handleMessage(msg);
        }
    };


}
