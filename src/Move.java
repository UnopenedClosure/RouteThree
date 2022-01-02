import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Move {	
	private static HashMap<IgnoreCaseString, Move> movesByName;
	
	public static Move getMoveByName(String name) {
		// System.out.println(name+" "+movesByName.containsKey(name)); //TODO
        return movesByName.get(new IgnoreCaseString(name));
    }
	
	public static void initMoves(Game game) {
		movesByName = new LinkedHashMap<IgnoreCaseString, Move>();
		
		JSONParser jsonParser = new JSONParser();
        BufferedReader in;
        try {
        	System.out.println("/resources/"+game.getMovesFilename()); // TODO
            in = new BufferedReader(new InputStreamReader(Move.class
                    .getResource("/resources/"+game.getMovesFilename()).openStream())); // TODO : handle custom files ?
            Move move = null;
            JSONArray array = (JSONArray) jsonParser.parse(in);
            for(Object moveObj : array) {
            	JSONObject moveDic = (JSONObject) moveObj;
            	
            	String name = (String) moveDic.get("name");
            	int index = currentIndex;
            	MoveEffect effect = MoveEffect.valueOf((String) moveDic.get("move_effect"));
            	int power = ((Long) moveDic.get("base_power")).intValue();
            	Type type = Type.valueOf((String) moveDic.get("type"));
            	int accuracy = ((Long) moveDic.get("accuracy")).intValue();
            	int pp = ((Long) moveDic.get("pp")).intValue();
            	int effectChance = ((Long) moveDic.get("secondary_effect_chance")).intValue();
            	MoveTarget target = MoveTarget.valueOf((String) moveDic.get("target"));
            	int priority = ((Long) moveDic.get("priority")).intValue();
            	
            	JSONArray flagsArray = (JSONArray) moveDic.get("flags");
            	ArrayList<String> moveFlagStrings = new ArrayList<>();
            	for(Object flagObj: flagsArray)
            		moveFlagStrings.add((String) flagObj);
            	
            	move = new Move(name, index, effect, power, type, accuracy, pp, effectChance,
                		target, priority, moveFlagStrings);
            	
            	movesByName.put(new IgnoreCaseString(name), move);
            	
            	currentIndex++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace(); //TODO
        } catch (IOException e) {
            e.printStackTrace(); //TODO
        } catch (ParseException e) {
			e.printStackTrace(); //TODO
		}
    }
	
	public static void printMoves() {
		for(Map.Entry<IgnoreCaseString, Move> entry : movesByName.entrySet()) {
			System.out.println(entry.getKey()+"="+entry.getValue());
		}
	}
	
	// TODO: Only for testing
	public static void main(String[] args) {
		initMoves(Settings.game);
		printMoves();
	}

	private static int currentIndex = 0;
	
    private String name;
    private int index;
    private MoveEffect effect;
    private int power;
    private Type type;
    private int accuracy;
    private int pp;
    private int effectChance;
    private MoveTarget target;
    private int priority;
    private EnumSet<MoveFlag> moveFlags;

    private Move(String name, int index, MoveEffect effect, int power, Type type, int accuracy, int pp, int effectChance,
    		MoveTarget target, int priority, List<String> moveFlagStrings) {
        this.name = name;
        this.index = index;
        this.effect = effect;
        this.power = power;
        this.type = type;
        this.accuracy = accuracy;
        this.pp = pp;
        this.effectChance = effectChance;
        this.target = target;
        this.priority = priority;
        this.moveFlags = MoveFlag.getMoveFlags(moveFlagStrings);   
    }
    
    public Move(Move m) { // Copy constructor
    	this.name = m.name;
        this.index = m.index;
        this.effect = m.effect;
        this.power = m.power;
        this.type = m.type;
        this.accuracy = m.accuracy;
        this.pp = m.pp;
        this.effectChance = m.effectChance;
        this.target = m.target;
        this.priority = m.priority;
        this.moveFlags = EnumSet.copyOf(m.moveFlags);
    }

    public String getName() {
        return name;
    }
    
    public int getIndex() {
    	return index;
    }

    public String getBoostedName(int i) {
        return name + " " + i;
    }

    public MoveEffect getEffect() {
        return effect;
    }

    public int getPower() {
        return power;
    }

    public Type getType() {
        return type;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public int getPP() {
        return pp;
    }

    public int getEffectChance() {
        return effectChance;
    }
    
    public MoveTarget getMoveTarget() {
    	return target;
    }
    
    public int getPriority() {
    	return priority;
    }
    
    public Set<MoveFlag> getMoveFlags(){
    	return moveFlags;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setType(Type type) {
        this.type = type;
    }
    
    public boolean isPhysical() {
    	return Type.isPhysicalType(type);
    }
    
    public boolean isSpecial() {
    	return !isPhysical() && type !=Type.NONE && type != Type.MYSTERY;
    }
    
    @Override
    public String toString() {
    	return String.format("{'%s' %d '%s' %d '%s' %d %d %d '%s' %d %s}", 
    			name, index, effect.toString(), power, type.toString(), accuracy, pp, effectChance, target.toString(), priority,
    			moveFlags.toString()
    			);
    }
}
