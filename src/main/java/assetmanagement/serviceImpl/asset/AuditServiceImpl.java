package assetmanagement.serviceImpl.asset;

import assetmanagement.dto.AuditDto;
import assetmanagement.enumData.ActiveInActive;
import assetmanagement.enumData.AuditStatus;
import assetmanagement.enumData.AvailableStatus;
import assetmanagement.enumData.DisposedStatus;
import assetmanagement.enumData.TransferStatus;
import assetmanagement.exception.EntityNotFoundException;
import assetmanagement.exception.ResourceNotFoundException;
import assetmanagement.model.Asset;
import assetmanagement.model.Users;
import assetmanagement.model.audit.Audit;
import assetmanagement.model.audit.AuditHistory;
import assetmanagement.repository.UserRepository;
import assetmanagement.repository.asset.AssetRepository;
import assetmanagement.repository.audit.AuditHistoryRepository;
import assetmanagement.repository.audit.AuditRepository;
import assetmanagement.request.AuditRequest;
import assetmanagement.request.RequestWithFilter;
import assetmanagement.response.*;
import assetmanagement.service.MailService;
import assetmanagement.service.asset.AuditService;
import assetmanagement.util.AuthUser;
import assetmanagement.util.Constant;
import assetmanagement.util.Format;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;

import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditRepository auditRepository;
    private final AuditHistoryRepository auditHistoryRepository;
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final MongoTemplate mongoTemplate;
    private final MailService mailService;
    @Value("${upload.path}")
    private String fileBasePath;

    @Override
    public List<AuditDto> getAll(String status) {
        return auditRepository.findByStatus(status);
    }

    @Override
    public Audit getById(String id) {
        Optional<Audit> audit = auditRepository.findById(id);
        if (audit.isPresent()) {
            return audit.get();
        }
        throw new ResourceNotFoundException(Constant.DATA_NOT_FOUND);
    }

    @Override
    public Audit updateStatus(String status, String id, String userId) {
        Optional<Audit> fetch = auditRepository.findById(id);
        if (fetch.isPresent()) {
            Audit audit = fetch.get();
            audit.setStatus(status);
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found for id: " + userId));
            audit.setAuditBy(user);
            Audit updated = auditRepository.save(audit);
            createHistory(status, updated);
            return audit;
        }
        throw new ResourceNotFoundException(Constant.DATA_NOT_FOUND);
    }

    @Override
    public List<Audit> getFilter(String assetClass, String assetType, String status) {
        // List<Audit> details = auditRepository.findAll();
        //
        // Predicate<Audit> statusFilter = auditDetail ->
        // auditDetail.getStatus().equals(status);
        // Predicate<Audit> assetClassFilter = null;
        // Predicate<Audit> assetTypeFilter = null;
        // Predicate<Audit> combinedFilter = statusFilter;
        //
        // if (assetClass != null && !assetClass.isEmpty()) {
        // assetClassFilter = auditDetail ->
        // auditDetail.getAssetId().getAssetCategory().getId().equals(assetClass);
        // combinedFilter = combinedFilter.and(assetClassFilter);
        // }
        //
        // if (assetType != null && !assetType.isEmpty()) {
        // assetTypeFilter = auditDetail ->
        // auditDetail.getAssetId().getAssetType().getId().equals(assetType);
        // combinedFilter = combinedFilter.and(assetTypeFilter);
        // }
        // return details.stream().filter(combinedFilter).collect(Collectors.toList());
        return null;
    }

    public void createHistory(String status, Audit audit) {
        AuditHistory history = new AuditHistory();
        if (status.equals(AuditStatus.Pending.getValue())) {
            history.setStatusFrom(AuditStatus.Created.getValue());
            history.setStatusTo(status);
        } else if (status.equals(AuditStatus.Completed.getValue())) {
            history.setStatusFrom(AuditStatus.Pending.getValue());
            history.setStatusTo(status);
        }
        history.setAuditId(audit);
        auditHistoryRepository.save(history);
    }

    // @Override
    // public PendingAuditAssetResponse getPendingAuditAsset(RequestWithFilter
    // requestWithFilter, int page, int size, boolean search, String value) {
    // Sort sortByDescId = Sort.by(Sort.Direction.DESC, "id");
    // Pageable pageable = PageRequest.of(page, size, sortByDescId);

    // // Initialize lists and counts
    // List<AuditResponse> assetResponse = new ArrayList<>();
    // long counts;
    // PendingAuditAssetResponse response = new PendingAuditAssetResponse();

    // String assetClass = requestWithFilter.getAssetClass();

    // Page<AuditResponse> assetPageWithoutAudit;
    // if (assetClass != null && !assetClass.isEmpty()) {
    // assetPageWithoutAudit =
    // assetRepository.findByCompanyIdAndPlantAndStatusAndAuditDateAndAssetClass(
    // AuthUser.getCompanyId(), AuthUser.getPlant(),
    // ActiveInActive.ACTIVE.getValue(), null,
    // assetClass, pageable); // this one not fixed//
    // }

    // if (search) {
    // assetPageWithoutAudit =
    // assetRepository.findByCompanyIdAndPlantAndStatusOrderByAuditDateAscWithSearch(AuthUser.getCompanyId(),
    // AuthUser.getPlant(), ActiveInActive.ACTIVE.getValue(), value, pageable);
    // } else {
    // assetPageWithoutAudit =
    // assetRepository.findByCompanyIdAndPlantAndStatusOrderByAuditDateAsc(AuthUser.getCompanyId(),
    // AuthUser.getPlant(), ActiveInActive.ACTIVE.getValue(), pageable);
    // }

    // // Get assets without audit
    // List<AuditResponse> assetWithoutAudit = assetPageWithoutAudit.getContent();

    // for (AuditResponse auditResponse : assetWithoutAudit) {
    // LocalDate auditingDate = auditResponse.getAuditDate();

    // if(auditingDate != null){
    // int auditingDay = auditingDate.getDayOfMonth();
    // if (auditingDay == 31) {
    // auditingDay -= 1;
    // if (auditingDay == 0) {
    // auditingDay = LocalDate.now().minusMonths(1).lengthOfMonth();
    // }
    // }
    // LocalDate nextAuditDate;

    // if (auditResponse.getAudit() != null &&
    // auditResponse.getAudit().getNextAuditDate() != null) {
    // nextAuditDate = auditResponse.getAudit().getNextAuditDate();
    // System.out.println("1");
    // } else {
    // LocalDate currentDate = LocalDate.now();
    // nextAuditDate = currentDate.plusMonths(1).withDayOfMonth(auditingDay);
    // if (!nextAuditDate.isAfter(currentDate)) {
    // nextAuditDate = currentDate.plusMonths(1).withDayOfMonth(1).minusDays(1);
    // }
    // }
    // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yyyy",
    // Locale.ENGLISH);

    // if(nextAuditDate != null){
    // LocalDate currentDate = LocalDate.now();
    // if (!nextAuditDate.isAfter(currentDate)) {
    // nextAuditDate = currentDate.plusMonths(1).withDayOfMonth(1).minusDays(1);
    // }
    // auditResponse.setAuditDateFormat(nextAuditDate.format(formatter));
    // }else{
    // LocalDate currentDate = LocalDate.now();
    // nextAuditDate = currentDate.plusMonths(1).withDayOfMonth(auditingDay);
    // if (!nextAuditDate.isAfter(currentDate)) {
    // nextAuditDate = currentDate.plusMonths(1).withDayOfMonth(1).minusDays(1);
    // }
    // auditResponse.setAuditDateFormat(nextAuditDate.format(formatter));
    // }
    // assetResponse.add(auditResponse);
    // }else{
    // assetResponse.add(auditResponse);
    // }
    // }
    // counts = assetPageWithoutAudit.getTotalElements();
    // response.setAuditResponses(assetResponse);
    // response.setPendingAuditAssetCounts(counts);
    // return response;
    // }

    // new method updated on 11/04/2024
    @Override
    public PendingAuditAssetResponse getPendingAuditAsset(RequestWithFilter requestWithFilter, int page, int size,
            boolean search, String value) {
        List<Criteria> criteriaList = new ArrayList<>();

        criteriaList.add(Criteria.where("companyId").is(AuthUser.getCompanyId())
                .and("plant").is(AuthUser.getPlant())
                .and("latitudeAndLongitude").ne(null)
                .and("status").is(ActiveInActive.ACTIVE.getValue())
                .and("availableStatus").nin(AuditStatus.Disposed.getValue(), DisposedStatus.Replaced.getValue()));

        // // Add additional match stages for each filter condition
        if (requestWithFilter.getAssetClass() != null && !requestWithFilter.getAssetClass().isEmpty()) {
            criteriaList.add(Criteria.where("assetClass").is(requestWithFilter.getAssetClass()));
        }

        if (search) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("assetId").regex(Pattern.compile(value,
                            Pattern.CASE_INSENSITIVE)),
                    Criteria.where("assetClass").regex(Pattern.compile(value,
                            Pattern.CASE_INSENSITIVE)));
            criteriaList.add(searchCriteria);

        }

        MatchOperation matchStage = match(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        Aggregation aggregationData = Aggregation.newAggregation(
                matchStage,
                group("assetId")
                        .first("auditDate").as("auditDate")
                        .first("assetId").as("assetId")
                        .first("assetClass").as("assetClass")
                        .first("childId").as("childId")
                        .first("assetType").as("assetType")
                        .first("companyId").as("companyId")
                        .first("nextAuditDate").as("nextAuditDate")
                        .first("plant").as("plant")
                        .first("status").as("status")
                        .first("latitudeAndLongitude").as("latitudeAndLongitude")
                        .first("latestAudits.auditDate").as("latestAuditDate"),
                sort(Sort.Direction.ASC, "auditDate", "_id"), // Sort by auditDate first, then by _id
                skip((long) page * size),
                limit(size));

        List<AuditResponse> assets = mongoTemplate.aggregate(aggregationData, "asset", AuditResponse.class)
                .getMappedResults();

        Aggregation aggregation = Aggregation.newAggregation(
                // Aggregation.match(
                // Criteria.where("companyId").is(AuthUser.getCompanyId())
                // .and("plant").is(AuthUser.getPlant())
                // .and("latitudeAndLongitude").ne(null)
                // .and("availableStatus").ne(AuditStatus.Disposed.getValue())),
                matchStage,
                Aggregation.group("assetId"),
                Aggregation.group().count().as("total"),
                Aggregation.project("total"));

        // filter with page count end //

        List<AuditResponse> assetResponse = new ArrayList<>();

        for (AuditResponse auditResponse : assets) {
            LocalDate auditingDate = auditResponse.getAuditDate();

            if (auditingDate != null) {

                int auditingDay = auditingDate.getDayOfMonth();
                if (auditingDay == 31) {
                    auditingDay -= 1;
                    if (auditingDay == 0) {
                        auditingDay = LocalDate.now().minusMonths(1).lengthOfMonth();
                    }
                }

                LocalDate nextAuditDate;

                if (auditResponse.getNextAuditDate() == null) {
                    LocalDate currentDate = LocalDate.now();
                    nextAuditDate = currentDate.plusMonths(1).withDayOfMonth(auditingDay);
                    if (!nextAuditDate.isAfter(currentDate)) {
                        nextAuditDate = currentDate.plusMonths(1).withDayOfMonth(1).minusDays(1);
                    }
                } else {
                    nextAuditDate = auditResponse.getNextAuditDate();
                    // nextAuditDate = assets.get(0).getNextAuditDate();
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yyyy",
                        Locale.ENGLISH);

                if (nextAuditDate != null) {
                    LocalDate currentDate = LocalDate.now();
                    if (!nextAuditDate.isAfter(currentDate)) {
                        nextAuditDate = currentDate.plusMonths(1).withDayOfMonth(1).minusDays(1);
                    }
                    auditResponse.setAuditDateFormat(nextAuditDate.format(formatter));
                } else {
                    LocalDate currentDate = LocalDate.now();
                    nextAuditDate = currentDate.plusMonths(1).withDayOfMonth(auditingDay);
                    if (!nextAuditDate.isAfter(currentDate)) {
                        nextAuditDate = currentDate.plusMonths(1).withDayOfMonth(1).minusDays(1);
                    }
                    auditResponse.setAuditDateFormat(nextAuditDate.format(formatter));
                }
                assetResponse.add(auditResponse);
            } else {
                assetResponse.add(auditResponse);
            }

        }

        AggregationResults<AuditAssetCountResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
                Asset.class, AuditAssetCountResponse.class);
        AuditAssetCountResponse result = aggregationResults.getUniqueMappedResult();
        Long total = (result != null) ? result.getTotal() : 0L; // Default to 0 if result is null

        // Long total = mongoTemplate.aggregate(aggregation, Asset.class,
        // AuditAssetCountResponse.class).getUniqueMappedResult().getTotal();
        PendingAuditAssetResponse pendingAsset = new PendingAuditAssetResponse();
        pendingAsset.setAuditResponses(assetResponse);
        pendingAsset.setPendingAuditAssetCounts(total);

        return pendingAsset;
    }

    // old method
    // public PendingAuditAssetResponse getPendingAuditAsset(RequestWithFilter
    // RequestWithFilter, int page, int size) {

    // Sort sortByDescId = Sort.by(Sort.Direction.DESC, "id");
    // Pageable pageable = PageRequest.of(page, size, sortByDescId);

    // Page<AuditResponse> assetPageWithoutAudit ;
    // Page<AuditResponse> assetPageWithAudit ;

    // List<AuditResponse> assetResponse = new ArrayList<>();
    // List<AuditResponse> assetWithoutAudit = new ArrayList<>();
    // List<AuditResponse> assetWithAudit = new ArrayList<>();

    // long counts;
    // long countAssetWithoutAudit;
    // long countAssetWithAudit;
    // PendingAuditAssetResponse response = new PendingAuditAssetResponse();
    // String assetClass = RequestWithFilter.getAssetClass();
    // String assetTypeId = RequestWithFilter.getAssetTypeId();
    // String subClass = RequestWithFilter.getSubClass();

    // if (assetClass != null && !assetClass.isEmpty()) {
    // assetPageWithoutAudit =
    // assetRepository.findByCompanyIdAndPlantAndStatusAndAuditDateAndAssetClass(
    // AuthUser.getCompanyId(), AuthUser.getPlant(),
    // ActiveInActive.ACTIVE.getValue(), null,
    // assetClass, pageable);
    // assetPageWithAudit =
    // assetRepository.findByCompanyIdAndPlantAndStatusAndAuditDateIsNotNullAndAssetClass(
    // AuthUser.getCompanyId(), AuthUser.getPlant(),
    // ActiveInActive.ACTIVE.getValue(), assetClass, pageable);

    // countAssetWithoutAudit =
    // assetRepository.countByCompanyIdAndPlantAndStatusAndAuditDateAndAssetClass(
    // AuthUser.getCompanyId(), AuthUser.getPlant(),
    // ActiveInActive.ACTIVE.getValue(), null, assetClass);
    // countAssetWithAudit =
    // assetRepository.countByCompanyIdAndPlantAndStatusAndAuditDateIsNotNullAndAssetClass(
    // AuthUser.getCompanyId(), AuthUser.getPlant(),
    // ActiveInActive.ACTIVE.getValue(), assetClass);

    // } else {
    // assetPageWithoutAudit =
    // assetRepository.findByCompanyIdAndPlantAndStatusAndAuditDate(
    // AuthUser.getCompanyId(), AuthUser.getPlant(),
    // ActiveInActive.ACTIVE.getValue(), null, pageable);
    // assetPageWithAudit =
    // assetRepository.findByCompanyIdAndPlantAndStatusAndAuditDateIsNotNull(
    // AuthUser.getCompanyId(), AuthUser.getPlant(),
    // ActiveInActive.ACTIVE.getValue(), pageable);

    // countAssetWithoutAudit =
    // assetRepository.countByCompanyIdAndPlantAndStatusAndAuditDate(
    // AuthUser.getCompanyId(), AuthUser.getPlant(),
    // ActiveInActive.ACTIVE.getValue(), null);
    // countAssetWithAudit =
    // assetRepository.countByCompanyIdAndPlantAndStatusAndAuditDateIsNotNull(
    // AuthUser.getCompanyId(), AuthUser.getPlant(),
    // ActiveInActive.ACTIVE.getValue());
    // }

    // assetWithoutAudit = assetPageWithoutAudit.getContent();
    // assetWithAudit = assetPageWithAudit.getContent();

    // assetResponse.addAll(assetWithoutAudit);

    // for (AuditResponse auditResponse : assetWithAudit) {

    // LocalDate auditingDate = auditResponse.getAuditDate();
    // int auditingDay = auditingDate.getDayOfMonth();
    // if (auditingDay == 31) {
    // auditingDay -= 1;
    // if (auditingDay == 0) {
    // auditingDay = LocalDate.now().minusMonths(1).lengthOfMonth();
    // }
    // }
    // Pageable descByPageable = PageRequest.of(0, 1,
    // Sort.by(Sort.Order.desc("nextAuditDate")));
    // List<Audit> auditDetails =
    // auditRepository.findByAssetIdOrderByNextAuditDateDesc(auditResponse.getId(),
    // descByPageable);
    // LocalDate nextAuditDate;

    // if (auditDetails.isEmpty()) {
    // LocalDate currentDate = LocalDate.now();
    // nextAuditDate = currentDate.plusMonths(1).withDayOfMonth(auditingDay);
    // if (!nextAuditDate.isAfter(currentDate)) {
    // nextAuditDate = currentDate.plusMonths(1).withDayOfMonth(1).minusDays(1);
    // }
    // } else {
    // nextAuditDate = auditDetails.get(0).getNextAuditDate();
    // }
    // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yyyy",
    // Locale.ENGLISH);

    // if(nextAuditDate != null){
    // LocalDate currentDate = LocalDate.now();
    // if (!nextAuditDate.isAfter(currentDate)) {
    // nextAuditDate = currentDate.plusMonths(1).withDayOfMonth(1).minusDays(1);
    // }
    // auditResponse.setAuditDateFormat(nextAuditDate.format(formatter));
    // }else{
    // LocalDate currentDate = LocalDate.now();
    // nextAuditDate = currentDate.plusMonths(1).withDayOfMonth(auditingDay);
    // if (!nextAuditDate.isAfter(currentDate)) {
    // nextAuditDate = currentDate.plusMonths(1).withDayOfMonth(1).minusDays(1);
    // }
    // auditResponse.setAuditDateFormat(nextAuditDate.format(formatter));
    // }

    // assetResponse.add(auditResponse);
    // }
    // response.setAuditResponses(assetResponse);
    // counts = countAssetWithoutAudit+countAssetWithAudit;
    // response.setPendingAuditAssetCounts(counts);
    // return response;
    // }

    @Override
    public List<AuditResponse> auditorFetchById(String id) {
        AuditResponse assetAuditList = assetRepository.findByIdAndCompanyIdAndPlantAndStatusNotAndAuditDateNotNull(id,
                AuthUser.getCompanyId(), AuthUser.getPlant(), AuditStatus.Disposed.getValue());
        List<AuditResponse> assetsWithLatestAudit = new ArrayList<>();

        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Order.desc(Constant.AUDIT_DATE)));
        List<Audit> audits = auditRepository.findByAssetIdOrderByAuditDateDesc(assetAuditList.getId(), pageable);

        if (!audits.isEmpty()) {
            Audit latestAudit = audits.get(0);
            LocalDate newAuditDate = latestAudit.getAuditDate().plusMonths(1);
            assetAuditList.setAuditDate(newAuditDate);
        } else {
            assetAuditList.setAuditDate(assetAuditList.getAuditDate());
        }
        assetsWithLatestAudit.add(assetAuditList);
        return assetsWithLatestAudit;
    }

    @Override
    public Asset uploadPreImage(MultipartFile file, String id) throws IOException {

        final List<String> allowedImageExtensions = Arrays.asList("jpg", "jpeg", "png");

        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constant.DATA_NOT_FOUND));
        String originalName = file.getOriginalFilename();
        String extension;
        if (originalName != null) {
            extension = getFileExtension(originalName);
        } else {
            extension = null;
        }
        if (extension == null || !allowedImageExtensions.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("Invalid image format. Only JPG, JPEG, and PNG files are allowed.");
        }
        String fileName = Format.formatDate() + "_" + originalName;
        Path path = Path.of(fileBasePath + fileName);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        asset.setPicture(fileName);
        assetRepository.save(asset);
        return asset;
    }

    private String getFileExtension(String originalName) {
        int lastIndex = originalName.lastIndexOf(".");
        if (lastIndex == -1) {
            return null;
        }
        return originalName.substring(lastIndex + 1);
    }

    @Override
    public Audit createAudit(MultipartFile file, AuditRequest auditRequest)
            throws IOException, TemplateException, RuntimeException {
        Audit audit = new Audit();
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("companyId").is(AuthUser.getCompanyId())
                .and("plant").is(AuthUser.getPlant())
                .and("status").is(ActiveInActive.ACTIVE.getValue())
                .and("availableStatus").nin(AuditStatus.Disposed.getValue(), DisposedStatus.Replaced.getValue())
                .and("auditDate").exists(true)
                .and("assetId").is(auditRequest.getId())
                .and("latitudeAndLongitude").ne(null));

        MatchOperation matchStage = match(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));

        Aggregation aggregation = Aggregation.newAggregation(
                matchStage,
                // Aggregation.match(Criteria.where("assetId").is(auditRequest.getId()).and("latitudeAndLongitude").ne(null)),

                Aggregation.group("assetId")
                        .first("companyId").as("companyId")
                        .first("plant").as("plant")
                        .first("auditDate").as("auditDate")
                        .first("nextAuditDate").as("nextAuditDate")
                        .first("assetId").as("assetId")
                        .first("assetClass").as("assetClass")
                        .first("picture").as("picture")
                        .first("status").as("status"));

        AggregationResults<Asset> results = mongoTemplate.aggregate(aggregation, "asset", Asset.class);
        Asset asset = results.getUniqueMappedResult();
        final List<String> allowedImageExtensions = Arrays.asList("jpg", "jpeg", "png");
        String originalName = file.getOriginalFilename();
        String extension;
        if (originalName != null) {
            extension = getFileExtension(originalName);
        } else {
            extension = null;
        }
        if (extension == null || !allowedImageExtensions.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("Invalid image format. Only JPG, JPEG, and PNG files are allowed.");
        }
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc(Constant.AUDIT_DATE)));
        List<Audit> audits = auditRepository.findByAssetIdOrderByAuditDateDesc(asset.getAssetId(), pageable);
        Collections.sort(audits, Comparator.comparing(Audit::getCreatedAt).reversed());
        if (!audits.isEmpty()) {
            Audit latestAudit = audits.get(0);
            audit.setPreviewImage(latestAudit.getCurrentImage());
        } else {
            audit.setPreviewImage(asset.getPicture());
        }
        if (file != null && !file.isEmpty()) {
            String fileName = Format.formatDate() + "_" + originalName;
            Path path = Path.of(fileBasePath + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            audit.setCurrentImage(fileName);
        } else {
            String fileName = "no_image.png";
            audit.setCurrentImage(fileName);
        }
        audit.setRemark(auditRequest.getRemark());
        audit.setWithCondition(auditRequest.getWithCondition());
        // double length = calculateDistance(auditRequest.getLatPre(),
        // auditRequest.getLongPre(), auditRequest.getLatCur(),
        // auditRequest.getLongCur());

        // if (length >= 200) {
        // audit.setStatus(AuditStatus.Rejected.getValue());
        // Map<String, Object> content = new HashMap<>();
        // List<Users> user =
        // userRepository.findByCompanyIdAndPlantAndRole(AuthUser.getCompanyId(),
        // AuthUser.getPlant(), Constant.USER_ROLE);
        // List<String> recipientEmails = new ArrayList<>();
        // for (Users users : user) {
        // recipientEmails.add(users.getEmail());
        // }
        //// String recipientEmails=user.get(0).getEmail();
        // String assetId = asset.getAssetId();
        // String subject = "Audit Rejected";
        // content.put("data", assetId);
        // content.put("role", Constant.USER_ROLE);
        // String emailSent = mailService.sendRejectedMail(content, subject,
        // recipientEmails);
        // if ("success".equals(emailSent)) {
        // System.out.println("Rejection email sent successfully to users");
        // } else {
        // throw new RuntimeException("Failed to send email to Users");
        // }
        // } else {
        // audit.setStatus(AuditStatus.Approved.getValue());
        // }
        // if (auditRequest.getStatus().equals(AuditStatus.Disposed.getValue())) {
        // audit.setStatus(AuditStatus.Waiting.getValue());
        // } else if (auditRequest.getStatus().equals(AuditStatus.Approved.getValue()))
        // {
        // audit.setStatus(auditRequest.getStatus());
        // }
        if (AuditStatus.Approved.getValue().equals(auditRequest.getStatus())) {
            audit.setStatus(auditRequest.getStatus());
        } else {
            audit.setStatus(auditRequest.getStatus());
            Map<String, Object> content = new HashMap<>();
            // Mailconcept
            // List<Users> user =
            // userRepository.findByCompanyIdAndPlantAndRole(AuthUser.getCompanyId(),
            // AuthUser.getPlant(), Constant.USER_ROLE);
            // List<String> recipientEmails = new ArrayList<>();
            // for (Users users : user) {
            // recipientEmails.add(users.getEmail());
            // }
            // String assetId = asset.getAssetId();
            // String subject = "Audit Rejected";
            // content.put("data", assetId);
            // content.put("role", Constant.USER_ROLE);
            // String emailSent = mailService.sendRejectedMail(content, subject,
            // recipientEmails);
            // if ("success".equals(emailSent)) {
            // System.out.println("Rejection email sent successfully to users");
            // } else {
            // throw new RuntimeException("Failed to send email to Users");
            // }
        }

        audit.setAssetId(asset.getAssetId());
        audit.setPlant(AuthUser.getPlant());
        LocalDate assetAuditDate = asset.getAuditDate();
        // LocalDate currentDate = LocalDate.now();
        // LocalDate currentDate = LocalDate.parse("2024-05-01");
        LocalDate currentDate = asset.getNextAuditDate();
        int userAuditDay = assetAuditDate.getDayOfMonth();

        LocalDate auditDateToFix;

        // int daysInMonth = currentDate.lengthOfMonth();
        int nextdaysInMonth = currentDate.plusMonths(1).lengthOfMonth();
        LocalDate nextMonth = currentDate.plusMonths(1);
        Optional<Users> user = userRepository.findById(AuthUser.getUserId());
        user.ifPresent(audit::setAuditBy);
        audit.setAuditFixedDate(assetAuditDate);
        audit.setAuditDate(LocalDate.now());

        // Calculate the next audit date
        if (userAuditDay > nextdaysInMonth) {
            userAuditDay = nextdaysInMonth;
        } else if (userAuditDay == nextdaysInMonth) {
            userAuditDay = userAuditDay;
        }
        auditDateToFix = LocalDate.of(nextMonth.getYear(), nextMonth.getMonthValue(), userAuditDay);
        audit.setNextAuditDate(auditDateToFix);

        // Update the next audit date in MongoDB
        Criteria filterCriteria = Criteria.where("assetId").is(auditRequest.getId())
                .and("plant").is(asset.getPlant())
                .and("companyId").is(asset.getCompanyId());
        Update update = Update.update("nextAuditDate", audit.getNextAuditDate());
        Query query = Query.query(filterCriteria);
        mongoTemplate.updateFirst(query, update, Asset.class);

        return auditRepository.save(audit);
    }

    public boolean isValidDate(LocalDate date) {
        try {
            date.with(TemporalAdjusters.lastDayOfMonth());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // public double calculateDistance(double lat1, double lon1, double lat2, double
    // lon2) {
    // double radius = 6371e3; // Earth radius in meters
    // double x = Math.toRadians(lat1); // Convert latitude 1 to radians
    // double y = Math.toRadians(lat2); // Convert latitude 2 to radians
    // double xx = Math.toRadians(lat2 - lat1); // Difference in latitudes in
    // radians
    // double yy = Math.toRadians(lon2 - lon1); // Difference in longitudes in
    // radians
    // double a = Math.sin(xx / 2) * Math.sin(xx / 2) +
    // Math.cos(x) * Math.cos(y) * Math.sin(yy / 2) * Math.sin(yy / 2);
    // double calculation = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    // return radius * calculation;
    // }

    public AuditorResponse assetAuditByStatus(RequestWithFilter requestWithFilter) {
        int page = requestWithFilter.getPage() == null ? 0 : requestWithFilter.getPage();
        int size = requestWithFilter.getSize() == null ? 10 : requestWithFilter.getSize();
        Boolean search = requestWithFilter.getSearch() == null ? false : requestWithFilter.getSearch();
        String value = requestWithFilter.getValue() == null ? null : requestWithFilter.getValue();

        Criteria criteria = Criteria.where("status").is(requestWithFilter.getStatus())
                .and("latestAudits.companyId").is(AuthUser.getCompanyId())
                .and("latestAudits.plant").is(AuthUser.getPlant())
                .and("latestAudits.availableStatus")
                .nin(AuditStatus.Disposed.getValue(), DisposedStatus.Replaced.getValue());
        if (requestWithFilter.getAssetClass() != null) {
            criteria.and("latestAudits.assetClass").is(requestWithFilter.getAssetClass());
        }
        if (search && value != null && !value.isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("assetId").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("latestAudits.assetClass").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE))
            // Criteria.where("nextAuditDate").regex(Pattern.compile(value,
            // Pattern.CASE_INSENSITIVE))
            );
            criteria.andOperator(searchCriteria);
        }
        long count = getApprovedRejectCount(requestWithFilter, search, value);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.lookup("asset", "assetId", "assetId", "latestAudits"),
                Aggregation.unwind("latestAudits"),
                Aggregation.match(criteria),
                Aggregation.sort(Sort.Direction.DESC, "createdAt"),

                Aggregation.group("$assetId")
                        .first("_id").as("oid")
                        .first("_id").as("oid")
                        .first("latestAudits.assetId").as("assetId")
                        .first("latestAudits.assetClass").as("assetClass")
                        .first("auditDate").as("auditDate")
                        .first("latestAudits.plant").as("plant")
                        .first("status").as("status")
                        .first("remark").as("remark")
                        .first("previewImage").as("previewImage")
                        .first("currentImage").as("currentImage")
                        .first("withCondition").as("withCondition")
                        .first("createdAt").as("createdAt")
                        .first("updatedAt").as("updatedAt")
                        .first("createdBy").as("createdBy")
                        .first("updatedBy").as("updatedBy"),
                Aggregation.sort(Sort.Direction.DESC, "createdAt"),
                Aggregation.skip(page * size),
                Aggregation.limit(size),
                Aggregation.project()
                        .andInclude("_id")
                        .and("oid").as("_id")
                        .and("assetId").as("assetId")
                        .and("assetClass").as("assetClass")
                        .and("auditDate").as("date")
                        .and("plant").as("plant")
                        .and("status").as("status")
                        .and("previewImage").as("previewImage")
                        .and("currentImage").as("currentImage")
                        .and("withCondition").as("withCondition")
                        .and("remark").as("remark")
                        .and("createdAt").as("createdAt")
                        .and("updatedAt").as("updatedAt")
                        .and("createdBy").as("createdBy")
                        .and("updatedBy").as("updatedBy"));

        AggregationResults<AuditorResponseDTO> aggregationResults = mongoTemplate.aggregate(aggregation, "audit",
                AuditorResponseDTO.class);
        List<AuditorResponseDTO> auditResponses = aggregationResults.getMappedResults();
        AuditorResponse response = new AuditorResponse();
        response.setAuditsData(auditResponses);
        response.setAuditStatusCounts(count);
        return response;
    }

    // Method to convert Audit to AuditDto
    private AuditDto convertToAuditDto(Audit audit) {
        AuditDto auditDto = new AuditDto();
        auditDto.setId(audit.getId());
        auditDto.setAuditDate(audit.getAuditDate());
        auditDto.setCurrentImage(audit.getCurrentImage());
        auditDto.setPreviewImage(audit.getPreviewImage());
        auditDto.setPreviewImageWithPath(audit.getPreviewImageWithPath());
        auditDto.setCurrentImageWithPath(audit.getCurrentImageWithPath());
        auditDto.setRemark(audit.getRemark());
        auditDto.setStatus(audit.getStatus());
        auditDto.setAuditFixedDate(audit.getAuditFixedDate());
        return auditDto;
    }

    @Override
    public PendingAuditAssetResponse auditorFetch(RequestWithFilter requestWithFilter, Integer page, Integer size) {
        if (page == null || size == null || page < 0 || size <= 0) {
            page = 0;
            size = 10;
        }
        long skipCount = (long) page * size;
        List<Criteria> criteriaList = new ArrayList<>();
        Boolean search = requestWithFilter.getSearch() == null ? false : requestWithFilter.getSearch();
        String value = requestWithFilter.getValue() == null ? null : requestWithFilter.getValue();

        criteriaList.add(Criteria.where("companyId").is(AuthUser.getCompanyId())
                .and("plant").is(AuthUser.getPlant())
                .and("status").is(ActiveInActive.ACTIVE.getValue())
                .and("availableStatus").nin(AuditStatus.Disposed.getValue(), DisposedStatus.Replaced.getValue())
                .and("auditDate").exists(true)
                .and("latitudeAndLongitude").exists(true));

        // Add additional match stages for each filter condition
        if (requestWithFilter.getAssetClass() != null && !requestWithFilter.getAssetClass().isEmpty()) {
            criteriaList.add(Criteria.where("assetClass").is(requestWithFilter.getAssetClass()));
        }

        if (requestWithFilter.getAssetNo() != null && !requestWithFilter.getAssetNo().isEmpty()) {
            criteriaList.add(Criteria.where("assetId").is(requestWithFilter.getAssetNo()));
        }

        if (requestWithFilter.getChildId() != null && !requestWithFilter.getChildId().isEmpty()) {
            criteriaList.add(Criteria.where("childId").is(requestWithFilter.getChildId()));
        }
        // if (search && value != null && !value.isEmpty()) {
        // Criteria searchCriteria = new Criteria().orOperator(
        // Criteria.where("assetId").regex(Pattern.compile(value,
        // Pattern.CASE_INSENSITIVE)),
        // Criteria.where("assetClass").regex(Pattern.compile(value,
        // Pattern.CASE_INSENSITIVE)));
        // // requestWithFilter.getEnv()
        // if ("mobile".equals("mobile1")) {
        // searchCriteria.orOperator(
        // Criteria.where("description").regex(Pattern.compile(value,
        // Pattern.CASE_INSENSITIVE)),
        // Criteria.where("assetStatus").regex(Pattern.compile(value,
        // Pattern.CASE_INSENSITIVE)));
        // }
        // criteriaList.add(searchCriteria);

        // }

        if (search && value != null && !value.isEmpty()) {
            List<Criteria> criteriaToCombine = new ArrayList<>();

            criteriaToCombine.add(Criteria.where("assetId").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)));
            criteriaToCombine.add(Criteria.where("assetClass").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)));

            if ("mobile".equals(requestWithFilter.getEnv())) {
                criteriaToCombine
                        .add(Criteria.where("description").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)));
                criteriaToCombine
                        .add(Criteria.where("assetStatus").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)));
            }

            Criteria searchCriteria = new Criteria().orOperator(criteriaToCombine.toArray(new Criteria[0]));
            criteriaList.add(searchCriteria);
        }

        MatchOperation matchStage = match(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        Aggregation aggregationData = Aggregation.newAggregation(
                matchStage,
                sort(Sort.Direction.ASC, "nextAuditDate"),
                group(
                        "assetId")
                        .first("auditDate").as("auditDate")
                        .first("assetId").as("assetId")
                        .first("assetClass").as("assetClass")
                        .first("childId").as("childId")
                        .first("picture").as("previewImage")
                        .first("companyId").as("companyId")
                        .first("plant").as("plant")
                        .first("assetStatus").as("assetStatus")
                        .first("description").as("description")
                        .first("status").as("status")
                        .first("latitudeAndLongitude").as("latitudeAndLongitude")
                        .first("nextAuditDate").as("nextAuditDate"),
                skip(skipCount),
                limit(size));

        List<AuditResponse> assets = mongoTemplate.aggregate(aggregationData, "asset", AuditResponse.class)
                .getMappedResults();
        Aggregation aggregation = Aggregation.newAggregation(
                matchStage,
                Aggregation.group("assetId"),
                Aggregation.group().count().as("total"),
                Aggregation.project("total"));
        AggregationResults<AuditAssetCountResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
                Asset.class, AuditAssetCountResponse.class);
        AuditAssetCountResponse result = aggregationResults.getUniqueMappedResult();
        Long total = (result != null) ? result.getTotal() : 0L;
        PendingAuditAssetResponse pendingAsset = new PendingAuditAssetResponse();
        pendingAsset.setAuditResponses(assets);
        pendingAsset.setPendingAuditAssetCounts(total);
        return pendingAsset;
    }

    @Override
    public List<Asset> saveAuditDate(String assetId, LocalDate auditDate) {

        List<Asset> assets = assetRepository.findAllByAssetIdAndCompanyIdAndPlantAndStatus(assetId,
                AuthUser.getCompanyId(), AuthUser.getPlant(), ActiveInActive.ACTIVE.getValue());

        if (!assets.isEmpty()) {
            List<Asset> updatedAssets = new ArrayList<>();
            LocalDate currentDate = LocalDate.now();
            // LocalDate currentDate = LocalDate.parse("2024-03-02");

            int currentYear = currentDate.getYear();
            int currentMonth = currentDate.getMonthValue();
            int userAuditDay = auditDate.getDayOfMonth();
            LocalDate nextAuditDate;

            LocalDate auditDateToFix = currentDate;

            int daysInMonth = auditDateToFix.getMonth()
                    .length(auditDateToFix.getYear() % 4 == 0 && currentMonth == 2);

            if (userAuditDay > daysInMonth) {
                userAuditDay = daysInMonth;
                auditDateToFix = LocalDate.of(currentYear, currentMonth, userAuditDay);
            } else {
                auditDateToFix = LocalDate.of(currentYear, currentMonth, auditDate.getDayOfMonth());
            }
            // Find the latest audit for this asset
            List<Audit> audits = auditRepository.findByAssetId(assetId, Sort.by(Sort.Direction.DESC, "id"));
            if (!audits.isEmpty()) {
                Audit latestAudit = audits.get(0);
                LocalDate existingDate = latestAudit.getNextAuditDate();
                int existingDateLength = existingDate.lengthOfMonth();

                if (userAuditDay > existingDateLength) {
                    latestAudit.setNextAuditDate(existingDate.withDayOfMonth(existingDateLength));
                    nextAuditDate = existingDate.withDayOfMonth(existingDateLength);

                } else {
                    latestAudit.setNextAuditDate(existingDate.withDayOfMonth(userAuditDay));
                    nextAuditDate = existingDate.withDayOfMonth(userAuditDay);

                }
                auditRepository.save(latestAudit);
            } else {
                nextAuditDate = auditDateToFix.plusMonths(1);
            }
            for (Asset asset : assets) {
                asset.setNextAuditDate(nextAuditDate);
                asset.setAuditDate(auditDateToFix);
                updatedAssets.add(assetRepository.save(asset));
            }

            return updatedAssets;
        } else {
            throw new ResourceNotFoundException(Constant.DATA_NOT_FOUND);
        }
    }

    @Override
    public RequestAuditAssetResponse getRequestAuditAsset(Integer page, Integer size, String assetClass, boolean search,
            String value) {
        page = page == null ? 0 : page;
        size = size == null ? 10 : size;
        long skipCount = (long) page * size;

        Criteria criteria = Criteria.where("companyId").is(AuthUser.getCompanyId())
                .and("plant").is(AuthUser.getPlant())
                .and("availableStatus").nin(AuditStatus.Disposed.getValue(), DisposedStatus.Replaced.getValue())
                .and("auditDate").exists(true)
                .and("latitudeAndLongitude").exists(true)
                .and("nextAuditDate").lte(LocalDate.now().minusDays(10));

        if (assetClass != null) {
            criteria = criteria.and("assetClass").is(assetClass);
        }
        if (search && value != null && !value.isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("assetClass").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("assetId").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)));
            criteria.andOperator(searchCriteria);
        }
        long count = getAuditAssetCount(criteria);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.sort(Sort.Direction.DESC, "id"),
                Aggregation.group("assetId")
                        .first("auditDate").as("auditDate")
                        .first("assetId").as("assetId")
                        .first("assetClass").as("assetClass")
                        .first("childId").as("childId")
                        .first("picture").as("previewImage")
                        .first("companyId").as("companyId")
                        .first("plant").as("plant")
                        .first("description").as("description")
                        .first("status").as("status")
                        .first("latitudeAndLongitude").as("latitudeAndLongitude")
                        .first("nextAuditDate").as("nextAuditDate"),
                Aggregation.skip(skipCount),
                Aggregation.limit(size));
        AggregationResults<AuditResponse> results = mongoTemplate.aggregate(aggregation, "asset", AuditResponse.class);
        List<AuditResponse> assetAuditList = results.getMappedResults();
        RequestAuditAssetResponse response = new RequestAuditAssetResponse();
        response.setRequestAuditAssetCounts(count);
        response.setAuditResponses(assetAuditList);
        return response;
    }

    private long getAuditAssetCount(Criteria criteria) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("assetId"),
                Aggregation.group().count().as("count"),
                Aggregation.project("count"));
        AggregationResults<CountDTO> aggregationResults = mongoTemplate.aggregate(aggregation, "asset", CountDTO.class);
        List<CountDTO> counts = aggregationResults.getMappedResults();
        return counts.isEmpty() ? 0 : counts.get(0).getCount();
    }

    // public RequestAuditAssetResponse getRequestAuditAsset(Integer page, Integer
    // size) {
    // Sort sortById = Sort.by(Sort.Direction.DESC, "id");
    // Page<AuditResponse> assetAuditListWithPage;
    // long counts;

    // PageRequest pageable = PageRequest.of(page, size, sortById);
    // RequestAuditAssetResponse response = new RequestAuditAssetResponse();
    // assetAuditListWithPage =
    // assetRepository.findByCompanyIdAndPlantAndStatusNotAndAuditDateNotNull(
    // AuthUser.getCompanyId(), AuthUser.getPlant(),
    // AuditStatus.Disposed.getValue(), pageable);
    // counts =
    // assetRepository.countByCompanyIdAndPlantAndStatusNotAndAuditDateNotNull(
    // AuthUser.getCompanyId(), AuthUser.getPlant(),
    // AuditStatus.Disposed.getValue());
    // List<AuditResponse> assetAuditList = assetAuditListWithPage.getContent();
    // List<AuditResponse> requestResponse = new ArrayList<>();

    // for (AuditResponse asset : assetAuditList) {
    // Pageable pageableSort = PageRequest.of(0, 1,
    // Sort.by(Sort.Order.desc(Constant.AUDIT_DATE)));
    // List<Audit> audits =
    // auditRepository.findByAssetIdOrderByAuditDateDesc(asset.getId(),
    // pageableSort);

    // if (!audits.isEmpty()) {
    // Audit latestAudit = audits.get(0);
    // LocalDate newAuditDate = latestAudit.getAuditDate();
    // asset.setAuditDateFormat(Format.nextDateWillBe(newAuditDate));

    // LocalDate currentDate = LocalDate.now();
    // LocalDate twoMonthsAgo = currentDate.minusMonths(2).withDayOfMonth(25);

    // if (newAuditDate.isBefore(twoMonthsAgo) ||
    // newAuditDate.isEqual(twoMonthsAgo)) {
    // requestResponse.add(asset);
    // }
    // }
    // }
    // response.setRequestAuditAssetCounts(counts);
    // response.setAuditResponses(requestResponse);
    // return response;
    // }

    @Override
    public RequestAuditAssetResponse getAuditCompletedAsset(RequestWithFilter requestWithFilter, int page, int size,
            boolean search, String value) {

        List<AuditResponse> assetAuditList = new ArrayList<>();

        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("assets.companyId").is(AuthUser.getCompanyId())
                .and("assets.plant").is(AuthUser.getPlant())
                .and("assets.status").ne(AuditStatus.Disposed.getValue()));

        // // // Add additional match stages for each filter condition
        // if (requestWithFilter.getAssetClass() != null &&
        // !requestWithFilter.getAssetClass().isEmpty()) {
        // criteriaList.add(Criteria.where("assets.assetClass").is(requestWithFilter.getAssetClass()));
        // }
        // if(search){
        // // Create a Criteria object with a regex for partial matching
        // criteriaList.add(Criteria.where("assets.assetId").regex(Pattern.compile(Pattern.quote(value))));
        // }

        MatchOperation matchStage = match(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        Aggregation aggregationData = Aggregation.newAggregation(
                lookup(
                        "asset",
                        "assetId",
                        "assetId",
                        "assets"),
                unwind("assets"),
                matchStage,
                group()
                        .first("assets.auditDate").as("auditDate")
                        .first("assets.assetId").as("assetId")
                        .first("assets.assetClass").as("assetClass")
                        .first("assets.childId").as("childId")
                        .first("assets.assetType").as("assetType")
                        .first("assets.latestAudits").as("audit")
                        .first("assets.companyId").as("companyId")
                        .first("assets.plant").as("plant")
                        .first("assets.status").as("status")
                        .first("assets.latitudeAndLongitude").as("latitudeAndLongitude")
                        .first("latestauditDate").as("latestAuditDate")
                        .first("nextAuditDate").as("nextAuditDate"),
                // sort(Sort.Direction.ASC, "nextAuditDate", "_id"), // Sort by auditDate first,
                // then by _id
                skip((long) page * size),
                limit(size));

        List<AuditResponse> assetResponse = mongoTemplate.aggregate(aggregationData, "audit", AuditResponse.class)
                .getMappedResults();

        Long total = 45L;
        RequestAuditAssetResponse completedAsset = new RequestAuditAssetResponse();
        completedAsset.setAuditResponses(assetResponse);
        completedAsset.setRequestAuditAssetCounts(total);

        return completedAsset;
        // String assetClass = RequestWithFilter.getAssetClass();
        // // String assetNo = RequestWithFilter.getAssetNo();

        // if (assetClass != null && !assetClass.isEmpty()) {
        // assetAuditList =
        // assetRepository.findByCompanyIdAndPlantAndStatusNotAndAuditDateNotNullAndAssetClass(
        // AuthUser.getCompanyId(), AuthUser.getPlant(),
        // AuditStatus.Disposed.getValue(), assetClass);
        // } else {
        // assetAuditList =
        // assetRepository.findByCompanyIdAndPlantAndStatusNotAndAuditDateNotNull(
        // AuthUser.getCompanyId(),
        // AuthUser.getPlant(),AuditStatus.Disposed.getValue());
        // }

        // List<AuditResponse> requestResponse = new ArrayList<>();

        // for (AuditResponse asset : assetAuditList) {
        // List<Audit> assetbyAudit =
        // auditRepository.findByAssetIdAndStatus(asset.getId(),
        // AuditStatus.Approved.getValue());
        // if (!assetbyAudit.isEmpty()) {
        // asset.setAuditCount(assetbyAudit.size());

        // LocalDate auditingDate = asset.getAuditDate();
        // int auditingDay = auditingDate.getDayOfMonth();

        // Pageable descByPageable = PageRequest.of(0, 1,
        // Sort.by(Sort.Order.desc("nextAuditDate")));
        // List<Audit> auditDetails =
        // auditRepository.findByAssetIdOrderByNextAuditDateDesc(asset.getId(),
        // descByPageable);
        // LocalDate nextAuditDate;
        // nextAuditDate = auditDetails.get(0).getNextAuditDate();

        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yyyy",
        // Locale.ENGLISH);

        // asset.setAuditDateFormat(nextAuditDate.format(formatter));
        // requestResponse.add(asset);
        // }
        // }

        // return requestResponse;
    }

    @Override
    public List<AuditResponse> auditorFetchByAssetId(String assetId) {
        AuditResponse assetAuditList = assetRepository.findByAssetIdAndCompanyIdAndPlantAndStatusNotAndAuditDateNotNull(
                assetId,
                AuthUser.getCompanyId(), AuthUser.getPlant(), AuditStatus.Disposed.getValue());
        List<AuditResponse> assetsWithLatestAudit = new ArrayList<>();

        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Order.desc(Constant.AUDIT_DATE)));
        List<Audit> audits = auditRepository.findByAssetIdOrderByAuditDateDesc(assetAuditList.getId(), pageable);

        if (!audits.isEmpty()) {
            Audit latestAudit = audits.get(0);
            LocalDate newAuditDate = latestAudit.getAuditDate().plusMonths(1);
            assetAuditList.setAuditDate(newAuditDate);
        } else {
            assetAuditList.setAuditDate(assetAuditList.getAuditDate());
        }
        assetsWithLatestAudit.add(assetAuditList);
        return assetsWithLatestAudit;
    }

    @Override
    public List<Audit> allAuditDataByAssetId(String assetId) {
        return auditRepository.findByAssetId(assetId);
    }

    public List<AuditHistoryResponse> getAuditCompletedHistory(String assetId) {

        return auditRepository.findAllByAssetIdAndStatus(assetId,
                AuditStatus.Approved.getValue());

    }

    // if (assetTypeId != null && !assetTypeId.isEmpty()) {
    // assetPageWithoutAudit =
    // assetRepository.findByCompanyIdAndPlantAndStatusAndAuditDateAndAssetClassAndAssetType(
    // AuthUser.getCompanyId(), AuthUser.getPlant(),
    // ActiveInActive.ACTIVE.getValue(), null,
    // assetClass, assetTypeId, pageable);
    // assetPageWithAudit =
    // assetRepository.findByCompanyIdAndPlantAndStatusAndAuditDateIsNotNullAndAssetClassAndAssetType(
    // AuthUser.getCompanyId(), ActiveInActive.ACTIVE.getValue(), assetClass,
    // assetTypeId, pageable);

    // countAssetWithoutAudit =
    // assetRepository.countByCompanyIdAndPlantAndStatusAndAuditDateAndAssetClassAndAssetType(
    // AuthUser.getCompanyId(), AuthUser.getPlant(),
    // ActiveInActive.ACTIVE.getValue(), null,
    // assetClass, assetTypeId);
    // countAssetWithAudit =
    // assetRepository.countByCompanyIdAndPlantAndStatusAndAuditDateIsNotNullAndAssetClassAndAssetType(
    // AuthUser.getCompanyId(), ActiveInActive.ACTIVE.getValue(), assetClass,
    // assetTypeId);
    // }

    // } else if (subClass != null && !subClass.isEmpty()) {
    // assetPageWithoutAudit =
    // assetRepository.findByCompanyIdAndPlantAndStatusAndAuditDateAndSubClass(
    // AuthUser.getCompanyId(), AuthUser.getPlant(),
    // ActiveInActive.ACTIVE.getValue(), null,
    // subClass, pageable);
    // assetPageWithAudit =
    // assetRepository.findByCompanyIdAndPlantAndStatusAndAuditDateIsNotNullAndSubClass(
    // AuthUser.getCompanyId(), AuthUser.getPlant(),
    // ActiveInActive.ACTIVE.getValue(), subClass, pageable);

    // countAssetWithoutAudit =
    // assetRepository.countByCompanyIdAndPlantAndStatusAndAuditDateAndSubClass(
    // AuthUser.getCompanyId(), AuthUser.getPlant(),
    // ActiveInActive.ACTIVE.getValue(), null,
    // subClass);
    // countAssetWithAudit =
    // assetRepository.countByCompanyIdAndPlantAndStatusAndAuditDateIsNotNullAndSubClass(
    // AuthUser.getCompanyId(), AuthUser.getPlant(),
    // ActiveInActive.ACTIVE.getValue(), subClass);

    @Override
    public long getApprovedRejectCount(RequestWithFilter requestWithFilter, boolean search, String value) {
        Criteria criteria = Criteria.where("status").is(requestWithFilter.getStatus())
                .and("latestAudits.companyId").is(AuthUser.getCompanyId())
                .and("latestAudits.plant").is(AuthUser.getPlant())
                .and("latestAudits.availableStatus")
                .nin(AuditStatus.Disposed.getValue(), DisposedStatus.Replaced.getValue());
        if (requestWithFilter.getAssetClass() != null) {
            criteria.and("assetClass").is(requestWithFilter.getAssetClass());
        }
        if (search && value != null && !value.isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("assetId").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("latestAudits.assetClass").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE))
            // Criteria.where("nextAuditDate").regex(Pattern.compile(value,
            // Pattern.CASE_INSENSITIVE)) date search not working
            );
            criteria.andOperator(searchCriteria);
        }
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.lookup("asset", "assetId", "assetId", "latestAudits"),
                Aggregation.match(criteria),
                Aggregation.group("assetId"));
        AggregationResults<CountDTO> aggregationResults = mongoTemplate.aggregate(aggregation, "audit", CountDTO.class);
        List<CountDTO> counts = aggregationResults.getMappedResults();
        return counts.size();
    }

    @Override
    public AuditCompletedResponse getCompletedAudit(RequestWithFilter requestWithFilter) {

        int page = requestWithFilter.getPage() == null ? 0 : requestWithFilter.getPage();
        int size = requestWithFilter.getSize() == null ? 10 : requestWithFilter.getSize();

        Boolean search = requestWithFilter.getSearch() == null ? false : requestWithFilter.getSearch();
        String value = requestWithFilter.getValue() == null ? null : requestWithFilter.getValue();

        Criteria criteria = Criteria.where("status")
                .in(TransferStatus.Approved.getValue(), TransferStatus.Rejected.getValue())
                .and("latestAudits.companyId").is(AuthUser.getCompanyId())
                .and("latestAudits.plant").is(AuthUser.getPlant())
                .and("latestAudits.availableStatus")
                .nin(AuditStatus.Disposed.getValue(), DisposedStatus.Replaced.getValue());

        if (requestWithFilter.getAssetClass() != null) {
            criteria.and("latestAudits.assetClass").is(requestWithFilter.getAssetClass());
        }
        if (search && value != null && !value.isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("assetId").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("latestAudits.assetClass").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("remark").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)));
            criteria.andOperator(searchCriteria);
        }

        long skipCount = page * size;
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.lookup("asset", "assetId", "assetId", "latestAudits"),
                Aggregation.unwind("latestAudits"),
                Aggregation.match(criteria),
                Aggregation.sort(Sort.Direction.DESC, "createdAt"),
                Aggregation.group("$assetId")
                        .first("_id").as("oid")
                        .first("assetId").as("assetId")
                        .first("latestAudits.assetClass").as("assetClass")
                        .first("latestAudits.plant").as("plant")
                        .first("remark").as("remark")
                        .first("auditDate").as("auditDate")
                        .first("nextAuditDate").as("nextAuditDate")
                        .first("createdAt").as("createdAt")
                        .first("status").as("status")
                        .count().as("statusCount"),
                Aggregation.sort(Sort.Direction.DESC, "createdAt"),
                Aggregation.skip(skipCount),
                Aggregation.limit(size),
                Aggregation.project()
                        .andInclude("_id")
                        .and("oid").as("_id")
                        .and("assetId").as("assetId")
                        .and("assetClass").as("assetClass")
                        .and("plant").as("plant")
                        .and("status").as("status")
                        .and("statusCount").as("statusCount")
                        .and("auditDate").as("auditDate")
                        .and("nextAuditDate").as("nextAuditDate")
                        .and("createdAt").as("createdAt")
                        .and("remark").as("remark"));

        AggregationResults<AuditCompletedDTO> aggregationResults = mongoTemplate.aggregate(aggregation, "audit",
                AuditCompletedDTO.class);
        List<AuditCompletedDTO> auditResponses = aggregationResults.getMappedResults();

        Aggregation countAggregation = Aggregation.newAggregation(
                Aggregation.lookup("asset", "assetId", "assetId", "latestAudits"),
                Aggregation.unwind("latestAudits"),
                Aggregation.match(criteria),
                Aggregation.group("assetId"));

        AggregationResults<CountDTO> countAggregationResults = mongoTemplate.aggregate(countAggregation, "audit",
                CountDTO.class);
        List<CountDTO> counts = countAggregationResults.getMappedResults();
        // nextAuditDate Format change
        List<AuditCompletedDTO> auditResponse = new ArrayList<>();
        for (AuditCompletedDTO auditCompletedDTO : auditResponses) {

            Long statusCount = auditRepository.countByAssetId(auditCompletedDTO.getAssetId());
            auditCompletedDTO.setStatusCount(statusCount);

            LocalDate auditingDate = auditCompletedDTO.getAuditDate();

            if (auditingDate != null) {

                int auditingDay = auditingDate.getDayOfMonth();
                if (auditingDay == 31) {
                    auditingDay -= 1;
                    if (auditingDay == 0) {
                        auditingDay = LocalDate.now().minusMonths(1).lengthOfMonth();
                    }
                }
                LocalDate nextAuditDate;

                if (auditCompletedDTO.getNextAuditDate() == null) {
                    LocalDate currentDate = LocalDate.now();
                    nextAuditDate = currentDate.plusMonths(1).withDayOfMonth(auditingDay);
                    if (!nextAuditDate.isAfter(currentDate)) {
                        nextAuditDate = currentDate.plusMonths(1).withDayOfMonth(1).minusDays(1);
                    }
                } else {
                    nextAuditDate = auditCompletedDTO.getNextAuditDate();
                    // nextAuditDate = assets.get(0).getNextAuditDate();
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yyyy",
                        Locale.ENGLISH);
                if (nextAuditDate != null) {
                    LocalDate currentDate = LocalDate.now();
                    if (!nextAuditDate.isAfter(currentDate)) {
                        nextAuditDate = currentDate.plusMonths(1).withDayOfMonth(1).minusDays(1);
                    }
                    auditCompletedDTO.setAuditDateFormat(nextAuditDate.format(formatter));
                } else {
                    LocalDate currentDate = LocalDate.now();
                    nextAuditDate = currentDate.plusMonths(1).withDayOfMonth(auditingDay);
                    if (!nextAuditDate.isAfter(currentDate)) {
                        nextAuditDate = currentDate.plusMonths(1).withDayOfMonth(1).minusDays(1);
                    }
                    auditCompletedDTO.setAuditDateFormat(nextAuditDate.format(formatter));
                }
                auditResponse.add(auditCompletedDTO);
            } else {
                auditResponse.add(auditCompletedDTO);
            }

        }

        AuditCompletedResponse response = new AuditCompletedResponse();
        response.setAuditsData(auditResponse);
        response.setAuditStatusCounts(counts.size());
        return response;

    }

}
