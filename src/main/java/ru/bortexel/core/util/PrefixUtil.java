package ru.bortexel.core.util;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PrefixUtil {
    public static final List<String> IMPORTANT_PREFIXES = new ArrayList<String>() {{
        add("admin");
        add("moderator");
        add("helper");
    }};

    public static HashMap<String, String> getPrefixMap() {
        return new HashMap<String, String>() {{
            put("admin", "§c§l * §r§f");
            put("moderator", "§e§l * §r§f");
            put("helper", "§2§l * §r§f");
        }};
    }

    public static String getPrefix(ServerPlayerEntity player, HashMap<String, String> prefixes) {
        for (String key : IMPORTANT_PREFIXES) {
            if (PermissionUtil.hasPermission(player, "prefix." + key)) return prefixes.get(key);
        }

        for (String key : prefixes.keySet()) {
            if (PermissionUtil.hasPermission(player, "prefix." + key)) return prefixes.get(key);
        }

        return "";
    }
}
