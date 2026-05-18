package com.covoiturage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.covoiturage.security.SessionUser;
import com.covoiturage.service.EvaluationService;
import com.covoiturage.service.MessagingService;
import com.covoiturage.service.NotificationService;
import com.covoiturage.service.ReclamationService;
import com.covoiturage.service.TrajetService;
import com.covoiturage.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/passager")
public class PassagerController {
    private final TrajetService trajets;
    private final UserService users;
    private final NotificationService notifs;
    private final EvaluationService evals;
    private final ReclamationService recs;
    private final MessagingService messages;

    public PassagerController(TrajetService trajets, UserService users, NotificationService notifs, EvaluationService evals, ReclamationService recs, MessagingService messages) {
        this.trajets = trajets;
        this.users = users;
        this.notifs = notifs;
        this.evals = evals;
        this.recs = recs;
        this.messages = messages;
    }

    private String guard(HttpSession session) {
        return SessionUser.hasRole(session, "passager") ? null : "redirect:/";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String redirect = guard(session);
        if (redirect != null) return redirect;
        String cin = SessionUser.cin(session);
        model.addAttribute("trajets", trajets.getAllTrajets());
        model.addAttribute("userName", SessionUser.name(session));
        model.addAttribute("reservations", trajets.getAllTrajets().stream().filter(t -> t.hasAccepted(cin) || t.hasPending(cin)).count());
        model.addAttribute("notifications", notifs.unreadPassager(cin));
        return "passager/dashboard";
    }

    @GetMapping("/trajets")
    public String search(@RequestParam(defaultValue = "") String depart, @RequestParam(defaultValue = "") String arrivee, HttpSession session, Model model) {
        String redirect = guard(session);
        if (redirect != null) return redirect;
        model.addAttribute("trajets", trajets.search(depart, arrivee));
        model.addAttribute("depart", depart);
        model.addAttribute("arrivee", arrivee);
        return "passager/trajets";
    }

    @PostMapping("/trajets/{id}/reserve")
    public String reserve(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        String redirect = guard(session);
        if (redirect != null) return redirect;
        trajets.demanderReservation(id, SessionUser.cin(session));
        redirectAttributes.addFlashAttribute("success", "Demande envoyee au conducteur");
        return "redirect:/passager/trajets";
    }

    @GetMapping("/reservations")
    public String reservations(HttpSession session, Model model) {
        String redirect = guard(session);
        if (redirect != null) return redirect;
        String cin = SessionUser.cin(session);
        model.addAttribute("trajets", trajets.getAllTrajets().stream().filter(t -> t.hasAccepted(cin) || t.hasPending(cin)).toList());
        return "passager/reservations";
    }

    @PostMapping("/reservations/{id}/cancel")
    public String cancel(@PathVariable Long id, HttpSession session) {
        String redirect = guard(session);
        if (redirect != null) return redirect;
        trajets.annulerReservation(id, SessionUser.cin(session));
        return "redirect:/passager/reservations";
    }

    @PostMapping("/reclamations")
    public String reclamation(@RequestParam String reservationId, @RequestParam String accusedId, @RequestParam(defaultValue = "Autre") String preset, @RequestParam String message, HttpSession session) {
        String redirect = guard(session);
        if (redirect != null) return redirect;
        recs.submit(reservationId, SessionUser.cin(session), "passager", accusedId, "conducteur", preset, message);
        return "redirect:/passager/reservations";
    }

    // Source Swing : EnhancedPassengerPanel.java -> bouton Help -> openAdminConversationForCurrentUser(..., "HELP")
    @PostMapping("/request-help")
    public String requestHelp(HttpSession session, RedirectAttributes redirectAttributes) {
        String redirect = guard(session);
        if (redirect != null) return redirect;
        String cin = SessionUser.cin(session);
        var user = users.passager(cin).orElseThrow();
        var admin = users.defaultAdmin();
        notifs.addAdminNotification("Aide demandee par " + user.getPrenom() + " " + user.getNom() + " (CIN: " + cin + ")", "HELP");
        messages.getOrCreateAdminConversation(cin, admin.getCin(), "HELP");
        redirectAttributes.addFlashAttribute("success", "Demande d'aide envoyee a l'administrateur");
        return "redirect:/messages?with=admin";
    }

    @GetMapping("/notifications")
    public String notifications(HttpSession session, Model model) {
        String redirect = guard(session);
        if (redirect != null) return redirect;
        model.addAttribute("notifications", notifs.passagerNotifications(SessionUser.cin(session)));
        return "notifications";
    }

    @PostMapping("/notifications/{id}/read")
    public String markNotifRead(@PathVariable String id, HttpSession session) {
        String redirect = guard(session);
        if (redirect != null) return redirect;
        notifs.marquerCommelue(SessionUser.cin(session), id);
        return "redirect:/passager/notifications";
    }

    @PostMapping("/notifications/read-all")
    public String markAllNotifRead(HttpSession session) {
        String redirect = guard(session);
        if (redirect != null) return redirect;
        notifs.marquerToutesCommelues(SessionUser.cin(session));
        return "redirect:/passager/notifications";
    }

    @PostMapping("/evaluate")
    public String evaluate(@RequestParam String conducteurCin, @RequestParam String trajetId, @RequestParam int rating, @RequestParam(required = false) String comment, HttpSession session) {
        String cin = SessionUser.cin(session);
        String name = users.passager(cin).map(p -> p.getPrenom() + " " + p.getNom()).orElse(cin);
        evals.evaluate(cin, name, conducteurCin, trajetId, rating, comment);
        return "redirect:/passager/reservations";
    }
}
