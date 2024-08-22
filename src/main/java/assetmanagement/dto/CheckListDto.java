package assetmanagement.dto;

import java.util.List;

import lombok.Data;

@Data
public class CheckListDto {

    private String id;
    private String assetClass;
    private List<String> checkList;
}
