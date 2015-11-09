package edu.utah.cs4962.battleship;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class GameStatsActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        AsyncTask<Void,Void,Void> sync = new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                //NetworkGameModel.GameDetail details = NetworkGameModel.getInstance().getGameDetail();
                return null;
            }
        };

        setContentView(null);
    }

}
