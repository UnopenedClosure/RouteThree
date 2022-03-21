import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

public class Main {
	//TODO allow for default battle configs (e.g. if we want every fight to be -v 1 -lvstats, we don't have to write it every time
    private static StringBuilder output = new StringBuilder();
    
    public static void append(String s) {
        output.append(s);
    }
    public static void appendln(String s) {
        output.append(s + Constants.endl);
    }
    
    public static void main(String[] args) throws InvalidFileFormatException, IOException { 
        String masterFileName = (args.length > 0) ? args[0] : "master.ini";
        String backupDebugFileName = (args.length > 1) ? args[1] : "debug.txt";
        Wini masterIni = null;
        File debugFile = null;
        File configFile = null;
        
        try {
	        masterIni = new Wini(new File(masterFileName));
	        
	        debugFile = new File(masterIni.get("master","debugFile"));
	        configFile = new File(masterIni.get("master","configFile"));
        
        } catch (Exception exc) {
        	appendln("The config/debug file path is unaccessible. It is probably a typo.");
        	
            FileWriter fw = debugFile != null ? new FileWriter(debugFile) : new FileWriter(backupDebugFileName);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(output.toString());
            bw.close();
            System.exit(-1);
        }
        
        
        Wini configIni = new Wini(configFile);
        
        // Prepare file outputs
        File fh = new File(configIni.get("files","routeFile"));
        String outputFilename = "outputs/out_"+fh.getName();
        
        if(configIni.get("files").containsKey("outputFile"))
        	outputFilename = configIni.get("files", "outputFile");
        
        // Set game and initialize
        String gameName = configIni.get("game", "game");
        Game game = Game.getEnum(gameName);
        
        Initialization.init(game);
        // Set pokemon
        String speciesStr = configIni.get("poke", "species");
        Species species = Species.getSpeciesByName(speciesStr);
        
        int level = configIni.get("poke", "level", int.class);
        
        String natureStr = configIni.get("poke",  "nature");
        Nature nature = Nature.getEnum(natureStr);
        
        int hpIV = configIni.get("poke", "hpIV", int.class);
        int atkIV = configIni.get("poke", "atkIV", int.class);
        int defIV = configIni.get("poke", "defIV", int.class);
        int spaIV = configIni.get("poke", "spaIV", int.class);
        int spdIV = configIni.get("poke", "spdIV", int.class);
        int speIV = configIni.get("poke", "speIV", int.class);

        
        // TODO : handle custom files

        
        if(configIni.get("util").containsKey("overallChanceKO"))
        	Settings.overallChanceKO = configIni.get("util", "overallChanceKO", boolean.class);
        if(configIni.get("util").containsKey("showGuarantees"))
        	Settings.showGuarantees = configIni.get("util", "showGuarantees", boolean.class);
        
        IVs ivs = new IVs(hpIV, atkIV, defIV, spaIV, spdIV, speIV);
        Pokemon p = null;
        try {
            p = new Pokemon(species, level, nature, ivs, false);
            if(configIni.get("poke").containsKey("boostedExp")) {
                boolean boostedExp = configIni.get("poke", "boostedExp", boolean.class);
                p.setBoostedExp(boostedExp);
            }
            if(configIni.get("poke").containsKey("pokerus")) {
                boolean pokerus = configIni.get("poke", "pokerus", boolean.class);
                p.setBoostedExp(pokerus);
            }
            
        } catch(NullPointerException e) {
            appendln("Error in your config file. Perhaps you have an incorrect pokemon species name?");
            FileWriter fw = new FileWriter(outputFilename);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(output.toString());
            bw.close();
        }
        
        //TODO allow routeFile and outputFile to be parsed from configFile.getName() if they are not defined
        List<GameAction> actions = RouteParser.parseFile(configIni.get("files","routeFile"));
        if (actions == null) { // Stop execution and output message at the first encountered error
        	FileWriter fw = new FileWriter(outputFilename);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(output.toString());
            bw.close();
            return;
        }
        
        int[] XItems = {0,0,0,0,0, 0}; //atk,def,spa,spd,spe,acc
        int numBattles = 0;
        int rareCandies = 0;
        int HPUp = 0;
        int iron = 0;
        int protein = 0;
        int carbos = 0;
        int zinc = 0;
        int calcium = 0;
        for(GameAction a : actions) {  
        	try {
        		a.performAction(p);
        	} catch (Exception exc) {
        		exc.printStackTrace();
        		appendln("Unexpected error : are your array parameters of proper length ?");
                FileWriter fw = new FileWriter(outputFilename);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(output.toString());
                bw.close();
                System.exit(-1); //TODO : bad workaround
        	}
            if (a instanceof Battle) { //TODO : some boosts might not come from X items, so what do we do ?
                StatModifier sm = ((Battle) a).getMod1();
                XItems[0] += Math.max(0, sm.getAtkStage());
                XItems[1] += Math.max(0, sm.getDefStage());
                XItems[2] += Math.max(0, sm.getSpaStage());
                XItems[3] += Math.max(0, sm.getSpeStage());
                XItems[4] += Math.max(0, sm.getAccStage());
                numBattles++;
            } else if (a == GameAction.eatRareCandy) {
                rareCandies++;
            } else if (a == GameAction.eatHPUp){
                HPUp++;
            } else if (a == GameAction.eatProtein){
                protein++;
            } else if (a == GameAction.eatIron){
                iron++;
            } else if (a == GameAction.eatCalcium){
                calcium++;
            } else if (a == GameAction.eatZinc){
                zinc++;
            } else if (a == GameAction.eatCarbos){
                carbos++;
            } 
        }        
        
        if(configIni.get("util", "printxitems", boolean.class)) { // TODO: bad code because hardcoded price values & boosts can come from moves, not items
            if(XItems[0] != 0)
                appendln("X ATKS: " + XItems[0]);
            if(XItems[1] != 0)
                appendln("X DEFS: " + XItems[1]);
            if(XItems[2] != 0)
                appendln("X SPAS: " + XItems[2]);
            if(XItems[3] != 0)
                appendln("X SPES: " + XItems[3]);
            if(XItems[4] != 0)
                appendln("X ACCURACYS: " + XItems[4]);
            int cost = XItems[0] * 500 + XItems[1] * 550 + XItems[2] * 350 + XItems[3] * 350 + XItems[4] * 950;
            if(cost != 0)
                appendln("X item cost: " + cost);
        }
        
        if(configIni.get("util", "printrarecandies", boolean.class)) {
            if(rareCandies != 0)
                appendln("Total Rare Candies: " + rareCandies);
        }
        if(configIni.get("util", "printstatboosters", boolean.class)) {
            if(HPUp != 0) {
                appendln("HP UP: " + HPUp);
            }
            if(protein != 0) {
                appendln("PROTEIN: " + protein);
            }
            if(iron != 0) {
                appendln("IRON: " + iron);
            }
            if(calcium != 0) {
                appendln("CALCIUM: " + calcium);
            }
            if(zinc != 0) {
                appendln("ZINC: " + zinc);
            }
            if(carbos != 0) {
                appendln("CARBOS: " + carbos);
            }
        }

        if(!configIni.get("files").containsKey("outputFile")) {
	        if(!(new File("outputs/")).exists())
	            (new File("outputs/")).mkdir();
        }
        FileWriter fw = new FileWriter(outputFilename);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(output.toString());
        bw.close();
    }
}
