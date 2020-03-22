package com.inwaiders.plames.modules.discord.domain.bot.listeners;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inwaiders.plames.modules.discord.domain.bot.DiscordBot;
import com.inwaiders.plames.modules.discord.domain.profile.DiscordProfile;

import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Component("DiscordMessageListener")
@Scope("prototype")
public class DiscordMessageListener extends ListenerAdapter {
	
	private ObjectMapper mapper = new ObjectMapper();
	
	long botId = -1;

	public DiscordMessageListener(long botId) {
		
		this.botId = botId;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {

		if(event.getAuthor().isBot()) return;
		
		String text = event.getMessage().getContentDisplay();
		
		long senderDiscrodId = event.getAuthor().getIdLong();
			
		DiscordBot bot = DiscordBot.getById(botId);
		DiscordProfile profile = DiscordProfile.getByDiscordId(senderDiscrodId);
		
		if(profile == null) {
			
			profile = DiscordProfile.create(senderDiscrodId);
				profile.addBot(bot);
		}
		
		bot.fromUser(profile, text);
	}
}
