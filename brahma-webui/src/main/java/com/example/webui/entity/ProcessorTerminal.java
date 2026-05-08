package com.example.webui.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "terminals", schema = "processor")
public class ProcessorTerminal extends PanacheEntityBase {

    @Id
    public String id;

    @JdbcTypeCode(SqlTypes.JSON)
    public java.util.Map<String, Object> data;

    public String status;

    @Column(name = "created_at")
    public java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    public java.time.LocalDateTime updatedAt;

    @Column(name = "source")
    public String source; // "KAFKA" or "GRPC"

    @Column(name = "received_at")
    public java.time.LocalDateTime receivedAt; // When request was received

    // Геттеры
    public String getModel() {
        return (String) data.get("model");
    }

    public String getLocation() {
        return (String) data.get("location");
    }

    public static void deleteById(String id) {
        if (id == null) return;
        ProcessorTerminal.delete("id", id);
    }
}