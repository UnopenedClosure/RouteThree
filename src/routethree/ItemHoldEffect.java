package routethree;

public enum ItemHoldEffect {
	NONE,
	RESTORE_HP,
	CURE_PAR,
	CURE_SLP,
	CURE_PSN,
	CURE_BRN,
	CURE_FRZ,
	RESTORE_PP,
	CURE_CONFUSION,
	CURE_STATUS,
	CONFUSE_SPICY,
	CONFUSE_DRY,
	CONFUSE_SWEET,
	CONFUSE_BITTER,
	CONFUSE_SOUR,
	ATTACK_UP,
	DEFENSE_UP,
	SPEED_UP,
	SP_ATTACK_UP,
	SP_DEFENSE_UP,
	CRITICAL_UP,
	RANDOM_STAT_UP,
	EVASION_UP,
	RESTORE_STATS,
	MACHO_BRACE,
	EXP_SHARE,
	QUICK_CLAW,
	HAPPINESS_UP,
	CURE_ATTRACT,
	CHOICE_BAND,
	FLINCH,
	BUG_POWER,
	DOUBLE_PRIZE,
	REPEL,
	SOUL_DEW,
	DEEP_SEA_TOOTH,
	DEEP_SEA_SCALE,
	CAN_ALWAYS_RUN,
	PREVENT_EVOLVE,
	FOCUS_BAND,
	LUCKY_EGG,
	SCOPE_LENS,
	STEEL_POWER,
	LEFTOVERS,
	DRAGON_SCALE,
	LIGHT_BALL,
	GROUND_POWER,
	ROCK_POWER,
	GRASS_POWER,
	DARK_POWER,
	FIGHTING_POWER,
	ELECTRIC_POWER,
	WATER_POWER,
	FLYING_POWER,
	POISON_POWER,
	ICE_POWER,
	GHOST_POWER,
	PSYCHIC_POWER,
	FIRE_POWER,
	DRAGON_POWER,
	NORMAL_POWER,
	UP_GRADE,
	SHELL_BELL,
	LUCKY_PUNCH,
	METAL_POWDER,
	THICK_CLUB,
	STICK;
	
	public boolean isTypeBoosting(Type type) {
		return this == BUG_POWER && type == Type.BUG
			|| this == STEEL_POWER && type == Type.STEEL
			|| this == GROUND_POWER && type == Type.GROUND
			|| this == ROCK_POWER && type == Type.ROCK
			|| this == GRASS_POWER && type == Type.GRASS
			|| this == DARK_POWER && type == Type.DARK
			|| this == FIGHTING_POWER && type == Type.FIGHTING
			|| this == ELECTRIC_POWER && type == Type.ELECTRIC
			|| this == WATER_POWER && type == Type.WATER
			|| this == FLYING_POWER && type == Type.FLYING
			|| this == POISON_POWER && type == Type.POISON
			|| this == ICE_POWER && type == Type.ICE
			|| this == GHOST_POWER && type == Type.GHOST
			|| this == PSYCHIC_POWER && type == Type.PSYCHIC
			|| this == FIRE_POWER && type == Type.FIRE
			|| this == DRAGON_POWER && type == Type.DRAGON
			|| this == NORMAL_POWER && type == Type.NORMAL;
	}
}
