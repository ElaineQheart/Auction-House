package me.elaineqheart.auctionHouse.data.persistentStorage.local.configs;

import me.clip.placeholderapi.PlaceholderAPI;
import me.elaineqheart.auctionHouse.data.StringUtils;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.Config;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class M extends Config {

    private static final MiniMessage mm = MiniMessage.builder()
            .preProcessor(M::convertLegacyInput)
            .postProcessor(M::explicitItalic)
            .build();

    // legacy %placeholder% tokens that are rewritten to MiniMessage tags so old configs keep working
    private static final Pattern LEGACY_PLACEHOLDER = Pattern.compile("%([a-zA-Z][a-zA-Z0-9_-]*)%");
    private static final Set<String> KNOWN_PLACEHOLDERS = Set.of(
            "player", "seller", "buyer", "price", "price-trim", "number",
            "item", "amount", "time", "reason", "request", "filter",
            "page", "pages", "tax", "amountOfBids", "sold", "total",
            "duration", "limit", "name", "input", "currency-symbol", "player_name");

    // AuctionHouse.getPlugin().saveResource("messages.yml", false);

    public static FileConfiguration get() {
        return ConfigManager.messages.getCustomFile();
    }

    private static String getValue(String key) {
        String message = get().getString(key);
        if (message == null) {
            return "<red>Missing message key: " + key;
        }
        return message.replace("&n", "\n");
    }

    public static Component getFormatted(String key, Object... replacements) {
        if (replacements.length % 2 != 0) return error("Invalid placeholder replacements for key: " + key);
        return deserialize(getValue(key), resolverOf(replacements));
    }

    public static List<Component> getLoreList(String key, Object... replacements) {
        if (replacements.length % 2 != 0) return List.of(error("Invalid placeholder replacements for key: " + key));
        return Arrays.stream(getValue(key).split("\n", -1))
                .map(line -> deserialize(line, resolverOf(replacements)))
                .toList();
    }

    // plain text, used for command names, decimal formats and anvil titles
    public static String getString(String key) {
        return PlainTextComponentSerializer.plainText().serialize(getFormatted(key));
    }

    // parses arbitrary text (item names, admin input) through the same MiniMessage pipeline
    public static Component deserialize(String input) {
        return safeDeserialize(input);
    }

    private static Component explicitItalic(Component component) {
        return component.style(s -> {
            if (s.build().decoration(TextDecoration.ITALIC) == TextDecoration.State.NOT_SET) {
                s.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
            }
        }).children(component.children().stream().map(M::explicitItalic).toList());
    }

    public static Component formatPlayer(String playerName, UUID playerID) {
        return resolveNameTemplate("placeholders.player", playerName, playerID);
    }

    public static Component formatSeller(String playerName, UUID playerID) {
        return resolveNameTemplate("placeholders.seller", playerName, playerID);
    }

    public static Component formatBuyer(String playerName, UUID playerID) {
        return resolveNameTemplate("placeholders.buyer", playerName, playerID);
    }

    // substitute <player_name> with PAPI when available
    private static Component resolveNameTemplate(String templateKey, String playerName, UUID playerID) {
        if (playerName == null) return Component.empty();
        String template = get().getString(templateKey);
        String result = template == null ? "<player_name>" : template;
        result = result.replace("<player_name>", playerName)
                .replace("%player_name%", playerName);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(playerID);
            result = PlaceholderAPI.setPlaceholders(target, result);
        }

        return deserialize(result.replace('&', '§'), resolverOf());
    }

    private static Component deserialize(String input, TagResolver resolver) {
        return mm.deserialize(input, resolver);
    }

    private static TagResolver resolverOf(Object... replacements) {
        TagResolver.Builder builder = TagResolver.builder();
        for (int i = 0; i < replacements.length; i += 2) {
            String tag = String.valueOf(replacements[i]).toLowerCase(Locale.ROOT);
            builder.resolver(TagResolver.resolver(tag, tagOf(tag, replacements[i + 1])));
        }
        return builder.build();
    }

    private static Tag tagOf(String tag, Object value) {
        if (value instanceof Component component) {
            return Tag.selfClosingInserting(component);
        }
        if (value instanceof Number number) {
            double d = number.doubleValue();
            if (tag.startsWith("price-trim")) return Tag.selfClosingInserting(StringUtils.formatPrice(d, true));
            if (tag.startsWith("price")) return Tag.selfClosingInserting(StringUtils.formatPrice(d, false));
            if (tag.startsWith("number")) return Tag.selfClosingInserting(StringUtils.formatNumber(d));
            return Tag.selfClosingInserting(Component.text(value.toString()));
        }
        return Tag.selfClosingInserting(safeDeserialize(value.toString()));
    }

    private static Component safeDeserialize(String input) {
        if (input == null) return Component.empty();
        try {
            return mm.deserialize(input);
        } catch (Exception e) {
            return Component.text(input);
        }
    }

    private static Component error(String message) {
        return mm.deserialize("<red>" + message);
    }

    private static String convertLegacyInput(String input) {
        String result = replaceLegacyPlaceholders(input);
        if (result.indexOf('§') == -1) return result;
        StringBuilder sb = new StringBuilder(result.length());
        for (int i = 0; i < result.length(); i++) {
            char c = result.charAt(i);
            if (c != '§') {
                sb.append(c);
                continue;
            }
            if (i + 1 >= result.length()) break;
            char code = Character.toLowerCase(result.charAt(++i));
            if (code == 'x') {
                StringBuilder hex = new StringBuilder(6);
                for (int h = 0; h < 6 && i + 2 < result.length(); h++) {
                    i += 2;
                    hex.append(result.charAt(i));
                }
                sb.append("<color:#").append(hex).append('>');
                continue;
            }
            String tag = legacyCodeToTag(code);
            if (tag != null) sb.append('<').append(tag).append('>');
        }
        return sb.toString();
    }

    private static String replaceLegacyPlaceholders(String input) {
        Matcher matcher = LEGACY_PLACEHOLDER.matcher(input);
        StringBuilder sb = new StringBuilder(input.length());
        while (matcher.find()) {
            String name = matcher.group(1);
            if (KNOWN_PLACEHOLDERS.contains(name)
                    || name.startsWith("price") || name.startsWith("number") || name.startsWith("price-trim")) {
                matcher.appendReplacement(sb, "<" + name + ">");
            }
        }
        return matcher.appendTail(sb).toString();
    }

    private static String legacyCodeToTag(char code) {
        return switch (code) {
            case '0' -> "black";
            case '1' -> "dark_blue";
            case '2' -> "dark_green";
            case '3' -> "dark_aqua";
            case '4' -> "dark_red";
            case '5' -> "dark_purple";
            case '6' -> "gold";
            case '7' -> "gray";
            case '8' -> "dark_gray";
            case '9' -> "blue";
            case 'a' -> "green";
            case 'b' -> "aqua";
            case 'c' -> "red";
            case 'd' -> "light_purple";
            case 'e' -> "yellow";
            case 'f' -> "white";
            case 'k' -> "obfuscated";
            case 'l' -> "bold";
            case 'm' -> "strikethrough";
            case 'n' -> "underlined";
            case 'o' -> "italic";
            case 'r' -> "reset";
            default -> null;
        };
    }

}
