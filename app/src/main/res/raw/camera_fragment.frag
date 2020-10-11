#extension GL_OES_EGL_image_external : require
//surfaceTexture 比较特殊
//指定float 数据是什么精度的
precision mediump float;
//采样点的坐标（必须和顶点采样器里面声明为一模一样的值就可以了）
varying vec2 aCoord ;
uniform samplerExternalOES vTexture;
void main(){
   //变量 接收像素值
    // texture2D：采样器 采集 aCoord的像素
    //赋值给 gl_FragColor 就可以了
    gl_FragColor =texture2D(vTexture,aCoord);
}