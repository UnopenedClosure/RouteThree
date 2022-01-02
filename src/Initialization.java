public class Initialization {
    public static void init(Game game) {
		Item.initItems(game);
		Move.initMoves(game);
		Learnset.initLearnsets(game);
		Species.initSpecies(game);
		Trainer.initCharValues(game);
		Trainer.initTrainers(game);
    }
}
