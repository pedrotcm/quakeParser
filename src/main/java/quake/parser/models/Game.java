package quake.parser.models;

import java.util.List;
import java.util.Map;

public class Game {

	private String name;
	private Integer totalKills;
	private List<Player> players;
	private Map<String, Integer> killsForPlayers;

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public Integer getTotalKills() {
		return totalKills;
	}

	public void setTotalKills( Integer totalKills ) {
		this.totalKills = totalKills;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers( List<Player> players ) {
		this.players = players;
	}

	public Map<String, Integer> getKillsForPlayers() {
		return killsForPlayers;
	}

	public void setKillsForPlayers( Map<String, Integer> killsForPlayers ) {
		this.killsForPlayers = killsForPlayers;
	}

}
