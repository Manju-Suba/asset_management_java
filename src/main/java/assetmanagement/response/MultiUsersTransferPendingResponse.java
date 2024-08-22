package assetmanagement.response;

import java.util.List;

import assetmanagement.dto.MultiUsersTransferDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultiUsersTransferPendingResponse {

    private long transferDetailsCount;
   private List<MultiUsersTransferDTO> transferDetails;
}
