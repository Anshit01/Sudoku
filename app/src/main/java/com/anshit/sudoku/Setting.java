package com.anshit.sudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class Setting extends AppCompatActivity {

    private int mDifficulty;

    private SeekBar mSeekBar;
    TextView mTextView;
    TextView mAboutTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Button backButton = findViewById(R.id.backButton);
        mSeekBar = findViewById(R.id.seekBar);
        mTextView = findViewById(R.id.difficultyTextView);
        mAboutTextView = findViewById(R.id.aboutTextView);

        mAboutTextView.setMovementMethod(LinkMovementMethod.getInstance());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setDifficulty(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SharedPreferences prefs = getSharedPreferences("saved", MODE_PRIVATE);
        mDifficulty = prefs.getInt("difficulty", 50);
        setText(mDifficulty);
        mSeekBar.setProgress(mDifficulty);
    }

    private void setDifficulty(int percentage){
        SharedPreferences.Editor prefEditor = getSharedPreferences("saved", MODE_PRIVATE).edit();
        prefEditor.putInt("difficulty", percentage);
        prefEditor.commit();
        mDifficulty = percentage;
        setText(percentage);
    }

    private void setText(int percent){
        mTextView.setText("Empty: " + mDifficulty + "% (approx.)");
    }

    public void onClickButtons(View v){
        Button buttonClicked = (Button) v;
        int index = Integer.parseInt(buttonClicked.getTag().toString());
        int percent = (index + 8) * 5;
        setDifficulty(percent);
        mSeekBar.setProgress(percent);
    }

}
