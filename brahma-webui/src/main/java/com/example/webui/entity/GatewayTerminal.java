package com.example.webui.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "terminals", schema = "gateway")
public class GatewayTerminal extends PanacheEntityBase {

    @Id
    public String id;

    @JdbcTypeCode(SqlTypes.JSON)
    public java.util.Map<String, Object> data;

    public String status;

    @Column(name = "created_at")
    public java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    public java.time.LocalDateTime updatedAt;

    // Геттеры
    public String getModel() {
        return (String) data.get("model");
    }

    public String getLocation() {
        return (String) data.get("location");
    }

    public static void deleteById(String id) {
        if (id == null) return;
        GatewayTerminal.delete("id", id);
    }
}