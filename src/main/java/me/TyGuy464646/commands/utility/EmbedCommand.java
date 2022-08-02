package me.TyGuy464646.commands.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.TyGuy464646.TyLord;
import me.TyGuy464646.commands.Category;
import me.TyGuy464646.commands.Command;
import me.TyGuy464646.util.embeds.EmbedUtils;
import me.TyGuy464646.util.embeds.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class EmbedCommand extends Command {

	public EmbedCommand(TyLord bot) {
		super(bot);
		this.name = "embed";
		this.description = "Create an embed";
		this.category = Category.UTILITY;
		this.permission = Permission.MANAGE_SERVER;

		this.subCommands.add(new SubcommandData("create", "Create an embed.")
				.addOptions(
						new OptionData(OptionType.CHANNEL, "channel", "The channel where it will be sent to", true)
								.setChannelTypes(ChannelType.TEXT, ChannelType.NEWS),
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
		this.subCommands.add(new SubcommandData("remove", "Removes embeds from a message.")
				.addOptions(
						new OptionData(OptionType.STRING, "message_url", "The url of the message you want to remove an embed from.", true),
						new OptionData(OptionType.INTEGER, "position", "The position of the embed you want to remove (leave blank for all - first embed is 1).", false, true)
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
						event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("Embed successfully created.")).queue();
					} catch (JsonProcessingException e) {
						throw new RuntimeException(e);
					}
				} else {
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
			case "remove" -> {
				OptionMapping message_url = event.getOption("message_url");
				OptionMapping position = event.getOption("position");
				String[] messageParsed = message_url.getAsString().split("/");
				try {
					long guildID = Long.parseLong(messageParsed[4]);
					long channelID = Long.parseLong(messageParsed[5]);
					long messageID = Long.parseLong(messageParsed[6]);

					if (guildID == event.getGuild().getIdLong()) {
						event.getGuild().getTextChannelById(channelID).retrieveMessageById(messageID).queue(message -> {
							String author = message.getAuthor().getAsTag();
							if (author.equals(event.getJDA().getSelfUser().getAsTag())) {
								if (position != null) {
									List<MessageEmbed> newEmbed = new ArrayList<>();
									newEmbed.addAll(message.getEmbeds());
									newEmbed.remove(position.getAsInt());
									message.editMessageEmbeds(newEmbed).queue();
									event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("Embeds modified!")).queue();
								} else {
									event.getGuild().getTextChannelById(channelID).deleteMessageById(messageID).queue();
									event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("Embeds deleted!")).queue();
								}
							} else {
								event.getHook().sendMessageEmbeds(EmbedUtils.createError("That's not a message sent by me")).queue();
							}
						});
					} else
						event.getHook().sendMessageEmbeds(EmbedUtils.createError("The message you are trying to delete is not in this server.")).queue();
				} catch (ArrayIndexOutOfBoundsException e) {
					throw new RuntimeException(e);
				}
			}
			default -> {
				event.getHook().sendMessageEmbeds(EmbedUtils.createError("This command doesn't exist.")).queue();
				throw new IllegalStateException("Unexpected value: " + event.getSubcommandName());
			}
		}
	}

	public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {
		if (event.getName().equals("embed") && event.getSubcommandName().equals("remove")) {
			OptionMapping message_url = event.getOption("message_url");

			String[] messageParsed = message_url.getAsString().split("/");
			try {
				long guildID = Long.parseLong(messageParsed[4]);
				long channelID = Long.parseLong(messageParsed[5]);
				long messageID = Long.parseLong(messageParsed[6]);

				event.getGuild().getTextChannelById(channelID).retrieveMessageById(messageID).queue(message -> {
					int numEmbeds = message.getEmbeds().size();
					List<Choice> choices = new ArrayList<>();
					for (int i = 0; i < numEmbeds; i++) {
						choices.add(new Choice("Embed " + String.valueOf(i + 1), i));
					}
					event.replyChoices(choices).queue();
				});
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
