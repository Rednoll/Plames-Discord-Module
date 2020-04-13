package enterprises.inwaiders.plames.modules.discord.web.bots.rest;

import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import enterprises.inwaiders.plames.modules.discord.domain.bot.DiscordBot;

@RestController
@RequestMapping("api/ds/rest")
public class DiscordBotRestController {
	
	@Autowired
	private ObjectMapper mapper;
	
	@GetMapping(value = "/bots/{id}", produces = "application/json; charset=UTF-8")
	public ObjectNode get(@PathVariable long id) {
		
		DiscordBot bot = DiscordBot.getById(id);
	
		ObjectNode node = mapper.createObjectNode();
		
			node.put("id", bot.getId());
			node.put("name", bot.getName());
			node.put("token", bot.getToken());

		return node;
	}
	
	@PostMapping(value = "/bots")
	public ObjectNode create(@RequestBody DiscordBot bot) {

		bot.save();
		
		bot = DiscordBot.getById(bot.getId());
		
		try {
			
			bot.initApi();
		}
		catch(LoginException e) {

			e.printStackTrace();
		}
		
		return get(bot.getId());
	}
	
	@PutMapping(value = "/bots/{id}") 
	public ResponseEntity save(@PathVariable long id, @RequestBody JsonNode node) {
		
		DiscordBot bot = DiscordBot.getById(id);
	
		if(bot == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
		
		if(node.has("name") && node.get("name").isTextual()) {
		
			bot.setName(node.get("name").asText());
		}
		
		if(node.has("token") && node.get("token").isTextual()) {
			
			bot.setToken(node.get("token").asText());
		}

		bot.save();
		
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/bots/{id}")
	public ResponseEntity delete(@PathVariable long id) {
	
		DiscordBot bot = DiscordBot.getById(id);
		
		if(bot != null) {		
			
			bot.delete();
					
			return new ResponseEntity<>(HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
}