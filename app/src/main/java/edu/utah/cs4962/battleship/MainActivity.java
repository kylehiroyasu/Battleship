package edu.utah.cs4962.battleship;

import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

//public class MainActivity extends AppCompatActivity implements GameListFragment.OnGameSelectedListener, GameScreenFragment.OnGameInteractionListener
public class MainActivity extends AppCompatActivity implements GameListFragment.OnGameSelectedListener
{
    public static final String GAME_LIST_FRAGMENT_TAG = "GAME_LIST_FRAGMENT_TAG";
    public static final String GAME_SCREEN_FRAGMENT_TAG = "GAME_SCREEN_FRAGMENT_TAG";

    GameModel _gameModel = new GameModel();

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
        rootLayout.addView(masterFrameLayout, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        rootLayout.addView(detailFrameLayout, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 2));

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

            //TODO:Is it correct to set this at 0
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
    }

    //this method should allow the gameListActivity to update as needed to reflect changes going on in game
    //@Override
    public void onGameInteraction(int index)
    {

    }
}
