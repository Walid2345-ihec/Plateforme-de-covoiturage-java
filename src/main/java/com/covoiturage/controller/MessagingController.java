package com.covoiturage.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.covoiturage.entity.Conversation;
import com.covoiturage.entity.Message;
import com.covoiturage.security.SessionUser;
import com.covoiturage.service.MessagingService;
import com.covoiturage.service.NotificationService;
import com.covoiturage.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/messages")
public class MessagingController {
    private final MessagingService messages;
    private final UserService users;
    private final NotificationService notifications;

    public MessagingController(MessagingService messages, UserService users, NotificationService notifications) {
        this.messages = messages;
        this.users = users;
        this.notifications = notifications;
    }

    @GetMapping
    public String conversation(@RequestParam(required = false) String with,
                               @RequestParam(required = false) String trajetId,
                               HttpSession session,
                               Model model) {
        if (!SessionUser.isLogged(session)) return "redirect:/";

        String role = SessionUser.role(session);
        String currentCin = SessionUser.cin(session);
        model.addAttribute("role", role);
        model.addAttribute("currentCin", currentCin);
        model.addAttribute("currentName", displayName(currentCin));
        model.addAttribute("trajetId", trajetId == null ? "" : trajetId);

        if ("admin".equals(role)) {
            List<Conversation> conversations = messages.adminConversations();
            model.addAttribute("conversations", conversations);
            if ((with == null || with.isBlank()) && !conversations.isEmpty()) {
                with = conversations.get(0).getUserId();
            }
        } else {
            // For conducteurs and passagers, load user conversations from messages
            List<Map<String, String>> userConversations = messages.userConversations(currentCin);
            model.addAttribute("conversations", userConversations);
            if ((with == null || with.isBlank()) && !userConversations.isEmpty()) {
                with = userConversations.get(0).get("userId");
            }
        }

        String otherCin = normalizeRecipient(with);
        model.addAttribute("otherCin", otherCin);
        model.addAttribute("otherName", otherCin.isBlank() ? "" : displayName(otherCin));
        model.addAttribute("otherType", otherCin.isBlank() ? "" : userType(otherCin));

        if (!otherCin.isBlank()) {
            List<Message> thread = messages.conversation(currentCin, otherCin);
            model.addAttribute("messages", thread);
            model.addAttribute("lastMessage", thread.stream().max(Comparator.comparing(Message::getTimestamp)).orElse(null));
        }

        return "messages/conversation";
    }

    @PostMapping("/admin/start")
    public String startAdminConversation(@RequestParam String userCin, @RequestParam(defaultValue = "admin") String triggeredBy, HttpSession session) {
        if (!SessionUser.hasRole(session, "admin")) return "redirect:/";
        messages.getOrCreateAdminConversation(userCin, SessionUser.cin(session), triggeredBy);
        return "redirect:/messages?with=" + userCin;
    }

    @PostMapping
    public String send(@RequestParam String recipientCin,
                       @RequestParam(required = false) String trajetId,
                       @RequestParam String content,
                       HttpSession session) {
        if (!SessionUser.isLogged(session)) return "redirect:/";
        String senderCin = SessionUser.cin(session);
        String normalizedRecipient = normalizeRecipient(recipientCin);
        if (content == null || content.trim().isEmpty()) {
            return "redirect:/messages?with=" + normalizedRecipient;
        }

        messages.send(senderCin, displayName(senderCin), normalizedRecipient, displayName(normalizedRecipient), content.trim(), trajetId);
        if (SessionUser.hasRole(session, "admin")) {
            messages.getOrCreateAdminConversation(normalizedRecipient, senderCin, "admin");
            notifications.notifyPassager(normalizedRecipient, senderCin, trajetId == null ? "" : trajetId, "MESSAGE", "Message admin: " + preview(content));
        } else if (users.admin(normalizedRecipient).isPresent()) {
            String adminCin = normalizedRecipient;
            messages.getOrCreateAdminConversation(senderCin, adminCin, trajetId == null || trajetId.isBlank() ? "admin" : trajetId);
            notifications.notifyAdmin("MESSAGE", "Message de " + displayName(senderCin) + ": " + preview(content), senderCin, null, trajetId);
        }
        return "redirect:/messages?with=" + normalizedRecipient + (trajetId == null || trajetId.isBlank() ? "" : "&trajetId=" + trajetId);
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id, @RequestParam String otherCin, HttpSession session) {
        if (!SessionUser.isLogged(session)) return "redirect:/";
        messages.deleteMessage(id, SessionUser.cin(session), SessionUser.hasRole(session, "admin"));
        return "redirect:/messages?with=" + otherCin;
    }

    @GetMapping("/api/search")
    @ResponseBody
    public List<Map<String, String>> searchUsers(@RequestParam String q, HttpSession session) {
        List<Map<String, String>> results = new ArrayList<>();
        
        if (q == null || q.trim().isEmpty()) {
            return results;
        }

        String query = normalize(q.toLowerCase().trim());
        
        // Search in passagers
        users.passagers().stream()
            .filter(p -> matchesQuery(query, p.getNom(), p.getPrenom(), p.getMail()))
            .forEach(p -> results.add(Map.of(
                "cin", p.getCin(),
                "nom", p.getNom(),
                "prenom", p.getPrenom(),
                "mail", p.getMail(),
                "role", "Passager"
            )));
        
        // Search in conducteurs
        users.conducteurs().stream()
            .filter(c -> matchesQuery(query, c.getNom(), c.getPrenom(), c.getMail()))
            .forEach(c -> results.add(Map.of(
                "cin", c.getCin(),
                "nom", c.getNom(),
                "prenom", c.getPrenom(),
                "mail", c.getMail(),
                "role", "Conducteur"
            )));
        
        return results;
    }

    private boolean matchesQuery(String query, String nom, String prenom, String mail) {
        String normalizedNom = normalize(nom);
        String normalizedPrenom = normalize(prenom);
        String normalizedMail = normalize(mail);
        return normalizedNom.contains(query) || normalizedPrenom.contains(query) || normalizedMail.contains(query);
    }

    private String normalize(String str) {
        if (str == null) return "";
        return str.toLowerCase().replaceAll("[àáâäã]", "a")
                .replaceAll("[èéêë]", "e")
                .replaceAll("[ìíîï]", "i")
                .replaceAll("[òóôöõ]", "o")
                .replaceAll("[ùúûü]", "u")
                .replaceAll("[ñ]", "n")
                .replaceAll("[ç]", "c");
    }

    private String displayName(String cin) {
        return users.passager(cin).map(p -> p.getPrenom() + " " + p.getNom())
                .or(() -> users.conducteur(cin).map(c -> c.getPrenom() + " " + c.getNom()))
                .or(() -> users.admin(cin).map(a -> a.getPrenom() + " " + a.getNom()))
                .orElse(cin);
    }

    private String normalizeRecipient(String cin) {
        if (cin == null || cin.isBlank()) return "";
        if ("admin".equalsIgnoreCase(cin)) {
            return users.admins().stream().findFirst().map(a -> a.getCin()).orElse(cin);
        }
        return cin;
    }

    private String userType(String cin) {
        if (users.admin(cin).isPresent()) return "Administrateur";
        if (users.conducteur(cin).isPresent()) return "Conducteur";
        if (users.passager(cin).isPresent()) return "Passager";
        return "Utilisateur";
    }

    private String preview(String content) {
        String value = content == null ? "" : content.trim();
        return value.length() > 50 ? value.substring(0, 50) + "..." : value;
    }
}
