package com.luban.opengl.filter;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.luban.opengl.R;
import com.luban.opengl.util.OpenUtils;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * 负责往屏幕上渲染
 *
 * @author liuyang
 */
public class ScreenFilter {
    private final int mProgram;
    private final FloatBuffer mVertexBuffer;
    private int width;
    private int height;
    // 着色器变量
    private int vPosition;
    private int vCoord;
    private int vMatrix;
    private FloatBuffer mTextureBuffer;
    private int vTexture;

    public ScreenFilter(Context contex) {
        //顶点中的内容读出字符串
        String vertexSource = OpenUtils.readRawTextFile(contex, R.raw.camera_vertex);
        //片源着色器中的字符串内容
        String fragSource = OpenUtils.readRawTextFile(contex, R.raw.camera_fragment);
        // 通过字符串创建着色器程序
        //使用opengl
        //1.创建顶点着色器
        int vShaderID = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        //绑定代码到着色器中去
        GLES20.glShaderSource(vShaderID, vertexSource);
        //编译着色器代码
        GLES20.glCompileShader(vShaderID);
        //主动获取成功或者失败
        int[] status = new int[1];
        GLES20.glGetShaderiv(vShaderID, GLES20.GL_COMPILE_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            throw new IllegalStateException("顶点着色器配置失败");
        }
        //2。创建片源着色器
        int fShaderID = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fShaderID, fragSource);
        GLES20.glCompileShader(fShaderID);
        GLES20.glGetShaderiv(fShaderID, GLES20.GL_COMPILE_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            throw new IllegalStateException("片源着色器配置失败");
        }
        //3。创建着色器程序(GPU上的程序)
        mProgram = GLES20.glCreateProgram();
        //把着色器塞到程序中
        GLES20.glAttachShader(mProgram, vShaderID);
        GLES20.glAttachShader(mProgram, fShaderID);
        //链接着色器
        GLES20.glLinkProgram(mProgram);
        //获取程序是否获取成功
        GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            throw new IllegalStateException("片链接着色器失败");
        }
        /**
         * 因为已经塞到着色器程序中了 可以删除
         */
        GLES20.glDeleteShader(vShaderID);
        GLES20.glDeleteShader(fShaderID);
        //获得顶点着色器具的变量的索引根据索引 赋值
        vPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");
        vCoord = GLES20.glGetAttribLocation(mProgram, "vCoord");
        vMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        //获得片源着色器具
        vTexture = GLES20.glGetUniformLocation(mProgram, "vTexture");
        // 创建一个数据缓冲区 4个点 每个点两个数据 xy 数据类型float 主要用来记录顶点坐标
        mVertexBuffer = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertexBuffer.clear();
        //顺序很重要 不要乱动
        float[] v = {-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f};
        mVertexBuffer.put(v);

        mTextureBuffer = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mTextureBuffer.clear();
        float[] t = {0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f};
        //旋转
//        float[] t = {1.0f, 1.0f,
//                1.0f, 0.0f,
//                0.0f, 1.0f,
//                0.0f, 0.0f};
        //镜像
//        float[] t = {1.0f, 0.0f,
//                1.0f, 1.0f,
//                0.0f, 0.0f,
//                0.0f, 1.0f
//        };
        mTextureBuffer.put(t);
    }

    public void onReady(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * 画画
     *
     * @param texture
     * @param mtx
     */
    public void onDrawFrame(int texture, float[] mtx) {
        //1.设置窗口 画画的时候，画布是10*10的但是可以画5*5大小的
        // 画画的时候画布可以看出 10*10  也可以看成5*5 其他的也成
        //设置的画布大小 然后画画 画布越大 你画上去的图像会显得越小
        GLES20.glViewport(0, 0, width, height);
        //使用着色器程序
        GLES20.glUseProgram(mProgram);
        Buffer position = mVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);

        //传了数据之后 激活
        GLES20.glEnableVertexAttribArray(vPosition);

        //2、将纹理坐标传入，采样坐标
        mTextureBuffer.position(0);
        GLES20.glVertexAttribPointer(vCoord, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
        GLES20.glEnableVertexAttribArray(vCoord);

        //3、变换矩阵
        GLES20.glUniformMatrix4fv(vMatrix,1,false,mtx,0);

        //片元 vTexture 绑定图像数据到采样器
        //激活图层
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // 图像数据
        // 正常：GLES20.GL_TEXTURE_2D
        // surfaceTexure的纹理需要
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,texture);
        //传递参数 0：需要和纹理层GL_TEXTURE0对应
        GLES20.glUniform1i(vTexture,0);

        //参数传完了 通知opengl 画画 从第0点开始 共4个点
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
    }


}
