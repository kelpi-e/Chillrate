package com.example.serverchillrate.models;

import com.example.serverchillrate.models.UserApp;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.jdbc.JsonJdbcType;

import java.sql.SQLType;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/// @struct UserData
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="_userData")
@Builder
public class UserData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    /// userID
    @ManyToOne
     UserApp _user;
    /// client time,where data get
     Date dateTime;
    /// data with sensor
    @JdbcTypeCode(SqlTypes.JSON)
     String data;
}
