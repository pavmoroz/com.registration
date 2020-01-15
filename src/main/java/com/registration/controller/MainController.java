package com.registration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import com.registration.dto.PasswordDto;
import com.registration.dto.UserDto;
import com.registration.exception.UserNotFoundException;
import com.registration.model.User;
import com.registration.model.VerificationToken;
import com.registration.registration.OnRegistrationCompleteEvent;
import com.registration.service.PasswordService;
import com.registration.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
public class MainController {

    private ApplicationEventPublisher eventPublisher;
    private UserService service;
    private final JavaMailSender mailSender;
    private final PasswordService passwordService;

    @Autowired
    public MainController(UserService userService, ApplicationEventPublisher eventPublisher, JavaMailSender mailSender, PasswordService passwordService) {
        this.service = userService;
        this.eventPublisher = eventPublisher;
        this.mailSender = mailSender;
        this.passwordService = passwordService;
    }

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/user")
    public String user() {
        return "user";
    }

    @RequestMapping("/admin")
    public String admin() {
        return "admin";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registration(Model model) {
        UserDto userDTO = new UserDto();
        model.addAttribute("user", userDTO);
        return "registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView registerUser(@ModelAttribute("user") @Valid UserDto dto,
                                     BindingResult result, WebRequest request) {
        if (result.hasErrors()) {
            return new ModelAndView("registration", "user", dto);
        }

        User registered = createUserAccount(dto);
        if (registered == null) {
            result.rejectValue("email", "", "This email was already used for registration.");
            return new ModelAndView("registration", "user", dto);
        }

        try {
            String url = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent
                    (registered, url));
        } catch (Exception e) {
            e.printStackTrace();
            return new ModelAndView("example", "user", dto);
        }
        return new ModelAndView("sucRegistr", "user", dto);
    }

    private User createUserAccount(UserDto dto) {
        User registered;
        try {
            registered = service.registerNewUserAccount(dto);
        } catch (Exception e) {
            return null;
        }
        return registered;
    }

    @RequestMapping(value = "/registrationConfirm.html", method = RequestMethod.GET)
    public String confirmRegistration
            (Model model, @RequestParam("token") String token) {

        VerificationToken verificationToken = service.getVerificationToken(token);
        if (verificationToken == null) {
            String message = "Invalid Token!";
            model.addAttribute("message", message);
            return "badUser";
        }

        User user = verificationToken.getUser();

        if (verificationToken.getExpirationTime().isBefore(LocalDateTime.now())) {
            String messageValue = "Your registration token has expired. Please register again.";
            model.addAttribute("message", messageValue);
            model.addAttribute("expired", "TOKEN IS EXPIRED!");
            model.addAttribute("token", verificationToken.getToken());
            return "badUser";
        }
        user.setEnabled(true);
        service.saveRegisteredUser(user);
        service.deleteVerificationToken(verificationToken.getId());
        model.addAttribute("message", "You have registered successfully. Please, log in now.");
        return "redirect:/login?sucReg";
//        return "login";
    }

    @RequestMapping(value = "/resendRegistrationToken", method = RequestMethod.GET)
    @ResponseBody
    public void resendRegistrationToken(
            HttpServletRequest request, @RequestParam("token") String existingToken) {
        VerificationToken newToken = service.generateNewVerificationToken(existingToken);

        User user = service.getUser(newToken.getToken());
        String appUrl =
                "http://" + request.getServerName() +
                        ":" + request.getServerPort() +
                        request.getContextPath();
        SimpleMailMessage email =
                createEmailWithResendVerificationToken(appUrl, newToken, user);
        mailSender.send(email);
    }

    private SimpleMailMessage createEmailWithResendVerificationToken(String contextPath,
                                                                     VerificationToken newToken,
                                                                     User user) {
        String confirmationUrl =
                contextPath + "/registrationConfirm.html?token=" + newToken.getToken();
        String message = "Please, click on a link below to finish registration.";
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject("Resend Registration Token");
        email.setText(message + "\n" + confirmationUrl);
        email.setTo(user.getEmail());
        return email;
    }

    @RequestMapping(value = "/forgotPassword")
    public String showChangePasswordPage(){
        return "forgotPassword";
    }

    @RequestMapping(value = "/resetPassword",
            method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView resetPassword(HttpServletRequest request,
                                         @RequestParam("email") String userEmail) {
        User user = service.findUserByEmail(userEmail);
        if (user == null) {
            throw new UserNotFoundException("");
        }
        String token = UUID.randomUUID().toString();
        service.createPasswordResetTokenForUser(user, token);
        mailSender.send(createEmailWithResetToken(getUrl(request), token, user));

        return new ModelAndView("sucRegistr");
    }

    private String getUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    private SimpleMailMessage createEmailWithResetToken(String contextPath, String token, User user) {
        String url = contextPath + "/changePassword?id=" +
                user.getId() + "&token=" + token;
        String message = "Reset Password bla bla"; //!!!//
        return createEmail("Reset Password", message + " \r\n" + url, user);
    }

    private SimpleMailMessage createEmail(String subject, String body, User user) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        return email;
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.GET)
    public String showChangePasswordPage(Model model,
                                         @RequestParam("id") long id, @RequestParam("token") String token) {
        String result = passwordService.validatePasswordResetToken(id, token);
        if (result != null) {
            model.addAttribute("message", "Error in change password!");
            return "login";

        }
        PasswordDto passwordDto = new PasswordDto();
        model.addAttribute("password", passwordDto);
        return "updatePassword";
    }

    @RequestMapping(value = "/savePassword", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView savePassword(@ModelAttribute("password") @Valid PasswordDto passwordDto) {
        User user =
                (User) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal();

        service.changeUserPassword(user, passwordDto.getNewPassword());
        return new ModelAndView("login");
    }

}