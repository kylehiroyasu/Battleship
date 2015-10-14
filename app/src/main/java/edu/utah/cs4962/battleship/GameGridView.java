package edu.utah.cs4962.battleship;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.Iterator;

/**
 * Created by kylehiroyasu on 10/11/2015.
 */
public class GameGridView extends View
{
    Game.GameGrid _grid;
    boolean _showBoats;

    public GameGridView(Context context)
    {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {

        super.onDraw(canvas);
        //If game does is nonnull we should draw the game grid
        if(_grid != null){
            float size = Math.max(getWidth(), getHeight())/10;


            for(int row = 0; row < 10; row++){
                for(int col = 0; col < 10; col++){

                    Paint cellPaint = new Paint();
                    cellPaint.setColor(Color.BLUE);
                    canvas.drawRect(col*size, row*size, (col+1)*size, (row+1)*size, cellPaint);
                }

            }
            if(_showBoats) {
                Iterator boatLocations = _grid.getBoats().iterator();
                while (boatLocations.hasNext()) {
                    Game.GridPoint point = (Game.GridPoint) boatLocations.next();
                    Paint cellPaint = new Paint();
                    cellPaint.setColor(Color.GRAY);
                    canvas.drawRect(point.x * size, point.y * size, (point.x + 1) * size, (point.y + 1) * size, cellPaint);
                }
            }

            Iterator missesLocations = _grid.getMisses().iterator();
            while(missesLocations.hasNext()){
                Game.GridPoint point = (Game.GridPoint)missesLocations.next();
                Paint cellPaint = new Paint();
                cellPaint.setColor(Color.WHITE);
                canvas.drawRect(point.x*size, point.y*size, (point.x+1)*size, (point.y+1)*size, cellPaint);
            }

            Iterator hitLocations = _grid.getBoats().iterator();
            while(hitLocations.hasNext()){
                Game.GridPoint point = (Game.GridPoint)hitLocations.next();
                Paint cellPaint = new Paint();
                cellPaint.setColor(Color.RED);
                canvas.drawRect(point.x*size, point.y*size, (point.x+1)*size, (point.y+1)*size, cellPaint);
            }

        }
    }

    public void loadGameGrid(Game.GameGrid grid, boolean showBoats){
        _grid = grid;
        _showBoats = showBoats;
    }
}
