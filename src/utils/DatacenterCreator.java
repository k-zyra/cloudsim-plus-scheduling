package utils;

import java.util.List;

import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.Host;


public class DatacenterCreator {

    public static Datacenter createDatacenter(CloudSimPlus simulation, String name, List<Host> hostList) {    
    	DatacenterSimple dc = new DatacenterSimple(simulation, hostList);	
		dc.setName(name);
        return dc;
    }

}
