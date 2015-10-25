package edu.utah.cs4962.battleship;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link edu.utah.cs4962.battleship.GameListFragment.OnGameSelectedListener} interface
 * to handle interaction events.
 * Use the {@link GameListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameListFragment extends Fragment implements ListAdapter
{

    private OnGameSelectedListener _onGameSelectedListener = null;

    ListView _rootView;
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
    public interface OnGameSelectedListener
    {
        public void onGameSelected(int gameId);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GameListFragment.
     */
    public static GameListFragment newInstance()
    {
        GameListFragment fragment = new GameListFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    public GameListFragment()
    {
        // Required empty public constructor
    }

    public void invalidateRows(){
        _rootView.invalidateViews();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        _rootView = new ListView(getActivity());
        _rootView.setBackgroundColor(Color.GRAY);
        _rootView.setAdapter(this);
        _rootView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                _onGameSelectedListener.onGameSelected((position));
            }
        });
        return  _rootView;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try {
            _onGameSelectedListener = (OnGameSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnGameInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        _onGameSelectedListener = null;
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

        LinearLayout rowLayout = new LinearLayout(getActivity());
        rowLayout.setOrientation(LinearLayout.VERTICAL);
        //Game number
        TextView drawingTitle = new TextView(getActivity());
        int padding = (int)(5* getResources().getDisplayMetrics().density);
        drawingTitle.setPadding(padding,padding,padding,padding);
        drawingTitle.setText("Game " + position);

        //if in progress
        TextView gameProgress = new TextView(getActivity());
        gameProgress.setPadding(padding,padding,padding,padding);
        String progress = (game.gameOver()) ? "Game Over" : "In Progress";
        gameProgress.setText("Game State: "+progress);

        //Whose turn it is
        TextView whoseTurn = new TextView(getActivity());
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
        TextView missilesLaunched = new TextView(getActivity());
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
    }



}
