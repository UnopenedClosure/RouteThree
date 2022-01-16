import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//a trainer has a class name and some pokemon, corresponding to some location in memory
public class Trainer implements Battleable, Iterable<Pokemon> {
	private static HashMap<IgnoreCaseString, Trainer> trainersByName;
	
	public static Trainer getTrainerByName(String name) {
        return trainersByName.get(new IgnoreCaseString(name));
    }
	
	public static void initTrainers(Game game) {
		trainersByName = new LinkedHashMap<IgnoreCaseString, Trainer>();
		
		JSONParser jsonParser = new JSONParser();
        BufferedReader in;
        try {
        	System.out.println("/resources/"+game.getTrainersFilename()); // TODO
            in = new BufferedReader(new InputStreamReader(Trainer.class
                    .getResource("/resources/"+game.getTrainersFilename()).openStream())); // TODO : handle custom files ?
            JSONObject trainersDic = (JSONObject) jsonParser.parse(in);
            for(Object trainerEntryObj : trainersDic.entrySet()) {
                Trainer trainer = null;
                
            	@SuppressWarnings("unchecked")
				Map.Entry<Object, Object> trainerEntry = (Map.Entry<Object, Object>) trainerEntryObj;
            	String trainerAlias = (String) trainerEntry.getKey();
            	JSONObject trainerDic = (JSONObject) trainerEntry.getValue();
            	
            	int partyFlags = ((Long) trainerDic.get("partyFlags")).intValue();
            	String trainerClass = (String) trainerDic.get("trainerClass");
            	int baseMoney = ((Long) trainerDic.get("MONEY")).intValue();
            	int encounterMusicGender = ((Long) trainerDic.get("encounterMusic_gender")).intValue();
            	int trainerPic = ((Long) trainerDic.get("trainerPic")).intValue();
            	String trainerName = (String) trainerDic.get("trainerName");
            	
            	ArrayList<Item> items = new ArrayList<>();
            	JSONArray itemsArray = (JSONArray) trainerDic.get("items");
            	for(Object itemObj : itemsArray) {
            		Item item = Item.getItemByName((String) itemObj);
            		items.add(item);
            	}
            	
            	int doubleBattle = ((Long) trainerDic.get("doubleBattle")).intValue();
            	int aiFlags = ((Long) trainerDic.get("aiFlags")).intValue();
            	
            	List<Stat> badgeBoosts = null;
            	if (trainerDic.containsKey("badgeBoosts")) {
            		badgeBoosts = new ArrayList<Stat>();
            		JSONArray badgeBoostsArray = (JSONArray) trainerDic.get("badgeBoosts");
            		for (Object badgeBoostObj : badgeBoostsArray) {
            			Stat stat = Stat.valueOf((String) badgeBoostObj);
            			badgeBoosts.add(stat);
            		}
            	}
            	
            	//int partySize = ((Long) trainerDic.get("partySize")).intValue();
            	
            	JSONObject partyDic = (JSONObject) trainerDic.get("party");
            	String partyType = (String) partyDic.get("PARTY_TYPE");
            	
            	JSONArray partyArray = (JSONArray) partyDic.get("TRAINER_PARTY");
            	
            	// personality value logic
                int nameHash = 0;
                int personalityValue;
            	//
                
            	ArrayList<Pokemon> party = new ArrayList<Pokemon>();
            	for(Object pokemonObj : partyArray){
            		JSONObject pokemonDic = (JSONObject) pokemonObj;
            		int iv = ((Long) pokemonDic.get("iv")).intValue();
            		int level = ((Long) pokemonDic.get("level")).intValue();
            		String speciesString = (String) pokemonDic.get("species");
            		Species species = Species.getSpeciesByName(speciesString);
            		
            		if(species == null)
            			System.out.println("null");
            		
            		String hashName = species.getHashName();
            		
            		Item heldItem = null;
            		if (pokemonDic.containsKey("heldItem"))
            			heldItem = Item.getItemByName((String) pokemonDic.get("heldItem"));
            		
            		Moveset moveset = null;
            		if (pokemonDic.containsKey("moves")) {
            			moveset = new Moveset();
            			JSONArray movesArray = (JSONArray) pokemonDic.get("moves");
            			for(Object moveNameObj : movesArray) {
            				String moveName = (String) moveNameObj;
            				Move move = Move.getMoveByName(moveName);
            				moveset.addMove(move);
            			}
            		}
            		else
            			moveset = Moveset.defaultMoveset(species, level);
            		
            		// personality value logic
                    if (doubleBattle != 0) personalityValue = 0x80;
                    else if ((encounterMusicGender & 0x80) != 0) personalityValue = 0x78;
                    else personalityValue = 0x88;
                    
                    for(int i = 0; i < trainerName.length(); i++)
                    	nameHash = (nameHash + getCharValue(trainerName.charAt(i))) & 0xFFFFFFFF;
                    
            		for(int i = 0; i < hashName.length(); i++)
                    	nameHash = (nameHash + getCharValue(hashName.charAt(i))) & 0xFFFFFFFF;
            		personalityValue = (personalityValue + ((nameHash << 8) & 0xFFFFFFFF)) & 0xFFFFFFFF;
            		
            		iv = iv * 31 / 255;
            		//

            		Pokemon pokemon = new Pokemon(species, level, Nature.getNatureFromPersonalityValue(personalityValue), 
            				moveset, new IVs(iv), false); // TODO: more  ?
            		pokemon.setItem(heldItem);
            		party.add(pokemon);
            	}
            	trainer = new Trainer(trainerAlias, partyFlags, trainerClass, baseMoney, encounterMusicGender, trainerPic, 
            			trainerName, items, doubleBattle, aiFlags, badgeBoosts, partyType, party);
            	
            	trainersByName.put(new IgnoreCaseString(trainerAlias), trainer);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace(); //TODO
        } catch (IOException e) {
            e.printStackTrace(); //TODO
        } catch (ParseException e) {
			e.printStackTrace(); //TODO
		}
    }
	
	private static HashMap<String, Integer> charValues = null;
	
	public static void initCharValues(Game game) {
		charValues = new LinkedHashMap<String, Integer>();
		
		JSONParser jsonParser = new JSONParser();
        BufferedReader in;
        try {
        	System.out.println("/resources/"+game.getCharCodesFilename()); // TODO
            in = new BufferedReader(new InputStreamReader(Trainer.class
                    .getResource("/resources/"+game.getCharCodesFilename()).openStream())); // TODO : handle custom files ?
            JSONObject charCodesDic = (JSONObject) jsonParser.parse(in);
            for(Object charCodesObj : charCodesDic.entrySet()) {
                @SuppressWarnings("unchecked")
				Map.Entry<Object, Object> charCodesEntry = (Map.Entry<Object, Object>) charCodesObj;
                String charr = (String) charCodesEntry.getKey();
                int value = ((Long) charCodesEntry.getValue()).intValue();
                
                charValues.put(charr, value);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace(); //TODO
        } catch (IOException e) {
            e.printStackTrace(); //TODO
        } catch (ParseException e) {
			e.printStackTrace(); //TODO
		}
	}
	
	public static int getCharValue(char c) {
		Object intValue = charValues.get(String.valueOf(c));
		if (intValue == null) {
			System.out.println("RIP getCharValue");
		}
		return (int)intValue;
	}
	
	private static void printTrainers() {
		for(Map.Entry<IgnoreCaseString, Trainer> entry : trainersByName.entrySet()) {
			System.out.println(entry.getKey()+"="+entry.getValue());
		}
	}
	
	public static void main(String[] args) {
		Game game = Game.EMERALD;
		Trainer.initCharValues(game);
		Item.initItems(game);
		Move.initMoves(game);
		Species.initSpecies(game);
		Learnset.initLearnsets(game);
		Trainer.initTrainers(game);
		//System.out.println(Trainer.getTrainerByName("BRENDAN_1").party);
		// printTrainers();
	}
	
	private String trainerAlias;
	private int partyFlags;
	private String trainerClass;
	private int baseMoney;
	private int encounterMusicGender;
	private int trainerPic;
	private String trainerName;
	private List<Item> items;
	private int doubleBattle;
	private int aiFlags;
	private List<Stat> badgeBoosts;
	private String partyType;
	private ArrayList<Pokemon> party;
    private int reward;

    public Trainer(String trainerAlias, int partyFlags, String trainerClass, int baseMoney, int encounterMusicGender, int trainerPic, 
    		String trainerName, List<Item> items, int doubleBattle, int aiFlags, List<Stat> badgeBoosts, String partyType, ArrayList<Pokemon> party) {
    	this.trainerAlias = trainerAlias;
    	this.partyFlags = partyFlags;
    	this.trainerClass = trainerClass;
    	this.baseMoney = baseMoney;
    	this.encounterMusicGender = encounterMusicGender;
    	this.trainerPic = trainerPic;
    	this.trainerName = trainerName;
    	this.items = items;
    	this.doubleBattle = doubleBattle;
    	this.aiFlags = aiFlags;
    	this.badgeBoosts = badgeBoosts;
    	this.partyType = partyType;
    	this.party = party;
    	
    	this.reward = calculateBaseReward();
    }
    
    private int calculateBaseReward() {
    	int lastLevel = party.get(party.size() - 1).getLevel();
    	int reward = 4 * lastLevel * baseMoney * (doubleBattle !=0  ? 2 : 1);
    	return reward;
    }
    
    public String getTrainerAlias() {
		return trainerAlias;
	}

	public int getPartyFlags() {
		return partyFlags;
	}

	public String getTrainerClass() {
		return trainerClass;
	}

	public int getBaseMoney() {
		return baseMoney;
	}

	public int getEncounterMusicGender() {
		return encounterMusicGender;
	}

	public int getTrainerPic() {
		return trainerPic;
	}

	public String getTrainerName() {
		return trainerName;
	}

	public List<Item> getItems() {
		return items;
	}

	public int getDoubleBattle() {
		return doubleBattle;
	}

	public int getAiFlags() {
		return aiFlags;
	}

	public String getPartyType() {
		return partyType;
	}

	public ArrayList<Pokemon> getParty() {
		return party;
	}

	public int getBaseReward() {
		return reward;
	}
	
	public int getReward(Pokemon p) {
		return reward * (p.getHeldItem() != null && p.getHeldItem().getHoldEffect() == ItemHoldEffect.DOUBLE_PRIZE ? 2 : 1); //TODO : hardcoded
	}

	public List<Stat> getBadgeBoosts() {
		return badgeBoosts;
	}

	public void setParty(Collection<Pokemon> party) {
        this.party = new ArrayList<>(party);
    }

    @Override
    public void battle(Pokemon p, BattleOptions options) {
        for (Pokemon tp : party) {
            tp.battle(p, options);
        }
    }

    @Override
    public Iterator<Pokemon> iterator() {
        return party.iterator();
    }
    
    public String toString(Pokemon p) {
    	StringBuffer sb = new StringBuffer();
    	sb.append(String.format("%s %s %s | REWARD: %s", trainerClass, trainerName, doubleBattle != 0 ? "<DOUBLE>" : "", getReward(p)));
    	sb.append(Constants.endl);
    	if (items.size() > 0) {
    		sb.append("[");
    		StringBuffer sbItems = new StringBuffer();
	    	for (Item item : items) {
	    		sbItems.append(item.getDisplayName());
	    		sbItems.append(", ");
	    	}
	    	sb.append(sbItems.delete(sbItems.length()-2, sbItems.length()).toString()); // TODO: hardcoded based on the length of string ", "
	    	sb.append("] ");
    	}
    	sb.append(String.format("{%s}", allPokes()));
    	
    	return sb.toString();
    }

    /*
    public String toDebugString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append(String.format("%s %d %s %d %d %d %s ", 
    			trainerAlias, partyFlags, trainerClass, baseMoney, encounterMusicGender, trainerPic, trainerName));
    	for (Item item : items) {
    		sb.append(item.getDisplayName());
    		sb.append(" ");
    	}
    	sb.append(String.format("%d %d %s %d", doubleBattle, aiFlags, partyType, reward));
    	
    	return sb.toString();
    }
    */

    public String allPokes() {
        StringBuilder sb = new StringBuilder();
        for (Pokemon p : party) {
            sb.append(p.levelNameNatureAbility() + ", ");
        }
        return sb.delete(sb.length()-2, sb.length()).toString(); // TODO: hardcoded based on the length of string ", "
    }
}
