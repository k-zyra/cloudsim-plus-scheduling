package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.PeSimple;

import config.HostParams;
import config.SimulationParams;


public class HostCreator {

    public static List<Host> createHosts() {
    	final var list = new ArrayList<Host>(SimulationParams.NO_OF_HOSTS);
        final var peList = new ArrayList<Pe>(HostParams.NO_OF_PES);
        IntStream.range(0, HostParams.NO_OF_PES).forEach(i -> peList.add(new PeSimple(HostParams.MIPS)));
    	        
        for (int id = 0; id < SimulationParams.NO_OF_HOSTS; id++) {
	      final var host = new HostSimple(HostParams.RAM, HostParams.BW, HostParams.STORAGE, peList);
	      list.add(host);
      	}
	
      return list;
    }

}
