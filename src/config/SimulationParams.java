package config;

public class SimulationParams {
	public static final boolean DYNAMIC = false;
	public static final boolean ENABLE_TRACING = false;
	public static final boolean DELAY_CLOUDLETS = false;
	public static final boolean SHUFFLE_CLODULETS = false;
	
    public static final int NO_OF_DATA_CENTERS = 1;
    public static final int NO_OF_HOSTS = 5;
    public static final int NO_OF_USERS = 1;
    public static final int NO_OF_VMS = 5;	
    
	public static final int NO_OF_SHORT_CLODULETS = 25;
	public static final int NO_OF_MEDIUM_CLODULETS = 50;
	public static final int NO_OF_LONG_CLODULETS = 25;
	public static final int NO_OF_CLOUDLETS = 
			NO_OF_SHORT_CLODULETS + NO_OF_MEDIUM_CLODULETS + NO_OF_LONG_CLODULETS;
}
