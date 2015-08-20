package quake.parser.models;

import java.util.Map;

public class Game {

	private String name;
	private Integer totalKills;
	private Map<String, Integer> killsForPlayers;
	private Map<String, Integer> meansOfDeath;

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

	public Map<String, Integer> getMeansOfDeath() {
		return meansOfDeath;
	}

	public void setMeansOfDeath( Map<String, Integer> meansOfDeath ) {
		this.meansOfDeath = meansOfDeath;
	}

	@Override
	public String toString() {
		return "Game [name=" + name + ", totalKills=" + totalKills + ", killsForPlayers=" + killsForPlayers + "]";
	}

}
