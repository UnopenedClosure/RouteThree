import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

public class BattleOptions {
    private int participants = 1;
    private ArrayList<Integer> sxps = null;
    
    private ArrayList<Integer> xatks = null;
    private ArrayList<Integer> xdefs = null;
    private ArrayList<Integer> xspas = null;
    private ArrayList<Integer> xspds = null;
    private ArrayList<Integer> xspes = null;
    private ArrayList<Integer> xaccs = null;
    private ArrayList<Integer> xevas = null;
    private ArrayList<Status> xstatuses1 = null;
    private ArrayList<EnumMap<Status, Boolean>> xstatuses2_3 = null;
    
    private ArrayList<Integer> yatks = null;
    private ArrayList<Integer> ydefs = null;
    private ArrayList<Integer> yspas = null;
    private ArrayList<Integer> yspds = null;
    private ArrayList<Integer> yspes = null;
    private ArrayList<Integer> yaccs = null;
    private ArrayList<Integer> yevas = null;
    private ArrayList<Status> ystatuses1 = null;
    private ArrayList<EnumMap<Status, Boolean>> ystatuses2_3 = null;
    
    private ArrayList<Integer> order = null;
    private ArrayList<Weather> weathers = null;
    
    private boolean isBattleTower = false;
    private boolean isDoubleBattle = false;
    
    private boolean printStatRangesOnLvl = false;
    private boolean printStatsOnLvl = false;
    private StatModifier mod1;
    private StatModifier mod2;
    private int verbose = BattleOptions.NONE;
    //verbose options
    public static final int NONE = 0;
    public static final int SOME = 1;
    public static final int ALL = 2;
    public static final int EVERYTHING = 3;
    
    public BattleOptions() {
        setMod1(new StatModifier());
        setMod2(new StatModifier());
    }

	public int getParticipants() {
		return participants;
	}

	public ArrayList<Integer> getSxps() {
		return sxps;
	}

	public ArrayList<Integer> getXatks() {
		return xatks;
	}

	public ArrayList<Integer> getXdefs() {
		return xdefs;
	}

	public ArrayList<Integer> getXspas() {
		return xspas;
	}

	public ArrayList<Integer> getXspds() {
		return xspds;
	}

	public ArrayList<Integer> getXspes() {
		return xspes;
	}

	public ArrayList<Integer> getXaccs() {
		return xaccs;
	}

	public ArrayList<Integer> getXevas() {
		return xevas;
	}

	public ArrayList<Status> getXstatuses1(){
		return xstatuses1;
	}
	
	public ArrayList<EnumMap<Status, Boolean>> getXstatuses2_3(){
		return xstatuses2_3;
	}	
	
	public ArrayList<Integer> getYatks() {
		return yatks;
	}

	public ArrayList<Integer> getYdefs() {
		return ydefs;
	}

	public ArrayList<Integer> getYspas() {
		return yspas;
	}

	public ArrayList<Integer> getYspds() {
		return yspds;
	}

	public ArrayList<Integer> getYspes() {
		return yspes;
	}

	public ArrayList<Integer> getYaccs() {
		return yaccs;
	}

	public ArrayList<Integer> getYevas() {
		return yevas;
	}

	public ArrayList<Status> getYstatuses1(){
		return ystatuses1;
	}
	
	public ArrayList<EnumMap<Status, Boolean>> getYstatuses2_3(){
		return ystatuses2_3;
	}

	public ArrayList<Integer> getOrder() {
		return order;
	}

	public ArrayList<Weather> getWeathers() {
		return weathers;
	}

	public boolean isPrintStatRangesOnLvl() {
		return printStatRangesOnLvl;
	}

	public boolean isPrintStatsOnLvl() {
		return printStatsOnLvl;
	}

	public StatModifier getMod1() {
		return mod1;
	}

	public StatModifier getMod2() {
		return mod2;
	}

	public int getVerbose() {
		return verbose;
	}

	public void setParticipants(int participants) {
		this.participants = participants;
	}

	public void setSxps(ArrayList<Integer> sxps) {
		this.sxps = new ArrayList<Integer>(sxps);
	}

	public void setXatks(ArrayList<Integer> xatks) {
		this.xatks = new ArrayList<Integer>(xatks);
	}

	public void setXdefs(ArrayList<Integer> xdefs) {
		this.xdefs = new ArrayList<Integer>(xdefs);
	}

	public void setXspas(ArrayList<Integer> xspas) {
		this.xspas = new ArrayList<Integer>(xspas);
	}

	public void setXspds(ArrayList<Integer> xspds) {
		this.xspds = new ArrayList<Integer>(xspds);
	}

	public void setXspes(ArrayList<Integer> xspes) {
		this.xspes = new ArrayList<Integer>(xspes);
	}

	public void setXaccs(ArrayList<Integer> xaccs) {
		this.xaccs = new ArrayList<Integer>(xaccs);
	}

	public void setXevas(ArrayList<Integer> xevas) {
		this.xevas = new ArrayList<Integer>(xevas);
	}

	public void setXstatuses1(ArrayList<Status> statuses1) {
		this.xstatuses1 = new ArrayList<Status>(statuses1);
	}

	public void setXstatuses2_3(ArrayList<EnumMap<Status, Boolean>> xstatuses2_3) {
		this.xstatuses2_3 = xstatuses2_3;
	}

	public void setYatks(ArrayList<Integer> yatks) {
		this.yatks = new ArrayList<Integer>(yatks);
	}

	public void setYdefs(ArrayList<Integer> ydefs) {
		this.ydefs = new ArrayList<Integer>(ydefs);
	}

	public void setYspas(ArrayList<Integer> yspas) {
		this.yspas = new ArrayList<Integer>(yspas);
	}

	public void setYspds(ArrayList<Integer> yspds) {
		this.yspds = new ArrayList<Integer>(yspds);
	}

	public void setYspes(ArrayList<Integer> yspes) {
		this.yspes = new ArrayList<Integer>(yspes);
	}

	public void setYaccs(ArrayList<Integer> yaccs) {
		this.yaccs = new ArrayList<Integer>(yaccs);
	}

	public void setYevas(ArrayList<Integer> yevas) {
		this.yevas = new ArrayList<Integer>(yevas);
	}
	
	public void setYstatuses1(ArrayList<Status> statuses1) {
		this.ystatuses1 = new ArrayList<Status>(statuses1);
	}

	public void setYstatuses2_3(ArrayList<EnumMap<Status, Boolean>> ystatuses2_3) {
		this.ystatuses2_3 = ystatuses2_3;
	}

	public void setOrder(ArrayList<Integer> order) {
		this.order = new ArrayList<Integer>(order);
	}
	
	public void setWeathers(Weather weather) {
		this.weathers = Weather.getUniqueWeathers(weather);
	}

	public void setWeathers(ArrayList<Weather> weathers) {
		this.weathers = new ArrayList<Weather>(weathers);
	}

	public void setPrintStatRangesOnLvl(boolean printStatRangesOnLvl) {
		this.printStatRangesOnLvl = printStatRangesOnLvl;
	}

	public void setPrintStatsOnLvl(boolean printStatsOnLv) {
		this.printStatsOnLvl = printStatsOnLv;
	}

	public void setMod1(StatModifier mod1) {
		this.mod1 = mod1;
	}

	public void setMod2(StatModifier mod2) {
		this.mod2 = mod2;
	}

	public void setVerbose(int verbose) {
		this.verbose = verbose;
	}

	public boolean isBattleTower() {
		return isBattleTower;
	}

	public void setBattleTower(boolean isBattleTower) {
		this.isBattleTower = isBattleTower;
	}

	public boolean isDoubleBattle() {
		return isDoubleBattle;
	}

	public void setDoubleBattle(boolean isDoubleBattle) {
		this.isDoubleBattle = isDoubleBattle;
	}
}
