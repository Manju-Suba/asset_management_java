package assetmanagement.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import assetmanagement.model.Asset;
import assetmanagement.repository.UserRepository;
import assetmanagement.repository.asset.AssetRepository;
import assetmanagement.service.MailService;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;

@RequestMapping("/mail")
@RestController
@RequiredArgsConstructor
public class MailNotificationController {

    public final MailService mailService;
    public final AssetRepository assetRepository;
    public final UserRepository userRepository;

    @GetMapping("/reminder-expiry")
    public List<Asset> sendReminderNotification() throws IOException, TemplateException {
        Map<String, Object> content = new HashMap<>();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(7);
        List<Asset> assetList = assetRepository.findByExpiryDateBetween(startDate, endDate);
        content.put("asset", assetList);
        String subject = "Alert Expiry Records";
        String receiver = "divya.k@hepl.com";
        mailService.sendReminderNotification(content, subject, receiver);
        return assetList;
    }
}
