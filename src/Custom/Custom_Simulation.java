package Custom;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.vms.Vm;

import config.SimulationParams;
import utils.WSO_Simulation;

public class Custom_Simulation extends WSO_Simulation {
	private static boolean shortest = false;
	private static int round = 1;
	public static List<Boolean> vmUsed;

	Custom_Simulation(String algorithmName) {
		super(algorithmName);
		
	}	
	
	@Override
    public void setCloudletOrder() {
		cloudletList.sort(Comparator.comparingDouble(cloudlet -> cloudlet.getLength()));
    }
	
	@Override
	public void submitAllCloudlets() {
    	System.out.println("Setting up simulation...");
    	
    	List<Cloudlet> cloudletToSubmit = new ArrayList<Cloudlet>(SimulationParams.NO_OF_CLOUDLETS);
    	Cloudlet cloudletToSend;
    	int vmId = 0;
    
    	for (int i = 0; i < SimulationParams.NO_OF_CLOUDLETS; i++) {
	        if (round < 15) {
	        	if (shortest) {
		        	cloudletToSend = cloudletList.remove(0);
		        } else {
		        	cloudletToSend = cloudletList.remove(cloudletList.size() - 1);
		        }
	        } else {
	        	int middleIndex = cloudletList.size()/2;
	        	cloudletToSend = cloudletList.remove(middleIndex);
	        }
	        
	        cloudletToSubmit.add(cloudletToSend);
	        broker.bindCloudletToVm(cloudletToSend, vmList.get(vmId));
	        
	        round += round;
	        shortest = !shortest;
	        
	        if (vmId < 4) {
	        	vmId += 1;
	        } else {
	        	vmId = 0;
	        }
    	}
    	
    	System.out.println("cloudlety do wyslania");
    	System.out.println(cloudletToSubmit);
    	broker.submitCloudletList(cloudletToSubmit);
    }
	
	@Override
    public void submitNext(Vm vm) {
        if (!cloudletList.isEmpty()) {
        	Cloudlet cloudletToSend;
        	int left = cloudletList.size();
        		        	
	        System.out.printf("\tThere are still some cloudlets waiting [%d/%d]%n", 
	        		left, SimulationParams.NO_OF_CLOUDLETS);
	        
	        if (round < 15) {
	        	if (shortest) {
		        	cloudletToSend = cloudletList.remove(0);
		        } else {
		        	cloudletToSend = cloudletList.remove(cloudletList.size() - 1);
		        }
	        } else {
	        	int middleIndex = left/2;
	        	cloudletToSend = cloudletList.remove(middleIndex);
	        }

	        round += round;
	        shortest = !shortest;
	        
	        broker.bindCloudletToVm(cloudletToSend, vm);
	        broker.submitCloudlet(cloudletToSend);
        }
    }
 
	
    public static void main(String[] args) {
    	new Custom_Simulation("Custom");
    }

}