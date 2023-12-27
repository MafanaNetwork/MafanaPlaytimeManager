package me.tahacheji.mafana.data;

import org.bukkit.entity.Player;

public class PlayerPlaytime {

    private Player player;
    private int secondsPlayed;

    public PlayerPlaytime(Player player, int secondsPlayed) {
        this.player = player;
        this.secondsPlayed = secondsPlayed;
    }

    public Player getPlayer() {
        return player;
    }

    public int getSecondsPlayed() {
        return secondsPlayed;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setSecondsPlayed(int secondsPlayed) {
        this.secondsPlayed = secondsPlayed;
    }
}
