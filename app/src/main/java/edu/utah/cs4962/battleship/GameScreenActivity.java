package edu.utah.cs4962.battleship;

import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

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
 TODO:Figure out how to bubble up events to deal with touch on attack
 */
public class GameScreenActivity extends AppCompatActivity implements GameGridView.SetAttackCoordListener
{
    static public String GAME_INDEX_EXTRA = "game_index";
    GameModel _gameModel = new GameModel();
    int _xCoord;
    int _yCoord;
    TextView _xText;
    TextView _yText;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //TODO: Make sure this is okay
        _gameModel = GameModel.getInstance();
        _gameModel.loadGame(new File(getFilesDir(), "game.txt").getPath());

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
        gameGridsLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
        LinearLayout.LayoutParams eachGridLayoutParams;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            gameGridsLayout.setOrientation(LinearLayout.VERTICAL);
            eachGridLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
        }else {
            gameGridsLayout.setOrientation(LinearLayout.HORIZONTAL);
            gameGridsLayout.setBackgroundColor(Color.CYAN);
            eachGridLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        }

        //Getting the current game to load into GameGridViews
        int currentGameIndex = getIntent().getIntExtra(GAME_INDEX_EXTRA, 0);
        Game currentGame = _gameModel.getGame(currentGameIndex);

        //Adding this players GameGridView to layout
        final GameGridView playersGameGrid = new GameGridView(this);
        playersGameGrid.loadGameGrid(currentGame.getPlayerOne(), true);
        playersGameGrid.setBackgroundColor(Color.BLUE);
        playersGameGrid.setSetAttackCoordListener(this);//TODO: THis needs to be controlled, so both aren't listening at the same time

        //Adding the opponents GameGridView to layout
        final GameGridView opponentsGameGrid = new GameGridView(this);
        opponentsGameGrid.loadGameGrid(currentGame.getPlayerTwo(), true); //TODO: Switch these values to alternate which is true and false
        opponentsGameGrid.setBackgroundColor(Color.RED);
        opponentsGameGrid.setSetAttackCoordListener(this);//TODO: THis needs to be controlled, so both aren't listening at the same time

        //Adding players grids
        gameGridsLayout.addView(playersGameGrid, eachGridLayoutParams);
        gameGridsLayout.addView(opponentsGameGrid, eachGridLayoutParams);

        //Attack Layout
        LinearLayout attackLayout = new LinearLayout(this);
        attackLayout.setOrientation(LinearLayout.HORIZONTAL);

        //Attack button
        Button attackButton = new Button(this);
        attackButton.setPadding(padding, padding, padding, padding);
        attackButton.setWidth((int) (76 * dp));
        attackButton.setHeight((int) (36 * dp));
        attackButton.setText("Attack");
        LinearLayout.LayoutParams buttonLayout = new LinearLayout.LayoutParams((int) (76 * dp),(int) (36 * dp), 2);

        //Creating functionality for Attack button
        attackButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(_xText.getText() != null && _yText.getText() != null){
                    //Getting intent game index
                    int gameIndex = getIntent().getIntExtra(GAME_INDEX_EXTRA, -1);
                    //making sure we had a valid game index
                    if(gameIndex >= 0) {
                        //Adding the turn to the selected game
                        _gameModel.getGame(gameIndex).addTurn(_xCoord, _yCoord);
                        opponentsGameGrid.invalidate(); //TODO: figure out why these gamegrids are not being redrawn
                        playersGameGrid.invalidate();
                    }
                }

            }
        });

        //Attack location boxes
        _xText = new TextView(this);
        _xText.setHeight((int) (36 * dp));
        _xText.setWidth((int) (36 * dp));
        _xText.setHint("X Coord");
        LinearLayout.LayoutParams xLayout = new LinearLayout.LayoutParams((int) (36 * dp),(int) (36 * dp),1);

        _yText = new TextView(this);
        _yText.setHeight((int) (36 * dp));
        _yText.setWidth((int) (36 * dp));
        _yText.setHint("Y Coord");

        attackLayout.addView(attackButton, buttonLayout);
        attackLayout.addView(_xText, xLayout);
        attackLayout.addView(_yText, xLayout);

        //Adding views to rootLayout
        rootLayout.addView(gameGridsLayout, gameGridsLayoutParams);
        rootLayout.addView(attackLayout);
        setContentView(rootLayout);

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        _gameModel.saveGame(new File(getFilesDir(), "game.txt").getPath());
    }

    @Override
    protected void onResume()
    {

        super.onResume();
        _gameModel.loadGame(new File(getFilesDir(), "game.txt").getPath());
    }


    //Event listener for game
    @Override
    public void SetAttackCoord(int x, int y)
    {
        _xCoord = x;
        _yCoord = y;
        _xText.setText("Col: "+_xCoord);
        _yText.setText("Row: "+_yCoord);

    }
}
