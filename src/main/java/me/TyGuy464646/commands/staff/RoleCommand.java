package me.TyGuy464646.commands.staff;

import me.TyGuy464646.TyLord;
import me.TyGuy464646.commands.Category;
import me.TyGuy464646.commands.Command;
import me.TyGuy464646.data.GuildData;
import me.TyGuy464646.handlers.ModerationHandler;
import me.TyGuy464646.util.embeds.EmbedColor;
import me.TyGuy464646.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * {@link Command} that gives or removes a role from a user.
 *
 * @author TyGuy464646
 */
public class RoleCommand extends Command {

	public RoleCommand(TyLord bot) {
		super(bot);
		this.name = "role";
		this.description = "Manage roles for a user.";
		this.category = Category.STAFF;
		this.subCommands.add(new SubcommandData("give", "Gives a role to a user.")
				.addOption(OptionType.USER, "user", "The user to give the role to.", true)
				.addOption(OptionType.ROLE, "role", "The role to give.", true));
		this.subCommands.add(new SubcommandData("remove", "Removes a role from a user.")
				.addOption(OptionType.USER, "user", "The user to remove role from.", true)
				.addOption(OptionType.ROLE, "role", "The role to remove.", true));
		this.permission = Permission.MANAGE_ROLES;
		this.botPermission = Permission.MANAGE_ROLES;
	}

	public void execute(SlashCommandInteractionEvent event) {
		Member member = event.getOption("user").getAsMember();
		Role role = event.getOption("role").getAsRole();
		if (member == null) {
			event.replyEmbeds(EmbedUtils.createError("That user is not in your server!")).setEphemeral(true).queue();
			return;
		}
		if (role.isManaged() || role.isPublicRole()) {
			event.replyEmbeds(EmbedUtils.createError("I cannot give/remove bot or managed roles!")).setEphemeral(true).queue();
			return;
		}

		// Check target role position
		ModerationHandler moderationHandler = GuildData.get(event.getGuild()).moderationHandler;
		if (!moderationHandler.canTargetMember(member)) {
			event.replyEmbeds(EmbedUtils.createError("This member cannot be updated.")).setEphemeral(true).queue();
			return;
		}

		StringBuilder text = new StringBuilder();
		text.append(EmbedUtils.GREEN_TICK + " Changed roles for ").append(member.getEffectiveName()).append(",\n");
		switch (event.getSubcommandName()) {
			case "give" -> {
				text.append("**+").append(role.getAsMention()).append("**");
				event.getGuild().addRoleToMember(member, role).queue(null, fail -> {});
			}
			case "remove" -> {
				text.append("**-").append(role.getAsMention()).append("**");
				event.getGuild().removeRoleFromMember(member, role).queue(null, fail -> {});
			}
		}
		event.replyEmbeds(new EmbedBuilder()
				.setDescription(text.toString())
				.setColor(EmbedColor.DEFAULT.color)
				.build()
		).queue();
	}

	public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {

	}
}
