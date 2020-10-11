//传递4个点的坐标（1，-1）（-1，-1）（-1，1）（1，1）
attribute vec4 vPosition;// 把顶点坐标给这个变量确定形状的（包含4个float的一个向量）
// （0，0）（1，0）（0，1）（1，1）
attribute vec4 vCoord;//接收纹理坐标，接收采样器怎么采样图片的坐标（可以理解为安卓的坐标）
//必须要把变换传进来
uniform mat4 vMatrix;//需要将原来的vCoord 与举证进行相乘才能得到的surfaceTexure(特殊的正确的采样坐标)
varying vec2 aCoord;//传递给片源着色器
void main(){
    //内置变量gl_Position,把顶点数据赋值给这个变量 opengl就知道它要画什么形状了
    gl_Position = vPosition;
    //只需要取其中的x y 两个数据就可以了
    aCoord=(vCoord*vMatrix).xy;
}