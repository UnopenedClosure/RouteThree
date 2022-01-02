import java.util.EnumMap;

public enum Status {
	// Status1 (non stackable)
	NONE(""), SLEEP("SLP"), POISON("PSN"), BURN("BRN"), FREEZE("FRZ"), PARALYSIS("PRZ"), TOXIC("TXC"),
	
	// Status2 (stackable)
	CONFUSED("CONFUSED"), WRAPPED("WRAPPED"), NIGHTMARE("NIGHTMARE"), CURSED("CURSED"), FORESIGHT("FORESIGHT"), DEFENSE_CURL("DEFENSECURL"),
	
	// Status3 (stackable)
	LEECH_SEED("LEECHSEED"), MINIMIZED("MINIMIZED"), ROOTED("ROOTED"), CHARGED_UP("CHARGEDUP"),
	MUDSPORT("MUD SPORT"), WATERSPOUT("WATER SPOUT"), UNDERWATER("UNDERWATER"), TRACED("TRACED"),
	
	// Side_Status
	REFLECT("REFLECT"), LIGHTSCREEN("LIGHTSCREEN"), SPIKES("SPIKES");

	private String shortStatus;
	
	private Status(String shortStatus) {
		this.shortStatus = shortStatus;
	}
	
	public String getShortStatus() {
		return shortStatus;
	}
	
	public static Status noStatus1() {
		return NONE;
	}
	
	public static EnumMap<Status, Boolean> noStatus2_3(){
		EnumMap<Status, Boolean> statuses = new EnumMap<Status, Boolean>(Status.class);
		for(int i = CONFUSED.ordinal() ; i < Status.values().length ; i++) {
			statuses.put(Status.values()[i], false);
		}
		
		return statuses;
	}
	
	public boolean isStatus2_3() {
		return ordinal() >= Status.CONFUSED.ordinal();
	}
	
	@Override
	public String toString() {
		return shortStatus;
	}
}
