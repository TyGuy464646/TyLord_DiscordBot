package me.TyGuy464646.commands.staff;

import me.TyGuy464646.TyLord;
import me.TyGuy464646.commands.Category;
import me.TyGuy464646.commands.Command;
import me.TyGuy464646.data.GuildData;
import me.TyGuy464646.util.embeds.EmbedColor;
import me.TyGuy464646.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * {@link Command} that unbans a user from the guild.
 *
 * @author TyGuy464646
 */
public class UnbanCommand extends Command {

	public UnbanCommand(TyLord bot) {
		super(bot);
		this.name = "unban";
		this.description = "Unbans a user from your server.";
		this.category = Category.STAFF;
		this.args.add(new OptionData(OptionType.STRING, "user_id", "The ID of the user to unban.", true));
		this.permission = Permission.BAN_MEMBERS;
		this.botPermission = Permission.BAN_MEMBERS;
	}

	public void execute(SlashCommandInteractionEvent event) {
		String input = event.getOption("user_id").getAsString();
		if (input.equals(event.getJDA().getSelfUser().getId())) {
			event.replyEmbeds(EmbedUtils.createError("Ah yes let me just unban myself...")).setEphemeral(true).queue();
			return;
		}

		try {
			event.getJDA().retrieveUserById(input).queue(user -> {
				// Remove banned user from database and any timer task
				GuildData.get(event.getGuild()).moderationHandler.removeBan(user);

				// Send confirmation message
				event.replyEmbeds(new EmbedBuilder()
						.setAuthor(user.getAsTag() + " has been unbanned", null, user.getEffectiveAvatarUrl())
						.setColor(EmbedColor.DEFAULT.color)
						.build()
				).queue();
			}, fail -> event.replyEmbeds(EmbedUtils.createError("That user does not exist!")).setEphemeral(true).queue());
		} catch (NumberFormatException e) {
			event.replyEmbeds(EmbedUtils.createError("That is not a valid user ID!")).setEphemeral(true).queue();
		}
	}

	public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {

	}
}
