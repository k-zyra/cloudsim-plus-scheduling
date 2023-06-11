package FCFS;

import utils.WSO_Simulation;

public class FCFS_Simulation extends WSO_Simulation {

	FCFS_Simulation(String algorithmName) {
		super(algorithmName);
	}
	
    public static void main(String[] args) {
    	new FCFS_Simulation("FCFS");
    }

}