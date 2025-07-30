package com.example.serverchillrate.entity;

import com.example.serverchillrate.entity.UserApp;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;

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
    @JsonIgnore
    @ManyToOne
     UserApp _user;
    /// client time,where data get
     Date dateTime;
    /// data with sensor
    @JdbcTypeCode(SqlTypes.JSON)
     String data;
}
