package enterprises.inwaiders.plames.modules.discord;

import java.util.List;

import javax.security.auth.login.LoginException;

import enterprises.inwaiders.plames.api.application.ApplicationAgent;
import enterprises.inwaiders.plames.api.locale.PlamesLocale;
import enterprises.inwaiders.plames.domain.messenger.impl.MessengerImpl;
import enterprises.inwaiders.plames.domain.module.impl.ModuleBase;
import enterprises.inwaiders.plames.modules.discord.dao.bot.DiscordBotHlRepository;
import enterprises.inwaiders.plames.modules.discord.dao.profile.DiscordProfileHlRepository;
import enterprises.inwaiders.plames.modules.discord.domain.DiscordMessenger;
import enterprises.inwaiders.plames.modules.discord.domain.bot.DiscordBot;
import enterprises.inwaiders.plames.modules.discord.domain.profile.DiscordProfile;

public class DiscordModule extends ModuleBase implements ApplicationAgent {

	private static final DiscordModule instance = new DiscordModule();
	
	private DiscordModule() {
		
	}
	
	@Override
	public void preInit() {
		
		DiscordBotHlRepository.setRepository(new DiscordBot.HighLevelRepository());
		DiscordProfileHlRepository.setRepository(new DiscordProfile.HighLevelRepository());
	}
	
	@Override
	public void init() {
	
		MessengerImpl mess = MessengerImpl.getByType("ds");
		
		if(mess == null) {
			
			DiscordMessenger ds = new DiscordMessenger();
			
			ds.save();
		}
	
		List<DiscordBot> bots = DiscordBot.getAll();
	
		for(DiscordBot bot : bots) {
			
			try {
				
				bot.initApi();
			}
			catch (LoginException e) {
			
				e.printStackTrace();
			}
		}
	}

	public String getDescription() {
		
		return PlamesLocale.getSystemMessage("module.discord.description");
	}
	
	@Override
	public String getName() {
		
		return "Discord Integration";
	}

	@Override
	public String getLicenseKey() {
	
		return null;
	}

	@Override
	public long getId() {
		
		return 5634245;
	}

	@Override
	public String getType() {
		
		return "integration";
	}

	@Override
	public String getVersion() {
		
		return "1V";
	}

	@Override
	public long getSystemVersion() {
		
		return 0;
	}
	
	public static DiscordModule getInstance() {
		
		return instance;
	}

	@Override
	public String getDisplayName() {
		
		return "Discord";
	}

	@Override
	public String getTag() {
		
		return "ds";
	}
}
