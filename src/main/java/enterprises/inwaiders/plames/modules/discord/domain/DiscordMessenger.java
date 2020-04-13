package enterprises.inwaiders.plames.modules.discord.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.security.auth.login.LoginException;

import enterprises.inwaiders.plames.api.locale.PlamesLocale;
import enterprises.inwaiders.plames.domain.messenger.impl.MessengerImpl;
import enterprises.inwaiders.plames.modules.discord.domain.bot.DiscordBot;
import enterprises.inwaiders.plames.modules.discord.domain.profile.DiscordProfile;

@Entity
public class DiscordMessenger extends MessengerImpl<DiscordProfile>{

	@Override
	public void init() {
		
		List<DiscordBot> bots = DiscordBot.getAll();
	
		for(DiscordBot bot : bots) {
			
			try {
				
				if(bot.isActive()) {
					
					bot.initApi();
				}
			}
			catch (LoginException e) {
				
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public String getWebDescription() {
		 
		return "- "+PlamesLocale.getSystemMessage("messenger.discord.description.profiles", DiscordProfile.getCount())+"<br/>- "+PlamesLocale.getSystemMessage("messenger.discord.description.bots", DiscordBot.getCount());
	}

	@Override
	public String getName() {
		
		return "discord";
	}
	
	@Override
	public String getType() {
	
		return "ds";
	}
}
