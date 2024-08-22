package assetmanagement.serviceImpl.asset;

import assetmanagement.dto.AssetTicketDTO;
import assetmanagement.enumData.ActiveInActive;
import assetmanagement.enumData.AuditStatus;
import assetmanagement.exception.ResourceNotFoundException;
import assetmanagement.model.AssetTicket;
import assetmanagement.repository.asset.AssetTicketRepository;
import assetmanagement.request.AssetTicketRequest;
import assetmanagement.response.AssetTicketResponse;
import assetmanagement.response.SapAssetTicketResponse;
import assetmanagement.response.SapTicketResponse;
import assetmanagement.response.TicketNoResponse;
import assetmanagement.service.asset.AssetTicketService;
import assetmanagement.util.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetTicketServiceImpl implements AssetTicketService {
    private static final String NO_DATA_FOUND_MESSAGE = "No Data Found";
    private static final String STATUS = "status";
    private final MongoTemplate mongoTemplate;
    private final AssetTicketRepository assetTicketRepository;

    @Override
    public AssetTicketResponse fetchAllTicket(String assetClass, String ticketNo,
                                              Boolean search, String value, Integer page, Integer size) {

        page = page == null ? 0 : page;
        size = size == null ? 10 : size;

        String ticket = "ticketNo";
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        AssetTicketResponse response = new AssetTicketResponse();

        Criteria criteria = Criteria.where("plant").is(AuthUser.getPlant())
                .and(STATUS).ne(ActiveInActive.DELETED.getValue());
        if (assetClass != null) {
            criteria.and("assetClass").is(assetClass);
        }
        if (ticketNo != null) {
            criteria.and(ticket).is(ticketNo);
        }
        if (Boolean.TRUE.equals(search) && value != null && !value.isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("assetName").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("description").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where(ticket).regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where(STATUS).regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("assetClass").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)));
            criteria.andOperator(searchCriteria);
        }
        Query query = new Query(criteria);
        long totalCount = mongoTemplate.count(query, AssetTicket.class);
        query.with(pageable);
        List<AssetTicket> assetList = mongoTemplate.find(query, AssetTicket.class);
        response.setAssetTicketCount(totalCount);
        response.setAssetTicket(assetList);
        return response;
    }

    @Override
    public Optional<AssetTicket> getAssetById(String id) {
        return assetTicketRepository.findByIdAndPlant(id, AuthUser.getPlant());

    }

    @Override
    public AssetTicket update(String id, AssetTicketRequest assetTicket) {
        Optional<AssetTicket> assetOptional = assetTicketRepository.findById(id);
        if (assetOptional.isPresent()) {
            AssetTicket assetTicketUpdate = assetOptional.get();
            assetTicketUpdate.setAssetName(assetTicket.getAssetName());
            assetTicketUpdate.setAssetClass(assetTicket.getAssetClass());
            assetTicketUpdate.setDescription(assetTicket.getDescription());
            return assetTicketRepository.save(assetTicketUpdate);
        }
        throw new ResourceNotFoundException(NO_DATA_FOUND_MESSAGE);
    }

    @Override
    public List<AssetTicket> getTicketNo() {
        Criteria criteria = Criteria.where("plant").is(AuthUser.getPlant())
                .and(STATUS).ne(ActiveInActive.DELETED.getValue());
        Query query = new Query(criteria);
        return mongoTemplate.find(query, AssetTicket.class);

    }

    @Override
    public AssetTicket create(AssetTicketRequest assetTicket) {

        AssetTicket assetTicketCreate = new AssetTicket();
        assetTicketCreate.setAssetName(assetTicket.getAssetName());
        assetTicketCreate.setAssetClass(assetTicket.getAssetClass());
        assetTicketCreate.setDescription(assetTicket.getDescription());
        assetTicketCreate.setTicketNo(assetTicket.getTicketNo());
        assetTicketCreate.setPlant(AuthUser.getPlant());
        assetTicketCreate.setRaisedDate(LocalDate.now());
        assetTicketCreate.setStatus(AuditStatus.Pending.getValue());
        return assetTicketRepository.save(assetTicketCreate);

    }

    @Override
    public Optional<AssetTicket> deleteAssetTicket(String id) {
        Optional<AssetTicket> assetOptional = assetTicketRepository.findByIdAndPlant(id, AuthUser.getPlant());
        if (assetOptional.isPresent()) {
            AssetTicket assetTicketUpdate = assetOptional.get();
            assetTicketUpdate.setStatus(ActiveInActive.DELETED.getValue()); // Mark as deleted
            assetTicketRepository.save(assetTicketUpdate);
            return Optional.of(assetTicketUpdate); // Return the updated entity
        }
        throw new ResourceNotFoundException("Data not found");
    }

    @Override
    public AssetTicket statusUpdate(String id, String status) {
        Optional<AssetTicket> assetOptional = assetTicketRepository.findByIdAndPlant(id, AuthUser.getPlant());
        if (assetOptional.isPresent()) {
            AssetTicket assetTicketUpdate = assetOptional.get();
            assetTicketUpdate.setStatus(status);
            return assetTicketRepository.save(assetTicketUpdate);
        }
        throw new ResourceNotFoundException("Data not found");
    }

    @Override
    public SapAssetTicketResponse sapRequestTicket() {
        SapAssetTicketResponse response = new SapAssetTicketResponse();
        Criteria criteria = Criteria.where(STATUS).is(AuditStatus.Pending.getValue());
        Query query = new Query(criteria);
        long totalCount = mongoTemplate.count(query, AssetTicket.class);
        List<AssetTicket> assetList = mongoTemplate.find(query, AssetTicket.class);
        List<SapTicketResponse> sapTicketResponse = assetList.stream()
                .map(asset -> {
                    SapTicketResponse sapTicket = new SapTicketResponse();
                    sapTicket.setTicketNo(asset.getTicketNo());
                    sapTicket.setAssetName(asset.getAssetName());
                    sapTicket.setAssetClass(asset.getAssetClass());
                    sapTicket.setDescription(asset.getDescription());
                    return sapTicket;
                }).toList();
        response.setSapAssetTicket(sapTicketResponse);
        response.setAssetTicketCount(totalCount);
        return response;
    }

    @Override
    public TicketNoResponse getTicketNoCount() {
        Criteria criteria = Criteria.where("ticketNo").exists(true);
        Query query = new Query(criteria);
        Long totalCount = mongoTemplate.count(query, AssetTicket.class);
        return new TicketNoResponse(totalCount);
    }

    @Override
    public AssetTicketResponse fetchStatusWiseTicket(String assetClass, String ticketNo,
                                              boolean search, String value, Integer page, Integer size, String ticketType) {

        page = page == null ? 0 : page;
        size = size == null ? 10 : size;

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        AssetTicketResponse response = new AssetTicketResponse();

        Criteria criteria = Criteria.where("plant").is(AuthUser.getPlant())
                .and ("status").ne(ActiveInActive.DELETED.getValue());
        if (assetClass != null) {
            criteria.and("assetClass").is(assetClass);
        }
        if (ticketNo != null) {
            criteria.and("ticketNo").is(ticketNo);
        }
        if (ticketType != null && ticketType != "") {
            criteria.and("sapStatus").is(ticketType);
        }

        if (search && value != null && !value.isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("assetName").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("description").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("ticketNo").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("status").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)),
                    Criteria.where("assetClass").regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)));
            criteria.andOperator(searchCriteria);
        }
        Query query = new Query(criteria);
        long totalCount = mongoTemplate.count(query, AssetTicket.class);
        query.with(pageable);
        List<AssetTicket> assetList = mongoTemplate.find(query, AssetTicket.class);
        response.setAssetTicketCount(totalCount);
        response.setAssetTicket(assetList);
        return response;
    }

    @Override
    public AssetTicket sapUpdate(String ticketNo, String sapStatus) {
        Optional<AssetTicket> assetOptional = assetTicketRepository.findByTicketNo(ticketNo);
        if (assetOptional.isPresent()) {
            AssetTicket assetTicketUpdate = assetOptional.get();
            assetTicketUpdate.setSapStatus(sapStatus);
            return assetTicketRepository.save(assetTicketUpdate);
        }
        throw new ResourceNotFoundException("Data not found");
    }

    @Override
    public List<AssetTicketDTO> assetCreationFilterData(String type) {
        Criteria criteria;
        if (type.equals("Approved") || type.equals("Pending")) {
            criteria = Criteria.where("plant").is(AuthUser.getPlant())
                .and("sapStatus").is(type)
                .and(STATUS).ne(ActiveInActive.DELETED.getValue());
        } else {
            criteria = Criteria.where("plant").is(AuthUser.getPlant())
                .and(STATUS).ne(ActiveInActive.DELETED.getValue());
        }
        
        Query query = new Query(criteria);
        query.fields().include("ticketNo", "assetClass", "plant");

        List<AssetTicket> assetTickets = mongoTemplate.find(query, AssetTicket.class);

        // Map AssetTicket to AssetTicketDTO
        return assetTickets.stream().map(ticket -> {
            AssetTicketDTO dto = new AssetTicketDTO();
            dto.setId(ticket.getId());
            dto.setTicketNo(ticket.getTicketNo());
            dto.setAssetClass(ticket.getAssetClass());
            dto.setPlant(ticket.getPlant());
            return dto;
        }).collect(Collectors.toList());

    }
    
}
