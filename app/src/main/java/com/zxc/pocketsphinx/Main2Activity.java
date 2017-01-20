package com.zxc.pocketsphinx;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class Main2Activity extends Activity {

    public static Thread thread;
    ProgressDialog dialog2;
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mButton = (Button) findViewById(R.id.mBtn);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main2Activity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        thread = new Thread(new Runnable() {
           @Override
           public void run() {
               CheckDir(Main2Activity.this);
           }
       });
        thread.start();
    }

    public void CheckDir(Context context){
            String dataPath = context.getFilesDir().getAbsolutePath();
            File zhPath = new File(dataPath + "/voice/zh");
            if (!zhPath.exists()) {
                zhPath.mkdirs();
            }
            File enPath = new File(dataPath + "/voice/en");
            if (!enPath.exists()) {
                enPath.mkdirs();
            }
            String rootPath = isZh(context) ? zhPath.getPath() : enPath.getPath();//根据环境选择中英文识别
            String dicPath = rootPath + "/text.dic";
            String imPath = rootPath + "/text.lm";
            if (!new File(dicPath).exists()) {
                releaseAssets(context, "/", dataPath);
            }
        }
        private boolean isZh(Context context) {
            Locale locale = context.getResources().getConfiguration().locale;
            String language = locale.getLanguage();
            if (language.endsWith("zh"))
                return true;
            else
                return false;
        }

        public void releaseAssets(Context context, String assetsDir,
                                  String releaseDir) {
            if (TextUtils.isEmpty(releaseDir)) {
                return;
            } else if (releaseDir.endsWith("/")) {
                releaseDir = releaseDir.substring(0, releaseDir.length() - 1);
            }

            if (TextUtils.isEmpty(assetsDir) || assetsDir.equals("/")) {
                assetsDir = "";
            } else if (assetsDir.endsWith("/")) {
                assetsDir = assetsDir.substring(0, assetsDir.length() - 1);
            }

            AssetManager assets = context.getAssets();
            try {
                String[] fileNames = assets.list(assetsDir);//只能获取到文件(夹)名,所以还得判断是文件夹还是文件
                if (fileNames.length > 0) {// is dir
                    for (String name : fileNames) {
                        if (!TextUtils.isEmpty(assetsDir)) {
                            name = assetsDir + "/" + name;//补全assets资源路径
                        }
//                    Log.i("", "brian name=" + name);
                        String[] childNames = assets.list(name);//判断是文件还是文件夹
                        if (!TextUtils.isEmpty(name) && childNames.length > 0) {
                            releaseAssets(context, name, releaseDir);//递归, 因为资源都是带着全路径,
                            //所以不需要在递归是设置目标文件夹的路径
                        } else {
                            InputStream is = assets.open(name);

                            String outPath = releaseDir + "/" + name;
                            if (outPath.contains("voice")) {
                                writeFile(outPath, is);
                            }

                        }
                    }
                } else {// is file
                    InputStream is = assets.open(assetsDir);
                    // 写入文件前, 需要提前级联创建好路径, 下面有代码贴出
                    String outPath = releaseDir + "/" + assetsDir;
                    if (outPath.contains("voice")) {
                        writeFile(outPath, is);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        private void writeFile(String outPath, InputStream is) throws IOException {
            FileOutputStream outputStream = new FileOutputStream(outPath);
            byte[] b = new byte[1024];
            int l = -1;
            while ((l = is.read(b)) != -1) {
                outputStream.write(b, 0, l);
            }
            outputStream.flush();
            outputStream.close();
            is.close();
        }





}
