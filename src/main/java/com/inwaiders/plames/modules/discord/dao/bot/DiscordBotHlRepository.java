package com.inwaiders.plames.modules.discord.dao.bot;

import java.util.List;

import com.inwaiders.plames.dao.HighLevelRepository;
import com.inwaiders.plames.dao.user.UserHlRepository;
import com.inwaiders.plames.modules.discord.domain.bot.DiscordBot;

public abstract class DiscordBotHlRepository<T extends DiscordBot> implements HighLevelRepository<T> {

	private static DiscordBotHlRepository instance = null;
	
	public abstract T getById(Long id);
	public abstract List<DiscordBot> getAll();
	
	public static void setRepository(DiscordBotHlRepository rep) {
		
		instance = rep;
	}
	
	public static DiscordBotHlRepository getRepository() {
		
		return instance;
	}
}
