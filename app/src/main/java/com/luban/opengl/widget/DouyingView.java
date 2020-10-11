package com.luban.opengl.widget;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * @author liuyang
 */
public class DouyingView extends GLSurfaceView {


    public DouyingView(Context context) {
        super(context,null);
    }

    public DouyingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        /****************配置开始*******************/
        //配置glSurfaceView
        //1.设置egl 版本
        setEGLContextClientVersion(2);
        //2.设置一个渲染器
        setRenderer(new DouyingRender(this));
        //3.设置渲染器模式 设置按需渲染（当调用requestRender 请求GLThread 回调drawFrams
        //连续渲染 自动处理
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        /****************配置结束*******************/
    }
}
