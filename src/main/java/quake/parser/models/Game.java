package quake.parser.models;

import java.util.Map;

public class Game {

	private String name;
	private Integer totalKills;
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

	public Map<String, Integer> getKillsForPlayers() {
		return killsForPlayers;
	}

	public void setKillsForPlayers( Map<String, Integer> killsForPlayers ) {
		this.killsForPlayers = killsForPlayers;
	}

	@Override
	public String toString() {
		return "Game [name=" + name + ", totalKills=" + totalKills + ", killsForPlayers=" + killsForPlayers + "]";
	}

}
