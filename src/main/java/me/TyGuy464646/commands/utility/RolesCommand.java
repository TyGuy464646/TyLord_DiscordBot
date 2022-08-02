package me.TyGuy464646.commands.utility;

import me.TyGuy464646.TyLord;
import me.TyGuy464646.commands.Category;
import me.TyGuy464646.commands.Command;
import me.TyGuy464646.util.embeds.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class RolesCommand extends Command {

	public RolesCommand(TyLord bot) {
		super(bot);
		this.name = "roles";
		this.description = "Display server roles and member counts.";
		this.category = Category.UTILITY;
	}

	public void execute(SlashCommandInteractionEvent event) {
		StringBuilder content = new StringBuilder();
		for (Role role : event.getGuild().getRoles()) {
			if (!role.isManaged()) {
				content.append(role.getAsMention());
				content.append("\n");
			}
		}

		EmbedBuilder embed = new EmbedBuilder()
				.setColor(EmbedColor.DEFAULT.color)
				.setTitle("All Roles")
				.setDescription(content);
		event.replyEmbeds(embed.build()).queue();
	}

	public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {
	}
}
