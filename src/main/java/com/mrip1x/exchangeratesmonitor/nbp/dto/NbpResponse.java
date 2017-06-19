package com.mrip1x.exchangeratesmonitor.nbp.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class NbpResponse {
    private Rate[] rates;
    private String code;
    private String table;
    private String currency;

    @Getter
    @Setter
    @EqualsAndHashCode
    @ToString
    public static class Rate {
        private String no;
        private String mid;
        private String effectiveDate;
    }
}
