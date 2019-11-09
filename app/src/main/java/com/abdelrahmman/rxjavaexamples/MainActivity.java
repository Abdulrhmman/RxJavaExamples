package com.abdelrahmman.rxjavaexamples;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.click_test_relative_layout).setOnClickListener(this);
        findViewById(R.id.type_test_relative_layout).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.click_test_relative_layout:
                Intent clickTestIntent = new Intent(MainActivity.this, ClickTestActivity.class);
                startActivity(clickTestIntent);

                break;

            case R.id.type_test_relative_layout:
                Intent typeTestIntent = new Intent(MainActivity.this, SpeedTestActivity.class);
                startActivity(typeTestIntent);

                break;

        }
    }
}
