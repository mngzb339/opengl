package com.luban.opengl.widget;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import com.luban.opengl.util.CameraHelper;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class DouyingRender implements GLSurfaceView.Renderer ,SurfaceTexture.OnFrameAvailableListener {

    private final DouyingView mView;
    private CameraHelper cameraHelper;
    private SurfaceTexture mSurfaceTexture;
    //4*4的变换矩阵
    private float[] mtx = new float[16];

    public DouyingRender(DouyingView douyingView) {
        mView = douyingView;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //1.初始化操作 后置摄像头
        cameraHelper = new CameraHelper(Camera.CameraInfo.CAMERA_FACING_BACK);
        //准备好绘制的画布
        //通过openGl 创建纹理Id
        int[] textures = new int[1];
        GLES20.glGenTextures(textures.length,textures,0);
        mSurfaceTexture = new SurfaceTexture(textures[0]);
        mSurfaceTexture.setOnFrameAvailableListener(this);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //开启预览
        cameraHelper.startPreview(mSurfaceTexture);
    }

    /**
     * 开始画画
     * @param gl
     */
    @Override
    public void onDrawFrame(GL10 gl) {
     //配置屏幕 清理屏幕 告诉opengl 需要把屏幕清理为。。。颜色
     GLES20.glClearColor(0,0,0,1);
     //真正执行上一个glClearColor配置的颜色
     GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
     // 更新纹理 然后才能使用opengl 从surfaceTexure 获得数据进行渲染
     mSurfaceTexture.updateTexImage();
     //获得变换矩阵
     mSurfaceTexture.getTransformMatrix(mtx);
    }

    /**
     * surfaceTextTures 又一个有效新的图片的时候回调
     * @param surfaceTexture
     */
    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mView.requestRender();
    }
}
