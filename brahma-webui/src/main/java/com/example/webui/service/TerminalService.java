package com.example.webui.service;

import com.example.webui.entity.GatewayTerminal;
import com.example.webui.model.TerminalViewGateway;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class TerminalService {

    public List<TerminalViewGateway> getAllTerminals() {
        var terminals = GatewayTerminal.listAll();
        var result = new ArrayList<TerminalViewGateway>();
        for (Object obj : terminals) {
            var t = (GatewayTerminal) obj;
            var view = new TerminalViewGateway();
            view.id = t.id;
            view.model = (String) t.data.get("model");
            view.location = (String) t.data.get("location");
            view.status = t.status;
            view.createdAt = t.createdAt.toString();
            view.updatedAt = t.updatedAt.toString();
            result.add(view);
        }
        return result;
    }

    public TerminalViewGateway findTerminalById(String id) {
        GatewayTerminal t = GatewayTerminal.findById(id);
        var view = new TerminalViewGateway();
        if (t == null) {
            view.id = "NOT_FOUND";
            view.model = "-";
            view.location = "-";
            view.status = "-";
            view.createdAt = "-";
            view.updatedAt = "-";
        } else {
            view.id = t.id;
            view.model = t.getModel();
            view.location = t.getLocation();
            view.status = t.status;
            view.createdAt = t.createdAt != null ? t.createdAt.toString() : "";
            view.updatedAt = t.updatedAt != null ? t.updatedAt.toString() : "";
        }
        return view;
    }
}