package edu.utah.cs4962.battleship;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/*
A screen showing:
 -a grid that contains the locations of the player’s ships
 -the locations their opponent has launched missiles against them
The screen also needs to show:
 -a grid that lets them launch missiles against their enemy’s ships by tapping
a grid cell
 -shows where they have launched missiles previously, including “hit” or
“missed” information. Colored boxes or circles are enough to communicate this
information.

If your interface is this simple, prefer blue to indicate open water, grey to
indicate a ship, white to indicate a miss, and red to indicate a hit.

The screen should not show where the opponent’s ships are.

If the game has already ended, no further missiles may be launched
 and the screen should somehow indicate the winner
 */
public class GameScreenActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);
    }

}
