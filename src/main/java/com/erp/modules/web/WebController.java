package com.erp.modules.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("activePage", "dashboard");
        return "dashboard";
    }
    
    @GetMapping("/procurement")
    public String procurement(Model model) {
        model.addAttribute("pageTitle", "Procurement Orders");
        model.addAttribute("activePage", "procurement");
        return "procurement/list";
    }

    @GetMapping("/procurement/new")
    public String newOrder(Model model) {
        model.addAttribute("pageTitle", "New Purchase Order");
        model.addAttribute("activePage", "procurement");
        return "procurement/form";
    }

    @GetMapping("/inventory")
    public String inventory(Model model) {
        model.addAttribute("pageTitle", "Inventory Stock");
        model.addAttribute("activePage", "inventory");
        return "inventory/stock";
    }

    @GetMapping("/inventory/receive")
    public String receiveGoods(Model model) {
        model.addAttribute("pageTitle", "Receive Goods");
        model.addAttribute("activePage", "inventory");
        return "inventory/receive";
    }

    @GetMapping("/manufacturing")
    public String manufacturing(Model model) {
        model.addAttribute("pageTitle", "Work Orders");
        model.addAttribute("activePage", "manufacturing");
        return "manufacturing/work-orders";
    }

    @GetMapping("/manufacturing/new")
    public String newWorkOrder(Model model) {
        model.addAttribute("pageTitle", "New Work Order");
        model.addAttribute("activePage", "manufacturing");
        return "manufacturing/form";
    }

    @GetMapping("/mrp")
    public String mrp(Model model) {
        model.addAttribute("pageTitle", "MRP Runs");
        model.addAttribute("activePage", "mrp");
        return "mrp/runs";
    }
    
    @GetMapping("/mrp/run")
    public String runMrp(Model model) {
        model.addAttribute("pageTitle", "Run MRP");
        model.addAttribute("activePage", "mrp");
        return "mrp/run";
    }
}