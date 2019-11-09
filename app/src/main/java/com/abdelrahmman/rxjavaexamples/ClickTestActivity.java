package com.abdelrahmman.rxjavaexamples;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import kotlin.Unit;

public class ClickTestActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ClickTestActivity";

    CompositeDisposable disposable = new CompositeDisposable();

    private RelativeLayout relativeLayoutPrepare, relativeLayoutPlayAgain;
    private NumberPicker numberPicker;
    private TextView result, timer, clickNow;
    private Button btnPlayAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_test);

        relativeLayoutPrepare = findViewById(R.id.prepare_relative_layout);
        relativeLayoutPlayAgain = findViewById(R.id.play_again_relative_layout);
        numberPicker = findViewById(R.id.number_picker);
        result = findViewById(R.id.result_text_view);
        timer = findViewById(R.id.timer_text_view);
        clickNow = findViewById(R.id.click_now_text_view);
        btnPlayAgain = findViewById(R.id.btn_play_again);

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(60);

        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_play_again).setOnClickListener(this);

    }

    private void startGame() {
        RxView.clicks(findViewById(R.id.relative_layout))
                .map(new Function<Unit, Integer>() {
                    @Override
                    public Integer apply(Unit unit) throws Exception {
                        return 1;
                    }
                }).buffer(numberPicker.getValue(), TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: started count");
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(List<Integer> integers) {
                        Log.d(TAG, "onNext: you have clicked " + integers.size() + " times in " + numberPicker.getValue() + " seconds.");
                        result.setText("You have clicked " + integers.size() + " times in " + numberPicker.getValue() + " seconds.");
                        relativeLayoutPlayAgain.setVisibility(View.VISIBLE);
                        clickNow.setVisibility(View.GONE);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                btnPlayAgain.setEnabled(true);
                            }
                        }, 2000);
                        disposable.clear();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void timerSleep() {

        Thread thread = new Thread() {

            @Override
            public void run() {
                for (int i = 3; i >= 0; i--) {
                    try {
                        Thread.sleep(1000);
                        final int finalI = i;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "run: to run: " + finalI);
                                timer.setText(String.valueOf(finalI));
                                if (finalI == 0) {
                                    timer.setVisibility(View.GONE);
                                    clickNow.setVisibility(View.VISIBLE);
                                    startGame();
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        thread.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:

                relativeLayoutPrepare.setVisibility(View.GONE);
                relativeLayoutPlayAgain.setVisibility(View.GONE);
                timer.setVisibility(View.VISIBLE);
                timerSleep();
                btnPlayAgain.setEnabled(false);

                break;

            case R.id.btn_play_again:

                relativeLayoutPrepare.setVisibility(View.VISIBLE);
                relativeLayoutPlayAgain.setVisibility(View.GONE);

                break;

        }
    }

}
