public enum Type {
	//TODO : handle effectiveness and Type.MYSTERY properly
    NORMAL, FIGHTING, FLYING, POISON, GROUND, ROCK, BUG, GHOST, FIRE, WATER, GRASS, ELECTRIC, PSYCHIC, ICE, DRAGON, STEEL, DARK, MYSTERY, NONE;
	
    // defType2 should be Type.NONE if the defending pokemon has only one type
    public static int modulateDamageByType(int damage, Type atk, Type def) {
    	damage = damage * effectivenessMultiplier(atk, def) / 10;
    	return damage;
    }

    private static int effectivenessMultiplier(Type atkType, Type defType) {
        if (defType == NONE || atkType == NONE) { 
            return 10;
        } else {
            int val = typeTable[typeIndex(atkType)][typeIndex(defType)];
            return val;
        }
    }
    
    public static boolean isImmune(Type atk, Type def1, Type def2) {
    	return effectivenessMultiplier(atk, def1) * effectivenessMultiplier(atk, def2) == 0;
    }
    
    public static boolean isSuperEffective(Type atk, Type def1, Type def2) {
    	return effectivenessMultiplier(atk, def1) * effectivenessMultiplier(atk, def2) > 100;
    }

    // returns index associated with this type (in the order written)
    // Type.NONE will return -1
    public static int typeIndex(Type t) {
        switch (t) {
        case NORMAL:
            return 0;
        case FIGHTING:
            return 1;
        case FLYING:
            return 2;
        case POISON:
            return 3;
        case GROUND:
            return 4;
        case ROCK:
            return 5;
        case BUG:
            return 6;
        case GHOST:
            return 7;
        case FIRE:
            return 8;
        case WATER:
            return 9;
        case GRASS:
            return 10;
        case ELECTRIC:
            return 11;
        case PSYCHIC:
            return 12;
        case ICE:
            return 13;
        case DRAGON:
            return 14;
        case STEEL:
            return 15;
        case DARK:
            return 16;
        default: // NONE
            return -1;
        }
    }
    
    public static Type getHiddenPowerType(int i) {
    	switch(i) {
    	case 0 : return FIGHTING;
    	case 1 : return FLYING;
    	case 2 : return POISON;
    	case 3 : return GROUND;
    	case 4 : return ROCK;
    	case 5 : return BUG;
    	case 6 : return GHOST;
    	case 7 : return STEEL;
    	case 8 : return FIRE;
    	case 9 : return WATER;
    	case 10: return GRASS;
    	case 11: return ELECTRIC;
    	case 12: return PSYCHIC;
    	case 13: return ICE;
    	case 14: return DRAGON;
    	case 15: return DARK;
    	default: return NONE; // should never occur
    	}
    }

    // typeTable[i][j] is type i's effectiveness against type j
    private static final int[][] typeTable = { // TODO : externalize type table somehow and account for Foresight
            { 10, 10, 10, 10, 10,  5, 10,  0, 10, 10, 10, 10, 10, 10, 10,  5, 10 },
            //{ 20, 10,  5,  5, 10, 20,  5, 10, 10, 10, 10, 10,  5, 20, 10, 20, 20 }, //20 - Foresight with Fighting
            { 20, 10,  5,  5, 10, 20,  5,  0, 10, 10, 10, 10,  5, 20, 10, 20, 20 }, //20 - Normal
            { 10, 20, 10, 10, 10,  5, 20, 10, 10, 10, 20,  5, 10, 10, 10,  5, 10 },
            { 10, 10, 10,  5,  5,  5, 10,  5, 10, 10, 20, 10, 10, 10, 10,  0, 10 },
            { 10, 10,  0, 20, 10, 20,  5, 10, 20, 10,  5, 20, 10, 10, 10, 20, 10 },
            { 10,  5, 20, 10,  5, 10, 20, 10, 20, 10, 10, 10, 10, 20, 10,  5, 10 },
            { 10,  5,  5,  5, 10, 10, 10,  5,  5, 10, 20, 10, 20, 10, 10,  5, 20 },
            {  0, 10, 10, 10, 10, 10, 10, 20, 10, 10, 10, 10, 20, 10, 10,  5,  5 },
            { 10, 10, 10, 10, 10,  5, 20, 10,  5,  5, 20, 10, 10, 20,  5, 20, 10 },
            { 10, 10, 10, 10, 20, 20, 10, 10, 20,  5,  5, 10, 10, 10,  5, 10, 10 },
            { 10, 10,  5,  5, 20, 20,  5, 10,  5, 20,  5, 10, 10, 10,  5,  5, 10 },
            { 10, 10, 20, 10,  0, 10, 10, 10, 10, 20,  5,  5, 10, 10,  5, 10, 10 },
            { 10, 20, 10, 20, 10, 10, 10, 10, 10, 10, 10, 10,  5, 10, 10,  5,  0 },
            { 10, 10, 20, 10, 20, 10, 10, 10,  5,  5, 20, 10, 10,  5, 20,  5, 10 },
            { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 20,  5, 10 },
            { 10, 10, 10, 10, 10, 20, 10, 10,  5,  5, 10,  5, 10, 20, 10,  5, 10 },
            { 10,  5, 10, 10, 10, 10, 10, 20, 10, 10, 10, 10, 20, 10, 10,  5,  5 }, };

    public static boolean isPhysicalType(Type t) {
        return (typeIndex(t) >= typeIndex(Type.NORMAL) && typeIndex(t) <= typeIndex(Type.GHOST))
                || t == Type.STEEL || t == Type.NONE;
    }

}
