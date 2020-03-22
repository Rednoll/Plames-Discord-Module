package com.inwaiders.plames.modules.discord.dao.bot;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inwaiders.plames.modules.discord.domain.bot.DiscordBot;

@Service
public class DiscordBotRepositoryInjector {

	@Autowired
	private DiscordBotRepository repository;

	@PostConstruct
	private void inject() {
		
		DiscordBot.setRepository(repository);
	}
}
