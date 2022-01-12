import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Species {
	private static HashMap<IgnoreCaseString, Species> speciesByName;
	
	public static Species getSpeciesByName(String name) {
        return speciesByName.get(new IgnoreCaseString(name));
    }
	
	
	public static void initSpecies(Game game) {
		speciesByName = new LinkedHashMap<IgnoreCaseString, Species>();
		
		JSONParser jsonParser = new JSONParser();
        BufferedReader in;
        try {
        	System.out.println("/resources/"+game.getSpeciesFilename()); // TODO
            in = new BufferedReader(new InputStreamReader(Species.class
                    .getResource("/resources/"+game.getSpeciesFilename()).openStream())); // TODO : handle custom files ?
            Species species = null;
            JSONArray array = (JSONArray) jsonParser.parse(in);
            for(Object speciesObj : array) {
            	JSONObject speciesDic = (JSONObject) speciesObj;
            	
            	String name = (String) speciesDic.get("name");
            	String displayName = (String) speciesDic.get("displayName");
            	int dexNum = ((Long) speciesDic.get("dexNum")).intValue();
            	
            	JSONArray baseStatsArray = (JSONArray) speciesDic.get("baseStats");
            	int baseHP = ((Long) baseStatsArray.get(Stat.HP.getId())).intValue();
            	int baseAtk = ((Long) baseStatsArray.get(Stat.ATK.getId())).intValue();
            	int baseDef = ((Long) baseStatsArray.get(Stat.DEF.getId())).intValue();
            	int baseSpa = ((Long) baseStatsArray.get(Stat.SPA.getId())).intValue();
            	int baseSpd = ((Long) baseStatsArray.get(Stat.SPD.getId())).intValue();
            	int baseSpe = ((Long) baseStatsArray.get(Stat.SPE.getId())).intValue();
            	
            	JSONArray typesArray = (JSONArray) speciesDic.get("types");
            	Type type1 = Type.valueOf((String) typesArray.get(0));
            	Type type2 = (typesArray.size() <= 1) ? Type.NONE : Type.valueOf((String) typesArray.get(1));

            	int baseExp = ((Long) speciesDic.get("baseExp")).intValue();
            	
            	JSONArray evYieldsArray = (JSONArray) speciesDic.get("evYields");
            	int HPEV = ((Long) evYieldsArray.get(Stat.HP.getId())).intValue();
            	int atkEV = ((Long) evYieldsArray.get(Stat.ATK.getId())).intValue();
            	int defEV = ((Long) evYieldsArray.get(Stat.DEF.getId())).intValue();
            	int spaEV = ((Long) evYieldsArray.get(Stat.SPA.getId())).intValue();
            	int spdEV = ((Long) evYieldsArray.get(Stat.SPD.getId())).intValue();
            	int speEV = ((Long) evYieldsArray.get(Stat.SPE.getId())).intValue();
            	
            	Gender genderRatio = Gender.valueOf((String) speciesDic.get("genderRatio"));
            	ExpCurve expCurve = ExpCurve.valueOf((String) speciesDic.get("expCurve"));
            	
            	JSONArray abilitiesArray = (JSONArray) speciesDic.get("abilities");
            	Ability ability1 = Ability.valueOf((String) abilitiesArray.get(0));
            	Ability ability2 = (abilitiesArray.size() <= 1) ? Ability.NONE : Ability.valueOf((String) abilitiesArray.get(1));
            	int weight = ((Long) speciesDic.get("weight")).intValue();
            	
            	
            	species = new Species(name, displayName, dexNum,
                		baseHP, baseAtk, baseDef, baseSpa, baseSpd, baseSpe,
                		type1, type2, baseExp,
                		HPEV, atkEV, defEV, spaEV, spdEV, speEV,
                		genderRatio, expCurve, ability1, ability2, weight);
            			
            	speciesByName.put(new IgnoreCaseString(name), species);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace(); //TODO
        } catch (IOException e) {
            e.printStackTrace(); //TODO
        } catch (ParseException e) {
			e.printStackTrace(); //TODO
		}
    }
	
	private static void printSpecies() {
		for(Map.Entry<IgnoreCaseString, Species> entry : speciesByName.entrySet()) {
			System.out.println(entry.getKey()+"="+entry.getValue());
		}
	}
	
    private String name;
    private String displayName;
    private int dexNum;
    private int baseHP;
    private int baseAtk;
    private int baseDef;
    private int baseSpa;
    private int baseSpd;
    private int baseSpe;
    private Type type1;
    private Type type2;
    private int baseExp;
    private int HPEV;
    private int atkEV;
    private int defEV;
    private int spaEV;
    private int spdEV;
    private int speEV;
    private Gender genderRatio;
    private ExpCurve expCurve;
    private Ability ability1;
    private Ability ability2;
    private int weight; // in hectograms

    private Species(String name, String displayName, int dexNum,
    		int baseHP, int baseAtk, int baseDef, int baseSpa, int baseSpd, int baseSpe,
    		Type type1, Type type2, int killExp,
    		int HPEV, int atkEV, int defEV, int spaEV, int spdEV, int speEV,
    		Gender gender, ExpCurve expCurve, Ability ability1, Ability ability2, int weight) {
        this.name = name;
        this.displayName = displayName;
        this.dexNum = dexNum;
        this.baseHP = baseHP;
        this.baseAtk = baseAtk;
        this.baseDef = baseDef;
        this.baseSpa = baseSpa;
        this.baseSpd = baseSpd;
        this.baseSpe = baseSpe;
        this.type1 = type1;
        this.type2 = type2;
        this.baseExp = killExp;
        this.HPEV = HPEV;
        this.atkEV = atkEV;
        this.defEV = defEV;
        this.spaEV = spaEV;
        this.spdEV = spdEV;
        this.speEV = speEV;
        this.genderRatio = gender;
        this.expCurve = expCurve;
        this.ability1 = ability1;
        this.ability2 = ability2;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }
    
    public String getDisplayName() {
    	return displayName;
    }

    public int getDexNum() {
        return dexNum;
    }
    
    public int getBaseHP() {
        return baseHP;
    }

    public int getBaseAtk() {
        return baseAtk;
    }

    public int getBaseDef() {
        return baseDef;
    }

    public int getBaseSpa() {
        return baseSpa;
    }

    public int getBaseSpd() {
        return baseSpd;
    }

    public int getBaseSpe() {
        return baseSpe;
    }

    public Type getType1() {
        return type1;
    }

    public Type getType2() {
        return type2;
    }

    public int getBaseExp() {
        return baseExp;
    }
    
    public int getHPEV() {
    	return HPEV;
    }
    
    public int getAtkEV() {
    	return atkEV;
    }
    
    public int getDefEV() {
    	return defEV;
    }
    
    public int getSpaEV() {
    	return spaEV;
    }
    
    public int getSpdEV() {
    	return spdEV;
    }
    
    public int getSpeEV() {
    	return speEV;
    }
    
    public Gender getGenderRatio() {
    	return genderRatio;
    }

    public ExpCurve getExpCurve() {
        return expCurve;
    }
    
    public Ability getAbility1() {
    	return ability1;
    }
    
    public Ability getAbility2() {
    	return ability2;
    }
    
    public int getWeight() {
    	return weight;
    }
    
    @Override
    public String toString() {
    	return String.format("{'%s' '%s' %d [%d/%d/%d/%d/%d/%d] '%s'%s %d <%d/%d/%d/%d/%d/%d> '%s' '%s' ['%s'%s] %d}", 
    			name, displayName, dexNum, baseHP, baseAtk, baseDef, baseSpa, baseSpd, baseSpe, 
    			type1, (type2 != Type.NONE) ? "/'"+type2+"'" : "", 
    			baseExp, HPEV, atkEV, defEV, spaEV, spdEV, speEV, 
    			genderRatio, expCurve, 
    			ability1, (ability2 != Ability.NONE) ? "/'"+ability2+"'" : "", weight
    			);
    }
    
    // TODO : only testing purposes
    public static void main(String[] args) {
    	initSpecies(Game.RUBY);
    	printSpecies();
    }
}
