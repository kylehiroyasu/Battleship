package edu.utah.cs4962.battleship;

import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

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

 TODO:Figure out how I want to control view of boats on turn by turn basis
 */
public class GameScreenActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Useful variables for creation
        float dp = getResources().getDisplayMetrics().density;
        int padding = (int)(10*dp);

        //Master layout for game screen
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);

        //Instantiating layout params for each players grid
        LinearLayout.LayoutParams playerLayout;

        //Layout for Grids
        LinearLayout gameGridsLayout = new LinearLayout(this);
        LinearLayout.LayoutParams gameGridsLayoutParams;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            gameGridsLayout.setOrientation(LinearLayout.VERTICAL);
            gameGridsLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
        }else {
            //TODO: Horizontal display isn't working
            gameGridsLayout.setOrientation(LinearLayout.HORIZONTAL);
            gameGridsLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        }
        //Adding this players GameGridView to layout
        View playersGameGrid = new View(this);
        playersGameGrid.setBackgroundColor(Color.BLUE);

        //Adding the opponents GameGridView to layout
        View opponentsGameGrid = new View(this);
        opponentsGameGrid.setBackgroundColor(Color.RED);


        //Adding players grids
        gameGridsLayout.addView(playersGameGrid, gameGridsLayoutParams);
        gameGridsLayout.addView(opponentsGameGrid, gameGridsLayoutParams);

        //Attack button
        Button attackButton = new Button(this);
        attackButton.setPadding(padding, padding, padding, padding);
        attackButton.setWidth((int) (76 * dp));
        attackButton.setHeight((int) (36 * dp));
        attackButton.setText("Attack");
        LinearLayout.LayoutParams buttonLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int) (36 * dp), 0);

        //Adding views to rootLayout
        rootLayout.addView(gameGridsLayout, gameGridsLayoutParams);
        rootLayout.addView(attackButton,buttonLayout);
        setContentView(rootLayout);

    }

}
