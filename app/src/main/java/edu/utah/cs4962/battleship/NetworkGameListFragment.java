package edu.utah.cs4962.battleship;

import android.app.Activity;
import android.app.Fragment;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by kylehiroyasu on 11/3/2015.
 */
public class NetworkGameListFragment extends Fragment implements ListAdapter
{
    ListView _rootView;

    NetworkGameModel.GameSummary[] summaries;

    //This listener/interface will control what screen to load when a game is selected
    private OnGameSelectedListener _onGameSelectedListener = null;

    public interface OnGameSelectedListener
    {
        public void onGameSelected(String gameId);
    }

    public static NetworkGameListFragment newInstance(){
        NetworkGameListFragment fragment = new NetworkGameListFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    public NetworkGameListFragment(){

}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        _rootView = new ListView(getActivity());
        _rootView.setBackgroundColor(Color.GRAY);
        _rootView.setAdapter(this);
        _rootView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                for(int i = 0; i < parent.getChildCount(); i++){
                    parent.getChildAt(i).setBackgroundColor(Color.GRAY);
                }
                view.setBackgroundColor(Color.LTGRAY);

                //TODO: This should be right...
                _onGameSelectedListener.onGameSelected(summaries[position].id);
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

    @Override
    public boolean isEmpty()
    {
        return getCount() > 0;
    }

    @Override
    public int getCount()
    {
        AsyncTask<Integer, Integer, Integer> sync = new AsyncTask<Integer, Integer, Integer>()
        {
            @Override
            protected Integer doInBackground(Integer... params)
            {
                return NetworkGameModel.getInstance().getGameCount();
            }
        };

        try{
            sync.execute(null,null,null);
            return sync.get();
        }catch (Exception e){
            Log.e("Exception getting Count", "Exception: " + e + " Message:" + e.getMessage());
        }
        return -1;
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
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //Creating async Task
        AsyncTask<Integer,Integer, NetworkGameModel.GameSummary[]> sync = new AsyncTask<Integer, Integer, NetworkGameModel.GameSummary[]>()
        {
            @Override
            protected NetworkGameModel.GameSummary[] doInBackground(Integer... params)
            {
                NetworkGameModel.getInstance().loadGames();
                return NetworkGameModel.getInstance().getGameSummaries();
            }
        };
        //Executing asyncTask and capturing results
        try{
            sync.execute(null, null,null);
            summaries = sync.get();

            LinearLayout rowLayout = new LinearLayout(getActivity());
            rowLayout.setOrientation(LinearLayout.VERTICAL);
            //Game number
            TextView drawingTitle = new TextView(getActivity());
            int padding = (int)(5* getResources().getDisplayMetrics().density);
            drawingTitle.setPadding(padding,padding,padding,padding);
            drawingTitle.setText("Name: " + summaries[position].name);

            //if in progress
            TextView gameProgress = new TextView(getActivity());
            gameProgress.setPadding(padding, padding, padding, padding);
            gameProgress.setText("Game State: " + summaries[position].status);

            //if in progress
            TextView gameId = new TextView(getActivity());
            gameId.setPadding(padding, padding, padding, padding);
            gameId.setText(summaries[position].id);
            gameId.setVisibility(View.GONE);

            rowLayout.addView(drawingTitle, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 0));
            rowLayout.addView(gameProgress, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 0));
            rowLayout.addView(gameId, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 0));


            return rowLayout;
        }catch (Exception e){
            Log.e("Exc. Loading Summaries", "Exception: "+e+" Message:"+e.getMessage());
        }
        TextView text = new TextView(getActivity());
        text.setText("ERROR");
        return text;
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

    public void invalidateRows(){
        _rootView.invalidateViews();
    }
}
