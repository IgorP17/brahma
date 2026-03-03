package com.example.gateway;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GatewayResource {
    
    @POST
    @Path("/register")
    public Response registerTerminal(TerminalRegistration registration) {
        System.out.println("📥 Регистрация терминала: " + registration.id);
        System.out.println("📦 Данные: " + registration.data);
        
        // TODO: Сохранить в БД
        // TODO: Отправить в Kafka
        
        return Response.ok()
            .entity("{\"status\":\"pending\",\"id\":\"" + registration.id + "\"}")
            .build();
    }
    
    @GET
    @Path("/health")
    public String health() {
        return "OK";
    }
}
