package com.grofers.skthinks.my2048.Interfaces;

/**
 * Created by Skthinks on 14/10/16.
 */

public interface MainInterface {
    interface View{
        void setGrid(int grid[][]);

        void showToast(String done);

        void setScore(int score);

        void setSharedPrefs(String grid, String ongoing, int score, long time);

        void endGame();

        void setTimer(long timer);
    }

    interface Presenter{
        void onCreate();
        void leftSwiped();
        void rightSwiped();
        void downSwiped();
        void upSwiped();

        void onDestroy();

        void onPause(long time);

        void onResume(String grid, int score, long time);
    }
}
