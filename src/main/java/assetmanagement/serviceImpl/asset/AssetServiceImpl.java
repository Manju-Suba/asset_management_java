package assetmanagement.serviceImpl.asset;

import assetmanagement.dto.AssetIdDTO;
import assetmanagement.dto.AssetSAPTwo;
import assetmanagement.enumData.ActiveInActive;
import assetmanagement.enumData.AuditStatus;
import assetmanagement.enumData.AvailableStatus;
import assetmanagement.exception.ResourceNotFoundException;
import assetmanagement.model.Asset;
import assetmanagement.model.AssetSAP;
import assetmanagement.model.SubClass;
import assetmanagement.model.masters.AssetClass;
import assetmanagement.model.masters.AssetType;
import assetmanagement.model.masters.Plant;
import assetmanagement.repository.SubClassRepository;
import assetmanagement.repository.asset.AssetHistoryRepository;
import assetmanagement.repository.asset.AssetRepository;
import assetmanagement.repository.masters.AssetClassRepository;
import assetmanagement.repository.masters.AssetTypeRepository;
import assetmanagement.repository.masters.PlantRepository;
import assetmanagement.response.AssetAllocationResponse;
import assetmanagement.response.AssetListResponse;
import assetmanagement.response.ChildIdDTO;
import assetmanagement.response.SapResponse;
import assetmanagement.service.asset.AssetService;
import assetmanagement.util.AuthUser;
import assetmanagement.util.Format;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private static final Logger logger = LoggerFactory.getLogger(AssetServiceImpl.class);
    private static final String PROGRESS_FILE = "progress.txt";
    public final AssetRepository assetRepository;
    public final AssetHistoryRepository assetHistoryRepository;
    public final AssetTypeRepository assetTypeRepository;
    public final AssetClassRepository assetClassRepository;
    public final PlantRepository plantRepository;
    public final SubClassRepository subClassRepository;
    private final MongoTemplate mongoTemplate;
    private final RestTemplate restTemplate;
    @Value("${upload.path}")
    private String fileBasePath;

    @Value("${sap.username}")
    private String SAPUSER;

    @Value("${sap.secret}")
    private String SAPSECRET;

    @Override
    public Asset create(Asset asset) {

        asset.setCompanyId(AuthUser.getCompanyId());

        boolean assetClassExist = assetClassRepository.existsByAssetClass(asset.getAssetClass());
        boolean plantExist = plantRepository.existsByPlant(asset.getPlant());
        boolean subClassExists = subClassRepository.existsByAssetIdAndChildId(asset.getAssetId(), asset.getChildId());
        if (!assetClassExist) {
            AssetClass assetClass = new AssetClass();
            assetClass.setAssetClass(asset.getAssetClass());
            assetClass.setCompanyId(AuthUser.getCompanyId());
            assetClass.setChildId(asset.getChildId());
            assetClass.setPlant(asset.getPlant());

            assetClassRepository.save(assetClass);
        }
        if (!plantExist) {
            Plant plant = new Plant();
            plant.setPlant(asset.getPlant());
            plant.setCompanyId(AuthUser.getCompanyId());

            plantRepository.save(plant);
        }
        if (!subClassExists) {
            SubClass subClass = new SubClass();
            subClass.setAssetClass(asset.getAssetClass());
            subClass.setAssetId(asset.getAssetId());
            subClass.setChildId(asset.getChildId());
            subClass.setCompanyId(AuthUser.getCompanyId());
            subClass.setPlant(AuthUser.getPlant());
            subClassRepository.save(subClass);
        }
        String assetStatus = asset.getAssetStatus();
        if (assetStatus != null && !assetStatus.isEmpty()) {
            String statusToSet = assetStatus.equals(AvailableStatus.Scrap.getValue())
                    ? AuditStatus.Disposed.getValue()
                    : AvailableStatus.Stock.getValue();
            asset.setAvailableStatus(statusToSet);
        } else {
            asset.setAvailableStatus(AvailableStatus.Stock.getValue());
        }

        return assetRepository.save(asset);
    }

    @Override
    public SapResponse fetchDataAndInsert() {
        try {
            String url = "http://10.100.7.73:8002/sap/bc/zasset?sap-client=500";
            String username = SAPUSER;
            String password = SAPSECRET;
            String companyId = "65be0267c3dc4b4127d833e2";

            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(username, password);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            String responseBody = response.getBody();
            if (responseBody == null) {
                throw new Exception("Response body is null");
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);
            JsonNode dataNode = root.get("Data");

            if (dataNode != null && dataNode.isArray()) {
                Set<String> processedAssetIds = loadProgress();

                for (JsonNode assetNode : dataNode) {
                    try {
                        AssetSAP asset = mapper.treeToValue(assetNode, AssetSAP.class);

                        // Skip already processed assets
                        if (processedAssetIds.contains(asset.getAssetId())) {
                            continue;
                        }

                        asset.setCompanyId(companyId);
                        asset.setAssetStatus("Online");
                        asset.setAssetId(asset.getAssetId().replaceAll("^0+", ""));
                        asset.setAssetClass(asset.getAssetClass().replaceAll("^0+", ""));
                        asset.setSapDate(LocalDate.now());

                        if (asset.getDepStart() != null && !asset.getDepStart().equals("0000-00-00")) {
                            asset.setAssetAgeingFrom(LocalDate.parse(asset.getDepStart()));
                        }

                        if (asset.getAssetRetirement() != null && !asset.getAssetRetirement().equals("0000-00-00")) {
                            asset.setAssetRetirementDate(LocalDate.parse(asset.getAssetRetirement()));
                        }

                        if (asset.getCapitalization() != null && !asset.getCapitalization().equals("0000-00-00")) {
                            asset.setCapitalizationDate(LocalDate.parse(asset.getCapitalization()));
                        }

                        boolean assetExists = assetRepository.existsByAssetIdAndChildIdAndPlant(
                                asset.getAssetId().replaceAll("^0+", ""), asset.getChildId(), asset.getPlant());
                        if (!assetExists) {
                            mongoTemplate.save(asset);
                        }

                        boolean assetClassExist = assetClassRepository.existsByAssetClassAndPlant(
                                asset.getAssetClass().replaceAll("^0+", ""), asset.getPlant());

                        if (!assetClassExist) {
                            AssetClass assetClass = new AssetClass();
                            assetClass.setAssetClass(asset.getAssetClass().replaceAll("^0+", ""));
                            assetClass.setCompanyId(companyId);
                            assetClass.setChildId(asset.getChildId());
                            assetClass.setPlant(asset.getPlant());

                            assetClassRepository.save(assetClass);
                        }

                        boolean plantExist = plantRepository.existsByPlant(asset.getPlant());
                        if (!plantExist) {
                            Plant plant = new Plant();
                            plant.setPlant(asset.getPlant());
                            plant.setCompanyId(companyId);

                            plantRepository.save(plant);
                        }

                        // Save progress after processing each asset
                        processedAssetIds.add(asset.getAssetId());
                        saveProgress(processedAssetIds);
                    } catch (Exception e) {
                        logger.info("Error processing asset ID: " + assetNode.get("AssetId").asText(), e);
                        // Skip the current record and continue with the next
                    }
                }
            } else {
                throw new Exception("Data node is null or not an array");
            }

            SapResponse responsebdy = new SapResponse();
            responsebdy.setAssetsCount(dataNode.size());
            responsebdy.setMessage("Data fetched and inserted successfully");
            return responsebdy;
        } catch (Exception e) {
            SapResponse response = new SapResponse();
            response.setMessage("Failed to fetch and insert data: " + e.getMessage());
            logger.info(".... Error Api: " + e.getMessage(), e);
            return response;
        }
    }

    private Set<String> loadProgress() {
        try {
            if (!Files.exists(Paths.get(PROGRESS_FILE))) {
                return new HashSet<>();
            }
            return new HashSet<>(Files.readAllLines(Paths.get(PROGRESS_FILE)));
        } catch (IOException e) {
            e.printStackTrace();
            return new HashSet<>();
        }
    }

    private void saveProgress(Set<String> processedAssetIds) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(PROGRESS_FILE))) {
            for (String assetId : processedAssetIds) {
                writer.write(assetId);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SapResponse fetchDataAndUpdate() {
        try {
            String urlSap = "http://10.100.7.73:8002/sap/bc/zar01_api?sap-client=500";
            String username = SAPUSER;
            String password = SAPSECRET;
            List<Plant> plantList = plantRepository.findAll();

            for (Plant plant : plantList) {
                try {
                    // Fetch the latest Asset based on the criteria
                    Aggregation aggregation = Aggregation.newAggregation(
                            Aggregation.match(Criteria.where("plant").is(plant.getPlant())),
                            Aggregation.match(Criteria.where("beginningTotalAsset").exists(true)),
                            Aggregation.sort(Sort.Direction.DESC, "sapDate"),
                            Aggregation.limit(1));
                    AggregationResults<Asset> results = mongoTemplate.aggregate(aggregation, "asset", Asset.class);
                    Asset dataOfAsset = results.getUniqueMappedResult();
                    Long cost = (dataOfAsset != null) ? Long.parseLong(dataOfAsset.getBeginningTotalAsset()) : 0L;

                    // Reset cost for each plant
                    cost = 0L;

                    // Get all assets for the plant
                    List<Asset> dataNode = assetRepository.findByPlant(plant.getPlant());

                    for (Asset assetNode : dataNode) {
                        try {
                            // Prepare headers
                            HttpHeaders headerSap = new HttpHeaders();
                            headerSap.setBasicAuth(username, password);
                            headerSap.setContentType(MediaType.APPLICATION_JSON);

                            // Create request JSON payload
                            ObjectMapper mapperSapNode = new ObjectMapper();
                            ArrayNode requestArray = mapperSapNode.createArrayNode();
                            ObjectNode assetNodeRequest = mapperSapNode.createObjectNode();

                            // Assuming `assetNode.getAssetId()` and `assetNode.getChildId()` are methods in
                            // `assetNode`
                            assetNodeRequest.put("ASSET_NUM", assetNode.getAssetId());
                            assetNodeRequest.put("SUB_ASSET_NUM",
                                    (assetNode.getChildId() != null) ? assetNode.getChildId() : "*");
                            assetNodeRequest.put("DATE", "31.03.2024"); // Replace with your desired date
                            requestArray.add(assetNodeRequest);

                            String jsonPayload = mapperSapNode.writeValueAsString(requestArray);
                            HttpEntity<String> entitySap = new HttpEntity<>(jsonPayload, headerSap);

                            // Send the request and get the response
                            ResponseEntity<String> responseSap = restTemplate.exchange(urlSap, HttpMethod.POST,
                                    entitySap, String.class);
                            String responseBodySap = responseSap.getBody();

                            ObjectMapper mapperSap = new ObjectMapper();
                            JsonNode rootSap = mapperSap.readTree(responseBodySap);
                            JsonNode dataNodeSap = rootSap.get("Data");

                            if (dataNodeSap != null && dataNodeSap.isArray()) {
                                for (JsonNode assetSapNode : dataNodeSap) {
                                    AssetSAPTwo assetSap = mapperSap.treeToValue(assetSapNode, AssetSAPTwo.class);
                                    assetNode.setCostOfAsset(assetSap.getCostOfAsset());
                                    assetNode.setEstimatedSalvageValue(assetSap.getEstimatedSalvageValue());
                                    assetNode.setCostOfInvestment(Long.toString(assetSap.getCostOfAsset()));
                                    if (assetSap.getCostOfAsset() != null) {
                                        cost += assetSap.getCostOfAsset();
                                        assetNode.setBeginningTotalAsset(Long.toString(cost));
                                    }
                                    // Save the updated assetNode
                                    assetRepository.save(assetNode);
                                }
                            }
                        } catch (Exception e) {
                            // Log error and continue with the next assetNode
                            logger.error("Error processing assetNode with ID: " + assetNode.getAssetId(), e);
                        }
                    }

                    // Update the endingTotalAsset field
                    LocalDate date = LocalDate.now();
                    Query query = new Query();
                    query.addCriteria(Criteria.where("sapDate").is(date).and("plant").is(plant.getPlant()));
                    Update update = Update.update("endingTotalAsset", Long.toString(cost));
                    mongoTemplate.updateMulti(query, update, Asset.class);

                } catch (Exception e) {
                    // Log error and continue with the next plant
                    logger.error("Error processing plant with ID: " + plant.getPlant(), e);
                }

            }

            SapResponse responsebdy = new SapResponse();
            responsebdy.setMessage("Data fetched and inserted successfully");
            return responsebdy;
        } catch (Exception e) {
            SapResponse response = new SapResponse();
            response.setMessage("Failed to fetch and insert data: " + e.getMessage());
            logger.error("Error Api", e);
            return response;
        }
    }

    @Override
    public AssetListResponse getAllAsset(String availableStatus, String assetClass, String assetStatus, String assetId,
                                         String childId, Boolean search, String value, Integer page, Integer size) {

        page = page == null ? 0 : page;
        size = size == null ? 10 : size;

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        AssetListResponse response = new AssetListResponse();

        Criteria criteria = Criteria.where("companyId").is(AuthUser.getCompanyId())
                .and("plant").is(AuthUser.getPlant())
                .and("status").is(ActiveInActive.ACTIVE.getValue());
        if (availableStatus != null) {
            criteria.and("availableStatus").is(availableStatus);
        }
        if (assetClass != null) {
            criteria.and("assetClass").is(assetClass);
        }
        if (assetStatus != null) {
            criteria.and("assetStatus").is(assetStatus);
        }
        if (assetId != null) {
            criteria.and("assetId").is(assetId);
        }
        if (childId != null) {
            criteria.and("childId").is(childId);
        }
        if (Boolean.TRUE.equals(search) && value != null && !value.isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("assetId").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("assetClass").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("childId").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("assetLifetime").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("costOfAsset").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("usefulLife").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("assetStatus").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)));
            criteria.andOperator(searchCriteria);
        }
        Query query = new Query(criteria);
        long totalCount = mongoTemplate.count(query, Asset.class);
        query.with(pageable);
        List<Asset> assetList = mongoTemplate.find(query, Asset.class);
        response.setAssetsCount(totalCount);
        response.setAssets(assetList);
        return response;
    }

    @Override
    public Optional<Asset> getAssetById(String id, String company, String plant) {
        if (!assetRepository.existsByAssetId(id)) {
            throw new ResourceNotFoundException("Id not found");
        }

        Criteria criteria = Criteria.where("companyId").is(company)
                .and("plant").is(plant)
                .and("status").is(ActiveInActive.ACTIVE.getValue())
                .and("assetId").is(id);
        Aggregation aggregationData = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("assetId")
                        .first("auditDate").as("auditDate")
                        .first("assetId").as("assetId")
                        .first("assetClass").as("assetClass")
                        .first("childId").as("childId")
                        .first("picture").as("previewImage")
                        .first("companyId").as("companyId")
                        .first("plant").as("plant")
                        .first("status").as("status")
                        .first("latitudeAndLongitude").as("latitudeAndLongitude")
                        .first("nextAuditDate").as("nextAuditDate")
                        .first("serialNumber").as("serialNumber")
                        .first("assetStatus").as("assetStatus")
                        .first("availableStatus").as("availableStatus")
                        .first("assetAgeingFrom").as("assetAgeingFrom")
                        .first("assetAgeingTo").as("assetAgeingTo")
                        .first("assetLifetime").as("assetLifetime")
                        .first("requiresAttention").as("requiresAttention")
                        .first("upOrDowntime").as("upOrDowntime")
                        .first("warrantyStatus").as("warrantyStatus")
                        .first("noOfRoutinesExecuted").as("noOfRoutinesExecuted")
                        .first("costBasedMajorAsset").as("costBasedMajorAsset")
                        .first("costBasedMinorAsset").as("costBasedMinorAsset")
                        .first("costOfAsset").as("costOfAsset")
                        .first("estimatedSalvageValue").as("estimatedSalvageValue")
                        .first("usefulLife").as("usefulLife")
                        .first("netProfitOrBenefit").as("netProfitOrBenefit")
                        .first("costOfInvestment").as("costOfInvestment")
                        .first("totalBenefit").as("totalBenefit")
                        .first("totalCost").as("totalCost")
                        .first("revenueGenerated").as("revenueGenerated")
                        .first("beginningTotalAsset").as("beginningTotalAsset")
                        .first("endingTotalAsset").as("endingTotalAsset")
                        .first("netIncome").as("netIncome")
                        .first("depreciation").as("depreciation")
                        .first("returnOfInvestment").as("returnOfInvestment")
                        .first("netBenefit").as("netBenefit")
                        .first("assetUtilization").as("assetUtilization")
                        .first("averageTotalAsset").as("averageTotalAsset")
                        .first("returnOfAsset").as("returnOfAsset")
                        .first("costClassWise").as("costClassWise")
                        .first("assetRetirementDate").as("assetRetirementDate")
                        .first("capitalizationDate").as("capitalizationDate")
                        .first("expiryDate").as("expiryDate")
                        .first("picture").as("picture")
                        .first("costCenter").as("costCenter")
                        .first("description").as("description"));

        List<Asset> assets = mongoTemplate.aggregate(aggregationData, "asset", Asset.class)
                .getMappedResults();

        return assets.isEmpty() ? Optional.empty() : Optional.of(assets.get(0));
    }

    @Override
    public Asset update(Asset asset, MultipartFile imageUpload, MultipartFile documentUpload) throws IOException {
        Optional<Asset> assetOptional = assetRepository.findByIdAndStatus(asset.getId(),
                ActiveInActive.ACTIVE.getValue());
        if (assetOptional.isPresent()) {
            Asset assetUpdate = assetOptional.get();
            assetUpdate.setAssetId(asset.getAssetId());
            assetUpdate.setLatitudeAndLongitude(asset.getLatitudeAndLongitude());
            assetUpdate.setCostClassWise(asset.getCostClassWise());
            assetUpdate.setAssetClass(asset.getAssetClass());
            assetUpdate.setSerialNumber(asset.getSerialNumber());
            assetUpdate.setAssetStatus(asset.getAssetStatus());
            assetUpdate.setAssetAgeingTo(asset.getAssetAgeingTo());
            assetUpdate.setAssetAgeingFrom(asset.getAssetAgeingFrom());
            assetUpdate.setAssetLifetime(asset.getAssetLifetime());
            assetUpdate.setRequiresAttention(asset.getRequiresAttention());
            assetUpdate.setUpOrDowntime(asset.getUpOrDowntime());
            assetUpdate.setWarrantyStatus(asset.getWarrantyStatus());
            assetUpdate.setNoOfRoutinesExecuted(asset.getNoOfRoutinesExecuted());
            assetUpdate.setCostBasedMajorAsset(asset.getCostBasedMajorAsset());
            assetUpdate.setCostBasedMinorAsset(asset.getCostBasedMinorAsset());
            assetUpdate.setCostOfAsset(asset.getCostOfAsset());
            assetUpdate.setEstimatedSalvageValue(asset.getEstimatedSalvageValue());
            assetUpdate.setUsefulLife(asset.getUsefulLife());
            assetUpdate.setNetProfitOrBenefit(asset.getNetProfitOrBenefit());
            assetUpdate.setCostOfInvestment(asset.getCostOfInvestment());
            assetUpdate.setTotalBenefit(asset.getTotalBenefit());
            assetUpdate.setTotalCost(asset.getTotalCost());
            assetUpdate.setRevenueGenerated(asset.getRevenueGenerated());
            assetUpdate.setAverageTotalAsset(asset.getAverageTotalAsset());
            assetUpdate.setBeginningTotalAsset(asset.getBeginningTotalAsset());
            assetUpdate.setEndingTotalAsset(asset.getEndingTotalAsset());
            assetUpdate.setNetIncome(asset.getNetIncome());
            assetUpdate.setDepreciation(asset.getDepreciation());
            assetUpdate.setReturnOfInvestment(asset.getReturnOfInvestment());
            assetUpdate.setNetBenefit(asset.getNetBenefit());
            assetUpdate.setAssetUtilization(asset.getAssetUtilization());
            assetUpdate.setAverageTotalAsset(asset.getAverageTotalAsset());
            assetUpdate.setReturnOfAsset(asset.getReturnOfAsset());
            assetUpdate.setExpiryDate(asset.getExpiryDate());

            final List<String> allowedImageExtensions = Arrays.asList("jpg", "jpeg", "png");

            if (imageUpload != null && !imageUpload.isEmpty()) {

                String originalName = imageUpload.getOriginalFilename();
                String extension;
                if (originalName != null) {
                    extension = getFileExtension(originalName);
                } else {
                    extension = null;
                }
                if (extension == null || !allowedImageExtensions.contains(extension.toLowerCase())) {
                    throw new IllegalArgumentException(
                            "Invalid image format. Only JPG, JPEG, and PNG files are allowed.");
                }
                String fileName = Format.formatDate() + "_" + originalName;
                Path path = Path.of(fileBasePath + fileName);
                Files.copy(imageUpload.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                assetUpdate.setPicture(fileName);
            }
            if (documentUpload != null && !documentUpload.isEmpty()) {
                String originalName = documentUpload.getOriginalFilename();
                String fileName = Format.formatDate() + "_" + originalName;
                Path path = Path.of(fileBasePath + fileName);
                Files.copy(documentUpload.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                assetUpdate.setDocument(fileName);
            }

            String assetStatus = asset.getAssetStatus();
            if (assetStatus != null && !assetStatus.isEmpty()) {
                String statusToSet = assetStatus.equals(AvailableStatus.Scrap.getValue())
                        ? AuditStatus.Disposed.getValue()
                        : AvailableStatus.Stock.getValue();
                assetUpdate.setAvailableStatus(statusToSet);
            } else {
                assetUpdate.setAvailableStatus(AvailableStatus.Stock.getValue());
            }

            return assetRepository.save(assetUpdate);
        }
        throw new ResourceNotFoundException("Data not found");
    }

    private String getFileExtension(String originalName) {
        int lastIndex = originalName.lastIndexOf(".");
        if (lastIndex == -1) {
            return null;
        }
        return originalName.substring(lastIndex + 1);
    }

    @Override
    public Asset delete(String id) {
        Optional<Asset> assetOptional = assetRepository.findByIdAndStatus(id, ActiveInActive.ACTIVE.getValue());
        if (assetOptional.isPresent()) {
            Asset assetDelete = assetOptional.get();
            assetDelete.setStatus(ActiveInActive.INACTIVE.getValue());
            return assetRepository.save(assetDelete);
        }
        throw new ResourceNotFoundException("Data not found");
    }

    @Override
    public List<AssetType> getAssetBasedOnCategory(String id) {

        return assetTypeRepository.findByAssetCategoryAndCompanyIdAndStatus(id, AuthUser.getCompanyId(),
                ActiveInActive.ACTIVE.getValue());

    }

    @Override
    public AssetAllocationResponse getAssetAllocation(String assetCategoryId, String assetStatus,
                                                      String availableStatus,
                                                      String allocateType, String subClass, Integer page, Integer size) {

        if (page == null && size == null || page == null || size == null) {
            page = 0;
            size = 10;
        }
        PageRequest pageable = PageRequest.of(page, size);
        Page<Asset> assetPage;
        List<Asset> assetList = new ArrayList<>();
        long counts;
        AssetAllocationResponse response = new AssetAllocationResponse();
        if (availableStatus == null || availableStatus.isEmpty()) {
            throw new ResourceNotFoundException("availableStatus must not be empty");
        }
        if (availableStatus.equals(AvailableStatus.Allocate.getValue())) {

            if (allocateType == null || allocateType.isEmpty()) {
                throw new ResourceNotFoundException("allocateType must not be empty");
            }

        } else {

            if (assetCategoryId != null && !assetCategoryId.isEmpty() && assetStatus != null
                    && !assetStatus.isEmpty() && subClass != null && !subClass.isEmpty()) {
                assetPage = assetRepository
                        .findByCompanyIdAndStatusAndAvailableStatusAndAssetClassAndAssetStatusAndSubClass(
                                AuthUser.getCompanyId(), ActiveInActive.ACTIVE.getValue(), availableStatus,
                                assetCategoryId,
                                assetStatus, subClass, pageable);
                assetList = assetPage.getContent();
                counts = assetPage.getTotalElements();
                response.setAssetsCount(counts);
            } else if (assetCategoryId != null && !assetCategoryId.isEmpty() && subClass != null
                    && !subClass.isEmpty()) {
                assetPage = assetRepository.findByCompanyIdAndStatusAndAvailableStatusAndAssetClassAndSubClass(
                        AuthUser.getCompanyId(), ActiveInActive.ACTIVE.getValue(), availableStatus, assetCategoryId,
                        subClass, pageable);
                assetList = assetPage.getContent();
                counts = assetPage.getTotalElements();
                response.setAssetsCount(counts);
            } else if (assetCategoryId != null && !assetCategoryId.isEmpty() && assetStatus != null
                    && !assetStatus.isEmpty()) {
                assetPage = assetRepository.findByCompanyIdAndStatusAndAvailableStatusAndAssetClassAndAssetStatus(
                        AuthUser.getCompanyId(), ActiveInActive.ACTIVE.getValue(), availableStatus, assetCategoryId,
                        assetStatus, pageable);
                assetList = assetPage.getContent();
                counts = assetPage.getTotalElements();
                response.setAssetsCount(counts);
            } else if (assetCategoryId != null && !assetCategoryId.isEmpty()) {
                assetPage = assetRepository.findByCompanyIdAndStatusAndAvailableStatusAndAssetClass(
                        AuthUser.getCompanyId(), ActiveInActive.ACTIVE.getValue(), availableStatus, assetCategoryId,
                        pageable);
                assetList = assetPage.getContent();
                counts = assetPage.getTotalElements();
                response.setAssetsCount(counts);

            } else if (assetStatus != null && !assetStatus.isEmpty()) {
                assetPage = assetRepository.findByCompanyIdAndStatusAndAvailableStatusAndAssetStatus(
                        AuthUser.getCompanyId(), ActiveInActive.ACTIVE.getValue(), availableStatus, assetStatus,
                        pageable);
                assetList = assetPage.getContent();
                counts = assetPage.getTotalElements();
                response.setAssetsCount(counts);

            } else {
                assetPage = assetRepository.findByCompanyIdAndStatusAndAvailableStatus(
                        AuthUser.getCompanyId(), ActiveInActive.ACTIVE.getValue(), availableStatus, pageable);
                assetList = assetPage.getContent();
                counts = assetPage.getTotalElements();
                response.setAssetsCount(counts);
            }
        }
        response.setAssets(assetList);
        return response;
    }

    @Override
    public List<Asset> getByClass(String assetClass, String assetStatus, String assetNo) {
        Sort sortById = Sort.by(Sort.Direction.DESC, "id");

        List<Asset> assetList;
        if (assetNo != null && !assetNo.isEmpty() && assetStatus != null && !assetStatus.isEmpty()) {
            assetList = assetRepository.findByCompanyIdAndPlantAndStatusAndAssetClassAndAssetStatusAndAssetId(
                    AuthUser.getCompanyId(), AuthUser.getPlant(), ActiveInActive.ACTIVE.getValue(), assetClass,
                    assetStatus, assetNo, sortById);
            return assetList;
        } else if (assetStatus != null && !assetStatus.isEmpty()) {
            assetList = assetRepository.findByCompanyIdAndPlantAndStatusAndAssetClassAndAssetStatus(
                    AuthUser.getCompanyId(), AuthUser.getPlant(), ActiveInActive.ACTIVE.getValue(), assetClass,
                    assetStatus, sortById);
            return assetList;
        } else if (assetNo != null && !assetNo.isEmpty()) {
            assetList = assetRepository.findByCompanyIdAndPlantAndStatusAndAssetClassAndAssetId(AuthUser.getCompanyId(),
                    AuthUser.getPlant(), ActiveInActive.ACTIVE.getValue(), assetClass, assetNo, sortById);
            return assetList;
        } else {
            assetList = assetRepository.findByCompanyIdAndPlantAndStatusAndAssetClass(AuthUser.getCompanyId(),
                    AuthUser.getPlant(), ActiveInActive.ACTIVE.getValue(), assetClass, sortById);
            return assetList;
        }

    }

    @Override
    public List<Asset> getAllByAssetStatus(String assetStatus) {
        return assetRepository.findByCompanyIdAndPlantAndStatusAndAssetStatus(AuthUser.getCompanyId(),
                AuthUser.getPlant(), ActiveInActive.ACTIVE.getValue(), assetStatus);
    }

    @Override
    public Asset getByAssetByAssetId(String assetId) {

        if (!assetRepository.existsByAssetId(assetId)) {
            throw new ResourceNotFoundException("Data not found");
        }
        return assetRepository.findByAssetIdAndCompanyIdAndPlantAndStatus(assetId, AuthUser.getCompanyId(),
                AuthUser.getPlant(), ActiveInActive.ACTIVE.getValue());

    }

    @Override
    public List<Asset> getAllAsset(String availableStatus, String assetClass, String assetStatus, String childId) {
        Sort sortById = Sort.by(Sort.Direction.DESC, "id");
        List<Asset> assetList;

        Criteria criteria = Criteria.where("companyId").is(AuthUser.getCompanyId())
                .and("plant").is(AuthUser.getPlant())
                .and("status").is(ActiveInActive.ACTIVE.getValue());

        if (availableStatus != null) {
            criteria.and("availableStatus").is(availableStatus);
        }
        if (assetClass != null) {
            criteria.and("assetClass").is(assetClass);
        }
        if (assetStatus != null) {
            criteria.and("assetStatus").is(assetStatus);
        }
        if (childId != null) {
            criteria.and("subClass").is(childId);
        }
        Query query = new Query(criteria).with(sortById);
        assetList = mongoTemplate.find(query, Asset.class);
        return assetList;
    }

    public List<AssetIdDTO> getNotReplacedAssetID() {

        List<String> allReplacedAssetId = assetHistoryRepository.findAllReplaceAssetId();

        List<String> replacedAssetIds = allReplacedAssetId.stream()
                .map(json -> json.replaceAll("\\{\"replaceAssetId\": \"(.*)\"\\}", "$1")) // Extract value
                .toList();

        Criteria criteria = new Criteria()
                .and("companyId").is(AuthUser.getCompanyId())
                .and("plant").is(AuthUser.getPlant())
                .and("availableStatus").is(AvailableStatus.Stock.getValue())
                .and("_id").nin(replacedAssetIds);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.project()
                        .and("_id").as("id")
                        .and("assetId").as("assetId")
                        .and("childId").as("childId"));

        AggregationResults<AssetIdDTO> results = mongoTemplate.aggregate(aggregation, "asset", AssetIdDTO.class);
        List<AssetIdDTO> assetList = results.getMappedResults();
        return assetList;

    }

    @Override
    public Optional<Asset> getAssetByAssetIdToAudit(String assetId, String company, String plant, String assetClass) {
        if (!assetRepository.existsByAssetId(assetId)) {
            throw new ResourceNotFoundException("Id not found");
        }

        Criteria criteria = Criteria.where("companyId").is(company)
                .and("plant").is(plant)
                .and("status").is(ActiveInActive.ACTIVE.getValue())
                .and("assetId").is(assetId)
                .and("assetClass").is(assetClass)
                .and("latitudeAndLongitude").exists(true);
        Aggregation aggregationData = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("assetId")
                        .first("auditDate").as("auditDate")
                        .first("assetId").as("assetId")
                        .first("assetClass").as("assetClass")
                        .first("childId").as("childId")
                        .first("picture").as("previewImage")
                        .first("companyId").as("companyId")
                        .first("plant").as("plant")
                        .first("status").as("status")
                        .first("latitudeAndLongitude").as("latitudeAndLongitude")
                        .first("nextAuditDate").as("nextAuditDate")
                        .first("serialNumber").as("serialNumber")
                        .first("assetStatus").as("assetStatus")
                        .first("availableStatus").as("availableStatus")
                        .first("assetAgeingFrom").as("assetAgeingFrom")
                        .first("assetAgeingTo").as("assetAgeingTo")
                        .first("assetLifetime").as("assetLifetime")
                        .first("requiresAttention").as("requiresAttention")
                        .first("upOrDowntime").as("upOrDowntime")
                        .first("warrantyStatus").as("warrantyStatus")
                        .first("noOfRoutinesExecuted").as("noOfRoutinesExecuted")
                        .first("costBasedMajorAsset").as("costBasedMajorAsset")
                        .first("costBasedMinorAsset").as("costBasedMinorAsset")
                        .first("costOfAsset").as("costOfAsset")
                        .first("estimatedSalvageValue").as("estimatedSalvageValue")
                        .first("usefulLife").as("usefulLife")
                        .first("netProfitOrBenefit").as("netProfitOrBenefit")
                        .first("costOfInvestment").as("costOfInvestment")
                        .first("totalBenefit").as("totalBenefit")
                        .first("totalCost").as("totalCost")
                        .first("revenueGenerated").as("revenueGenerated")
                        .first("beginningTotalAsset").as("beginningTotalAsset")
                        .first("endingTotalAsset").as("endingTotalAsset")
                        .first("netIncome").as("netIncome")
                        .first("depreciation").as("depreciation")
                        .first("returnOfInvestment").as("returnOfInvestment")
                        .first("netBenefit").as("netBenefit")
                        .first("assetUtilization").as("assetUtilization")
                        .first("averageTotalAsset").as("averageTotalAsset")
                        .first("returnOfAsset").as("returnOfAsset")
                        .first("costClassWise").as("costClassWise")
                        .first("assetRetirementDate").as("assetRetirementDate")
                        .first("capitalizationDate").as("capitalizationDate")
                        .first("picture").as("picture")
                        .first("costCenter").as("costCenter")
                        .first("description").as("description")
                        .first("expiryDate").as("expiryDate"));

        List<Asset> assets = mongoTemplate.aggregate(aggregationData, "asset", Asset.class)
                .getMappedResults();

        return assets.isEmpty() ? Optional.empty() : Optional.of(assets.get(0));
    }

    @Override
    public List<ChildIdDTO> getChildIdByAssetId(String assetId, String plant, String company) {
        if (!assetRepository.existsByAssetId(assetId)) {
            throw new ResourceNotFoundException("Id not found");
        }

        Criteria criteria = Criteria.where("companyId").is(company)
                .and("plant").is(plant)
                .and("status").is(ActiveInActive.ACTIVE.getValue())
                .and("assetId").is(assetId);
        Aggregation aggregationData = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("assetId")
                        .first("childId").as("childId"));

        List<ChildIdDTO> assets = mongoTemplate.aggregate(aggregationData, "asset", ChildIdDTO.class)
                .getMappedResults();

        return assets;
    }

}
