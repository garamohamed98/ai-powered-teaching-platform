package com.mohamedgara.ai_teaching_platform.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

@RestController
public class TestDbController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/test/db")
    public String testDatabase(){
        try {
            dataSource.getConnection().close();
            return "Database connect successful";
        }catch (Exception e){
            return "Database connect failed" + e.getMessage();
        }
    }
}
