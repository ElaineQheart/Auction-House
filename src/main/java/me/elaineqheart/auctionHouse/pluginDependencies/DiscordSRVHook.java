package me.elaineqheart.auctionHouse.pluginDependencies;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import me.elaineqheart.auctionHouse.AuctionHouse;

public class DiscordSRVHook {

    private static final DiscordSRVHook instance = new DiscordSRVHook();
    public static boolean enabled;

    private DiscordSRVHook() {
    }


    @Subscribe
    public void onMessageReceived(DiscordGuildMessageReceivedEvent event) {

    }

    public static void sendMessage(String channel, String message) {
        /*AuctionHouse.getScheduler().asyncScheduler().run(() -> {
            if (!DiscordSRVHook.enabled) return;
            TextChannel textChannel = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel);
            MessageEmbed embed = new MessageEmbed();
            if (textChannel != null) {
                textChannel.sendMessageEmbeds().complete();
            } else {
                AuctionHouse.getInstance().getLogger().warning("DiscordSRV: Text channel is null");
            }
        });
        */

        /*
        discord-srv:
        new-bin-auction: "AH >> Started to sell [%item%] x%amount% for **%price%** on the auction!"
        new-bid-auction: "AH >> Started bid auction on [%item%] x%amount% for **%price%** on the auction!"
        */
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
