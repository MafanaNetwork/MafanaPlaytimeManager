package me.tahacheji.mafana.data;

public class Playtime {

    private String playerUUID;
    private String date;
    private String serverUUID;
    private int secondsPlayed;

    public Playtime(String playerUUID, String date, String serverUUID, int secondsPlayed) {
        this.playerUUID = playerUUID;
        this.date = date;
        this.serverUUID = serverUUID;
        this.secondsPlayed = secondsPlayed;
    }

    public void setPlayerUUID(String playerUUID) {
        this.playerUUID = playerUUID;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setServerUUID(String serverUUID) {
        this.serverUUID = serverUUID;
    }

    public void setSecondsPlayed(int secondsPlayed) {
        this.secondsPlayed = secondsPlayed;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    public String getDate() {
        return date;
    }

    public String getServerUUID() {
        return serverUUID;
    }

    public int getSecondsPlayed() {
        return secondsPlayed;
    }
}
