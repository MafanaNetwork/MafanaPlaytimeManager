package me.tahacheji.mafana.command;

import me.tahacheji.mafana.MafanaPlaytimeManager;
import me.tahacheji.mafana.commandExecutor.Command;
import me.tahacheji.mafana.commandExecutor.paramter.Param;
import me.tahacheji.mafana.data.OfflineProxyPlayer;
import me.tahacheji.mafana.data.PlaytimeDisplay;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlaytimeCommand {

    @Command(names = {"pt"}, playerOnly = true)
    public void openPlayTimeGUI(Player player) {
        new PlaytimeDisplay().getPlaytimeGui(player.getUniqueId(), true, "", false, player)
                .thenAccept(paginatedGui -> Bukkit.getScheduler().runTask(MafanaPlaytimeManager.getInstance(), () -> paginatedGui.open(player)));
    }

    @Command(names = {"pt admin"}, permission = "mafana.admin", playerOnly = true)
    public void openPlayTimeGUI(Player player, @Param(name = "target")OfflineProxyPlayer offlineProxyPlayer) {
        new PlaytimeDisplay().getPlaytimeGui(UUID.fromString(offlineProxyPlayer.getPlayerUUID()), true, "", false, player)
                .thenAccept(paginatedGui -> Bukkit.getScheduler().runTask(MafanaPlaytimeManager.getInstance(), () -> paginatedGui.open(player)));
    }
}
