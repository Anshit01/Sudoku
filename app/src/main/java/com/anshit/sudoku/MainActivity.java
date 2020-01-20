package com.anshit.sudoku;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.prefs.Preferences;

public class MainActivity extends AppCompatActivity {

    AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button startButton = findViewById(R.id.startButton);
        Button settingButton = findViewById(R.id.settingButton);
        Button exitButton = findViewById(R.id.exitButton);

        alertDialog = new AlertDialog.Builder(this);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent gameIntent = new Intent(MainActivity.this, Game.class);
                SharedPreferences pref = getSharedPreferences("saved", MODE_PRIVATE);
                if(pref.getString("table", "") != ""){
                    alertDialog.setCancelable(true);
                    alertDialog.setMessage("You have an existing saved game!");
                    alertDialog.setPositiveButton("Continue Saved Game", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(gameIntent);
                        }
                    });
                    alertDialog.setNegativeButton("Start New Game", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences pref = getSharedPreferences("saved", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            int difficulty = pref.getInt("difficulty", 50);
                            editor.clear();
                            editor.putInt("difficulty", difficulty);
                            editor.commit();
                            startActivity(gameIntent);
                        }
                    });

                    alertDialog.show();
                }else {
                    startActivity(gameIntent);
                }
            }
        });

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingActivityIntent = new Intent(MainActivity.this, Setting.class);
                startActivity(settingActivityIntent);
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
