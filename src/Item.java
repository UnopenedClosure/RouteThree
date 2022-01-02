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

public class Item {
private static HashMap<IgnoreCaseString, Item> itemsByName;
	
	public static Item getItemByName(String name) {
        return itemsByName.get(new IgnoreCaseString(name));
    }
	
	public static void initItems(Game game) {
		itemsByName = new LinkedHashMap<IgnoreCaseString, Item>();
		
		JSONParser jsonParser = new JSONParser();
        BufferedReader in;
        try {
        	System.out.println("/resources/"+game.getItemsFilename()); // TODO
            in = new BufferedReader(new InputStreamReader(Trainer.class
                    .getResource("/resources/"+game.getItemsFilename()).openStream())); // TODO : handle custom files ?
            JSONArray itemsArray = (JSONArray) jsonParser.parse(in);
            for(Object itemObj : itemsArray) {
				JSONObject itemDic = (JSONObject) itemObj;
            	
            	String displayName = ((String) itemDic.get("name"));
            	int itemId = ((Long) itemDic.get("itemId")).intValue();
            	int price = ((Long) itemDic.get("price")).intValue();
            	String holdEffect = (String) itemDic.get("holdEffect");
            	int holdEffectParam = ((Long) itemDic.get("holdEffectParam")).intValue();
            	int importance = ((Long) itemDic.get("importance")).intValue();
            	int pocket = ((Long) itemDic.get("pocket")).intValue();
            	int type = ((Long) itemDic.get("type")).intValue();
            	int secondaryId = ((Long) itemDic.get("secondaryId")).intValue();
            	
            	Item item = new Item(displayName, itemId, price, holdEffect, holdEffectParam, 
            			importance, pocket, type, secondaryId);
            	
            	itemsByName.put(new IgnoreCaseString(displayName), item);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace(); //TODO
        } catch (IOException e) {
            e.printStackTrace(); //TODO
        } catch (ParseException e) {
			e.printStackTrace(); //TODO
		}
    }
	
	private static void printItems() {
		for(Map.Entry<IgnoreCaseString, Item> entry : itemsByName.entrySet()) {
			System.out.println(entry.getKey()+"="+entry.getValue());
		}
	}
	
	public static void main(String[] args) {
		Item.initItems(Game.RUBY);
		printItems();
	}
	
	private String displayName;
	private int itemId;
	private int price;
	private ItemHoldEffect holdEffect;
	private int holdEffectParam;
	private int importance;
	private int pocket;
	private int type;
	private int secondaryId;
	
	public Item(String displayName, int itemId, int price, String holdEffect, int holdEffectParam, 
			int importance, int pocket, int type, int secondaryId) {
		this.displayName = displayName;
		this.itemId = itemId;
		this.price = price;
		this.holdEffect = ItemHoldEffect.valueOf(holdEffect);
		this.holdEffectParam = holdEffectParam;
		this.importance = importance;
		this.pocket = pocket;
		this.type = type;
		this.secondaryId = secondaryId;	
	}

	public String getDisplayName() {
		return displayName;
	}

	public int getItemId() {
		return itemId;
	}

	public int getPrice() {
		return price;
	}

	public ItemHoldEffect getHoldEffect() {
		return holdEffect;
	}

	public int getHoldEffectParam() {
		return holdEffectParam;
	}

	public int getImportance() {
		return importance;
	}

	public int getPocket() {
		return pocket;
	}

	public int getType() {
		return type;
	}

	public int getSecondaryId() {
		return secondaryId;
	}
	
	public int getBuyPrice() {
		return getPrice();
	}
	
	public int getSellPrice() {
		return getPrice() / 2;
	}
	
	@Override
	public String toString() {
		return String.format("{'%s' id:%d price:%d '%s' %d imp:%d pocket:%d type:%d secId:%d}", 
				displayName, itemId, price, holdEffect, holdEffectParam, importance, pocket, type, secondaryId
				);
	}
	
}
