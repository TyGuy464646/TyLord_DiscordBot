package me.TyGuy464646;

import io.github.cdimascio.dotenv.Dotenv;
import me.TyGuy464646.commands.CommandRegistry;
import me.TyGuy464646.listeners.ButtonListener;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;

public class TyLord {

	private static final Logger LOGGER = LoggerFactory.getLogger(TyLord.class);

	public final @NotNull Dotenv config;
	public final @NotNull ShardManager shardManager;

	public final @NotNull ButtonListener buttonListener;

	/**
	 * Builds bot shards and registers commands and modules.
	 *
	 * @throws LoginException throws if bot token is invalid
	 */
	public TyLord() throws LoginException, SQLException {
		// Setup Database
		config = Dotenv.configure().ignoreIfMissing().load();

		// Build JDA shards
		DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(config.get("TOKEN"));
		builder.setStatus(OnlineStatus.ONLINE);
		builder.setActivity(Activity.playing("/help"));
		builder.enableIntents(GatewayIntent.GUILD_MEMBERS,
				GatewayIntent.GUILD_MESSAGES,
				GatewayIntent.GUILD_MESSAGE_REACTIONS);
		builder.addEventListeners(new CommandRegistry(this));
		shardManager = builder.build();

		// Register Listeners
		buttonListener = new ButtonListener();
		shardManager.addEventListener(
				buttonListener
		);
	}

	public static void main(String[] args) throws LoginException, SQLException {
		new TyLord();
	}
}
