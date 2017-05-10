package com.grofers.skthinks.my2048.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grofers.skthinks.my2048.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Skthinks on 17/11/16.
 */

public class HighScoresAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;

    private String teamRaw[] = {"Kevin Owens (C)", "Seth Rollins", "Roman Reigns", "Chris Jericho", "Braun Strowman"};
    private String teamSmackdown[] = {"A.J Styles (C)", "Dean Ambrose", "Randy Orton", "Bray Wyatt", "Shane McMahon"};

    public HighScoresAdapter(Context context){
        this.context = context;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(context).inflate(R.layout.single_row_typei, parent, false);
            return new TeamRawHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.single_row_typeii, parent, false);
            return new TeamSDHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position % 2 == 0) {
            TeamRawHolder teamRawHolder = (TeamRawHolder) holder;
            teamRawHolder.setSuperstar(teamRaw[position/2]);
        } else {
            TeamSDHolder teamSDHolder = (TeamSDHolder) holder;
            teamSDHolder.setSuperstar(teamSmackdown[position/2]);
        }


    }

    @Override
    public int getItemCount() {
        return teamRaw.length+teamSmackdown.length;
    }

    @Override
    public int getItemViewType(int position) {
        return (position % 2);
    }

    public void swapAdapter() {
        notifyDataSetChanged();
    }


    static class TeamRawHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.text_team_raw)
        TextView textTeamRaw;

        public TeamRawHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setSuperstar(String name){
            textTeamRaw.setText(name);
        }
    }

    static class TeamSDHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.text_team_smackdown)
        TextView textTeamSD;

        public TeamSDHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setSuperstar(String name){
            textTeamSD.setText(name);
        }

    }
}
