package me.TyGuy464646.commands.utility;

import me.TyGuy464646.TyLord;
import me.TyGuy464646.commands.Category;
import me.TyGuy464646.commands.Command;
import me.TyGuy464646.util.embeds.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class PingCommand extends Command {

    public PingCommand(TyLord bot) {
        super(bot);
        this.name = "ping";
        this.description = "Display bot latency.";
        this.category = Category.UTILITY;
    }

    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        long time = System.currentTimeMillis();
        event.getHook().sendMessage(":signal_strength: Ping").queue(m -> {
            long latency = System.currentTimeMillis() - time;
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(":ping_pong: Pong!");
            embed.addField("Latency", latency + "ms", false);
            embed.addField("Discord API", event.getJDA().getGatewayPing() + "ms", false);
            embed.setColor(EmbedColor.DEFAULT.color);
            m.editMessageEmbeds(embed.build()).override(true).queue();
        });
    }

    public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {

    }
}
