package com.store.demo.steps;

import com.store.demo.model.Checkout;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BuyerSteps extends CucumberTest {

    @When("buyer with {string} checkout {string} {string} with response code {int}")
    public void buyerWithCheckoutWithResponseCode(String userId, String quantity, String productCode, int responseCode) {
        Map<String, Long> productCodeAndQuantity = new HashMap<>();
        productCodeAndQuantity.put(productCode, Long.valueOf(quantity));
        Checkout checkout = Checkout.builder()
                .userId(Long.valueOf(userId))
                .productCodeAndQuantity(productCodeAndQuantity)
                .build();

        HttpEntity httpEntity = new HttpEntity<>(checkout, new HttpHeaders());
        ParameterizedTypeReference<Boolean> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<Boolean> response =
                testRestTemplate.exchange(baseUrl + port + "/checkout", HttpMethod.POST, httpEntity, responseType);
        assertTrue(response.getStatusCode().value() == responseCode);
    }

    @Then("buyer with {string} see total price {string} with {string}")
    public void buyerWithSeeTotalPriceWith(String userId, String totalPrice, String bundleDeal) {
        HttpEntity httpEntity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<Checkout> response =
                testRestTemplate.exchange(baseUrl + port + "/checkout/" + Long.valueOf(userId), HttpMethod.GET, httpEntity, Checkout.class);

        BigDecimal calculatedValue = response.getBody().getTotalPrice();
        assertTrue(calculatedValue.setScale(2, RoundingMode.HALF_UP).equals(new BigDecimal(totalPrice)));
        assertTrue(response.getBody().getBundleDeal().equals(bundleDeal));
    }


    @When("buyer with {string} checkout reduce {string} {string} with response code {int}")
    public void buyerWithCheckoutReduceWithResponseCode(String userId, String quantity, String productCode, int responseCode) {
        Map<String, Long> productCodeAndQuantity = new HashMap<>();
        productCodeAndQuantity.put(productCode, Long.valueOf(quantity));
        Checkout checkout = Checkout.builder()
                .userId(Long.valueOf(userId))
                .productCodeAndQuantity(productCodeAndQuantity)
                .build();

        HttpEntity httpEntity = new HttpEntity<>(checkout, new HttpHeaders());
        ParameterizedTypeReference<Boolean> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<Boolean> response =
                testRestTemplate.exchange(baseUrl + port + "/checkout", HttpMethod.DELETE, httpEntity, responseType);
        assertTrue(response.getStatusCode().value() == responseCode);
    }


}
