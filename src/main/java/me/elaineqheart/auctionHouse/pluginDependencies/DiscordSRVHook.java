package me.elaineqheart.auctionHouse.pluginDependencies;

import com.loohp.interactivechat.objectholders.ICPlayerFactory;
import com.loohp.interactivechat.objectholders.OfflineICPlayer;
import com.loohp.interactivechat.utils.ItemStackUtils;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.DiscordMessageContent;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ImageDisplayData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ImageDisplayType;
import com.loohp.interactivechatdiscordsrvaddon.utils.DiscordContentUtils;
import com.loohp.interactivechatdiscordsrvaddon.wrappers.TitledInventoryWrapper;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.MessageAction;
import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.configs.M;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class DiscordSRVHook {

    private static final DiscordSRVHook instance = new DiscordSRVHook();
    public static boolean enabled;

    private DiscordSRVHook() {
    }

    @Subscribe
    public void onMessageReceived(DiscordGuildMessageReceivedEvent event) {
    }

    public static void sendAuctionListingToDiscord(Player seller, ItemStack inputItem, String title) {
        AuctionHouse.getScheduler().asyncScheduler().run(() -> {
            try {
                DiscordMessageContent embed = getItemEmbed(seller, inputItem, title);
                TextChannel textChannel = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(M.getFormatted("discord-srv.new-listing-channel-name"));
                MessageAction action = textChannel.sendMessageEmbeds(embed.toJDAMessageEmbeds().getFirst());
                for (var attachment : embed.getAttachments().entrySet()) {
                    action = action.addFile(attachment.getValue(), attachment.getKey());
                }
                action.queue();

            } catch (Exception e) {
                AuctionHouse.getInstance().getLogger().warning("Could not send item data to discord");
            }
        });
    }

    private static DiscordMessageContent getItemEmbed(Player seller, ItemStack inputItem, String title) {
        OfflineICPlayer offlinePlayer = ICPlayerFactory.getOfflineICPlayer(seller.getUniqueId());
        ImageDisplayData displayData;

        Inventory containerInventory = DiscordContentUtils.getBlockInventory(inputItem);
        if (containerInventory == null) {
            displayData = new ImageDisplayData(
                    offlinePlayer,
                    0,
                    title,
                    ImageDisplayType.ITEM,
                    inputItem
            );
        } else {
            displayData = new ImageDisplayData(
                    offlinePlayer,
                    0,
                    title,
                    ImageDisplayType.ITEM_CONTAINER,
                    inputItem,
                    new TitledInventoryWrapper(
                            ItemStackUtils.getDisplayName(inputItem, false),
                            containerInventory
                    )
            );
        }
        return DiscordContentUtils.createContents(Collections.singletonList(displayData), offlinePlayer).getFirst().getFirst();
    }

    public static void register() {
        DiscordSRV.api.subscribe(instance);
        enabled = true;
    }

    public static void unregister() {
        DiscordSRV.api.unsubscribe(instance);
        enabled = false;
    }

}
