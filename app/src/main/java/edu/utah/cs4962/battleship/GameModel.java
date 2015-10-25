package edu.utah.cs4962.battleship;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by kylehiroyasu on 10/10/2015.
 *
 * The game itself involves two grids positioned over locations in the ocean. One grid
 belongs to the player while the other belongs to his enemy. Each grid contains 5 ships that can
 be positioned in a row or column of the grid and have lengths of 2, 3, 3, 4, and 5 units. Players
 take turns launching missiles into individual grid locations in their enemy’s grid with the goal of
 sinking the opponent’s ships. When a missile is launched, the player is told wether the missile
 “hit” or “missed”. Some variations of the game also say on a “hit” if the ship was “sunk”, meaning
 that all of the locations the ship occupies have been hit, but our variation will not give that
 distinction. While both grids will contain hits and misses, each player may only see ships that
 are in their own grid. The game is won when all locations that the enemy’s ships cover have
 been “hit”.

 TODO: In current state this game model is not thread safe
 */
public class GameModel
{
    public static String GAME_INDEX_EXTRA = "game_index";

    private static GameModel _gameModel = null;

    //Game model will contain the following data
    //-List of games
    private List<Game> _games;

    //This empty method works to prevent multiple instances of GameModel
    protected GameModel(){
        _games = new ArrayList<>();
        _games.add(new Game());
        _games.add(new Game());
    }

    public static GameModel getInstance(){
        if(_gameModel == null) {
            _gameModel = new GameModel();
        }
        return _gameModel;
    }

    public void loadGame(String path){
        try{
            File gameFile = new File(path);
            FileReader fileReader = new FileReader(gameFile);
            BufferedReader reader = new BufferedReader(fileReader);
            String gamesJson = reader.readLine();

            Gson gson = new Gson();
            Type collectionType = new TypeToken<ArrayList<Game>>(){}.getType();
            _games = (ArrayList<Game>)gson.fromJson(gamesJson, collectionType);
        }catch (Exception e){
            Log.e("Persistence", "Failed to load game. Error: " +e.getMessage());
        }
    }

    public void saveGame(String path){
        Gson gson = new Gson();
        String jsonGame = gson.toJson(_games);
        try{
            FileWriter fileWriter = new FileWriter(path, false);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write(jsonGame);
            writer.close();

        }catch (Exception e){
            Log.e("Persistence", "Failed to save game. Error: " +e.getMessage());
        }
    }
    //This method will check if the current game is non null, save if true, and generate a new empty game
    // and return the index of the new game
    public int createGame(){
        _games.add(new Game());
        return _games.size() - 1;
    }

    //This method will get the game at the specified index
    public Game readGame(int index){
        return new Game(_games.get(index));
    }


    //This method will specify the game to be deleted
    //Need to check and make sure we arent deleting current game, otherwise index invalid
    public void deleteGame(int index){
        if(index < _games.size()){
            _games.remove(index);
        }
    }

    public int getGameCount(){
        return _games.size();
    }

    public Game getGame(int index){
        if(index < _games.size())
            return _games.get(index);
        return null;
    }

    public void updateGame(int x, int y, int index){
        Game tempGame = _games.get(index);
        tempGame.addTurn(x,y);
        _games.set(index, tempGame);
    }


}
