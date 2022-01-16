import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

//represents a battle, with planned statmods
public class Battle extends GameAction {
    private Battleable opponent;
    private BattleOptions options;

    public Battle(Battleable b) {
        opponent = b;
        options = new BattleOptions();
    }

    public Battle(Battleable b, BattleOptions options) {
        opponent = b;
        this.options = options;
    }

    public BattleOptions getOptions() {
        return options;
    }

    public StatModifier getMod1() {
        return options.getMod1();
    }

    public StatModifier getMod2() {
        return options.getMod2();
    }

    public int getVerbose() {
        return options.getVerbose();
    }

    public static Battle makeBattle(Trainer trainer) {
        return new Battle(trainer);
    }

    public static Battle makeBattle(Trainer trainer, BattleOptions options) {
        return new Battle(trainer, options);
    }

    public static Battle makeBattle(Pokemon p) {
        return new Battle(p);
    }

    public static Battle makeBattle(Pokemon p, BattleOptions options) {
        return new Battle(p, options);
    }

    @Override
    public void performAction(Pokemon p) {
        doBattle(p);

        // check for special gym leader badges
        if (!(opponent instanceof Trainer))
        	return; 
        
        List<Stat> badgeBoosts = ((Trainer) opponent).getBadgeBoosts();
        if(badgeBoosts == null)
        	return;
        
        for (Stat stat : ((Trainer) opponent).getBadgeBoosts()) {
			switch(stat) {
			case ATK: p.setAtkBadge(true); break;
			case DEF: p.setDefBadge(true); break;
			case SPA: p.setSpaBadge(true); break;
			case SPD: p.setSpdBadge(true); break;
			case SPE: p.setSpeBadge(true); break;
			default: break;
			}
        }
    }

    private void doBattle(Pokemon p) {
        int lastLvl = p.getLevel();

        // TODO: automatically determine whether or not to print
        if (opponent instanceof Pokemon) {
            if(getVerbose() == BattleOptions.ALL || getVerbose() == BattleOptions.EVERYTHING)
                printBattle(p, (Pokemon) opponent);
            else if (getVerbose() == BattleOptions.SOME)
                printShortBattle(p, (Pokemon) opponent);

            opponent.battle(p, options);
            if (p.getLevel() > lastLvl) {
                lastLvl = p.getLevel();
                if (options.isPrintStatRangesOnLvl()) {
                    Main.appendln(p.statRanges(false));
                }
                if (options.isPrintStatsOnLvl()) {
                    Main.appendln(p.statRanges(true));
                }
            }
            if(getVerbose() == BattleOptions.EVERYTHING) {
                Main.appendln(String.format("LVL: %d EXP NEEDED: %d/%d", p.getLevel(),
                    p.expToNextLevel(), p.expForLevel()));
            }
            
            /* TODO: Fix this (non working) hack, because battleTower will be handled differently
            if(!((Pokemon) opponent).isTowerPoke()) {
                opponent.battle(p, options);
                if (p.getLevel() > lastLvl) {
                    lastLvl = p.getLevel();
                    if (options.isPrintSRsOnLvl()) {
                        Main.appendln(p.statRanges(false));
                    }
                    if (options.isPrintSRsBoostOnLvl()) {
                        Main.appendln(p.statRanges(true));
                    }
                }
                if(getVerbose() == BattleOptions.EVERYTHING) {
                    Main.appendln(String.format("LVL: %d EXP NEEDED: %d/%d", p.getLevel(),
                        p.expToNextLevel(), p.expForLevel()));
                }
            }
            */
        } else { // is a Trainer
            Trainer t = (Trainer) opponent;
            Settings.money += t.getReward(p);
            
            /*
            if(t.getTrainerName().equalsIgnoreCase("TATE&LIZA")) {
            	System.out.println("We're in !"); // TODO : get rid
            	System.out.println(t.getDoubleBattle());
            }
            */
            
            int sxpIdx = 0;
            int sxp = 1;
            ArrayList<Integer> sxps = options.getSxps();
            
            ArrayList<Integer> xatks = options.getXatks();
            ArrayList<Integer> xdefs = options.getXdefs();
            ArrayList<Integer> xspas = options.getXspas();
            ArrayList<Integer> xspds = options.getXdefs();
            ArrayList<Integer> xspes = options.getXspes();
            ArrayList<Integer> xaccs = options.getXaccs();
            ArrayList<Integer> xevas = options.getXevas();
            ArrayList<Status> xstatuses1 = options.getXstatuses1();
            ArrayList<EnumMap<Status, Boolean>> xstatuses2_3 = options.getXstatuses2_3();
            
            ArrayList<Integer> yatks = options.getYatks();
            ArrayList<Integer> ydefs = options.getYdefs();
            ArrayList<Integer> yspas = options.getYspas();
            ArrayList<Integer> yspds = options.getYspds();
            ArrayList<Integer> yspes = options.getYspes();
            ArrayList<Integer> yaccs = options.getYaccs();
            ArrayList<Integer> yevas = options.getYevas();
            ArrayList<Status> ystatuses1 = options.getYstatuses1();
            ArrayList<EnumMap<Status, Boolean>> ystatuses2_3 = options.getYstatuses2_3();
            
            ArrayList<Integer> order = options.getOrder();
            ArrayList<Weather> weathers = options.getWeathers();
            
            options.setDoubleBattle(t.getDoubleBattle() != 0);
            //TODO : battleTower not implemented
            
            Iterable<Pokemon> trainerPokes = null;
            if(order == null) {
                trainerPokes = t;
            } else {
                ArrayList<Pokemon> origPokes = new ArrayList<>();
                for(Pokemon poke : t)
                	origPokes.add(poke);
                //t.forEach(origPokes::add); TODO: older version, why ?
                
                ArrayList<Pokemon> modPokes = new ArrayList<>();
                for(int i : order) {
                    modPokes.add(origPokes.get(i-1));
                }
                trainerPokes = modPokes;
                t.setParty(modPokes);
            }
            if(getVerbose() == BattleOptions.ALL || getVerbose() == BattleOptions.SOME || getVerbose() == BattleOptions.EVERYTHING) {
                Main.appendln(Constants.endl + t.toString(p));
            }
            for (Pokemon opps : trainerPokes) {
                if(sxps != null) {
                    sxp = sxps.get(sxpIdx);
                    if(sxp != 0) {
                        options.setParticipants(sxp);
                    }
                }
                
                if(xatks != null) {
                    int xatk = xatks.get(sxpIdx);
                    StatModifier mod1 = options.getMod1();
                    mod1.setAtkStage(xatk);
                    options.setMod1(mod1);
                }
                if(xdefs != null) {
                    int xdef = xdefs.get(sxpIdx);
                    StatModifier mod1 = options.getMod1();
                    mod1.setDefStage(xdef);
                    options.setMod1(mod1);
                }
                if(xspas != null) {
                    int xspa = xspas.get(sxpIdx);
                    StatModifier mod1 = options.getMod1();
                    mod1.setSpaStage(xspa);
                    options.setMod1(mod1);
                }
                if(xspds != null) {
                    int xspd = xspds.get(sxpIdx);
                    StatModifier mod1 = options.getMod1();
                    mod1.setSpdStage(xspd);
                    options.setMod1(mod1);
                }
                if(xspes != null) {
                    int xspe = xspes.get(sxpIdx);
                    StatModifier mod1 = options.getMod1();
                    mod1.setSpeStage(xspe);
                    options.setMod1(mod1);
                }
                if(xaccs != null) {
                    int xacc = xaccs.get(sxpIdx);
                    StatModifier mod1 = options.getMod1();
                    mod1.setAccStage(xacc);
                    options.setMod1(mod1);
                }
                if(xevas != null) {
                    int xeva = xevas.get(sxpIdx);
                    StatModifier mod1 = options.getMod1();
                    mod1.setEvaStage(xeva);
                    options.setMod1(mod1);
                }
                if(xstatuses1 != null) {
                	Status xstatus1 = xstatuses1.get(sxpIdx);
                	StatModifier mod1 = options.getMod1();
                    mod1.setStatus1(xstatus1);
                    options.setMod1(mod1);
                }
                if(xstatuses2_3 != null) {
                	EnumMap<Status, Boolean> xstatus2_3 = xstatuses2_3.get(sxpIdx);
                	StatModifier mod1 = options.getMod1();
                    mod1.setStatus2_3(xstatus2_3);
                    options.setMod1(mod1);
                }
                

                if(yatks != null) {
                    int yatk = yatks.get(sxpIdx);
                    StatModifier mod2 = options.getMod2();
                    mod2.setAtkStage(yatk);
                    options.setMod2(mod2);
                }
                if(ydefs != null) {
                    int ydef = ydefs.get(sxpIdx);
                    StatModifier mod2 = options.getMod2();
                    mod2.setDefStage(ydef);
                    options.setMod2(mod2);
                }
                if(yspas != null) {
                    int yspa = yspas.get(sxpIdx);
                    StatModifier mod2 = options.getMod2();
                    mod2.setSpaStage(yspa);
                    options.setMod2(mod2);
                }
                if(yspds != null) {
                    int yspd = yspds.get(sxpIdx);
                    StatModifier mod2 = options.getMod2();
                    mod2.setSpdStage(yspd);
                    options.setMod2(mod2);
                }
                if(yspes != null) {
                    int yspe = yspes.get(sxpIdx);
                    StatModifier mod2 = options.getMod2();
                    mod2.setSpeStage(yspe);
                    options.setMod2(mod2);
                }
                if(yaccs != null) {
                    int yacc = yaccs.get(sxpIdx);
                    StatModifier mod2 = options.getMod2();
                    mod2.setAccStage(yacc);
                    options.setMod2(mod2);
                }
                if(yevas != null) {
                    int yeva = yevas.get(sxpIdx);
                    StatModifier mod2 = options.getMod2();
                    mod2.setEvaStage(yeva);
                    options.setMod2(mod2);
                }
                if(ystatuses1 != null) {
                	Status ystatus1 = ystatuses1.get(sxpIdx);
                	StatModifier mod2 = options.getMod2();
                    mod2.setStatus1(ystatus1);
                    options.setMod2(mod2);
                }
                if(ystatuses2_3 != null) {
                	EnumMap<Status, Boolean> ystatus2_3 = ystatuses2_3.get(sxpIdx);
                	StatModifier mod2 = options.getMod2();
                    mod2.setStatus2_3(ystatus2_3);
                    options.setMod2(mod2);
                }
                
                if(weathers != null) {
                    Weather weather = weathers.get(sxpIdx);
                    StatModifier mod1 = options.getMod1();
                    StatModifier mod2 = options.getMod2(); 
                    mod1.setWeather(weather);
                    mod2.setWeather(weather);
                    options.setMod1(mod1);
                    options.setMod2(mod2);
                }

                if(sxp != 0) {
                    if(getVerbose() == BattleOptions.ALL || getVerbose() == BattleOptions.EVERYTHING)
                        printBattle(p, (Pokemon) opps);
                    else if (getVerbose() == BattleOptions.SOME)
                        printShortBattle(p, (Pokemon) opps);
                    if (getVerbose() != BattleOptions.NONE) {
                    	int meMinSpeed = options.getMod1().modSpeWithIVandNature(p, IVs.MIN, p.getNature());
                    	int meMaxSpeed = options.getMod1().modSpeWithIVandNature(p, IVs.MAX, p.getNature());
                    	int oppSpeed = options.getMod2().modSpe(opps);
                    	
                    	if(meMaxSpeed < oppSpeed) {
                    		Main.appendln("(always slower)");
                            Main.appendln("");
                    	} else if (meMinSpeed > oppSpeed) {
                    		Main.appendln("(always faster)");
                            Main.appendln("");
                    	}
                    	
                    	else if (meMinSpeed <= oppSpeed && meMaxSpeed >= oppSpeed) {
                    		int realSpeed = options.getMod1().modSpe(p);
                    		if(realSpeed > oppSpeed)
                    			Main.appendln("(currently faster)");
                    		else if (realSpeed == oppSpeed)
                    			Main.appendln("(currently speedtied)");
                    		else
                    			Main.appendln("(currently slower)");
                    		
                    		
                            int tieIV = IVs.RANGE, outspeedIV = IVs.RANGE;
                            int oppSpd = options.getMod2().modSpe(opps);
                            for (int sIV = IVs.MIN; sIV < IVs.RANGE; sIV++) {
                                int mySpd = options.getMod1().modSpeWithIVandNature(p, sIV, p.getNature());
                                if (mySpd == oppSpd && sIV < tieIV) {
                                    tieIV = sIV;
                                }
                                if (mySpd > oppSpd && sIV < outspeedIV) {
                                    outspeedIV = sIV;
                                    break;
                                }
                            }
                            Main.append("(Speed IV required");
                            if (tieIV != IVs.RANGE && outspeedIV != IVs.RANGE && (tieIV != outspeedIV)) {
                                Main.append(" to outspeed: " + outspeedIV + ", to speedtie: " + tieIV);
                            } else if (outspeedIV != IVs.RANGE) {
                                Main.append(" to outspeed: " + outspeedIV);
                            } else {
                                Main.append(" to speedtie: " + tieIV);
                            }
                            Main.appendln(" with the same nature)");
                            Main.appendln("");
                        }
                    }
                    opps.battle(p, options);
                    // test if you leveled up on this pokemon
                    if (p.getLevel() > lastLvl) {
                        lastLvl = p.getLevel();
                        if (options.isPrintStatRangesOnLvl()) {
                            Main.appendln(p.statRanges(false));
                        }
                        if (options.isPrintStatsOnLvl()) {
                            Main.appendln(p.statsWithoutBoost());
                        }
                    }
                    if(getVerbose() == BattleOptions.EVERYTHING) {
                        Main.appendln(String.format("LVL: %d EXP NEEDED: %d/%d",
                                p.getLevel(), p.expToNextLevel(), p.expForLevel()));
                    }
                }
                sxpIdx++;
            }
        }
        if(getVerbose() == BattleOptions.ALL || getVerbose() == BattleOptions.SOME) {
            Main.appendln(String.format("LVL: %d EXP NEEDED: %d/%d", p.getLevel(),
                    p.expToNextLevel(), p.expForLevel()));
        }
    }

    // does not actually do the battle, just prints summary
    public void printBattle(Pokemon us, Pokemon them) {
        Main.appendln(DamageCalculator.battleSummary(us, them, options));
    }

    // does not actually do the battle, just prints short summary
    public void printShortBattle(Pokemon us, Pokemon them) {
        Main.appendln(DamageCalculator.shortBattleSummary(us, them, options));
    }
}

class Encounter extends Battle {
    Encounter(Species s, int lvl, Nature nature, BattleOptions options) {
        super(new Pokemon(s, lvl, nature), options);
    }

    Encounter(String s, int lvl, Nature nature) {
        this(Species.getSpeciesByName(s), lvl, nature, new BattleOptions());
    }

    Encounter(String s, int lvl, Nature nature, BattleOptions options) {
        this(Species.getSpeciesByName(s), lvl, nature, options);
    }
}

class TrainerPoke extends Battle {
    TrainerPoke(Species s, int lvl, Nature nature, BattleOptions options) {
        super(new Pokemon(s, lvl, nature), options);
    }

    TrainerPoke(String s, int lvl, Nature nature) {
        this(Species.getSpeciesByName(s), lvl, nature, new BattleOptions());
    }

    TrainerPoke(String s, int lvl, Nature nature, BattleOptions options) {
        this(Species.getSpeciesByName(s), lvl, nature, options);
    }
}
