package routethree;

public class IgnoreCaseString {
	private String original;
	private String comparisonString;
	
	public IgnoreCaseString(String s) {
		this.original = s;
		this.comparisonString = tighten(s);
	}
	
	private static String tighten(String s) {
		return s.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
	}
	
	public String getComparisonString() {
		return comparisonString;
	}
	
	public String getOriginalString() {
		return original;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (getComparisonString() == null || o == null)
			return false;
		if (o.getClass() == String.class) {
			String os = (String) o;
			return getComparisonString().equalsIgnoreCase(tighten(os));
		}
		if (getClass() == o.getClass()) {
			IgnoreCaseString cs = (IgnoreCaseString) o;
			return getComparisonString().equalsIgnoreCase(cs.getComparisonString());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return getComparisonString().hashCode();
	}
	
	@Override
	public String toString() {
		return getComparisonString();
	}
	
	// test
	@SuppressWarnings("unlikely-arg-type")
	public static void main(String[] args) {
		IgnoreCaseString moveStr = new IgnoreCaseString("MUD_SLAP");
		System.out.println(moveStr.equals("MUDSLAP"));
	}
	
}
