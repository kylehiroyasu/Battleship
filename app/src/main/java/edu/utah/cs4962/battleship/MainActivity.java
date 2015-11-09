package edu.utah.cs4962.battleship;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
//TODO: Use android timer or alarmmanager to check for updates
//TODO: I need to save games I have created with gameId and playerId
//public class MainActivity extends AppCompatActivity implements GameListFragment.OnGameSelectedListener, GameScreenFragment.OnGameInteractionListener
public class MainActivity extends AppCompatActivity implements NetworkGameListFragment.OnGameSelectedListener, NetworkGameScreenFragment.OnGameInteractionListener
{
    public static final String GAME_LIST_FRAGMENT_TAG = "GAME_LIST_FRAGMENT_TAG";
    public static final String GAME_SCREEN_FRAGMENT_TAG = "GAME_SCREEN_FRAGMENT_TAG";

    GameModel _gameModel = GameModel.getInstance();

    Activity _gameActivity;

    String _gameId;

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

        if(isTablet(getApplicationContext())) {
            delGame.setText("-");
        }else{
            delGame.setText("Delete Previous Game");
        }
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
                int newGameIndex = _gameModel.createGame();
                //TODO: Need to build an interface to collect user name and game name
                final String name = "Bozo";
                final String game = "RAW";
                AsyncTask<String, Void, String[]> sync = new AsyncTask<String, Void,String[]>()
                {
                    @Override
                    protected String[] doInBackground(String... params)
                    {
                        NetworkGameModel.getInstance().createGame(name, game);
                        return new String[]{NetworkGameModel.getInstance().getCurrentPlayerId(), NetworkGameModel.getInstance().getCurrenGameId()};
                    }
                };
                try{
                    sync.execute();
                    String[] result = sync.get();
                    _gameId = result[1];
                    String playerId = result[0];
                    onGameSelected(_gameId);
                    onGameInteraction();

                }catch(Exception e){
                    Log.e("Network Error:", "Exc: "+e+": "+e.getMessage());
                }


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
            rootLayout.addView(masterLayout, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        }

        //Never instantiate FragmentTransaction, always get via beginTransaction()
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        //Animations should always go first...

        //Need to make parts of transaction conditional to avoid making extra lost fragments in memory..
        NetworkGameListFragment gameListFragment = (NetworkGameListFragment) getFragmentManager().findFragmentByTag(GAME_LIST_FRAGMENT_TAG);
        //Null check
        if(gameListFragment == null){
            Log.i("Fragment", "Created game list fragment.");
            gameListFragment = NetworkGameListFragment.newInstance();
            //Only add if we didn't find one
            transaction.add(masterFrameLayout.getId(), gameListFragment, GAME_LIST_FRAGMENT_TAG);
        }

        if(isTablet(getApplicationContext())) {
            //Need to make parts of transaction conditional to avoid making extra lost fragments in memory..
            NetworkGameScreenFragment gameScreenFragment = (NetworkGameScreenFragment) getFragmentManager().findFragmentByTag(GAME_SCREEN_FRAGMENT_TAG);
            //Null check
            if (gameScreenFragment == null) {
                Log.i("Fragment", "created art fragment.");

                gameScreenFragment = NetworkGameScreenFragment.newInstance(0);
                //Only add if we didn't find one
                transaction.add(detailFrameLayout.getId(), gameScreenFragment, GAME_SCREEN_FRAGMENT_TAG);
            }
        }
        //This actually causes the transactions to occur
        transaction.commit();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        //GameModel.getInstance().saveGame(new File(getFilesDir(), "game.txt").getPath());
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //GameModel.getInstance().loadGame(new File(getFilesDir(), "game.txt").getPath());
    }

    /*This method returns the gameindex which was selected.

    From here it should find the choose the correct game model and throw it into the GameScreenFragment
     */

    @Override
    public void onGameSelected(String gameId)
    {
        if(isTablet(getApplicationContext())){
            NetworkGameScreenFragment gameScreenFragment = (NetworkGameScreenFragment)getFragmentManager().findFragmentByTag(GAME_SCREEN_FRAGMENT_TAG);
            if(gameScreenFragment != null) {
                gameScreenFragment.setCurrentGameId(gameId);
            }
        }else{
            _gameId = gameId;
            NetworkGameModel.getInstance().setCurrenGameId(_gameId);
            Intent openGameActivityIntent = new Intent();
            openGameActivityIntent.setClass(MainActivity.this, SubActivity.class);
            openGameActivityIntent.putExtra(SubActivity.GAME_INDEX_EXTRA, gameId);
            MainActivity.this.startActivity(openGameActivityIntent);
        }
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

    //Method to detect if the device is a tablet or phone
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
