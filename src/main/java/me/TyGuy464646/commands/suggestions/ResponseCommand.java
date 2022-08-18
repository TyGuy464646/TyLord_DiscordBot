package me.TyGuy464646.commands.suggestions;

import me.TyGuy464646.TyLord;
import me.TyGuy464646.commands.Category;
import me.TyGuy464646.commands.Command;
import me.TyGuy464646.data.GuildData;
import me.TyGuy464646.handlers.SuggestionHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * {@link Command} that responds to suggestions on the suggesiton board.
 *
 * @author TyGuy464646
 */
public class ResponseCommand extends Command {

	public ResponseCommand(TyLord bot) {
		super(bot);
		this.name = "respond";
		this.description = "Respond to a suggestion on the suggestion board.";
		this.category = Category.SUGGESTIONS;
		this.args.add(new OptionData(OptionType.STRING, "response", "The response to the suggestion.", true)
				.addChoice("Approve", "APPROVE")
				.addChoice("Consider", "CONSIDER")
				.addChoice("Deny", "DENY")
				.addChoice("Implement", "IMPLEMENT"));
		this.args.add(new OptionData(OptionType.INTEGER, "number", "The suggestion number to respond to.", true)
				.setMinValue(1)
				.setMaxValue(Integer.MAX_VALUE));
		this.args.add(new OptionData(OptionType.STRING, "reason", "The reason for your response."));
		this.permission = Permission.MANAGE_SERVER;
	}

	public void execute(SlashCommandInteractionEvent event) {
		event.deferReply().queue();
		String responseString = event.getOption("response").getAsString();
		SuggestionHandler.SuggestionResponse response = SuggestionHandler.SuggestionResponse.valueOf(responseString);

		int id = event.getOption("number").getAsInt() - 1;
		OptionMapping reason = event.getOption("reason");

		GuildData.get(event.getGuild()).suggestionHandler.respond(event, id, reason, response);
	}

	public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {

	}
}
