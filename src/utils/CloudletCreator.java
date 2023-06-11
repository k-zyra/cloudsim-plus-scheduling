package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.utilizationmodels.UtilizationModelDynamic;
import org.cloudsimplus.utilizationmodels.UtilizationModelFull;

import config.CloudletParams;
import config.SimulationParams;


public class CloudletCreator {
	
    @SuppressWarnings("unused")
	public static List<Cloudlet> createCloudlets(boolean shuffle) {
        final var list = new ArrayList<Cloudlet>(SimulationParams.NO_OF_CLOUDLETS);
    
        CloudletParams.generateShortCloudlets();
        CloudletParams.generateMediumCloudlets();
        CloudletParams.generateLongCloudlets();
        
        
        if (shuffle) {
        	Collections.shuffle(CloudletParams.LENGTH);
        }
        
        for (int id = 0; id < SimulationParams.NO_OF_CLOUDLETS; id++) {
            final var cloudlet =
                new CloudletSimple(id, CloudletParams.LENGTH.get(id), CloudletParams.NO_OF_PES)
                    .setFileSize(CloudletParams.INPUT_SIZE)
                    .setOutputSize(CloudletParams.OUTPUT_SIZE)
                    .setUtilizationModelCpu(new UtilizationModelFull())
                    .setUtilizationModelBw(new UtilizationModelDynamic(CloudletParams.RAM_UTILIZATION))
                    .setUtilizationModelRam(new UtilizationModelDynamic(CloudletParams.BW_UTILIZATION));

            if (SimulationParams.DELAY_CLOUDLETS) {
            	cloudlet.setSubmissionDelay((id % 25) * 10);
            }

            list.add(cloudlet);
        }
        
	    return list;
	}
}
