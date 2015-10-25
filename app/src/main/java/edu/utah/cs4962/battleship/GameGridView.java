package edu.utah.cs4962.battleship;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import java.util.Iterator;

/**
 * Created by kylehiroyasu on 10/11/2015.
 */
public class GameGridView extends View
{
    public interface SetAttackCoordListener
    {
        void SetAttackCoord(int x, int y);
    }
    SetAttackCoordListener _setAttackCoordListener = null;

    public SetAttackCoordListener getSetAttackCoordListener()
    {
        return _setAttackCoordListener;
    }

    public void setSetAttackCoordListener(SetAttackCoordListener setAttackCoordListener)
    {
        _setAttackCoordListener = setAttackCoordListener;
    }

    Game.GameGrid _grid;
    boolean _showBoats;

    public boolean isShowBoats()
    {
        return _showBoats;
    }

    public void setShowBoats(boolean showBoats)
    {
        _showBoats = showBoats;
        invalidate();
    }

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
            float size = Math.min(getWidth(), getHeight())/10;
            Paint cellPaint = new Paint();

            for(int row = 0; row < 10; row++){
                for(int col = 0; col < 10; col++){
                    //Filling inside
                    cellPaint.setStyle(Paint.Style.FILL);
                    cellPaint.setColor(Color.BLUE);
                    canvas.drawRect(col*size, row*size, (col+1)*size, (row+1)*size, cellPaint);
                    //Drawing the border
                    cellPaint.setStyle(Paint.Style.STROKE);
                    cellPaint.setStrokeWidth(size / 10);
                    cellPaint.setColor(Color.BLACK);
                    canvas.drawRect(col*size, row*size, (col+1)*size, (row+1)*size, cellPaint);
                }

            }
            if(_showBoats) {
                Iterator boatLocations = _grid.getBoats().iterator();
                while (boatLocations.hasNext()) {
                    Game.GridPoint point = (Game.GridPoint) boatLocations.next();
                    //Fill
                    cellPaint.setStyle(Paint.Style.FILL);
                    cellPaint.setColor(Color.GRAY);
                    canvas.drawRect(point.x * size, point.y * size, (point.x + 1) * size, (point.y + 1) * size, cellPaint);
                    //Stroke
                    cellPaint.setStyle(Paint.Style.STROKE);
                    cellPaint.setStrokeWidth(size/10);
                    cellPaint.setColor(Color.BLACK);
                    canvas.drawRect(point.x * size, point.y * size, (point.x + 1) * size, (point.y + 1) * size, cellPaint);
                }
            }

            Iterator missesLocations = _grid.getMisses().iterator();
            while(missesLocations.hasNext()){
                Game.GridPoint point = (Game.GridPoint)missesLocations.next();

                cellPaint.setStyle(Paint.Style.FILL);
                cellPaint.setColor(Color.WHITE);
                canvas.drawRect(point.x * size, point.y * size, (point.x + 1) * size, (point.y + 1) * size, cellPaint);

                cellPaint.setStyle(Paint.Style.STROKE);
                cellPaint.setStrokeWidth(size/10);
                cellPaint.setColor(Color.BLACK);
                canvas.drawRect(point.x*size, point.y*size, (point.x+1)*size, (point.y+1)*size, cellPaint);
            }

            Iterator hitLocations = _grid.getHits().iterator();
            while(hitLocations.hasNext()){
                Game.GridPoint point = (Game.GridPoint)hitLocations.next();

                cellPaint.setStyle(Paint.Style.FILL);
                cellPaint.setColor(Color.RED);
                canvas.drawRect(point.x*size, point.y*size, (point.x+1)*size, (point.y+1)*size, cellPaint);

                cellPaint.setStyle(Paint.Style.STROKE);
                cellPaint.setColor(Color.BLACK);
                canvas.drawRect(point.x*size, point.y*size, (point.x+1)*size, (point.y+1)*size, cellPaint);
            }

        }
    }

    public void loadGameGrid(Game.GameGrid grid, boolean showBoats){
        _grid = grid;
        _showBoats = showBoats;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        //Only acknowledge the touch up event if boats are hidden =
        if(event.getActionMasked()== MotionEvent.ACTION_UP && _setAttackCoordListener != null) {
            //ontouchup event
            //get x and y
            float x = event.getX();
            float y = event.getY();
            float size = Math.min(getWidth(), getHeight())/10;
            //find the grid point they touched
            int xCoord = (int) Math.floor(x/size);
            int yCoord = (int) Math.floor(y/size);
            //Add to model
            _setAttackCoordListener.SetAttackCoord(xCoord, yCoord);
        }
        return true;
    }
}
