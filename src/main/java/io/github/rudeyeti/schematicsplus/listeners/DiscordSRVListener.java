package io.github.rudeyeti.schematicsplus.listeners;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.util.DiscordUtil;
import io.github.rudeyeti.schematicsplus.SchematicsPlus;

public class DiscordSRVListener {
    @Subscribe
    public void discordReadyEvent(DiscordReadyEvent event) {
        DiscordUtil.getJda().addEventListener(new JDAListener());
        SchematicsPlus.guild = DiscordSRV.getPlugin().getMainGuild();
    }
}
