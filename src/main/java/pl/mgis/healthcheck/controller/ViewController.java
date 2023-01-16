package pl.mgis.healthcheck.controller;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.mgis.healthcheck.dto.MailSettingDto;
import pl.mgis.healthcheck.dto.ServiceUrlDto;
import pl.mgis.healthcheck.service.EmailService;
import pl.mgis.healthcheck.service.HitLogService;
import pl.mgis.healthcheck.service.ServiceUrlService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ViewController {

    private final EmailService emailService;
    private final ServiceUrlService serviceUrlService;
    private final HitLogService hitLogService;

    public ViewController(EmailService emailService, ServiceUrlService serviceUrlService, HitLogService hitLogService) {
        this.emailService = emailService;
        this.serviceUrlService = serviceUrlService;
        this.hitLogService = hitLogService;
    }

    @GetMapping("favicon.ico")
    @ResponseBody
    void disableFavicon() {
    }

    @GetMapping("/")
    public String start() {
        return "redirect:/view/dashboard";
    }

    @GetMapping("/sw")
    public String swagger() {
        return "redirect:/swagger-ui/";
    }

    @PostMapping("/verify")
    public @ResponseBody
    boolean isUserLoggedIn() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails;
    }

    @GetMapping("/verify")
    public String redirect() {
        return "redirect:/view/dashboard";
    }


    @GetMapping("/view/")
    public String index(Model model) {
        model.addAttribute("pageContent", null);
        return "view/main";
    }

    @GetMapping("/view/{page}")
    public String index(@PathVariable("page") String page, Model model, @ModelAttribute("msg") String msg, @ModelAttribute("info") String info) {
        model.addAttribute("pageContent", null);
        if (!page.isBlank()) {
            model.addAttribute("pageContent", page);
            model.addAttribute("msg", msg);
            model.addAttribute("info", info);
        }
        return "view/main";
    }

    @PostMapping("/view/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("hitLogList", hitLogService.findTodayHitLogDto());
        return "view/main-content :: main-dashboard";
    }

    @PostMapping("/view/hitlog")
    public String hitLog(@RequestParam(name = "fromday", required = false) String fromDay, Model model) {
        if (fromDay == null) {
            fromDay = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        Object hitLogFromDay = hitLogService.findHitLogFromDay(fromDay);
        model.addAttribute("hitLogList", hitLogService.findHitLogFromDay(fromDay));
        model.addAttribute("dateValue", fromDay);
        return "view/main-content :: main-hitlog";
    }

    @PostMapping("/view/services")
    public String getServiceUrlList(Model model) {
        List<ServiceUrlDto> serviceUrlList = new ArrayList<>(serviceUrlService.getAll());
        model.addAttribute("serviceUrlList", serviceUrlList);
        return "view/main-content :: main-services";
    }

    @PostMapping("/view/mail")
    public String getMailObject(Model model) {
        MailSettingDto mailSettingDto = emailService.getEmailSettingDto();
        model.addAttribute("mailSetting", mailSettingDto);
        return "view/main-content :: main-mail";
    }

    @PostMapping("/view/mail/save")
    public String saveMailObject(@ModelAttribute MailSettingDto mailSettingsDto) {
        emailService.updateMailSetting(mailSettingsDto);
        return "redirect:/view/mail";
    }

}
