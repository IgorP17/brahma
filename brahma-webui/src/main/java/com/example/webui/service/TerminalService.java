package com.example.webui.service;

import com.example.webui.entity.GatewayTerminal;
import com.example.webui.entity.ProcessorTerminal;
import com.example.webui.model.TerminalViewGateway;
import com.example.webui.model.TerminalViewProcessor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

@ApplicationScoped
public class TerminalService {

    private static final Logger log = Logger.getLogger(TerminalService.class);

    public TerminalViewGateway findTerminalById(String id) {
        log.infof("🔍 Searching for terminal in gateway: %s", id);
        GatewayTerminal t = GatewayTerminal.findById(id);
        var view = new TerminalViewGateway();
        if (t == null) {
            view.id = "NOT_FOUND";
            view.model = "-";
            view.location = "-";
            view.status = "-";
            view.createdAt = "-";
            view.updatedAt = "-";
            log.warnf("⚠️ Terminal %s not found in gateway", id);
        } else {
            view.id = t.id;
            view.model = t.getModel();
            view.location = t.getLocation();
            view.status = t.status;
            view.createdAt = t.createdAt != null ? t.createdAt.toString() : "";
            view.updatedAt = t.updatedAt != null ? t.updatedAt.toString() : "";
            log.infof("✅ Terminal %s found in gateway", id);
        }
        return view;
    }

    public TerminalViewProcessor findProcessorTerminalById(String id) {
        log.infof("🔍 Searching for terminal in processor: %s", id);
        ProcessorTerminal t = ProcessorTerminal.findById(id);
        var view = new TerminalViewProcessor();
        if (t == null) {
            view.id = "NOT_FOUND";
            view.model = "-";
            view.location = "-";
            view.status = "-";
            view.createdAt = "-";
            view.updatedAt = "-";
            log.warnf("⚠️ Terminal %s not found in processor", id);
        } else {
            view.id = t.id;
            view.model = t.getModel();
            view.location = t.getLocation();
            view.status = t.status;
            view.createdAt = t.createdAt != null ? t.createdAt.toString() : "";
            view.updatedAt = t.updatedAt != null ? t.updatedAt.toString() : "";
            log.infof("✅ Terminal %s found in processor", id);
        }
        return view;
    }

    @Transactional
    public void deleteTerminalFromGateway(String id) {
        log.infof("🗑️ Deleting terminal %s from gateway", id);
        GatewayTerminal.deleteById(id);
        log.infof("✅ Terminal %s deleted from gateway", id);
    }

    @Transactional
    public void deleteTerminalFromProcessor(String id) {
        log.infof("🗑️ Deleting terminal %s from processor", id);
        ProcessorTerminal.deleteById(id);
        log.infof("✅ Terminal %s deleted from processor", id);
    }

    @Transactional
    public void deleteTerminalFromBoth(String id) {
        log.infof("🗑️ Deleting terminal %s from gateway and processor", id);
        deleteTerminalFromGateway(id);
        deleteTerminalFromProcessor(id);
        log.infof("✅ Terminal %s deleted from both gateway and processor", id);
    }
}