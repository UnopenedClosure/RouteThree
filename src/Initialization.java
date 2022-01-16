public class Initialization {
    public static void init(Game game) {
		Item.initItems(game);
		Move.initMoves(game);
		Learnset.initLearnsets(game);
		Species.initSpecies(game);
		Trainer.initCharValues(game);
		Trainer.initTrainers(game);
    }
    
    public static void main(String[] args) {
    	for (Game game : Game.values())
    		init(game);
    }
}
