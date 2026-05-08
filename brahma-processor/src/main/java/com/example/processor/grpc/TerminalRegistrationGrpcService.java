package com.example.processor.grpc;

import com.example.common.TerminalLogicHelper;
import com.example.common.TerminalStatus;
import com.example.processor.entity.ProcessorTerminal;
import com.example.processor.grpc.*;
import com.example.terminal.grpc.*;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.Map;

@GrpcService
public class TerminalRegistrationGrpcService extends TerminalRegistrationServiceGrpc.TerminalRegistrationServiceImplBase {

    private static final Logger log = Logger.getLogger(TerminalRegistrationGrpcService.class);

    @Override
    @Blocking
    public void registerTerminal(RegisterTerminalRequest request, io.grpc.stub.StreamObserver<RegisterTerminalResponse> responseObserver) {
        String id = request.getId();
        Map<String, String> dataMap = request.getDataMap();
        String source = request.getSource().isEmpty() ? "GRPC" : request.getSource();

        log.infof("📥 gRPC: Received registration request for terminal %s via %s", id, source);

        try {
            String status = TerminalLogicHelper.determineStatus(dataMap.get("location")).name();

            // Save to DB
            ProcessorTerminal terminal = ProcessorTerminal.findById(id);
            if (terminal == null) {
                terminal = new ProcessorTerminal();
                terminal.id = id;
                terminal.data = new java.util.HashMap<>(dataMap);
                terminal.createdAt = LocalDateTime.now();
                terminal.updatedAt = null;
            } else {
                terminal.data = new java.util.HashMap<>(dataMap);
                terminal.updatedAt = LocalDateTime.now();
            }
            terminal.source = source; // "GRPC"
            terminal.receivedAt = LocalDateTime.now();

            terminal.persist();

            log.infof("✅ DB: terminal %s saved via %s, status: %s", id, source, status);

            // Prepare response
            RegisterTerminalResponse response = RegisterTerminalResponse.newBuilder()
                    .setId(id)
                    .setStatus(status)
                    .setMessage("Successfully registered via " + source)
                    .setReceivedAt(terminal.receivedAt.toString()) // ISO string
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.errorf("❌ gRPC: Error processing terminal %s: %s", id, e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}