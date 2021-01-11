package io.github.rudeyeti.schematicsplus.listeners;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import io.github.rudeyeti.schematicsplus.Config;
import io.github.rudeyeti.schematicsplus.Schematic;
import io.github.rudeyeti.schematicsplus.SchematicsPlus;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class JDAListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getGuild() == SchematicsPlus.guild && event.getChannel().getId().equals(Config.channelId) && !event.getAuthor().isBot()) {
            Plugin worldEdit = SchematicsPlus.server.getPluginManager().getPlugin("WorldEdit");
            File schematicsFolder = new File(worldEdit.getDataFolder() + File.separator + "schematics");

            try {
                if (!schematicsFolder.exists()) {
                    schematicsFolder.mkdir();
                }

                // If the message is not a url, an exception will be thrown.
                URL url = new URL(event.getMessage().getContentRaw());
                String downloadFile = Schematic.download(url, schematicsFolder);

                if (downloadFile.startsWith("Usage:")) {
                    Schematic.errorMessage(event, downloadFile);
                } else {
                    event.getChannel().sendMessage(downloadFile).queue();
                }
            } catch (MalformedURLException error) {
                List<Message.Attachment> attachments = event.getMessage().getAttachments();

                // Otherwise download an attachment if it exists.
                if (attachments.size() == 1) {
                    String fileName = attachments.get(0).getFileName();

                    if (fileName.endsWith(".schematic")) {
                        File file = new File(schematicsFolder, fileName);

                        if (!file.exists()) {
                            attachments.get(0).downloadToFile(file);
                            event.getChannel().sendMessage(Schematic.message(2, fileName)).queue();
                        } else {
                            Schematic.errorMessage(event, Schematic.message(1, fileName));
                        }
                    } else {
                        Schematic.errorMessage(event, Schematic.message(0, fileName));
                    }
                } else {
                    Schematic.errorMessage(event, "Usage: The message must either contain a link or have a schematic attached to it.");
                }
            }
        }
    }
}
