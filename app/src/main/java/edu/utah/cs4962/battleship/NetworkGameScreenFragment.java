package edu.utah.cs4962.battleship;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by kylehiroyasu on 11/4/2015.
 */
public class NetworkGameScreenFragment extends Fragment implements GameGridView.SetAttackCoordListener
{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String GAME_RESOURCE = "GAME_RESOURCE";
    private static final int GAME_ID = 420;
    
    Integer _xCoord = null;
    Integer _yCoord = null;
    TextView _xText;
    TextView _yText;
    public int Player1Grid = 421;
    public int OpponentGrid = 422;
    GameGridView _playersGameGrid;
    GameGridView _opponentsGameGrid;

    private OnGameInteractionListener _onGameInteractionListener;

    //TODO: I dont think I should have this in the class...
    String _currentGameId;


    public void setCurrentGameId(String currentGameId)
    {
        _currentGameId = currentGameId;
        //Creating async Task
        AsyncTask<Void, Void, Game> sync = new AsyncTask<Void, Void, Game>()
        {

            protected Game doInBackground(Void... params)
            {
                return NetworkGameModel.getInstance().getGame();
            }
        };
        Game game;
        try{
            sync.execute();
            game = (Game) sync.get();
            _playersGameGrid.loadGameGrid(game.getPlayerOne(), false);
            _opponentsGameGrid.loadGameGrid(game.getPlayerTwo(), true);
            _playersGameGrid.invalidate();
            _opponentsGameGrid.invalidate();
        }catch (Exception e){
            Log.e("Exception!", "Excep:" + e + " Mess:" + e.getMessage());
        }

    }

    public static NetworkGameScreenFragment newInstance(int gameResource)
    {
        Bundle args = new Bundle();

        NetworkGameScreenFragment fragment = new NetworkGameScreenFragment();
        args.putInt(GAME_RESOURCE, gameResource);
        fragment.setArguments(args);
        return fragment;
    }

    public NetworkGameScreenFragment(){
        //Required empty public constructor
    }
    

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);



        AsyncTask<Void,Void,String> sync = new AsyncTask<Void, Void, String>()
        {
            @Override
            protected String doInBackground(Void... params)
            {
                    return NetworkGameModel.getInstance().getCurrenGameId();
            }
        };
        try{
            sync.execute();
            if(sync.get() != null){
                //Todo: Returns null!!!
                _currentGameId = sync.get();
            }
        }catch (Exception e){
            Log.e("Exception", "onCreate exception with Network. E:"+e+" "+e.getMessage());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //Useful variables for creation
        float dp = getResources().getDisplayMetrics().density;
        int padding = (int)(10*dp);

        //Master layout for game screen
        final LinearLayout rootLayout = new LinearLayout(getActivity());
        rootLayout.setOrientation(LinearLayout.VERTICAL);

        //Instantiating layout params for each players grid
        LinearLayout.LayoutParams playerLayout;

        //Layout for Grids
        final LinearLayout gameGridsLayout = new LinearLayout(getActivity());
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

        Game currentGame = null;
        //Getting the current game to load into GameGridViews
        AsyncTask<Void, Void, Game> sync = new AsyncTask<Void, Void, Game>()
        {
            @Override
            protected Game doInBackground(Void... params)
            {
                String gameId = NetworkGameModel.getInstance().getCurrenGameId();
                String playerId = NetworkGameModel.getInstance().getCurrentPlayerId();

                Log.i("GameSelection","Game selected: Current id-"+gameId+" playerId:"+playerId);
                //TODO: CurrentGameId is null too!
                return NetworkGameModel.getInstance().getGame();
            }
        };
        try{
            sync.execute();
            currentGame = sync.get();

            //Adding this players GameGridView to layout
            _playersGameGrid = new GameGridView(getActivity());
            _playersGameGrid.setId(Player1Grid);
            _playersGameGrid.loadGameGrid(currentGame.getPlayerOne(), true);
            _playersGameGrid.setBackgroundColor(Color.BLUE);
            if(currentGame.playerOnesTurn()) {
                _playersGameGrid.setShowBoats(true);
                _playersGameGrid.setSetAttackCoordListener(null);
            }else{
                _playersGameGrid.setShowBoats(false);
                _playersGameGrid.setSetAttackCoordListener(this);
            }
            //Adding the opponents GameGridView to layout
            _opponentsGameGrid = new GameGridView(getActivity());
            _opponentsGameGrid.setId(OpponentGrid);
            _opponentsGameGrid.loadGameGrid(currentGame.getPlayerTwo(), true);
            _opponentsGameGrid.setBackgroundColor(Color.RED);
            if(currentGame.playerOnesTurn()){
                _opponentsGameGrid.setShowBoats(false);
                _opponentsGameGrid.setSetAttackCoordListener(this);
            }else{
                _opponentsGameGrid.setShowBoats(true);
                _opponentsGameGrid.setSetAttackCoordListener(null);
            }




            //Adding players grids
            gameGridsLayout.addView(_playersGameGrid, eachGridLayoutParams);
            gameGridsLayout.addView(_opponentsGameGrid, eachGridLayoutParams);

        }catch (Exception e){
            Log.e("Exception", "FetchingCurrentGame in GameScreenFrag: "+e+": "+e.getMessage());
        }

        //Attack Layout
        LinearLayout attackLayout = new LinearLayout(getActivity());
        attackLayout.setOrientation(LinearLayout.HORIZONTAL);

        //Attack button
        final Button attackButton = new Button(getActivity());
        attackButton.setPadding(padding, padding, padding, padding);
        attackButton.setWidth((int) (76 * dp));
        attackButton.setHeight((int) (36 * dp));
        attackButton.setText("Attack");
        LinearLayout.LayoutParams buttonLayout = new LinearLayout.LayoutParams((int) (76 * dp),(int) (36 * dp), 2);

        //Creating functionality for Attack button
        final Game finalCurrentGame = currentGame;
        attackButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(_currentGameId != null) {
                    if(finalCurrentGame.gameOver()){
                        _opponentsGameGrid.setSetAttackCoordListener(null);
                        _playersGameGrid.setSetAttackCoordListener(null);
                        attackButton.setText("Game Over");
                    }else {
                        if (attackButton.getText() == "Attack" && _xCoord != null && _yCoord != null) {

                            //making sure we had a valid game index
                            //TODO: This might make trouble, changed on 11/6/15
                            if (true) {
                                //Adding the turn to the selected game
                                AsyncTask<String, Void, Game> sync = new AsyncTask<String, Void, Game>()
                                {
                                    @Override
                                    protected Game doInBackground(String... params)
                                    {
                                        NetworkGameModel.getInstance().updateGame(_xCoord, _yCoord);
                                        return NetworkGameModel.getInstance().getGame();
                                    }
                                };
                                Game updatedGame = null;
                                try{
                                    sync.execute();
                                    updatedGame =sync.get();
                                    if(updatedGame != null){
                                        _opponentsGameGrid.loadGameGrid(updatedGame.getPlayerTwo(), false);
                                        _playersGameGrid.loadGameGrid(updatedGame.getPlayerOne(), false);
                                        _opponentsGameGrid.setSetAttackCoordListener(null);
                                        _playersGameGrid.setSetAttackCoordListener(null);
                                        //Changing the ShowBoats boolean and click listener
                                        _xText.setText(null);
                                        _yText.setText(null);
                                        _xCoord = null;
                                        _yCoord = null;
                                    }
                                }catch (Exception e){
                                    Log.e("Exception: ", "AttackButton Exception: "+e+": "+e.getMessage());
                                }
                            }
                            attackButton.setText("Next Turn");
                            _onGameInteractionListener.onGameInteraction();
                        }
                        else if (attackButton.getText() == "Next Turn") {
                            attackButton.setText("Attack");
                            //switchClickListeners(_playersGameGrid, _opponentsGameGrid, _currentGameIndex);
                            //TODO: Need to ping server occasionally and update this if it is players turn!!!!
                        }
                    }
                }
            }
        });

        //Attack location boxes
        _xText = new TextView(getActivity());
        _xText.setHeight((int) (36 * dp));
        _xText.setWidth((int) (36 * dp));
        _xText.setHint("X Coord");
        LinearLayout.LayoutParams xLayout = new LinearLayout.LayoutParams((int) (36 * dp),(int) (36 * dp),1);

        _yText = new TextView(getActivity());
        _yText.setHeight((int) (36 * dp));
        _yText.setWidth((int) (36 * dp));
        _yText.setHint("Y Coord");

        _yText.setText(null);
        _xText.setText(null);

        attackLayout.addView(attackButton, buttonLayout);
        attackLayout.addView(_xText, xLayout);
        attackLayout.addView(_yText, xLayout);

        //Adding views to rootLayout
        rootLayout.addView(gameGridsLayout, gameGridsLayoutParams);
        rootLayout.addView(attackLayout);
        rootLayout.setId(GAME_ID);
        return rootLayout;

    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try {
            _onGameInteractionListener = (OnGameInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnGameInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }

    @Override
    public void SetAttackCoord(int x, int y)
    {
        _xCoord = x;
        _yCoord = y;
        _xText.setText("Col: "+_xCoord);
        _yText.setText("Row: "+_yCoord);

    }

    public interface OnGameInteractionListener
    {
        public void onGameInteraction();
    }
}
