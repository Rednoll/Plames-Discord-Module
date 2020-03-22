package com.inwaiders.plames.modules.discord.web.bots.ajax;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.inwaiders.plames.modules.discord.domain.bot.DiscordBot;

@RestController
@RequestMapping("web/controller/ajax/ds/bot")
public class DiscordBotWebAjax {

	@PostMapping("/active")
	public ResponseEntity activeToggle(@RequestBody JsonNode json) {
		
		if(!json.has("bot") || !json.get("bot").isNumber()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
		if(!json.has("active") || !json.get("active").isBoolean()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
	
		long botId = json.get("bot").asLong();
		boolean active = json.get("active").asBoolean();
	
		DiscordBot bot = DiscordBot.getById(botId);
		
		if(bot != null) {
			
			bot.setActive(active);
			bot.save();
		
			return new ResponseEntity(HttpStatus.OK);
		}
		
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}
	
	@PostMapping(value = "/description", consumes = "application/json;charset=UTF-8", produces = "text/plain;charset=UTF-8")
	public ResponseEntity<String> description(@RequestBody JsonNode json) {
	
		if(!json.has("bot") || !json.get("bot").isNumber()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
		
		long botId = json.get("bot").asLong();
		
		DiscordBot bot = DiscordBot.getById(botId);
		
		if(bot != null) {
			
			try {
				
				return new ResponseEntity<String>(bot.getDescription(), HttpStatus.OK);
			}
			catch(Exception e) {
				
				return new ResponseEntity<String>("Ошибка загрузки данных", HttpStatus.OK);
			}
		}
		
		return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
	}
}
