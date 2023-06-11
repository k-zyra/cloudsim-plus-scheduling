package utils;

import java.util.ArrayList;
import java.util.List;

import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;
import org.cloudsimplus.schedulers.cloudlet.*;

import config.SimulationParams;
import config.VmParams;


public class VmCreator {

    public static List<Vm> createVms() {
        final var vmList = new ArrayList<Vm>(SimulationParams.NO_OF_VMS);
        
        for (int id = 0; id < SimulationParams.NO_OF_VMS; id++) {
        	final var scheduler = new CloudletSchedulerSpaceShared();
            final var vm = new VmSimple(VmParams.MIPS[id], VmParams.NO_OF_PES);
            
            vm.setCloudletScheduler(scheduler);
            vm.setRam(VmParams.RAM)
            	.setBw(VmParams.BW)
            	.setSize(VmParams.SIZE)
            	.setId(id);
            
            vmList.add(vm);
        }

        return vmList;
    }
}
