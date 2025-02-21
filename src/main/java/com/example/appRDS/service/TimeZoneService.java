package com.example.appRDS.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.TimeZone;

@Service
public class TimeZoneService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String dbTimeZone = "Asia/Seoul";  // 기본 타임존 설정

    @PostConstruct
    public void setTimeZone() {
        // DB에서 타임존을 조회
        dbTimeZone = getDBTimeZone();

        // DB에서 조회한 타임존을 설정
        TimeZone.setDefault(TimeZone.getTimeZone(dbTimeZone));
        System.out.println("Time Zone set to: " + dbTimeZone);
    }

    private String getDBTimeZone() {
        // DB에서 타임존을 조회하는 쿼리 실행
        String query = "SELECT @@global.time_zone";
        return jdbcTemplate.queryForObject(query, String.class);
    }

    public String getDbTimeZone() {
        return dbTimeZone;
    }
}
