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
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/*
A screen containing a list view that allows listing of games in-progress
or ended that opens the game when an item is tapped.

The item should note:
 if the game is in progress or if it has ended
 who’s turn it is in that game (or has ended)
 how many missiles have been launched by each player.
 Games can be started from this screen by pressing a “new game” button or other appropriate control
 */
public class GameListActivity extends AppCompatActivity implements ListAdapter
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        String[] drawingNames = {"Game1","Game2","Game3"};
        ArrayAdapter<String> Games = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, drawingNames);

        ListView gameListView = new ListView(this);
        gameListView.setAdapter(this);
        setContentView(gameListView);

        gameListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent openGameActivityIntent = new Intent();
                openGameActivityIntent.setClass(GameListActivity.this, GameScreenActivity.class);
                //openGameActivityIntent.putExtra(GameScreenActivity.GAME_INDEX_EXTRA, (int) id);
                GameListActivity.this.startActivity(openGameActivityIntent);
            }
        });

        setContentView(gameListView);
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
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView drawingTitle = new TextView(this);
        int padding = (int)(10* getResources().getDisplayMetrics().density);
        drawingTitle.setPadding(padding,padding,padding,padding);
        drawingTitle.setText(game.toString());
        rowLayout.addView(drawingTitle, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 0));

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
