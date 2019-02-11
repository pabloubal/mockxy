package io.github.pabloubal.mockxy.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MockxyApplicationTests {

    @Before
    public void setup(){
        System.setProperty("http.proxyHost", "localhost");
        System.setProperty("http.proxyPort", "8000");

        System.setProperty("socksProxyHost", "localhost");
        System.setProperty("socksProxyPort", "1080");
        System.setProperty("socksNonProxyHosts", "httpbin*");
    }

	@Test
	public void get() {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://httpbin.org/get", String.class);

        Assert.assertEquals(200, responseEntity.getStatusCodeValue());

    }

    @Test
    public void post() {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://httpbin.org/post", "{\"test\": \"TEST\"}", String.class);

        Assert.assertEquals(200, responseEntity.getStatusCodeValue());
        Assert.assertTrue(responseEntity.getBody().contains("{\\\"test\\\": \\\"TEST\\\"}"));

    }

}

