package routethree;
import java.util.EnumMap;
import java.util.Map;

//the in-battle stat modifiers to a pokemon (caused by e.g. x attack)
public class StatModifier {
    // int from -6 to +6 that represents the stage
    private int atk = 0;
    private int def = 0;
    private int spa = 0;
    private int spd = 0;
    private int spe = 0;
    private int acc = 0;
    private int eva = 0;
    private boolean isFlashFireActivated = false;
    private boolean isOneThirdHPOrLess = false;
    private Status status1 = Status.noStatus1();
    private EnumMap<Status, Boolean> status2_3 = Status.noStatus2_3();
    private Weather weather = Weather.NONE;

    public StatModifier() {}

    public StatModifier(int atk, int def, int spa, int spd, int spe) {
        this.atk = atk;
        this.def = def;
        this.spa = spa;
        this.spd = spd;
        this.spe = spe;
    }

    public StatModifier(int atk, int def, int spa, int spd, int spe, int acc) {
        this(atk, def, spa, spd, spe);
        this.acc = acc;
    }


    public StatModifier(int atk, int def, int spa, int spd, int spe, int acc, int eva) {
        this(atk, def, spa, spd, spe, acc);
        this.eva = eva;
    }

    // used to keep the stage between -6 and +6
    private static int bound(int stage) {
        if (stage < -6)
            return -6;
        else if (stage > 6)
            return 6;
        else
            return stage;
    }

    // in gen 1, accuracy/evasion stages are done the same as other stats
    private static double accEvaMultiplier(int stage) {
        return ((double) acc_eva_multipliers[stage + 6]) / acc_eva_divisors[stage + 6];
    }

    //multiplier for atk,def,spc,spd
    private static double normalStatMultiplier(int stage) {
        return ((double) normal_multipliers[stage + 6]) / normal_divisors[stage + 6];
    }

    private static int[] normal_multipliers = new int[] { 2, 2, 2, 2, 2, 2, 2, 3, 4, 5, 6, 7, 8};
    private static int[] normal_divisors    = new int[] { 8, 7, 6, 5, 4, 3, 2, 2, 2, 2, 2, 2, 2};
    private static int[] acc_eva_multipliers = new int[] {  33,  36,  43,  50,  60,  75, 100, 133, 166, 200, 250, 266, 300};
    private static int[] acc_eva_divisors    = new int[] { 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100};
    

    private static int modifyStat(int original, int stage) {
        return original * normal_multipliers[stage + 6] / normal_divisors[stage + 6];
    }
    
    public int getAccStage() {
        return acc;
    }

    public void setAccStage(int acc) {
        this.acc = bound(acc);
    }

    public int getEvaStage() {
        return eva;
    }

    public void setEvaStage(int eva) {
        this.eva = bound(eva);
    }
    
    public Status getStatus1() {
    	return status1;
    }
    
    public void setStatus1(Status status1) {
    	this.status1 = status1;
    }
    
    public boolean hasStatus2_3(Status status) {
    	if(!status.isStatus2_3()) {
    		return false;
    	}
    	return status2_3.get(status);
    }
    
    /* TODO : not needed ?
    public void setStatus2_3(EnumMap<Status, Integer> status2_3, boolean isInteger) { // TODO : dummy parameter to circumvent type erasure
    	EnumMap<Status, Boolean> newMap = new EnumMap<Status, Boolean>(Status.class);
    	for(Map.Entry<Status, Integer> entry : status2_3.entrySet())
    		newMap.put(entry.getKey(), (entry.getValue()) != 0);
    	this.status2_3 = newMap;
    }
    */
    
    public void setStatus2_3(EnumMap<Status, Boolean> status2_3) {
    	this.status2_3 = status2_3;
    }
    
    // TODO: needed ?
    public void setStatus2_3(Status status, boolean hasStatus) {
    	if(!status.isStatus2_3()) {
    		return;
    	}
    	status2_3.put(status, hasStatus);
    }

    public int getAtkStage() {
        return atk;
    }

    public void setAtkStage(int atk) {
        this.atk = bound(atk);
    }

    public int getDefStage() {
        return def;
    }

    public void setDefStage(int def) {
        this.def = bound(def);
    }

    public int getSpaStage() {
        return spa;
    }

    public void setSpaStage(int spa) {
        this.spa = bound(spa);
    }

    public int getSpdStage() {
        return spd;
    }

    public void setSpdStage(int spd) {
        this.spd = bound(spd);
    }

    public int getSpeStage() {
        return spe;
    }

    public void setSpeStage(int spe) {
        this.spe = bound(spe);
    }

    public void incrementAccStage() {
        incrementAccStage(1);
    }

    public void incrementEvaStage() {
        incrementEvaStage(1);
    }

    public void incrementAtkStage() {
        incrementAtkStage(1);
    }

    public void incrementDefStage() {
        incrementDefStage(1);
    }

    public void incrementSpaStage() {
        incrementSpaStage(1);
    }

    public void incrementSpdStage() {
        incrementSpdStage(1);
    }

    public void incrementSpeStage() {
        incrementSpeStage(1);
    }

    public void incrementAccStage(int i) {
        setAccStage(getAccStage() + i);
    }

    public void incrementEvaStage(int i) {
        setEvaStage(getEvaStage() + i);
    }

    public void incrementAtkStage(int i) {
        setAtkStage(getAtkStage() + i);
    }

    public void incrementDefStage(int i) {
        setDefStage(getDefStage() + i);
    }

    public void incrementSpaStage(int i) {
        setSpaStage(getSpaStage() + i);
    }

    public void incrementSpdStage(int i) {
        setSpdStage(getSpdStage() + i);
    }

    public void incrementSpeStage(int i) {
        setSpeStage(getSpeStage() + i);
    }

    public double getAccMultiplier() {
        return accEvaMultiplier(acc);
    }

    public double getEvasionMultiplier() {
        return accEvaMultiplier(eva);
    }

    public double getAtkMultiplier() {
        return normalStatMultiplier(atk);
    }

    public double getDefMultiplier() {
        return normalStatMultiplier(def);
    }

    public double getSpaMultiplier() {
        return normalStatMultiplier(spa);
    }

    public double getSpdMultiplier() {
        return normalStatMultiplier(spd);
    }

    public double getSpeMultiplier() {
        return normalStatMultiplier(spe);
    }

    public String summary(Pokemon p) {
    	StringBuffer sb = new StringBuffer();
    	sb.append(getStageStr(p));
        sb.append(getStatus1Str());
        sb.append(getStatus2_3Str());
        
        return sb.toString();
    }
    
    private String getStageStr(Pokemon p) {
    	StringBuffer sb = new StringBuffer();
    	if(hasStageMods() || p.hasBadgeBoost())
    		sb.append(String.format("+[%s%d/%s%d/%s%d/%s%d/%s%d|%d/%d] ", 
    				p.hasAtkBadge() ? "*": "", atk, // TODO: hardcoded
    				p.hasDefBadge() ? "*": "", def, 
    				p.hasSpaBadge() ? "*": "", spa,
    				p.hasSpdBadge() ? "*": "", spd, 
    				p.hasSpeBadge() ? "*": "", spe, 
    				acc, eva));
    	
    	if(spe != 0 || p.hasSpeBadge()) {
    		sb.append(String.format("Final speed: %d ", modStat(p.getSpe(), Stat.SPE)));
    	}
    	
    	return sb.toString();
    }
    
    private String getStatus1Str() {
    	if(hasStatus1())
    		return String.format("<%s> ", status1);
    	else
    		return "";
    }
    
    private String getStatus2_3Str() {
    	StringBuffer sb = new StringBuffer();
    	for(Map.Entry<Status, Boolean> entry : status2_3.entrySet()) {
    		if (entry.getValue())
    			sb.append(String.format("%s ", entry.getKey()));
    	}
    	
    	return sb.length() > 0 ? sb.toString().substring(0, sb.length() - 1) : sb.toString();
    }
    
    private boolean hasStageMods() {
    	return atk != 0 || def != 0 || spa != 0 || spd != 0 || spe != 0 || acc != 0 || eva != 0;
    }
    
    private boolean hasStatus1() {
    	return status1 != Status.NONE;
    }
    
    private boolean hasAnyStatus2_3() {
    	for(Map.Entry<Status, Boolean> entry : status2_3.entrySet()) {
    		if (entry.getValue())
    			return true;
    	}
    	return false;
    }
    
    public boolean hasMods() {
    	return hasStageMods() || hasStatus1() || hasAnyStatus2_3();
    }

    public int modStat(int value, Stat stat) { // TODO : doesn't seem to limit to a minimum of 1 in Gen 3 | doesn't apply badge boosts
    	switch(stat) {
    	case ATK : return modifyStat(value, atk);
    	case DEF : return modifyStat(value, def);
    	case SPA : return modifyStat(value, spa);
    	case SPD : return modifyStat(value, spd);
    	case SPE : return modifyStat(value, spe);
    	default : return value;
    	}
    }
    
    // TODO : Adapter methods for speed comparisons with opponents, might not be ideal in some places idk
    public int modSpe(Pokemon p) { // TODO: wouldn't work in Gen 4 i guess, because of speed boosting items
        int a = modifyStat(p.getSpe(), spe);
        return a;
    }
    
    public int modSpeWithIVandNature(Pokemon p, int iv, Nature nature) { // TODO: wouldn't work in Gen 4 i guess, because of speed boosting items
        int a = modifyStat(p.getSpeWithIV(iv, nature), spe);
        return a;
    }

    /* TODO : probably obsolete methods for Gen 3. Gen 4 too ?
    public int modAtk(Pokemon p) {
        int a = Math.max(modifyStat(p.getTrueAtk(), atk), 1);
        return a;
    }

    public int modDef(Pokemon p) {
        int a = Math.max(modifyStat(p.getTrueDef(), def), 1);
        return a;
    }

    public int modSpa(Pokemon p) {
        int a = Math.max(modifyStat(p.getTrueSpa(), spa), 1);
        return a;
    }

    public int modSpd(Pokemon p) {
        int a = Math.max(modifyStat(p.getTrueSpd(), spd), 1);
        return a;
    }
    */


    public String modStatsStr(Pokemon p) { // TODO : as is, no badge boosts
        return String.format("%s/%s/%s/%s/%s/%s", p.getHP(), 
        		modStat(p.getTrueAtk(), Stat.ATK), 
        		modStat(p.getTrueDef(), Stat.DEF),
        		modStat(p.getTrueSpa(), Stat.SPA),
        		modStat(p.getTrueSpd(), Stat.SPD),
        		modStat(p.getTrueSpe(), Stat.SPE));
    }

	public Weather getWeather() {
		return weather;
	}

	public void setWeather(Weather weather) {
		this.weather = weather;
	}


	public boolean isOneThirdHPOrLess() {
		return isOneThirdHPOrLess;
	}

	public void setOneThirdHPOrLess(boolean isOneThirdHPOrLess) {
		this.isOneThirdHPOrLess = isOneThirdHPOrLess;
	}

	public boolean isFlashFireActivated() {
		return isFlashFireActivated;
	}

	public void setFlashFireActivated(boolean isFlashFireActivated) {
		this.isFlashFireActivated = isFlashFireActivated;
	}
}
