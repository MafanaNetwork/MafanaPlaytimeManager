package me.tahacheji.mafana;

import me.tahacheji.mafana.command.PlaytimeCommand;
import me.tahacheji.mafana.commandExecutor.CommandHandler;
import me.tahacheji.mafana.data.PlayerPlaytime;
import me.tahacheji.mafana.data.PlaytimeDatabase;
import me.tahacheji.mafana.event.PlayerJoin;
import me.tahacheji.mafana.event.PlayerLeave;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public final class MafanaPlaytimeManager extends JavaPlugin {


    private static MafanaPlaytimeManager instance;
    private PlaytimeDatabase playtimeDatabase = new PlaytimeDatabase();
    private List<PlayerPlaytime> playerPlaytimeList = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;
        playtimeDatabase.connect();
        getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        getServer().getPluginManager().registerEvents(new PlayerLeave(), this);
        CommandHandler.registerCommands(PlaytimeCommand.class, this);
        new BukkitRunnable() {
            @Override
            public void run() {
                for(PlayerPlaytime playtime : getPlayerPlaytimeList()) {
                    if(playtime != null) {
                        int x = playtime.getSecondsPlayed() + 1;
                        playtime.setSecondsPlayed(x);
                    }
                }
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    @Override
    public void onDisable() {
        playtimeDatabase.disconnect();

    }

    public void removePlayerPlaytime(Player player) {
        PlayerPlaytime playtimeToRemove = null;
        for(PlayerPlaytime playtime : getPlayerPlaytimeList()) {
            if(playtime.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                playtimeToRemove = playtime;
            }
        }
        if(playtimeToRemove != null) {
            getPlayerPlaytimeList().remove(playtimeToRemove);
        }
    }

    public PlayerPlaytime getPlayerPlaytime(Player player) {
        for(PlayerPlaytime playtime : getPlayerPlaytimeList()) {
            if (playtime != null) {
                if (playtime.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                    return playtime;
                }
            }
        }
        return null;
    }

    public List<PlayerPlaytime> getPlayerPlaytimeList() {
        return playerPlaytimeList;
    }

    public PlaytimeDatabase getPlaytimeDatabase() {
        return playtimeDatabase;
    }

    public static MafanaPlaytimeManager getInstance() {
        return instance;
    }
}
