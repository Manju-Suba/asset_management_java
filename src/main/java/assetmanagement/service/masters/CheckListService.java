package assetmanagement.service.masters;

import assetmanagement.dto.CheckListDto;
import assetmanagement.model.CheckList;
import assetmanagement.response.CheckListResponse;


public interface CheckListService {

    CheckList saveCheckList(CheckListDto checkListDto);

    CheckListResponse getCheckList(Integer page, Integer size, Boolean search, String value, String assetClass);

    CheckList update(CheckListDto checkListDto);

    CheckList delete(String id);

    CheckList getCheckListByAssetClass(String assetClass);

    CheckList getByAssetClass(String assetClass, String companyId, String plant);

    CheckList getCheckListToAudit(String assetClass, String plant, String companyId);
}
