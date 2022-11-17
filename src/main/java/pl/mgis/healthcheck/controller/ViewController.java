package pl.mgis.healthcheck.controller;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.mgis.healthcheck.dto.MailSettingDto;
import pl.mgis.healthcheck.dto.ServiceUrlDto;
import pl.mgis.healthcheck.model.MailSetting;
import pl.mgis.healthcheck.service.EmailService;
import pl.mgis.healthcheck.service.ServiceUrlService;

import java.util.ArrayList;
import java.util.List;

@Controller()
public class ViewController {

    private final EmailService emailService;
    private final ServiceUrlService serviceUrlService;

    public ViewController(EmailService emailService, ServiceUrlService serviceUrlService) {
        this.emailService = emailService;
        this.serviceUrlService = serviceUrlService;
    }

    @GetMapping("favicon.ico")
    @ResponseBody
    void disableFavicon() {
    }

/*    @GetMapping({"/","/{page}"})
    public String start(@PathVariable(value = "page",required = false) String page) {
        return "redirect:/view/dashboard";
    }*/

    @GetMapping("/authorize")
    public @ResponseBody boolean isUserLoggedIn() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails;
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

        return "view/main-content :: main-dashboard";
    }

    @PostMapping("/view/services")
    public String getServiceUrlList(Model model) {
        List<ServiceUrlDto> serviceUrlList = new ArrayList<>(serviceUrlService.getAll());
        model.addAttribute("serviceUrlList",serviceUrlList);
        return "view/main-content :: main-services";
    }

    @PostMapping("/view/mail")
    public String getMailObject(Model model) {
        MailSettingDto mailSettingDto = emailService.getEmailSetting();
        model.addAttribute("mailSetting",mailSettingDto);
        return "view/main-content :: main-mail";
    }

    @PostMapping("/view/mail/save")
    public String saveMailObject(@ModelAttribute MailSettingDto mailSettingsDto, RedirectAttributes redirectAttrs) {
        emailService.updateMailSetting(mailSettingsDto);
        //redirectAttrs.addFlashAttribute("msg", msg);
        //redirectAttrs.addFlashAttribute("info", info);
        return "redirect:/view/mail";
    }

}
