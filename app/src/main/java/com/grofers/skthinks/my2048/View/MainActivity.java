package com.grofers.skthinks.my2048.View;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.grofers.skthinks.my2048.CommonLibs;
import com.grofers.skthinks.my2048.Interfaces.ITwoButtonDialogListener;
import com.grofers.skthinks.my2048.Interfaces.MainInterface;
import com.grofers.skthinks.my2048.Presenters.MainPresenter;
import com.grofers.skthinks.my2048.R;
import com.grofers.skthinks.my2048.TimeTicker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;

public class MainActivity extends AppCompatActivity implements MainInterface.View, ITwoButtonDialogListener {

    MainInterface.Presenter presenter;
    TextView grid[][] = new TextView[4][4];

    @BindView(R.id.activity_main)
    TableLayout activity_2048;

    @BindView(R.id.text_2048_score)
    TextView textScore;

    @BindView(R.id.text_time)
    TextView textTime;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    TimeTicker tickerService;

    boolean serviceBound = false;

    TimeTicker.MyBinder bind;


    float x1=0, y1=0;
    boolean swipedStatus=false;

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            bind = (TimeTicker.MyBinder) iBinder;
            tickerService = bind.getService();
            serviceBound = true;
            getSharedPrefsLoadGrid();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            serviceBound = false;

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupSharedPreferences();
        Intent intent = new Intent(this, TimeTicker.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        setViewPresenter();
        setupGrid();
        registerReceiver(br, new IntentFilter(TimeTicker.COUNTDOWN_BR));
        startService(intent);
        //getSharedPrefsLoadGrid();

    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setTime(String.valueOf(intent.getLongExtra("countdown", 0)));
        }
    };

    private void setupSharedPreferences() {
        sharedPreferences = getSharedPreferences(CommonLibs.SharedPreferencesKeys.PREFERENCES, Context.MODE_PRIVATE);
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setScore(int score) {
        textScore.setText(String.valueOf(score));
    }

    @Override
    public void setSharedPrefs(String grid, String ongoing, int score, long time) {
        editor = sharedPreferences.edit();
        editor.putString(CommonLibs.SharedPreferencesKeys.GAME_STATUS, ongoing);
        editor.putString(CommonLibs.SharedPreferencesKeys.GAME_GRID, grid);
        editor.putInt(CommonLibs.SharedPreferencesKeys.GAME_SCORE, score);
        editor.putLong(CommonLibs.SharedPreferencesKeys.GAME_TIME, time);
        editor.commit();
    }

    @Override
    public void endGame() {
        setSharedPrefs("", CommonLibs.SharedPreferencesKeys.GAME_STATUS_COMPLETED, 0, 0);
        stopService(new Intent(this, TimeTicker.class));
        openDialog();
    }

    @Override
    public void setTimer(long timer) {
        if (bind!= null) {
            bind.setTime(timer);
        }
        else
            showToast("Binder is Null");
    }

    private void getSharedPrefsLoadGrid(){
        String grid = sharedPreferences.getString(CommonLibs.SharedPreferencesKeys.GAME_GRID, "");
        String status = sharedPreferences.getString(CommonLibs.SharedPreferencesKeys.GAME_STATUS,"");
        int score = sharedPreferences.getInt(CommonLibs.SharedPreferencesKeys.GAME_SCORE, 0);
        long time = sharedPreferences.getLong(CommonLibs.SharedPreferencesKeys.GAME_TIME, 0);
        if (status.equals(CommonLibs.SharedPreferencesKeys.GAME_STATUS_ONGOING)) {
            presenter.onResume(grid, score, time);
        }
        else
            presenter.onCreate();

    }


    @OnTouch(R.id.activity_main)
    public boolean detectTouch(final View v, final MotionEvent event){
        float x2, y2;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                // when user first touches the screen we get x and y coordinate
                x1 = event.getX();
                y1 = event.getY();
                swipedStatus = false;
                return true;
            case MotionEvent.ACTION_MOVE:
                if (swipedStatus == true)
                    return true;
                x2 = event.getX();
                y2 = event.getY();
                float X = x2 - x1;
                float Y = y2 - y1;
                if(Math.abs(X) > 20 || Math.abs(Y)>20) {
                    swipedStatus = true;
                    if (Math.abs(X) > Math.abs(Y)) {
                        if (X < 0) {
                            presenter.leftSwiped();
                        }
                        else {
                            presenter.rightSwiped();
                        }
                        return true;
                    } else {
                        if (Y > 0)
                            presenter.downSwiped();
                        else
                            presenter.upSwiped();
                        return true;
                    }
                }
                return false;
            default:
                return false;
        }


    }


    private void setViewPresenter() {
        presenter = new MainPresenter(this);
    }

    public void setupGrid(){
        for(int row=0; row < grid.length; row++){
            for(int col=0; col < grid.length; col++){
                grid[row][col] = (TextView) findViewById(getId(row,col));
                grid[row][col].setText("");
            }
        }
    }

    @Override
    public void setGrid(int gridNos[][]){
        for(int row=0; row < grid.length; row++){
            for(int col=0; col < grid.length; col++){
                if (gridNos[row][col] == 0) {
                    grid[row][col].setText("");
                }
                else
                    grid[row][col].setText(String.valueOf(gridNos[row][col]));
            }
        }
    }

    private int getId(int row, int col) {
        String code = row+""+col;
        switch (code){
            case "00":
                return R.id.pos00;
            case "01":
                return R.id.pos01;
            case "02":
                return R.id.pos02;
            case "03":
                return R.id.pos03;
            case "10":
                return R.id.pos10;
            case "11":
                return R.id.pos11;
            case "12":
                return R.id.pos12;
            case "13":
                return R.id.pos13;
            case "20":
                return R.id.pos20;
            case "21":
                return R.id.pos21;
            case "22":
                return R.id.pos22;
            case "23":
                return R.id.pos23;
            case "30":
                return R.id.pos30;
            case "31":
                return R.id.pos31;
            case "32":
                return R.id.pos32;
            case "33":
                return R.id.pos33;
            default:
                return 0;
        }
    }

    @Override
    protected void onPause() {
        presenter.onPause(bind.getTime());
        /*unregisterReceiver(br);
        stopService(new Intent(this, TimeTicker.class));*/
        if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
            unregisterReceiver(br);
            Intent intent = new Intent(this, TimeTicker.class);
            stopService(intent);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(sharedPreferences.getString(CommonLibs.SharedPreferencesKeys.GAME_STATUS,"").equals(sharedPreferences.getString(CommonLibs.SharedPreferencesKeys.GAME_STATUS_ONGOING,""))) {
            startService(new Intent(this, TimeTicker.class));
            registerReceiver(br, new IntentFilter(TimeTicker.COUNTDOWN_BR));
        }
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, TimeTicker.class));
        super.onDestroy();
    }

    public void setTime(String time){
        textTime.setText(timeFormat(time));
    }

    private String timeFormat(String time) {
        int time_in_secs = Integer.parseInt(time);

        String formatTime = "";

        if (time_in_secs / 3600 > 9)
            formatTime = String.valueOf(time_in_secs/3600)+":";
        else if (time_in_secs / 3600 > 0)
            formatTime = "0"+String.valueOf(time_in_secs/3600)+":";
        time_in_secs = time_in_secs % 3600;

        if (time_in_secs / 60 > 9)
            formatTime += String.valueOf(time_in_secs/60)+":";
        else if (time_in_secs / 60 > 0)
            formatTime += "0"+String.valueOf(time_in_secs/60)+":";


        if (time_in_secs % 60 > 9)
            formatTime += String.valueOf(time_in_secs%60);
        else
            formatTime += "0"+String.valueOf(time_in_secs%60);

        return formatTime;
    }

    public void openDialog(){
        DialogFragment dialogFragment = DialogFrag.newInstance(
                0,
                null,
                "",
                "",
                "",
                ""
        );

        dialogFragment.show(getSupportFragmentManager(), "StockOutDialog");
    }

    @Override
    public void onClickYes(Bundle bundle) {
        showToast("Yes");
    }

    @Override
    public void onClickNo() {
        showToast("No");
    }
}