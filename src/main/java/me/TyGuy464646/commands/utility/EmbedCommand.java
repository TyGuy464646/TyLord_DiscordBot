package me.TyGuy464646.commands.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.TyGuy464646.TyLord;
import me.TyGuy464646.commands.Category;
import me.TyGuy464646.commands.Command;
import me.TyGuy464646.util.embeds.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.awt.*;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class EmbedCommand extends Command {

    public EmbedCommand(TyLord bot) {
        super(bot);
        this.name = "embed";
        this.description = "Create an embed";
        this.category = Category.UTILITY;

        this.subCommands.add(new SubcommandData("create", "Create an embed")
                .addOptions(
                        new OptionData(OptionType.CHANNEL, "channel", "The channel where it will be sent to", true),
                        new OptionData(OptionType.STRING, "raw_json", "Insert an embed json here. Generate here: discohook.org"),
                        new OptionData(OptionType.STRING, "title", "The title of your embed."),
                        new OptionData(OptionType.STRING, "color", "The color of your embed (hex code e.g. '#5865F2')."),
                        new OptionData(OptionType.STRING, "description", "The main text of your embed."),
                        new OptionData(OptionType.STRING, "footer", "The footer of your embed."),
                        new OptionData(OptionType.BOOLEAN, "timestamp", "Should the embed have a timestamp?"),
                        new OptionData(OptionType.STRING, "thumbnail", "The thumbnail of your embed (image url)."),
                        new OptionData(OptionType.STRING, "image", "The (large) image of your embed (image url)"),
                        new OptionData(OptionType.STRING, "url", "The url the title links to (any url)")
                ));
    }

    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().setEphemeral(true).queue();
        OptionMapping channelOption = event.getOption("channel");

        switch (event.getSubcommandName()) {
            case "create" -> {
                OptionMapping rawJson = event.getOption("raw_json");
                if (rawJson != null) {
                    try {
                        JsonParser.toEmbed(event, rawJson.getAsString(), channelOption.getAsMessageChannel());
                        event.getHook().sendMessage("Embed successfully created.").queue();
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    EmbedBuilder embedBuilder = new EmbedBuilder();

                    // Title
                    OptionMapping title = event.getOption("title");
                    OptionMapping url = event.getOption("url");
                    if (title != null && url != null)
                        embedBuilder.setTitle(title.getAsString(), url.getAsString());
                    else if (title != null)
                        embedBuilder.setTitle(title.getAsString());

                    // Color
                    OptionMapping color = event.getOption("color");
                    if (color != null)
                        embedBuilder.setColor(Color.decode(color.getAsString()));

                    // Description
                    OptionMapping description = event.getOption("description");
                    if (description != null)
                        embedBuilder.setDescription(description.getAsString());

                    // Footer
                    OptionMapping footer = event.getOption("footer");
                    if (footer != null)
                        embedBuilder.setFooter(footer.getAsString());

                    // Timestamp
                    OptionMapping timestamp = event.getOption("timestamp");
                    if (timestamp != null && timestamp.getAsBoolean())
                        embedBuilder.setTimestamp(OffsetDateTime.now());

                    // Thumbnail
                    OptionMapping thumbnail = event.getOption("thumbnail");
                    if (thumbnail != null)
                        embedBuilder.setThumbnail(thumbnail.getAsString());

                    // Image
                    OptionMapping image = event.getOption("image");
                    if (image != null)
                        embedBuilder.setImage(image.getAsString());

                    event.getGuild().getTextChannelById(channelOption.getAsLong()).sendMessageEmbeds(embedBuilder.build()).queue();
                    event.getHook().sendMessage("Embed successfully created.").queue();
                }
            }
        }
    }
}
