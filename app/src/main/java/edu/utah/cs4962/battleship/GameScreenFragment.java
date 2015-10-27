package edu.utah.cs4962.battleship;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnGameInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GameScreenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameScreenFragment extends Fragment implements GameGridView.SetAttackCoordListener
{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String GAME_RESOURCE = "GAME_RESOURCE";
    private static final int GAME_ID = 420;

    GameModel _gameModel = new GameModel();
    Game _currentGame;
    Integer _xCoord = null;
    Integer _yCoord = null;
    TextView _xText;
    TextView _yText;
    public int _currentGameIndex;
    public int Player1Grid = 421;
    public int OpponentGrid = 422;
    GameGridView _playersGameGrid;
    GameGridView _opponentsGameGrid;

    public int getCurrentGameIndex()
    {
        return _currentGameIndex;
    }

    public void setCurrentGameIndex(int currentGameIndex)
    {
        _currentGameIndex = currentGameIndex;
        _playersGameGrid._grid = _gameModel.getGame(_currentGameIndex).getPlayerOne();
        _opponentsGameGrid._grid = _gameModel.getGame(_currentGameIndex).getPlayerTwo();

        _playersGameGrid.invalidate();
        _opponentsGameGrid.invalidate();

    }

    private OnGameInteractionListener _onGameInteractionListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param gameResource Parameter 1.
     * @return A new instance of fragment GameScreenFragment.
     */
    public static GameScreenFragment newInstance(int gameResource)
    {
        GameScreenFragment fragment = new GameScreenFragment();
        Bundle args = new Bundle();
        args.putInt(GAME_RESOURCE, gameResource);
        fragment.setArguments(args);
        return fragment;
    }

    public GameScreenFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        _gameModel = GameModel.getInstance();
        _gameModel.loadGame(new File(getActivity().getFilesDir(), "game.txt").getPath());

        if(getArguments() != null && getArguments().containsKey(GAME_RESOURCE))
            _currentGameIndex = getArguments().getInt(GAME_RESOURCE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
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

        //Getting the current game to load into GameGridViews
        Game currentGame = _gameModel.getInstance().getGame(_currentGameIndex);

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
        attackButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(_currentGameIndex >= 0) {
                    if(_gameModel.getGame(_currentGameIndex).gameOver()){
                        _opponentsGameGrid.setSetAttackCoordListener(null);
                        _playersGameGrid.setSetAttackCoordListener(null);
                        attackButton.setText("Game Over");
                    }else {
                        if (attackButton.getText() == "Attack" && _xCoord != null && _yCoord != null) {

                            //making sure we had a valid game index
                            if (_currentGameIndex >= 0) {
                                //Adding the turn to the selected game
                                _gameModel.updateGame(_xCoord, _yCoord, _currentGameIndex);

                                _opponentsGameGrid.loadGameGrid(_gameModel.getGame(_currentGameIndex).getPlayerTwo(), false);
                                _playersGameGrid.loadGameGrid(_gameModel.getGame(_currentGameIndex).getPlayerOne(), false);
                                _opponentsGameGrid.setSetAttackCoordListener(null);
                                _playersGameGrid.setSetAttackCoordListener(null);
                                //Changing the ShowBoats boolean and click listener
                                _xText.setText(null);
                                _yText.setText(null);
                                _xCoord = null;
                                _yCoord = null;
                            }
                            attackButton.setText("Next Turn");
                            _onGameInteractionListener.onGameInteraction();
                        }
                        else if (attackButton.getText() == "Next Turn") {
                            attackButton.setText("Attack");
                            switchClickListeners(_playersGameGrid, _opponentsGameGrid, _currentGameIndex);
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

    public void switchClickListeners(GameGridView playersGameGrid, GameGridView opponentsGameGrid, int gameIndex){
        if(_gameModel.getGame(gameIndex).playerOnesTurn()){
            playersGameGrid.setShowBoats(true);
            opponentsGameGrid.setShowBoats(false);

            opponentsGameGrid.setSetAttackCoordListener(this);
            playersGameGrid.setSetAttackCoordListener(null);
        }else{
            playersGameGrid.setShowBoats(false);
            opponentsGameGrid.setShowBoats(true);

            playersGameGrid.setSetAttackCoordListener(this);
            opponentsGameGrid.setSetAttackCoordListener(null);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        _gameModel.saveGame(new File(getActivity().getFilesDir(), "game.txt").getPath());
    }

    @Override
    public void onResume()
    {

        super.onResume();
        _gameModel.loadGame(new File(getActivity().getFilesDir(), "game.txt").getPath());
    }

    //Called when the fragment has been associated with the activity (the Activity is passed in here).
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

    //Called when the fragment is being disassociated from the activity.
    @Override
    public void onDetach()
    {
        super.onDetach();
        //_onGameInteractionListener = null;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnGameInteractionListener
    {
        public void onGameInteraction();
    }
}
