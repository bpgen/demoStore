package com.store.demo.steps;

import com.store.demo.DemoApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = DemoApplication.class, loader = SpringBootContextLoader.class)
@Slf4j
public class CucumberTest {
    @LocalServerPort
    protected int port;
    protected String baseUrl = "http://localhost:";
    @Autowired
    protected TestRestTemplate testRestTemplate;

}
