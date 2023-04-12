package megamek.common;

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
    public void testSetMapSettings() {
        // Default test
        Game game = new Game();
        MapSettings mapSettings = MapSettings.getInstance();
        game.setMapSettings(mapSettings);
        assertSame(mapSettings, game.getMapSettings());
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
