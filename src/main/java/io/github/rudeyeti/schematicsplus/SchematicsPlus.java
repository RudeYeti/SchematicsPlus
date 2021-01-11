package io.github.rudeyeti.schematicsplus;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.requests.GatewayIntent;
import github.scarsz.discordsrv.util.DiscordUtil;
import io.github.rudeyeti.schematicsplus.listeners.DiscordSRVListener;
import io.github.rudeyeti.schematicsplus.listeners.JDAListener;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class SchematicsPlus extends JavaPlugin {

    public static Plugin plugin;
    public static Server server;
    public static Logger logger;
    public static Guild guild;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        plugin = getPlugin(this.getClass());
        server = plugin.getServer();
        logger = this.getLogger();
        Config.config = plugin.getConfig();
        plugin.saveDefaultConfig();

        if (Config.validateConfig()) {
            Config.updateConfig();
        } else {
            server.getPluginManager().disablePlugin(plugin);
            return;
        }

        DiscordSRV.api.requireIntent(GatewayIntent.GUILD_MESSAGES);
        DiscordSRV.api.subscribe(new DiscordSRVListener());
    }

    @Override
    public void onDisable() {
        try {
            DiscordUtil.getJda().removeEventListener(new JDAListener());
            DiscordSRV.api.unsubscribe(new DiscordSRVListener());
        } catch (NullPointerException ignored) {}
    }
}
