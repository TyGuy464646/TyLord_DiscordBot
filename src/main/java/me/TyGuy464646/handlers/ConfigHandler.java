package me.TyGuy464646.handlers;

import com.mongodb.client.model.Filters;
import me.TyGuy464646.TyLord;
import me.TyGuy464646.data.cache.Config;
import net.dv8tion.jda.api.entities.Guild;
import org.bson.conversions.Bson;

public class ConfigHandler {

	private final Guild guild;
	private final TyLord bot;
	private final Bson filter;
	private Config config;

	public ConfigHandler(TyLord bot, Guild guild) {
		this.guild = guild;
		this.bot = bot;

		// Get POJO object from database
		this.filter = Filters.eq("guild", guild.getIdLong());
		this.config = bot.database.config.find(filter).first();
		if (this.config == null) {
			this.config = new Config(guild.getIdLong());
			bot.database.config.insertOne(config);
		}
	}

	public Config getConfig() {
		return config;
	}

	public boolean isPremium() {
		Long premiumTimestamp = bot.database.config.find(filter).first().getPremium();
		return premiumTimestamp != null && premiumTimestamp >= System.currentTimeMillis();
	}
}
