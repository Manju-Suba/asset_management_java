package assetmanagement.response;


import assetmanagement.dto.TransferDetailDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferPendingResponse {
    private long transferDetailsCount;
    private List<TransferDetailDto> transferDetailDto;
}
