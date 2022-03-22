package routethree;

public enum Nature {
	HARDY(null, null),
	LONELY(Stat.ATK, Stat.DEF),
	BRAVE(Stat.ATK, Stat.SPE),
	ADAMANT(Stat.ATK, Stat.SPA),
	NAUGHTY(Stat.ATK, Stat.SPD),
	BOLD(Stat.DEF, Stat.ATK),
	DOCILE(null, null),
	RELAXED(Stat.DEF, Stat.SPE),
	IMPISH(Stat.DEF, Stat.SPA),
	LAX(Stat.DEF, Stat.SPD),
	TIMID(Stat.SPE, Stat.ATK),
	HASTY(Stat.SPE, Stat.DEF),
	SERIOUS(null, null),
	JOLLY(Stat.SPE, Stat.SPA),
	NAIVE(Stat.SPE, Stat.SPD),
	MODEST(Stat.SPA, Stat.ATK),
	MILD(Stat.SPA, Stat.DEF),
	QUIET(Stat.SPA, Stat.SPE),
	BASHFUL(null, null),
	RASH(Stat.SPA, Stat.SPD),
	CALM(Stat.SPD, Stat.ATK),
	GENTLE(Stat.SPD, Stat.DEF),
	SASSY(Stat.SPD, Stat.SPE),
	CAREFUL(Stat.SPD, Stat.SPA),
	QUIRKY(null, null);
	
	private static int INCREASE_MULT = 11;
	private static int DECREASE_MULT = 9;
	private static int DIV = 10;
	
	
	private final Stat increased;
	private final Stat decreased;
	
	private Nature(Stat increased, Stat decreased) {
		this.increased = increased;
		this.decreased = decreased;
	}
	
	public Stat getIncreased() {
		return increased;
	}
	
	public Stat getDecreased() {
		return decreased;
	}
	

	public int getAlteredStat(int value, Stat stat) {
		if(this.getIncreased() == null)
			return value;
		else if(this.getIncreased() == stat) 
			return value * INCREASE_MULT / DIV;
		else if(this.getDecreased() == stat)
			return value * DECREASE_MULT / DIV;
		return value;
	}
	
	public static Nature getNatureFromPersonalityValue(int personalityValue) {
		return Nature.values()[personalityValue % 25];
	}
	
	public static Nature getEnum(String value) {
        for(Nature v : values())
            if(v.name().equalsIgnoreCase(value)) return v;
        throw new IllegalArgumentException();
    }
}
