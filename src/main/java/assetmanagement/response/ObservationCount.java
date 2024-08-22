package assetmanagement.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObservationCount {

    private long ObservationCount;
    private List<ObservationResponse> ObservationData;

}
