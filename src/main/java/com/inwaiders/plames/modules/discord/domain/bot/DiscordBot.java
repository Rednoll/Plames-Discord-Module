package com.inwaiders.plames.modules.discord.domain.bot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.security.auth.login.LoginException;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.inwaiders.plames.api.locale.PlamesLocale;
import com.inwaiders.plames.api.messenger.message.Message;
import com.inwaiders.plames.dao.EntityLink;
import com.inwaiders.plames.modules.discord.dao.bot.DiscordBotHlRepository;
import com.inwaiders.plames.modules.discord.dao.bot.DiscordBotRepository;
import com.inwaiders.plames.modules.discord.domain.bot.listeners.DiscordMessageListener;
import com.inwaiders.plames.modules.discord.domain.profile.DiscordProfile;
import com.inwaiders.plames.spring.ApplicationContextProvider;
import com.inwaiders.plames.spring.SpringUtils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

@Cache(region = "messengers-additionals-cache-region", usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "discord_bots")
public class DiscordBot {

	private static transient DiscordBotRepository repository;
	
	private static transient Map<String, JDA> discordApis = new HashMap<String, JDA>();
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@Column(name = "name")
	private String name = null;
	
	@Column(name = "token")
	private String token = null;
	
	@ManyToMany(cascade = CascadeType.MERGE)
	@JoinTable(name = "discord_profiles_bots_mtm", joinColumns = @JoinColumn(name = "discord_bot_id"), inverseJoinColumns = @JoinColumn(name = "discord_profile_id"))
	private Set<DiscordProfile> users = new HashSet<>();
	
	@Column(name = "active")
	private boolean active = true;
	
	@Column(name = "deleted")
	private volatile boolean deleted = false;
	
	public DiscordBot() {
		
	}
	
	@Override
	public int hashCode() {
		
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DiscordBot other = (DiscordBot) obj;
		return active == other.active && Objects.equals(id, other.id) && Objects.equals(token, other.token);
	}

	public boolean fromUser(DiscordProfile profile, String text) {
		
		if(!isActive()) return false;
		
		profile.fromUser(text);
		
		return true;
	}
	
	public boolean sendMessage(Message message) {
		
		if(!isActive()) return false;
		
		DiscordProfile targetProfile = (DiscordProfile) message.getReceiver();
		String text = message.getDisplayText();
		
		return sendMessage(targetProfile.getDiscordId(), text);
	}
	
	private boolean sendMessage(long targetDiscordId, String text) {

		if(!isActive()) return false;
		
		JDA discordApi = this.getDiscordApi();
		
		net.dv8tion.jda.api.entities.User discordUser = discordApi.getUserById(targetDiscordId);
		
		discordUser.openPrivateChannel().queue((channel)-> {
	
			channel.sendMessage(text).queue();
		});
		
		return true;
	}
	
	public String getDescription() {
		
		String result = "";
		
		result += PlamesLocale.getSystemMessage("discord_bot.description.token", shortyToken());
		result += "<br/>";
		
		return result;
	}
	
	public String shortyToken() {
		
		return token.length() > 12 ? token.substring(0, 6)+"..."+token.substring(token.length()-6) : token;
	}
	
	public JDA getDiscordApi() {
		
		if(discordApis.containsKey(this.token)) {
			
			return discordApis.get(this.token);
		}
		else {
			
			try {
				
				initApi();
			}
			catch(LoginException e) {
				
				e.printStackTrace();
			}
			
			return discordApis.get(this.token);
		}
	}
	
	public void initApi() throws LoginException {
		
		JDA newDiscrodApi = new JDABuilder(getToken()).build();
			
			DiscordMessageListener listener = ApplicationContextProvider.getApplicationContext().getBean(DiscordMessageListener.class, (long) this.getId());
			newDiscrodApi.addEventListener(listener);
				
		discordApis.put(this.token, newDiscrodApi);	
	}
	
	public void setName(String name) {
		
		this.name = name;
	}
	
	public String getName() {
		
		return this.name;
	}
	
	public void setActive(boolean active) {
		
		this.active = active;
	}
	
	public boolean isActive() {
		
		return this.active;
	}
	
	public Set<DiscordProfile> getUsers() {
		
		return this.users;
	}
	
	public void setToken(String token) {
		
		this.token = token;
	}
	
	public String getToken() {
		
		return this.token;
	}
	
	public Long getId() {
		
		return this.id;
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
	
	public static DiscordBot create() {
		
		DiscordBot bot = new DiscordBot();
		
		bot = repository.saveAndFlush(bot);
		
		return bot;
	}
	
	public static DiscordBot getById(long id) {
		
		return repository.getOne(id);
	}
	
	public static List<DiscordBot> getAll() {
	
		return repository.findAll();
	}
	
	public static long getCount() {
		
		return repository.count();
	}
	
	public static void setRepository(DiscordBotRepository rep) {
		
		repository = rep;
	}
	
	public static class HighLevelRepository extends DiscordBotHlRepository<DiscordBot> {
		
		public EntityLink getLink(DiscordBot bot) {
	
			return new EntityLink(SpringUtils.getEntityName(DiscordBot.class), bot.getId());
		}
		
		@Override
		public void save(DiscordBot bot) {
			
			bot.save();
		}
		
		@Override
		public DiscordBot getById(Long id) {
			
			return DiscordBot.getById(id);
		}

		@Override
		public List<DiscordBot> getAll() {
			
			return DiscordBot.getAll();
		}
	}
}
