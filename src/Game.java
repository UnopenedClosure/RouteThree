
public enum Game {	
	RUBY("ruby", 
			"char_values.json", "item_data.json", "species_data.json", "move_data.json", 
			"learnset_data_rse.json", "trainer_data_rs.json"), 
	SAPPHIRE("sapphire", 
			"char_values.json", "item_data.json", "species_data.json", "move_data.json", 
			"learnset_data_rse.json", "trainer_data_rs.json"),
	
	EMERALD("emerald", 
			"char_values.json", "item_data.json", "species_data.json", "move_data.json", 
			"learnset_data_rse.json", "trainer_data_e.json"),
	
	FIRERED("firered", 
			"char_values.json", "item_data.json", "species_data.json", "move_data.json", 
			"learnset_data_fr.json", "trainer_data_frlg.json"),	
	LEAFGREEN("leafgreen", 
			"char_values.json", "item_data.json", "species_data.json", "move_data.json", 
			"learnset_data_lg.json", "trainer_data_frlg.json"),
	;
	
	//CUSTOM("custom", null, null, null, null, null, null);
	
	private final String name;
	private final String charCodesFilename;
	private final String itemsFilename;
	private final String speciesFilename;
	private final String movesFilename;
	private final String learnsetsFilename;
	private final String trainersFilename;
	
	private Game(String name, String charCodesFilename, String itemsFilename, String speciesFilename, String movesFilename, String learnsetsFilename, String trainersFilename) {
		this.name = name;
		this.charCodesFilename = charCodesFilename;
		this.itemsFilename = itemsFilename;
		this.speciesFilename = speciesFilename;
		this.movesFilename = movesFilename;
		this.learnsetsFilename = learnsetsFilename;
		this.trainersFilename = trainersFilename;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCharCodesFilename() {
		return charCodesFilename;
	}

	public String getItemsFilename() {
		return itemsFilename;
	}
	
	public String getSpeciesFilename() {
		return speciesFilename;
	}

	public String getMovesFilename() {
		return movesFilename;
	}

	public String getLearnsetsFilename() {
		return learnsetsFilename;
	}

	public String getTrainersFilename() {
		return trainersFilename;
	}

	public static Game getEnum(String value) {
		for(Game v : values())
            if(v.getName().equalsIgnoreCase(value)) return v;
        throw new IllegalArgumentException();
	}
}
