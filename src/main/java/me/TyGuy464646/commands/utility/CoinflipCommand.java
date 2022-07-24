package me.TyGuy464646.commands.utility;

import me.TyGuy464646.TyLord;
import me.TyGuy464646.commands.Category;
import me.TyGuy464646.commands.Command;
import me.TyGuy464646.util.embeds.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Random;

public class CoinflipCommand extends Command {

    private final Random random;

    public CoinflipCommand(TyLord bot) {
        super(bot);
        this.name = "coinflip";
        this.description = "Flip a coin.";
        this.category = Category.UTILITY;
        this.random = new Random();
    }

    public void execute(SlashCommandInteractionEvent event) {
        int bound = 2;
        int result = random.nextInt(bound) + 1;

        String stringResult = result == 1 ? "Heads" : "Tails";

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(EmbedColor.DEFAULT.color)
                .setDescription(":coin: You flipped a coin and got **" + stringResult + "**");
        event.replyEmbeds(embedBuilder.build()).queue();
    }

    public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {}
}
