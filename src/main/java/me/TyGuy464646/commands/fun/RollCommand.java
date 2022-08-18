package me.TyGuy464646.commands.fun;

import me.TyGuy464646.TyLord;
import me.TyGuy464646.commands.Category;
import me.TyGuy464646.commands.Command;
import me.TyGuy464646.util.embeds.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Random;

/**
 * {@link Command} that rolls a die.
 * @author TyGuy464646
 */
public class RollCommand extends Command {

	private final Random random;

	public RollCommand(TyLord bot) {
		super(bot);
		this.name = "roll";
		this.description = "Roll a dice.";
		this.category = Category.FUN;
		this.args.add(new OptionData(OptionType.INTEGER, "dice", "The number of sides on the dice").setMinValue(1).setMaxValue(1000000));
		this.random = new Random();
	}

	public void execute(SlashCommandInteractionEvent event) {
		OptionMapping option = event.getOption("dice");
		int bound = option != null ? option.getAsInt() : 6;
		if (bound == 0) bound = 1;
		int result = random.nextInt(bound) + 1;

		EmbedBuilder embedBuilder = new EmbedBuilder()
				.setColor(EmbedColor.DEFAULT.color)
				.setDescription(":game_die: You rolled a " + bound + "-sided dice and got: **" + result + "**");
		event.replyEmbeds(embedBuilder.build()).queue();
	}

	public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {
	}
}
