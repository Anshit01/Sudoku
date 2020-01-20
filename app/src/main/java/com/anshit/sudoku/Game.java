package com.anshit.sudoku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class Game extends AppCompatActivity {

    private Board mBoard;
    private TextView[][] mTextViewArr;
    private int mDifficulty;
    private TextView mActiveCell = null;
    private Timer mTimer;
    private int mTimerSeconds = 0;
    private boolean mGameActive = true;

    private TextView mTimerTextView;
    private TextView mLeftCellsCountTextView;
    private Button mExitButton;
    private LinearLayout mNumpadContainer;

    //KEYS:
    private final String PREF_DIFFICULTY = "difficulty";
    private final String PREF_TABLE = "table";
    private final String PREF_EDITABLE_CELLS = "editableCells";
    private final String PREF_PREVIOUS_GAME_DIFFICULTY = "previousGameDifficulty";
    private final String PREF_FILLED_CELLS = "filledCells";
    private final String PREF_TIMER_SECONDS = "timerSeconds";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mTimerTextView = findViewById(R.id.timerTextView);
        mLeftCellsCountTextView = findViewById(R.id.leftCellsCountTextView);
        mExitButton = findViewById(R.id.exitButton);
        mNumpadContainer = findViewById(R.id.numpadContainer);

        mExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: implement resume Option:
                finish();
            }
        });

        SharedPreferences prefs = getSharedPreferences("saved", MODE_PRIVATE);
        mDifficulty = prefs.getInt("difficulty", 50);

        getTextViews();
        Log.d("debug", "onCreate()");
        if(prefs.contains("table")){
            String table = prefs.getString(PREF_TABLE, null);
            String editableCells = prefs.getString(PREF_EDITABLE_CELLS, null);
            mDifficulty = prefs.getInt(PREF_PREVIOUS_GAME_DIFFICULTY, 50);
            int filledCells = prefs.getInt(PREF_FILLED_CELLS, 0);
            mTimerSeconds = prefs.getInt(PREF_TIMER_SECONDS, 0);
            updateTimer();
            mBoard = Board.fromSaved(table, editableCells, mDifficulty, filledCells);
        }
        else{
            mBoard = new Board(mDifficulty);
        }

        Toast.makeText(getApplicationContext(), "Difficulty: " + mDifficulty, Toast.LENGTH_SHORT).show();
        print();
        mLeftCellsCountTextView.setText("Left: " + (81 - mBoard.getFilledCells()));

        mTimer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                mTimerSeconds++;
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        updateTimer();
                    }
                });
            }
        };
        mTimer.scheduleAtFixedRate(timerTask, 1000, 1000);
    }


    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(!mGameActive){
            SharedPreferences pref = getSharedPreferences("saved", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            int difficulty = pref.getInt(PREF_DIFFICULTY, 50);
            editor.clear();
            editor.putInt(PREF_DIFFICULTY, difficulty);
            editor.commit();
        }else{
            SharedPreferences.Editor prefEditor = getSharedPreferences("saved", MODE_PRIVATE).edit();
            StringBuffer table = new StringBuffer();
            StringBuffer editableCells = new StringBuffer();
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    table.append(mBoard.getValFromTable(i, j));
                    editableCells.append((mBoard.getEditableCell(i, j)) ? 1 : 0);
                }
            }
            prefEditor.putString(PREF_TABLE, table.toString());
            prefEditor.putString(PREF_EDITABLE_CELLS, editableCells.toString());
            prefEditor.putInt(PREF_PREVIOUS_GAME_DIFFICULTY, mDifficulty);
            prefEditor.putInt(PREF_FILLED_CELLS, mBoard.getFilledCells());
            prefEditor.putInt(PREF_TIMER_SECONDS, mTimerSeconds);
            prefEditor.commit();
        }
        super.onDestroy();
    }

    private void updateTimer(){
        int sec = mTimerSeconds % 60;
        int min = mTimerSeconds / 60;

        String time = "";
        if(min < 10)
            time += "0";
        time += min + ":";
        if(sec < 10)
            time += "0";
        time += sec;
        mTimerTextView.setText(time);
    }

    private void getTextViews(){
        ConstraintLayout board = findViewById(R.id.board);
        View view;
        ConstraintLayout box;
        TextView cell;
        mTextViewArr = new TextView[9][9];

        ConstraintLayout[] boxes = new ConstraintLayout[9];
        boxes[0] = findViewById(R.id.include1);
        boxes[1] = findViewById(R.id.include2);
        boxes[2] = findViewById(R.id.include3);
        boxes[3] = findViewById(R.id.include4);
        boxes[4] = findViewById(R.id.include5);
        boxes[5] = findViewById(R.id.include6);
        boxes[6] = findViewById(R.id.include7);
        boxes[7] = findViewById(R.id.include8);
        boxes[8] = findViewById(R.id.include9);

        for(int i = 0; i < 9; i++){
            for(int j = 0; j <9; j++){
                mTextViewArr[i][j] = boxes[i].findViewWithTag("" + (j+1));
                mTextViewArr[i][j].setTag("" + (i+1) + (j+1));
            }
        }
    }

    public void onClickListener(View v){
        if(mGameActive) {
            if (mActiveCell != null) {
                int tag = Integer.parseInt(mActiveCell.getTag().toString());
                if (((tag/10)*9 + (tag%10)) % 2 == 0) {
                    mActiveCell.setBackgroundColor(getResources().getColor(R.color.boardColorDark));
                } else {
                    mActiveCell.setBackgroundColor(getResources().getColor(R.color.boardColorLight));
                }
            }
            mActiveCell = (TextView) v;
            int tag = Integer.parseInt(mActiveCell.getTag().toString());
            int boxno = tag/10;
            int cellno = tag%10;
            if(mBoard.check(boxno, cellno)){
                mActiveCell.setBackgroundColor(getResources().getColor(R.color.boardColorActive));
            }
            else{
                mActiveCell.setBackgroundColor(getResources().getColor(R.color.boardColorActiveWrong));
            }
        }
    }

    public void onClickListenerNumpad(View v){
        if(mActiveCell != null) {
            int val = Integer.parseInt(v.getTag().toString());
            int tag = Integer.parseInt(mActiveCell.getTag().toString());
            int boxno = tag / 10;
            int cellno = tag % 10;

            mBoard.set(boxno, cellno, val);
            mLeftCellsCountTextView.setText("Left: " + (81 - mBoard.getFilledCells()));

            if (val == 0) {
                mActiveCell.setText(" ");
            } else {
                mActiveCell.setText("" + val);
            }
            if(mBoard.check(boxno, cellno)){
                mActiveCell.setBackgroundColor(getResources().getColor(R.color.boardColorActive));
            }
            else{
                mActiveCell.setBackgroundColor(getResources().getColor(R.color.boardColorActiveWrong));
            }
            if (mBoard.getFilledCells() == 81) {
                if (mBoard.completeCheck()) {
                    win();
                }
            }
        }
    }

    private void print(){
        int num;
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                num = mBoard.get(i+1, j+1);
                mTextViewArr[i][j].setText((num == 0)? " " : "" + num);
                if(!mBoard.isEditable(i + 1, j + 1)){
                    mTextViewArr[i][j].setEnabled(false);
                    mTextViewArr[i][j].setTextColor(getResources().getColor(R.color.boardTextColorLight));
                }
                if(((i*9)+j)%2 == 0){
                    mTextViewArr[i][j].setBackgroundColor(getResources().getColor(R.color.boardColorDark));
                }
                else{
                    mTextViewArr[i][j].setBackgroundColor(getResources().getColor(R.color.boardColorLight));
                }
            }
        }
    }

    private void win(){
        showToast("You won!");
        mLeftCellsCountTextView.setText("Completed!");
        mLeftCellsCountTextView.setBackgroundColor(getResources().getColor(R.color.winColor));
        mExitButton.setBackgroundColor(getResources().getColor(R.color.winExit));

        int tag = Integer.parseInt(mActiveCell.getTag().toString());
        if (((tag/10)*9 + (tag%10)) % 2 == 0) {
            mActiveCell.setBackgroundColor(getResources().getColor(R.color.boardColorDark));
        } else {
            mActiveCell.setBackgroundColor(getResources().getColor(R.color.boardColorLight));
        }
        mActiveCell = null;
        mGameActive = false;

    }

    private void showToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

}
