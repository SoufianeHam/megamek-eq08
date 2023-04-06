package megamek.client.commands;

import megamek.client.Client;
import megamek.common.Coords;
import megamek.common.Hex;

import java.util.ArrayList;
import java.util.List;

public class LookCommand extends ClientCommand {
    private final static List<String> directions = new ArrayList<>();
    {
        directions.add("N");
        directions.add("NE");
        directions.add("SE");
        directions.add("S");
        directions.add("SW");
        directions.add("NW");
    }

    public LookCommand(Client client) {
        super(client, "look", "Look around the current hex.");
    }

    @Override
    public String run(String[] args) {
        Coords pos = client.getCurrentHex();
        Hex hex = getClient().getGame().getBoard().getHex(pos);
        StringBuilder str;
        if (hex != null) {
            str = new StringBuilder("Looking around hex (" + (pos.getX() + 1) + ", "
                    + (pos.getY() + 1) + ")\n");
            for (String dir : directions) {
                Coords coord = pos.translated(dir);
                str.append("To the ").append(dir).append(" (").append(coord.getX() + 1).append(", ").append(coord.getY() + 1).append("): ");
                hex = getClient().getGame().getBoard().getHex(coord);
                if (hex != null) {
                    str.append(hex.toString());
                } else {
                    str.append("off map.\n");
                }
            }
        } else {
            str = new StringBuilder("No current hex, or current hex offboard.");
        }
        return str.toString();
    }
}
