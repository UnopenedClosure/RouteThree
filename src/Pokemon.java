
public class Pokemon implements Battleable {
    private Species species;
    private int level;
    private Nature nature;
    private Ability ability;
    private IVs ivs;
    private int ev_hp, ev_hp_used;
    private int ev_atk, ev_atk_used;
    private int ev_def, ev_def_used;
    private int ev_spa, ev_spa_used;
    private int ev_spd, ev_spd_used;
    private int ev_spe, ev_spe_used;
    private int hp;
    private int atk;
    private int def;
    private int spa;
    private int spd;
    private int spe;
    private int totalExp;
    private Moveset moves;
    private boolean wild;
    private Item heldItem = null;
    private boolean hasPokerus = false;
    private boolean hasBoostedExp = false;
    private boolean atkBadge = false;
    private boolean defBadge = false;
    private boolean spaBadge = false;
    private boolean spdBadge = false;
    private boolean speBadge = false;

    // defaults to wild pokemon
    public Pokemon(Species s, int newLevel, Nature nature) {
        this(s, newLevel, nature, true);
        this.setAbility(s.getAbility1());
        setExpForLevel();
    }

    // default based off of species and level (works on all trainer pokemon,
    // except leaders' tms)
    public Pokemon(Species s, int newLevel, Nature nat, boolean wild) {
        species = s;
        level = newLevel;
        nature = nat;
        this.setAbility(s.getAbility1());
        ivs = new IVs();
        setZeroEVs();
        moves = Moveset.defaultMoveset(species, level);
        calculateStats();
        this.wild = wild;
        setExpForLevel();
    }

    // will work for leaders
    public Pokemon(Species s, int newLevel, Nature nat, Moveset moves, boolean wild) {
        this(s, newLevel, nat, wild);
        this.moves = moves;
        calculateStats();
        setExpForLevel();
    }

    public Pokemon(Species s, int newLevel, Nature nat, IVs ivs, boolean wild) {
        species = s;
        level = newLevel;
        nature = nat;
        this.setAbility(s.getAbility1());
        this.ivs = ivs;
        setZeroEVs();
        moves = Moveset.defaultMoveset(species, level);
        calculateStats();
        this.wild = wild;
        setExpForLevel();
    }

    public Pokemon(Species s, int newLevel, Nature nat, Moveset moves, IVs ivs, boolean wild) {
        species = s;
        level = newLevel;
        nature = nat;
        this.setAbility(s.getAbility1());
        this.ivs = ivs;
        setZeroEVs();
        this.moves = moves;
        calculateStats();
        this.wild = wild;
        setExpForLevel();
    }

    /*TODO Battle Tower poke
    public Pokemon(Species s, int newLevel, Nature nat, Moveset moves, IVs ivs, int hp, int atk, int def, int spe, int spA, int spD) {
        species = s;
        level = newLevel;
        nature = nat;
        this.ivs = ivs;
        this.hp = hp;
        this.atk = atk;
        this.def = def;
        this.spa = spA;
        this.spd = spD;
        this.spe = spe;
        this.moves = moves;
        this.battleTower = true;
        setExpForLevel();
    }
    */

    // TODO constructor which accepts EVs
    public void setZeroEVs() {
        ev_hp = ev_hp_used = 0;
        ev_atk = ev_atk_used = 0;
        ev_def = ev_def_used = 0;
        ev_spa = ev_spa_used = 0;
        ev_spd = ev_spd_used = 0;
        ev_spe = ev_spe_used = 0;
    }

    // call this to update your stats
    // automatically called on level ups/rare candies, but not just from gaining
    // stat EV
    public void calculateStats() {
        ev_hp_used = ev_hp;
        ev_atk_used = ev_atk;
        ev_def_used = ev_def;
        ev_spa_used = ev_spa;
        ev_spd_used = ev_spd;
        ev_spe_used = ev_spe;
        hp = calcHPWithIV(ivs.getHPIV());
        atk = calcAtkWithIV(ivs.getAtkIV(), nature);
        def = calcDefWithIV(ivs.getDefIV(), nature);
        spa = calcSpaWithIV(ivs.getSpaIV(), nature);
        spd = calcSpdWithIV(ivs.getSpdIV(), nature);
        spe = calcSpeWithIV(ivs.getSpeIV(), nature);
    }

    private int calcHPWithIV(int iv) {
        return calcStatNumerator(iv, species.getBaseHP(), ev_hp_used) * level / 100
                + level + 10;
    }

    private int calcAtkWithIV(int iv, Nature nat) {
        int stat = calcStatNumerator(iv, species.getBaseAtk(), ev_atk_used) * level
                / 100 + 5;
        return nat.getAlteredStat(stat, Stat.ATK);
    }

    private int calcDefWithIV(int iv, Nature nat) {
    	int stat = calcStatNumerator(iv, species.getBaseDef(), ev_def_used) * level
                / 100 + 5;
        return nat.getAlteredStat(stat, Stat.DEF);
    }

    private int calcSpaWithIV(int iv, Nature nat) {
    	int stat = calcStatNumerator(iv, species.getBaseSpa(), ev_spa_used) * level
                / 100 + 5;
        return nat.getAlteredStat(stat, Stat.SPA);
    }

    private int calcSpdWithIV(int iv, Nature nat) {
    	int stat = calcStatNumerator(iv, species.getBaseSpd(), ev_spd_used) * level
                / 100 + 5;
        return nat.getAlteredStat(stat, Stat.SPD);
    }
    
    private int calcSpeWithIV(int iv, Nature nat) {
    	int stat = calcStatNumerator(iv, species.getBaseSpe(), ev_spe_used) * level
                / 100 + 5;
        return nat.getAlteredStat(stat, Stat.SPE);
    }

    private int evCalc(int ev) {
        return ev / 4;
    }

    private int calcStatNumerator(int iv, int base, int ev) {
        return 2 * base + iv + evCalc(ev);
    }
    
    public int applyBadgeBoost(int value) {
    	return value * 11 / 10; // TODO: hardcoded
    }
    
    public int applyBadgeBoostIfHasBadge(int value, Stat stat) {
    	switch(stat) {
    	case ATK:
    		return hasAtkBadge() ? applyBadgeBoost(value) : value;
    	case DEF:
    		return hasDefBadge() ? applyBadgeBoost(value) : value;
    	case SPA:
    		return hasSpaBadge() ? applyBadgeBoost(value) : value;
    	case SPD:
    		return hasSpdBadge() ? applyBadgeBoost(value) : value;
    	case SPE:
    		return hasSpeBadge() ? applyBadgeBoost(value) : value;
    	default:
    		return value;
    	}
    }

    private void setExpForLevel() {
        totalExp = ExpCurve.lowestExpForLevel(species.getExpCurve(), level);
    }

    public IVs getIVs() {
        return ivs;
    }
    // TODO: EV setter

    public int getHP() {
        return hp;
    }

    // badge boosts
    // WARNING : not to be used for battle calculations, only display
    public int getAtk() {
        return (int) (atkBadge ? 11 * atk / 10 : atk);
    }

    public int getDef() {
        return (int) (defBadge ? 11 * def / 10 : def);
    }

    public int getSpa() {
        return (int) (spaBadge ? 11 * spa / 10 : spa);
    }

    public int getSpd() {
    	return (int) (spdBadge ? 11 * spd / 10 : spd);
    }

    public int getSpe() {
        return (int) (speBadge ? 11 * spe / 10 : spe);
    }

    public int getSpeWithIV(int iv, Nature nature) {
    	int spe = calcSpeWithIV(iv, nature);
        return applyBadgeBoostIfHasBadge(spe, Stat.SPE);
    }

    public Ability getAbility() {
		return ability;
	}

	public void setAbility(Ability ability) {
		this.ability = ability;
	}

	// not affected by badge boosts
    public int getTrueAtk() {
        return atk;
    }

    public int getTrueDef() {
        return def;
    }

    public int getTrueSpa() {
        return spa;
    }

    public int getTrueSpd() {
        return spd;
    }

    public int getTrueSpe() {
        return spe;
    }

    public int getLevel() {
        return level;
    }

    public Species getSpecies() {
        return species;
    }
    
    public Nature getNature() {
    	return nature;
    }

    public void setMoveset(Moveset m) {
        moves = m;
    }

    public Moveset getMoveset() {
        return moves;
    }

    public boolean isWild() {
        return wild;
    }

    public void setWild(boolean isWild) {
        this.wild = isWild;
    }

    public int getTotalExp() {
        return totalExp;
    }

    public int expGiven(int participants) {
        return (species.getBaseExp() / participants) * level / 7 * 3
                / (isWild() ? 3 : 2);
    }
    
    public Item getHeldItem() {
    	return heldItem;
    }
    
    public void setItem(Item newItem) {
    	this.heldItem = newItem;
    }

    public String toString() {
        return statsWithBoost();
    }

    public String statsWithBoost() {
        String endl = Constants.endl;
        StringBuilder sb = new StringBuilder();
        sb.append(levelNameNatureAbility() + " ");
        sb.append("EXP Needed: " + expToNextLevel() + "/" + expForLevel()
                + endl);
        sb.append("Stats WITH badge boosts:" + endl);
        sb.append(String.format("  %1$7s%2$7s%3$7s%4$7s%5$7s%6$7s", "HP",
                atkBadge ? "*ATK" : "ATK", defBadge ? "*DEF" : "DEF",
                spaBadge ? "*SPA" : "SPA", spdBadge ? "*SPD" : "SPD",
                speBadge ? "*SPE" : "SPE")
                + endl);
        sb.append(String.format("  %1$7s%2$7s%3$7s%4$7s%5$7s%6$7s", getHP(),
                getAtk(), getDef(), getSpa(), getSpd(), getSpe()) + endl);
        sb.append(String.format("IV%1$7s%2$7s%3$7s%4$7s%5$7s%6$7s",
                ivs.getHPIV(), ivs.getAtkIV(), ivs.getDefIV(), ivs.getSpaIV(),
                ivs.getSpdIV(), ivs.getSpeIV()) + endl);
        sb.append(String.format("EV%1$7s%2$7s%3$7s%4$7s%5$7s%6$7s", ev_hp,
                ev_atk, ev_def, ev_spa, ev_spd, ev_spe) + endl);
        sb.append(moves.toString() + endl);
        return sb.toString();
    }

    public String statsWithoutBoost() {
        String endl = Constants.endl;
        StringBuilder sb = new StringBuilder();
        sb.append(levelNameNatureAbility() + " ");
        sb.append("EXP Needed: " + expToNextLevel() + "/" + expForLevel()
                + endl);
        sb.append("Stats WITHOUT badge boosts:" + endl);
        sb.append(String.format("  %1$7s%2$7s%3$7s%4$7s%5$7s%6$7s", "HP",
                "ATK", "DEF", "SPA", "SPD", "SPE") + endl);
        sb.append(String.format("  %1$7s%2$7s%3$7s%4$7s%5$7s%6$7s", getHP(),
                getTrueAtk(), getTrueDef(), getTrueSpa(),getTrueSpd(), getTrueSpe())
                + endl);
        sb.append(String.format("IV%1$7s%2$7s%3$7s%4$7s%5$7s%6$7s",
                ivs.getHPIV(), ivs.getAtkIV(), ivs.getDefIV(), ivs.getSpaIV(),
                ivs.getSpdIV(), ivs.getSpeIV()) + endl);
        sb.append(String.format("EV%1$7s%2$7s%3$7s%4$7s%5$7s%6$7s", ev_hp,
                ev_atk, ev_def, ev_spa, ev_spd, ev_spe) + endl);
        sb.append(moves.toString() + endl);
        return sb.toString();
    }

    // utility getters
    public String levelNameNatureAbility() {
        return String.format("L%d %s [%s] #%s#", level, getDisplayName(), nature, ability);
    }

    // only for hash
    public String pokeName() {
        return getSpecies().getHashName();
    }
    
    public String getDisplayName() {
    	return getSpecies().getDisplayName();
    }
    
    /*
    public String pokeNameFixed() {
    	return getSpecies().getName().replace("\\u2642", " M").replace("\\u2640", " F"); // TODO : hacky
    }
	*/
    
    public String statsStr() {
        return String.format("%s/%s/%s/%s/%s/%s", getHP(), getAtk(), getDef(),
                getSpa(), getSpd(), getSpe());
    }
    
    public String trueStatsStr() {
        return String.format("%s/%s/%s/%s/%s/%s", getHP(), getTrueAtk(), getTrueDef(),
                getTrueSpa(), getTrueSpd(), getTrueSpe());
    }

    // experience methods
    // exp needed to get to next level
    public int expToNextLevel() {
        return ExpCurve.expToNextLevel(species.getExpCurve(), level, totalExp);
    }

    // total exp needed to get from this level to next level (no partial exp)
    public int expForLevel() {
        return ExpCurve.expForLevel(species.getExpCurve(), level);
    }

    // in game actions

    // gain num exp
    private void gainExp(int num) {
        num = (num * 3) / (hasBoostedExp ? 2 : 3);
        num = (num * 3) / (heldItem != null && heldItem.getHoldEffect() == ItemHoldEffect.LUCKY_EGG ? 2 : 3);
        totalExp += num;
        // update lvl if necessary
        while (expToNextLevel() <= 0 && level < 100) { //TODO hardcoded
            level++;
            calculateStats();
        }
    }

    // gain stat exp from a pokemon of species s
    private void gainEvs(Species s) { // TODO : code the EVs handling better
    	int MAX_EV_PER_STAT = 252; 
    	int MAX_TOTAL_EVS = 510;
    	
    	int hpYield = s.getHPEV() * (hasPokerus ? 2 : 1) * (heldItem != null && heldItem.getHoldEffect() == ItemHoldEffect.MACHO_BRACE ? 2 : 1);
    	int atkYield = s.getAtkEV() * (hasPokerus ? 2 : 1) * (heldItem != null && heldItem.getHoldEffect() == ItemHoldEffect.MACHO_BRACE ? 2 : 1);
    	int defYield = s.getDefEV() * (hasPokerus ? 2 : 1) * (heldItem != null && heldItem.getHoldEffect() == ItemHoldEffect.MACHO_BRACE ? 2 : 1);
    	int speYield = s.getSpeEV() * (hasPokerus ? 2 : 1) * (heldItem != null && heldItem.getHoldEffect() == ItemHoldEffect.MACHO_BRACE ? 2 : 1);
    	int spaYield = s.getSpaEV() * (hasPokerus ? 2 : 1) * (heldItem != null && heldItem.getHoldEffect() == ItemHoldEffect.MACHO_BRACE ? 2 : 1);
    	int spdYield = s.getSpdEV() * (hasPokerus ? 2 : 1) * (heldItem != null && heldItem.getHoldEffect() == ItemHoldEffect.MACHO_BRACE ? 2 : 1);
    	
    	int totalEVs = this.getTotalEVs();
    	int maxTotalYield = MAX_TOTAL_EVS - totalEVs;
    	
    	int maxHPYield = MAX_EV_PER_STAT - ev_hp;
    	int maxAtkYield = MAX_EV_PER_STAT - ev_atk;
    	int maxDefYield = MAX_EV_PER_STAT - ev_def;
    	int maxSpeYield = MAX_EV_PER_STAT - ev_spe;
    	int maxSpaYield = MAX_EV_PER_STAT - ev_spa;
    	int maxSpdYield = MAX_EV_PER_STAT - ev_spd;
    	
    	int effectiveHPYield = Math.max(0, Math.min(hpYield, Math.min(maxTotalYield, maxHPYield)));
    	ev_hp += effectiveHPYield;
    	maxTotalYield -= effectiveHPYield;
    	
    	int effectiveAtkYield = Math.max(0, Math.min(atkYield, Math.min(maxTotalYield, maxAtkYield)));
    	ev_atk += effectiveAtkYield;
    	maxTotalYield -= effectiveAtkYield;
    	
    	int effectiveDefYield = Math.max(0, Math.min(defYield, Math.min(maxTotalYield, maxDefYield)));
    	ev_def += effectiveDefYield;
    	maxTotalYield -= effectiveDefYield;
    	
    	int effectiveSpeYield = Math.max(0, Math.min(speYield, Math.min(maxTotalYield, maxSpeYield)));
    	ev_spe += effectiveSpeYield;
    	maxTotalYield -= effectiveSpeYield;
    	
    	int effectiveSpaYield = Math.max(0, Math.min(spaYield, Math.min(maxTotalYield, maxSpaYield)));
    	ev_spa += effectiveSpaYield;
    	maxTotalYield -= effectiveSpaYield;
    	
    	int effectiveSpdYield = Math.max(0, Math.min(spdYield, Math.min(maxTotalYield, maxSpdYield)));
    	ev_spd += effectiveSpdYield;
    	maxTotalYield -= effectiveSpdYield;
    }
    
    public int getTotalEVs() {
    	return ev_hp + ev_atk + ev_def + ev_spa + ev_spd + ev_spe;
    }

    @Override
    public void battle(Pokemon p, BattleOptions options) {
        // p is the one that gets leveled up
        // this is the one that dies like noob
        // be sure to gain EVs before the exp
    	// if no participants (for example a death), don't give any ev or exp
    	if (options.getParticipants() > 0) {
	        p.gainEvs(this.getSpecies());
	        p.gainExp(this.expGiven(options.getParticipants()));
    	}
    }

    // gains from eating stat/level boosters
    public void eatRareCandy() {
        if (level < 100) { // TODO : hardcoded
            level++;
            setExpForLevel();
            calculateStats();
        }
    }

    private static final int EV_BOOST_FROM_VITAMIN = 10; // TODO
    private static final int EV_LIMIT_FROM_VITAMIN = 100; // TODO
    
    public void eatHPUp() {
        if (ev_hp >= EV_LIMIT_FROM_VITAMIN)
            return;
        ev_hp += Math.min(EV_BOOST_FROM_VITAMIN, EV_LIMIT_FROM_VITAMIN-ev_hp);
        calculateStats();
    }

    public void eatProtein() {
        if (ev_atk >= EV_LIMIT_FROM_VITAMIN)
            return;
        ev_atk += Math.min(EV_BOOST_FROM_VITAMIN, EV_LIMIT_FROM_VITAMIN-ev_atk);
        calculateStats();
    }

    public void eatIron() {
        if (ev_def >= EV_LIMIT_FROM_VITAMIN)
            return;
        ev_def += Math.min(EV_BOOST_FROM_VITAMIN, EV_LIMIT_FROM_VITAMIN-ev_def);
        calculateStats();
    }

    public void eatCalcium() {
        if (ev_spa >= EV_LIMIT_FROM_VITAMIN)
            return;
        ev_spa += Math.min(EV_BOOST_FROM_VITAMIN, EV_LIMIT_FROM_VITAMIN-ev_spa);
        calculateStats();
    }
    
    public void eatZinc() {
        if (ev_spd >= EV_LIMIT_FROM_VITAMIN)
            return;
        ev_spd += Math.min(EV_BOOST_FROM_VITAMIN, EV_LIMIT_FROM_VITAMIN-ev_spd);
        calculateStats();
    }

    public void eatCarbos() {
        if (ev_spe >= EV_LIMIT_FROM_VITAMIN)
            return;
        ev_spe += Math.min(EV_BOOST_FROM_VITAMIN, EV_LIMIT_FROM_VITAMIN-ev_spe);
        calculateStats();
    }

    // TODO: proper evolution
    public void evolve(Species s) {
        species = s;
    }

    // badge get/set
    public boolean hasAtkBadge() {
        return atkBadge;
    }

    public void setAtkBadge(boolean atkBadge) {
        this.atkBadge = atkBadge;
    }

    public boolean hasDefBadge() {
        return defBadge;
    }

    public void setDefBadge(boolean defBadge) {
        this.defBadge = defBadge;
    }

    public boolean hasSpaBadge() {
        return spaBadge;
    }

    public void setSpaBadge(boolean spaBadge) {
        this.spaBadge = spaBadge;
    }

    public boolean hasSpdBadge() {
        return spdBadge;
    }

    public void setSpdBadge(boolean spdBadge) {
        this.spdBadge = spdBadge;
    }
    
    public boolean hasSpeBadge() {
        return speBadge;
    }

    public void setSpeBadge(boolean speBadge) {
        this.speBadge = speBadge;
    }

    public void setAllBadges() {
        atkBadge = true;
        defBadge = true;
        spaBadge = true;
        spdBadge = true;
        speBadge = true;
    }

    public void loseAllBadges() {
        atkBadge = false;
        defBadge = false;
        spaBadge = false;
        spdBadge = false;
        speBadge = false;
    }

    // a printout of stat ranges given this pokemon's EVs (not IVs)
    private static final int IV_RANGE = 32; // TODO
    public String statRanges(boolean isBoosted) {
        int[] possibleHPs = new int[IV_RANGE];
        int[] possibleAtks = new int[IV_RANGE];
        int[] possibleDefs = new int[IV_RANGE];
        int[] possibleSpds = new int[IV_RANGE];
        int[] possibleSpcAtks = new int[IV_RANGE];
        int[] possibleSpcDefs = new int[IV_RANGE];
        if (isBoosted) {
            for (int i = 0; i < IV_RANGE; i++) {
                possibleHPs[i] = calcHPWithIV(i);
                possibleAtks[i] = 9 * calcAtkWithIV(i, nature) / 8;
                possibleDefs[i] = 9 * calcDefWithIV(i, nature) / 8;
                possibleSpds[i] = 9 * calcSpeWithIV(i, nature) / 8;
                possibleSpcAtks[i] = 9 * calcSpaWithIV(i, nature) / 8;
                possibleSpcDefs[i] = 9 * calcSpdWithIV(i, nature) / 8;
            }
        } else {
            for (int i = 0; i < IV_RANGE; i++) {
                possibleHPs[i] = calcHPWithIV(i);
                possibleAtks[i] = calcAtkWithIV(i, nature);
                possibleDefs[i] = calcDefWithIV(i, nature);
                possibleSpds[i] = calcSpeWithIV(i, nature);
                possibleSpcAtks[i] = calcSpaWithIV(i, nature);
                possibleSpcDefs[i] = calcSpdWithIV(i, nature);
            }
        }
        StringBuilder sb = new StringBuilder(levelNameNatureAbility() + Constants.endl);
        sb.append("Stat ranges " + (isBoosted ? "WITH" : "WITHOUT")
                + " badge boosts:" + Constants.endl);
        sb.append("IV    |0   |1   |2   |3   |4   |5   |6   |7   |8   |9   |10  |11  |12  |13  |14  |15  |16  |17  |18  |19  |20  |21  |22  |23  |24  |25  |26  |27  |28  |29  |30  |31  "
                + Constants.endl
                + "------------------------------------------------------------------------------------"
                + Constants.endl);
        sb.append("HP    ");
        for (int i = 0; i < IV_RANGE; i++) {
            sb.append(String.format("|%1$4s", possibleHPs[i])); // pad left,
                                                                // length 4
        }
        sb.append(Constants.endl);
        sb.append("ATK   ");
        for (int i = 0; i < IV_RANGE; i++) {
            sb.append(String.format("|%1$4s", possibleAtks[i])); // pad left,
                                                                    // length 4
        }
        sb.append(Constants.endl);
        sb.append("DEF   ");
        for (int i = 0; i < IV_RANGE; i++) {
            sb.append(String.format("|%1$4s", possibleDefs[i])); // pad left,
                                                                    // length 4
        }
        sb.append(Constants.endl);
        sb.append("SPATK ");
        for (int i = 0; i < IV_RANGE; i++) {
            sb.append(String.format("|%1$4s", possibleSpcAtks[i])); // pad left,
                                                                    // length 4
        }
        sb.append(Constants.endl);
        sb.append("SPDEF ");
        for (int i = 0; i < IV_RANGE; i++) {
            sb.append(String.format("|%1$4s", possibleSpcDefs[i])); // pad left,
                                                                    // length 4
        }
        sb.append(Constants.endl);
        sb.append("SPD   ");
        for (int i = 0; i < IV_RANGE; i++) {
            sb.append(String.format("|%1$4s", possibleSpds[i])); // pad left,
                                                                    // length 4
        }
        sb.append(Constants.endl);

        return sb.toString();
    }

	public boolean hasPokerus() {
		return hasPokerus;
	}

	public void setPokerus(boolean hasPokerus) {
		this.hasPokerus = hasPokerus;
	}

	public boolean hasBoostedExp() {
		return hasBoostedExp;
	}

	public void setBoostedExp(boolean hasBoostedExp) {
		this.hasBoostedExp = hasBoostedExp;
	}
	
	public boolean hasBadgeBoost() {
		return atkBadge || defBadge || spaBadge | spdBadge | speBadge;
	}
}
