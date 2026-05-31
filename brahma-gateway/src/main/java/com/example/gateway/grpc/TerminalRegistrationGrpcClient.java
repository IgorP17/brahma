package com.example.gateway.grpc;

import com.example.terminal.grpc.*;
import io.quarkus.grpc.GrpcClient;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TerminalRegistrationGrpcClient {

    @GrpcClient("processor") // имя сервиса из application.properties
    TerminalRegistrationServiceGrpc.TerminalRegistrationServiceBlockingStub processorClient;

    public RegisterTerminalResponse registerTerminal(String id, java.util.Map<String, String> data, String source) {
        RegisterTerminalRequest request = RegisterTerminalRequest.newBuilder()
                .setId(id)
                .putAllData(data)
                .setSource(source) // "GRPC"
                .build();

        return processorClient.registerTerminal(request);
    }
}