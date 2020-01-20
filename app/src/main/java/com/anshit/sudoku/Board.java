package com.anshit.sudoku;

import android.util.Log;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Random;

public class Board {

    private int[][] mTable = new int[9][9];
    private int mDifficulty;
    private int mFilledCells = 0;
    private boolean[][] mEditableCells = new boolean[9][9];
    private Random mRandom = new Random();

    Board(int difficulty){
        mDifficulty = difficulty;
        generate();
        removeSomeCells();
        updateFilledCells();
    }

    private Board(){

    }

    public void newBoard(int difficulty){
        mDifficulty = difficulty;
        generate();
        removeSomeCells();
        updateFilledCells();
    }

    public static Board fromSaved(String table, String editableCells,int difficulty, int filledCells){
        Board board = new Board();
        Log.d("debug", table + "\n" + editableCells + "\n" + difficulty + " & " + filledCells);

        board.mDifficulty = difficulty;
        board.mFilledCells = filledCells;
        int k = 0;
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++, k++){
                board.mTable[i][j] = Integer.parseInt(table.charAt(k) + "");
                if(editableCells.charAt(k) == '1'){
                    board.mEditableCells[i][j] = true;
                }
                else{
                    board.mEditableCells[i][j] = false;
                }
            }
        }

        return board;
    }

    public int get(int boxno, int cellno){
        int rb = boxno/3 + 1;
        int cb = boxno%3;
        if(cb == 0){
            rb--;
            cb = 3;
        }
        return getVal(rb, cb, cellno);
    }

    public void set(int boxno, int cellno, int val){
        int rb = boxno/3 + 1;
        int cb = boxno%3;
        if(cb == 0){
            rb--;
            cb = 3;
        }
        int oldVal = getVal(rb, cb, cellno);
        if(oldVal == 0 && val != 0){
            mFilledCells++;
        }
        else if(oldVal != 0 && val == 0){
            mFilledCells--;
        }
        setVal(rb, cb, cellno, val);
    }

    public boolean check(int boxno, int cellno){
        int rb = boxno/3 + 1;
        int cb = boxno%3;
        if(cb == 0){
            rb--;
            cb = 3;
        }
        int r = cellno/3 + 1;
        int c = cellno%3;
        if(c == 0){
            r--;
            c = 3;
        }
        int i = (--rb*3) + r - 1;
        int j = (--cb*3) + c - 1;
        if(mTable[i][j] == 0){
            return true;
        }
        for(int k = 0; k < 9; k++){
            if (j != k && mTable[i][j] == mTable[i][k]) {
                return false;
            } else if (i != k && mTable[i][j] == mTable[k][j]) {
                return false;
            } else if (cellno != k + 1 && mTable[i][j] == getVal(rb+1, cb+1, k + 1)) {
                return false;
            }
        }
        return true;
    }

    public int getValFromTable(int i, int j){
        return mTable[i][j];
    }

    public boolean getEditableCell(int i, int j){
        return mEditableCells[i][j];
    }

    public int getFilledCells(){
        return mFilledCells;
    }

    public boolean isEditable(int boxno, int cellno){
        int rb = boxno/3 + 1;
        int cb = boxno%3;
        if(cb == 0){
            rb--;
            cb = 3;
        }
        int r = cellno/3 + 1;
        int c = cellno%3;
        if(c == 0){
            r--;
            c = 3;
        }
        return mEditableCells[(--rb*3) + r - 1][(--cb*3) + c - 1];
    }

    private void setVal(int r, int b, int val){
        mTable[r - 1][b - 1] = val;
    }

    private void setVal(int rb, int cb, int r, int c,int val){
        mTable[(--rb*3) + r - 1][(--cb*3) + c - 1] = val;
    }

    private void setVal(int rb, int cb, int i, int val){
        int r = i/3 +1;
        int c = i % 3;
        if(c == 0){
            r--;
            c = 3;
        }
        setVal(rb, cb, r, c, val);
    }

    private int getVal(int r, int c){
        return mTable[r - 1][c - 1];
    }

    private int getVal(int rb, int cb, int r, int c){
        return mTable[(--rb*3) + r - 1][(--cb*3) + c - 1];
    }

    private int getVal(int rb, int cb, int i){
        int r = i/3 +1;
        int c = i % 3;
        if(c == 0){
            r--;
            c = 3;
        }
        return getVal(rb, cb, r, c);
    }
    private void generate(){
        clearTable();
        //fill diagonal boxes randomly
        for(int rcbi = 1; rcbi <= 3; rcbi++){
            ArrayList<Integer> list = new ArrayList<Integer>();
            for(int i = 1; i <= 9; i++){
                list.add(i);
            }
            for(int i = 1; i <= 9; i++){
                int rnd = mRandom.nextInt(10 - i);
                setVal(rcbi, rcbi, i, list.get(rnd));
                list.remove(rnd);
            }
        }
        //fill the rest of boxes
        generate2(1, 2, 1, 1);
    }

    private boolean generate2(int rbi, int cbi, int ri, int ci){
        if(rbi == 3 && cbi == 3)
            return true;
        if(rbi == cbi){
            cbi++;
            ri = ci = 1;
        }
        ArrayList<Integer> list = new ArrayList<Integer>();
        for(int i = 1; i <= 9; i++){
            list.add(i);
        }
        while(!list.isEmpty()){
            int rnd = mRandom.nextInt(list.size());
            if(check(rbi, cbi, ri, ci, list.get(rnd))){
                setVal(rbi, cbi, ri, ci, list.get(rnd));
                if(generate2((cbi == 3 && ri == 3 && ci == 3)? rbi +1 : rbi, (ri == 3 && ci == 3)? ((cbi == 3)? 1 : cbi+1) : cbi, (ci == 3)? ((ri == 3)? 1 : ri+1) : ri, (ci == 3)? 1 : ci+1)){
                    return true;
                }
                else{
                    list.remove(rnd);
                    setVal(rbi, cbi, ri, ci, 0);
                }
            }
            else{
                list.remove(rnd);
            }
        }
        return false;
    }

    private void removeSomeCells(){
        int favourable_probability = (int)(mDifficulty)*10000;
        int r;
        for(int i = 1; i <= 9; i++){
            for(int j = 1; j <= 9; j++){
                r = mRandom.nextInt(1000000);
                if(r < favourable_probability){
                    setVal(i, j, 0);
                    mEditableCells[i-1][j-1] = true;
                }
                else
                    mEditableCells[i-1][j-1] = false;
            }
        }
    }

    private void updateFilledCells(){
        int count = 0;
        for(int i = 1; i <= 9; i++){
            for(int j = 1; j <= 9; j++){
                if(getVal(i, j) != 0){
                    count++;
                }
            }
        }
        mFilledCells = count;
    }

    private void clearTable(){
        for(int i = 1; i <= 9; i++){
            for(int j = 1; j <= 9; j++){
                setVal(i, j, 0);
            }
        }
    }

    private boolean check(int rb, int cb, int r, int c, int val){
        int R = (rb-1)*3 + r;
        int C = (cb-1)*3 + c;
        for(int Ci = 1; Ci <= 9; Ci++){
            if(val == getVal(R, Ci))
                return false;
        }
        for(int Ri = 1; Ri <= 9; Ri++){
            if(val == getVal(Ri, C))
                return false;
        }
        for(int i = 1; i <= 3; i++){
            for(int j = 1; j <= 3; j++){
                if(val == getVal(rb, cb, i, j))
                    return false;
            }
        }
        return true;
    }

    public boolean completeCheck(){
        if(mFilledCells == 81){
            if(isComplete()){
                return true;
            }
        }
        return false;
    }

    private boolean isComplete(){
        int[] arr = new int[9];
        for(int i = 1; i <= 9; i++){
            for(int j = 1; j <= 9; j++){
                arr[j-1] = getVal(i, j);
                for(int k = 0; k <= j-2; k++){
                    if(arr[j-1] == arr[k]){
                        return false;
                    }
                }
            }
        }
        for(int i = 1; i <= 9; i++){
            for(int j = 1; j <= 9; j++){
                arr[j-1] = getVal(j, i);
                for(int k = 0; k <= j-2; k++){
                    if(arr[j-1] == arr[k]){
                        return false;
                    }
                }
            }
        }
        for(int rbi = 1; rbi <= 3; rbi++){
            for(int cbi = 1; cbi <= 3; cbi++){
                for(int i = 1; i <= 9; i++){
                    arr[i-1] = getVal(rbi, cbi, i);
                    for(int k = 0; k <= i-2; k++){
                        if(arr[i-1] == arr[k]){
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }



}
