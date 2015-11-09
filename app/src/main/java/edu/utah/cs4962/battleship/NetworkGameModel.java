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
    //TODO: Remember to store and modify important current states for both fragments
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

            //Saving the ids
            _currenGameId = response.gameId;
            _currentPlayerId = response.playerId;

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

    public GameBoard getGameBoard(String gameId, String playerId){
        try {
            URL gameTurnUrl = new URL("http://battleship.pixio.com/api/games/"+gameId+"/board");
            HttpURLConnection gameTurnConnection = (HttpURLConnection) gameTurnUrl.openConnection();
            gameTurnConnection.addRequestProperty("Content-Type", "application/json");
            gameTurnConnection.setRequestMethod("POST");
            //JSONObject payload = new JSONObject()
            String payload = "{ \"playerId\" : \""+playerId+"\"}";

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
            if(playerId != _currentPlayerId && gameId != _currenGameId){
                throw new Exception("Player/Game id mismatch on update game in NetworkGameModel");
            }
            URL gameGuessUrl = new URL("http://battleship.pixio.com/api/games/"+gameId+"/guess");
            HttpURLConnection gameGuessConnection = (HttpURLConnection) gameGuessUrl.openConnection();
            gameGuessConnection.addRequestProperty("Content-Type", "application/json");
            gameGuessConnection.setRequestMethod("POST");
            //JSONObject payload = new JSONObject()
            String payload = "{ \"playerId\" : \""+playerId+"\",\"xPos\" : \""+x+"\", \"yPos\" : \""+y+"\"}";

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

            //Updating variables
            _currenGameId = gameId;
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
            Log.i("Response:", "Game response includes isYourTurn: " + response.isYourTurn + " Winner:" + response.winner);
            gameTurnConnection.disconnect();

            return  response;
        }catch (MalformedURLException e){
            Log.e("Connection", "Malformed URL");
        }catch (Exception e){
            Log.e("Persistence", "Failed to load game. Error: " + e);
        }
        return null;
    }

    public Game getGame(String gameId, String playerId){
        //TODO: Assuming i just want to save the gameId if i'm accessing this game. correct assumption?
        _currenGameId = gameId;
        _currentPlayerId = playerId;

        GameBoard boards = getGameBoard(gameId, playerId);

        List<Game.GridPoint> playerHits = new ArrayList<>();
        List<Game.GridPoint> playerMisses = new ArrayList<>();
        List<Game.GridPoint> playerBoats = new ArrayList<>();

        List<Game.GridPoint> oppHits = new ArrayList<>();
        List<Game.GridPoint> oppMisses = new ArrayList<>();
        List<Game.GridPoint> oppBoats = new ArrayList<>();

        for(int i = 0; i < boards.playerBoard.length; i++){
            if(boards.playerBoard[i].status == "HIT"){
                //TODO: MAKE SURE THIS WIZARD SHIT WORKS
                playerHits.add(new Game().new GridPoint(boards.playerBoard[i].xPos,boards.playerBoard[i].yPos));
                //Need to count it as a boat too!
                playerBoats.add(new Game().new GridPoint(boards.playerBoard[i].xPos,boards.playerBoard[i].yPos));
            }else if(boards.playerBoard[i].status == "MISS"){
                playerMisses.add(new Game().new GridPoint(boards.playerBoard[i].xPos,boards.playerBoard[i].yPos));
            }else if(boards.playerBoard[i].status == "SHIP"){
                playerBoats.add(new Game().new GridPoint(boards.playerBoard[i].xPos, boards.playerBoard[i].yPos));
            }else if(boards.playerBoard[i].status == "NONE"){

            }
        }

        for(int i = 0; i < boards.opponentBoard.length; i++){
            if(boards.opponentBoard[i].status == "HIT"){
                oppHits.add(new Game().new GridPoint(boards.opponentBoard[i].xPos,boards.opponentBoard[i].yPos));
                oppBoats.add(new Game().new GridPoint(boards.opponentBoard[i].xPos,boards.opponentBoard[i].yPos));
            }else if(boards.opponentBoard[i].status == "MISS"){
                oppMisses.add(new Game().new GridPoint(boards.opponentBoard[i].xPos,boards.opponentBoard[i].yPos));
            }else if(boards.opponentBoard[i].status == "SHIP"){
                oppBoats.add(new Game().new GridPoint(boards.opponentBoard[i].xPos,boards.opponentBoard[i].yPos));
            }else if(boards.opponentBoard[i].status == "NONE"){

            }
        }

        //Generating game grids for each player
        Game.GameGrid playerGrid = new Game().new GameGrid(playerMisses,playerHits,playerBoats);
        Game.GameGrid oppGrid = new Game().new GameGrid(oppMisses,oppHits,oppBoats);

        //Determining if game is over
        boolean gameOver = (playerGrid.allBoatsDestroyed() || oppGrid.allBoatsDestroyed());

        Game game = new Game();
        game.loadGame(playerGrid, oppGrid, gameOver);
        //returning game
        return game;
    }

}
