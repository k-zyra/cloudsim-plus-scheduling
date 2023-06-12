package MAXMIN;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.listeners.CloudletVmEventInfo;
import org.cloudsimplus.vms.Vm;

import config.SimulationParams;
import utils.WSO_Simulation;

public class MAXMIN_Simulation extends WSO_Simulation {
	
	MAXMIN_Simulation(String algorithmName) {
		super(algorithmName);		
	}
	
	@Override
    public void setCloudletOrder() {
		cloudletList.sort(Comparator.comparingDouble(cloudlet -> cloudlet.getLength()));
    }
	
	@Override
    public void setVmOrder() {
    	broker.setVmComparator(Comparator.comparingDouble(vm -> vm.getTotalExecutionTime()));
    }
	
	@Override
    public void submitNext(Vm vm) {
        if (!cloudletList.isEmpty()) {
        	Cloudlet cloudletToSend;
        	int left = cloudletList.size();
    	
	        System.out.printf("\tThere are still some cloudlets waiting [%d/%d]%n", 
	        		left, SimulationParams.NO_OF_CLOUDLETS);
	        
	        cloudletList.sort(Comparator.comparingDouble(cloudlet -> cloudlet.getLength()));
        	cloudletToSend = cloudletList.remove(0);
	        broker.bindCloudletToVm(cloudletToSend, vm);
	        broker.submitCloudlet(cloudletToSend);
	        System.out.printf("\tSending cloudlet %d to VM%d%n", 
	        		cloudletToSend.getId(), vm.getId());
        }
    }
	
	public void update(CloudletVmEventInfo info) {
		var freedVm = info.getVm();
		var finishedCloudlet = info.getCloudlet();

        System.out.printf(
                "\t#EventListener: Cloudlet %d finished running at Vm %d at time %.2f%n",
                finishedCloudlet.getId(), freedVm.getId(), info.getTime());

        getCloudletStats(finishedCloudlet, freedVm);

    	long minId = Collections.min(vmUtilization.entrySet(), Map.Entry.comparingByValue()).getKey();       
        Vm vmToSubmit = vmList.get((int)minId);
        submitNext(vmToSubmit);
	}
	
    public static void main(String[] args) {
    	new MAXMIN_Simulation("MAXMIN");
    }

}