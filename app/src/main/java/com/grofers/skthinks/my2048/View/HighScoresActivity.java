package com.grofers.skthinks.my2048.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.grofers.skthinks.my2048.Interfaces.HighScores;
import com.grofers.skthinks.my2048.Adapters.HighScoresAdapter;
import com.grofers.skthinks.my2048.Presenters.HighScoresPresenter;
import com.grofers.skthinks.my2048.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Skthinks on 17/11/16.
 */

public class HighScoresActivity extends AppCompatActivity implements HighScores.IActivity {

    @BindView(R.id.recycler_mixed)
    RecyclerView recyclerView;

    HighScores.Presenter presenter;

    HighScoresAdapter highScoresAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.highscores);
        ButterKnife.bind(this);
        setViewPresenter();
        initAdapter();

    }

    private void initAdapter() {
        highScoresAdapter = new HighScoresAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(highScoresAdapter);
        swapAdapter();
    }

    private void swapAdapter(){
        highScoresAdapter.swapAdapter();
    }

    private void setViewPresenter() {
        presenter = new HighScoresPresenter(this);
    }
}
