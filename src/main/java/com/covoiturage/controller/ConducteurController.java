package com.covoiturage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.covoiturage.dto.TrajetForm;
import com.covoiturage.security.SessionUser;
import com.covoiturage.service.EvaluationService;
import com.covoiturage.service.MessagingService;
import com.covoiturage.service.NotificationService;
import com.covoiturage.service.ReclamationService;
import com.covoiturage.service.TrajetService;
import com.covoiturage.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/conducteur")
public class ConducteurController {
    private final TrajetService trajets;
    private final NotificationService notifs;
    private final EvaluationService evals;
    private final ReclamationService recs;
    private final UserService users;
    private final MessagingService messages;

    public ConducteurController(TrajetService trajets, NotificationService notifs, EvaluationService evals, ReclamationService recs, UserService users, MessagingService messages) {
        this.trajets = trajets;
        this.notifs = notifs;
        this.evals = evals;
        this.recs = recs;
        this.users = users;
        this.messages = messages;
    }

    private String guard(HttpSession session) {
        return SessionUser.hasRole(session, "conducteur") ? null : "redirect:/";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String redirect = guard(session);
        if (redirect != null) return redirect;
        String cin = SessionUser.cin(session);
        var list = trajets.getTrajetsConducteur(cin);
        model.addAttribute("trajets", list);
        model.addAttribute("userName", SessionUser.name(session));
        users.conducteur(cin).ifPresent(c -> model.addAttribute("placesDisponibles", c.getPlacesDisponibles()));
        model.addAttribute("demandes", list.stream().mapToInt(t -> (t.getPendingCins() == null || t.getPendingCins().isBlank()) ? 0 : t.getPendingCins().split(",").length).sum());
        model.addAttribute("evaluations", evals.forConducteur(cin));
        model.addAttribute("moyenne", evals.moyenne(cin));
        model.addAttribute("notifications", notifs.unreadConducteur(cin));
        return "conducteur/dashboard";
    }

    @GetMapping("/trajets")
    public String mesTrajets(HttpSession session, Model model) {
        String redirect = guard(session);
        if (redirect != null) return redirect;
        String cin = SessionUser.cin(session);
        model.addAttribute("trajets", trajets.getTrajetsConducteur(cin));
        model.addAttribute("trajetsRecurrents", trajets.getTrajetsRecurrents(cin));
        model.addAttribute("trajetsNormaux", trajets.getTrajetsConducteurSansSchedule(cin));
        return "conducteur/trajets";
    }

    @GetMapping("/trajets/new")
    public String form(HttpSession session, Model model) {
        String redirect = guard(session);
        if (redirect != null) return redirect;
        model.addAttribute("form", new TrajetForm());
        return "conducteur/trajet-form";
    }

    @PostMapping("/trajets")
    public String create(@Valid @ModelAttribute("form") TrajetForm form, BindingResult bindingResult, HttpSession session) {
        String redirect = guard(session);
        if (redirect != null) return redirect;
        if (bindingResult.hasErrors()) return "conducteur/trajet-form";
        trajets.createTrajet(SessionUser.cin(session), form);
        return "redirect:/conducteur/trajets";
    }

    @PostMapping("/trajets/{id}/finish")
    public String finish(@PathVariable Long id) {
        trajets.finishTrajet(id);
        return "redirect:/conducteur/trajets";
    }

    @PostMapping("/trajets/{id}/price")
    public String price(@PathVariable Long id, @RequestParam double prix) {
        trajets.modifierPrix(id, prix);
        return "redirect:/conducteur/trajets";
    }

    @PostMapping("/trajets/{id}/passagers/remove")
    public String removePassenger(@PathVariable Long id, @RequestParam String passagerCin, HttpSession session) {
        String redirect = guard(session);
        if (redirect != null) return redirect;
        trajets.supprimerPassagerAccepte(id, passagerCin, SessionUser.cin(session));
        return "redirect:/conducteur/trajets";
    }

    @PostMapping("/trajets/{id}/delete")
    public String delete(@PathVariable Long id) {
        trajets.deleteTrajet(id);
        return "redirect:/conducteur/trajets";
    }

    @GetMapping("/demandes")
    public String demandes(HttpSession session, Model model) {
        String redirect = guard(session);
        if (redirect != null) return redirect;
        model.addAttribute("trajets", trajets.getTrajetsConducteur(SessionUser.cin(session)).stream().filter(t -> t.getPendingCins() != null && !t.getPendingCins().isBlank()).toList());
        model.addAttribute("passagers", users.passagers());
        return "conducteur/demandes";
    }

    @GetMapping("/passagers")
    public String passagers(HttpSession session, Model model) {
        String redirect = guard(session);
        if (redirect != null) return redirect;
        var mesTrajets = trajets.getTrajetsConducteur(SessionUser.cin(session));
        model.addAttribute("trajets", mesTrajets.stream().filter(t -> t.getAcceptedCins() != null && !t.getAcceptedCins().isBlank()).toList());
        model.addAttribute("passagers", users.passagers());
        return "conducteur/passagers";
    }

    @PostMapping("/demandes/{id}/accept")
    public String accept(@PathVariable Long id, @RequestParam String passagerCin, RedirectAttributes redirectAttributes) {
        trajets.accepterPassager(id, passagerCin);
        redirectAttributes.addFlashAttribute("success", "Passager accepte");
        return "redirect:/conducteur/demandes";
    }

    @PostMapping("/demandes/{id}/refuse")
    public String refuse(@PathVariable Long id, @RequestParam String passagerCin) {
        trajets.refuserPassager(id, passagerCin);
        return "redirect:/conducteur/demandes";
    }

    @PostMapping("/reclamations")
    public String reclamation(@RequestParam String reservationId, @RequestParam String accusedId, @RequestParam(defaultValue = "Autre") String preset, @RequestParam String message, HttpSession session) {
        String redirect = guard(session);
        if (redirect != null) return redirect;
        recs.submit(reservationId, SessionUser.cin(session), "conducteur", accusedId, "passager", preset, message);
        return "redirect:/conducteur/trajets";
    }

    @PostMapping("/request-help")
    public String requestHelp(HttpSession session, RedirectAttributes redirectAttributes) {
        String redirect = guard(session);
        if (redirect != null) return redirect;
        String cin = SessionUser.cin(session);
        var user = users.conducteur(cin).orElseThrow();
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
        model.addAttribute("notifications", notifs.conducteurNotifications(SessionUser.cin(session)));
        return "conducteur/notifications";
    }

    @PostMapping("/notifications/{id}/read")
    public String markNotifRead(@PathVariable String id, HttpSession session) {
        String redirect = guard(session);
        if (redirect != null) return redirect;
        notifs.marquerCommelueConducteur(SessionUser.cin(session), id);
        return "redirect:/conducteur/notifications";
    }

    @PostMapping("/notifications/read-all")
    public String markAllNotifRead(HttpSession session) {
        String redirect = guard(session);
        if (redirect != null) return redirect;
        notifs.marquerToutesCommelueConducteur(SessionUser.cin(session));
        return "redirect:/conducteur/notifications";
    }
}
