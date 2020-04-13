package enterprises.inwaiders.plames.modules.discord.web.bots;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import enterprises.inwaiders.plames.modules.discord.domain.bot.DiscordBot;

@Controller
public class BotsPageController {

	@GetMapping("/ds/bots")
	public String mainPage(Model model) {
		
		List<DiscordBot> bots = DiscordBot.getAll();
		
		model.addAttribute("bots", bots);
		
		return "ds_bots";
	}
}
