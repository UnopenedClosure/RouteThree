package routethree;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.logging.Level;

import org.ini4j.jdk14.edu.emory.mathcs.backport.java.util.Arrays;

public class RouteParser {
    public static int lineNum = 0;

    public static List<GameAction> parseFile(String fileName) {
        lineNum = 0;
        ArrayList<GameAction> actions = new ArrayList<GameAction>();

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(
                    fileName)));
            while (in.ready()) {
                lineNum++;
                String wholeLine = in.readLine();
                String line = wholeLine.indexOf("//") == -1 ? wholeLine : wholeLine.substring(0, wholeLine.indexOf("//"));
                //String[] lines = wholeLine.split("//"); // remove comments //TODO use substring and indexOf instead of split
                //String line = lines[0];
                GameAction a = null;
                try {
                    a = parseLine(line);
                } catch (Exception e) {
                    Main.appendln("Error in line " + lineNum);
                    in.close();
                    return null; //TODO : really hacky
                }
                if (a != null)
                    actions.add(a);

            }
            in.close();
        } catch (FileNotFoundException e) {
            Main.appendln("Could not find Route file: `" + fileName + "`");
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return actions;
    }

    // assumes no comments
    private static GameAction parseLine(String line) throws Exception {
        String[] tokens = line.split(" ");
        int n = tokens.length;
        if (n == 0)
            return null;

        String firstToken = tokens[0];
        // L(num), to indicate pokemon
        if (firstToken.matches("[Ll][0-9]+")) {
        	//for(String tok : tokens)
        	//	System.out.println("'"+tok+"'"); //TODO : get rid
        	
            if(n < 3) { // TODO : wrong check since you're nearly always adding -v argument
                Main.appendln("ERROR ON LINE " + lineNum + ": wrong wild pokemon (requires at least level, species and nature).");
                throw new Exception();
            }
            int startFlag = 3; // TODO : hardcoded
            int lvl = -1;
            try {
            	lvl = Integer.parseInt(firstToken.substring(1));
            } catch(Exception exc) {
            	Main.appendln("ERROR ON LINE " + lineNum + ": wrong wild pokemon (first argument is a level).");
            	throw new Exception();
            }
            
            String speciesStr = tokens[1];
            Species species = Species.getSpeciesByName(speciesStr);
            if(species == null) {
            	Main.appendln("ERROR ON LINE " + lineNum + ": wrong wild pokemon (second argument must be a species).");
            	throw new Exception();
            }
        
            Nature nature = null;
            try {
	            String natureStr = tokens[2];
	            nature = Nature.getEnum(natureStr);
            } catch (Exception exc) {
            	Main.appendln("ERROR ON LINE " + lineNum + ": wrong wild pokemon (third argument must be a nature).");
            	throw new Exception();
            }
            
            IVs ivs = null;
            try
            {
            	Integer hp = Integer.parseInt(tokens[3]);
                Integer atk = Integer.parseInt(tokens[4]);
                Integer def = Integer.parseInt(tokens[5]);
                Integer spa = Integer.parseInt(tokens[6]);
                Integer spd = Integer.parseInt(tokens[7]);
                Integer spe = Integer.parseInt(tokens[8]);
                ivs = new IVs(hp, atk, def, spa, spd, spe);
                startFlag = 9; // TODO : hardcoded
            }
            catch(Exception exc)
            {
                ivs = new IVs();
            }
            Pokemon b = new Pokemon(species, lvl, nature, ivs, true);
            
            String[] flagTokens = (String[]) Arrays.copyOfRange(tokens, startFlag, n);
            return addFlagsToBattleable(b, flagTokens);
        }
        // evolve
        else if (firstToken.equalsIgnoreCase("e")
                || firstToken.equalsIgnoreCase("evolve")) {
            if (n < 2) {
                Main.appendln("ERROR ON LINE " + lineNum + ": missing evolve arguments.");
                throw new Exception();
            }
            String species = tokens[1];
            Species s = Species.getSpeciesByName(species);
            return new Evolve(s);
        }
        // learnmove
        else if (firstToken.equalsIgnoreCase("lm")
                || firstToken.equalsIgnoreCase("learnmove")) {
            if (n < 2) {
                Main.appendln("ERROR ON LINE " + lineNum + ": missing learnmove arguments.");
                throw new Exception();
            }
            String move = tokens[1];
            LearnMove l = new LearnMove(move);
            if (l.getMove() == null) {
                Main.appendln("ERROR ON LINE " + lineNum + ": bad move name");
                throw new Exception();
            }
            return l;
        }
        // unlearnmove
        else if (firstToken.equalsIgnoreCase("um")
                || firstToken.equalsIgnoreCase("unlearnmove")) {
            if (n < 2) {
                Main.appendln("ERROR ON LINE " + lineNum + ": missing unlearnmove arguments.");
                throw new Exception();
            }
            String move = tokens[1];
            UnlearnMove l = new UnlearnMove(move);
            if (l.getMove() == null) {
                Main.appendln("ERROR ON LINE " + lineNum + ": bad move name");
                throw new Exception();
            }
            return l;
        }
        
        else if (firstToken.equalsIgnoreCase("addMoney")) {
        	if (n < 2) {
                Main.appendln("ERROR ON LINE " + lineNum + ": money missing");
                throw new Exception();
            }
            try{
            	int money = Integer.parseInt(tokens[1]);
            	return new AddMoney(money);
            } catch (Exception e) {
            	Main.appendln("ERROR ON LINE " + lineNum + ": money needs to be an integer");
            	throw new Exception();
            }
        }
        
        else if (firstToken.equalsIgnoreCase("spendMoney")) {
        	if (n < 2) {
                Main.appendln("ERROR ON LINE " + lineNum + ": money missing");
                throw new Exception();
            }
            try{
            	int money = Integer.parseInt(tokens[1]);
            	return new AddMoney(-money);
            } catch (Exception e) {
            	Main.appendln("ERROR ON LINE " + lineNum + ": money needs to be an integer");
            	throw new Exception();
            }
        }
        
        else if (firstToken.equalsIgnoreCase("buy")) {
        	if (n < 2) {
                Main.appendln("ERROR ON LINE " + lineNum + ": item missing");
                throw new Exception();
            }
        	try {
        		String itemName = null;
        		Item item = null;
        		int quantity = 1;
        		if (n == 2) { // no quantity
            		itemName = tokens[1];
        		}
        		if (n >= 3) { // quantity item
        			quantity = Integer.parseInt(tokens[1]);
            		itemName = tokens[2];
        			if (quantity < 1)
        				throw new NumberFormatException();
        		}
        		item = Item.getItemByName(itemName);
        		
        		int cost = item.getBuyPrice()*quantity;
        		return new AddMoney(-cost);
        	} catch(NumberFormatException nfe) {
        		Main.appendln("ERROR ON LINE " + lineNum + ": quantity must be > 1");
        		throw new Exception();
        	} catch (Exception e) {
        		Main.appendln("ERROR ON LINE " + lineNum + ": unknown item name");
        		throw new Exception();
        	}
        }
        
        else if (firstToken.equalsIgnoreCase("sell")) {
        	if (n < 2) {
                Main.appendln("ERROR ON LINE " + lineNum + ": item missing");
                throw new Exception();
            }
        	try {
        		String itemName = null;
        		Item item = null;
        		int quantity = 1;
        		if (n == 2) { // no quantity
            		itemName = tokens[1];
        		}
        		if (n >= 3) { // quantity item
        			quantity = Integer.parseInt(tokens[1]);
            		itemName = tokens[2];
        			if (quantity < 1)
        				throw new NumberFormatException();
        		}
        		item = Item.getItemByName(itemName);
        		
        		int earn = item.getSellPrice()*quantity;
        		return new AddMoney(earn);
        	} catch(NumberFormatException nfe) {
        		Main.appendln("ERROR ON LINE " + lineNum + ": quantity must be > 1");
        		throw new Exception();
        	} catch (Exception e) {
        		Main.appendln("ERROR ON LINE " + lineNum + ": unknown item name");
        		throw new Exception();
        	}
        }
        
        // candies, etc
        else if (firstToken.equalsIgnoreCase("rc")
                || firstToken.equalsIgnoreCase("rarecandy")) {
            return GameAction.eatRareCandy;
        } else if (firstToken.equalsIgnoreCase("hpup")) {
            return GameAction.eatHPUp;
        } else if (firstToken.equalsIgnoreCase("iron")) {
            return GameAction.eatIron;
        } else if (firstToken.equalsIgnoreCase("protein")) {
            return GameAction.eatProtein;
        } else if (firstToken.equalsIgnoreCase("calcium")) {
            return GameAction.eatCalcium;
        } else if (firstToken.equalsIgnoreCase("zinc")) {
        	return GameAction.eatZinc;
        } else if (firstToken.equalsIgnoreCase("carbos")) {
            return GameAction.eatCarbos;
        }

        else if(firstToken.equalsIgnoreCase("stonebadge")) {
            return GameAction.getStoneBadge;
        }
        else if(firstToken.equalsIgnoreCase("dynamobadge")) {
            return GameAction.getDynamoBadge;
        }
        else if(firstToken.equalsIgnoreCase("balancebadge")) {
            return GameAction.getBalanceBadge;
        }
        else if(firstToken.equalsIgnoreCase("mindbadge")) {
            return GameAction.getMindBadge;
        }		
        
        /* TODO : handle battletowerflag directly from trainers
        else if(firstToken.equalsIgnoreCase("battletowerflag")) {
            Constants.battleTower = true;
            return GameAction.battleTowerFlag;
        }
        */
        /* TODO : handle happiness and returnpower directly from the damage routine
        else if(firstToken.equalsIgnoreCase("returnpower")) {
            int newPower = Integer.parseInt(tokens[1]);
            return new ChangeReturnPower(newPower);
        }
        */
        
        else if (firstToken.equalsIgnoreCase("equip")) {
        	if (n < 2) {
                Main.appendln("ERROR ON LINE " + lineNum + ": item missing");
                throw new Exception();
            }
        	try {
        		String itemName = tokens[1];
        		Item item = Item.getItemByName(itemName);
        		return new Equip(item);
        	} catch(NumberFormatException nfe) {
        		Main.appendln("ERROR ON LINE " + lineNum + ": quantity must be > 1");
        		throw new Exception();
        	} catch (Exception e) {
        		Main.appendln("ERROR ON LINE " + lineNum + ": unknown item name");
        		throw new Exception();
        	}
        }
        else if (firstToken.equalsIgnoreCase("unequip")) {
            return GameAction.unequip;
        }
        
        /* TODO : maybe keep to allow multiple effects
        else if(firstToken.equalsIgnoreCase("setAmuletCoin")) {
        	return GameAction.setAmuletCoin;
        }        
        else if(firstToken.equalsIgnoreCase("unsetAmuletCoin")) {
        	return GameAction.unsetAmuletCoin;
        }
        */

        else if (firstToken.equalsIgnoreCase("pokerus")) {
            return GameAction.infectPokerus;
        }

        else if (firstToken.equalsIgnoreCase("setBoostedExp")) {
            return GameAction.setBoostedExp;
        }

        else if (firstToken.equalsIgnoreCase("unsetBoostedExp")) {
            return GameAction.unsetBoostedExp;
        }


        // printing commands        
        else if(firstToken.equalsIgnoreCase("money")) {
        	return GameAction.printMoney;
        }
        else if (firstToken.equalsIgnoreCase("stats")) {
            if (n == 1) {
                return GameAction.printAllStatsNoBoost;
            } else if (tokens[1].equalsIgnoreCase("-b")) {
                return GameAction.printAllStats;
            } else {
                return GameAction.printAllStatsNoBoost;
            }
        } else if (firstToken.equalsIgnoreCase("ranges")) {
            if (n == 1) {
                return GameAction.printStatRangesNoBoost;
            } else if (tokens[1].equalsIgnoreCase("-b")) {
                return GameAction.printStatRanges;
            } else {
                return GameAction.printStatRangesNoBoost;
            }
        } else if (!firstToken.trim().isEmpty()) {
            Battleable b = null;
            // attempt to parse as trainer name
            //TODO : battle trainers
            b = Trainer.getTrainerByName(firstToken);
            if (b == null) {
                Main.appendln("ERROR ON LINE "
                        + lineNum
                        + ": that trainer doesn't exist. Check for typos.");
                throw new Exception();
            }
            String[] flagTokens = (String[]) Arrays.copyOfRange(tokens, 1, n);
            return addFlagsToBattleable(b, flagTokens);
        }
        return null;
    }

    enum NextFlag {
        ANY_FLAG, 
        XITEMS, YITEMS, 
        XATK, XDEF, XSPA, XSPD, XSPE, XACC, XEVA,
        YATK, YDEF, YSPA, YSPD, YSPE, YACC, YEVA,
        XATKS, XDEFS, XSPAS, XSPDS, XSPES, XACCS, XEVAS,
        YATKS, YDEFS, YSPAS, YSPDS, YSPES, YACCS, YEVAS,
        VERBOSE, SXP, SXPS, 
        ORDER,
        XSTATUS, XSTATUS2, YSTATUS, YSTATUS2,
        WEATHER, WEATHERS
    }

    private static GameAction addFlagsToBattleable(Battleable b,
            String[] flagTokens) throws Exception {
        NextFlag nf = NextFlag.ANY_FLAG;
        BattleOptions options = new BattleOptions();

        for (String s : flagTokens) {
            // we're looking for a flag
            if (nf == NextFlag.ANY_FLAG) {
                // set this pokemon to wild
                if (s.equalsIgnoreCase("-w") || s.equalsIgnoreCase("-wild")) {
                    if (b instanceof Trainer) {
                        Main.appendln("ERROR ON LINE " + lineNum +": can't use -w or -wild on trainers.");
                        throw new Exception();
                        // can't use -wild or -trainer flag on trainers
                    }
                    ((Pokemon) b).setWild(true);
                    nf = NextFlag.ANY_FLAG;
                    continue;
                }
                // set this pokemon to trainer
                else if (s.equalsIgnoreCase("-t")
                        || s.equalsIgnoreCase("-trainer")) {
                    if (b instanceof Trainer) {
                        Main.appendln("ERROR ON LINE " + lineNum +": can't use -t or -trainer on trainers.");
                        throw new Exception();
                        // can't use -wild or -trainer flag on trainers
                    }
                    ((Pokemon) b).setWild(false);
                    nf = NextFlag.ANY_FLAG;
                    continue;
                }
                
                //set this battle to double
                else if (s.equalsIgnoreCase("-double") 
                		|| s.equalsIgnoreCase("-doubleBattle")) {
                	options.setDoubleBattle(true);
                	nf = NextFlag.ANY_FLAG;
                	continue;
                }
              //set this battle to single
                else if (s.equalsIgnoreCase("-nodouble") 
                		|| s.equalsIgnoreCase("-nodoubleBattle")) {
                	options.setDoubleBattle(true);
                	nf = NextFlag.ANY_FLAG;
                	continue;
                }
                
                
                // xitems (sm1)
                else if (s.equalsIgnoreCase("-x")
                        || s.equalsIgnoreCase("-xitems")) {
                    nf = NextFlag.XITEMS;
                    continue;
                }
                // yitems (sm2)
                else if (s.equalsIgnoreCase("-y")
                        || s.equalsIgnoreCase("-yitems")) {
                    nf = NextFlag.YITEMS;
                    continue;
                }
                // all the xitem (and yitem)
                else if (s.equalsIgnoreCase("-xatk")) {
                    nf = NextFlag.XATK;
                    continue;
                } else if (s.equalsIgnoreCase("-xdef")) {
                    nf = NextFlag.XDEF;
                    continue;
                } else if (s.equalsIgnoreCase("-xspa")) {
                    nf = NextFlag.XSPA;
                    continue;
                } else if (s.equalsIgnoreCase("-xspd")) {
                    nf = NextFlag.XSPD;
                    continue;
                } else if (s.equalsIgnoreCase("-xspe")) {
                    nf = NextFlag.XSPE;
                    continue;
                } else if (s.equalsIgnoreCase("-xacc")) {
                    nf = NextFlag.XACC;
                    continue;
                } else if (s.equalsIgnoreCase("-xeva")) {
                    nf = NextFlag.XEVA;
                    continue;
                } 
                
                else if (s.equalsIgnoreCase("-yatk")) {
                    nf = NextFlag.YATK;
                    continue;
                } else if (s.equalsIgnoreCase("-ydef")) {
                    nf = NextFlag.YDEF;
                    continue;
                } else if (s.equalsIgnoreCase("-yspa")) {
                    nf = NextFlag.YSPA;
                    continue;
                } else if (s.equalsIgnoreCase("-yspd")) {
                    nf = NextFlag.YSPD;
                    continue;
                } else if (s.equalsIgnoreCase("-yspe")) {
                    nf = NextFlag.YSPE;
                    continue;
                } else if (s.equalsIgnoreCase("-yacc")) {
                    nf = NextFlag.YACC;
                    continue;
                } else if (s.equalsIgnoreCase("-yeva")) {
                    nf = NextFlag.YEVA;
                    continue;
                } 
                
                
                // verbose
                else if (s.equalsIgnoreCase("-v")
                        || s.equalsIgnoreCase("-verbose")) {
                    nf = NextFlag.VERBOSE;
                    continue;
                }
                // split exp
                else if (s.equalsIgnoreCase("-sxp")) {
                    nf = NextFlag.SXP;
                    continue;
                }
                else if (s.equalsIgnoreCase("-sxps")) {
                    nf = NextFlag.SXPS;
                    continue;
                }
                
                // xitems and ytiems
                else if (s.equalsIgnoreCase("-xatks")) {
                    nf = NextFlag.XATKS;
                    continue;
                } else if (s.equalsIgnoreCase("-xdefs")) {
                    nf = NextFlag.XDEFS;
                    continue;
                } else if (s.equalsIgnoreCase("-xspas")) {
                    nf = NextFlag.XSPAS;
                    continue;
                } else if (s.equalsIgnoreCase("-xspds")) {
                    nf = NextFlag.XSPDS;
                    continue;
                } else if (s.equalsIgnoreCase("-xspes")) {
                    nf = NextFlag.XSPES;
                    continue;
                } else if (s.equalsIgnoreCase("-xaccs")) {
                    nf = NextFlag.XACCS;
                    continue;
                } else if (s.equalsIgnoreCase("-xevas")) {
                    nf = NextFlag.XEVAS;
                    continue;
                } 
                
                else if (s.equalsIgnoreCase("-yatks")) {
                    nf = NextFlag.YATKS;
                    continue;
                } else if (s.equalsIgnoreCase("-ydefs")) {
                    nf = NextFlag.YDEFS;
                    continue;
                } else if (s.equalsIgnoreCase("-yspas")) {
                    nf = NextFlag.YSPAS;
                    continue;
                } else if (s.equalsIgnoreCase("-yspds")) {
                    nf = NextFlag.YSPDS;
                    continue;
                } else if (s.equalsIgnoreCase("-yspes")) {
                    nf = NextFlag.YSPES;
                    continue;
                } else if (s.equalsIgnoreCase("-yaccs")) {
                    nf = NextFlag.YACCS;
                    continue;
                } else if (s.equalsIgnoreCase("-yevas")) {
                    nf = NextFlag.YEVAS;
                    continue;
                } 

                else if (s.equalsIgnoreCase("-order")) {
                    nf = NextFlag.ORDER;
                    continue;
                } else if (s.equalsIgnoreCase("-weather")) {
                	nf = NextFlag.WEATHER;
                    continue;
                } else if (s.equalsIgnoreCase("-weathers")) {
                	nf = NextFlag.WEATHERS;
                    continue;
                }
                
                // statuses
                else if (s.equalsIgnoreCase("-xstatus")) {
                	nf = NextFlag.XSTATUS;
        			continue;
                } else if (s.equalsIgnoreCase("-xstatus2")) {
                	nf = NextFlag.XSTATUS2;
        			continue;
                } else if (s.equalsIgnoreCase("-ystatus")) {
                	nf = NextFlag.YSTATUS;
        			continue;
                } else if (s.equalsIgnoreCase("-ystatus2")) {
                	nf = NextFlag.YSTATUS2;
        			continue;
                }
                
                else if (s.equalsIgnoreCase("-xtorrent") || s.equalsIgnoreCase("-xblaze") || s.equalsIgnoreCase("-xovergrow") 
                		|| s.equalsIgnoreCase("-xswarm")) {
                	options.getMod1().setOneThirdHPOrLess(true);
            		continue;
                }
                else if (s.equalsIgnoreCase("-ytorrent") || s.equalsIgnoreCase("-yblaze") || s.equalsIgnoreCase("-yovergrow") 
                		|| s.equalsIgnoreCase("-yswarm")) {
                	options.getMod2().setOneThirdHPOrLess(true);
                	continue;
                }
                
                // print stat ranges if level
                else if (s.equalsIgnoreCase("-lvranges")) {
                    options.setPrintStatRangesOnLvl(true);
                    nf = NextFlag.ANY_FLAG;
                    continue;
                } else if (s.equalsIgnoreCase("-lvstats")) {
                    options.setPrintStatsOnLvl(true);
                    nf = NextFlag.ANY_FLAG;
                    continue;
                }
                
            }
            
            if (nf == NextFlag.ANY_FLAG) {
            	Main.appendln(String.format("ERROR ON LINE %d: unknown battle modifier '%s'", lineNum, s));
                throw new Exception();
            }
            
            // -x flag
            else if (nf == NextFlag.XITEMS) {
                String[] nums = s.split("/");
                if (nums.length < 5 && nums.length > 7) {
                    Main.appendln("ERROR ON LINE " + lineNum +": -x or -xitems needs 5 arguments (6 if X Accuracy, 7 if evasion)");
                    throw new Exception();
                }
                options.getMod1().incrementAtkStage(Integer.parseInt(nums[0]));
                options.getMod1().incrementDefStage(Integer.parseInt(nums[1]));
                options.getMod1().incrementSpaStage(Integer.parseInt(nums[2]));
                options.getMod1().incrementSpdStage(Integer.parseInt(nums[3]));
                options.getMod1().incrementSpeStage(Integer.parseInt(nums[4]));
                if (nums.length <= 6)
                	options.getMod1().incrementAccStage(Integer.parseInt(nums[5]));
                if (nums.length <= 7)
                	options.getMod1().incrementEvaStage(Integer.parseInt(nums[6]));
                
                nf = NextFlag.ANY_FLAG;
                continue;
            }
            // -y flag
            else if (nf == NextFlag.YITEMS) {
                String[] nums = s.split("/");
                if (nums.length < 5 && nums.length > 7) {
                    Main.appendln("ERROR ON LINE " + lineNum +": -y or -yitems needs 5 arguments (6 if X Accuracy, 7 if evasion)");
                    throw new Exception();
                }
                options.getMod2().incrementAtkStage(Integer.parseInt(nums[0]));
                options.getMod2().incrementDefStage(Integer.parseInt(nums[1]));
                options.getMod2().incrementSpaStage(Integer.parseInt(nums[2]));
                options.getMod2().incrementSpdStage(Integer.parseInt(nums[3]));
                options.getMod2().incrementSpeStage(Integer.parseInt(nums[4]));
                if (nums.length <= 6)
                	options.getMod2().incrementAccStage(Integer.parseInt(nums[5]));
                if (nums.length <= 7)
                	options.getMod2().incrementEvaStage(Integer.parseInt(nums[6]));
                nf = NextFlag.ANY_FLAG;
                continue;
            }
            // x<stat>, y<stat>
            else if (nf == NextFlag.XATK) {
                options.getMod1().incrementAtkStage(Integer.parseInt(s));
                nf = NextFlag.ANY_FLAG;
                continue;
            } else if (nf == NextFlag.XDEF) {
                options.getMod1().incrementDefStage(Integer.parseInt(s));
                nf = NextFlag.ANY_FLAG;
                continue;
            } else if (nf == NextFlag.XSPA) {
                options.getMod1().incrementSpaStage(Integer.parseInt(s));
                nf = NextFlag.ANY_FLAG;
                continue;
            } else if (nf == NextFlag.XSPD) {
                options.getMod1().incrementSpdStage(Integer.parseInt(s));
                nf = NextFlag.ANY_FLAG;
                continue;
            } else if (nf == NextFlag.XSPE) {
                options.getMod1().incrementSpeStage(Integer.parseInt(s));
                nf = NextFlag.ANY_FLAG;
                continue;
            } else if (nf == NextFlag.XACC) {
                options.getMod1().incrementAccStage(Integer.parseInt(s));
                nf = NextFlag.ANY_FLAG;
                continue;
            } else if (nf == NextFlag.XEVA) {
                options.getMod1().incrementEvaStage(Integer.parseInt(s));
                nf = NextFlag.ANY_FLAG;
                continue;
            }
            
            else if (nf == NextFlag.YATK) {
                options.getMod2().incrementAtkStage(Integer.parseInt(s));
                nf = NextFlag.ANY_FLAG;
                continue;
            } else if (nf == NextFlag.YDEF) {
                options.getMod2().incrementDefStage(Integer.parseInt(s));
                nf = NextFlag.ANY_FLAG;
                continue;
            } else if (nf == NextFlag.YSPA) {
                options.getMod2().incrementSpaStage(Integer.parseInt(s));
                nf = NextFlag.ANY_FLAG;
                continue;
            } else if (nf == NextFlag.YSPD) {
                options.getMod2().incrementSpdStage(Integer.parseInt(s));
                nf = NextFlag.ANY_FLAG;
                continue;
            } else if (nf == NextFlag.YSPE) {
                options.getMod2().incrementSpeStage(Integer.parseInt(s));
                nf = NextFlag.ANY_FLAG;
                continue;
            } else if (nf == NextFlag.YACC) {
                options.getMod2().incrementAccStage(Integer.parseInt(s));
                nf = NextFlag.ANY_FLAG;
                continue;
            } else if (nf == NextFlag.YEVA) {
                options.getMod2().incrementEvaStage(Integer.parseInt(s));
                nf = NextFlag.ANY_FLAG;
                continue;
            }
            // verbose
            else if (nf == NextFlag.VERBOSE) {
                if (s.matches("[0-9]+")) {
                    options.setVerbose(Integer.parseInt(s));
                } else if (s.equalsIgnoreCase("NONE")) {
                    options.setVerbose(BattleOptions.NONE);
                } else if (s.equalsIgnoreCase("SOME")) {
                    options.setVerbose(BattleOptions.SOME);
                } else if (s.equalsIgnoreCase("ALL")) {
                    options.setVerbose(BattleOptions.ALL);
                } else if(s.equalsIgnoreCase("EVERYTHING")) {
                    options.setVerbose(BattleOptions.EVERYTHING);
                }
                nf = NextFlag.ANY_FLAG;
                continue;
            }
            // sxp, sxps
            else if (nf == NextFlag.SXP) {
                options.setParticipants(Integer.parseInt(s));
                nf = NextFlag.ANY_FLAG;
                continue;
            }
            else if(nf == NextFlag.SXPS) {
                String[] nums = s.split("/");
                ArrayList<Integer> list = new ArrayList<>();
                for(String iStr : nums) {
                    list.add(Integer.parseInt(iStr));
                }
                options.setSxps(list);
                nf = NextFlag.ANY_FLAG;
                continue;
            }
            
            // x<stat>s, y<stat>s | TODO : find a way to refactor
            else if(nf == NextFlag.XATKS) {
                String[] nums = s.split("/");
                ArrayList<Integer> list = new ArrayList<>();
                for(String iStr : nums) {
                    list.add(Integer.parseInt(iStr));
                }
                options.setXatks(list);
                nf = NextFlag.ANY_FLAG;
                continue;
            } else if(nf == NextFlag.XDEFS) {
                String[] nums = s.split("/");
                ArrayList<Integer> list = new ArrayList<>();
                for(String iStr : nums) {
                    list.add(Integer.parseInt(iStr));
                }
                options.setXdefs(list);
                nf = NextFlag.ANY_FLAG;
                continue;
            } else if(nf == NextFlag.XSPAS) {
                String[] nums = s.split("/");
                ArrayList<Integer> list = new ArrayList<>();
                for(String iStr : nums) {
                    list.add(Integer.parseInt(iStr));
                }
                options.setXspas(list);
                nf = NextFlag.ANY_FLAG;
                continue;
            } else if(nf == NextFlag.XSPDS) {
                String[] nums = s.split("/");
                ArrayList<Integer> list = new ArrayList<>();
                for(String iStr : nums) {
                    list.add(Integer.parseInt(iStr));
                }
                options.setXspds(list);
                nf = NextFlag.ANY_FLAG;
                continue;
            } else if(nf == NextFlag.XSPES) {
                String[] nums = s.split("/");
                ArrayList<Integer> list = new ArrayList<>();
                for(String iStr : nums) {
                    list.add(Integer.parseInt(iStr));
                }
                options.setXspes(list);
                nf = NextFlag.ANY_FLAG;
                continue;
            } else if(nf == NextFlag.XACCS) {
                String[] nums = s.split("/");
                ArrayList<Integer> list = new ArrayList<>();
                for(String iStr : nums) {
                    list.add(Integer.parseInt(iStr));
                }
                options.setXaccs(list);
                nf = NextFlag.ANY_FLAG;
                continue;
            } else if(nf == NextFlag.XEVAS) {
                String[] nums = s.split("/");
                ArrayList<Integer> list = new ArrayList<>();
                for(String iStr : nums) {
                    list.add(Integer.parseInt(iStr));
                }
                options.setXevas(list);
                nf = NextFlag.ANY_FLAG;
                continue;
            } 
            
            else if(nf == NextFlag.YATKS) {
                String[] nums = s.split("/");
                ArrayList<Integer> list = new ArrayList<>();
                for(String iStr : nums) {
                    list.add(Integer.parseInt(iStr));
                }
                options.setYatks(list);
                nf = NextFlag.ANY_FLAG;
                continue;
            } else if(nf == NextFlag.YDEFS) {
                String[] nums = s.split("/");
                ArrayList<Integer> list = new ArrayList<>();
                for(String iStr : nums) {
                    list.add(Integer.parseInt(iStr));
                }
                options.setYdefs(list);
                nf = NextFlag.ANY_FLAG;
                continue;
            } else if(nf == NextFlag.YSPAS) {
                String[] nums = s.split("/");
                ArrayList<Integer> list = new ArrayList<>();
                for(String iStr : nums) {
                    list.add(Integer.parseInt(iStr));
                }
                options.setYspas(list);
                nf = NextFlag.ANY_FLAG;
                continue;
            } else if(nf == NextFlag.YSPDS) {
                String[] nums = s.split("/");
                ArrayList<Integer> list = new ArrayList<>();
                for(String iStr : nums) {
                    list.add(Integer.parseInt(iStr));
                }
                options.setYspds(list);
                nf = NextFlag.ANY_FLAG;
                continue;
            } else if(nf == NextFlag.YSPES) {
                String[] nums = s.split("/");
                ArrayList<Integer> list = new ArrayList<>();
                for(String iStr : nums) {
                    list.add(Integer.parseInt(iStr));
                }
                options.setYspes(list);
                nf = NextFlag.ANY_FLAG;
                continue;
            } else if(nf == NextFlag.YACCS) {
                String[] nums = s.split("/");
                ArrayList<Integer> list = new ArrayList<>();
                for(String iStr : nums) {
                    list.add(Integer.parseInt(iStr));
                }
                options.setYaccs(list);
                nf = NextFlag.ANY_FLAG;
                continue;
            } else if(nf == NextFlag.YEVAS) {
                String[] nums = s.split("/");
                ArrayList<Integer> list = new ArrayList<>();
                for(String iStr : nums) {
                    list.add(Integer.parseInt(iStr));
                }
                options.setYevas(list);
                nf = NextFlag.ANY_FLAG;
                continue;
            } 
            
            // Order
            else if(nf == NextFlag.ORDER) {
                String[] nums = s.split("/");
            	ArrayList<Integer> list = new ArrayList<>();
                for(String iStr : nums) {
                    list.add(Integer.parseInt(iStr));
                }
                options.setOrder(list);
                nf = NextFlag.ANY_FLAG;
                continue;
            }

            // Status
            else if (nf == NextFlag.XSTATUS) {
            	Status status = Status.valueOf(s);
            	ArrayList<Status> statuses = new ArrayList<Status>();
            	for(int i = 0 ; i < 6 ; i++)
            		statuses.add(status);
            	options.setXstatuses1(statuses);
                nf = NextFlag.ANY_FLAG;
                continue;
            } else if (nf == NextFlag.YSTATUS) { // TODO : with above, refactor ?
            	Status status = Status.valueOf(s);
            	ArrayList<Status> statuses = new ArrayList<Status>();
            	for(int i = 0 ; i < 6 ; i++)
            		statuses.add(status);
            	options.setYstatuses1(statuses);
                nf = NextFlag.ANY_FLAG;
                continue;
            }
            
            else if (nf == NextFlag.XSTATUS2) { //TODO: differents status2_3 among different turns ? + factor out code with ystatus2
            	String[] names = s.split("/");
            	EnumMap<Status, Boolean> statuses2_3 = Status.noStatus2_3();
            	for(String name : names) {
            		Status status = Status.valueOf(name);
            		statuses2_3.put(status, true);
            	}
            	ArrayList<EnumMap<Status, Boolean>> statuses = new ArrayList<>();
            	for(int i = 0 ; i < 6 ; i++)
            		statuses.add(statuses2_3);
            	options.setXstatuses2_3(statuses);
                nf = NextFlag.ANY_FLAG;
                continue;
            } else if (nf == NextFlag.YSTATUS2) { //TODO: differents status2_3 among different turns ? + factor out code with ystatus2
            	String[] names = s.split("/");
            	EnumMap<Status, Boolean> statuses2_3 = Status.noStatus2_3();
            	for(String name : names) {
            		Status status = Status.valueOf(name);
            		statuses2_3.put(status, true);
            	}
            	ArrayList<EnumMap<Status, Boolean>> statuses = new ArrayList<>();
            	for(int i = 0 ; i < 6 ; i++)
            		statuses.add(statuses2_3);
            	options.setYstatuses2_3(statuses);
                nf = NextFlag.ANY_FLAG;
                continue;
            }
            
            // Weather, weathers
            else if (nf == NextFlag.WEATHER) {
            	Weather w = Weather.getWeatherFromName(s);
            	if(w == null) {
            		Main.appendln("ERROR ON LINE " + lineNum + ": received `"+s+"`. -weather must be RAIN, SUN, SANDSTORM, HAIL or NONE/0.");
            		throw new Exception();
            	}
            	options.setWeathers(w);
            	nf = NextFlag.ANY_FLAG;
            	continue;
            } else if (nf == NextFlag.WEATHERS) {
            	String[] weatherStrArr = s.split("/");
            	Weather w = Weather.NONE;
            	ArrayList<Weather> weathers = Weather.getAllNoneWeathers();
            	for (int i = 0; i < weatherStrArr.length; i++) {
	            	w = Weather.getWeatherFromName(weatherStrArr[i]);
	            	if(w == null) {
	            		Main.appendln("ERROR ON LINE " + lineNum + ": received `"+s+"`. -weathers must be RAIN, SUN, SANDSTORM, HAIL or NONE/0 (or a list of these).");
	            		throw new Exception();
	            	}
	            	weathers.set(i, w);
            	}
            	options.setWeathers(weathers);
            	
            	nf = NextFlag.ANY_FLAG;
            	continue;
            }
        }
        if (nf != NextFlag.ANY_FLAG) {
            // TODO: error check
        }
        return new Battle(b, options);
    }
}
