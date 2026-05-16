package com.covoiturage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.covoiturage.security.SessionUser;
import com.covoiturage.service.EvaluationService;
import com.covoiturage.service.MessagingService;
import com.covoiturage.service.NotificationService;
import com.covoiturage.service.ReclamationService;
import com.covoiturage.service.TrajetService;
import com.covoiturage.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService users;
    private final TrajetService trajets;
    private final EvaluationService evals;
    private final ReclamationService recs;
    private final NotificationService notifs;
    private final MessagingService messages;

    public AdminController(UserService users, TrajetService trajets, EvaluationService evals, ReclamationService recs, NotificationService notifs, MessagingService messages) {
        this.users = users;
        this.trajets = trajets;
        this.evals = evals;
        this.recs = recs;
        this.notifs = notifs;
        this.messages = messages;
    }

    private String guard(HttpSession session) {
        return SessionUser.hasRole(session, "admin") ? null : "redirect:/";
    }

    @GetMapping("/dashboard")
    public String dash(HttpSession session, Model model) {
        String redirect = guard(session);
        if (redirect != null) return redirect;
        model.addAttribute("conducteurs", users.conducteurs().size());
        model.addAttribute("passagers", users.passagers().size());
        model.addAttribute("trajets", trajets.getAllTrajets().size());
        model.addAttribute("evaluations", evals.all().size());
        model.addAttribute("unread", notifs.unreadAdmin());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(HttpSession session, Model model) {
        String redirect = guard(session);
        if (redirect != null) return redirect;
        model.addAttribute("conducteurs", users.conducteurs());
        model.addAttribute("passagers", users.passagers());
        return "admin/users";
    }

    @PostMapping("/users/{role}/{cin}/delete")
    public String deleteUser(@PathVariable String role, @PathVariable String cin) {
        users.deleteUser(role, cin);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{role}/{cin}/card")
    public String card(@PathVariable String role, @PathVariable String cin, @RequestParam String carte) {
        users.updateCard(role, cin, carte);
        return "redirect:/admin/users";
    }

    @GetMapping("/trajets")
    public String trajets(HttpSession session, Model model) {
        String redirect = guard(session);
        if (redirect != null) return redirect;
        model.addAttribute("trajets", trajets.getAllTrajets());
        return "admin/trajets";
    }

    @PostMapping("/trajets/{id}/delete")
    public String deleteTrajet(@PathVariable Long id) {
        trajets.deleteTrajet(id);
        return "redirect:/admin/trajets";
    }

    @GetMapping("/evaluations")
    public String evaluations(HttpSession session, Model model) {
        String redirect = guard(session);
        if (redirect != null) return redirect;
        model.addAttribute("evaluations", evals.all());
        return "admin/evaluations";
    }

    @GetMapping("/reclamations")
    public String reclamations(HttpSession session, Model model) {
        String redirect = guard(session);
        if (redirect != null) return redirect;
        model.addAttribute("reclamations", recs.all());
        return "admin/reclamations";
    }

    // Source Swing : AdminPanel.java -> demandes HELP separees de COMPLAINT/RECLAMATION
    @GetMapping("/notifications")
    public String notifications(HttpSession session, Model model) {
        String redirect = guard(session);
        if (redirect != null) return redirect;
        model.addAttribute("notifications", notifs.adminNotifications());
        model.addAttribute("helpRequests", notifs.adminNotificationsByType("HELP"));
        model.addAttribute("reclamations", notifs.adminNotificationsByType("RECLAMATION"));
        model.addAttribute("adminMessages", messages.adminConversations());
        model.addAttribute("unreadCount", notifs.unreadAdmin());
        return "admin/notifications";
    }

    @PostMapping("/notifications/read-all")
    public String markAllNotificationsRead(HttpSession session) {
        String redirect = guard(session);
        if (redirect != null) return redirect;
        notifs.marquerToutesAdminCommelues();
        return "redirect:/admin/notifications";
    }
}
