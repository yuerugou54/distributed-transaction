package com.mxd.dt.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.Date;

@Entity(name = "accounts")
@Data
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"created", "lastModified", "bankCode"}, allowGetters = true)
@NoArgsConstructor
public class Account {

    @Id
    @Column(unique = true, nullable = false)
    @NotEmpty
    private String number;

    @NotEmpty
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String bankCode;

    @NotEmpty
    @Column(nullable = false)
    private String currencyCode;

    private BigDecimal balance;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date created;

    @LastModifiedDate
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModified;
}
