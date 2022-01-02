import java.util.TreeMap;

//TODO : Fury Cutter and Rage are broken => probably Rollout too

public class DamageCalculator {
    private static int MIN_ROLL = 85;
    private static int MAX_ROLL = 100;
    private static int NUM_ROLLS = MAX_ROLL - MIN_ROLL + 1;

    // ****************** //
    // MAIN DAMAGE METHOD //
    // ****************** //
    
    // rangeNum should range from 85 to 100 (both included)
    // Layout : 
    // 1. https://github.com/pret/pokeruby/blob/0ea1e7620cc5fea1e651974442052ba9c52cdd13/data/battle_scripts_1.s#L230-L273
    // Main routines : 
    // 1. Inner damage : https://github.com/pret/pokeruby/blob/a3228d4c86494ee25aff60fc037805ddc1d47d32/src/calculate_base_damage.c#L93
    // 2. Outer damage : https://github.com/pret/pokeruby/blob/a3228d4c86494ee25aff60fc037805ddc1d47d32/src/battle_script_commands.c#L1475
    // Other routines (maybe useful) :
    // - https://github.com/pret/pokeruby/blob/a3228d4c86494ee25aff60fc037805ddc1d47d32/src/battle_script_commands.c#L1410
    // - https://github.com/pret/pokeruby/blob/a3228d4c86494ee25aff60fc037805ddc1d47d32/src/battle_script_commands.c#L6430
    // - https://github.com/pret/pokeruby/blob/a3228d4c86494ee25aff60fc037805ddc1d47d32/src/battle_script_commands.c#L8559
    private static int damage(Move attackMove, Pokemon attacker, Pokemon defender,
                              StatModifier atkMod, StatModifier defMod, int roll,
                              boolean isCrit, int extra_multiplier, boolean isBattleTower, boolean isDoubleBattle) {
        if (roll < MIN_ROLL) {
            roll = MIN_ROLL;
        }
        if (roll > MAX_ROLL) {
            roll = MAX_ROLL;
        }
        
        /*
        if(attackMove.getName().equalsIgnoreCase("WATER GUN")) {
        	System.out.println("Def in !"); // TODO get rid
        }
        */
        
        /* Plan :
         * 
         * [EffectHit]
         *   Surf on Dive multiplier
         * 
         * [atk05_damagecalc]
         * >> (CalculateBaseDamage)
         *     Forced override  | TODO : what is this ? Probably the "power == 1" cases and some more
         *     Enigma Berry
         *     Huge Power, Pure Power
         *     Badge boosts
         *     Type boosting items
         *     Choice Band
         *     Soul Dew
         *     Deep Sea Tooth
         *     Deep Sea Scale
         *     Light Ball
         *     Metal Power
         *     Thick Club
         *     Thick Fat
         *     Hustle
         *     Plus & Minus
         *     Guts
         *     Marvel Scale
         *     Mud Sport
         *     Water Spout
         *     Overgrow, Blaze, Torrent, Swarm
         *     Explosion (effect)
         * 
         *     Base damage
         *     Reflect, Lightscreen
         *     Double battle damage halving (split damage)
         *     Weather checks
         *     Flash Fire boost
         * 
         *     Add 2
         * >> (End CalculateBaseDamage)
         * 
         *   Crit multiplier 
         *   Damage multiplier (TODO : probably Rage, Fury Cutter, Rollout, etc.)
         *   Charged up
         *   Helping hand
         * [End atk05_damagecalc]
		 *
         * [atk06_typecalc]
         *   STAB
         *   Levitate
         *   Foresight
         *   Type effectiveness
         *   Wonder Guard
         * [End atk06_typecalc]
         * 
         * [atk07_adjustnormaldamage]
         *   Damage rolls
         */
        
        Move modifiedAttackMove = new Move(attackMove); // Copy
        
        // *********** //
        // [EffectHit] //
        // *********** //
        
        // Overwrite multiplier if Surfing an underwater pokemon
        // TODO : maybe put this somewhere else, idk
        if(modifiedAttackMove.getName().equalsIgnoreCase("SURF") && defMod.hasStatus2_3(Status.UNDERWATER))
        	extra_multiplier = 2;
        
        // *************** //
        // [End EffectHit] //
        // ############### //
        
        
        // ****************** //
        // [atk05_damagecalc] //
        // ****************** //
        
        // ************************ //
        // >> (CalculateBaseDamage) //
        // ************************ //
        
        //TODO: Line randomly added there, might move it somewhere
        if (modifiedAttackMove.getPower() == 0) {
        	return 0; // TODO: hardcoded
        }
        
        // Forced override
        // TODO : better power & type overrides
        if (modifiedAttackMove.getEffect() == MoveEffect.LEVEL_DAMAGE)
        	return attacker.getLevel();
        
        if (modifiedAttackMove.getPower() == 1) { // Special cases seem to have this in common
            // TODO: more special cases
        	switch(modifiedAttackMove.getEffect()) {
        	case HIDDEN_POWER :
                Type type = attacker.getIVs().getHiddenPowerType();
                int power = attacker.getIVs().getHiddenPowerPower();
                modifiedAttackMove.setType(type);
                modifiedAttackMove.setPower(power);
                break;
        	case SONICBOOM :
        		return 20; //TODO : hardcoded
        	case DRAGON_RAGE :
        		return 40; //TODO : hardcoded
        	default: return 0;
        	}
        }
        
        // Enigma Berry
        // TODO
        
        // (retrieving stats)
        int attackerAtk = attacker.getTrueAtk();
        int attackerSpa = attacker.getTrueSpa();
        
        int defenderDef = defender.getTrueDef();
        int defenderSpd = defender.getTrueSpd();
        
        // (retrieving items)
        Item attackerItem = attacker.getHeldItem();
        boolean hasAttackerItem = attackerItem != null;
        Item defenderItem = defender.getHeldItem();
        boolean hasDefenderItem = defenderItem != null;
        
        // Huge Power, Pure Power
        if(attacker.getAbility() == Ability.HUGE_POWER || attacker.getAbility() == Ability.PURE_POWER)
        	attackerAtk *= 2;
        
        // Badge boosts
        if(!isBattleTower) { 
        	// TODO : game checks for 'gBattleTypeFlags & BATTLE_TYPE_TRAINER', so maybe no badge boosts against encounters ?
        	// See : https://github.com/pret/pokeruby/blob/a3228d4c86494ee25aff60fc037805ddc1d47d32/src/calculate_base_damage.c#L85
	        attackerAtk = attacker.applyBadgeBoost(attackerAtk, Stat.ATK);
	        attackerSpa = attacker.applyBadgeBoost(attackerSpa, Stat.SPA);
	        defenderDef = defender.applyBadgeBoost(defenderDef, Stat.DEF);
	        defenderSpd = defender.applyBadgeBoost(defenderSpd, Stat.SPD);
        }
        
        // Type boosting items
        if (hasAttackerItem) {
        	ItemHoldEffect attackerItemHoldEffect = attackerItem.getHoldEffect();
        	Type moveType = modifiedAttackMove.getType();
        	if(attackerItemHoldEffect.isTypeBoosting(moveType)) {
        		int effectParam = attackerItem.getHoldEffectParam();
        		if(Type.isPhysicalType(moveType)) // TODO: may be different for Gen 4
        			attackerAtk = attackerAtk * (100 + effectParam) / 100; // TODO: hardcoded
        		else
        			attackerSpa = attackerSpa * (100 + effectParam) / 100; // TODO: hardcoded
        	}
        }
        
        // Choice Band
        if (hasAttackerItem && attackerItem.getHoldEffect() == ItemHoldEffect.CHOICE_BAND)
        	attackerAtk = attackerAtk * 150 / 100; // TODO: hardcoded
        
        // Attacker Soul Dew
        if (hasAttackerItem && attackerItem.getHoldEffect() == ItemHoldEffect.SOUL_DEW && !isBattleTower
        		&& (attacker.getSpecies().getName().equalsIgnoreCase("LATIOS") || attacker.getSpecies().getName().equalsIgnoreCase("LATIAS"))) // TODO: hardcoded
        	attackerSpa = attackerSpa * 150 / 100; // TODO: hardcoded
        
        // Defender Soul Dew
        if (hasDefenderItem && defenderItem.getHoldEffect() == ItemHoldEffect.SOUL_DEW && !isBattleTower
        		&& (defender.getSpecies().getName().equalsIgnoreCase("LATIOS") || defender.getSpecies().getName().equalsIgnoreCase("LATIAS"))) // TODO: hardcoded
        	defenderSpd = defenderSpd * 150 / 100; // TODO: hardcoded
        
        // Attacker Deep Sea Tooth
        if (hasAttackerItem && attackerItem.getHoldEffect() == ItemHoldEffect.DEEP_SEA_TOOTH
        		&& attacker.getSpecies().getName().equalsIgnoreCase("CLAMPERL")) // TODO: hardcoded
        	attackerSpa *= 2; // TODO: hardcoded
        
        // Defender Deep Sea Scale
        if (hasDefenderItem && defenderItem.getHoldEffect() == ItemHoldEffect.DEEP_SEA_SCALE
        		&& defender.getSpecies().getName().equalsIgnoreCase("CLAMPERL")) // TODO: hardcoded
        	defenderSpd *= 2; // TODO: hardcoded
        
        // Attacker Light Ball
        if (hasAttackerItem && attackerItem.getHoldEffect() == ItemHoldEffect.LIGHT_BALL
        		&& attacker.getSpecies().getName().equalsIgnoreCase("PIKACHU")) // TODO: hardcoded
        	attackerSpa *= 2; // TODO: hardcoded
        
        // Defender Metal Powder
        if (hasDefenderItem && defenderItem.getHoldEffect() == ItemHoldEffect.METAL_POWDER
        		&& defender.getSpecies().getName().equalsIgnoreCase("DITTO")) // TODO: hardcoded
        	defenderDef *= 2; // TODO: hardcoded
        
        // Attacker Thick Club
        if (hasAttackerItem && attackerItem.getHoldEffect() == ItemHoldEffect.THICK_CLUB
        		&& (attacker.getSpecies().getName().equalsIgnoreCase("CUBONE") || attacker.getSpecies().getName().equalsIgnoreCase("MAROWAK"))) // TODO: hardcoded
        	attackerAtk *= 2; // TODO: hardcoded
        
        // Defender Thick Fat
        if(defender.getAbility() == Ability.THICK_FAT 
        		&& (modifiedAttackMove.getType() == Type.FIRE || modifiedAttackMove.getType() == Type.ICE))
        	attackerSpa /= 2; // TODO: hardcoded
        
        // Attacker Hustle
        if(attacker.getAbility() == Ability.HUSTLE)
        	attackerAtk = attackerAtk * 150 / 100; // TODO: hardcoded
        
        // TODO: Plus & Minus
        
        // Attacker Guts
        if(attacker.getAbility() == Ability.GUTS && atkMod.getStatus1() != Status.NONE)
        	attackerAtk = attackerAtk * 150 / 100; // TODO: hardcoded
        
        // Defender Marvel Scale
        if(defender.getAbility() == Ability.MARVEL_SCALE && defMod.getStatus1() != Status.NONE)
        	defenderDef = defenderDef * 150 / 100; // TODO: hardcoded
        
        // Attacker Mud Sport
        if(modifiedAttackMove.getType() == Type.ELECTRIC && atkMod.hasStatus2_3(Status.MUDSPORT))
        	modifiedAttackMove.setPower(modifiedAttackMove.getPower() / 2); // TODO: hardcoded
        
        // Attacker Water Spout
        if(modifiedAttackMove.getType() == Type.FIRE && atkMod.hasStatus2_3(Status.WATERSPOUT))
        	modifiedAttackMove.setPower(modifiedAttackMove.getPower() / 2); // TODO: hardcoded
        
        // Attacker Overgrow, Blaze, Torrent, Swarm
        if(modifiedAttackMove.getType() == Type.GRASS && attacker.getAbility() == Ability.OVERGROW && atkMod.isOneThirdHPOrLess())
        	modifiedAttackMove.setPower(modifiedAttackMove.getPower() * 150 / 100); // TODO: hardcoded
        if(modifiedAttackMove.getType() == Type.FIRE && attacker.getAbility() == Ability.BLAZE && atkMod.isOneThirdHPOrLess())
        	modifiedAttackMove.setPower(modifiedAttackMove.getPower() * 150 / 100); // TODO: hardcoded
        if(modifiedAttackMove.getType() == Type.WATER && attacker.getAbility() == Ability.TORRENT && atkMod.isOneThirdHPOrLess())
        	modifiedAttackMove.setPower(modifiedAttackMove.getPower() * 150 / 100); // TODO: hardcoded
        if(modifiedAttackMove.getType() == Type.BUG && attacker.getAbility() == Ability.SWARM && atkMod.isOneThirdHPOrLess())
        	modifiedAttackMove.setPower(modifiedAttackMove.getPower() * 150 / 100); // TODO: hardcoded
        
        // Defender Explosion
        if (modifiedAttackMove.getEffect() == MoveEffect.EXPLOSION)
        	defenderDef /= 2; // TODO: hardcoded
        
        // Base damage
        int damage = Integer.MIN_VALUE;
        int damageHelper = Integer.MIN_VALUE;
        if(modifiedAttackMove.isPhysical()){
        	if(isCrit) {
        		if(atkMod.getAtkStage() > 0)
        			damage = atkMod.modStat(attackerAtk, Stat.ATK);
        		else
        			damage = attackerAtk;
        	}
        	else
        		damage = atkMod.modStat(attackerAtk, Stat.ATK);
        	
        	damage *= modifiedAttackMove.getPower();
        	damage *= (2 * attacker.getLevel() / 5 + 2);
        	
        	if(isCrit) {
        		if(defMod.getDefStage() < 0)
        			damageHelper = defMod.modStat(defenderDef, Stat.DEF);
        		else
        			damageHelper = defenderDef;
        	}
        	else
        		damageHelper = defMod.modStat(defenderDef, Stat.DEF);
        	
        	damage = damage / damageHelper;
        	damage /= 50;
        	
        	// Attacker Burn
        	if(atkMod.getStatus1() == Status.BURN && !(attacker.getAbility() == Ability.GUTS))
        		damage /= 2;
        	
        	// Defender Reflect
        	if(defMod.hasStatus2_3(Status.REFLECT) && !isCrit) {
        		if(isDoubleBattle) // TODO: countMon == 2 needed ?
        			damage = 2 * (damage / 3);
        		else
        			damage /= 2;
        	}
        	
        	// Moves hitting both enemies
        	if(isDoubleBattle && modifiedAttackMove.getMoveTarget() == MoveTarget.BOTH_ENEMIES) // TODO: countMon == 2 needed ?
        		damage /= 2;
        	
        	// Damage of at least 1
        	if (damage == 0)
        		damage = 1;
        }
        else if (modifiedAttackMove.getType() == Type.MYSTERY || modifiedAttackMove.getType() == Type.NONE)
        	damage = 0; // TODO: Struggle is actually type NONE, so need to care for this
        else { // Special types
        	if(isCrit) {
        		if(atkMod.getSpaStage() > 0)
        			damage = atkMod.modStat(attackerSpa, Stat.SPA);
        		else
        			damage = attackerSpa;
        	}
        	else
        		damage = atkMod.modStat(attackerSpa, Stat.SPA);
        	
        	damage *= modifiedAttackMove.getPower();
        	damage *= (2 * attacker.getLevel() / 5 + 2);
        	
        	if(isCrit) {
        		if(defMod.getSpdStage() < 0)
        			damageHelper = defMod.modStat(defenderSpd, Stat.SPD);
        		else
        			damageHelper = defenderSpd;
        	}
        	else
        		damageHelper = defMod.modStat(defenderSpd, Stat.SPD);
        	
        	damage = damage / damageHelper;
        	damage /= 50;
        	
        	// Defender Light Screen
        	if(defMod.hasStatus2_3(Status.LIGHTSCREEN) && !isCrit) {
        		if(isDoubleBattle) // TODO: countMon == 2 needed ?
        			damage = 2 * (damage / 3);
        		else
        			damage /= 2;
        	}
        	
        	// Moves hitting both enemies
        	if(isDoubleBattle && modifiedAttackMove.getMoveTarget() == MoveTarget.BOTH_ENEMIES) // TODO: countMon == 2 needed ?
        		damage /= 2;
        	
        	// Are effects of weather negated with cloud nine or air lock
        	if(!(attacker.getAbility() == Ability.CLOUD_NINE || attacker.getAbility() == Ability.AIR_LOCK
        			|| defender.getAbility() == Ability.CLOUD_NINE || defender.getAbility() == Ability.AIR_LOCK)) {
        		// Rainy
        		if(atkMod.getWeather() == Weather.RAIN) {
        			if(modifiedAttackMove.getType() == Type.FIRE)
        				damage /= 2;
        			else if (modifiedAttackMove.getType() == Type.WATER)
        				damage = damage * 15 / 10;
        		}
        		
        		// Any weather except sun weakens solar beam
        		if(modifiedAttackMove.getEffect() == MoveEffect.SOLARBEAM 
        				&& !(atkMod.getWeather() == Weather.NONE || atkMod.getWeather() == Weather.SUN))
        			damage /= 2;
        		
        		// Sunny
        		if(atkMod.getWeather() == Weather.SUN) {
        			if(modifiedAttackMove.getType() == Type.FIRE)
        				damage = damage * 15 / 10;
        			else if (modifiedAttackMove.getType() == Type.WATER)
        				damage /= 2;
        		}
        	}
        	
        	// Attacker Flash Fire
        	if(atkMod.isFlashFireActivated() && modifiedAttackMove.getType() == Type.FIRE)
        		damage = damage * 15 / 10;
        	
        	// No Damage at least 1 for special moves wtf
        }
        
        damage += 2;
        // CalculateBaseDamage routine returns damage + 2 here
        
        // **************************** //
        // >> (End CalculateBaseDamage) //
        // ***######################### //
        
        
        // Critical hit gBattleMoveDamage * gCritMultiplier * gBattleStruct->dmgMultiplier;
        if (isCrit)
        	damage *= 2;
        
        // Damage multiplier (TODO: What is this ?)
        // TODO
        
        // Charged up
        if (modifiedAttackMove.getType() == Type.ELECTRIC && atkMod.hasStatus2_3(Status.CHARGED_UP))
        	damage *= 2;
        
        // Helping hand
        // TODO
        
        
    	// ********************** //
        // [end atk05_damagecalc] //
        // ###################### //
        
        
        // **************** //
        // [atk06_typecalc] //
        // **************** //
        
        if(!modifiedAttackMove.getName().equalsIgnoreCase("STRUGGLE")) {
        	// STAB
        	if(attacker.getSpecies().getType1() == modifiedAttackMove.getType()
        			|| attacker.getSpecies().getType2() == modifiedAttackMove.getType())
        		damage = damage * 15 / 10;
        	
        	// Levitate
        	if(defender.getAbility() == Ability.LEVITATE && modifiedAttackMove.getType() == Type.GROUND)
        		return 0;
        	
        	// Type effectiveness
        	// TODO: handle Foresight (check within Types because there's a TODO there too)
        	damage = Type.modulateDamageByType(damage, modifiedAttackMove.getType(), defender.getSpecies().getType1());
        	damage = Type.modulateDamageByType(damage, modifiedAttackMove.getType(), defender.getSpecies().getType2());
        	
        	// Wonder Guard
        	if(defender.getAbility() == Ability.WONDER_GUARD 
        			&& Type.isSuperEffective(modifiedAttackMove.getType(), 
        					defender.getSpecies().getType1(), defender.getSpecies().getType2()))
        		return 0;
        }
        
        // ******************** //
        // [End atk06_typecalc] //
        // #################### //
        
        // ************************** //
        // [atk07_adjustnormaldamage] //
        // ************************** //
        
        // Damage roll
        damage = damage * roll / MAX_ROLL;
        
        // Bunch of stuff skipped because not useful or linked to damage
        
        // ****************************** //
        // [End atk07_adjustnormaldamage] //
        // ****************************** //
        
        return damage;
    }

    
    // ********************* //
    // HELPER DAMAGE METHODS //
    // ********************* //
    
    public static int minDamage(Move attack, Pokemon attacker,
                                Pokemon defender, StatModifier atkMod, StatModifier defMod,
                                int extra_multiplier, boolean isBattleTower, boolean isDoubleBattle) {
        return damage(attack, attacker, defender, atkMod, defMod, MIN_ROLL,
                false, extra_multiplier, isBattleTower, isDoubleBattle);
    }

    public static int maxDamage(Move attack, Pokemon attacker,
                                Pokemon defender, StatModifier atkMod, StatModifier defMod,
                                int extra_multiplier, boolean isBattleTower, boolean isDoubleBattle) {
        return damage(attack, attacker, defender, atkMod, defMod, MAX_ROLL,
                false, extra_multiplier, isBattleTower, isDoubleBattle);
    }

    public static int minCritDamage(Move attack, Pokemon attacker,
                                    Pokemon defender, StatModifier atkMod, StatModifier defMod,
                                    int extra_multiplier, boolean isBattleTower, boolean isDoubleBattle) {
        return damage(attack, attacker, defender, atkMod, defMod, MIN_ROLL,
                true, extra_multiplier, isBattleTower, isDoubleBattle);
    }

    public static int maxCritDamage(Move attack, Pokemon attacker,
                                    Pokemon defender, StatModifier atkMod, StatModifier defMod,
                                    int extra_multiplier, boolean isBattleTower, boolean isDoubleBattle) {
        return damage(attack, attacker, defender, atkMod, defMod, MAX_ROLL,
                true, extra_multiplier, isBattleTower, isDoubleBattle);
    }
    
    
    
    // ******************************** //
    // HELPER STRING FORMATTING METHODS //
    // ******************************** //
    
    public static void battleIntroSummary(StringBuilder sb, Pokemon p1, Pokemon p2, BattleOptions options) {
        sb.append(String.format("%s vs %s", p1.levelNameNatureAbility(), p2.levelNameNatureAbility()));
        // Don't show exp for tower pokes (minor thing since exp isn't added anyway)
        if(!options.isBattleTower()) {
            sb.append("          >>> EXP GIVEN: " + p2.expGiven(options.getParticipants()));
        }
    }
    
    public static void pokemonSummary(StringBuilder sb, Pokemon p, StatModifier mod) {
    	sb.append(String.format("%s (%s) ", p.pokeName(), p.trueStatsStr()));
        if (mod.hasMods())
            sb.append(String.format("%s ", mod.summary(p)));
        if (p.getHeldItem() != null)
        	sb.append(String.format("<%s> ", p.getHeldItem().getDisplayName()));
        if (mod.getWeather() != Weather.NONE)
        	sb.append(String.format("~%s~ ", mod.getWeather()));
    }
    
    public static void formatMoveName(StringBuilder sb, Move m, Pokemon p1,
            Pokemon p2, StatModifier mod1, StatModifier mod2,
            int _extra_multiplier, boolean isBattleTower, boolean isDoubleBattle) {
        // Move name
        if(m.getEffect() == MoveEffect.RAGE || m.getEffect() == MoveEffect.FURY_CUTTER
        		|| m.getEffect() == MoveEffect.ROLLOUT) {
            sb.append(m.getBoostedName(_extra_multiplier));
        } else if (m.getEffect() == MoveEffect.HIDDEN_POWER) {
        	Type type = p1.getIVs().getHiddenPowerType();
	        int power = p1.getIVs().getHiddenPowerPower();
	        sb.append(String.format("%s [%s %d]", m.getName(), type, power)); // TODO : hardcoded
        } else 
        	sb.append(m.getName());

        // Various modifiers
		if(m.getPower() > 1) { //TODO : hardcoded value
			Item attackerItem = p1.getHeldItem();
			Item defenderItem = p2.getHeldItem();
			boolean hasAttackerItem = attackerItem != null;
			boolean hasDefenderItem = defenderItem != null;
			
			String underwaterBonusStr = " +UW"; //TODO : hardcoded
			String itemBonusStr = " +ITEM"; // TODO: hardcoded
			String itemMalusStr = " -ITEM"; // TODO: hardcoded
			String mudSportMalusStr = " -" + Status.MUDSPORT;
			String waterSpoutMalusStr = " -" + Status.WATERSPOUT;
			
			
	        if(m.getName().equalsIgnoreCase("SURF") && mod2.hasStatus2_3(Status.UNDERWATER)) // TODO : hardcoded
	        	sb.append(underwaterBonusStr);
			
			
			// Type boosting items
	        if (hasAttackerItem) {
	        	ItemHoldEffect attackerItemHoldEffect = attackerItem.getHoldEffect();
	        	Type moveType = m.getType();
	        	if(attackerItemHoldEffect.isTypeBoosting(moveType))
	        		sb.append(itemBonusStr);
	        }
	        
	        // Choice Band
	        if (hasAttackerItem && m.isPhysical() && attackerItem.getHoldEffect() == ItemHoldEffect.CHOICE_BAND)
        		sb.append(itemBonusStr);
	        
	        // Attacker Soul Dew
	        if (hasAttackerItem && m.isSpecial() && attackerItem.getHoldEffect() == ItemHoldEffect.SOUL_DEW && !isBattleTower
	        		&& (p1.getSpecies().getName().equalsIgnoreCase("LATIOS") || p1.getSpecies().getName().equalsIgnoreCase("LATIAS"))) // TODO: hardcoded
        		sb.append(itemBonusStr);
	        
	        // Defender Soul Dew
	        if (hasDefenderItem && m.isSpecial() && defenderItem.getHoldEffect() == ItemHoldEffect.SOUL_DEW && !isBattleTower
	        		&& (p2.getSpecies().getName().equalsIgnoreCase("LATIOS") || p2.getSpecies().getName().equalsIgnoreCase("LATIAS"))) // TODO: hardcoded
        		sb.append(itemMalusStr);
	        
	        // Attacker Deep Sea Tooth
	        if (hasAttackerItem && m.isSpecial() && attackerItem.getHoldEffect() == ItemHoldEffect.DEEP_SEA_TOOTH
	        		&& p1.getSpecies().getName().equalsIgnoreCase("CLAMPERL")) // TODO: hardcoded
        		sb.append(itemBonusStr);
	        
	        // Defender Deep Sea Scale
	        if (hasDefenderItem && m.isSpecial() && defenderItem.getHoldEffect() == ItemHoldEffect.DEEP_SEA_SCALE
	        		&& p2.getSpecies().getName().equalsIgnoreCase("CLAMPERL")) // TODO: hardcoded
        		sb.append(itemMalusStr);
	        
	        // Attacker Light Ball
	        if (hasAttackerItem && m.isSpecial() && attackerItem.getHoldEffect() == ItemHoldEffect.LIGHT_BALL
	        		&& p1.getSpecies().getName().equalsIgnoreCase("PIKACHU")) // TODO: hardcoded
        		sb.append(itemBonusStr);
	        
	        // Defender Metal Powder
	        if (hasDefenderItem && m.isPhysical() && defenderItem.getHoldEffect() == ItemHoldEffect.METAL_POWDER
	        		&& p2.getSpecies().getName().equalsIgnoreCase("DITTO")) // TODO: hardcoded
        		sb.append(itemMalusStr);
	        
	        // Attacker Thick Club
	        if (hasAttackerItem && m.isPhysical() && attackerItem.getHoldEffect() == ItemHoldEffect.THICK_CLUB
	        		&& (p1.getSpecies().getName().equalsIgnoreCase("CUBONE") || p1.getSpecies().getName().equalsIgnoreCase("MAROWAK"))) // TODO: hardcoded
        		sb.append(itemBonusStr);
	        
	        // Defender Thick Fat
	        if(p2.getAbility() == Ability.THICK_FAT 
	        		&& (m.getType() == Type.FIRE || m.getType() == Type.ICE))
	        	sb.append(" -" + p2.getAbility()); // TODO: hardcoded
	        
	        // Attacker Hustle
	        if(m.isPhysical() && p1.getAbility() == Ability.HUSTLE)
	        	sb.append(" +" + p1.getAbility()); // TODO: hardcoded
	        
	        // TODO: Plus & Minus
	        
	        // Attacker Guts
	        if(m.isPhysical() && p1.getAbility() == Ability.GUTS && mod1.getStatus1() != Status.NONE)
	        	sb.append(" +" + p1.getAbility()); // TODO: hardcoded
	        
	        // Defender Marvel Scale
	        if(m.isSpecial() && p2.getAbility() == Ability.MARVEL_SCALE && mod2.getStatus1() != Status.NONE)
	        	sb.append(" -" + p2.getAbility()); // TODO: hardcoded
	        
	        
	        // Attacker Overgrow, Blaze, Torrent, Swarm
	        if(m.getType() == Type.GRASS && p1.getAbility() == Ability.OVERGROW && mod1.isOneThirdHPOrLess())
	        	sb.append(" +" + p1.getAbility()); // TODO: hardcoded
	        if(m.getType() == Type.FIRE && p1.getAbility() == Ability.BLAZE && mod1.isOneThirdHPOrLess())
	        	sb.append(" +" + p1.getAbility()); // TODO: hardcoded
	        if(m.getType() == Type.WATER && p1.getAbility() == Ability.TORRENT && mod1.isOneThirdHPOrLess())
	        	sb.append(" +" + p1.getAbility()); // TODO: hardcoded
	        if(m.getType() == Type.BUG && p1.getAbility() == Ability.SWARM && mod1.isOneThirdHPOrLess())
	        	sb.append(" +" + p1.getAbility()); // TODO: hardcoded
	        
	        
	        // Field modifiers
	        
	        // Attacker Mud Sport
	        if(m.getType() == Type.ELECTRIC && mod1.hasStatus2_3(Status.MUDSPORT))
        		sb.append(mudSportMalusStr);
	        
	        // Attacker Water Spout
	        if(m.getType() == Type.FIRE && mod1.hasStatus2_3(Status.WATERSPOUT))
        		sb.append(waterSpoutMalusStr);
	     
	        if(m.isPhysical() && mod1.getStatus1() == Status.BURN && p1.getAbility() != Ability.GUTS)
	        	sb.append(" -" + Status.BURN); // TODO: hardcoded
	        
	        // Double target in double battle
	        if(m.getMoveTarget() == MoveTarget.BOTH_ENEMIES && isDoubleBattle)
	        	sb.append(" -DOUBLE"); //TODO : hardcoded
	        
			// Weather
	        if (mod1.getWeather() != Weather.NONE) {
	        	if(!(p1.getAbility() == Ability.CLOUD_NINE || p1.getAbility() == Ability.AIR_LOCK
	        			|| p2.getAbility() == Ability.CLOUD_NINE || p2.getAbility() == Ability.AIR_LOCK)) {
	
	        		// Rainy
	        		if(mod1.getWeather() == Weather.RAIN) {
	        			if(m.getType() == Type.FIRE)
	        				sb.append(String.format(" -%s", mod1.getWeather()));
	        			else if (m.getType() == Type.WATER)
	        				sb.append(String.format(" +%s", mod1.getWeather()));
	        		}
	        		
	        		// Any weather except sun weakens solar beam
	        		if(m.getEffect() == MoveEffect.SOLARBEAM 
	        				&& !(mod1.getWeather() == Weather.NONE || mod1.getWeather() == Weather.SUN))
	        			sb.append(String.format(" -%s", mod1.getWeather()));
	        		
	        		// Sunny
	        		if(mod1.getWeather() == Weather.SUN) {
	        			if(m.getType() == Type.FIRE)
	        				sb.append(String.format(" +%s", mod1.getWeather()));
	        			else if (m.getType() == Type.WATER)
	        				sb.append(String.format(" -%s", mod1.getWeather()));
	        		}
	        	}
	        }
        } 
        if (m.isPhysical() && mod2.hasStatus2_3(Status.REFLECT)) {
        	sb.append(" -R");
        } else if (!m.isPhysical() && mod2.hasStatus2_3(Status.LIGHTSCREEN)) {
        	sb.append(" -LS");
        }
    }
    
    
    // *********************** //
    // MAIN FORMATTING METHODS //
    // *********************** //
    
    // printout of move damages between the two pokemon
    // assumes you are p1
    public static String summary(Pokemon p1, Pokemon p2, BattleOptions options) {
        StringBuilder sb = new StringBuilder();
        String endl = Constants.endl;
        StatModifier mod1 = options.getMod1();
        StatModifier mod2 = options.getMod2();

        battleIntroSummary(sb, p1, p2, options);
        sb.append(endl);
        
        // PLayer side
        pokemonSummary(sb, p1, mod1);
        sb.append(endl);

        summary_help(sb, p1, p2, mod1, mod2, options.isBattleTower(), options.isDoubleBattle());
        sb.append(endl);

        if(options.getVerbose() == BattleOptions.EVERYTHING || options.getVerbose() == BattleOptions.ALL) {
            extra_damage_help(sb, p1, p2, options);
            sb.append(endl);
            
            // Opponent side
            pokemonSummary(sb, p2, mod2);
            sb.append(endl);
        }
        summary_help(sb, p2, p1, mod2, mod1, options.isBattleTower(), options.isDoubleBattle());
        sb.append(endl);

        if(options.getVerbose() == BattleOptions.EVERYTHING) {
            extra_damage_help(sb, p2, p1, options);
            sb.append(endl);
        }
        return sb.toString();
    }
    
    // used for the less verbose option
    public static String shortSummary(Pokemon p1, Pokemon p2, BattleOptions options) {
        StringBuilder sb = new StringBuilder();
        String endl = Constants.endl;

        StatModifier mod1 = options.getMod1();
        StatModifier mod2 = options.getMod2();

        battleIntroSummary(sb, p1, p2, options);
        sb.append(endl);
        
        pokemonSummary(sb, p1, mod1);
        sb.append(endl);

        summary_help(sb, p1, p2, mod1, mod2, options.isBattleTower(), options.isDoubleBattle());
        sb.append(endl);
        
        pokemonSummary(sb, p2, mod2);
        sb.append(endl);

        sb.append(p2.getMoveset().toString());
        sb.append(endl);
        
        return sb.toString();
    }

       
    
    // ******************************** //
    // HELPER DAMAGE FORMATTING METHODS //
    // ******************************** //
    
    private static void damage_help(StringBuilder sb, Move move, Pokemon p1, Pokemon p2, StatModifier mod1, StatModifier mod2, int _extra_modifier, boolean isBattleTower, boolean isDoubleBattle) {
        int extra_modifier = (move.getEffect() == MoveEffect.FURY_CUTTER || move.getEffect() == MoveEffect.ROLLOUT) ? 
        		1 << (_extra_modifier - 1) : _extra_modifier;
        String endl = Constants.endl;
        int minDmg = Math.min(p2.getHP(), minDamage(move, p1, p2, mod1, mod2, extra_modifier, isBattleTower, isDoubleBattle));
        if(minDmg > 0) {
            int minCritDmg = Math.min(p2.getHP(), minCritDamage(move, p1, p2, mod1, mod2, extra_modifier, isBattleTower, isDoubleBattle));
            TreeMap<Integer,Double> dmgMap = detailledDamage(move, p1, p2, mod1, mod2, false, extra_modifier, isBattleTower, isDoubleBattle);
            TreeMap<Integer,Double> critMap = detailledDamage(move, p1, p2, mod1, mod2, true, extra_modifier, isBattleTower, isDoubleBattle);
            
            formatMoveName(sb, move, p1, p2, mod1, mod2, _extra_modifier, isBattleTower, isDoubleBattle);
            sb.append(endl);
            sb.append("          NON-CRITS");
            for(Integer i : dmgMap.keySet()) {
                if((i - minDmg) % 7 == 0) {
                    sb.append(endl);
                    if(i.intValue() == p2.getHP() && minDmg != p2.getHP()) {
                        sb.append(endl);
                    }
                    sb.append("            ");
                }
                else if(i.intValue() == p2.getHP() && minDmg != p2.getHP()) {
                    sb.append(endl);
                    sb.append(endl);
                    sb.append("            ");
                }
                sb.append(String.format("%3d: %6.02f%%     ", i, dmgMap.get(i)));
            }
            sb.append(endl);
            sb.append(endl);
            sb.append("          CRITS");
            for(Integer i : critMap.keySet()) {
                if((i - minCritDmg) % 7 == 0) {
                    sb.append(endl);
                    if(i.intValue() == p2.getHP() && minCritDmg != p2.getHP()) {
                        sb.append(endl);
                    }
                    sb.append("            ");
                }
                else if(i.intValue() == p2.getHP() && minCritDmg != p2.getHP()) {
                    sb.append(endl);
                    sb.append(endl);
                    sb.append("            ");
                }
                sb.append(String.format("%3d: %6.02f%%     ", i, critMap.get(i)));
            }
            sb.append(endl);
            sb.append(endl);
        }
    }
    
    
    public static void extra_damage_help(StringBuilder sb, Pokemon p1, Pokemon p2, BattleOptions options) {
        StatModifier mod1 = options.getMod1();
        StatModifier mod2 = options.getMod2();
    	
    	for(Move move : p1.getMoveset()) {
            if (move.getEffect() == MoveEffect.FURY_CUTTER) {
                for (int i = 1; i <= 5; i++) {
                    damage_help(sb, move, p1, p2, mod1, mod2, i, options.isBattleTower(), options.isDoubleBattle());
                }
            } else if (move.getEffect() == MoveEffect.ROLLOUT) {
            	for (int i = 1; i <= 6; i++) {
                    damage_help(sb, move, p1, p2, mod1, mod2, i, options.isBattleTower(), options.isDoubleBattle());
                }
            } else if (move.getEffect() == MoveEffect.RAGE) {
                for (int i = 1; i <= 8; i++) {
                    damage_help(sb, move, p1, p2, mod1, mod2, i, options.isBattleTower(), options.isDoubleBattle());
                }
            } else if(move.getEffect() == MoveEffect.MAGNITUDE) {
                for (int i=4; i<=10; i++) {
                    if(i==10) { i++; }
                    move.setPower(i*20-70);
                    damage_help(sb, move, p1, p2, mod1, mod2, 1, options.isBattleTower(), options.isDoubleBattle());
                    move.setPower(1);
                }
            } else {
                damage_help(sb, move, p1, p2, mod1, mod2, 1, options.isBattleTower(), options.isDoubleBattle());
            }
        }
    }
    
    // String summary of all of p1's moves used on p2
    // (would be faster if i didn't return intermediate strings)
    private static void summary_help(StringBuilder sb, Pokemon p1, Pokemon p2, StatModifier mod1, StatModifier mod2, 
    		boolean isBattleTower, boolean isDoubleBattle) {
        int enemyHP = p2.getHP();

        for (Move m : p1.getMoveset()) {
            if (m.getEffect() == MoveEffect.FURY_CUTTER) {
                for (int i = 1; i <= 5; i++) {
                    printMoveDamage(sb, m, p1, p2, mod1, mod2, enemyHP, i, isBattleTower, isDoubleBattle);
                }
            } else if (m.getEffect() == MoveEffect.ROLLOUT) {
                for (int i = 1; i <= 6; i++) {
                    printMoveDamage(sb, m, p1, p2, mod1, mod2, enemyHP, i, isBattleTower, isDoubleBattle);
                }
        	} else if (m.getEffect() == MoveEffect.RAGE) {
                for (int i = 1; i <= 8; i++) {
                    printMoveDamage(sb, m, p1, p2, mod1, mod2, enemyHP, i, isBattleTower, isDoubleBattle);
                }
            } else if(m.getEffect() == MoveEffect.MAGNITUDE) {
                for (int i=4; i<=10; i++) {
                    if(i==10) { i++; }
                    m.setPower(i*20-70);
                    printMoveDamage(sb, m, p1, p2, mod1, mod2, enemyHP, 1, isBattleTower, isDoubleBattle);
                    m.setPower(1);
                }
            } else {
                printMoveDamage(sb, m, p1, p2, mod1, mod2, enemyHP, 1, isBattleTower, isDoubleBattle);
            }
        }
    }
    
    
    public static void printMoveDamage(StringBuilder sb, Move m, Pokemon p1,
                                       Pokemon p2, StatModifier mod1, StatModifier mod2,
                                       int enemyHP, int _extra_multiplier, boolean isBattleTower, boolean isDoubleBattle) {
    	formatMoveName(sb, m, p1, p2, mod1, mod2, _extra_multiplier, isBattleTower, isDoubleBattle);
        sb.append("\t");
        
        String endl = Constants.endl;
    	int extra_multiplier = (m.getEffect() == MoveEffect.FURY_CUTTER || m.getEffect() == MoveEffect.ROLLOUT) ?
        		1 << (_extra_multiplier - 1) : _extra_multiplier;
        
        // calculate damage of this move, and its percentages on opposing
        // pokemon
        int minDmg = minDamage(m, p1, p2, mod1, mod2, extra_multiplier, isBattleTower, isDoubleBattle);
        int maxDmg = maxDamage(m, p1, p2, mod1, mod2, extra_multiplier, isBattleTower, isDoubleBattle);

        // don't spam if the move doesn't do damage
        // TODO: better test of damaging move, to be done when fixes are made
        if (maxDmg == 0) {
            sb.append(endl);
            return;
        }
        double minPct = 100.0 * minDmg / enemyHP;
        double maxPct = 100.0 * maxDmg / enemyHP;
        sb.append(String.format("%d-%d %.02f-%.02f", minDmg, maxDmg, minPct,
                maxPct));
        sb.append("%\t(crit: ");
        // do it again, for crits
        int critMinDmg = minCritDamage(m, p1, p2, mod1, mod2, extra_multiplier, isBattleTower, isDoubleBattle);
        int critMaxDmg = maxCritDamage(m, p1, p2, mod1, mod2, extra_multiplier, isBattleTower, isDoubleBattle);

        double critMinPct = 100.0 * critMinDmg / enemyHP;
        double critMaxPct = 100.0 * critMaxDmg / enemyHP;
        sb.append(String.format("%d-%d %.02f-%.02f", critMinDmg, critMaxDmg,
                critMinPct, critMaxPct));
        sb.append("%)" + endl);

        int oppHP = p2.getHP();
        
     // normal rolls
        sb.append("\tNormal rolls: ");
        int lastDam = -1;
        int lastDamCount = -1;
        for (int i = MIN_ROLL; i <= MAX_ROLL; i++) {
            int dam = damage(m, p1, p2, mod1, mod2, i, false, extra_multiplier, isBattleTower, isDoubleBattle); /// TODO : Rage, Rollout etc.
            if (dam > oppHP) {
                dam = oppHP;
            }
            if (dam != lastDam) {
                if (lastDamCount != -1) {
                    sb.append(lastDam + "x" + lastDamCount + ", ");
                }
                lastDam = dam;
                lastDamCount = 1;
            } else {
                lastDamCount++;
            }
        }
        sb.append(lastDam + "x" + lastDamCount + endl);

        // crit rolls
        sb.append("\tCrit rolls: ");
        lastDam = -1;
        lastDamCount = -1;
        for (int i = MIN_ROLL; i <= MAX_ROLL; i++) {
            int dam = damage(m, p1, p2, mod1, mod2, i, true, extra_multiplier, isBattleTower, isDoubleBattle); /// TODO : Rage, Rollout etc.
            if (dam > oppHP) {
                dam = oppHP;
            }
            if (dam != lastDam) {
                if (lastDamCount != -1) {
                    sb.append(lastDam + "x" + lastDamCount + ", ");
                }
                lastDam = dam;
                lastDamCount = 1;
            } else {
                lastDamCount++;
            }
        }
        sb.append(lastDam + "x" + lastDamCount + endl);

        int realminDmg = Math.min(minDmg, critMinDmg);
        int realmaxDmg = Math.max(maxDmg, critMaxDmg);

        // TODO : proper handling of critical hits
        if (Settings.includeCrits) {

            double critChance = 1 / 16.0;
            if (m.getEffect() == MoveEffect.HIGH_CRITICAL){
                critChance *= 4;
            }

            for (int hits = 1; hits <= 8; hits++) {
                if (realminDmg * hits < oppHP && realmaxDmg * hits >= oppHP) {
                    double totalKillPct = 0;
                    for (int crits = 0; crits <= hits; crits++) {
                        double nShotPct = nShotPercentage(m, p1, p2, mod1, mod2, hits - crits, crits, extra_multiplier, isBattleTower, isDoubleBattle); /// TODO : Rage, Rollout etc.
                        totalKillPct += nShotPct * choose(hits, crits) * Math.pow(critChance, crits)
                                * Math.pow(1 - critChance, hits - crits);
                    }
                    if (totalKillPct >= 0.1 && totalKillPct <= 99.999) {
                        sb.append(String.format("\t(Overall %d-hit Kill%%: %.04f%%)", hits, totalKillPct) + endl);
                    }
                }
            }
        } else {

            // test if noncrits can kill in 1shot
            if (maxDmg >= oppHP && minDmg < oppHP) {
                int oneShotNum = oneShotNumerator(m, p1, p2, mod1, mod2, false, extra_multiplier, isBattleTower, isDoubleBattle); /// TODO : Rage, Rollout etc.
                sb.append(String.format("\t(One shot prob.: %d/%d | %.02f%%)", oneShotNum, NUM_ROLLS, 100. * oneShotNum / NUM_ROLLS) + endl);
            }
            // test if crits can kill in 1shot
            if (critMaxDmg >= oppHP && critMinDmg < oppHP) {
                int oneShotNum = oneShotNumerator(m, p1, p2, mod1, mod2, true, extra_multiplier, isBattleTower, isDoubleBattle); /// TODO : Rage, Rollout etc.
                sb.append(String.format("\t(Crit one shot prob.: %d/%d | %.02f%%)", oneShotNum, NUM_ROLLS, 100. * oneShotNum / NUM_ROLLS) + endl);
            }

            // n-shot
            int minDmgWork = minDmg;
            int maxDmgWork = maxDmg;
            int hits = 1;
            while (minDmgWork < oppHP && hits < 5) {
                hits++;
                minDmgWork += minDmg;
                maxDmgWork += maxDmg;
                if (maxDmgWork >= oppHP && minDmgWork < oppHP) {
                    //System.out.println("working out a " + hits + "-shot"); //TODO: remaining println
                    double nShotPct = nShotPercentage(m, p1, p2, mod1, mod2, hits, 0, extra_multiplier, isBattleTower, isDoubleBattle); /// TODO : Rage, Rollout etc.
                    sb.append(String.format("\t(%d shot prob.: %.04f%%)", hits, nShotPct) + endl);
                }
            }

            // n-crit-shot
            minDmgWork = critMinDmg;
            maxDmgWork = critMaxDmg;
            hits = 1;
            while (minDmgWork < oppHP && hits < 5) {
                hits++;
                minDmgWork += critMinDmg;
                maxDmgWork += critMaxDmg;
                if (maxDmgWork >= oppHP && minDmgWork < oppHP) {
                    //System.out.println("working out a " + hits + "-crit-shot"); //TODO: remaining println
                    double nShotPct = nShotPercentage(m, p1, p2, mod1, mod2, 0, hits, extra_multiplier, isBattleTower, isDoubleBattle); /// TODO : Rage, Rollout etc.
                    sb.append(String.format("\t(%d crits death prob.: %.04f%%)", hits, nShotPct) + endl); 
                }
            }

            // mixed a-noncrit and b-crit shot
            for (int non = 1; non <= 5 && realminDmg * (non + 1) < oppHP; non++) {
                for (int crit = 1; non + crit <= 5 && realminDmg * (non + crit) < oppHP; crit++) {
                    int sumMin = critMinDmg * crit + minDmg * non;
                    int sumMax = critMaxDmg * crit + maxDmg * non;
                    if (sumMin < oppHP && sumMax >= oppHP) {
                        //System.out.printf("working out %d non-crits + %d crits\n", non, crit); //TODO: remaining println
                        double nShotPct = nShotPercentage(m, p1, p2, mod1, mod2, non, crit, extra_multiplier, isBattleTower, isDoubleBattle); /// TODO : Rage, Rollout etc.
                        sb.append(String.format("\t(%d non-crit%s + %d crit%s death prob.: %.04f%%)", non,
                                non > 1 ? "s" : "", crit, crit > 1 ? "s" : "", nShotPct)
                                + endl);
                    }
                }
            }
        }

        // guaranteed n-shot
        if (Settings.showGuarantees) {
            int guarantee = (int) Math.ceil(((double) oppHP) / realminDmg);
            sb.append(String.format("\t(guaranteed %d-shot)", guarantee) + endl);
        }
    }
    
    
    
    // ******************** //
    // MATHS HELPER METHODS //
    // ******************** //
    
    public static long choose(long total, long choose) {
        if (total < choose)
            return 0;
        if (choose == 0 || choose == total)
            return 1;
        if (choose == 1 || choose == total - 1)
            return total;
        return choose(total - 1, choose - 1) + choose(total - 1, choose);
    }

    private static int oneShotNumerator(Move attack, Pokemon attacker,
                                            Pokemon defender, StatModifier atkMod, StatModifier defMod,
                                            boolean crit, int extra_multiplier, boolean isBattleTower, boolean isDoubleBattle) {
        // iterate until damage is big enough
        int rangeNum = MIN_ROLL;
        while (damage(attack, attacker, defender, atkMod, defMod, rangeNum,
                crit, extra_multiplier, isBattleTower, isDoubleBattle) < defender.getHP()) {
            rangeNum++;
        }
        return MAX_ROLL - rangeNum + 1 ;
    }

    private static TreeMap<Integer,Double> detailledDamage(Move attack, Pokemon attacker, Pokemon defender,
                                                          StatModifier atkMod, StatModifier defMod, boolean crit,
                                                          int extra_multiplier, boolean isBattleTower, boolean isDoubleBattle) {
        TreeMap<Integer,Double> dmgMap = new TreeMap<Integer,Double>();
        for(int i=MIN_ROLL; i<=MAX_ROLL; i++) {
            int dmg = Math.min(defender.getHP(), damage(attack, attacker, defender, atkMod, defMod, i, crit, extra_multiplier, isBattleTower, isDoubleBattle));
            if(dmgMap.containsKey(dmg)) {
                dmgMap.put(dmg,100.0/((double)(NUM_ROLLS))+dmgMap.get(dmg));
            } else {
                dmgMap.put(dmg,100.0/((double)(NUM_ROLLS)));
            }
        }
        return dmgMap;
    }
    
    private static double nShotPercentage(Move attack, Pokemon attacker, Pokemon defender, StatModifier atkMod,
            StatModifier defMod, int numHitsNonCrit, int numHitsCrit, int extra_multiplier, boolean isBattleTower, boolean isDoubleBattle) {
        int rawHitDamageNC = damage(attack, attacker, defender, atkMod, defMod, MAX_ROLL, false, extra_multiplier, isBattleTower, isDoubleBattle);  /// TODO : Rage, Rollout etc.
        int minDamageNC = rawHitDamageNC * MIN_ROLL / MAX_ROLL;
        int[] probsNC = new int[rawHitDamageNC - minDamageNC + 1];
        for (int i = MIN_ROLL; i <= MAX_ROLL; i++) {
            int dmg = rawHitDamageNC * i / MAX_ROLL;
            probsNC[dmg - minDamageNC]++;
        }
        int rawHitDamageCR = damage(attack, attacker, defender, atkMod, defMod, MAX_ROLL, true, extra_multiplier, isBattleTower, isDoubleBattle);  /// TODO : Rage, Rollout etc.
        int minDamageCR = rawHitDamageCR * MIN_ROLL / MAX_ROLL;
        int[] probsCR = new int[rawHitDamageCR - minDamageCR + 1];
        for (int i = MIN_ROLL; i <= MAX_ROLL; i++) {
            int dmg = rawHitDamageCR * i / MAX_ROLL;
            probsCR[dmg - minDamageCR]++;
        }
        double chances = 0;
        int rawHP = defender.getHP();
        if (numHitsNonCrit > 0) {
            for (int i = minDamageNC; i <= rawHitDamageNC; i++) {
                chances += nShotPctInner(minDamageNC, rawHitDamageNC, minDamageCR, rawHitDamageCR, rawHP, 0, i,
                        numHitsNonCrit, numHitsCrit, probsNC, probsCR);
            }
        } else {
            for (int i = minDamageCR; i <= rawHitDamageCR; i++) {
                chances += nShotPctInner(minDamageNC, rawHitDamageNC, minDamageCR, rawHitDamageCR, rawHP, 0, i,
                        numHitsNonCrit, numHitsCrit, probsNC, probsCR);
            }
        }
        return 100.0 * chances / Math.pow(MAX_ROLL - MIN_ROLL + 1, numHitsNonCrit + numHitsCrit);
    }

    private static double nShotPctInner(int minDamageNC, int maxDamageNC, int minDamageCR, int maxDamageCR, int hp,
            int stackedDmg, int rolledDamage, int hitsLeftNonCrit, int hitsLeftCrit, int[] probsNC, int[] probsCR) {
        boolean wasCritical = false;
        if (hitsLeftNonCrit > 0) {
            hitsLeftNonCrit--;
        } else {
            hitsLeftCrit--;
            wasCritical = true;
        }
        stackedDmg += rolledDamage;
        if (stackedDmg >= hp || (stackedDmg + hitsLeftNonCrit * minDamageNC + hitsLeftCrit * minDamageCR) >= hp) {
            return Math.pow(MAX_ROLL - MIN_ROLL + 1, hitsLeftNonCrit + hitsLeftCrit)
                    * (wasCritical ? probsCR[rolledDamage - minDamageCR] : probsNC[rolledDamage - minDamageNC]);
        } else if (hitsLeftNonCrit == 0 && hitsLeftCrit == 0) {
            return 0;
        } else if (stackedDmg + hitsLeftNonCrit * maxDamageNC + hitsLeftCrit * maxDamageCR < hp) {
            return 0;
        } else {
            double chances = 0;
            if (hitsLeftNonCrit > 0) {
                for (int i = minDamageNC; i <= maxDamageNC; i++) {
                    chances += nShotPctInner(minDamageNC, maxDamageNC, minDamageCR, maxDamageCR, hp, stackedDmg, i,
                            hitsLeftNonCrit, hitsLeftCrit, probsNC, probsCR);
                }
            } else {
                for (int i = minDamageCR; i <= maxDamageCR; i++) {
                    chances += nShotPctInner(minDamageNC, maxDamageNC, minDamageCR, maxDamageCR, hp, stackedDmg, i,
                            hitsLeftNonCrit, hitsLeftCrit, probsNC, probsCR);
                }
            }
            return chances * (wasCritical ? probsCR[rolledDamage - minDamageCR] : probsNC[rolledDamage - minDamageNC]);
        }
    }
}
