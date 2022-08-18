package me.TyGuy464646.commands.suggestions;

import me.TyGuy464646.TyLord;
import me.TyGuy464646.commands.Category;
import me.TyGuy464646.commands.Command;
import me.TyGuy464646.data.GuildData;
import me.TyGuy464646.handlers.SuggestionHandler;
import me.TyGuy464646.util.embeds.EmbedColor;
import me.TyGuy464646.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * {@link Command} that allows a user to make a suggestion on the suggestion board.
 *
 * @author TyGuy464646
 */
public class SuggestCommand extends Command {
	public SuggestCommand(TyLord bot) {
		super(bot);
		this.name = "suggest";
		this.description = "Add a suggestion to the suggestion board.";
		this.category = Category.SUGGESTIONS;
		this.args.add(new OptionData(OptionType.STRING, "suggestion", "The content for your suggestion", true));
	}

	public void execute(SlashCommandInteractionEvent event) {
		event.deferReply().queue();

		// Check if suggestion board has been setup
		SuggestionHandler suggestionHandler = GuildData.get(event.getGuild()).suggestionHandler;
		if (!suggestionHandler.isSetup()) {
			String text = "The suggestion channel has not been set!";
			event.getHook().sendMessageEmbeds(EmbedUtils.createError(text)).queue();
			return;
		}

		// Create suggesiton embed
		String content = event.getOption("suggestion").getAsString();
		EmbedBuilder embed = new EmbedBuilder()
				.setColor(EmbedColor.DEFAULT.color)
				.setTitle("Suggestion #" + suggestionHandler.getNumber())
				.setDescription(content);

		// Add author to embed if anonymous mode is turned off
		if (suggestionHandler.isAnonymous())
			embed.setAuthor("Anonymous", null, "https://cdn.discordapp.com/embed/avatars/0.png");
		else embed.setAuthor(event.getUser().getAsTag(), null, event.getUser().getEffectiveAvatarUrl());

		// Make sure channel is valid
		TextChannel channel = event.getGuild().getTextChannelById(suggestionHandler.getChannel());
		if (channel == null) {
			String text = "The suggesiton channel has been deleted, please set a new one!";
			event.getHook().sendMessageEmbeds(EmbedUtils.createError(text)).queue();
			return;
		}

		// Add suggestion and reaction buttons
		channel.sendMessageEmbeds(embed.build()).queue(suggestion -> {
			suggestionHandler.add(suggestion.getIdLong(), event.getUser().getIdLong());
			try {
				suggestion.addReaction(Emoji.fromUnicode("\\uF53C")).queue();
				suggestion.addReaction(Emoji.fromUnicode("\\uF53D")).queue();
			} catch (InsufficientPermissionException ignored) { }
		});

		// Send a response message
		String text = "Your suggestion has been added to the suggestion channel!";
		event.getHook().sendMessageEmbeds(EmbedUtils.createDefault(text)).queue();
	}

	public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {

	}
}
