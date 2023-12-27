package me.tahacheji.mafana.command;

import me.tahacheji.mafana.commandExecutor.Command;
import me.tahacheji.mafana.data.PlaytimeDisplay;
import org.bukkit.entity.Player;

public class PlaytimeCommand {

    @Command(names = {"pt"}, playerOnly = true)
    public void openPlayTimeGUI(Player player) {
        new PlaytimeDisplay().getPlaytimeGui(player, true, "", false).open(player);
    }
}
