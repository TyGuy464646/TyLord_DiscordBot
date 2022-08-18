package me.TyGuy464646.commands.suggestions;

import me.TyGuy464646.TyLord;
import me.TyGuy464646.commands.Category;
import me.TyGuy464646.commands.Command;
import me.TyGuy464646.data.GuildData;
import me.TyGuy464646.handlers.SuggestionHandler;
import me.TyGuy464646.listeners.ButtonListener;
import me.TyGuy464646.util.embeds.EmbedColor;
import me.TyGuy464646.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageAction;

import java.util.ArrayList;

/**
 * Admin {@link Command} to setup and modify the suggestion board.
 *
 * @author TyGuy464646
 */
public class SuggestionsCommand extends Command {

	public SuggestionsCommand(TyLord bot) {
		super(bot);
		this.name = "suggestions";
		this.description = "Setup and modify the suggestions config.";
		this.category = Category.SUGGESTIONS;
		this.subCommands.add(new SubcommandData("create", "Sets a channel to become the suggestion board.")
				.addOptions(new OptionData(OptionType.CHANNEL, "channel", "The channel to set as the suggestion board.")
						.setChannelTypes(ChannelType.TEXT, ChannelType.NEWS)));
		this.subCommands.add(new SubcommandData("dm", "Toggle private messages on suggestion response."));
		this.subCommands.add(new SubcommandData("annonymous", "Toggle anonymous mode for suggestions."));
		this.subCommands.add(new SubcommandData("config", "Display the current suggestions config."));
		this.subCommands.add(new SubcommandData("reset", "Reset all suggestion board data and settings."));
		this.permission = Permission.MANAGE_SERVER;
	}

	public void execute(SlashCommandInteractionEvent event) {
		event.deferReply().queue();
		Guild guild = event.getGuild();
		SuggestionHandler suggestionHandler = GuildData.get(guild).suggestionHandler;

		String text = null;
		switch(event.getSubcommandName()) {
			case "create" -> {
				// Setup suggestion board
				OptionMapping channelOption = event.getOption("channel");
				if (channelOption == null) {
					// Create new suggestion channel
					guild.createTextChannel("suggestions").queue(textChannel -> {
						ArrayList<Permission> denyPerms = new ArrayList<>();
						denyPerms.add(Permission.MESSAGE_ADD_REACTION);
						denyPerms.add(Permission.MESSAGE_SEND);

						ArrayList<Permission> allowPerms = new ArrayList<>();
						allowPerms.add(Permission.VIEW_CHANNEL);
						allowPerms.add(Permission.MESSAGE_HISTORY);

						textChannel.upsertPermissionOverride(guild.getPublicRole()).deny(denyPerms).setAllowed(allowPerms).queue();
						suggestionHandler.setChannel(textChannel.getIdLong());
					});
					text = "Created a new suggestion channel!";
				} else {
					// Set suggestion board to mentioned channel
					try {
						long channel = channelOption.getAsGuildChannel().getIdLong();
						String channelMention = "<#" + channel + ">";
						suggestionHandler.setChannel(channel);
						text = "Set the suggesiton channel to " + channelMention;
					} catch (NullPointerException e) {
						text = "You can only set a text channel as a suggestion board!";
						event.getHook().sendMessageEmbeds(EmbedUtils.createError(text)).queue();
						return;
					}
				}
			}
			case "dm" -> {
				boolean isEnabled = suggestionHandler.toggleResponseDM();
				if (isEnabled)
					text = "Response DMs have been **enabled** for suggestions!";
				else text = "Response DMs have been **disabled** for suggestions!";
			}
			case "anonymous" -> {
				boolean isEnabled = suggestionHandler.toggleAnonymous();
				if (isEnabled)
					text = "Anonymous mode has been **enabled** for suggestions!";
				else text = "Anonymous mode has been **disabled** for suggestions!";
			}
			case "config" -> {
				text = "";
				if (suggestionHandler.getChannel() != null)
					text += "\n**Channel:** <#" + suggestionHandler.getChannel() + ">";
				else text += "\n**Channel:** none";

				text += "\n**DM on Response:** " + suggestionHandler.hasResponseDM();
				text += "\n**Anonymous Mode:** " + suggestionHandler.isAnonymous();
				event.getHook().sendMessage(text).queue();
				return;
			}
			case "reset" -> {
				text = "Would you like to reset the suggestions system?\nThis will delete ALL data!";
				WebhookMessageAction<Message> action = event.getHook().sendMessageEmbeds(EmbedUtils.createDefault(text));
				ButtonListener.sendResetMenu(event.getUser().getId(), "Suggestion", action);
				return;
			}
		}
		event.getHook().sendMessageEmbeds(new EmbedBuilder()
				.setColor(EmbedColor.DEFAULT.color)
				.setDescription(text)
				.build()
		).queue();
	}

	public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {

	}
}
