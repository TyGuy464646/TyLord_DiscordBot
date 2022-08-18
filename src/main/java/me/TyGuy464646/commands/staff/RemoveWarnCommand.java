package me.TyGuy464646.commands.staff;

import me.TyGuy464646.TyLord;
import me.TyGuy464646.commands.Category;
import me.TyGuy464646.commands.Command;
import me.TyGuy464646.data.GuildData;
import me.TyGuy464646.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * {@link Command} that removes a warning by user or ID.
 *
 * @author TyGuy464646
 */
public class RemoveWarnCommand extends Command {

	public RemoveWarnCommand(TyLord bot) {
		super(bot);
		this.name = "remove-warn";
		this.description = "Remove warnings by user or ID.";
		this.category = Category.STAFF;
		this.args.add(new OptionData(OptionType.USER, "user", "User to clear ALL warnings for."));
		this.args.add(new OptionData(OptionType.INTEGER, "id", "ID number for the warning to remove")
				.setMinValue(1));
		this.permission = Permission.MODERATE_MEMBERS;
	}

	public void execute(SlashCommandInteractionEvent event) {
		GuildData data = GuildData.get(event.getGuild());
		OptionMapping userOption = event.getOption("user");
		OptionMapping idOption = event.getOption("id");

		MessageEmbed embed;
		if (idOption != null) {
			// Remove warning with this ID
			int count = data.moderationHandler.removeWarning(idOption.getAsInt());
			if (count == 1) {
				embed = EmbedUtils.createDefault("Warning #" + idOption.getAsInt() + " has been removed.");
			} else {
				embed = EmbedUtils.createError("Unable to find a warning with that ID!");
				event.replyEmbeds(embed).setEphemeral(true).queue();
				return;
			}
			event.replyEmbeds(embed).queue();
		} else if (userOption != null) {
			// Remove all warnings from user
			User target = userOption.getAsUser();
			int count = data.moderationHandler.clearWarnings(target.getIdLong());
			if (count > 1)
				embed = EmbedUtils.createDefault(count + " warnings have been removed for " + target.getName() + ".");
			else if (count == 1)
				embed = EmbedUtils.createDefault("1 warning has been removed for " + target.getName() + ".");
			else {
				embed = EmbedUtils.createError("That user does not have any warnings!");
				event.replyEmbeds(embed).setEphemeral(true).queue();
				return;
			}
			event.replyEmbeds(embed).queue();
		} else {
			// No user or ID specified
			event.replyEmbeds(EmbedUtils.createError("You must specify a user or a warning ID!")).setEphemeral(true).queue();
		}
	}

	public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {

	}
}
