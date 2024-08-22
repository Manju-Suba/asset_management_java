package assetmanagement.service.observation;


import assetmanagement.model.audit.Observation;
import assetmanagement.response.ObservationCount;

public interface ObservationService {

    Observation create(Observation observation);

    ObservationCount getList(String assetClass, boolean search,String value, Integer page, Integer size);

    ObservationCount getParticularList( String assetId, Integer page,Integer size);

}
