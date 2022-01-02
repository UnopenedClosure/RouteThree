
public enum Stat {
	HP(0), ATK(1), DEF(2), SPA(3), SPD(4), SPE(5);
	
	private final int id;
	
	private Stat(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}
