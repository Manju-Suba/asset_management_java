package assetmanagement.response;

import java.util.List;

import assetmanagement.model.CheckList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckListResponse {

    private long checkListCount;
    private List<CheckList> checkListResponse;

}
