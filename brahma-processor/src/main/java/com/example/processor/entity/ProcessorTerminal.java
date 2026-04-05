package com.example.processor.entity;

import com.example.common.TerminalStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "terminals", schema = "processor")
public class ProcessorTerminal extends PanacheEntityBase {

    @Id
    public String id;

    @JdbcTypeCode(SqlTypes.JSON)
    public java.util.Map<String, Object> data;

    @Enumerated(EnumType.STRING)
    public TerminalStatus status;

    @Column(name = "created_at")
    public java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")  // 🔥 NULL при создании, NOW() при обновлении
    public java.time.LocalDateTime updatedAt;

    // Геттеры
    public String getLocation() {
        return (String) data.get("location");
    }
}