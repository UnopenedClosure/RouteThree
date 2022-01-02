public class IVs {
	public static final int MIN = 0;
	public static final int MAX = 31;
	public static final int RANGE = MAX - MIN + 1 ;


    private int hp;
    private int atk;
    private int def;
    private int spa;
    private int spd;
    private int spe;
    
    public IVs() {
        hp = atk = def = spa = spd = spe = MAX;
    }
    
    // For Trainer Pokes
    public IVs(int fixedIV) {
    	hp = atk = def = spa = spd = spe = fixedIV;
    }
    
    public IVs(int newHP, int newAtk, int newDef, int newSpa, int newSpd, int newSpe) {
        hp = newHP;
    	atk = newAtk;
        def = newDef;
        spa = newSpa;
        spd = newSpd;
        spe = newSpe;
    }
    
    public int getHPIV() {
        return hp;
    }
    public int getAtkIV() {
        return atk;
    }
    public int getDefIV() {
        return def;
    }
    public int getSpaIV() {
        return spa;
    }
    public int getSpdIV() {
        return spd;
    }
    public int getSpeIV() {
        return spe;
    }
    
    public Type getHiddenPowerType() {
    	int _hp = hp & 1;
    	int _atk = atk & 1;
    	int _def = def & 1;
    	int _spe = spe & 1;
    	int _spa = spa & 1;
    	int _spd = spd & 1;
    	
    	int typeId = (_hp + 2*_atk + 4*_def + 8*_spe + 16*_spa + 32*_spd) * 15 / 63;
    	return Type.getHiddenPowerType(typeId);
    }
    
    public int getHiddenPowerPower() {
    	int _hp = (hp>>1) & 1;
    	int _atk = (atk>>1) & 1;
    	int _def = (def>>1) & 1;
    	int _spe = (spe>>1) & 1;
    	int _spa = (spa>>1) & 1;
    	int _spd = (spd>>1) & 1;
    	
    	int power = (_hp + 2*_atk + 4*_def + 8*_spe + 16*_spa + 32*_spd) * 40 / 63 + 30 ;
    	return power;
    }
}
