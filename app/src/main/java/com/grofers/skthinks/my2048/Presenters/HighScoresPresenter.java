package com.grofers.skthinks.my2048.Presenters;

import com.grofers.skthinks.my2048.Interfaces.HighScores;
import com.grofers.skthinks.my2048.View.HighScoresActivity;

/**
 * Created by Skthinks on 17/11/16.
 */

public class HighScoresPresenter implements HighScores.Presenter {

    HighScores.IActivity view;

    public HighScoresPresenter(HighScoresActivity highScoresActivity){
        view = highScoresActivity;
    }
}
