package edu.utah.cs4962.battleship;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

//public class MainActivity extends AppCompatActivity implements GameListFragment.OnGameSelectedListener, GameScreenFragment.OnGameInteractionListener
public class MainActivity extends AppCompatActivity implements GameListFragment.OnGameSelectedListener, GameScreenFragment.OnGameInteractionListener
{
    public static final String GAME_LIST_FRAGMENT_TAG = "GAME_LIST_FRAGMENT_TAG";
    public static final String GAME_SCREEN_FRAGMENT_TAG = "GAME_SCREEN_FRAGMENT_TAG";

    GameModel _gameModel = GameModel.getInstance();

    Activity _gameActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.HORIZONTAL);

        setContentView(rootLayout);

        FrameLayout masterFrameLayout = new FrameLayout(this);
        masterFrameLayout.setId(10);
        FrameLayout detailFrameLayout = new FrameLayout(this);
        detailFrameLayout.setId(11);

        //Adding buttons
        Button newGame = new Button(this);
        Button delGame = new Button(this);
        newGame.setText("+");
        newGame.setHeight((int) (64 * getResources().getDisplayMetrics().density));

        delGame.setText("-");
        delGame.setHeight((int) (64 * getResources().getDisplayMetrics().density));

        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.addView(newGame, new LinearLayout.LayoutParams(0, (int) (64 * getResources().getDisplayMetrics().density), 1));
        buttonLayout.addView(delGame, new LinearLayout.LayoutParams(0, (int) (64 * getResources().getDisplayMetrics().density), 1));

        //Adding button functionality:
        newGame.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int newGameIndex = _gameModel.getInstance().createGame();
                onGameSelected(newGameIndex);
                onGameInteraction();

            }
        });

        delGame.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                GameScreenFragment gameScreenFragment = (GameScreenFragment)getFragmentManager().findFragmentByTag(GAME_SCREEN_FRAGMENT_TAG);
                int currentGameIndex = gameScreenFragment.getCurrentGameIndex();
                _gameModel.getInstance().deleteGame(currentGameIndex);
                if(_gameModel.getGameCount() < 1)
                    _gameModel.getInstance().createGame();
                gameScreenFragment.setCurrentGameIndex(0);
                onGameInteraction();
            }
        });

        //Building left side column with buttons and gamelist
        LinearLayout masterLayout = new LinearLayout(this);
        masterLayout.setOrientation(LinearLayout.VERTICAL);
        masterLayout.addView(buttonLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int) (64 * getResources().getDisplayMetrics().density), 0));
        masterLayout.addView(masterFrameLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        if(isTablet(getApplicationContext())){
            rootLayout.addView(masterLayout, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            rootLayout.addView(detailFrameLayout, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 2));
        }else{

        }

        //Never instantiate FragmentTransaction, always get via beginTransaction()
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        //Animations should always go first...

        //Need to make parts of transaction conditional to avoid making extra lost fragments in memory..
        GameListFragment gameListFragment = (GameListFragment) getFragmentManager().findFragmentByTag(GAME_LIST_FRAGMENT_TAG);
        //Null check
        if(gameListFragment == null){
            Log.i("Fragment", "Created game list fragment.");
            gameListFragment = GameListFragment.newInstance();
            //Only add if we didn't find one
            transaction.add(masterFrameLayout.getId(), gameListFragment, GAME_LIST_FRAGMENT_TAG);
        }


        //Need to make parts of transaction conditional to avoid making extra lost fragments in memory..
        GameScreenFragment gameScreenFragment = (GameScreenFragment)getFragmentManager().findFragmentByTag(GAME_SCREEN_FRAGMENT_TAG);
        //Null check
        if(gameScreenFragment == null){
            Log.i("Fragment", "created art fragment.");

            gameScreenFragment = GameScreenFragment.newInstance(0);
            //Only add if we didn't find one
            transaction.add(detailFrameLayout.getId(), gameScreenFragment, GAME_SCREEN_FRAGMENT_TAG);
        }

        //This actually causes the transactions to occur
        transaction.commit();
    }


    /*This method returns the gameindex which was selected.

    From here it should find the choose the correct game model and throw it into the GameScreenFragment
     */

    @Override
    public void onGameSelected(int gameId)
    {
        GameScreenFragment gameScreenFragment = (GameScreenFragment)getFragmentManager().findFragmentByTag(GAME_SCREEN_FRAGMENT_TAG);
        gameScreenFragment.setCurrentGameIndex(gameId);

        //TODO: Make sure this works
        if(!isTablet(getApplicationContext())){
            _gameActivity.setVisible(true);

            Intent openGameActivityIntent = new Intent();
            openGameActivityIntent.setClass(MainActivity.this, Activity.class);
            MainActivity.this.startActivity(openGameActivityIntent);
        }
    }

    //this method should allow the gameListActivity to update as needed to reflect changes going on in game
    //@Override
    public void onGameInteraction()
    {
        GameListFragment gameListFragment = (GameListFragment)getFragmentManager().findFragmentByTag(GAME_LIST_FRAGMENT_TAG);
        gameListFragment.invalidateRows();
    }

    //Method to detect if the device is a tablet or phone
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
