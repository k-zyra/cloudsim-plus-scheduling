package config;

import java.util.ArrayList;
import java.util.Random;

public class CloudletParams {
	private static final int SEED = 300392;
	
    public static final int NO_OF_PES = 1;					// number of processors
    public static final int INPUT_SIZE = 300;				// size of input file
    public static final int OUTPUT_SIZE = 300;				// size of output file
    public static final double RAM_UTILIZATION = 0.2;
	public static final double BW_UTILIZATION = 0.1;
	public static ArrayList<Integer> LENGTH = new ArrayList<Integer>();
	
	public static Random random = new Random(SEED);

    public static void generateShortCloudlets() {
    	for (int i = 0; i < SimulationParams.NO_OF_SHORT_CLODULETS; i++) {
    		var base = random.nextInt(10 - 1) + 1;
    		LENGTH.add(base * 1000);
    	}
    }
    
    public static void generateMediumCloudlets() {
    	for (int i = 0; i < SimulationParams.NO_OF_MEDIUM_CLODULETS; i++) {
    		var base = random.nextInt(10 - 1) + 1;
    		LENGTH.add(base * 10000);
    	}
    }
    
    public static void generateLongCloudlets() {
    	for (int i = 0; i < SimulationParams.NO_OF_LONG_CLODULETS; i++) {
    		var base = random.nextInt(10 - 1) + 1;
    		LENGTH.add(base * 1000000);
    	}
    }
}		
