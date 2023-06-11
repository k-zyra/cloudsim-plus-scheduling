package utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudsimplus.allocationpolicies.VmAllocationPolicy;
import org.cloudsimplus.allocationpolicies.VmAllocationPolicySimple;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.listeners.CloudletVmEventInfo;
import org.cloudsimplus.util.Log;
import org.cloudsimplus.vms.Vm;

import config.SimulationParams;

public class WSO_Simulation {
	public static final CloudSimPlus simulation = new CloudSimPlus();

	public static String algorithm;
	
	public static double maxWaitingTime = 0;
	public static double maxWaitToSizeRatio = 0;
	
	public static double totalVmsExecTime = 0;
	public static double totalCloudletsWaitingTime = 0;
	public static double totalWaitToSizeRatio = 0;
	
	public static Map<Long, Double> vmUtilization = new HashMap<Long, Double>();
	public static Map<Long, Integer> assignedTasks = new HashMap<Long, Integer>();
	
	public static List<Cloudlet> cloudletList;
    public static List<Vm> vmList;
    public static List<Host> hostList;
    
    public static Datacenter datacenter;
    public static DatacenterBrokerSimple broker;
    
    
    public void addCloudletDelay(int cloudletId, double delay) {
    	if (cloudletId < SimulationParams.NO_OF_CLOUDLETS) {
    		cloudletList.get(cloudletId).setStartupDelay(delay);
    	}    	
    }
    
    public void setCloudletOrder() {
    	cloudletList.sort(Comparator.comparingDouble(cloudlet -> cloudlet.getId()));
    }
    
    public void setVmOrder() {
    	broker.setVmComparator(Comparator.comparingDouble(vm -> vm.getId()));
    }
        
    
    public void submitNext(Vm vm) {
        if (!cloudletList.isEmpty()) {
        	int left = cloudletList.size();
        	Cloudlet cloudletToSend = cloudletList.remove(0);
	        	
	        System.out.printf("\tThere are still some cloudlets waiting [%d/%d]%n", 
	        		left, SimulationParams.NO_OF_CLOUDLETS);       

	        broker.bindCloudletToVm(cloudletToSend, vm);
	        broker.submitCloudlet(cloudletToSend);	        
        }
    }
    
    public void getCloudletStats(Cloudlet cloudlet, Vm vm) {
        double waitTime = cloudlet.getStartTime();
        double waitToExecRatio = cloudlet.getStartTime()/cloudlet.getTotalExecutionTime();
        
        if (waitTime > maxWaitingTime) {
        	maxWaitingTime = waitTime;
        }
        
        if (waitToExecRatio > maxWaitToSizeRatio) {
        	maxWaitToSizeRatio = waitToExecRatio;
        }
        
        totalVmsExecTime += cloudlet.getTotalExecutionTime();
        totalCloudletsWaitingTime += waitTime;
        totalWaitToSizeRatio += waitToExecRatio;
        
        double utilization = vmUtilization.get(vm.getId());
        utilization += cloudlet.getTotalExecutionTime();
    	vmUtilization.put(vm.getId(), utilization);
    	
    	int assigned = assignedTasks.get(vm.getId());
    	assigned += 1;
    	assignedTasks.put(vm.getId(), assigned);
    }
 
	public void update(CloudletVmEventInfo info) {
		var finishedCloudlet = info.getCloudlet();
		var freedVm = info.getVm();
		
        System.out.printf(
                "\t#EventListener: Cloudlet %d finished running at Vm %d at time %.2f%n",
                finishedCloudlet.getId(), freedVm.getId(), info.getTime());
        
        getCloudletStats(finishedCloudlet, freedVm);
        
        if (SimulationParams.DYNAMIC) {
        	submitNext(info.getVm());
        }        
	}
	
	public void notify(CloudletVmEventInfo info) {
        System.out.printf(
                "\t#EventListener: Cloudlet %d started running at Vm %d at time %.2f%n",
                info.getCloudlet().getId(), info.getVm().getId(), info.getTime());
	}
    
	public void setupSimulation() {
    	System.out.println("Setting up simulation...");
    	
    	int waiting = broker.getVmWaitingList().size();
    	List<Cloudlet> cloudletToSubmit = new ArrayList<Cloudlet>(waiting);
    
    	for (int i = 0; i < waiting; i++) {
    		cloudletToSubmit.add(cloudletList.remove(0));
    	}
    	
    	broker.submitCloudletList(cloudletToSubmit);
    }
    
	public void addOnStartedListeners() {
        for (Cloudlet cloudlet : cloudletList) {
        	cloudlet.addOnStartListener(this::notify);
        }
    }
    
	public void addOnFinishedListeners() {
        for (Cloudlet cloudlet : cloudletList) {
        	cloudlet.addOnFinishListener(this::update);
        }
    }
	
	public void getSummary() {
        final var cloudletAllocatedList = broker.getCloudletFinishedList();
        cloudletAllocatedList.sort(Comparator.comparingDouble(cloudlet -> cloudlet.getStartTime()));
        new CloudletsTableBuilder(cloudletAllocatedList)
            .setTitle("Cloudlets sorted by submission time:")
            .build();
        
        final var cloudletFinishedList = broker.getCloudletFinishedList();
        new CloudletsTableBuilder(cloudletFinishedList)
            .setTitle("Cloudlets sorted by finish time:")
            .build();
	}
	
	public void getStatistics() {
		var averageVmExecTime = totalVmsExecTime/SimulationParams.NO_OF_VMS;
		var averageCloudletWaitingTime = totalCloudletsWaitingTime/SimulationParams.NO_OF_CLOUDLETS;
		var averageWaitToSizeRatio = totalWaitToSizeRatio/SimulationParams.NO_OF_CLOUDLETS;
		
		System.out.println("Simulation statistics");
		System.out.printf(
                "\tAverage VM execution time: %.2f [s] %n", averageVmExecTime); 
        System.out.printf(
                "\tAverage cloudlet waiting time: %.2f [s] %n", averageCloudletWaitingTime);              
        System.out.printf(
                "\tAverage wait to size ratio: %.2f %n", averageWaitToSizeRatio);     
        
        System.out.printf(
                "%n\tMaximum cloudlet waiting time: %.2f [s] %n", maxWaitingTime);              
        System.out.printf(
                "\tMaximum wait to size ratio: %.2f%n", maxWaitToSizeRatio);   
	}
	
	public void getVmUtilizationTime() {
		System.out.printf("%nTotal utilization time:%n");
        for (Map.Entry<Long, Double> entry : vmUtilization.entrySet()) {
    		System.out.printf("\tVM%d: %.2f [s] %n", 
    				entry.getKey(), entry.getValue()); 
        }
	}
	
	public void getVmAssignedTasks() {
		System.out.printf("%nNumber of assigned tasks:%n");
        for (Map.Entry<Long, Integer> entry : assignedTasks.entrySet()) {
    		System.out.printf("\tVM%d: %d tasks%n", 
    				entry.getKey(), entry.getValue()); 
        }
	}
    
	@SuppressWarnings("unused")
	public void allocateVms() {
        VmAllocationPolicy allocationPolicy = new VmAllocationPolicySimple();
        allocationPolicy.setDatacenter(datacenter);
        
    	if (SimulationParams.NO_OF_VMS != SimulationParams.NO_OF_HOSTS) {
	        System.out.printf("Cannot allocate VMs to Hosts (VMs: %d, Hosts: %d)",
	        		SimulationParams.NO_OF_VMS, SimulationParams.NO_OF_HOSTS); 
    	} else {
        	for (int i = 0; i < SimulationParams.NO_OF_VMS; i++) {
        		allocationPolicy.allocateHostForVm(vmList.get(i), hostList.get(i));
        	}
        	
			for (Vm vm : vmList) {
				vmUtilization.put(vm.getId(), 0.0);
			}
			
			for (Vm vm : vmList) {
				assignedTasks.put(vm.getId(), 0);
			}
    	}
    }
	
	public void submitAllCloudlets() {
		broker.submitCloudletList(cloudletList);
	}
    		
	public WSO_Simulation(String algorithmName) {	
    	Log.setLevel(ch.qos.logback.classic.Level.WARN);
    	
    	algorithm = algorithmName;
    	System.out.println("Starting " + algorithm + " Dynamic Scheduler...");
    	    	
        try {
        	// Create hosts 
            hostList = HostCreator.createHosts();
            
        	// Create data center
            datacenter = DatacenterCreator.createDatacenter(simulation, "Datacenter_" + algorithm, hostList);

            // Create allocation policy
            VmAllocationPolicy allocationPolicy = new VmAllocationPolicySimple();
            allocationPolicy.setDatacenter(datacenter);
            
            // Create broker
            broker = BrokerCreator.createBroker(simulation, datacenter, "Broker_" + algorithm);
            
            // Create VMs and submit them to broker
            vmList = VmCreator.createVms();
            setVmOrder();
            broker.submitVmList(vmList);     
            allocateVms();            

            // Create tasks and submit them to broker
	        cloudletList = CloudletCreator.createCloudlets(SimulationParams.SHUFFLE_CLODULETS);
	        setCloudletOrder();
	        System.out.println(cloudletList);       
      
	        addOnStartedListeners();
	        addOnFinishedListeners();
	        
            if (SimulationParams.DYNAMIC) {
    	        setupSimulation();  
            } else {
            	submitAllCloudlets();
            }
	        
            // Start the simulation
            simulation.start();
            System.out.println(WSO_Simulation.class.getName() + " finished!");

            // Display results
            getSummary();   
            getStatistics();
            getVmUtilizationTime();
            getVmAssignedTasks();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("The simulation has been terminated due to an unexpected error");
        }
    }
    
    public static void main(String[] args) {
    	
    }

}