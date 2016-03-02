package me.kahjiit.OITC.arena;

public class PlayerStats {

	private boolean alive;
	private int kills;
	private int deaths;
	private int lives;
	private int streak;
	private int xp;
	
	public PlayerStats() {
		alive = true;
		kills = 0;
		lives = 10;
		streak = 0;
	}
	
	public void addDeath() {
		deaths ++;
		lives --;
	}
	
	public void addKill() {
		kills ++;
	}
	
	public void addStreak() {
		streak ++;
	}
	
	public void addXP(int xp) {
		this.xp += xp;
	}
	
	public int getDeaths() {
		return deaths;
	}
	
	public int getKills() {
		return kills;
	}
	
	public int getLives() {
		return lives;
	}
	
	public int getStreak() {
		return streak;
	}
	
	public int getXP() {
		return xp;
	}
	
	public boolean isAlive() {
		return alive;
	}
	
	public void reset() {
		alive = true;
		deaths = 0;
		kills = 0;
		lives = 10;
		streak = 0;
		xp = 0;
	}
	
	public void resetStreak() {
		streak = 0;
	}
	
	public void setAlive(boolean alive) {
		this.alive = alive;
	}
}