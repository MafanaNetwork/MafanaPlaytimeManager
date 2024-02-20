package me.tahacheji.mafana.event;

import me.tahacheji.mafana.MafanaPlaytimeManager;
import me.tahacheji.mafana.data.PlayerPlaytime;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.concurrent.CompletableFuture;

public class PlayerJoin implements Listener {


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CompletableFuture.supplyAsync(() -> {
            MafanaPlaytimeManager.getInstance().getPlaytimeDatabase().addPlayer(player);
            MafanaPlaytimeManager.getInstance().getPlaytimeDatabase().addPlaytimeForTheDay(player.getUniqueId());
            PlayerPlaytime playtime = new PlayerPlaytime(player, 0);
            MafanaPlaytimeManager.getInstance().getPlayerPlaytimeList().add(playtime);
            return null;
        });
    }
}
