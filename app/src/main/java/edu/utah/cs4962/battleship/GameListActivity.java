package edu.utah.cs4962.battleship;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/*
A screen containing a list view that allows listing of games in-progress
or ended that opens the game when an item is tapped.

The item should note:
 if the game is in progress or if it has ended
 who’s turn it is in that game (or has ended)
 how many missiles have been launched by each player.
 Games can be started from this screen by pressing a “new game” button or other appropriate control
 */
public class GameListActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

}
