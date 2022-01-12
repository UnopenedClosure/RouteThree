import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/* TODO : Implement damage effects
 * @ = implemented
 * - = to-do
 * 
 * @ PSYWAVE :
 *   > script : https://github.com/pret/pokeruby/blob/0ea1e7620cc5fea1e651974442052ba9c52cdd13/data/battle_scripts_1.s#L1266
 *   > code : https://github.com/pret/pokeruby/blob/a3228d4c86494ee25aff60fc037805ddc1d47d32/src/battle_script_commands.c#L7690
 * 	 > (remark) No damage variation, only base power variation based on level
 *   
 * @ FLAIL :
 *   > script : https://github.com/pret/pokeruby/blob/0ea1e7620cc5fea1e651974442052ba9c52cdd13/data/battle_scripts_1.s#L1409
 *   > code : https://github.com/pret/pokeruby/blob/a3228d4c86494ee25aff60fc037805ddc1d47d32/src/battle_script_commands.c#L8014
 *   
 * @ RAGE : 
 *   > script : https://github.com/pret/pokeruby/blob/0ea1e7620cc5fea1e651974442052ba9c52cdd13/data/battle_scripts_1.s#L1180
 *   > code : https://github.com/pret/pokeruby/blob/a3228d4c86494ee25aff60fc037805ddc1d47d32/src/battle_script_commands.c#L4298
 *          : https://github.com/pret/pokeruby/blob/a3228d4c86494ee25aff60fc037805ddc1d47d32/src/battle_script_commands.c#L8211
 *   > (remark) For Gen III onwards, Rage increases the ATK stat stage
 *   > (implementation) Not implemented in a special way
 *          
 * @ ROLLOUT : 
 *   > script : https://github.com/pret/pokeruby/blob/0ea1e7620cc5fea1e651974442052ba9c52cdd13/data/battle_scripts_1.s#L1673
 *   > code : https://github.com/pret/pokeruby/blob/a3228d4c86494ee25aff60fc037805ddc1d47d32/src/battle_script_commands.c#L8211
 *   > (implementation) not handling Defense Curl, but pushing the multiplier up to stage 6 instead
 *   
 * @ FURY_CUTTER : 
 *   > script : https://github.com/pret/pokeruby/blob/0ea1e7620cc5fea1e651974442052ba9c52cdd13/data/battle_scripts_1.s#L1711
 *   > code : https://github.com/pret/pokeruby/blob/a3228d4c86494ee25aff60fc037805ddc1d47d32/src/battle_script_commands.c#L8250
 *   
 * @ MAGNITUDE : 
 *   > script : https://github.com/pret/pokeruby/blob/0ea1e7620cc5fea1e651974442052ba9c52cdd13/data/battle_scripts_1.s#L1764
 *   > code : https://github.com/pret/pokeruby/blob/a3228d4c86494ee25aff60fc037805ddc1d47d32/src/battle_script_commands.c#L8324
 *   
 * - PRESENT : https://github.com/pret/pokeruby/blob/0ea1e7620cc5fea1e651974442052ba9c52cdd13/data/battle_scripts_1.s#L1745
 * - PURSUIT : https://github.com/pret/pokeruby/blob/0ea1e7620cc5fea1e651974442052ba9c52cdd13/data/battle_scripts_1.s#L3121
 * - RETURN
 * - FRUSTRATION
 * - TWISTER : https://github.com/pret/pokeruby/blob/0ea1e7620cc5fea1e651974442052ba9c52cdd13/data/battle_scripts_1.s#L1908
 * - EARTHQUAKE : https://github.com/pret/pokeruby/blob/0ea1e7620cc5fea1e651974442052ba9c52cdd13/data/battle_scripts_1.s#L1917
 * - GUST : https://github.com/pret/pokeruby/blob/0ea1e7620cc5fea1e651974442052ba9c52cdd13/data/battle_scripts_1.s#L1981
 * - SPIT_UP : https://github.com/pret/pokeruby/blob/0ea1e7620cc5fea1e651974442052ba9c52cdd13/data/battle_scripts_1.s#L2196
 * - FACADE : https://github.com/pret/pokeruby/blob/0ea1e7620cc5fea1e651974442052ba9c52cdd13/data/battle_scripts_1.s#L2357
 * - SMELLINGSALT : https://github.com/pret/pokeruby/blob/0ea1e7620cc5fea1e651974442052ba9c52cdd13/data/battle_scripts_1.s#L2373
 * - NATURE_POWER : https://github.com/pret/pokeruby/blob/0ea1e7620cc5fea1e651974442052ba9c52cdd13/data/battle_scripts_1.s#L2394
 * - REVENGE : https://github.com/pret/pokeruby/blob/0ea1e7620cc5fea1e651974442052ba9c52cdd13/data/battle_scripts_1.s#L2520
 * 
 * @ LOW_KICK : 
 *   > script : https://github.com/pret/pokeruby/blob/0ea1e7620cc5fea1e651974442052ba9c52cdd13/data/battle_scripts_1.s#L2665
 *   > code : https://github.com/pret/pokeruby/blob/a3228d4c86494ee25aff60fc037805ddc1d47d32/src/battle_script_commands.c#L9033
 * 
 * - STOMP : https://github.com/pret/pokeruby/blob/0ea1e7620cc5fea1e651974442052ba9c52cdd13/data/battle_scripts_1.s#L1987
 * 
 * TODO : Implement multi-hit effects
 * - TRIPLE_KICK : https://github.com/pret/pokeruby/blob/0ea1e7620cc5fea1e651974442052ba9c52cdd13/data/battle_scripts_1.s#L1450
 * 
 * TODO : Residual damage
 * - Statuses
 * - Weathers
 * - SPIKES
 * 
 * TODO : Special types
 * - FORESIGHT
 * - WEATHER_BALL : https://github.com/pret/pokeruby/blob/0ea1e7620cc5fea1e651974442052ba9c52cdd13/data/battle_scripts_1.s#L2754
 * 
 * TODO : Implement priority markers 
 * - QUICK_ATTACK
 * 
 * TODO : Implement heal moves
 * - MORNING_SUN
 * - SYNTHESIS
 * - MOONLIGHT
 * - SOFTBOILED
 * - SWALLOW
 * - INGRAIN
 */


/* Updates for new move/damage logics :
 * **************************************
 * 1. to add a special damage roll generation :
 *    ==>> DamageCalculator.damage()
 *    
 * 2. to generate special rolls (PSYWAVE, ...) :
 *    ==>> Damages.calculate()
 *    
 * 3. to handle moves with multiple multipliers (RAGE, ROLLOUT, ...) or base powers (MAGNITUDE, ...) :
 *    ==>> DamageCalculator.damagesSummaryCore()
 *    
 * 4. to handle text formatting :
 *    ==>> DamageCalculator.appendFormattedMoveName()
 */
public class DamageCalculator {
    private static int MIN_ROLL = 85;
    private static int MAX_ROLL = 100;
    // private static int NUM_ROLLS = MAX_ROLL - MIN_ROLL + 1;
    
    private static int MIN_PSYWAVE_ROLL = 50;
    private static int MAX_PSYWAVE_ROLL = 150;
    private static int PSYWAVE_DIV = 100;
    private static int PSYWAVE_ROLL_STEP = 10;

    /**
     * A wrapper class for handling damages.
     * @author UnderscorePoY
     *
     */
	public static class Damages {
		private TreeMap<Integer, Long> normalDamageRolls;
		private TreeMap<Integer, Long> critDamageRolls;
		private boolean hasCrit = true;
		private int numRolls = 0;
		
		private Move attackMove;
		private Pokemon attacker;
		private Pokemon defender;
		private StatModifier atkMod;
		private StatModifier defMod;
		private int extra_multiplier;
		private boolean isBattleTower;
		private boolean isDoubleBattle;
		
		private long normalHitsGreaterOrEqualThanEnemyHP = 0;
		private long critHitsGreaterOrEqualThanEnemyHP = 0;
		
		
		public Damages(Move attackMove, Pokemon attacker, Pokemon defender,
                StatModifier atkMod, StatModifier defMod, int extra_multiplier, boolean isBattleTower, boolean isDoubleBattle) {
			normalDamageRolls = new TreeMap<>();
			critDamageRolls = new TreeMap<>();
			
			this.attackMove = attackMove;
			this.attacker = attacker;
			this.defender = defender;
			this.atkMod = atkMod;
			this.defMod = defMod;
			this.extra_multiplier = extra_multiplier;
			this.isBattleTower = isBattleTower;
			this.isDoubleBattle = isDoubleBattle;
			
			this.calculate();
			for(long mult : normalDamageRolls.tailMap(defender.getHP()).values())
				normalHitsGreaterOrEqualThanEnemyHP += mult;
			for(long mult : critDamageRolls.tailMap(defender.getHP()).values())
				critHitsGreaterOrEqualThanEnemyHP += mult;
		}
		
		private void calculate() {
			switch(attackMove.getEffect()) {
			case PSYWAVE:
				this.setCrit(false);
				for (int roll = MIN_PSYWAVE_ROLL; roll <= MAX_PSYWAVE_ROLL; roll += PSYWAVE_ROLL_STEP) {
					int dmg = damage(attackMove, attacker, defender, atkMod, defMod, roll, false, extra_multiplier, isBattleTower, isDoubleBattle);
					this.addNormalDamage(dmg);
				}
				break;
				
			case FUTURE_SIGHT:
				this.setCrit(false);
				int dmg = damage(attackMove, attacker, defender, atkMod, defMod, MAX_ROLL, false, extra_multiplier, isBattleTower, isDoubleBattle);
				this.addNormalDamage(dmg);
				break;
				
			case LEVEL_DAMAGE:
				this.setCrit(false);
				dmg = damage(attackMove, attacker, defender, atkMod, defMod, MAX_ROLL, false, extra_multiplier, isBattleTower, isDoubleBattle);
				this.addNormalDamage(dmg);
				break;
				
			default:
				for (int roll = MIN_ROLL; roll <= MAX_ROLL; roll++) {
					dmg = damage(attackMove, attacker, defender, atkMod, defMod, roll, false, extra_multiplier, isBattleTower, isDoubleBattle);
					this.addNormalDamage(dmg);
					
					int critDmg = damage(attackMove, attacker, defender, atkMod, defMod, roll, true, extra_multiplier, isBattleTower, isDoubleBattle);
					this.addCritDamage(critDmg);
				}
				break;
			}
		}
		
		
		/* ************************ */
		/* INSTANCE-RELATED METHODS */
		/* ************************ */
		
		private int lowestNormalDamage() {
			return normalDamageRolls.firstKey();
		}
		
		private int highestNormalDamage() {
			return normalDamageRolls.lastKey();
		}
		
		private int lowestCritDamage() {
			return critDamageRolls.firstKey();
		}
		
		private int highestCritDamage() {
			return critDamageRolls.lastKey();
		}
		
		private int lowestDamage() {
			return lowestNormalDamage();
		}
		
		private int highestDamage() {
			if(hasCrit)
				return critDamageRolls.lastKey();
			return normalDamageRolls.lastKey();
		}
		
		private void increment(TreeMap<Integer, Long> map, int dmg) {
			if (!map.containsKey(dmg))
				map.put(dmg, (long) 1);
			else
				map.put(dmg, 1 + map.get(dmg));
			
			if(map == normalDamageRolls)
				numRolls++;
		}
		
		public void addNormalDamage(int dmg) {
			increment(normalDamageRolls, dmg);
		}
		
		public void addCritDamage(int dmg) {
			increment(critDamageRolls, dmg);
		}
		
		public boolean hasCrit() {
			return hasCrit;
		}
		
		public void setCrit(boolean hasCrit) {
			this.hasCrit = hasCrit;
		}
		
		public int getNumRolls() {
			return numRolls;
		}
		
		public boolean hasDamage() {
			return highestDamage() != 0;
		}
		
	    private long oneShotNumerator(boolean crit) {
			if(crit)
				return critHitsGreaterOrEqualThanEnemyHP;
			else
				return normalHitsGreaterOrEqualThanEnemyHP;
		}
	    
	    
	    
	    
		
		/* ************************ */
		/* PRINTING-RELATED METHODS */
		/* ************************ */
		
		private void appendDamages(StringBuilder sb, TreeMap<Integer, Long> map) {
			String endl = Constants.endl;
			
			int enemyHP = defender.getHP();
			int lastHPtoPrint = Math.min(map.lastKey(), enemyHP);
			
			// All but last damage strictly lower than enemyHP displayed one by one
			SortedMap<Integer, Long> headMap = map.headMap(lastHPtoPrint, false); // Map corresponding to [minDmg, lastHPtoPrint[
			for (Map.Entry<Integer, Long> headEntry : headMap.entrySet()) {
				int dmg = headEntry.getKey();
				long mult = headEntry.getValue();
				sb.append(String.format("%dx%d, ", dmg, mult));
			}
			
			// Last or all damage higher or equal than enemyHP displayed only once
			SortedMap<Integer, Long> tailMap = map.tailMap(lastHPtoPrint); // Map corresponding to [lastHPtoPrint, maxDmg]
			long totalMult = 0;
			for (Map.Entry<Integer, Long> tailEntry : tailMap.entrySet()) {
				long mult = tailEntry.getValue();
				totalMult += mult;
			}
			sb.append(String.format("%dx%d", lastHPtoPrint, totalMult));
			sb.append(endl);
		}
		
		public void appendNormalDamages(StringBuilder sb) {
			appendDamages(sb, normalDamageRolls);
		}
		
		public void appendCritDamages(StringBuilder sb) {
			appendDamages(sb, critDamageRolls);
		}
		
		public void appendShortDamages(StringBuilder sb) {
			String endl = Constants.endl;
			
			int minDmg = lowestNormalDamage();
			int maxDmg = highestNormalDamage();
			
	        double minPct = toDmgPercent(minDmg);
	        double maxPct = toDmgPercent(maxDmg);
	        
	        if(minDmg == maxDmg)
	        	sb.append(String.format("%d %.02f%%", minDmg, minPct));
	        else
	        	sb.append(String.format("%d-%d %.02f-%.02f%%", minDmg, maxDmg, minPct, maxPct));
	        
	        if(hasCrit()) {
		        sb.append("\t(crit: ");
		        
		        int critMinDmg = lowestCritDamage();
				int critMaxDmg = highestCritDamage();
	
		        double critMinPct = toDmgPercent(critMinDmg);
		        double critMaxPct = toDmgPercent(critMaxDmg);
		        
		        if(critMinDmg == critMaxDmg)
		        	sb.append(String.format("%d %.02f%%)", critMinDmg, critMinPct));
		        else
		        	sb.append(String.format("%d-%d %.02f-%.02f%%)", critMinDmg, critMaxDmg, critMinPct, critMaxPct));
	        }
	        
	        sb.append(endl);
		}
		
		public void appendOverallChanceKO(StringBuilder sb) {
			String endl = Constants.endl;
			
			int oppHP = defender.getHP();
			int realminDmg = lowestDamage();
	        int realmaxDmg = highestDamage();
			// TODO : proper handling of critical hits
            double critChance = 1 / 16.0; //TODO : hardcoded
            if (attackMove.getEffect() == MoveEffect.HIGH_CRITICAL){
                critChance *= 4;
            }

            for (int hits = 1; hits <= 8; hits++) {
                if (realminDmg * hits < oppHP && realmaxDmg * hits >= oppHP) {
                    double totalKOPct = 0;
                    for (int crits = 0; crits <= hits; crits++) {
                        double nShotPct = nShotPercentage(hits - crits, crits, normalDamageRolls, critDamageRolls); /// TODO : Rage, Rollout etc.
                        totalKOPct += nShotPct * choose(hits, crits) * Math.pow(critChance, crits)
                                * Math.pow(1 - critChance, hits - crits);
                    }
                    if (totalKOPct >= 0.1 && totalKOPct <= 99.999) {
                        sb.append(String.format("\t(Overall %d-hit KO%%: %.04f%%)", hits, totalKOPct));
                        sb.append(endl);
                    }
                }
            }
		}
		
		public void appendNShots(StringBuilder sb) {
			String endl = Constants.endl;
			
			int oppHP = defender.getHP();

			int minDmg = lowestNormalDamage();
			int maxDmg = highestNormalDamage();

            // test if noncrits can KO in 1shot
            if (maxDmg >= oppHP && minDmg < oppHP) {
                long oneShotNum = oneShotNumerator(false); /// TODO : Rage, Rollout etc.
                sb.append(String.format("\t(One shot prob.: %d/%d | %.02f%%)", oneShotNum, numRolls, toPercent(oneShotNum, numRolls)));
                sb.append(endl);
            }
            // test if crits can KO in 1shot
            if(hasCrit()) {
    			int critMinDmg = lowestCritDamage();
    			int critMaxDmg = highestCritDamage();
	            if (critMaxDmg >= oppHP && critMinDmg < oppHP) {
	                long oneShotNum = oneShotNumerator(true); /// TODO : Rage, Rollout etc.
	                sb.append(String.format("\t(Crit one shot prob.: %d/%d | %.02f%%)", oneShotNum, numRolls, toPercent(oneShotNum,numRolls)));
	                sb.append(endl);
	            }
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
                    double nShotPct = nShotPercentage(hits, 0, normalDamageRolls, critDamageRolls); /// TODO : Rage, Rollout etc.
                    sb.append(String.format("\t(%d shot prob.: %.04f%%)", hits, nShotPct));
                    sb.append(endl);
                }
            }

            // n-crit-shot
            if(hasCrit()) {
    			int critMinDmg = lowestCritDamage();
    			int critMaxDmg = highestCritDamage();
	            minDmgWork = critMinDmg;
	            maxDmgWork = critMaxDmg;
	            hits = 1;
	            while (minDmgWork < oppHP && hits < 5) {
	                hits++;
	                minDmgWork += critMinDmg;
	                maxDmgWork += critMaxDmg;
	                if (maxDmgWork >= oppHP && minDmgWork < oppHP) {
	                    //System.out.println("working out a " + hits + "-crit-shot"); //TODO: remaining println
	                    double nShotPct = nShotPercentage(0, hits, normalDamageRolls, critDamageRolls); /// TODO : Rage, Rollout etc.
	                    sb.append(String.format("\t(%d crits death prob.: %.04f%%)", hits, nShotPct));
	                    sb.append(endl);
	                }
	            }
            

				int realminDmg = lowestNormalDamage();
		        //int realmaxDmg = highestCritDamage();
	            // mixed a-noncrit and b-crit shot
	            for (int non = 1; non <= 5 && realminDmg * (non + 1) < oppHP; non++) {
	                for (int crit = 1; non + crit <= 5 && realminDmg * (non + crit) < oppHP; crit++) {
	                    int sumMin = critMinDmg * crit + minDmg * non;
	                    int sumMax = critMaxDmg * crit + maxDmg * non;
	                    if (sumMin < oppHP && sumMax >= oppHP) {
	                        //System.out.printf("working out %d non-crits + %d crits\n", non, crit); //TODO: remaining println
	                        double nShotPct = nShotPercentage(non, crit, normalDamageRolls, critDamageRolls); /// TODO : Rage, Rollout etc.
	                        sb.append(String.format("\t(%d non-crit%s + %d crit%s death prob.: %.04f%%)", non,
	                                non > 1 ? "s" : "", crit, crit > 1 ? "s" : "", nShotPct));
	                        sb.append(endl);
	                    }
	                }
	            }
            }
		}
		
		public void appendGuaranteed(StringBuilder sb) {
			String endl = Constants.endl;
			int oppHP = defender.getHP();
			int realminDmg = lowestDamage();
            int guarantee = (int) Math.ceil(((double) oppHP) / realminDmg);
            
            sb.append(String.format("\t(guaranteed %d-shot)", guarantee));
            sb.append(endl);
		}
		
		 private void appendDetailledPercentMap(StringBuilder sb, TreeMap<Integer, Double> map) {
		    	final int ROLLS_PER_LINE = 8;
		    	
		        String endl = Constants.endl;
		    	
		    	Pokemon p2 = this.defender;
		    	int minDmg = map.firstKey();
		    	
		    	int cnt = -1;
	            for(int dmg : map.keySet()) {
	            	cnt++;
	                if(cnt % ROLLS_PER_LINE == 0) {
	                    sb.append(endl);
	                    if(dmg == p2.getHP() && minDmg != p2.getHP()) {
	                        sb.append(endl);
	                    }
	                    sb.append("            ");
	                }
	                else if(dmg == p2.getHP() && minDmg != p2.getHP()) {
	                    sb.append(endl);
	                    sb.append(endl);
	                    sb.append("            ");
	                }
	                sb.append(String.format("%3d: %6.02f%%     ", dmg, map.get(dmg)));
	            }
	            sb.append(endl);
	            sb.append(endl);
		    }
		    
		    public void appendDetailledPercentDamages(StringBuilder sb) {
		    	Move move = this.attackMove;
		    	int _extra_modifier = this.extra_multiplier;
		    	Pokemon p1 = attacker;
		    	Pokemon p2 = defender;
		    	StatModifier mod1 = atkMod;
		    	StatModifier mod2 = defMod;
		    	Object param = null;

		        String endl = Constants.endl;
		        
		        if(hasDamage()) {
		            TreeMap<Integer,Double> dmgMap = percentMapWithMaxHP(this.normalDamageRolls, p2.getHP());
		            
		            appendFormattedMoveName(sb, move, p1, p2, mod1, mod2, _extra_modifier, isBattleTower, isDoubleBattle, param);
		            sb.append(endl);
		            
		            sb.append("          NON-CRITS");
		            appendDetailledPercentMap(sb, dmgMap);
		            	            
		            if(this.hasCrit()) {
			            TreeMap<Integer,Double> critMap = percentMapWithMaxHP(this.critDamageRolls, p2.getHP());
			            sb.append("          CRITS");
			            appendDetailledPercentMap(sb, critMap);
		            }
		        }
		    }
		    
		    public void appendAllMoveInfo(StringBuilder sb) {
				String endl = Constants.endl;
				Move m = this.attackMove;
				int _extra_multiplier = this.extra_multiplier;
				Pokemon p1 = attacker;
				Pokemon p2 = defender;
				StatModifier mod1 = atkMod;
				StatModifier mod2 = defMod;
				Object param = null;

				appendFormattedMoveName(sb, m, p1, p2, mod1, mod2, _extra_multiplier, isBattleTower, isDoubleBattle, param);
				
				if (hasDamage()) {
					sb.append("\t");
					appendShortDamages(sb);
					
					// normal rolls
					sb.append("\tNormal rolls: ");
					appendNormalDamages(sb);
					
					// crit rolls
					if(hasCrit()) {
						sb.append("\tCrit rolls: ");
						appendCritDamages(sb);
					}
					
					appendNShots(sb);
					
					// guaranteed n-shot
					if (Settings.showGuarantees)
					appendGuaranteed(sb);
					
					// overall chance of KO
					if (Settings.overallChanceKO)
					appendOverallChanceKO(sb);
				}
				
				sb.append(endl);
			}

		
		    
		/* ********************* */
		/* MATHS UTILITY METHODS */
		/* ********************* */
		    
		private double toDmgPercent(int dmg) {
			return toPercent(dmg, defender.getHP());
		}
		
		
		private static double toPercent(long num, long div) {
			return 100. * num / div;
		}
		
	    private static long choose(long total, long choose) {
	        if (total < choose)
	            return 0;
	        if (choose == 0 || choose == total)
	            return 1;
	        if (choose == 1 || choose == total - 1)
	            return total;
	        return choose(total - 1, choose - 1) + choose(total - 1, choose);
	    }

	    
		/* ******************* */
		/* MAP UTILITY METHODS */
		/* ******************* */
	    
	    private TreeMap<Integer, Long> multiplyMaps(TreeMap<Integer, Long> map1, TreeMap<Integer, Long> map2){
	    	TreeMap<Integer, Long> results = new TreeMap<>();
	    	for (Map.Entry<Integer, Long> map1Entry : map1.entrySet()) {
	    		int dmg1 = map1Entry.getKey();
	    		long mult1 = map1Entry.getValue();
	    		for (Map.Entry<Integer, Long> map2Entry : map2.entrySet()) {
	    			int dmg2 = map2Entry.getKey();
		    		long mult2 = map2Entry.getValue();
		    		
		    		int dmg = dmg1 + dmg2;
		    		long mult = mult1 * mult2;
		    		long oldMult = results.containsKey(dmg) ? results.get(dmg) : 0;
		    		
	    			results.put(dmg, mult + oldMult);	
	    		}
	    	}
	    	
	    	return results;
	    }
	    
	    private TreeMap<Integer, Long> powerMap(TreeMap<Integer, Long> map1, int nb){
	    	TreeMap<Integer, Long> results;
	    	
	    	if (nb <= 0) {
	    		results = new TreeMap<>();
	    		results.put(0, (long) 1);
	    		return results;
	    	}
	    	
	    	results = new TreeMap<>(map1);
	    	if (nb == 1) {
	    		results = new TreeMap<>(map1);
	    		return map1;
	    	}
	    	for (int i = 1; i < nb; i++) {
	    		results = multiplyMaps(results, map1);
	    	}
	    	
	    	return results;
	    }
	    
	    private static long totalHits(SortedMap<Integer, Long> map) {
	    	long total = 0;
	    	
	    	for(Map.Entry<Integer, Long> entry : map.entrySet())
	    		total += entry.getValue();
	    	
	    	return total;
	    }
	    
	    private double nShotPercentage(int normalHits, int critHits, TreeMap<Integer, Long> noCritMap, TreeMap<Integer, Long> critMap) {
	    	TreeMap<Integer, Long> multiHitsDmgMap = multiHitsDmgMap(normalHits, critHits, noCritMap, critMap);
	    	
	    	SortedMap<Integer, Long> koMap = multiHitsDmgMap.tailMap(defender.getHP());
	    	long numKORolls = totalHits(koMap);
	    	long totalRolls = totalHits(multiHitsDmgMap);
	    	
	    	return toPercent(numKORolls, totalRolls);
	    }
	    
	    private TreeMap<Integer, Long> multiHitsDmgMap(int normalHits, int hitsLeftCrit, TreeMap<Integer, Long> noCritMap, TreeMap<Integer, Long> critMap) {
	    	
	    	TreeMap<Integer, Long> noCritPower = powerMap(noCritMap, normalHits);
	    	TreeMap<Integer, Long> critPower = powerMap(critMap, hitsLeftCrit);
	    	TreeMap<Integer, Long> results = multiplyMaps(noCritPower, critPower);

	    	return results;
	    }
	    
	    public static TreeMap<Integer,Double> percentMapWithMaxHP(TreeMap<Integer, Long> damages, int maxHP) {
			TreeMap<Integer,Double> percentMap = new TreeMap<Integer,Double>();
	    	long totalHits = totalHits(damages);
	    	int lastHPtoPrint = Math.min(damages.lastKey(), maxHP);
	    		    	
	    	// All but last damage strictly lower than enemyHP displayed one by one
			SortedMap<Integer, Long> headMap = damages.headMap(maxHP, false); // Map corresponding to [minDmg, lastHPtoPrint[
			for (Map.Entry<Integer, Long> headEntry : headMap.entrySet()) {
				int dmg = headEntry.getKey();
				long rollNb = headEntry.getValue();
				double percent = ((double)rollNb/totalHits) * 100;
	    		percentMap.put(dmg, percent);
			}
			
			// Last or all damage higher or equal than enemyHP displayed only once
			SortedMap<Integer, Long> tailMap = damages.tailMap(maxHP); // Map corresponding to [lastHPtoPrint, maxDmg]
			long totalMult = 0;
			for (Map.Entry<Integer, Long> tailEntry : tailMap.entrySet()) {
				long mult = tailEntry.getValue();
				totalMult += mult;
			}
			double percent = ((double)totalMult/totalHits) * 100;
    		percentMap.put(lastHPtoPrint, percent);

			return percentMap;
		}
	    
	} // end Damages class
	
	
    // ****************** //
    // MAIN DAMAGE METHOD //
    // ****************** //
    
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
    	return damage(attackMove, attacker, defender,
                atkMod, defMod, roll,
                isCrit, extra_multiplier, isBattleTower, isDoubleBattle, null);
    }
	
	/**
	 * Calculate one damage value based on a provided roll.
	 * @param attackMove the used move.
	 * @param attacker the attacker Pokemon.
	 * @param defender the defender Pokemon.
	 * @param atkMod the attacker's stat modifiers.
	 * @param defMod the defender's stat modifiers.
	 * @param roll the provided roll.
	 * @param isCrit if the move is a critical hit.
	 * @param extra_multiplier the desired extra multiplier
	 * @param isBattleTower if the battle occurs in Battle Tower.
	 * @param isDoubleBattle if the battle is a double battle.
	 * @param param an optional parameter used in specific cases.
	 * @return the calculated damage value.
	 */
    private static int damage(Move attackMove, Pokemon attacker, Pokemon defender,
                              StatModifier atkMod, StatModifier defMod, int roll,
                              boolean isCrit, int extra_multiplier, boolean isBattleTower, boolean isDoubleBattle, 
                              Object param) {
        Move modifiedAttackMove = new Move(attackMove); // Copy
        
        /* Psywave :
         * - Code : https://github.com/pret/pokeruby/blob/a3228d4c86494ee25aff60fc037805ddc1d47d32/src/battle_script_commands.c#L7690
         * - Expected rolls : {50, 60, ..., 150} | Only possible damages are level * {50, 60, ..., 150} / 100
         */
        if (modifiedAttackMove.getEffect() == MoveEffect.PSYWAVE) {
        	Type atkType = modifiedAttackMove.getType();
        	Type defType1 = defender.getSpecies().getType1();
        	Type defType2 = defender.getSpecies().getType2();
        	if(Type.isImmune(atkType, defType1, defType2))
        		return 0; //TODO : hardcoded
        	
        	return attacker.getLevel() * roll / PSYWAVE_DIV;
        }
        
        
        /* Low Kick : 
         * - Base power table : https://github.com/pret/pokeruby/blob/a3228d4c86494ee25aff60fc037805ddc1d47d32/src/battle_script_commands.c#L995
         * - Comparison code : https://github.com/pret/pokeruby/blob/a3228d4c86494ee25aff60fc037805ddc1d47d32/src/battle_script_commands.c#L9033
         */
        if (modifiedAttackMove.getEffect() == MoveEffect.LOW_KICK) {
        	int defenderWeight = defender.getSpecies().getWeight();
        	int basePower; //TODO : hardcoded
        	if (defenderWeight < 100)
        		basePower = 20; //TODO : hardcoded
        	else if (defenderWeight < 250)
        		basePower = 40; //TODO : hardcoded
        	else if (defenderWeight < 500)
        		basePower = 60; //TODO : hardcoded
        	else if (defenderWeight < 1000)
        		basePower = 80; //TODO : hardcoded
        	else if (defenderWeight < 2000)
        		basePower = 100; //TODO : hardcoded
        	else 
        		basePower = 120; //TODO : hardcoded
        	
        	modifiedAttackMove.setPower(basePower);
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
        if (modifiedAttackMove.getEffect() == MoveEffect.LEVEL_DAMAGE) {
        	Type atkType = modifiedAttackMove.getType();
        	Type defType1 = defender.getSpecies().getType1();
        	Type defType2 = defender.getSpecies().getType2();
        	if(Type.isImmune(atkType, defType1, defType2))
        		return 0; //TODO : hardcoded
        	return attacker.getLevel();
        } else if (modifiedAttackMove.getEffect() == MoveEffect.ROLLOUT || modifiedAttackMove.getEffect() == MoveEffect.FURY_CUTTER) {
    		modifiedAttackMove.setPower(modifiedAttackMove.getPower() * extra_multiplier);
        }
        
        if (modifiedAttackMove.getPower() == 1) { // Special cases seem to have this in common
            // TODO: more special cases
        	switch(modifiedAttackMove.getEffect()) {        		
        	case HIDDEN_POWER:
                Type type = attacker.getIVs().getHiddenPowerType();
                int power = attacker.getIVs().getHiddenPowerPower();
                modifiedAttackMove.setType(type);
                modifiedAttackMove.setPower(power);
                break;
                
        	case SONICBOOM:
        		Type atkType = modifiedAttackMove.getType();
            	Type defType1 = defender.getSpecies().getType1();
            	Type defType2 = defender.getSpecies().getType2();
            	if(Type.isImmune(atkType, defType1, defType2))
            		return 0; //TODO : hardcoded
        		return 20; //TODO : hardcoded
        		
        	case DRAGON_RAGE:
        		atkType = modifiedAttackMove.getType();
            	defType1 = defender.getSpecies().getType1();
            	defType2 = defender.getSpecies().getType2();
            	if(Type.isImmune(atkType, defType1, defType2))
            		return 0; //TODO : hardcoded
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
        
        if(modifiedAttackMove.getEffect() == MoveEffect.FUTURE_SIGHT) // Future Sight stops here
        	return damage;
        
        
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
    
    
    public static int minPsywaveDamage(Move attack, Pokemon attacker,
	            Pokemon defender, StatModifier atkMod, StatModifier defMod,
	            int extra_multiplier, boolean isBattleTower, boolean isDoubleBattle) {
    	return damage(attack, attacker, defender, atkMod, defMod, MIN_PSYWAVE_ROLL,
    			false, extra_multiplier, isBattleTower, isDoubleBattle);
	}
	
	public static int maxPsywaveDamage(Move attack, Pokemon attacker,
	            Pokemon defender, StatModifier atkMod, StatModifier defMod,
	            int extra_multiplier, boolean isBattleTower, boolean isDoubleBattle) {
		return damage(attack, attacker, defender, atkMod, defMod, MAX_PSYWAVE_ROLL,
				false, extra_multiplier, isBattleTower, isDoubleBattle);
	}
    
    
    
    // ******************************** //
    // HELPER STRING FORMATTING METHODS //
    // ******************************** //
    
    public static void appendBattleIntroSummary(StringBuilder sb, Pokemon p1, Pokemon p2, BattleOptions options) {
        sb.append(String.format("%s vs %s", p1.levelNameNatureAbility(), p2.levelNameNatureAbility()));
        // Don't show exp for tower pokes (minor thing since exp isn't added anyway)
        if(!options.isBattleTower()) {
            sb.append("          >>> EXP GIVEN: " + p2.expGiven(options.getParticipants()));
        }
    }
    
    public static void appendPokemonSummary(StringBuilder sb, Pokemon p, StatModifier mod) {
    	sb.append(String.format("%s (%s) ", p.pokeName(), p.trueStatsStr()));
        if (mod.hasMods())
            sb.append(String.format("%s ", mod.summary(p)));
        if (p.getHeldItem() != null)
        	sb.append(String.format("<%s> ", p.getHeldItem().getDisplayName()));
        if (mod.getWeather() != Weather.NONE)
        	sb.append(String.format("~%s~ ", mod.getWeather()));
    }
    
    public static void appendFormattedMoveName(StringBuilder sb, Move m, Pokemon p1,
            Pokemon p2, StatModifier mod1, StatModifier mod2,
            int _extra_multiplier, boolean isBattleTower, boolean isDoubleBattle, Object param) {
        // Move name
    	switch(m.getEffect()) {
    	//case RAGE:
    	case FURY_CUTTER:
    	case ROLLOUT:
            sb.append(m.getBoostedName(_extra_multiplier));
            break;
            
    	case HIDDEN_POWER:
    		Type type = p1.getIVs().getHiddenPowerType();
	        int power = p1.getIVs().getHiddenPowerPower();
	        sb.append(String.format("%s [%s %d]", m.getName(), type, power)); // TODO : hardcoded
	        break;
	        
    	case MAGNITUDE:
    		power = m.getPower();
    		int magnitude = -1;
    		int percent = 0;
    		
    		if (power == 10) {//TODO : hardcoded
    			magnitude = 4;
    			percent = 5;
    		} else if (power == 30) {
    			magnitude = 5;
    			percent = 10;
    		} else if (power == 50) {
    			magnitude = 6;
    			percent = 20;
    		} else if (power == 70) {
    			magnitude = 7;
    			percent = 30;
    		} else if (power == 90) {
    			magnitude = 8;
    			percent = 20;
    		} else if (power == 110) {
    			magnitude = 9;
    			percent = 10;
    		} else {
    			magnitude = 10;
    			percent = 5;
    		}
    		sb.append(String.format("%s %d (%d%%)", m.getName(), magnitude, percent)); // TODO : hardcoded
	        break;
	        
    	case FLAIL:
    		int minScale, maxScale;
        	power = m.getPower();
        	//System.out.println(power);
        	if (power == 20) {
        		minScale = 33;
        		maxScale = 48;
        	} else if (power == 40) {
        		minScale = 17;
        		maxScale = 32;
        	} else if (power == 80) {
        		minScale = 10;
        		maxScale = 16;
        	} else if (power == 100) {
        		minScale = 5;
        		maxScale = 9;
        	} else if (power == 150) {
        		minScale = 2;
        		maxScale = 4;
        	} else {
        		minScale = 0;
        		maxScale = 1;
        	}
        	
        	int fullHP = p1.getHP();
        	int minHP = 0;
        	int maxHP = fullHP;
        	
        	for(int hp = 1; hp <= fullHP; hp++) {
        		int scale = hp * 48 / fullHP;
        		if(scale == minScale && minHP == 0)
        			minHP = hp;
        		if(scale > maxScale) {
        			maxHP = hp - 1;
        			break;
        		}
        	}
    		sb.append(String.format("%s %d (HP:%d-%d)", m.getName(), power, minHP, maxHP)); // TODO : hardcoded
    		break;
    		
    	default:
    		sb.append(m.getName());
    		break;
    	}

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
    public static String battleSummary(Pokemon p1, Pokemon p2, BattleOptions options) {
        StringBuilder sb = new StringBuilder();
        String endl = Constants.endl;
        StatModifier mod1 = options.getMod1();
        StatModifier mod2 = options.getMod2();

        appendBattleIntroSummary(sb, p1, p2, options);
        sb.append(endl);
        
        // Player side
        appendPokemonSummary(sb, p1, mod1);
        sb.append(endl);

        appendMainDamagesSummary(sb, p1, p2, mod1, mod2, options.isBattleTower(), options.isDoubleBattle());
        sb.append(endl);

        if(options.getVerbose() == BattleOptions.EVERYTHING || options.getVerbose() == BattleOptions.ALL) {
            appendVerboseDamagesSummary(sb, p1, p2, options);
            sb.append(endl);
            
            // Opponent side
            appendPokemonSummary(sb, p2, mod2);
            sb.append(endl);
        }
        appendMainDamagesSummary(sb, p2, p1, mod2, mod1, options.isBattleTower(), options.isDoubleBattle());
        sb.append(endl);

        if(options.getVerbose() == BattleOptions.EVERYTHING) {
            appendVerboseDamagesSummary(sb, p2, p1, options);
            sb.append(endl);
        }
        return sb.toString();
    }
    
    // used for the less verbose option
    public static String shortBattleSummary(Pokemon p1, Pokemon p2, BattleOptions options) {
        StringBuilder sb = new StringBuilder();
        String endl = Constants.endl;

        StatModifier mod1 = options.getMod1();
        StatModifier mod2 = options.getMod2();

        appendBattleIntroSummary(sb, p1, p2, options);
        sb.append(endl);
        
        appendPokemonSummary(sb, p1, mod1);
        sb.append(endl);

        appendMainDamagesSummary(sb, p1, p2, mod1, mod2, options.isBattleTower(), options.isDoubleBattle());
        sb.append(endl);
        
        appendPokemonSummary(sb, p2, mod2);
        sb.append(endl);

        sb.append(p2.getMoveset().toString());
        sb.append(endl);
        
        return sb.toString();
    }

       
    
    // ******************************** //
    // HELPER DAMAGE FORMATTING METHODS //
    // ******************************** //
    
     
    public static void appendVerboseDamagesSummary(StringBuilder sb, Pokemon p1, Pokemon p2, BattleOptions options) {
    	damagesSummaryCore(sb, p1, p2, options.getMod1(), options.getMod2(), options.isBattleTower(), options.isDoubleBattle(), true);
    }
    
    public static void appendMainDamagesSummary(StringBuilder sb, Pokemon p1, Pokemon p2, StatModifier mod1, StatModifier mod2, 
    		boolean isBattleTower, boolean isDoubleBattle) {
    	damagesSummaryCore(sb, p1, p2, mod1, mod2, isBattleTower, isDoubleBattle, false);
    }
    
    //TODO: fuzzy, hacky, idk ... but at least the logic stays within a single method
    private static void damagesSummaryCore(StringBuilder sb, Pokemon p1, Pokemon p2, StatModifier mod1, StatModifier mod2, 
    		boolean isBattleTower, boolean isDoubleBattle, boolean isExtraDamageHelp) {

        for (Move move : p1.getMoveset()) {
        	switch (move.getEffect()) {
        	case FURY_CUTTER:
        		for (int _extra_multiplier : new Integer[] {1, 2, 3, 4, 5}) { //TODO: hardcoded
        			Damages damages = new Damages(move, p1, p2, mod1, mod2, _extra_multiplier, isBattleTower, isDoubleBattle);
        			if (isExtraDamageHelp)
                    	damages.appendDetailledPercentDamages(sb);
                    else
                    	damages.appendAllMoveInfo(sb);
                }
        		break;
        		
        	case ROLLOUT:
        		for (int _extra_multiplier : new Integer[] {1, 2, 3, 4, 5, 6}) { //TODO: hardcoded
        			Damages damages = new Damages(move, p1, p2, mod1, mod2, _extra_multiplier, isBattleTower, isDoubleBattle);
        			if (isExtraDamageHelp)
                    	damages.appendDetailledPercentDamages(sb);
                    else
                    	damages.appendAllMoveInfo(sb);
                }
        		break;
        		
        	case MAGNITUDE:
        		int oldPower = move.getPower();
                for (int power : new Integer[] {10, 30, 50, 70, 90, 110, 150}) { //TODO: hardcoded
                    move.setPower(power);
                    Damages damages = new Damages(move, p1, p2, mod1, mod2, 1, isBattleTower, isDoubleBattle);
                    if (isExtraDamageHelp)
                    	damages.appendDetailledPercentDamages(sb);
                    else
                    	damages.appendAllMoveInfo(sb);
                }
                move.setPower(oldPower);
                break;
                
        	case FLAIL:
        		oldPower = move.getPower();
            	for(int power : new Integer[]{20, 40, 80, 100, 150, 200}) { //TODO: hardcoded
            		move.setPower(power);
            		Damages damages = new Damages(move, p1, p2, mod1, mod2, 1, isBattleTower, isDoubleBattle);
                    if (isExtraDamageHelp)
                    	damages.appendDetailledPercentDamages(sb);
                    else
                    	damages.appendAllMoveInfo(sb);
            	}
                move.setPower(oldPower);
                break;
                
        	default:
        		Damages damages = new Damages(move, p1, p2, mod1, mod2, 1, isBattleTower, isDoubleBattle);
                if (isExtraDamageHelp)
                	damages.appendDetailledPercentDamages(sb);
                else
                	damages.appendAllMoveInfo(sb);
            	break;
            	
        	} // end switch
        } // end for
        	
    }
 
}