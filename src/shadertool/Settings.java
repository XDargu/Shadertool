package shadertool;

public class Settings {
	public static boolean autoAsignInputs;
	public static boolean autoNameNodes;
	public static int version;
	
	public static Object[] getSettings() {
		Object[] settings = new Object[3];
		settings[0] = autoAsignInputs;
		settings[1] = autoNameNodes;
		return settings;
	}
	
	public static void setSettings(Object[] settings) {
		autoAsignInputs = (boolean)settings[0];
		autoNameNodes = (boolean)settings[0];
	}
}
