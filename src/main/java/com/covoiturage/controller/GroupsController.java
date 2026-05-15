package com.covoiturage.controller;

import java.util.Arrays;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.covoiturage.security.SessionUser;
import com.covoiturage.service.GroupService;
import com.covoiturage.service.TrajetService;
import com.covoiturage.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/groups")
public class GroupsController {
    private final GroupService groups;
    private final TrajetService trajets;
    private final UserService users;

    public GroupsController(GroupService groups, TrajetService trajets, UserService users) {
        this.groups = groups;
        this.trajets = trajets;
        this.users = users;
    }

    @GetMapping
    public String list(HttpSession session, Model model) {
        if (!SessionUser.isLogged(session)) return "redirect:/";
        model.addAttribute("groups", groups.groupsFor(SessionUser.role(session), SessionUser.cin(session)));
        model.addAttribute("role", SessionUser.role(session));
        if (SessionUser.hasRole(session, "conducteur")) {
            model.addAttribute("trajets", trajets.getTrajetsConducteur(SessionUser.cin(session)));
            model.addAttribute("passagers", users.passagers());
        }
        return "groups/list";
    }

    @PostMapping
    public String create(@RequestParam String groupName, @RequestParam String memberCins, HttpSession session) {
        if (!SessionUser.hasRole(session, "conducteur")) return "redirect:/";
        groups.create(SessionUser.cin(session), groupName, Arrays.asList(memberCins.split(",")));
        return "redirect:/groups";
    }

    @GetMapping("/{id}")
    public String chat(@PathVariable String id, HttpSession session, Model model) {
        if (!SessionUser.isLogged(session)) return "redirect:/";
        model.addAttribute("group", groups.group(id));
        model.addAttribute("messages", groups.messages(id));
        model.addAttribute("role", SessionUser.role(session));
        return "groups/chat";
    }

    @PostMapping("/{id}/messages")
    public String send(@PathVariable String id, @RequestParam String content, HttpSession session) {
        if (!SessionUser.isLogged(session)) return "redirect:/";
        groups.sendMessage(id, SessionUser.cin(session), displayName(SessionUser.cin(session)), content);
        return "redirect:/groups/" + id;
    }

    @PostMapping("/{groupId}/messages/{messageId}/delete")
    public String delete(@PathVariable String groupId, @PathVariable String messageId, HttpSession session) {
        if (!SessionUser.isLogged(session)) return "redirect:/";
        groups.deleteMessage(messageId, SessionUser.cin(session));
        return "redirect:/groups/" + groupId;
    }

    private String displayName(String cin) {
        return users.passager(cin).map(p -> p.getPrenom() + " " + p.getNom())
                .or(() -> users.conducteur(cin).map(c -> c.getPrenom() + " " + c.getNom()))
                .or(() -> users.admin(cin).map(a -> a.getPrenom() + " " + a.getNom()))
                .orElse(cin);
    }
}
