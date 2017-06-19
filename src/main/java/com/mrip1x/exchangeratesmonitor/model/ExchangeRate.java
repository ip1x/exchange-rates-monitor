package com.mrip1x.exchangeratesmonitor.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mrip1x.exchangeratesmonitor.support.JsonLocalDateDeserializer;
import com.mrip1x.exchangeratesmonitor.support.JsonLocalDateSerializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "exchange_rate")
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ExchangeRate {
    @Id
    @JsonSerialize(using = JsonLocalDateSerializer.class)
    @JsonDeserialize(using = JsonLocalDateDeserializer.class)
    private LocalDate date;
    @Column
    private Double rate;

    public ExchangeRate() {
    }

    public ExchangeRate(LocalDate date, Double rate) {
        this.date = date;
        this.rate = rate;
    }
}
