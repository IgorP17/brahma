package com.example.webui.service;

import com.example.webui.entity.GatewayTerminal;
import com.example.webui.entity.ProcessorTerminal;
import com.example.webui.model.TerminalViewGateway;
import com.example.webui.model.TerminalViewProcessor;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TerminalService {

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

    public TerminalViewProcessor findProcessorTerminalById(String id) {
        ProcessorTerminal t = ProcessorTerminal.findById(id);
        var view = new TerminalViewProcessor();
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