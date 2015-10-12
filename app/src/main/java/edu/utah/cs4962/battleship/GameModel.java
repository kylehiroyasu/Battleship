package edu.utah.cs4962.battleship;

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
    private static GameModel _gameModel = null;

    //Game model will contain the following data
    //-List of games
    //-Current game
    List<Game> _games;
    Game _currentGame = null;

    //This empty method works to prevent multiple instances of GameModel
    protected GameModel(){
    }

    public static GameModel getInstance(){
        if(_gameModel == null) {
            _gameModel = new GameModel();
        }
        return _gameModel;
    }

    //This method will check if the current game is non null, save if true, and generate a new empty game
    public void createGame(){
        if(_currentGame == null){
            _currentGame = new Game();
        }else{
            _games.add(new Game(_currentGame));
            _currentGame = new Game();
        }
    }

    //This method will get the game at the specified index and set it to _currentGame
    public Game readGame(int index){
        if(index < _games.size()){
            return new Game(_games.get(index));
        }
        else return new Game(_currentGame);
    }

    //TODO: This assumes we always want to update currentGame, is this assumption wrong?
    public void updateGame(int x, int y){
        _currentGame.addTurn(x, y);
    }

    //This method will specify the game to be deleted
    public void deleteGame(int index){
        if(index < _games.size()){
            _games.remove(index);
        }
        else _currentGame = null;
    }

    /*Each game should have the following:
        -each with 2 game grids
            -one to indicate where they have hit and missed opponents ships
            -one to indicate their own ship and when it has been or missed on board
        -game state
    Local Variables:
    GameGrid _playerOne : Grid containing location of player1's positions and attacks on their boats
    GameGrid _playerTwo : Grid containing location of player2's positions and attacks on their boats
    boolean _inProgess : indicates whether or not the game has been completed


    */
    public class Game{
        private boolean _gameOver;
        private GameGrid _playerOne;
        private GameGrid _playerTwo;

        public boolean gameOver()
        {
            return _gameOver;
        }

        private void isGameOver(boolean state){ _gameOver = state;}

        public GameGrid getPlayerOne()
        {
            return _playerOne;
        }

        public void setPlayerOne(GameGrid playerOne)
        {
            _playerOne = playerOne;
        }

        public GameGrid getPlayerTwo()
        {
            return _playerTwo;
        }

        public void setPlayerTwo(GameGrid playerTwo)
        {
            _playerTwo = playerTwo;
        }

        public Game(){
            _gameOver = false;
            _playerOne = new GameGrid();
            _playerTwo = new GameGrid();
        }

        public Game(Game game){
            _gameOver = game.gameOver();
            _playerOne = game.getPlayerOne();
            _playerTwo = game.getPlayerTwo();
        }

        public void loadGame(GameGrid playerOne, GameGrid playerTwo, boolean progress){
            _gameOver = progress;
            _playerOne = playerOne;
            _playerTwo = playerTwo;
        }

        public void addTurn(int x, int y){
            if(playerOnesTurn()){
                _playerTwo.addMove(x, y);
                isGameOver(_playerTwo.allBoatsDestroyed());
            }else{
                _playerOne.addMove(x, y);
                isGameOver(_playerOne.allBoatsDestroyed());
            }
        }

        public boolean playerOnesTurn(){
            if(_playerOne.hasBeenAttacked() == 0 && _playerTwo.hasBeenAttacked() == 0){
                //If this loop is entered it is the first more and playerOne gets to start
                return true;
            }else if(_playerOne.hasBeenAttacked() < _playerTwo.hasBeenAttacked()){
                //PlayerOne has been attacked less ==> player one has made more moves than playerTwo
                //==> this is player Twos turn
                return false;
            }else{
                //Otherwise
                return true;

            }

        }

    }

    /*
    Game grid is constructed with empty array of arrays representing game board
    The game board will then construct a random arrangement of valid boat
    Will contain method which evaluates whether or not the current move was a hit or miss

    Local Variables:
    _misses : List of points that have been misses
    _hits : List of points that have were hits
    _boats: List of points where boats are located
     */
    public class GameGrid
    {
        private List<GridPoint> _misses;
        private List<GridPoint> _hits;
        private List<GridPoint> _boats;

        public GameGrid(){
            _misses = new ArrayList<>();
            _hits = new ArrayList<>();
            _boats = new ArrayList<>();
            generateBoatLocations();
        }

        private void generateBoatLocations(){
            int[] boatSizes = {5,4,3,3,2};
            List<GridPoint> tempBoatLocations = new ArrayList<>();
            Random random = new Random();
            boolean allBoatsPlaced = false;
            int boatNumber = 0;
            while(!allBoatsPlaced){
                //NOTE: I subtract 1 from the size ofeach boat to make it zero based and more convenient to work with
                int currentBoat = boatSizes[boatNumber] - 1;
                int currentX = random.nextInt(10);
                int currentY = random.nextInt(10);
                int orientation = random.nextInt(2);
                if(orientation == 0 && currentX + currentBoat < 10){
                    //Set the position of the boat to this span of coordinates
                    for(int tempX = currentX; tempX <= currentX + currentBoat; tempX++){
                        //Save coordinates to tempBoarLocation
                        tempBoatLocations.add(new GridPoint(tempX,currentY));
                    }
                    //increment boatNumber
                    boatNumber++;
                }else if(orientation == 0 && currentX - currentBoat >= 0){
                    //Set the position of the boat to this span of coordinates
                    for(int tempX = currentX - currentBoat; tempX <= currentX; tempX++){
                        //Save coordinates to tempBoarLocation
                        tempBoatLocations.add(new GridPoint(tempX,currentY));
                    }
                    //increment boatNumber
                    boatNumber++;
                }else if(orientation == 1 && currentY + currentBoat < 10){
                    //Set the position of the boat to this span of coordinates
                    for(int tempY = currentY; tempY <= currentY + currentBoat; tempY++){
                        //Save coordinates to tempBoarLocation
                        tempBoatLocations.add(new GridPoint(currentX,tempY));
                    }
                    //increment boatNumber
                    boatNumber++;
                }else if(orientation == 1 && currentY - currentBoat >= 0){
                    //Set the position of the boat to this span of coordinates
                    for(int tempY = currentY - currentBoat; tempY <= currentY; tempY++){
                        //Save coordinates to tempBoarLocation
                        tempBoatLocations.add(new GridPoint(currentX,tempY));
                    }
                    //increment boatNumber
                    boatNumber++;
                }
                if(boatNumber == 5)
                    allBoatsPlaced = true;
            }
            _boats = tempBoatLocations;
        }

        /*
        This method tracks the hits or misses this game grid has received
        It then returns true if the attack was  a hit and false otherwise
         */
        public boolean addMove(int x, int y){
            GridPoint move = new GridPoint(x,y);
            Iterator<GridPoint> iterator = _boats.iterator();
            while(iterator.hasNext()){
                GridPoint boatLocation = iterator.next();
                if(boatLocation.isEqual(move))
                    _hits.add(move);
                    return true;
            }
            _misses.add(move);
            return false;
        }

        public int hasBeenAttacked(){
            return _hits.size() + _misses.size();
        }

        public boolean allBoatsDestroyed(){
            if(_hits.size() == _boats.size())
                return true;
            return false;
        }


    }

    /*
    Gridpoint will represent a point on the GameGrid object, this class wil be responsible for storing
     */
    public class  GridPoint{
        public int x;
        public int y;
        public GridPoint(int xCoord, int yCoord){
            x = xCoord;
            y = yCoord;
        }

        public boolean isEqual(GridPoint otherPoint){
            if(x == otherPoint.x && y == otherPoint.y)
                return true;
            return false;
        }
    }

}
