package assetmanagement.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailService {
    public static final String MESSAGE_SUCCESS = "success";
    public static final String OPEN_DIV = "<div>";
    public static final String COLSE_DIV = "</div>";
    private final JavaMailSender jsender;
    private final Configuration config;
    @Value("${spring.mail.username}")
    private String sender;

    public String sendReminderNotification(Map<String, Object> content, String subject, String receiver)
            throws IOException, TemplateException {
        MimeMessage message = jsender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            Template template = config.getTemplate("expiry.ftl");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, content);
            helper.setTo(receiver);
            helper.setText(OPEN_DIV + html + COLSE_DIV, true);
            helper.setSubject(subject);
            helper.setFrom(sender);
            jsender.send(message);
            return MESSAGE_SUCCESS;
        } catch (MessagingException e) {

            return "failed: MessagingException";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String sendRequestMail(Map<String, Object> content, String subject, String receiver)
            throws IOException, TemplateException {
        MimeMessage message = jsender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            Template template = config.getTemplate("sendRequest.ftl");

            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template,
                    content);
            helper.setTo(receiver);
            helper.setText(OPEN_DIV + html + COLSE_DIV, true);
            helper.setSubject(subject);
            helper.setFrom(sender);
            jsender.send(message);
            return MESSAGE_SUCCESS;
        } catch (MessagingException e) {

            return "failed: MessagingException";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String sendRejectedMail(Map<String, Object> content, String subject, List<String> receiver)
            throws IOException, TemplateException {
        MimeMessage message = jsender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            Template template = config.getTemplate("sendRejected.ftl");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template,
                    content);
            for (String receivers : receiver) {
                helper.addTo(receivers);
            }
            helper.setText(OPEN_DIV + html + COLSE_DIV, true);
            helper.setSubject(subject);
            helper.setFrom(sender);
            jsender.send(message);
            return MESSAGE_SUCCESS;
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
