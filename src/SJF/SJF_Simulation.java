package SJF;

import java.util.Comparator;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.vms.Vm;

import config.SimulationParams;
import utils.WSO_Simulation;

public class SJF_Simulation extends WSO_Simulation {

	SJF_Simulation(String algorithmName) {
		super(algorithmName);
	}
	
	@Override
    public void setCloudletOrder() {
		cloudletList.sort(Comparator.comparingDouble(cloudlet -> cloudlet.getLength()));
    }
	
	@Override
    public void submitNext(Vm vm) {
        if (!cloudletList.isEmpty()) {
        	int left = cloudletList.size();
        	cloudletList.sort(Comparator.comparingDouble(cloudlet -> cloudlet.getLength()));
        	Cloudlet cloudletToSend = cloudletList.remove(0);
	        	
	        System.out.printf("\tThere are still some cloudlets waiting [%d/%d]%n", 
	        		left, SimulationParams.NO_OF_CLOUDLETS);       

	        broker.bindCloudletToVm(cloudletToSend, vm);
	        broker.submitCloudlet(cloudletToSend);	        
        }
    }
	
    public static void main(String[] args) {
    	new SJF_Simulation("SJF");
    }

}