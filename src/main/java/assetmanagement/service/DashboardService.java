package assetmanagement.service;

import assetmanagement.dto.RejectAsstCount;
import assetmanagement.model.Asset;
import assetmanagement.response.MajorMinorAssetsResponse;

import java.util.List;
import java.util.Map;

public interface DashboardService {

    Map<String, Object> getAssetsCounts();

    Map<String, Object> getDamagedAssetsCountByYearAndMonth(Integer year);

    List<Asset> getRecentlyPurchasedAssets();

    Map<String, Object> getAssetCategoryCounts();

    Map<String, Long> totalDamagedRejectedAssets();

    List<Map<String, Object>> assetTypeCount(Integer page, Integer size);

    Map<String, Long> overallAsset();

    RejectAsstCount getRejectedAssets(Integer page, Integer size, boolean search, String value);

    List<Map<String, Object>> assetsCount(Integer page, Integer size, Boolean search, String value);

    MajorMinorAssetsResponse getMajorAndMinorAsset(Integer page, Integer size, Boolean search, String value);
}
