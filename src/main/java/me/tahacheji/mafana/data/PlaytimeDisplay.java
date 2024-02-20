package me.tahacheji.mafana.data;

import de.rapha149.signgui.SignGUI;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.tahacheji.mafana.MafanaNetworkCommunicator;
import me.tahacheji.mafana.MafanaPlaytimeManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlaytimeDisplay {

    public CompletableFuture<PaginatedGui> getPlaytimeGui(UUID uuid, boolean sortNewestToOldest, String dateFilter, boolean mostPlayTimeToLeast, Player open) {
        CompletableFuture<PaginatedGui> z = new CompletableFuture<>();
        MafanaPlaytimeManager.getInstance().getPlaytimeDatabase().getPlaytime(uuid).thenAcceptAsync(playtimes -> {
            PaginatedGui gui = Gui.paginated()
                    .title(Component.text(ChatColor.GOLD + "Playtime Logs"))
                    .rows(6)
                    .pageSize(28)
                    .disableAllInteractions()
                    .create();

            List<String> lore = new ArrayList<>();
            ItemStack greystainedglass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta newmeta = greystainedglass.getItemMeta();
            newmeta.setDisplayName(ChatColor.GRAY + " ");
            newmeta.setLore(lore);
            greystainedglass.setItemMeta(newmeta);

            ItemStack closePage = new ItemStack(Material.BARRIER);
            ItemMeta closePageItemMeta = closePage.getItemMeta();
            closePageItemMeta.setDisplayName(ChatColor.GRAY + "Close Page");
            closePageItemMeta.setLore(lore);
            closePage.setItemMeta(closePageItemMeta);

            gui.setItem(0, new GuiItem(greystainedglass));
            gui.setItem(1, new GuiItem(greystainedglass));
            gui.setItem(2, new GuiItem(greystainedglass));
            gui.setItem(3, new GuiItem(greystainedglass));
            gui.setItem(4, new GuiItem(greystainedglass));
            gui.setItem(5, new GuiItem(greystainedglass));
            gui.setItem(6, new GuiItem(greystainedglass));
            gui.setItem(7, new GuiItem(greystainedglass));
            gui.setItem(8, new GuiItem(greystainedglass));
            gui.setItem(17, new GuiItem(greystainedglass));
            gui.setItem(26, new GuiItem(greystainedglass));
            gui.setItem(35, new GuiItem(greystainedglass));
            gui.setItem(45, new GuiItem(greystainedglass));
            gui.setItem(53, new GuiItem(greystainedglass));
            gui.setItem(52, new GuiItem(greystainedglass));
            gui.setItem(51, new GuiItem(greystainedglass));
            gui.setItem(50, new GuiItem(greystainedglass));
            gui.setItem(48, new GuiItem(greystainedglass));
            gui.setItem(47, new GuiItem(greystainedglass));
            gui.setItem(46, new GuiItem(greystainedglass));
            gui.setItem(44, new GuiItem(greystainedglass));
            gui.setItem(36, new GuiItem(greystainedglass));
            gui.setItem(27, new GuiItem(greystainedglass));
            gui.setItem(18, new GuiItem(greystainedglass));
            gui.setItem(9, new GuiItem(greystainedglass));
            gui.setItem(49, new GuiItem(closePage, event -> {
                event.getWhoClicked().closeInventory();
            }));

            gui.setItem(6, 3, ItemBuilder.from(Material.PAPER).setName(ChatColor.DARK_GRAY + "Previous")
                    .asGuiItem(event -> gui.previous()));
            gui.setItem(6, 7, ItemBuilder.from(Material.PAPER).setName(ChatColor.DARK_GRAY + "Next")
                    .asGuiItem(event -> gui.next()));

            ItemStack sortButton = new ItemStack(Material.COMPARATOR);
            ItemMeta sortButtonMeta = sortButton.getItemMeta();

            if (sortNewestToOldest) {
                sortButtonMeta.setDisplayName(ChatColor.YELLOW + "Sort: Newest to Oldest");
            } else {
                sortButtonMeta.setDisplayName(ChatColor.YELLOW + "Sort: Oldest to Newest");
            }
            sortButton.setItemMeta(sortButtonMeta);

            gui.setItem(53, new GuiItem(sortButton, event -> {
                boolean newSortDirection = !sortNewestToOldest;
                Player clicker = (Player) event.getWhoClicked();
                try {
                    clicker.closeInventory();
                    getPlaytimeGui(uuid, newSortDirection, dateFilter, mostPlayTimeToLeast, open).thenAccept(paginatedGui -> Bukkit.getScheduler().runTask(MafanaPlaytimeManager.getInstance(), () -> paginatedGui.open(open)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));

            ItemStack dateFilterButton = new ItemStack(Material.CLOCK);
            ItemMeta dateFilterButtonMeta = dateFilterButton.getItemMeta();
            dateFilterButtonMeta.setDisplayName(ChatColor.YELLOW + "Date Filter: " + (dateFilter.isEmpty() ? "None" : dateFilter));
            dateFilterButton.setItemMeta(dateFilterButtonMeta);

            gui.setItem(50, new GuiItem(dateFilterButton, event -> {
                event.getWhoClicked().closeInventory();
                openDateFilterSign(uuid, sortNewestToOldest, dateFilter, mostPlayTimeToLeast, open);
            }));

            ItemStack mostPlayTimeButton = new ItemStack(Material.STONE_BUTTON);
            ItemMeta mostPlayTimeButtonMeta = mostPlayTimeButton.getItemMeta();

            mostPlayTimeButtonMeta.setDisplayName(ChatColor.YELLOW + "Order: " + (mostPlayTimeToLeast ? "Most Play Time to Least" : "Least Play Time to Most"));
            mostPlayTimeButton.setItemMeta(mostPlayTimeButtonMeta);

            gui.setItem(48, new GuiItem(mostPlayTimeButton, event -> {
                boolean newOrder = !mostPlayTimeToLeast;
                Player clicker = (Player) event.getWhoClicked();
                try {
                    clicker.closeInventory();
                    getPlaytimeGui(uuid, sortNewestToOldest, dateFilter, newOrder, open).thenAccept(paginatedGui -> Bukkit.getScheduler().runTask(MafanaPlaytimeManager.getInstance(), () -> paginatedGui.open(open)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
            List<Playtime> filteredPlaytimes = new ArrayList<>();

            for (Playtime playtime : playtimes) {
                if (dateFilter.isEmpty() || playtime.getDate().equals(dateFilter)) {
                    filteredPlaytimes.add(playtime);
                }
            }
            if (sortNewestToOldest) {
                filteredPlaytimes.sort(Comparator.comparing(Playtime::getSecondsPlayed).reversed());
            } else {
                filteredPlaytimes.sort(Comparator.comparing(Playtime::getSecondsPlayed));
            }

            List<GuiItem> guiItems = new ArrayList<>();
            CompletableFuture<Void> future = CompletableFuture.allOf(filteredPlaytimes.stream().map(playtime -> getItemStackPlaytime(playtime).thenAcceptAsync(itemStack -> {
                guiItems.add(new GuiItem(itemStack));
            })).toArray(CompletableFuture[]::new));
            future.thenRun(() -> {
                for (GuiItem g : guiItems) {
                    gui.addItem(g);
                }
                gui.update();
                z.complete(gui);
            });

        });

        return z;
    }

    @NotNull
    private CompletableFuture<ItemStack> getItemStackPlaytime(Playtime playtime) {
        CompletableFuture<ItemStack> i = new CompletableFuture<>();
        MafanaNetworkCommunicator.getInstance().getNetworkCommunicatorDatabase().getServerNickNameAsync(UUID.fromString(playtime.getServerUUID())).thenAcceptAsync(string -> {
            int hoursPlayed = (playtime.getSecondsPlayed() / 60) / 60;
            ItemStack item = new ItemStack(Material.CLOCK);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + string + ChatColor.DARK_GRAY + "] " + ChatColor.AQUA + "Playtime: " + hoursPlayed + "h");
            List<String> itemLore = new ArrayList<>();
            itemLore.add("------------------------");
            itemLore.add(ChatColor.DARK_GRAY + "Date: " + playtime.getDate());
            itemLore.add(ChatColor.DARK_GRAY + "Game: " + string);
            itemLore.add(ChatColor.DARK_GRAY + "Hours Played: " + hoursPlayed);
            itemLore.add("------------------------");
            itemMeta.setLore(itemLore);
            item.setItemMeta(itemMeta);
            i.complete(item);
        });
        return i;
    }

    public void openDateFilterSign(UUID uuid, boolean sortNewestToOldest, String dateFilter, boolean mostPlayTimeToLeast, Player open) {
        String filterName = "Date";
        SignGUI.builder()
                .setLines(null, filterName + " Filter:", dateFilter, "[M/d/yyyy]")
                .setType(Material.DARK_OAK_SIGN)
                .setHandler((p, result) -> {
                    String filterValue = result.getLineWithoutColor(0);
                    open.closeInventory();
                    try {
                        getPlaytimeGui(uuid, sortNewestToOldest, filterValue, mostPlayTimeToLeast, open).thenAccept(paginatedGui -> Bukkit.getScheduler().runTask(MafanaPlaytimeManager.getInstance(), () -> paginatedGui.open(open)));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                }).callHandlerSynchronously(MafanaPlaytimeManager.getInstance()).build().open(open);
    }
}
