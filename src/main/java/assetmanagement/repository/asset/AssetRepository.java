package assetmanagement.repository.asset;

import assetmanagement.model.Asset;
import assetmanagement.response.AssetResponse;
import assetmanagement.response.AuditResponse;
import assetmanagement.response.DisposedResponse;
import jakarta.annotation.Nullable;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends MongoRepository<Asset, String> {

        List<Asset> findByCompanyIdAndStatus(String companyId, String status, Sort sortByDescId);

        long countByStatusAndCompanyIdAndPlant(String status, String companyId, String plant);

        Page<Asset> findByCompanyIdAndPlantAndStatus(String companyId, String plant, String status, Pageable pageable);

        List<Asset> findByCompanyIdAndPlantAndStatus(String companyId, String plant, String status);

        boolean existsByAssetId(String assetId);

        List<Asset> findByCompanyIdAndPlantAndStatusAndAvailableStatusAndRenewStatus(String companyId, String plant,
                        String status, String availableStatus, String renewableStatus);

        Page<Asset> findByCompanyIdAndPlantAndStatusAndAvailableStatusAndRenewStatus(String companyId, String plant,
                        String status, String availableStatus, String renewableStatus, Pageable pageable);

        long countByCompanyIdAndPlantAndStatusAndAvailableStatusAndRenewStatus(String companyId, String plant,
                        String status, String availableStatus, String renewableStatus);

        Page<Asset> findByCompanyIdAndPlantAndStatusAndAvailableStatusAndRenewStatusAndAssetClass(String companyId,
                        String plant,
                        String status, String availableStatus, String renewableStatus, String assetClass,
                        Pageable pageable);

        Page<Asset> findByCompanyIdAndPlantAndStatusAndAvailableStatusAndRenewStatusAndAssetClassAndAssetId(
                        String companyId, String plant,
                        String status, String availableStatus, String renewableStatus, String assetClass,
                        String assetId, Pageable pageable);

        long countByCompanyIdAndPlantAndStatusAndAvailableStatusAndRenewStatusAndAssetClassAndAssetId(String companyId,
                        String plant,
                        String status, String availableStatus, String renewableStatus, String assetClass,
                        String assetId);

        long countByCompanyIdAndPlantAndStatusAndAvailableStatusAndRenewStatusAndAssetClassAndAssetIdAndChildId(
                        String companyId, String plant,
                        String status, String availableStatus, String renewableStatus, String assetClass,
                        String assetId, String childId);

        long countByCompanyIdAndPlantAndStatusAndAvailableStatusAndRenewStatusAndAssetClass(String companyId,
                        String plant,
                        String status, String availableStatus, String renewableStatus, String assetClass);

        Page<Asset> findByCompanyIdAndPlantAndStatusAndAvailableStatusAndRenewStatusAndAssetClassAndAssetIdAndChildId(
                        String companyId, String plant,
                        String status, String availableStatus, String renewableStatus, String assetClass,
                        String assetId, String childId, Pageable pageable);

        // Page<Asset> findByCompanyIdAndPlantAndStatus(String companyId, String plant,
        // String status, Pageable pageable);

        // List<Asset> findByCompanyIdAndPlantAndStatus(String companyId, String plant,
        // String status);

        // boolean existsByAssetId(String assetId);

        // List<Asset>
        // findByCompanyIdAndPlantAndStatusAndAvailableStatusAndRenewStatus(String
        // companyId, String plant,
        // String status, String availableStatus, String renewableStatus);

        // Page<Asset>
        // findByCompanyIdAndPlantAndStatusAndAvailableStatusAndRenewStatus(String
        // companyId, String plant,
        // String status, String availableStatus, String renewableStatus, Pageable
        // pageable);

        // long countByCompanyIdAndPlantAndStatusAndAvailableStatusAndRenewStatus(String
        // companyId, String plant,
        // String status, String availableStatus, String renewableStatus);

        // Page<Asset>
        // findByCompanyIdAndPlantAndStatusAndAvailableStatusAndRenewStatusAndAssetClass(String
        // companyId, String plant,
        // String status, String availableStatus, String renewableStatus, String
        // assetClass, Pageable pageable);

        Page<Asset> findByCompanyIdAndPlantAndStatusAndAvailableStatusAndRenewStatusAndAssetClassAndSubClass(
                        String companyId, String plant,
                        String status, String availableStatus, String renewableStatus, String assetClass,
                        String subClass, Pageable pageable);

        long countByCompanyIdAndPlantAndStatusAndAvailableStatusAndRenewStatusAndAssetClassAndSubClass(String companyId,
                        String plant,
                        String status, String availableStatus, String renewableStatus, String assetClass,
                        String subClass);

        // long
        // countByCompanyIdAndPlantAndStatusAndAvailableStatusAndRenewStatusAndAssetClass(String
        // companyId, String plant,
        // String status, String availableStatus, String renewableStatus, String
        // assetClass);

        List<Asset> findByCompanyIdAndPlantAndStatusAndAvailableStatus(String companyId, String plant, String status,
                        String availableStatus, Sort sortByDescId);

        Page<Asset> findByCompanyIdAndPlantAndStatusAndAvailableStatus(String companyId, String plant, String status,
                        String availableStatus, Pageable pageable);

        // get-all without available status
        List<Asset> findByCompanyIdAndPlantAndStatusAndAssetClassAndAssetStatus(String companyId, String plant,
                        String status, String assetClass, String assetStatus, Sort sortByDescId);

        Page<Asset> findByCompanyIdAndPlantAndStatusAndAssetClassAndAssetStatus(String companyId, String plant,
                        String status, String assetClass, String assetStatus, Pageable pageable);

        long countByCompanyIdAndPlantAndStatusAndAssetClassAndAssetStatus(String companyId, String plant,
                        String status, String assetClass, String assetStatus);

        List<Asset> findByCompanyIdAndPlantAndStatusAndAssetClass(String companyId, String plant, String status,
                        String assetClass, Sort sortByDescId);

        Page<Asset> findByCompanyIdAndPlantAndStatusAndAssetClass(String companyId, String plant, String status,
                        String assetClass, Pageable pageable);

        List<Asset> findByCompanyIdAndPlantAndStatusAndAssetStatus(String companyId, String plant, String status,
                        String assetStatus, Sort sortByDescId);

        Page<Asset> findByCompanyIdAndPlantAndStatusAndAssetStatus(String companyId, String plant, String status,
                        String assetStatus, Pageable pageable);

        Page<Asset> findByCompanyIdAndPlantAndStatusAndSubClassAndAssetClass(String companyId, String plant,
                        String status, String subClass, String assetClass, Pageable pageable);

        Page<Asset> findByCompanyIdAndPlantAndStatusAndAssetStatusAndAssetClassAndSubClass(String companyId,
                        String plant, String status, String assetStatus, String assetClass, String subClass,
                        Pageable pageable);

        long countByCompanyIdAndPlantAndStatusAndAssetStatusAndAssetClassAndSubClass(String companyId, String plant,
                        String status, String assetStatus, String assetClass, String subClass);

        long countByCompanyIdAndPlantAndStatusAndAssetStatus(String companyId, String plant, String status,
                        String assetStatus);

        long countByCompanyIdAndPlantAndStatusAndSubClassAndAssetClass(String companyId, String plant, String status,
                        String subClass, String assetClass);

        List<Asset> findByCompanyIdAndPlantAndStatus(String companyId, String plant, String status, Sort sortByDescId);

        // ---//
        List<Asset> findByCompanyIdAndPlantAndStatusAndAssetClassAndAssetId(String companyId, String plant,
                        String status,
                        String assetClass, String assetId, Sort sortByDescId);

        List<Asset> findByCompanyIdAndPlantAndStatusAndAssetClassAndAssetStatusAndAssetId(String companyId,
                        String plant,
                        String status, String assetClass, String assetType, String assetId, Sort sortByDescId);

        // Page<Asset>
        // findByAssetClassAndAvailableStatusAndCompanyIdAndPlantAndStatus(String
        // assetClass,String availableStatus, String companyId, String plant, String
        // status, Pageable pageable);

        Page<Asset> findByAssetClassAndAssetIdAndChildIdAndAvailableStatusAndCompanyIdAndPlantAndStatus(
                        String assetClass, String assetId, String childId,
                        String availableStatus, String companyId, String plant, String status, Pageable pageable);

        Page<Asset> findByAssetClassAndAssetIdAndAvailableStatusAndCompanyIdAndPlantAndStatus(String assetClass,
                        String assetId,
                        String availableStatus, String companyId, String plant, String status, Pageable pageable);

        long countByAssetClassAndAvailableStatusAndCompanyIdAndPlantAndStatus(String assetClass,
                        String availableStatus, String companyId, String plant, String status);

        List<AuditResponse> findByCompanyIdAndStatusAndAuditDateAndAssetCategory(String companyId, String status,
                        @Nullable LocalDate auditDate, String assetCategoryId);

        List<Asset> findByCompanyIdAndPlantAndStatusAndAvailableStatusAndAssetClass(String companyId, String plant,
                        String status, String availableStatus, String assetClass, Sort sortByDescId);

        Page<Asset> findByCompanyIdAndPlantAndStatusAndAvailableStatusAndAssetClass(String companyId, String plant,
                        String status, String availableStatus, String assetClass, Pageable pageable);

        long countByCompanyIdAndPlantAndStatusAndAvailableStatusAndAssetClass(String companyId, String plant,
                        String status, String availableStatus, String assetClass);

        List<Asset> findByCompanyIdAndPlantAndStatusAndAvailableStatusAndAssetClassAndAssetStatus(String companyId,
                        String plant, String status, String availableStatus, String assetClass, String assetStatus,
                        Sort sortByDescId);

        Page<Asset> findByCompanyIdAndPlantAndStatusAndAvailableStatusAndAssetClassAndAssetStatus(String companyId,
                        String plant, String status, String availableStatus, String assetClass, String assetStatus,
                        Pageable pageable);

        Page<Asset> findByCompanyIdAndPlantAndStatusAndAvailableStatusAndSubClass(String companyId,
                        String plant, String status, String availableStatus, String subClass,
                        Pageable pageable);

        long countByCompanyIdAndPlantAndStatusAndAvailableStatusAndSubClass(String companyId, String plant,
                        String status, String availableStatus, String subClass);

        long countByCompanyIdAndPlantAndStatusAndAvailableStatusAndAssetClassAndAssetStatus(String companyId,
                        String plant, String status, String availableStatus, String assetClass, String assetStatus);

        List<Asset> findByCompanyIdAndPlantAndStatusAndAssetStatus(String companyId, String plant, String status,
                        String assetStatus);

        List<AuditResponse> findByCompanyIdAndStatusAndAuditDateAndAssetCategoryAndAssetType(String companyId,
                        String status, @Nullable LocalDate auditDate, String assetCategoryId, String assetTypeId);

        List<AuditResponse> findByCompanyIdAndStatusAndAuditDateIsNotNull(String companyId, String status);

        List<AuditResponse> findByCompanyIdAndStatusAndAuditDateIsNotNullAndAssetCategory(String companyId,
                        String status,
                        String assetCategoryId);

        List<AuditResponse> findByCompanyIdAndStatusAndAuditDateIsNotNullAndAssetCategoryAndAssetType(String companyId,
                        String status, String assetCategoryId, String assetTypeId);

        List<AssetResponse> findAllByCompanyIdAndStatus(String companyId, String status, Sort sortByDescId);

        Optional<Asset> findByIdAndCompanyIdAndStatus(String id, String companyId, String status);

        Optional<Asset> findByIdAndStatus(String id, String status);

        long countByCompanyIdAndPlantAndAssetClassAndAvailableStatus(String companyId, String plant, String assetClass,
                        String availableStatus);

        long countByCompanyIdAndPlantAndAssetClassAndAssetStatus(String companyId, String plant, String assetClass,
                        String assetStatus);

        long countByCompanyIdAndPlantAndAvailableStatus(String companyId, String plant, String availableStatus);

        long countByAvailableStatusAndAssetTypeId(String status, String assetTypeId);

        List<Asset> findByAvailableStatusAndCompanyIdAndPlantAndStatus(String availableStatus, String companyId,
                        String plant, String status, Sort sortByDescId);

        Page<Asset> findByAvailableStatusAndCompanyIdAndPlantAndStatus(String availableStatus, String companyId,
                        String plant, String status, Pageable pageable);

        long countByAvailableStatusAndCompanyIdAndPlantAndStatus(String availableStatus, String companyId,
                        String plant, String status);

        long countByCompanyIdAndPlant(String companyId, String plant);

        List<Asset> findByAssetClassAndAvailableStatusAndCompanyIdAndPlantAndStatus(String assetClass,
                        String availableStatus, String companyId, String plant, String status, Sort sortByDescId);

        Page<Asset> findByAssetClassAndAvailableStatusAndCompanyIdAndPlantAndStatus(String assetClass,
                        String availableStatus, String companyId, String plant, String status, Pageable pageable);

        Page<Asset> findByAssetClassAndSubClassAndAvailableStatusAndCompanyIdAndPlantAndStatus(String assetClass,
                        String subClass,
                        String availableStatus, String companyId, String plant, String status, Pageable pageable);

        // Optional<Object> findByAssetId(String id);
        // Optional<Asset> findByAssetId(String assetId);
        Optional<Asset> findById(ObjectId objectId);


        List<Asset> findByAssetIdAndPlant(String assetId, String plant);

        Object countByCompanyIdAndPlantAndAssetTypeIdAndAvailableStatus(String companyId, String plant, String id,
                        String status);

        List<Asset> findByAvailableStatus(String availableStatus);

        long countByCompanyIdAndPlantAndStatus(String companyId, String plant, String status);

        long countByCompanyIdAndPlantAndStatusAndAssetClass(String companyId, String plant, String status,
                        String assetClass);

        long countByCompanyIdAndPlantAndStatusAndAvailableStatus(String companyId, String plant, String status,
                        String availableStatus);

        long countByCompanyIdAndPlantAndStatusAndAvailableStatusAndUpdatedAtBetween(String companyId, String plant,
                        String status, String availableStatus, LocalDate start, LocalDate end);

        List<Asset> findByCompanyIdAndStatusAndAvailableStatus(String companyId, String status, String availableStatus);

        Page<Asset> findByCompanyIdAndStatusAndAvailableStatus(String companyId, String status, String availableStatus,
                        Pageable pageable);

        long countByCompanyIdAndStatusAndAvailableStatus(String companyId, String status, String availableStatus);

        List<Asset> findByCompanyIdAndStatusAndAvailableStatusAndTemporary(String companyId, String status,
                        String availableStatus, String temporary);

        List<Asset> findByCompanyIdAndStatusAndAvailableStatusAndAssetCategory(String companyId, String status,
                        String availableStatus, String assetCategoryId);

        Page<Asset> findByCompanyIdAndStatusAndAvailableStatusAndAssetClass(String companyId, String status,
                        String availableStatus, String assetCategoryId, Pageable pageable);

        Page<Asset> findByCompanyIdAndStatusAndAvailableStatusAndAssetStatus(String companyId, String status,
                        String availableStatus, String assetStatus, Pageable pageable);

        long countByCompanyIdAndStatusAndAvailableStatusAndAssetClass(String companyId, String status,
                        String availableStatus, String assetCategoryId);

        List<Asset> findByCompanyIdAndStatusAndAvailableStatusAndAssetCategoryAndTemporary(String companyId,
                        String status,
                        String availableStatus, String assetCategoryId, String temporary);

        List<Asset> findByCompanyIdAndStatusAndAvailableStatusAndAssetCategoryAndAssetType(String companyId,
                        String status,
                        String availableStatus, String assetCategoryId, String assetTypeId);

        Page<Asset> findByCompanyIdAndStatusAndAvailableStatusAndAssetClassAndAssetStatus(String companyId,
                        String status,
                        String availableStatus, String assetCategoryId, String assetStatus, Pageable pageable);

        Page<Asset> findByCompanyIdAndStatusAndAvailableStatusAndAssetClassAndSubClass(String companyId,
                        String status,
                        String availableStatus, String assetCategoryId, String subClass, Pageable pageable);

        Page<Asset> findByCompanyIdAndStatusAndAvailableStatusAndAssetClassAndAssetStatusAndSubClass(String companyId,
                        String status,
                        String availableStatus, String assetCategoryId, String assetStatus, String subClass,
                        Pageable pageable);

        long countByCompanyIdAndStatusAndAvailableStatusAndAssetClassAndAssetType(String companyId,
                        String status,
                        String availableStatus, String assetCategoryId, String assetTypeId);

        List<Asset> findByCompanyIdAndStatusAndAvailableStatusAndAssetCategoryAndAssetTypeAndTemporary(String companyId,
                        String status, String availableStatus, String assetCategoryId, String assetTypeId,
                        String temporary);

        List<Asset> findByCompanyIdAndStatusAndAssetCategory(String companyId, String status, String categoryId);

        List<Asset> findByCompanyIdAndStatusAndAssetCategoryAndExpiryDateBefore(String companyId, String status,
                        String categoryId, LocalDate today);

        List<Asset> findByCompanyIdAndStatusAndAssetCategoryAndExpiryDateBetween(String companyId, String status,
                        String categoryId, LocalDate starDate, LocalDate endDate);

        List<Asset> findByExpiryDateBetween(LocalDate starDate, LocalDate endDate);

        List<AssetResponse> findByLocationIdAndCompanyIdAndStatus(String locationId, String companyId, String status,
                        Sort sort);

        List<AssetResponse> findByAssetTypeIdAndCompanyIdAndStatus(String assetTypeId, String companyId, String status,
                        Sort sort);

        List<AuditResponse> findByCompanyIdAndStatusNotAndAuditDateNotNull(String companyId, String value);

        List<AuditResponse> findByCompanyIdAndStatusNotAndAuditDateNotNullAndAssetCategory(String companyId,
                        String value,
                        String assetCategory);

        List<AuditResponse> findByCompanyIdAndStatusNotAndAuditDateNotNullAndAssetCategoryAndAssetType(String companyId,
                        String value, String assetCategory, String assetType);

        AuditResponse findByIdAndCompanyIdAndStatusNotAndAuditDateNotNull(String id, String companyId, String value);

        @Query("{ 'status' : { $nin: ?0 }, 'expiryDate' : { $gt: ?1, $lt: ?2 } }")
        List<DisposedResponse> findByStatusNotAndExpiryDateBetween(List statusNotDisposed, LocalDate today,
                        LocalDate tomorrow);

        @Query("{ 'status' : { $nin: ?0 }, 'expiryDate' : { $gt: ?1, $lt: ?2 } }")
        Page<DisposedResponse> findByStatusNotAndExpiryDateBetween(List statusNotDisposed, LocalDate today,
                        LocalDate tomorrow, Pageable pageable);

        @Query("{ 'status' : { $nin: ?0 }, 'assetClass' : ?1, 'expiryDate' : { $gt: ?2, $lt: ?3 } }")
        Page<DisposedResponse> findByStatusNotAndAssetClassAndExpiryDateBetween(List statusNotDisposed,
                        String assetClass, LocalDate today,
                        LocalDate tomorrow, Pageable pageable);

        @Query("{ 'status': { $nin: ?0 }, 'expiryDate': { $gt: ?1, $lt: ?2 }, $or: [ { 'assetId': { $regex: ?3, $options: 'i' } }, { 'assetClass': { $regex: ?3, $options: 'i' } }, { 'plant': { $regex: ?3, $options: 'i' } } ] }")

        Page<DisposedResponse> findByStatusNotAndExpiryDateBetweenWithSearch(List statusNotDisposed, LocalDate today,
                        LocalDate tomorrow, String value, Pageable pageable);

//     Page<DisposedResponse> findByStatusNotAndExpiryDateBetweenWithSearch(List statusNotDisposed, LocalDate today,
//                                                                LocalDate tomorrow,String value, Pageable pageable);
    @Query(value = "{ 'status': { $nin: ?0 }, 'expiryDate': { $gt: ?1, $lt: ?2 }, $or: [ { 'assetId': { $regex: ?3, $options: 'i' } }, { 'assetClass': { $regex: ?3, $options: 'i' } }, { 'plant': { $regex: ?3, $options: 'i' } } ] }", count = true)
        long countByStatusNotInAndExpiryDateBetweenAndAssetIdOrAssetClassOrPlantLike(
                                                                   List<String> statusNotDisposed, LocalDate today, LocalDate tomorrow, String value);
                                                               
                                                               
        long countByStatusNotAndExpiryDateBetween(List statusNotDisposed, LocalDate today, LocalDate tomorrow);

        long countByStatusNotAndAssetClassAndExpiryDateBetween(List statusNotDisposed, String assetClass,
                        LocalDate today, LocalDate tomorrow);

        List<Asset> findByStatus(String statusBy);

        // Optional<Object> findByAssetId(String id);

        AuditResponse findByAssetIdAndCompanyIdAndStatusNotAndAuditDateNotNull(String assetId, String companyId,
                        String value);

        List<AuditResponse> findByCompanyIdAndPlantAndStatusAndAuditDateAndAssetCategoryAndAssetType(String companyId,
                        String plant, String value, Object object, String assetCategoryId, String assetTypeId);

        List<AuditResponse> findByCompanyIdAndPlantAndStatusAndAuditDateIsNotNullAndAssetCategoryAndAssetType(
                        String companyId, String value, String assetCategoryId, String assetTypeId);

        List<AuditResponse> findByCompanyIdAndPlantAndStatusAndAuditDateAndAssetCategory(String companyId, String plant,
                        String value, Object object, String assetCategoryId);

        List<AuditResponse> findByCompanyIdAndPlantAndStatusAndAuditDateIsNotNullAndAssetCategory(String companyId,
                        String plant, String value, String assetCategoryId);

        Page<AuditResponse> findByCompanyIdAndPlantAndStatusAndAuditDate(String companyId, String plant, String value,
                        Object object, Pageable pageable);

        Page<AuditResponse> findByCompanyIdAndPlantAndStatusAndAuditDateIsNotNull(String companyId, String plant,
                        String value, Pageable pageable);

        // Page<AuditResponse>
        // findByCompanyIdAndPlantAndStatusOrderByAuditDateAsc(String companyId, String
        // plant,String value, Pageable pageable);

        // Page<AuditResponse>
        // findByCompanyIdAndPlantAndStatusOrderByAuditDateAscWithSearch(String
        // companyId, String plant,String status, String value, Pageable pageable);
        @Query("{'companyId': ?0, 'plant': ?1, 'status': ?2, 'assetId': { $regex: ?3, $options: 'i' }}")
        Page<AuditResponse> findByCompanyIdAndPlantAndStatusOrderByAuditDateAscWithSearch(String companyId,
                        String plant, String status, String value, Pageable pageable);

        @Query("{ 'companyId' : ?0, 'plant' : ?1, 'status' : ?2 }")
        Page<AuditResponse> findByCompanyIdAndPlantAndStatusOrderByAuditDateAsc(String companyId, String plant,
                        String status, Pageable pageable);

        AuditResponse findByIdAndCompanyIdAndPlantAndStatusNotAndAuditDateNotNull(String id, String companyId,
                        String plant,
                        String value);

        // Page<AuditResponse>
        // findByCompanyIdAndPlantAndStatusNotAndAuditDateNotNull(String companyId,
        // String plant,String value, PageRequest pageable);
        // Page<AuditResponse>
        // findByCompanyIdAndPlantAndStatusNotAndAuditDateNotNullAndNextAuditDateAfterOrEqual(String
        // companyId, String plant,String value, LocalDate nextAuditDate, PageRequest
        // pageable);

        // for pageable data
        @Query("{ 'companyId' : ?0, 'plant' : ?1, 'status' : { $ne : ?2 }, 'auditDate' : { $ne : null }, 'nextAuditDate' : { $gte : ?3 } }")
        Page<AuditResponse> findByCompanyIdAndPlantAndStatusNotAndAuditDateNotNullAndNextAuditDateAfterOrEqual(
                        String companyId, String plant, String status, LocalDate auditDate, Pageable pageable);

        // for overall data
        @Query("{ 'companyId' : ?0, 'plant' : ?1, 'status' : { $ne : ?2 }, 'auditDate' : { $ne : null }, 'nextAuditDate' : { $gte : ?3 } }")
        List<AuditResponse> findByCompanyIdAndPlantAndStatusNotAndAuditDateNotNullAndNextAuditDateAfterOrEqual(
                        String companyId, String plant, String status, LocalDate auditDate);

        List<AuditResponse> findByCompanyIdAndPlantAndStatusNotAndAuditDateNotNull(String companyId, String plant,
                        String value);

        Page<AuditResponse> findByCompanyIdAndPlantAndStatusNotAndAuditDateNotNull(String companyId, String plant,
                        String value, Pageable pageable);
        // long countByCompanyIdAndPlantAndStatusNotAndAuditDateNotNull(String
        // companyId, String plant,
        // String value);

        List<AuditResponse> findByCompanyIdAndPlantAndStatusNotAndAuditDateNotNullAndAssetCategoryAndAssetType(
                        String companyId, String plant, String value, String assetCategoryId, String assetTypeId);

        List<AuditResponse> findByCompanyIdAndPlantAndStatusNotAndAuditDateNotNullAndAssetCategory(String companyId,
                        String plant, String value, String assetCategoryId);

        AuditResponse findByAssetIdAndCompanyIdAndPlantAndStatusNotAndAuditDateNotNull(String assetId, String companyId,
                        String plant, String value);

        Page<AuditResponse> findByCompanyIdAndPlantAndStatusAndAuditDateAndAssetClassAndAssetType(String companyId,
                        String plant, String value, Object object, String assetCategoryId, String assetTypeId,
                        PageRequest pageable);

        Page<AuditResponse> findByCompanyIdAndPlantAndStatusAndAuditDateIsNotNullAndAssetClassAndAssetType(
                        String companyId,
                        String value, String assetCategoryId, String assetTypeId, PageRequest pageable);

        Page<AuditResponse> findByCompanyIdAndPlantAndStatusAndAuditDateIsNotNullAndAssetClass(String companyId,
                        String plant, String value, String assetCategoryId, Pageable pageable);

        List<AuditResponse> findByCompanyIdAndPlantAndStatusNotAndAuditDateNotNullAndAssetClassAndAssetType(
                        String companyId, String plant, String value, String assetCategoryId, String assetTypeId);

        Page<AuditResponse> findByCompanyIdAndPlantAndStatusNotAndAuditDateNotNullAndAssetClassAndAssetType(
                        String companyId, String plant, String value, String assetCategoryId, String assetTypeId,
                        Pageable pageable);

        long countByCompanyIdAndPlantAndStatusNotAndAuditDateNotNullAndAssetClassAndAssetType(
                        String companyId, String plant, String value, String assetCategoryId, String assetTypeId);

        List<AuditResponse> findByCompanyIdAndPlantAndStatusNotAndAuditDateNotNullAndAssetClass(String companyId,
                        String plant, String value, String assetCategoryId);

        Page<AuditResponse> findByCompanyIdAndPlantAndStatusNotAndAuditDateNotNullAndAssetClass(String companyId,
                        String plant, String value, String assetCategoryId, Pageable pageable);

        long countByCompanyIdAndPlantAndStatusNotAndAuditDateNotNullAndAssetClass(String companyId,
                        String plant, String value, String assetCategoryId);

        List<AuditResponse> findByCompanyIdAndPlantAndAuditDateNotNullAndAssetClassAndAssetType(String companyId,
                        String plant, String assetCategoryId, String assetTypeId);

        Page<AuditResponse> findByCompanyIdAndPlantAndAuditDateNotNullAndAssetClass(String companyId, String plant,
                        String assetCategoryId, PageRequest pageable);

        Page<AuditResponse> findByCompanyIdAndPlantAndAuditDateNotNull(String companyId, String plant,
                        PageRequest pageable);

        List<Asset> findByStatusAndCompanyIdAndPlant(String statusBy, String companyId, String plant);

        @Query("{$and:[" + "{'status': ?0}," + "{'companyId': ?1}," + "{'plant': ?2}," +
                        "{$or:[" +
                        "{'assetId': {$regex: ?3, $options: 'i'}}, " +
                        "{'assetClass': {$regex: ?3, $options: 'i'}}, " +
                        "{'plant': {$regex: ?3, $options: 'i'}}" +
                        "]}" +
                        "]}")
        Page<Asset> findByStatusAndCompanyIdAndPlantWithSearch(String statusBy, String companyId, String plant,
                        String value, Pageable pageable);

        Page<Asset> findByStatusAndCompanyIdAndPlant(String statusBy, String companyId, String plant,
                        Pageable pageable);

        long countByStatusAndCompanyIdAndPlantAndAssetClassAndAssetId(String statusBy, String companyId, String plant,
                        String assetClass, String assetId);

        long countByStatusAndCompanyIdAndPlantAndAssetClassAndAssetIdAndChildId(String statusBy, String companyId,
                        String plant, String assetClass, String assetId, String childId);

        long countByStatusAndCompanyIdAndPlantAndAssetClass(String statusBy, String companyId, String plant,
                        String assetClass);

        long countByStatusAndCompanyIdAndPlantAndAssetClassAndSubClass(String statusBy, String companyId, String plant,
                        String assetClass, String subClass);

        Asset findByAssetIdAndCompanyIdAndPlantAndStatus(String assetId, String companyId, String plant, String status);

        List<Asset> findAllByAssetIdAndCompanyIdAndPlantAndStatus(String assetId, String companyId, String plant,
                        String status);

        List<Asset> findByCompanyIdAndPlantAndStatusAndAvailableStatusAndAssetStatus(String companyId, String plant,
                        String value, String availableStatus, String assetStatus, Sort sortById);

        Page<Asset> findByCompanyIdAndPlantAndStatusAndAvailableStatusAndAssetStatus(String companyId, String plant,
                        String value, String availableStatus, String assetStatus, Pageable pageable);

        Page<AuditResponse> findByCompanyIdAndPlantAndStatusAndAuditDateAndAssetClass(String companyId,
                        String plant, String value, Object object, String assetClass, Pageable pageable);

        Page<AuditResponse> findByCompanyIdAndPlantAndStatusAndAuditDateAndSubClass(String companyId, String plant,
                        String value, Object o, String assetCategoryId, PageRequest pageable);

        Page<AuditResponse> findByCompanyIdAndPlantAndStatusAndAuditDateIsNotNullAndSubClass(String companyId,
                        String plant, String value, String assetCategoryId, PageRequest pageable);

        long countByCompanyIdAndPlantAndStatusAndAuditDateAndAssetClassAndAssetType(String companyId, String plant,
                        String value, Object o, String assetCategoryId, String assetTypeId);

        long countByCompanyIdAndPlantAndStatusAndAuditDateIsNotNullAndAssetClassAndAssetType(String companyId,
                        String value, String assetCategoryId, String assetTypeId);

        long countByCompanyIdAndPlantAndStatusAndAuditDateAndAssetClass(String companyId, String plant, String value,
                        Object o, String assetCategoryId);

        long countByCompanyIdAndPlantAndStatusAndAuditDateIsNotNullAndAssetClass(String companyId, String plant,
                        String value, String assetCategoryId);

        long countByCompanyIdAndPlantAndStatusAndAuditDateAndSubClass(String companyId, String plant, String value,
                        Object o, String subClass);

        long countByCompanyIdAndPlantAndStatusAndAuditDateIsNotNullAndSubClass(String companyId, String plant,
                        String value, String subClass);

        long countByCompanyIdAndPlantAndStatusAndAuditDate(String companyId, String plant, String value, Object o);

        long countByCompanyIdAndPlantAndStatusAndAuditDateIsNotNull(String companyId, String plant, String value);

        long countByCompanyIdAndPlantAndStatusNotAndAuditDateNotNull(String companyId, String plant, String value);

        // long
        // countByCompanyIdAndPlantAndStatusNotAndAuditDateNotNullAndNextAuditDateAfterOrEqual(
        // String companyId, String plant, String value, LocalDate nextAuditDate);
        long countByCompanyIdAndPlantAndAuditDateNotNullAndAssetClassAndAssetType(String companyId, String plant,
                        String assetCategoryId, String assetTypeId);

        long countByCompanyIdAndPlantAndAuditDateNotNull(String companyId, String plant);

        long countByCompanyIdAndPlantAndAuditDateNotNullAndAssetClass(String companyId, String plant,
                        String assetCategoryId, PageRequest pageable);

        // @Query(value = "{'companyId': ?0, 'plant': ?1, 'status': { $ne: ?2 },
        // 'auditDate': { $exists: true }}, " +
        // "{$lookup: { from: 'audit', localField: 'assetId.id', foreignField: 'id', as:
        // 'latestAudits' }}, " +
        //// "{$unwind: '$latestAudits'}, " +
        // "{$sort: { 'latestAudits.auditDate': -1 }}, " +
        // "{$project: { _id: '$latestAudits._id', auditDate: { $first: '$auditDate' },
        // assetId: { $first: '$assetId' }, " +
        // "assetClass: { $first: '$assetClass' }, assetType: { $first: '$assetType' },
        // subClass: { $first: '$subClass' }, " +
        // "currentImage: { $first: '$latestAudits.currentImage' }, previewImage: {
        // $first: '$picture' }, audit: { $first: '$latestAudits' }, " +
        // "status: { $first: '$status' }, latestAuditDate: { $first:
        // '$latestAudits.auditDate' } }}," +
        // "{$skip: 0}, {$limit: 10}")
        @Query(value = "{" +
                        "'companyId': ?0, " +
                        "'plant': ?1, " +
                        "'status': { $ne: ?2 }, " +
                        "'auditDate': { $exists: true }" +
                        "}, " +
                        "{$lookup: { " +
                        "from: 'audit', " +
                        "localField: 'assetId.$id', " +
                        "foreignField: '_id', " +
                        "as: 'latestAudits' " +
                        "}}, " +
                        // "{$unwind: '$latestAudits'}, " + // Unwind the array field latestAudits
                        "{$sort: { 'latestAudits.auditDate': -1 }}, " +
                        "{$project: { " +
                        "_id: '$latestAudits._id', " +
                        "auditDate: { $first: '$auditDate' }, " +
                        "assetId: { $first: '$assetId' }, " +
                        "assetClass: { $first: '$assetClass' }, " +
                        "assetType: { $first: '$assetType' }, " +
                        "subClass: { $first: '$subClass' }, " +
                        "currentImage: { $first: '$latestAudits.currentImage' }, " +
                        "previewImage: { $first: '$picture' }, " +
                        "audit: { $first: '$latestAudits' }, " +
                        "status: { $first: '$status' }, " +
                        "latestAuditDate: { $first: '$latestAudits.auditDate' } " +
                        "}}, " +
                        "{$skip: ?3}, " +
                        "{$limit: ?4}")
        List<AuditResponse> findByAggregation(String companyId, String plant, String value, Integer page, Integer size);

        boolean existsByAssetIdAndChildId(String assetId, String childId);

        List<Asset> findByPlant(String plant);

        boolean existsByAssetIdAndChildIdAndPlant(String replaceAll, String childId, String plant);

        // @Query(value = "{ 'companyId': ?0, 'plant': ?1, 'status': { $ne: ?2 },
        // 'auditDate': { $exists: true } }, " +
        // "{$lookup: { from: 'audit', localField: 'assetId.id', foreignField: 'id', as:
        // 'latestAudits' }}, " +
        // "{$unwind: '$latestAudits'}, " +
        // "{$group: { _id: '$_id' }}, " +
        // "{$count: 'total' }")
        // List<AuditResponse> findByCriteria(String companyId, String plant, String
        // status);
}
