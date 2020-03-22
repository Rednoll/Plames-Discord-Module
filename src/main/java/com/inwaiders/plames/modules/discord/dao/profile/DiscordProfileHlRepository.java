package com.inwaiders.plames.modules.discord.dao.profile;

import java.util.List;

import com.inwaiders.plames.dao.HighLevelRepository;
import com.inwaiders.plames.modules.discord.domain.profile.DiscordProfile;

public abstract class DiscordProfileHlRepository<T extends DiscordProfile> implements HighLevelRepository<T> {

	private static DiscordProfileHlRepository instance = null;
	
	public abstract T getByDiscordId(Long id);
	public abstract T getById(Long id);
	public abstract List<DiscordProfile> getAll();
	
	public static void setRepository(DiscordProfileHlRepository rep) {
		
		instance = rep;
	}
	
	public static DiscordProfileHlRepository getRepository() {
		
		return instance;
	}
}
