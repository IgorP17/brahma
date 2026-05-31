package com.example.processor.grpc;

import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.quarkus.grpc.GrpcService; // <-- Импортируем
import jakarta.inject.Singleton;

@Singleton
@GrpcService // <-- ОБЯЗАТЕЛЬНО ДОБАВЛЯЕМ ЭТУ АННОТАЦИЮ
public class GrpcReflectionService implements BindableService {

    private final BindableService delegate;

    @SuppressWarnings("deprecation")
    public GrpcReflectionService() {
        this.delegate = ProtoReflectionService.newInstance();
    }

    @Override
    public ServerServiceDefinition bindService() {
        return delegate.bindService();
    }
}