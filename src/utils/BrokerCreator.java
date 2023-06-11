package utils;

import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.Datacenter;

public class BrokerCreator {

	public static DatacenterBrokerSimple createBroker(CloudSimPlus simulation, Datacenter dc, String name) {
        final var broker = new DatacenterBrokerSimple(simulation);
        broker.setName(name);
        broker.setLastSelectedDc(dc);
        return broker;
	}
	
}
