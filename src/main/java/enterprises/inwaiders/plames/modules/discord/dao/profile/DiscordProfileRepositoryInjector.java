package enterprises.inwaiders.plames.modules.discord.dao.profile;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import enterprises.inwaiders.plames.modules.discord.domain.profile.DiscordProfile;

@Service
public class DiscordProfileRepositoryInjector {

	@Autowired
	private DiscordProfileRepository repository;
	
	@PostConstruct
	private void inject() {
		
		DiscordProfile.setRepository(repository);
	}
}
