package enterprises.inwaiders.plames.modules.discord.dao.bot;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import enterprises.inwaiders.plames.modules.discord.domain.bot.DiscordBot;

@Repository
public interface DiscordBotRepository extends JpaRepository<DiscordBot, Long>{

	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Override
	@Query("SELECT b FROM DiscordBot b WHERE b.id = :id AND b.deleted != true")
	public DiscordBot getOne(@Param(value = "id") Long id);
	
	@Override
	@Query("SELECT b FROM DiscordBot b WHERE b.deleted != true")
	public List<DiscordBot> findAll();
	
	@Override
	@Query("SELECT COUNT(*) FROM DiscordBot b WHERE b.deleted != true")
	public long count();
}
