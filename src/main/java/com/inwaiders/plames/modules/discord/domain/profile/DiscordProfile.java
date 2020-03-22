package com.inwaiders.plames.modules.discord.domain.profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.inwaiders.plames.api.messenger.message.Message;
import com.inwaiders.plames.dao.EntityLink;
import com.inwaiders.plames.domain.messenger.profile.impl.UserProfileBase;
import com.inwaiders.plames.modules.discord.dao.profile.DiscordProfileHlRepository;
import com.inwaiders.plames.modules.discord.dao.profile.DiscordProfileRepository;
import com.inwaiders.plames.modules.discord.domain.bot.DiscordBot;
import com.inwaiders.plames.spring.SpringUtils;

@Entity
@Table(name = "discord_profiles")
public class DiscordProfile extends UserProfileBase {

	private static transient DiscordProfileRepository repository;
	
	@Column(name = "discord_id")
	private long discordId = -1;
	
	@ManyToMany(cascade = CascadeType.MERGE, mappedBy = "users", fetch = FetchType.EAGER)
	private List<DiscordBot> bots = new ArrayList<>();
	
	@JoinColumn(name = "priority_bot_id")
	@ManyToOne(cascade = CascadeType.ALL)
	private DiscordBot priorityBot = null;
	
	@Override
	public int hashCode() {
		
		return Objects.hash(getId(), deleted);
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DiscordProfile other = (DiscordProfile) obj;
		return discordId == other.discordId && getId() == other.getId() && deleted == other.deleted;
	}

	public void addBot(DiscordBot bot) {
		
		if(bots.isEmpty()) {
			
			setPriorityBot(bot);
		}
		
		bots.add(bot);
		bot.getUsers().add(this);
		
		this.save();
		bot.save();
	}
	
	@Override
	public boolean receiveMessage(Message message) {
		
		DiscordBot bot = pickBot();
		
		return bot.sendMessage(message);
	}
	
	public DiscordBot pickBot() {
		
		if(getPriorityBot() != null) return getPriorityBot();
		
		for(DiscordBot bot : getBots()) {
			
			if(bot.isActive()) {
				
				return bot;
			}
		}
		
		return null;
	}
	
	public void setPriorityBot(DiscordBot bot) {
		
		this.priorityBot = bot;
	}
	
	public DiscordBot getPriorityBot() {
		
		return this.priorityBot;
	}
	
	public List<DiscordBot> getBots() {
		
		return this.bots;
	}
	
	public void setDiscordId(long id) {
		
		this.discordId = id;
	}
	
	public long getDiscordId() {
		
		return this.discordId;
	}
	
	public String getHumanSign() {
		
		DiscordBot bot = pickBot();
		
		net.dv8tion.jda.api.entities.User dsUser = bot.getDiscordApi().getUserById(discordId);
		
		return "[ds] Nickname: "+dsUser.getName()+"#"+dsUser.getDiscriminator();
	}
	
	@Override
	public String getMessengerType() {
		
		return "ds";
	}
	
	public void save() {
		
		if(!deleted) {
			
			repository.save(this);
		}
	}
	
	public void delete() {
		
		deleted = true;
		repository.save(this);
	}
	
	public static DiscordProfile create(long discrodId) {
		
		DiscordProfile profile = new DiscordProfile();
			profile.setDiscordId(discrodId);
		
		profile = repository.saveAndFlush(profile);
	
		return profile;
	}
	
	public static DiscordProfile getById(long id) {
		
		return repository.getOne(id);
	}
	
	public static DiscordProfile getByDiscordId(long id) {
		
		return repository.getByDiscordId(id);
	}
	
	public static List<DiscordProfile> getAll() {
		
		return repository.findAll();
	}
	
	public static long getCount() {
		
		return repository.count();
	}
	
	public static void setRepository(DiscordProfileRepository rep) {
		
		repository = rep;
	}
	
	public static class HighLevelRepository extends DiscordProfileHlRepository<DiscordProfile> {

		public EntityLink getLink(DiscordProfile bot) {
			
			return new EntityLink(SpringUtils.getEntityName(DiscordProfile.class), bot.getId());
		}

		@Override
		public void save(DiscordProfile profile) {
			
			profile.save();
		}
		
		@Override
		public DiscordProfile getByDiscordId(Long id) {
			
			return DiscordProfile.getByDiscordId(id);
		}

		@Override
		public DiscordProfile getById(Long id) {
			
			return DiscordProfile.getById(id);
		}

		@Override
		public List<DiscordProfile> getAll() {
			
			return DiscordProfile.getAll();
		}
	}
}
