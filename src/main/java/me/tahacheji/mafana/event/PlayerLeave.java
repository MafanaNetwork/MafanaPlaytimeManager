package me.tahacheji.mafana.event;

import me.tahacheji.mafana.MafanaPlaytimeManager;
import me.tahacheji.mafana.data.PlayerPlaytime;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.CompletableFuture;

public class PlayerLeave implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CompletableFuture.supplyAsync(() -> {
            PlayerPlaytime playtime = MafanaPlaytimeManager.getInstance().getPlayerPlaytime(player);
            if(playtime != null) {
                MafanaPlaytimeManager.getInstance().getPlaytimeDatabase().updatePlaytimeForTheDay(playtime);
                MafanaPlaytimeManager.getInstance().removePlayerPlaytime(player);
            }
            return null;
        });
    }
}
