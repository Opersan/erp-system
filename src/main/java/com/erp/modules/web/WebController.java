package com.erp.modules.web;

import com.erp.modules.inventory.service.InventoryService;
import com.erp.modules.manufacturing.service.ManufacturingService;
import com.erp.modules.procurement.service.ProcurementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final ProcurementService procurementService;
    private final InventoryService inventoryService;
    private final ManufacturingService manufacturingService;

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
        model.addAttribute("pageTitle", "Kontrol Paneli");
        model.addAttribute("activePage", "dashboard");
        
        // KPI Data
        model.addAttribute("totalOrders", procurementService.getAllOrders().size());
        model.addAttribute("totalStock", inventoryService.getAllStock().size());
        model.addAttribute("totalWorkOrders", manufacturingService.getAllWorkOrders().size());
        model.addAttribute("pendingOrders", procurementService.getAllOrders().stream()
            .filter(o -> o.getStatus().toString().equals("DRAFT") || o.getStatus().toString().equals("APPROVED"))
            .count());
        
        // Recent orders for table
        var recentOrders = procurementService.getAllOrders().stream()
            .sorted((a, b) -> b.getId().compareTo(a.getId()))
            .limit(5)
            .toList();
        model.addAttribute("recentOrders", recentOrders);
        
        // Low stock items
        var lowStockItems = inventoryService.getAllStock().stream()
            .filter(s -> s.getOnHand() < 10)
            .limit(5)
            .toList();
        model.addAttribute("lowStockItems", lowStockItems);
        
        return "dashboard";
    }
    
    @GetMapping("/procurement")
    public String procurement(Model model) {
        model.addAttribute("pageTitle", "Satın Alma Siparişleri");
        model.addAttribute("activePage", "procurement");
        model.addAttribute("orders", procurementService.getAllOrders());
        return "procurement/list";
    }

    @GetMapping("/procurement/new")
    public String newOrder(Model model) {
        model.addAttribute("pageTitle", "Yeni Satın Alma Siparişi");
        model.addAttribute("activePage", "procurement");
        model.addAttribute("suppliers", procurementService.getAllSuppliers());
        model.addAttribute("items", procurementService.getAllItems());
        return "procurement/form";
    }

    @GetMapping("/inventory")
    public String inventory(Model model) {
        model.addAttribute("pageTitle", "Stok Durumu");
        model.addAttribute("activePage", "inventory");
        model.addAttribute("stocks", inventoryService.getAllStock());
        return "inventory/stock";
    }

    @GetMapping("/inventory/receive")
    public String receiveGoods(Model model) {
        model.addAttribute("pageTitle", "Mal Kabul");
        model.addAttribute("activePage", "inventory");
        model.addAttribute("pendingOrders", inventoryService.getPendingPurchaseOrders());
        model.addAttribute("warehouses", inventoryService.getAllWarehouses());
        model.addAttribute("items", procurementService.getAllItems());
        return "inventory/receive";
    }

    @GetMapping("/manufacturing")
    public String manufacturing(Model model) {
        model.addAttribute("pageTitle", "İş Emirleri");
        model.addAttribute("activePage", "manufacturing");
        model.addAttribute("workOrders", manufacturingService.getAllWorkOrders());
        return "manufacturing/work-orders";
    }

    @GetMapping("/manufacturing/new")
    public String newWorkOrder(Model model) {
        model.addAttribute("pageTitle", "Yeni İş Emri");
        model.addAttribute("activePage", "manufacturing");
        model.addAttribute("items", manufacturingService.getItemsWithBOM());
        return "manufacturing/form";
    }

    @GetMapping("/mrp")
    public String mrp(Model model) {
        model.addAttribute("pageTitle", "MRP Çalıştırmaları");
        model.addAttribute("activePage", "mrp");
        return "mrp/runs";
    }
    
    @GetMapping("/mrp/run")
    public String runMrp(Model model) {
        model.addAttribute("pageTitle", "MRP Çalıştır");
        model.addAttribute("activePage", "mrp");
        return "mrp/run";
    }
}
