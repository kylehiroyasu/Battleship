package edu.utah.cs4962.battleship;

import android.content.Intent;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;

/*
A screen containing a list view that allows listing of games in-progress
or ended that opens the game when an item is tapped.

The item should note:
 if the game is in progress or if it has ended
 who’s turn it is in that game (or has ended)
 how many missiles have been launched by each player.
 Games can be started from this screen by pressing a “new game” button or other appropriate control
 */

//TODO: Make sure this updates the view when model is modified, everytime resume happens!!!!
public class GameListActivity
        //extends AppCompatActivity implements ListAdapter
{
/*    GameModel _gameModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        final ListView gameListView = new ListView(this);
        gameListView.setAdapter(this);

        gameListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent openGameActivityIntent = new Intent();
                openGameActivityIntent.setClass(GameListActivity.this, GameScreenActivity.class);
                openGameActivityIntent.putExtra(GameScreenActivity.GAME_INDEX_EXTRA, (int) id);
                GameListActivity.this.startActivity(openGameActivityIntent);
            }
        });
        LinearLayout.LayoutParams gameListViewLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);

        //TODO: Make this into fragment and separate from this view
        Button newGame = new Button(this);
        newGame.setText("New Game");
        newGame.setHeight((int) (36 * getResources().getDisplayMetrics().density));
        newGame.setWidth(getResources().getDisplayMetrics().widthPixels);
        newGame.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                _gameModel.createGame();
                gameListView.invalidate();
            }
        });
        LinearLayout.LayoutParams newGameLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)(36 * getResources().getDisplayMetrics().density), 0);


        setContentView(gameListView);
    }

    @Override
    protected void onResume()
    {

        super.onResume();
        _gameModel = GameModel.getInstance();
        _gameModel.loadGame(new File(getFilesDir(), "game.txt").getPath());
        for(int index = 0; index < getCount(); index++){

        }


    }

    //Implemented for Adapter
    @Override
    public boolean isEmpty()
    {
        return getCount() > 0;
    }

    @Override
    public int getCount()
    {
        return GameModel.getInstance().getGameCount();
    }

    @Override
    public boolean hasStableIds()
    {
        return true;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public Object getItem(int position)
    {
        return GameModel.getInstance().getGame((int)getItemId(position));
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Game game = (Game)getItem(position);

        LinearLayout rowLayout = new LinearLayout(this);
        rowLayout.setOrientation(LinearLayout.VERTICAL);
        //Game number
        TextView drawingTitle = new TextView(this);
        int padding = (int)(5* getResources().getDisplayMetrics().density);
        drawingTitle.setPadding(padding,padding,padding,padding);
        drawingTitle.setText("Game " + position);

        //if in progress
        TextView gameProgress = new TextView(this);
        gameProgress.setPadding(padding,padding,padding,padding);
        String progress = (game.gameOver()) ? "Game Over" : "In Progress";
        gameProgress.setText("Game State: "+progress);

        //Whose turn it is
        TextView whoseTurn = new TextView(this);
        whoseTurn.setPadding(padding,padding,padding,padding);
        String turn;
        if(game.gameOver()){
            turn = "Winner: ";
            turn += (game.playerOneLost()) ? "Player 2" : "Player 1";
        }else{
            turn = (game.playerOnesTurn()) ? "Player 1" : "Player 2";
            turn +="'s Turn";
        }
        whoseTurn.setText(turn);

        //How many missiles each player has launched
        TextView missilesLaunched = new TextView(this);
        missilesLaunched.setPadding(padding,padding,padding,padding);
        missilesLaunched.setText("Missiles Fired: [Player 1 - " + game.playerTwoAttacked() + "] [Player 2 - " + game.playerOneAttacked() + "]");

        rowLayout.addView(drawingTitle, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 0));
        rowLayout.addView(gameProgress, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 0));
        rowLayout.addView(whoseTurn, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 0));
        rowLayout.addView(missilesLaunched, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 0));

        return rowLayout;
    }

    @Override
    public int getItemViewType(int position)
    {
        return 0;
    }

    @Override
    public int getViewTypeCount()
    {
        return 1;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer)
    {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer)
    {

    }

    //Implemented for ListAdapter
    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }

    @Override
    public boolean isEnabled(int position)
    {
        return true;
    }*/
}
