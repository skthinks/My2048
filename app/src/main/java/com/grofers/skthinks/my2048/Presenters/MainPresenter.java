package com.grofers.skthinks.my2048.Presenters;

import android.util.Log;

import com.grofers.skthinks.my2048.Interfaces.MainInterface;
import com.grofers.skthinks.my2048.View.MainActivity;

import java.util.Random;

/**
 * Created by Skthinks on 14/10/16.
 */

public class MainPresenter implements MainInterface.Presenter {

    private int positionSetup[][] = new int[4][4];
    private int changeLog[][] = new int[4][4]; 
    private MainInterface.View view;
    private boolean MOVED;
    private int SCORE;

    public MainPresenter(MainActivity mainActivity){
        view = mainActivity;
    }

    public void onCreate(){
        setTimer(0);
        setPositions();
        setInitialPositions();
        setScore(0);
        setView();
    }

    private void setView(){
        view.setGrid(positionSetup);
        view.setScore(SCORE);
    }

    public void onResume(String grid, int score, long time){
        setGridfromSharedPreferences(grid);
        setScore(score);
        setTimer(time);
        setView();

    }

    private void setScore(int score){
        SCORE = score;
    }

    private void setMoved(boolean moved){
        MOVED = moved;
    }

    private void setGridfromSharedPreferences(String grid) {
        String rows[];
        rows = grid.split("\n");
        for (int row = 0; row < 4; row++){
            String cols[] = rows[row].split(",");
            for(int col =0; col < 4; col++){
                positionSetup[row][col] = Integer.parseInt(cols[col]);
            }
        }
    }


    private  void printRow(int row){
        Log.d("Row", String.valueOf(positionSetup[row][0] + " " +
                                    positionSetup[row][1] + " " +
                                    positionSetup[row][2] + " " +
                                    positionSetup[row][3]));
    }


    private void printCol(int col) {
        Log.d("Col", String.valueOf(positionSetup[0][col] + " " +
                                    positionSetup[1][col] + " " +
                                    positionSetup[2][col] + " " +
                                    positionSetup[3][col]));
    }

    
    
    @Override
    public void leftSwiped() {
        setMoved(false);
        int row = 0;
        while (row <= 3){
            int col = 0;
            Log.d("Rows",String.valueOf(row) );
            printRow(row);
            while (col <= 3) {
                if (col!=0) {
                    if (positionSetup[row][col] == 0) {
                        while(positionSetup[row][col] == 0 && numbersRight(row, col) && col!=3) {
                            shiftAllLeft(row, col);
                            setMoved(true);
                        }
                        if (positionSetup[row][col] == positionSetup[row][col - 1]){
                            positionSetup[row][col - 1] *= 2;
                            positionSetup[row][col] = 0;
                            if (positionSetup[row][col-1] != 0) {
                                SCORE += positionSetup[row][col - 1];
                                setMoved(true);
                                col--;
                            }

                        }
                        col++;
                    } else if (positionSetup[row][col] == positionSetup[row][col - 1] ) {
                        positionSetup[row][col - 1] *= 2;
                        positionSetup[row][col] = 0;
                        SCORE += positionSetup[row][col - 1];
                        setMoved(true);
                    } else {
                        col++;
                    }
                }
                else {
                    if (positionSetup[row][col] == 0) {
                        while(positionSetup[row][col] == 0 && numbersRight(row, col)) {
                            shiftAllLeft(row, col);
                            setMoved(true);
                        }
                        col++;
                    } else {
                        col++;
                    }
                }

            }
            printRow(row);
            row ++;
        }
        checkEndandNewEntry();
        view.setGrid(positionSetup);
        view.setScore(SCORE);
    }


    private void shiftAllLeft(int row, int col) {
        for (int c = col; c < 3; c++){
            positionSetup[row][c] = positionSetup[row][c+1];
            positionSetup[row][c+1] = 0;
        }
    }

    private boolean numbersRight(int row, int col) {
        for (int c=col;c <=3 ; c++)
            if(positionSetup[row][c] != 0)
                return true;
        return false;
    }


    @Override
    public void rightSwiped() {
        setMoved(false);
        int row = 0;
        while (row <= 3){
            int col = 3;
            printRow(row);
            while (col >= 0) {
                if (col!=3) {
                    if (positionSetup[row][col] == 0) {
                        while(positionSetup[row][col] == 0 && numbersLeft(row, col) && col!=0) {
                            shiftAllRight(row, col);
                            setMoved(true);
                        }
                        if (positionSetup[row][col] == positionSetup[row][col + 1]){
                            positionSetup[row][col + 1] *= 2;
                            positionSetup[row][col] = 0;
                            if (positionSetup[row][col + 1] != 0) {
                                setMoved(true);
                                SCORE += positionSetup[row][col + 1];
                                col++; // We need to keep it at the same position
                            }
                        }
                        col--;
                    } else if (positionSetup[row][col] == positionSetup[row][col + 1] ) {
                        positionSetup[row][col + 1] *= 2;
                        positionSetup[row][col] = 0;
                        SCORE += positionSetup[row][col + 1];
                        setMoved(true);
                    } else {
                        col--;
                    }
                }
                else {
                    if (positionSetup[row][col] == 0) {
                        while(positionSetup[row][col] == 0 && numbersLeft(row, col)) {
                            shiftAllRight(row, col);
                            setMoved(true);
                        }
                        col--;
                    } else {
                        col--;
                    }
                }

            }
            printRow(row);
            row ++;
        }
        checkEndandNewEntry();
        view.setGrid(positionSetup);
        view.setScore(SCORE);

    }

    private boolean numbersLeft(int row, int col) {
        for (int c=col;c >= 0 ; c--)
            if(positionSetup[row][c] != 0)
                return true;
        return false;
    }

    private void shiftAllRight(int row, int col) {
        for (int c = col; c >= 1; c--){
            positionSetup[row][c] = positionSetup[row][c-1];
            positionSetup[row][c-1] = 0;
        }
    }

    @Override
    public void downSwiped() {
        setMoved(false);
        int col = 0;
        while (col <= 3){
            int row = 3;
            printCol(col);
            while (row >= 0) {
                if (row!=3) {
                    if (positionSetup[row][col] == 0) {
                        while(positionSetup[row][col] == 0 && numbersUp(row, col) && row!=0) {
                            shiftAllDown(row, col);
                            setMoved(true);
                        }
                        if (positionSetup[row][col] == positionSetup[row+1][col]){
                            positionSetup[row+1][col] *= 2;
                            positionSetup[row][col] = 0;
                            if (positionSetup[row+1][col] != 0) {
                                SCORE += positionSetup[row + 1][col];
                                setMoved(true);
                                row++; // We need to keep it at the same position
                            }
                        }
                        row--;
                    } else if (positionSetup[row][col] == positionSetup[row + 1][col] ) {
                        positionSetup[row + 1][col] *= 2;
                        positionSetup[row][col] = 0;
                        SCORE += positionSetup[row + 1][col];
                        setMoved(true);
                    } else {
                        row--;
                    }
                }
                else {
                    if (positionSetup[row][col] == 0) {
                        while(positionSetup[row][col] == 0 && numbersUp(row, col)) {
                            shiftAllDown(row, col);
                            setMoved(true);
                        }
                        row--;
                    } else {
                        row--;
                    }
                }

            }
            printCol(col);
            col ++;
        }
        checkEndandNewEntry();
        view.setGrid(positionSetup);
        view.setScore(SCORE);

    }

    private void shiftAllDown(int row, int col) {
        for (int r = row; r >= 1; r--) {
            positionSetup[r][col] = positionSetup[r-1][col];
            positionSetup[r-1][col] = 0;
        }
    }

    private boolean numbersUp(int row, int col) {
        for (int r=row;r >= 0 ; r--)
            if(positionSetup[r][col] != 0)
                return true;
        return false;
    }

    @Override
    public void upSwiped() {
        setMoved(false);
        int col = 0;
        while (col <= 3){
            int row = 0;
            printCol(col);
            while (row <= 3) {
                if (row!=0) {
                    if (positionSetup[row][col] == 0) {
                        while(positionSetup[row][col] == 0 && numbersDown(row, col) && row!=3) {
                            shiftAllUp(row, col);
                            setMoved(true);
                        }
                        if (positionSetup[row][col] == positionSetup[row-1][col]){
                            positionSetup[row-1][col] *= 2;
                            positionSetup[row][col] = 0;
                            if (positionSetup[row-1][col] != 0) {
                                SCORE += positionSetup[row - 1][col];
                                setMoved(true);
                                row--;
                            }

                        }
                        row++;
                    } else if (positionSetup[row][col] == positionSetup[row-1][col] ) {
                        positionSetup[row-1][col] *= 2;
                        positionSetup[row][col] = 0;
                        SCORE += positionSetup[row - 1][col];
                        setMoved(true);
                    } else {
                        row++;
                    }
                }
                else {
                    if (positionSetup[row][col] == 0) {
                        while(positionSetup[row][col] == 0 && numbersDown(row, col)) {
                            shiftAllUp(row, col);
                            setMoved(true);
                        }
                        row++;
                    } else {
                        row++;
                    }
                }

            }
            printCol(col);
            col ++;
        }
        checkEndandNewEntry();
        view.setGrid(positionSetup);
        view.setScore(SCORE);


    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onPause(long time) {
        String grid = setupforSharing();
        if (!checkGameOver())
            view.setSharedPrefs(grid, "ONGOING", SCORE, time);
        else
            view.setSharedPrefs(grid, "COMPLETED", SCORE, time);
    }

    private String setupforSharing() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                sb.append(positionSetup[i][j]).append(",");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private void shiftAllUp(int row, int col) {
        for (int r = row; r < 3; r++){
            positionSetup[r][col] = positionSetup[r+1][col];
            positionSetup[r+1][col] = 0;
        }

    }

    private boolean numbersDown(int row, int col) {
        for (int r=row;r <=3 ; r++)
            if(positionSetup[r][col] != 0)
                return true;
        return false;
    }


    private void checkEndandNewEntry() {
        if(checkGameOver()) {
            view.showToast("GAME OVER");
            view.endGame();
        }
        else if(MOVED) {
            setRandomBlankPosition();
            if(checkGameOver()){
                view.showToast("GAME OVER");
                view.endGame();
            }

        };
    }

    private boolean checkGameOver() {
        if(checkFilled() && checkNoMove())
            return true;
        else
            return false;
    }

    private boolean checkNoMove() {
        for (int i = 0; i<=3; i++) {
            for (int j = 0; j <= 3; j++)
                if (i != 3 && j != 3) {
                    if (positionSetup[i][j] == positionSetup[i][j + 1] || positionSetup[i][j] == positionSetup[i + 1][j])
                        return false;
                } else if (i == 3 && j == 3) {
                    return true;
                } else if (i == 3) {
                    if (positionSetup[i][j] == positionSetup[i][j + 1])
                        return false;
                } else {
                    if (positionSetup[i][j] == positionSetup[i+1][j])
                        return false;
                }
        }
        return true;
    }


    private boolean checkFilled() {
        for (int i = 0; i<=3; i++)
            for (int j = 0; j<=3; j++)
                if (positionSetup[i][j] == 0)
                    return false;
        return true;
    }

    private void setPositions() {
        for(int row=0; row < positionSetup.length; row++) {
            for (int col = 0; col < positionSetup.length; col++) {
                positionSetup[row][col] = 0;
            }
        }
    }

    private void setInitialPositions(){
        setRandomBlankPosition();
        setRandomBlankPosition();
    }


    private void setRandomBlankPosition() {
        int pos;
        Random rand = new Random();
        while (true){
            pos = rand.nextInt(16);
            int row = pos/4;
            int col = pos%4;
            if (positionSetup[row][col] == 0){
                positionSetup[row][col] = getNumber();
                return;
            }
        }
    }

    private int getNumber() {
        Random rand = new Random();
        if (rand.nextInt(10) < 8)
            return 2;
        else
            return 4;
    }

    public void setTimer(long timer) {
        view.setTimer(timer);
    }
}
