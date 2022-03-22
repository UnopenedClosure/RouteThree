package routethree;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//represents a sequence of moves and levels which a species learns moves at
public class Learnset {
	private static HashMap<IgnoreCaseString, Learnset> learnsetsByName;
	
	public static Learnset getLearnsetByName(String name) {
        return learnsetsByName.get(new IgnoreCaseString(name));
    }
	
	public static void initLearnsets(Game game) {
		learnsetsByName = new LinkedHashMap<IgnoreCaseString, Learnset>();
		
		JSONParser jsonParser = new JSONParser();
        BufferedReader in;
        try {
        	System.out.println("/resources/"+game.getLearnsetsFilename()); // TODO
            in = new BufferedReader(new InputStreamReader(Learnset.class
                    .getResource("/resources/"+game.getLearnsetsFilename()).openStream())); // TODO : handle custom files ?
            JSONObject learnsetsDic = (JSONObject) jsonParser.parse(in);
            for(Object learnsetEntryObj : learnsetsDic.entrySet()) {
                Learnset learnset = new Learnset();
                
            	@SuppressWarnings("unchecked")
				Map.Entry<Object, Object> learnsetEntry = (Map.Entry<Object, Object>) learnsetEntryObj;
            	String speciesName = (String) learnsetEntry.getKey();
            	JSONArray levelMovesArray = (JSONArray) learnsetEntry.getValue();
            	for(Object levelMoveObj : levelMovesArray) {
            		JSONArray levelMoveArray = (JSONArray) levelMoveObj;
            		int level = ((Long) levelMoveArray.get(0)).intValue(); // TODO: hardcoded value
            		String moveName = (String) levelMoveArray.get(1); // TODO: hardcoded value
            		Move move = Move.getMoveByName(moveName);
            		//System.out.println(moveName+" "+move); // TODO
            		LevelMove levelMove = new LevelMove(level, move);
            		learnset.add(levelMove);
            	}
            	
            	learnsetsByName.put(new IgnoreCaseString(speciesName), learnset);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace(); //TODO
        } catch (IOException e) {
            e.printStackTrace(); //TODO
        } catch (ParseException e) {
			e.printStackTrace(); //TODO
		}
    }
	
	private static void printLearnsets() {
		for(Map.Entry<IgnoreCaseString, Learnset> entry : learnsetsByName.entrySet()) {
			System.out.println(entry.getKey()+"="+entry.getValue());
		}
	}
	
	public static void main(String[] args) {
		Move.initMoves(Game.RUBY);
		//Move.printMoves();
		initLearnsets(Game.RUBY);
		printLearnsets();
	}
	

    private ArrayList<LevelMove> levelMoves;
    
    public Learnset() {
        levelMoves = new ArrayList<LevelMove>();
    }
            
    public Learnset(List<LevelMove> new_levelMoves) {
        if (new_levelMoves == null) {
            levelMoves = new ArrayList<LevelMove>();
        } else {
        	levelMoves = new ArrayList<LevelMove>(new_levelMoves);
        }
    }
    
    public boolean add(LevelMove levelMove) {
    	return levelMoves.add(levelMove);
    }
    
    public ArrayList<LevelMove> getLevelMoves(){
    	return levelMoves;
    }
    
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	for (LevelMove levelMove : levelMoves) {
    		sb.append(levelMove);
    		sb.append("\n");
    	}
    	return sb.toString();
    }
    
}
