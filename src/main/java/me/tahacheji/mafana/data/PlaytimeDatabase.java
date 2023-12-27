package me.tahacheji.mafana.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.TahaCheji.mysqlData.MySQL;
import me.TahaCheji.mysqlData.MysqlValue;
import me.TahaCheji.mysqlData.SQLGetter;
import me.tahacheji.mafana.MafanaNetworkCommunicator;
import me.tahacheji.mafana.MafanaPlaytimeManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlaytimeDatabase extends MySQL {

    SQLGetter sqlGetter = new SQLGetter(this);

    public PlaytimeDatabase() {
        super("162.254.145.231", "3306", "51252", "51252", "346a1ef0fc");
    }

    public void addPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        if (!sqlGetter.exists(uuid)) {
            sqlGetter.setString(new MysqlValue("NAME", uuid, player.getName()));
            sqlGetter.setString(new MysqlValue("PLAYTIME", uuid, ""));
        }
    }

    public void updatePlaytimeForTheDay(PlayerPlaytime playerPlaytime) {
        Player player = playerPlaytime.getPlayer();
        Playtime playtime = getPlaytimeForTheDay(player);
        if(playtime != null) {
            int secondsPlayed = playtime.getSecondsPlayed() + playerPlaytime.getSecondsPlayed();
            Playtime newPlayTime = new Playtime(player.getUniqueId().toString(), playtime.getDate(), playtime.getServerUUID(), secondsPlayed);
            removePlayTime(player, playtime);
            addPlayTime(player, newPlayTime);
        }
    }

    public void addPlaytimeForTheDay(OfflinePlayer player) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("M/d/yyyy");
        LocalDateTime now = LocalDateTime.now();
        String time = "[" + dtf.format(now) + "]";
        Playtime playtime = getPlaytimeForTheDay(player);
        ProxyPlayer proxyPlayer = MafanaNetworkCommunicator.getInstance().getNetworkCommunicatorDatabase().getProxyPlayer(player);
        if (proxyPlayer != null) {
            if(MafanaNetworkCommunicator.getInstance().getNetworkCommunicatorDatabase().getAllServerValues(proxyPlayer.getServerID()) != null) {
                if (MafanaNetworkCommunicator.getInstance().getNetworkCommunicatorDatabase().hasServerValue(proxyPlayer.getServerID(), "COUNT_PLAYTIME")) {
                    if (playtime == null) {
                        List<Playtime> x = getPlaytime(player.getUniqueId());
                        x.add(new Playtime(player.getUniqueId().toString(), time, proxyPlayer.getServerID().toString(), 0));
                        setPlaytime(player.getUniqueId(), x);
                    }
                }
            }
        }
    }

    public void addPlayTime(OfflinePlayer player, Playtime playtime) {
        List<Playtime> p =  getPlaytime(player.getUniqueId());
        p.add(playtime);
        setPlaytime(player.getUniqueId(), p);
    }

    public void removePlayTime(OfflinePlayer offlinePlayer, Playtime playtime) {
        List<Playtime> playtimes = getPlaytime(offlinePlayer.getUniqueId());

        Playtime playTimeToRemove = playtimes.stream()
                .filter(x -> x.getDate().equalsIgnoreCase(playtime.getDate()))
                .findFirst()
                .orElse(null);

        if (playTimeToRemove != null) {
            playtimes.remove(playTimeToRemove);
            setPlaytime(offlinePlayer.getUniqueId(), playtimes);
        }
    }


    public Playtime getPlaytimeForTheDay(OfflinePlayer player) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("M/d/yyyy");
        LocalDateTime now = LocalDateTime.now();
        String time = "[" + dtf.format(now) + "]";
        for (Playtime playtime : getPlaytime(player.getUniqueId())) {
            if (playtime.getDate() != null) {
                if (playtime.getDate().equalsIgnoreCase(time)) {
                    return playtime;
                }
            }
        }
        return null;
    }

    public void setPlaytime(UUID uuid, List<Playtime> list) {
        Gson gson = new Gson();
        sqlGetter.setString(new MysqlValue("PLAYTIME", uuid, gson.toJson(list)));
    }

    public List<Playtime> getPlaytime(UUID uuid) {
        String x = sqlGetter.getString(uuid, new MysqlValue("PLAYTIME"));
        Gson gson = new Gson();
        List<Playtime> playtime = gson.fromJson(x, new TypeToken<List<Playtime>>() {
        }.getType());
        return playtime != null ? playtime : new ArrayList<>();
    }

    @Override
    public SQLGetter getSqlGetter() {
        return sqlGetter;
    }

    @Override
    public void connect() {
        super.connect();
        if (this.isConnected()) sqlGetter.createTable("player_playtime_manager",
                new MysqlValue("NAME", ""),
                new MysqlValue("PLAYTIME", ""));
    }
}
