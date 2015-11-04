package edu.utah.cs4962.battleship;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by kylehiroyasu on 11/1/2015.
 */
public class NetworkGameModel
{
    private class GameDetail{
        public String id;
        public String name;
        public String player1;
        public String player2;
        public String winner;
        public Integer missilesLaunched;
    }
    public class GameSummary{
        public String id;
        public String name;
        public String status;
    }
    private class GameJoinResponse{
        public String playerId;
    }
    private class GameCreateResponse{
        public String playerId;
        public String gameId;
    }
    private class GameGuessResponse{
        public boolean hit;
        public int shipSunk;
    }
    private class GameTurnResponse{
        public boolean isYourTurn;
        public String winner;
    }
    private class GameCoord{
        public int xPos;
        public int yPos;
        public String status;
    }
    private class GameBoard{
        public GameCoord[] playerBoard;
        public  GameCoord[] opponentBoard;
    }

    public static String GAME_INDEX_EXTRA = "game_index";

    //I should store useful variables like:
    String _currenGameId;
    public String getCurrenGameId()
    {
        return _currenGameId;
    }
    public void setCurrenGameId(String currenGameId)
    {
        _currenGameId = currenGameId;
    }

    String _currentPlayerId;
    public String getCurrentPlayerId()
    {
        return _currentPlayerId;
    }
    public void setCurrentPlayerId(String currentPlayerId)
    {
        _currentPlayerId = currentPlayerId;
    }

    GameSummary[] _gameSummaries;
    public GameSummary[] getGameSummaries()
    {
        return _gameSummaries;
    }
    public void setGameSummaries(GameSummary[] gameSummaries)
    {
        _gameSummaries = gameSummaries;
    }

    private static NetworkGameModel _gameModel = null;

    //Game model will contain the following data
    //-List of games
    private List<Game> _games;

    //This empty method works to prevent multiple instances of GameModel
    protected NetworkGameModel(){
        _games = new ArrayList<>();
        _games.add(new Game());
    }

    public static NetworkGameModel getInstance(){
        if(_gameModel == null) {
            _gameModel = new NetworkGameModel();
        }
        return _gameModel;
    }

    public void loadGames(){
        try {
            URL url = new URL("http://battleship.pixio.com/api/games/");
            HttpURLConnection gameListConnection = (HttpURLConnection)url.openConnection();
            if(gameListConnection.getResponseCode() < 200 || gameListConnection.getResponseCode() >= 300)
                throw new Exception("Error response code");
            Scanner ResponseScanner = new Scanner(gameListConnection.getInputStream());

            //Could check to make sure connection is a content type
            gameListConnection.getHeaderField("Content-Type");

            StringBuilder responseString = new StringBuilder();
            while(ResponseScanner.hasNext()){
                responseString.append(ResponseScanner.nextLine());

            }
            String response = responseString.toString();

            Gson gson = new Gson();
            GameSummary[] gameSummaries = gson.fromJson(response, GameSummary[].class);
            _gameSummaries = gameSummaries;
        }catch (MalformedURLException e){
            Log.e("Connection", "Malformed URL");
        }catch (Exception e){
            Log.e("Persistence", "Failed to load game. Error: " + e.getMessage());
        }
    }

    public void saveGame(String id){
    }
    //This method will check if the current game is non null, save if true, and generate a new empty game
    // and return the index of the new game
    public GameCreateResponse createGame(String gameName, String playerName){
        try {
            URL gameCreateUrl = new URL("http://battleship.pixio.com/api/games/");
            HttpURLConnection gameCreateConnection = (HttpURLConnection) gameCreateUrl.openConnection();
            gameCreateConnection.addRequestProperty("Content-Type", "application/json");
            //gameCreateConnection.setRequestMethod("POST");
            //JSONObject payload = new JSONObject()
            String payload = "{ \"playerName\" : \""+playerName+"\",\"gameName\" : \""+gameName+"\"}";
            Log.i("Create Game", "create game PAYLOAD: "+payload);

            gameCreateConnection.setDoOutput(true);
            OutputStreamWriter payloadStream = new OutputStreamWriter(gameCreateConnection.getOutputStream());
            payloadStream.write(payload);
            payloadStream.flush();
            payloadStream.close();

            int code = gameCreateConnection.getResponseCode();

            Scanner gameCreateRepsonseScanner = new Scanner(gameCreateConnection.getInputStream());
            StringBuilder gameCreateResponseStringBuilder = new StringBuilder();
            while(gameCreateRepsonseScanner.hasNext())
                gameCreateResponseStringBuilder.append(gameCreateRepsonseScanner.nextLine());
            Gson gson = new Gson();
            GameCreateResponse response = gson.fromJson(gameCreateResponseStringBuilder.toString(), GameCreateResponse.class);
            Log.i("Response:", "Game response includes gameId: " + response.gameId + " playerId: " + response.playerId);
            gameCreateConnection.disconnect();

            return response;

        }catch (MalformedURLException e){
            Log.e("Connection", "Malformed URL");
        }catch (Exception e){
            Log.e("Persistence", "Failed to load game. Error: " + e.getMessage());
        }
        return null;
    }


    //This method will specify the game to be deleted
    //Need to check and make sure we arent deleting current game, otherwise index invalid
    public void deleteGame(int index){
        if(index < _games.size()){
            _games.remove(index);
        }
    }

    public int getGameCount(){
        try {
            URL url = new URL("http://battleship.pixio.com/api/games/");
            HttpURLConnection gameListConnection = (HttpURLConnection)url.openConnection();
            if(gameListConnection.getResponseCode() < 200 || gameListConnection.getResponseCode() >= 300)
                throw new Exception("Error response code");
            Scanner ResponseScanner = new Scanner(gameListConnection.getInputStream());

            //Could check to make sure connection is a content type
            gameListConnection.getHeaderField("Content-Type");

            StringBuilder responseString = new StringBuilder();
            while(ResponseScanner.hasNext()){
                responseString.append(ResponseScanner.nextLine());

            }
            String response = responseString.toString();

            Gson gson = new Gson();
            GameSummary[] gameSummaries = gson.fromJson(response, GameSummary[].class);
            return gameSummaries.length;
        }catch (MalformedURLException e){
            Log.e("Connection", "Malformed URL");
        }catch (Exception e){
            Log.e("Persistence", "Failed to load game. Error: " + e.getMessage());
        }
        return -1;
    }

    public GameBoard getGame(String gameId, String playerId){
        try {
            URL gameTurnUrl = new URL("http://battleship.pixio.com/api/games/"+gameId+"/board");
            HttpURLConnection gameTurnConnection = (HttpURLConnection) gameTurnUrl.openConnection();
            gameTurnConnection.addRequestProperty("Content-Type", "application/json");
            gameTurnConnection.setRequestMethod("POST");
            //JSONObject payload = new JSONObject()
            String payload = "{ \"playerId\" : \""+playerId+"\"}";
            //Todo: Read

            gameTurnConnection.setDoOutput(true);
            OutputStreamWriter payloadStream = new OutputStreamWriter(gameTurnConnection.getOutputStream());
            payloadStream.write(payload);
            payloadStream.flush();
            payloadStream.close();

            int code = gameTurnConnection.getResponseCode();

            Scanner gameTurnRepsonseScanner = new Scanner(gameTurnConnection.getInputStream());
            StringBuilder gameJoinResponseStringBuilder = new StringBuilder();
            while(gameTurnRepsonseScanner.hasNext())
                gameJoinResponseStringBuilder.append(gameTurnRepsonseScanner.nextLine());
            Gson gson = new Gson();
            GameBoard response = gson.fromJson(gameJoinResponseStringBuilder.toString(), GameBoard.class);
            Log.i("Response:", "Game response includes isYourTurn: " + response);
            gameTurnConnection.disconnect();
            return response;
        }catch (MalformedURLException e){
            Log.e("Connection", "Malformed URL");
        }catch (Exception e){
            Log.e("Persistence", "Failed to load game. Error: " + e.getMessage());
        }
        return null;
    }

    public GameGuessResponse updateGame(int x, int y, String playerId, String gameId){
        try {
            URL gameGuessUrl = new URL("http://battleship.pixio.com/api/games/"+gameId+"/guess");
            HttpURLConnection gameGuessConnection = (HttpURLConnection) gameGuessUrl.openConnection();
            gameGuessConnection.addRequestProperty("Content-Type", "application/json");
            gameGuessConnection.setRequestMethod("POST");
            //JSONObject payload = new JSONObject()
            String payload = "{ \"playerId\" : \""+playerId+"\",\"xPos\" : \""+x+"\", \"yPos\" : \""+y+"\"}";
            //Todo: Read

            gameGuessConnection.setDoOutput(true);
            OutputStreamWriter payloadStream = new OutputStreamWriter(gameGuessConnection.getOutputStream());
            payloadStream.write(payload);
            payloadStream.flush();
            payloadStream.close();

            //TODO: Check the code, if it is not in correct range then there is a problem, may not be turn,
            int code = gameGuessConnection.getResponseCode();

            Scanner gameCreateRepsonseScanner = new Scanner(gameGuessConnection.getInputStream());
            StringBuilder gameCreateResponseStringBuilder = new StringBuilder();
            while(gameCreateRepsonseScanner.hasNext())
                gameCreateResponseStringBuilder.append(gameCreateRepsonseScanner.nextLine());
            Gson gson = new Gson();
            GameGuessResponse response = gson.fromJson(gameCreateResponseStringBuilder.toString(), GameGuessResponse.class);
            Log.i("Response:", "Game response includes hit: "+response.hit+" shipSunk: "+response.shipSunk);
            gameGuessConnection.disconnect();

            return response;
        }catch (MalformedURLException e){
            Log.e("Connection", "Malformed URL");
        }catch (Exception e){
            Log.e("Persistence", "Failed to load game. Error: " + e);
        }

        return null;
    }

    public GameDetail getGameDetail(String id){
        try {

            URL url = new URL("http://battleship.pixio.com/api/games/"+id);
            HttpURLConnection gameListConnection = (HttpURLConnection)url.openConnection();
            if(gameListConnection.getResponseCode() < 200 || gameListConnection.getResponseCode() >= 300)
                throw new Exception("Error response code");
            Scanner ResponseScanner = new Scanner(gameListConnection.getInputStream());

            //Could check to make sure connection is a content type
            gameListConnection.getHeaderField("Content-Type");

            StringBuilder responseString = new StringBuilder();
            while(ResponseScanner.hasNext()){
                responseString.append(ResponseScanner.nextLine());

            }
            String response = responseString.toString();

            Gson gson = new Gson();
            GameDetail gameDetail =gson.fromJson(response, GameDetail.class);
            Log.i("GameSummary", "Game id: " + gameDetail.id + " Game players:" + gameDetail.player1 + " && " + gameDetail.player2);
            return gameDetail;
        }catch (MalformedURLException e){
            Log.e("Connection", "Malformed URL");
        }catch (Exception e){
            Log.e("Persistence", "Failed to load game. Error: " + e);
        }
        return null;
    }

    public String joinGame(String gameId, String playerName){
        try {
            URL gameJoinUrl = new URL("http://battleship.pixio.com/api/games/"+gameId+"/join");
            HttpURLConnection gameJoinConnection = (HttpURLConnection) gameJoinUrl.openConnection();
            gameJoinConnection.addRequestProperty("Content-Type", "application/json");
            gameJoinConnection.setRequestMethod("POST");
            //JSONObject payload = new JSONObject()
            String payload = "{ \"playerName\" : \""+playerName+"\"}";
            //Todo: Read

            gameJoinConnection.setDoOutput(true);
            OutputStreamWriter payloadStream = new OutputStreamWriter(gameJoinConnection.getOutputStream());
            payloadStream.write(payload);
            payloadStream.flush();
            payloadStream.close();

            int code = gameJoinConnection.getResponseCode();

            Scanner gameCreateRepsonseScanner = new Scanner(gameJoinConnection.getInputStream());
            StringBuilder gameJoinResponseStringBuilder = new StringBuilder();
            while(gameCreateRepsonseScanner.hasNext())
                gameJoinResponseStringBuilder.append(gameCreateRepsonseScanner.nextLine());
            Gson gson = new Gson();
            GameJoinResponse response = gson.fromJson(gameJoinResponseStringBuilder.toString(), GameJoinResponse.class);
            Log.i("Response:", "Game response includes playerId: " + response.playerId);
            gameJoinConnection.disconnect();
            _currentPlayerId = response.playerId;
            return  response.playerId;
        }catch (MalformedURLException e){
            Log.e("Connection", "Malformed URL");
        }catch (Exception e){
            Log.e("Persistence", "Failed to load game. Error: " + e);
        }
        return null;

    }

    public GameTurnResponse whoseTurn(String gameId, String playerId){
        try {
            URL gameTurnUrl = new URL("http://battleship.pixio.com/api/games/"+gameId+"/status");
            HttpURLConnection gameTurnConnection = (HttpURLConnection) gameTurnUrl.openConnection();
            gameTurnConnection.addRequestProperty("Content-Type", "application/json");
            gameTurnConnection.setRequestMethod("POST");
            //JSONObject payload = new JSONObject()
            String payload = "{ \"playerId\" : \""+playerId+"\"}";
            //Todo: Read

            gameTurnConnection.setDoOutput(true);
            OutputStreamWriter payloadStream = new OutputStreamWriter(gameTurnConnection.getOutputStream());
            payloadStream.write(payload);
            payloadStream.flush();
            payloadStream.close();

            int code = gameTurnConnection.getResponseCode();

            Scanner gameTurnRepsonseScanner = new Scanner(gameTurnConnection.getInputStream());
            StringBuilder gameJoinResponseStringBuilder = new StringBuilder();
            while(gameTurnRepsonseScanner.hasNext())
                gameJoinResponseStringBuilder.append(gameTurnRepsonseScanner.nextLine());
            Gson gson = new Gson();
            GameTurnResponse response = gson.fromJson(gameJoinResponseStringBuilder.toString(), GameTurnResponse.class);
            Log.i("Response:", "Game response includes isYourTurn: " + response.isYourTurn + " Winner:"+response.winner);
            gameTurnConnection.disconnect();

            return  response;
        }catch (MalformedURLException e){
            Log.e("Connection", "Malformed URL");
        }catch (Exception e){
            Log.e("Persistence", "Failed to load game. Error: " + e);
        }
        return null;
    }


}
