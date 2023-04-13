package megamek.common;

import megamek.common.enums.GamePhase;
import megamek.common.force.Force;
import megamek.common.force.Forces;
import megamek.server.victory.VictoryResult;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    @Test
    public void testCancelVictory() {
        // Default test
        Game game = new Game();
        game.cancelVictory();
        assertFalse(game.isForceVictory());
        assertSame(Player.PLAYER_NONE, game.getVictoryPlayerId());
        assertSame(Player.TEAM_NONE, game.getVictoryTeam());

        // Test with members set to specific values
        Game game2 = new Game();
        game2.setVictoryPlayerId(10);
        game2.setVictoryTeam(10);
        game2.setForceVictory(true);

        game2.cancelVictory();
        assertFalse(game.isForceVictory());
        assertSame(Player.PLAYER_NONE, game.getVictoryPlayerId());
        assertSame(Player.TEAM_NONE, game.getVictoryTeam());
    }
    @Test
    public void testSetForces() {
        // Default test
        Game game = new Game();
        Forces forces = new Forces(game);
        game.setForces(forces);
        assertSame(forces, game.getForces());

        // Test with new forces
        Forces newForces = new Forces(game);
        game.setForces(newForces);
        assertSame(newForces, game.getForces());
    }




    @Test
    public void testGetMapSettings() {
        // Default test
        Game game = new Game();
        MapSettings mapSettings = game.getMapSettings();
        assertNotNull(mapSettings);

        // Test that same instance is returned every time
        MapSettings mapSettings2 = game.getMapSettings();
        assertSame(mapSettings, mapSettings2);
    }
    @Test
    public void testSetBoard() {
        // Create a new game
        Game game = new Game();

        // Create a new board and set it on the game
        Board newBoard = new Board();
        game.setBoard(newBoard);

        // Ensure that the board was set on the game and the old board was processed
        assertSame(newBoard, game.getBoard());
    }

    @Test
    public void testAddMinefield() {
        // Create a new game with a board
        Game game = new Game();
        Board board = new Board(10, 10);
        game.setBoard(board);
        Coords position = new Coords(1, 1);
        // Add a minefield to the board
        Minefield mf = Minefield.createMinefield(position,1,1,1);
        game.addMinefield(mf);

        assertNotNull(mf);
    }
    @Test
    public void testRemoveMinefield() {
        // Create a new game with a board
        Game game = new Game();
        Board board = new Board(10, 10);
        game.setBoard(board);
        Coords position = new Coords(1, 1);
        // Add a minefield to the board
        Minefield mf = Minefield.createMinefield(position, 1, 1, 1);
        game.addMinefield(mf);
        assertNotNull(mf);
        assertEquals(mf.getCoords(),position);
        // Remove the minefield
        game.removeMinefield(mf);
        game.removeMinefieldHelper(mf);
        game.clearMinefields();
        game.clearMinefieldsHelper();

        assertNotEquals(game.getMinefields(position),mf);
    }
    @Test
    public void testGetNbrMinefields() {
        // Create a new game with a board
        Game game = new Game();
        Board board = new Board(10, 10);
        game.setBoard(board);

        // Add a minefield to the board at position (1,1)
        Coords position = new Coords(1, 1);
        Minefield mf = Minefield.createMinefield(position, 1, 1, 1);
        game.addMinefield(mf);

        // Check that there is exactly one minefield at position (1,1)
        assertEquals(1, game.getNbrMinefields(position));

        // Add another minefield to the board at position (1,1)
        Minefield mf2 = Minefield.createMinefield(position, 1, 1, 1);
        game.addMinefield(mf2);

        // Check that there are now two minefields at position (1,1)
        assertEquals(2, game.getNbrMinefields(position));

        // Check that there are no minefields at position (2,2)
        Coords position2 = new Coords(2, 2);
        assertEquals(0, game.getNbrMinefields(position2));
    }

    @Test
    public void testSetAndRemovePlayer() {
        // Create a game with two players
        Game game = new Game();
        Player player1 = new Player(0, "Alice");
        Player player2 = new Player(1, "Bob");
        game.addPlayer(0, player1);
        game.addPlayer(1, player2);

        // Update player 1 with a new name
        Player updatedPlayer1 = new Player(0, "Alice Smith");
        game.setPlayer(0, updatedPlayer1);

        // Check that the player was updated correctly
        assertEquals("Alice Smith", game.getPlayer(0).getName());

        // Remove player 2
        game.removePlayer(1);

        // Check that player 2 was removed and player 1 still exists
        assertNull(game.getPlayer(1));
        assertNotNull(game.getPlayer(0));
    }

    @Test
    public void testSetPhase() {
        Game game = new Game();
        game.setPhase(GamePhase.TARGETING);
        assertEquals(GamePhase.TARGETING, game.getPhase());

        game.setPhase(GamePhase.PHYSICAL);
        assertEquals(GamePhase.PHYSICAL, game.getPhase());

        game.setPhase(GamePhase.INITIATIVE);
        assertEquals(GamePhase.INITIATIVE, game.getPhase());

        game.setPhase(GamePhase.PHYSICAL_REPORT);
        assertEquals(GamePhase.PHYSICAL_REPORT, game.getPhase());

        game.setPhase(GamePhase.END);
        assertEquals(GamePhase.END, game.getPhase());
    }



    @Test
    public void testSetMapSettings() {
        // Default test
        Game game = new Game();
        MapSettings mapSettings = MapSettings.getInstance();
        game.setMapSettings(mapSettings);
        assertSame(mapSettings, game.getMapSettings());
    }

    @Test
    void testGetGunEmplacements() {
        // Set up a new game with a board and entities
        Game game = new Game();
        Board board = new Board(10, 10);
        game.setBoard(board);
        Entity gun1 = new GunEmplacement();
        Entity gun2 = new GunEmplacement();

        game.addEntity(gun1, true);
        game.addEntity(gun2, true);
        Coords coords1 = new Coords(5, 5);
        Coords coords2 = new Coords(6, 6);
        Coords coords3 = new Coords(7, 7);
        gun1.setGame(game);
        gun2.setGame(game);
        gun1.setPosition(coords1);
        gun2.setPosition(coords2);

        // Test getting gun emplacements at different coords
        assertEquals(1, game.getGunEmplacements(coords1).size());
        assertEquals(1, game.getGunEmplacements(coords2).size());
        assertEquals(0, game.getGunEmplacements(coords3).size());
        assertEquals(0, game.getGunEmplacements(new Coords(11, 11)).size()); // Out of bounds
    }





    @Test
    public void testGetVictoryReport() {
        Game game = new Game();
        game.createVictoryConditions();
        VictoryResult victoryResult = game.getVictoryResult();
        assertNotNull(victoryResult);

        // Note: this accessors are tested in VictoryResultTest
        assertSame(Player.PLAYER_NONE, victoryResult.getWinningPlayer());
        assertSame(Player.TEAM_NONE, victoryResult.getWinningTeam());

        int winningPlayer = 2;
        int winningTeam = 5;

        // Test an actual scenario
        Game game2 = new Game();
        game2.setVictoryTeam(winningTeam);
        game2.setVictoryPlayerId(winningPlayer);
        game2.setForceVictory(true);
        game2.createVictoryConditions();
        VictoryResult victoryResult2 = game2.getVictoryResult();

        assertSame(winningPlayer, victoryResult2.getWinningPlayer());
        assertSame(winningTeam, victoryResult2.getWinningTeam());
    }

    @Test
    public void testWinnerAndLooserGetGameScore(){
        //Creating players
        Player player1 = new Player(1,"player1");
        Player player2 = new Player(2,"player2");

        //Creating teams
        Team team1 = new Team(3);
        Team team2 = new Team(4);

        //Adding the players to each team
        player1.setTeam(team1.getId());
        player2.setTeam(team2.getId());

        //Creating a game
        Game game = new Game();
        game.addPlayer(player1.getId(),player1);
        game.addPlayer(player2.getId(),player2);
        //Ending of the game
        game.end(player1.getId(),team1.getId());

        int player1ScoreAfterWin=30;
        int player2ScoreAfterLost=-5;
        //Test winner points and looser points
        assertSame(player1ScoreAfterWin,player1.getScore());
        assertSame(player2ScoreAfterLost,player2.getScore());

    }

    @Test
    public void testTeamWinnerGetScore(){
        //Creating players
        Player player1 = new Player(1,"player1");
        //Creating teams
        Team team1 = new Team(3);
        //Adding the players to each team
        player1.setTeam(team1.getId());
        //Creating a game
        Game game = new Game();
        game.addPlayer(player1.getId(),player1);
        //Ending of the game with no winner
        game.end(Player.PLAYER_NONE,team1.getId());

        int player1ScoreAfterTeamWin=15;
        //Test team win score
        assertSame(player1ScoreAfterTeamWin,player1.getScore());
    }
}
