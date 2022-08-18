package me.TyGuy464646.commands.staff;

import me.TyGuy464646.TyLord;
import me.TyGuy464646.commands.Category;
import me.TyGuy464646.commands.Command;
import me.TyGuy464646.data.GuildData;
import me.TyGuy464646.util.embeds.EmbedColor;
import me.TyGuy464646.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.RoleAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Command that sets/creates the mute role to give on /mute.
 *
 * @author TyGuy464646
 */
public class MuteRoleCommand extends Command {

	public MuteRoleCommand(TyLord bot) {
		super(bot);
		this.name = "mute-role";
		this.description = "Create or set a mute role for the server.";
		this.category = Category.STAFF;
		this.permission = Permission.MANAGE_SERVER;
		this.botPermission = Permission.MANAGE_ROLES;
		this.subCommands.add(new SubcommandData("set", "Set an existing role as the mute role.")
				.addOption(OptionType.ROLE, "role", "The role to set as the mute role.", true));
		this.subCommands.add(new SubcommandData("create", "Create a mute role for this server.")
				.addOption(OptionType.STRING, "name", "Name for the mute role."));
	}

	public void execute(SlashCommandInteractionEvent event) {
		Guild guild = event.getGuild();
		GuildData data = GuildData.get(guild);

		// Check for admin permissions
		Role botRole = event.getGuild().getBotRole();
		if (!botRole.hasPermission(Permission.ADMINISTRATOR)) {
			event.replyEmbeds(EmbedUtils.createError("I am unable to create roles. Please check my permissions and role position.")).setEphemeral(true).queue();
			return;
		}

		event.deferReply().setEphemeral(true).queue();
		switch (event.getSubcommandName()) {
			case "set" -> {
				// Set existing role as the mute role
				Role role = event.getOption("role").getAsRole();
				if (role.isManaged() || role.isPublicRole()) {
					event.getHook().sendMessageEmbeds(EmbedUtils.createError("I cannot set bot/managed role as the mute role!")).queue();
					return;
				}
				data.moderationHandler.setMuteRole(role.getIdLong());
				String text = EmbedUtils.GREEN_TICK + " The " + role.getAsMention() + " role will be used for the `/mute` command.";
				event.getHook().sendMessageEmbeds(new EmbedBuilder()
						.setDescription(text)
						.setColor(EmbedColor.DEFAULT.color)
						.build()
				).queue();
			}
			case "create" -> {
				// Create new role
				RoleAction action = guild.createRole().setColor(EmbedColor.ERROR.color).setPermissions(new ArrayList<>());
				OptionMapping nameOption = event.getOption("name");
				if (nameOption != null) action = action.setName(nameOption.getAsString());
				else action = action.setName("Muted");

				action.queue(role -> {
					// Create list of denied perms
					List<Permission> denyPerms = Arrays.asList(
							Permission.MESSAGE_SEND,
							Permission.MESSAGE_ADD_REACTION,
							Permission.MESSAGE_SEND_IN_THREADS,
							Permission.CREATE_PUBLIC_THREADS,
							Permission.CREATE_PRIVATE_THREADS
					);

					// Apply overwrites to channels
					long roleID = role.getIdLong();
					for (GuildChannel channel : guild.getChannels()) {
						channel.getPermissionContainer().getManager().putRolePermissionOverride(roleID, null, denyPerms).queue();
					}
					data.moderationHandler.setMuteRole(roleID);
					String text = EmbedUtils.GREEN_TICK + " The " + role.getAsMention() + " role will be used for the `/mute` command.";
					event.getHook().sendMessageEmbeds(new EmbedBuilder()
							.setDescription(text)
							.setColor(EmbedColor.DEFAULT.color)
							.build()
					).queue();
				});
			}
		}
	}

	public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {

	}
}
