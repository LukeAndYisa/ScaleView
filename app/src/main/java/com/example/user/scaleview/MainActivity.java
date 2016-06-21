package com.example.user.scaleview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tvNumber;
    private ScaleView scaleview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvNumber = (TextView)findViewById(R.id.tvNumber);
        scaleview = (ScaleView)findViewById(R.id.scaleview);
        scaleview.setMaxNumber(1000);
        scaleview.setMinNumber(-1000);
        scaleview.setScaleNumber(10);
        scaleview.setAllBlockNum(30);
        scaleview.setTextSize(30);
        scaleview.setCenterNum(100);
        scaleview.setNumberListener(new ScaleView.NumberListener() {
            @Override
            public void onChanged(int mCurrentNum) {
                tvNumber.setText(mCurrentNum + "");
            }
        });
    }
}
