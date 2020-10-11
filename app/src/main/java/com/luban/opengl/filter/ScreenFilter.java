package com.luban.opengl.filter;

import android.content.Context;

import com.luban.opengl.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 负责往屏幕上渲染
 *
 * @author liuyang
 */
public class ScreenFilter {
    public ScreenFilter(Context contex) {
        if (contex == null) {
            return;
        }
        InputStream is = contex.getResources().openRawResource(R.raw.camera_vertex);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder sb = new StringBuilder();
        try{

        while((line=br.readLine())!=null){
            sb.append(line);
        }
        br.close();
        }catch (Exception e){
            e.getStackTrace();
        }
        String vertexSource = sb.toString();

    }
}
