package me.tahacheji.mafana.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.tahacheji.mafana.MafanaNetworkCommunicator;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PlaytimeDatabase extends MySQL {

    SQLGetter sqlGetter = new SQLGetter(this);

    public PlaytimeDatabase() {
        super("162.254.145.231", "3306", "51252", "51252", "346a1ef0fc");
    }

    public CompletableFuture<Void> addPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        return sqlGetter.existsAsync(uuid)
                .thenApplyAsync(exists -> {
                    if (!exists) {
                        sqlGetter.setStringAsync(new DatabaseValue("NAME", uuid, player.getName()));
                        sqlGetter.setStringAsync(new DatabaseValue("PLAYTIME", uuid, ""));
                    }
                    return null;
                });
    }





    public CompletableFuture<Void> updatePlaytimeForTheDay(PlayerPlaytime playerPlaytime) {
        CompletableFuture<Void> z = new CompletableFuture<>();
        Player player = playerPlaytime.getPlayer();
        getPlaytimeForTheDay(player.getUniqueId()).thenAcceptAsync(playtime -> {
            if(playtime != null) {
                int secondsPlayed = playtime.getSecondsPlayed() + playerPlaytime.getSecondsPlayed();
                Playtime newPlayTime = new Playtime(player.getUniqueId().toString(), playtime.getDate(), playtime.getServerUUID(), secondsPlayed);
                removePlayTime(player.getUniqueId(), playtime).thenAcceptAsync(unused -> {
                    addPlayTime(player.getUniqueId(), newPlayTime).thenAcceptAsync(unused1 -> {
                        z.complete(null);
                    });
                });
            }
        });
        return null;
    }

    public CompletableFuture<Void> addPlaytimeForTheDay(UUID uuid) {
        return getPlaytimeForTheDay(uuid).thenComposeAsync(playtime -> {
            if (playtime == null) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("M/d/yyyy");
                LocalDateTime now = LocalDateTime.now();
                String time = "[" + dtf.format(now) + "]";

                return MafanaNetworkCommunicator.getInstance().getNetworkCommunicatorDatabase().getProxyPlayerAsync(uuid)
                        .thenComposeAsync(proxyPlayer -> {
                            if (proxyPlayer != null) {
                                return MafanaNetworkCommunicator.getInstance().getNetworkCommunicatorDatabase()
                                        .getAllServerValuesAsync(proxyPlayer.getServerID())
                                        .thenComposeAsync(serverValues -> {
                                            if (serverValues != null) {
                                                return MafanaNetworkCommunicator.getInstance()
                                                        .getNetworkCommunicatorDatabase().hasServerValueAsync(proxyPlayer.getServerID(), "COUNT_PLAYTIME")
                                                        .thenApplyAsync(hasValue -> {
                                                            if (hasValue) {
                                                                List<Playtime> x = getPlaytime(uuid).join();
                                                                x.add(new Playtime(uuid.toString(), time, proxyPlayer.getServerID().toString(), 0));
                                                                setPlaytime(uuid, x).join();
                                                            }
                                                            return null;
                                                        });
                                            }
                                            return CompletableFuture.completedFuture(null);
                                        });
                            }
                            return CompletableFuture.completedFuture(null);
                        });
            }
            return CompletableFuture.completedFuture(null);
        });
    }

    public CompletableFuture<Void> addPlayTime(UUID uuid, Playtime playtime) {
        CompletableFuture<Void> z = new CompletableFuture<>();
        getPlaytime(uuid).thenAcceptAsync(playtimes -> {
            playtimes.add(playtime);
            setPlaytime(uuid, playtimes).thenAcceptAsync(unused -> z.complete(null));
        });
        return z;
    }

    public CompletableFuture<Void> removePlayTime(UUID uuid, Playtime playtime) {
        CompletableFuture<Void> z = new CompletableFuture<>();
        getPlaytime(uuid).thenAcceptAsync(playtimes -> {
            Playtime playTimeToRemove = playtimes.stream()
                    .filter(x -> x.getDate().equalsIgnoreCase(playtime.getDate()))
                    .findFirst()
                    .orElse(null);

            if (playTimeToRemove != null) {
                playtimes.remove(playTimeToRemove);
                setPlaytime(uuid, playtimes).thenAcceptAsync(unused -> {
                    z.complete(null);
                });
            }
        });
        return z;
    }


    public CompletableFuture<Playtime> getPlaytimeForTheDay(UUID uuid) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("M/d/yyyy");
        LocalDateTime now = LocalDateTime.now();
        String time = "[" + dtf.format(now) + "]";
        CompletableFuture<Playtime> x = new CompletableFuture<>();
        getPlaytime(uuid).thenAcceptAsync(playtimes -> {
           for(Playtime playtime : playtimes) {
               if (playtime.getDate() != null) {
                   if (playtime.getDate().equalsIgnoreCase(time)) {
                       x.complete(playtime);
                       return;
                   }
               }
           }
        });
        return x;
    }

    public CompletableFuture<Void> setPlaytime(UUID uuid, List<Playtime> list) {
        Gson gson = new Gson();
        return sqlGetter.setStringAsync(new DatabaseValue("PLAYTIME", uuid, gson.toJson(list)));
    }

    public CompletableFuture<List<Playtime>> getPlaytime(UUID uuid) {
        CompletableFuture<List<Playtime>> x = new CompletableFuture<>();
        sqlGetter.getStringAsync(uuid, new DatabaseValue("PLAYTIME")).thenAcceptAsync(string -> {
            Gson gson = new Gson();
            List<Playtime> playtime = new ArrayList<>(gson.fromJson(string, new TypeToken<List<Playtime>>() {
            }.getType()));
            x.complete(playtime);
        });
        return x;
    }

    @Override
    public SQLGetter getSqlGetter() {
        return sqlGetter;
    }

    public void connect() {
        sqlGetter.createTable("player_playtime_manager",
                new DatabaseValue("NAME", ""),
                new DatabaseValue("PLAYTIME", ""));
    }
}
