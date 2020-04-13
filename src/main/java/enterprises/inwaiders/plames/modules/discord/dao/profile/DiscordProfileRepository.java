package enterprises.inwaiders.plames.modules.discord.dao.profile;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import enterprises.inwaiders.plames.modules.discord.domain.bot.DiscordBot;
import enterprises.inwaiders.plames.modules.discord.domain.profile.DiscordProfile;

@Repository
public interface DiscordProfileRepository extends JpaRepository<DiscordProfile, Long>{

	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Query("SELECT p FROM DiscordProfile p WHERE p.discordId = :id AND p.deleted != true")
	public DiscordProfile getByDiscordId(@Param("id") long discordId);
	
	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Override
	@Query("SELECT p FROM DiscordProfile p WHERE p.id = :id AND p.deleted != true")
	public DiscordProfile getOne(@Param(value = "id") Long id);
	
	@Override
	@Query("SELECT p FROM DiscordProfile p WHERE p.deleted != true")
	public List<DiscordProfile> findAll();
	
	@Override
	@Query("SELECT COUNT(*) FROM DiscordProfile p WHERE p.deleted != true")
	public long count();
}
