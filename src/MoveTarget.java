
public enum MoveTarget {
	SELECTED_POKEMON(0),
	SPECIAL(1<<0),
	RANDOM(1<<2),
	BOTH_ENEMIES(1<<3),
	USER(1<<4),
	ALL_EXCEPT_USER(1<<5),
	ENEMY_SIDE(1<<6);
	
	private int flagValue;
	
	private MoveTarget(int flagValue) {
		this.flagValue = flagValue;
	}
	
	public int getFlagValue() {
		return flagValue;
	}
}
