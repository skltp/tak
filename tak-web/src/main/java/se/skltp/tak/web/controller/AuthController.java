package se.skltp.tak.web.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class AuthController {
    @RequestMapping("auth/login")
    public String login() {
        return "auth/login";
    }

    @PostMapping("auth/signIn")
    public RedirectView signIn(RedirectAttributes attributes, HttpServletRequest request,
                               @RequestParam String username, @RequestParam String password) {
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            Subject currentUser = SecurityUtils.getSubject();
            currentUser.login(token);
            request.getSession().setAttribute("username", username);

            SavedRequest savedRequest = WebUtils.getSavedRequest(request);
            if (savedRequest != null) {
                return new RedirectView(savedRequest.getRequestURI(), false);
            }
            return new RedirectView("/", true);
        }
        catch (Exception e) {
            attributes.addFlashAttribute("message", "Ogiltigt användarnamn och/eller lösenord");
            return new RedirectView("/auth/login", true);
        }
    }

    @RequestMapping("auth/signOut")
    public RedirectView signOut() {
        SecurityUtils.getSubject().logout();
        return new RedirectView("/auth/login", true);
    }
}
