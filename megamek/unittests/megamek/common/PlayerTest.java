package megamek.common;

import megamek.client.ui.swing.util.PlayerColour;
import megamek.common.enums.Rank;
import org.junit.jupiter.api.Test;

import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    @Test
    public void testGetColorForPlayer() {
        String playerName = "jefke";
        Player player = new Player(0, playerName);
        assertEquals("<B><font color='8080b0'>" + playerName + "</font></B>", player.getColorForPlayer());

        playerName = "Jeanke";
        Player player2 = new Player(1, playerName);
        player2.setColour(PlayerColour.FUCHSIA);
        assertEquals("<B><font color='f000f0'>" + playerName + "</font></B>", player2.getColorForPlayer());
    }

    @Test
    public void testGetRank(){
        //Creating Player
        Player player1 = CreatePlayer();
        //Test initial rank
        assertSame(Rank.BRONZE,player1.getRank());
    }

    @Test
    public void testSetRank(){
        //Creating Player
        Player player1 = CreatePlayer();
        //Test initial rank
        assertSame(Rank.BRONZE,player1.getRank());
        //New Rank
        player1.setRank(Rank.GOLD);

        assertEquals(Rank.GOLD,player1.getRank());


    }

    @Test
    public void testCopy(){
        //Creating Player
        Player player1 = CreatePlayer();
        //Creating a copy of the first player
        Player copiedPLayer = player1.copy();
        //Testing if the players have the same values
        assertSame(player1.getId(),copiedPLayer.getId());
        assertSame(player1.getName(),copiedPLayer.getName());
    }

    @Test
    public void testEquals(){
        //Creating Player
        Player player1 = CreatePlayer();
        //Creating a copy of the first player
        Player copiedPLayer = player1.copy();
        //Test if returns true when two player have the same id
        assertTrue(player1.equals(copiedPLayer));
    }

    @Test
    public void testToString() {
        //Creating Player
        Player player1 = CreatePlayer();
        String EXPECTED_STRING = "Player 1 (player1)";
        assertEquals(EXPECTED_STRING, player1.toString());

    }

    @Test
    public void testAddMineFieldAndRemoveMineField () {
        //Creating Player
        Player player1 = CreatePlayer();
        //Creating a mineField
        Coords coords= new Coords(0,0);
        int MINE_TYPE = 0;
        int MINE_DENSITY=0;
        Minefield minefield = Minefield.createMinefield(coords,player1.getId(),MINE_TYPE,MINE_DENSITY);

        int EXPECTED_NUMBER_OF_MINEFIELDS_AFTER_ADD=1;
        int EXPECTED_NUMBER_OF_MINEFIELDS_AFTER_REMOVE=0;


        player1.addMinefield(minefield);

        assertEquals(EXPECTED_NUMBER_OF_MINEFIELDS_AFTER_ADD,player1.getMinefields().size());

        player1.removeMinefield(minefield);

        assertEquals(EXPECTED_NUMBER_OF_MINEFIELDS_AFTER_REMOVE, player1.getMinefields().size());
    }

    @Test
    public void testAddMineFieldsAndRemoveMineFields () {
        //Creating Player
        Player player1 = CreatePlayer();
        //Creating a mineField
        Coords coords= new Coords(0,0);
        int MINE_TYPE = 0;
        int MINE_DENSITY=0;
        Minefield minefield = Minefield.createMinefield(coords,player1.getId(),MINE_TYPE,MINE_DENSITY);
        Minefield minefield2 = Minefield.createMinefield(coords,player1.getId(),MINE_TYPE,MINE_DENSITY);

        Vector<Minefield> minefields= new Vector<Minefield>();
        minefields.add(minefield);
        minefields.add(minefield2);

        int EXPECTED_NUMBER_OF_MINEFIELDS_AFTER_ADD=2;
        int EXPECTED_NUMBER_OF_MINEFIELDS_AFTER_REMOVE=0;

        player1.addMinefields(minefields);

        assertEquals(EXPECTED_NUMBER_OF_MINEFIELDS_AFTER_ADD,player1.getMinefields().size());

        player1.removeMinefields();

        assertEquals(EXPECTED_NUMBER_OF_MINEFIELDS_AFTER_REMOVE,player1.getMinefields().size());

    }


    private static Player CreatePlayer() {
        Player player1 = new Player(1,"player1");
        return player1;
    }


}
