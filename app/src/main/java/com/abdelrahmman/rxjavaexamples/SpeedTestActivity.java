package com.abdelrahmman.rxjavaexamples;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SpeedTestActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SpeedTestActivity";

    private CompositeDisposable disposable = new CompositeDisposable();

    private RelativeLayout prepareRelativeLayout, gameRelativeLayout;
    private EditText editText;
    private TextView testWord, result;
    private String randomWord;

    private List<String> testWordList = Arrays.asList("Comparable", "Imperceptible", "Congratulations", "Illumination", "Algorithm",
            "Rhetoric", "Commemorate", "Phenomenon", "Fraught", "Hypothesis", "Straightforward", "Mysterious",
            "Disgraceful", "Nomenclature", "Overwrought");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_test);

        prepareRelativeLayout = findViewById(R.id.prepare_relative_layout);
        gameRelativeLayout = findViewById(R.id.game_relative_layout);
        editText = findViewById(R.id.edit_text);
        testWord = findViewById(R.id.test_text_view);
        result = findViewById(R.id.result_text_view);

        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_play_again).setOnClickListener(this);

    }

    private void startGame(){

        int randomIndex = new Random().nextInt(testWordList.size());
        randomWord = testWordList.get(randomIndex);
        testWord.setText(randomWord);

        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {


                editText.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable s) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!emitter.isDisposed()){
                            emitter.onNext(String.valueOf(s));
                        }
                    }
                });
            }
        }).debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext: text: " + s);
                Log.d(TAG, "onNext: testWord.toString(): " + randomWord);
                testResult(s.toLowerCase(), randomWord.toLowerCase());
                result.setVisibility(View.VISIBLE);
                hideKeyboard();
                editText.setEnabled(false);
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

    private void testResult(String passedText, String testWord){
        if (passedText.equals(testWord)){
            result.setText("Congratulations, You won!");
            Log.d(TAG, "Congratulations, You won!");
        } else {
            result.setText("Failed, try again!");
            Log.d(TAG, "Failed, try again!");
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:

                prepareRelativeLayout.setVisibility(View.GONE);
                gameRelativeLayout.setVisibility(View.VISIBLE);
                editText.requestFocus();
                showKeyboard();
                startGame();
                break;

            case R.id.btn_play_again:

                prepareRelativeLayout.setVisibility(View.GONE);
                gameRelativeLayout.setVisibility(View.VISIBLE);
                result.setVisibility(View.INVISIBLE);
                showKeyboard();
                editText.setText("");
                editText.setEnabled(true);
                startGame();

                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}
