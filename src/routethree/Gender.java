package routethree;

public enum Gender {
	GENDERLESS(255),
	FEMALE(254),
	FEMALE_75(190),
	FEMALE_50(127),
	FEMALE_25(63),
	FEMALE_12_5(31),
	MALE(0);
	
	private final int value;
	
	private Gender(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
}
