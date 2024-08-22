package assetmanagement.serviceImpl;

import assetmanagement.dto.MaintenanceDTO;
import assetmanagement.model.Maintenance;
import assetmanagement.model.masters.Plant;
import assetmanagement.repository.audit.MaintenanceRepository;
import assetmanagement.repository.masters.PlantRepository;
import assetmanagement.response.MaintenanceResponse;
import assetmanagement.response.SapResponse;
import assetmanagement.service.MaintenanceService;
import assetmanagement.util.AuthUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class MaintenanceServiceImpl implements MaintenanceService {

    private static final Logger logger = LoggerFactory.getLogger(MaintenanceServiceImpl.class);
    public final PlantRepository plantRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final MongoTemplate mongoTemplate;
    @Value("${maintenance.username}")
    private String mainUser;

    @Value("${maintenance.password}")
    private String mainPassword;

    private String getTextNode(JsonNode node, String fieldName) {
        JsonNode textNode = node.get(fieldName);
        return textNode != null ? textNode.asText() : null;
    }

    @Override
    public List<Maintenance> getAllMaintenances() {
        return maintenanceRepository.findAll();
    }

    @Override
    public MaintenanceResponse getByAssetId(String assetId, String fromDate, String toDate, Integer page,
            Integer size) {
        page = page == null ? 0 : page;
        size = size == null ? 10 : size;

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        MaintenanceResponse response = new MaintenanceResponse();

        Criteria criteria = Criteria.where("plant").is(AuthUser.getPlant())
                .and("assetId").is(assetId);
        if (fromDate != null && toDate != null) {
            try {
                LocalDateTime fromDateTime = LocalDateTime.parse(fromDate);
                LocalDateTime toDateTime = LocalDateTime.parse(toDate);
                criteria.and("malfunctionEndDate").gte(fromDateTime).lte(toDateTime);
            } catch (DateTimeParseException e) {
                // Handle invalid date format
                throw new IllegalArgumentException("Invalid date format. Please use 'yyyy-MM-ddTHH:mm:ss' format.");
            }
        }
        Query query = new Query(criteria);
        long totalCount = mongoTemplate.count(query, Maintenance.class);
        query.with(pageable);
        List<Maintenance> assetList = mongoTemplate.find(query, Maintenance.class);
        response.setMaintenanceAssetCount(totalCount);
        response.setMaintenanceList(assetList);
        return response;
    }

    @Override
    public Maintenance create(MaintenanceDTO maintenanceDTO) {
        Maintenance maintenance = new Maintenance();
        maintenance.setBreakDownDateTime(maintenanceDTO.getBreakDownDateTime());
        maintenance.setMalfunctionEndDate(maintenanceDTO.getMalfunctionEndDate());
        maintenance.setMalfunctionStartDate(maintenanceDTO.getMalfunctionStartDate());
        maintenance.setBreakDownDuration(maintenanceDTO.getBreakDownDuration());
        maintenance.setAssetId(maintenanceDTO.getAssetId());
        maintenance.setEquipmentNumber(maintenanceDTO.getEquipmentNumber());
        maintenance.setPlant(maintenanceDTO.getPlant());
        maintenance.setProblem(maintenanceDTO.getProblem());
        maintenance.setProductionLine(maintenanceDTO.getProductionLine());
        maintenance.setEquipmentName(maintenanceDTO.getEquipmentName());
        maintenance.setCategory(maintenanceDTO.getCategory());
        maintenance.setTag(maintenanceDTO.getTag());
        maintenance.setPriority(maintenanceDTO.getPriority());
        maintenance.setProblem(maintenanceDTO.getProblem());
        maintenance.setActionTaken(maintenanceDTO.getActionTaken());
        maintenance.setRootCause(maintenanceDTO.getRootCause());
        maintenance.setStatus(maintenanceDTO.getStatus());
        maintenance.setMaintenanceType(maintenanceDTO.getMaintenanceType());
        maintenance.setRequestedBy(maintenanceDTO.getRequestedBy());
        maintenance.setReportedBy(maintenanceDTO.getReportedBy());
        return maintenanceRepository.save(maintenance);
    }

    private Mono<String> fetchDataFromApi(WebClient client, String url, String plantName) {
        return client.get()
                .uri(uriBuilder -> {
                    String builtUri = uriBuilder
                            .path(url)
                            .queryParam("Plant", plantName)
                            .build()
                            .toString();
                    logger.info("Built URI: {}", builtUri);
                    return URI.create(builtUri);
                })
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    e.printStackTrace();
                    if ((e.getStatusCode().is4xxClientError() || e.getStatusCode().is5xxServerError()) &&
                            (e.getStatusCode().value() == 402 || e.getStatusCode().value() == 404
                                    || e.getStatusCode().value() == 502)) {
                        String errorMessage = "Error Occurred in mono: " + e.getStatusCode() + " - Skipping plant: "
                                + plantName;
                        logger.error(errorMessage);
                        return Mono.empty();
                    }
                    if (e.getStatusCode().is2xxSuccessful()) {
                        logger.info(e.getMessage());
                        logger.info(e.getResponseBodyAs(String.class));
                        // if (isValidJson(e.getResponseBodyAsString().toString())) {
                        // String errorMessage = "Error: 200 OK with invalid body - Skipping plant: " +
                        // plantName;
                        // logger.error(errorMessage);
                        // return Mono.empty();
                        // }
                    }
                    logger.error("Error Occurred in mono: outside condition:" + e);
                    return Mono.empty();
                })
                .retryWhen(Retry.fixedDelay(10, Duration.ofSeconds(90)));
    }

    @SuppressWarnings("deprecation") // to avoid ssl layer error while getting data from url
@Override
public SapResponse addApiData() {
    SapResponse response = new SapResponse();
    List<Plant> plantList = plantRepository.findAll();
    AtomicInteger totalCount = new AtomicInteger(0);
    ResponseBody responsebdy = new ResponseBody();

    try {
        HttpClient httpClient = HttpClient.create()
                .secure(sslContextSpec -> sslContextSpec.sslContext(
                        SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)));

        WebClient client = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl("https://bkappprd.ckdigital.in")
                .defaultHeaders(header -> header.setBasicAuth(mainUser, mainPassword))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // Set max buffer size to 10MB
                .build();

        for (Plant plant : plantList) {
            // String plantName = "ASM1"; // Assuming Plant has a getPlant() method
            String plantName = plant.getPlant(); // Assuming Plant has a getPlant() method
            String url = "/iot/service/v1/BreakdownByPlant";
            Mono<String> result = fetchDataFromApi(client, url, plantName);
            if (result.blockOptional().isEmpty()) {
                continue;
            }
            result.subscribe(
                    responseBody -> {
                        try {
                            if (responseBody == null || responseBody.isEmpty()) {
                                logger.info("No data found for plant: {}", plantName);
                                responsebdy.setMessage("No data found for plant: " + plantName);
                            } else if (isValidJson(responseBody)) {
                                int count = processResponse(responseBody);
                                totalCount.addAndGet(count);
                                responsebdy.setMessage("Data fetched and inserted successfully");
                            } else {
                                logger.error("Invalid JSON response for plant {}: {}", plantName, responseBody);
                                responsebdy.setMessage("Invalid JSON response for plant " + plantName + ": " + responseBody);
                            }
                        } catch (JsonProcessingException e) {
                            logger.error("Error processing JSON response for plant " + plantName + ": " + e.getMessage(), e);
                        }
                    },
                    error -> {
                        logger.error("Error occurred for plant " + plantName + ": " + error.getMessage(), error);
                        responsebdy.setMessage("Failed to fetch and insert data for plant " + plantName + ": " + error.getMessage());
                    });
        }
        response.setAssetsCount(totalCount.get());
        response.setMessage(responsebdy.getMessage());
    } catch (Exception e) {
        response.setMessage("Failed to fetch and insert data: " + e.getMessage());
        logger.error("Error occurred: " + e.getMessage(), e);
    }

    return response;
}

    private boolean isValidJson(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.readTree(responseBody);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    private int processResponse(String responseBody) throws JsonProcessingException {
        int count = 0;
        String date = "0000-00-00";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(responseBody);
            for (JsonNode node : root) {
                MaintenanceDTO maintenance = new MaintenanceDTO();
                String breakdownDateTime = getTextNode(node, "BreakdownDateTime");
                if (breakdownDateTime != null && !breakdownDateTime.equals(date)) {
                    OffsetDateTime offsetDateTime = OffsetDateTime.parse(breakdownDateTime);
                    LocalDateTime localDateTime = offsetDateTime.toLocalDateTime();
                    maintenance.setBreakDownDateTime(localDateTime);
                }

                String malfunctionEnd = getTextNode(node, "MalfunctionEnd");
                if (malfunctionEnd != null && !malfunctionEnd.equals(date)) {
                    OffsetDateTime offsetDateTime = OffsetDateTime.parse(malfunctionEnd);
                    LocalDateTime localDateTime = offsetDateTime.toLocalDateTime();
                    maintenance.setMalfunctionEndDate(localDateTime);
                }

                String malfunctionStart = getTextNode(node, "MalfunctionStart");
                if (malfunctionStart != null && !malfunctionStart.equals(date)) {
                    OffsetDateTime offsetDateTime = OffsetDateTime.parse(malfunctionStart);
                    LocalDateTime localDateTime = offsetDateTime.toLocalDateTime();
                    maintenance.setMalfunctionStartDate(localDateTime);
                }

                String breakdownDuration = getTextNode(node, "BreakdownDuration");
                if (breakdownDuration != null) {
                    maintenance.setBreakDownDuration(Float.parseFloat(breakdownDuration));
                }
                maintenance.setAssetId(getTextNode(node, "SAPAssetCode"));
                maintenance.setEquipmentNumber(getTextNode(node, "EquipmentNumber"));
                maintenance.setPlant(getTextNode(node, "Plant"));
                maintenance.setProblem(getTextNode(node, "Problem"));
                maintenance.setProductionLine(getTextNode(node, "ProductionLine"));
                maintenance.setEquipmentName(getTextNode(node, "EquipmentName"));
                maintenance.setCategory(getTextNode(node, "Category"));
                maintenance.setTag(getTextNode(node, "Tag"));
                maintenance.setPriority(getTextNode(node, "Priority"));
                maintenance.setProblem(getTextNode(node, "Problem"));
                maintenance.setActionTaken(getTextNode(node, "ActionTaken"));
                maintenance.setRootCause(getTextNode(node, "RootCause"));
                maintenance.setStatus(getTextNode(node, "Status"));
                maintenance.setMaintenanceType(getTextNode(node, "MaintenanceType"));
                maintenance.setRequestedBy(getTextNode(node, "RequestedBy"));
                maintenance.setReportedBy(getTextNode(node, "ReportedBy"));
                create(maintenance);
                count++;

            }
            logger.info("Saved maintenance data Count:{} ", count);
        } catch (NullPointerException e) {
            logger.error("Error occurred For Null:{}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error occurred while processing response:{}", e.getMessage());
        }
        return count;
    }

    public class ResponseBody {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
