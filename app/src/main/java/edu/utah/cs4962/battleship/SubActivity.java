package edu.utah.cs4962.battleship;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.io.File;

public class SubActivity extends AppCompatActivity implements NetworkGameScreenFragment.OnGameInteractionListener
{
    public static final String GAME_LIST_FRAGMENT_TAG = "GAME_LIST_FRAGMENT_TAG";
    public static final String GAME_SCREEN_FRAGMENT_TAG = "GAME_SCREEN_FRAGMENT_TAG";
    static public String GAME_INDEX_EXTRA = "game_index";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        FrameLayout detailFrameLayout = new FrameLayout(this);
        detailFrameLayout.setId(12);

        //Never instantiate FragmentTransaction, always get via beginTransaction()
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        //Need to make parts of transaction conditional to avoid making extra lost fragments in memory..
        NetworkGameScreenFragment gameScreenFragment = (NetworkGameScreenFragment)getFragmentManager().findFragmentByTag(GAME_SCREEN_FRAGMENT_TAG);

        //Null check
        if(gameScreenFragment == null){
            Log.i("Fragment", "created art fragment.");
            gameScreenFragment = NetworkGameScreenFragment.newInstance(0);
            //Only add if we didn't find one
            transaction.add(detailFrameLayout.getId(), gameScreenFragment, GAME_SCREEN_FRAGMENT_TAG);
        }

        //This actually causes the transactions to occur
        transaction.commit();

        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.HORIZONTAL);
        rootLayout.addView(detailFrameLayout, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 2));

        setContentView(rootLayout);

    }
    //this method should allow the gameListActivity to update as needed to reflect changes going on in game
    //@Override
    public void onGameInteraction()
    {
        NetworkGameListFragment gameListFragment = (NetworkGameListFragment)getFragmentManager().findFragmentByTag(GAME_LIST_FRAGMENT_TAG);
        if(gameListFragment != null) {
            gameListFragment.invalidateRows();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        NetworkGameModel.getInstance().saveGame(new File(getFilesDir(), "game.txt").getPath());
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        NetworkGameModel.getInstance().loadGame(new File(getFilesDir(), "game.txt").getPath());
    }
}
