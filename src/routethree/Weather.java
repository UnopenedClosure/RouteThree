package routethree;
import java.util.ArrayList;

public enum Weather {
	NONE, RAIN, SUN, SANDSTORM, HAIL;
	
	public static Weather getWeatherFromName(String s) {
		for(Weather w : Weather.values()) {
			if("0".equals(s)) return NONE;
			
			if(w.toString().equalsIgnoreCase(s))
				return w;
		}
		
		return null;
	}

	public static ArrayList<Weather> getAllNoneWeathers() {
		return getUniqueWeathers(NONE);
	}
	
	public static ArrayList<Weather> getUniqueWeathers(Weather w) {
		if(w == null)
			return getAllNoneWeathers();
		
		ArrayList<Weather> list = new ArrayList<>();
		for(int i = 0; i < 6; i++)
			list.add(w);
		
		return list;
	}
}
