package com.zxc.pocketsphinx;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zxc.pocketsphinx.util.PocketSphinxUtil;

import edu.cmu.pocketsphinx.demo.RecognitionListener;

public class MainActivity extends Activity implements RecognitionListener {

    private TextView text;
    private Button mStartBtn;
    private Button mStopBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text= (TextView)findViewById(R.id.text);
        mStartBtn = (Button) findViewById(R.id.start);
        mStopBtn = (Button) findViewById(R.id.stop);
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Main2Activity.thread.isAlive()){
                    Toast.makeText(MainActivity.this,"正在初始化",Toast.LENGTH_SHORT).show();
                    return;
                }
                PocketSphinxUtil.get(MainActivity.this).setListener(MainActivity.this);
                PocketSphinxUtil.get(MainActivity.this).start();
            }
        });
        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PocketSphinxUtil.get(MainActivity.this).stop();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PocketSphinxUtil.get(this).stop();
    }

    @Override
    public void onPartialResults(String b) {
        text.append("             2="+b);

    }

    @Override
    public void onResults(String b) {
        text.append("             1="+b);

    }

    @Override
    public void onError(int err) {
    }

}
