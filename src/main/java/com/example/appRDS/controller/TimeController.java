package com.example.appRDS.controller;

import com.example.appRDS.service.TimeZoneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.ZoneId;
import java.util.Map;
import java.util.TimeZone;

@RestController
@RequestMapping("/api/time")
@Log4j2
@RequiredArgsConstructor
public class TimeController {

    private final DataSource dataSource;
    private final TimeZoneService timeZoneService;

    @GetMapping("/now")
    public Map<String,String>getNow(){

        String sessionTimeZone = "";
        String globalTimeZone = "";
        String now = "";

        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("select now()");
            ResultSet resultSet = preparedStatement.executeQuery();){

            resultSet.next();
            now = resultSet.getString(1);
            //sessionTimeZone = resultSet.getString(2);
            //globalTimeZone = resultSet.getString(3);

            log.info("DB Time Zone: " + timeZoneService.getDbTimeZone());
            log.info("JVM Time Zone: " + TimeZone.getDefault().getID());
            log.info("Current Time (DB): " + now);
        }catch (Exception e){
            e.printStackTrace();
        }
        //return Map.of("sessionTimeZone",sessionTimeZone,"globalTimeZone",globalTimeZone,"NOW", now);
        return Map.of("NOW", now, "DB Time Zone", timeZoneService.getDbTimeZone());
    }
}
