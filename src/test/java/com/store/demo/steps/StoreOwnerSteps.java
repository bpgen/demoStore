package com.store.demo.steps;

import com.store.demo.model.Product;
import com.store.demo.repository.ProductRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StoreOwnerSteps extends CucumberTest {
    @Autowired
    private ProductRepository productRepository;

    @When("store owner creates new products with {string},{string},{string},{string},{string},{string},{string} with response code {int}")
    public void storeOwnerCreatesNewProductsWith(String productCode, String desc, String price, String currency, String quantity, String discountDeal, String bundleDeal, int responseCode) {
        Product product = Product.builder()
                .productCode(productCode)
                .desc(desc)
                .price(new BigDecimal(price))
                .currency(currency)
                .quantity(Long.valueOf(quantity))
                .discountDeal(discountDeal)
                .bundleDeal(bundleDeal)
                .build();

        HttpEntity httpEntity = new HttpEntity<>(product, new HttpHeaders());
        ParameterizedTypeReference<Boolean> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<Boolean> response =
                testRestTemplate.exchange(baseUrl + port + "/products", HttpMethod.POST, httpEntity, responseType);
        assertTrue(response.getStatusCode() == HttpStatus.CREATED);
        assertTrue(response.getStatusCode().value() == responseCode);

    }

    @Then("store owner retrieves product code {string}")
    public void storeOwnerRetrievesProductCode(String productCode) {
        HttpEntity httpEntity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<Product> response =
                testRestTemplate.exchange(baseUrl + port + "/products/" + productCode, HttpMethod.GET, httpEntity, Product.class);
        assertTrue(response.getBody().getProductCode().equals(productCode));
    }

    @And("store owner update product's price {string} or desc {string} for code {string}")
    public void storeOwnerUpdateProductSPriceUpdatePriceForCode(String price, String desc, String productCode) {
        Product product = Product.builder()
                .productCode(productCode)
                .price(new BigDecimal(price))
                .desc(desc)
                .build();

        HttpEntity httpEntity = new HttpEntity<>(product, new HttpHeaders());
        ParameterizedTypeReference<Boolean> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<Boolean> response =
                testRestTemplate.exchange(baseUrl + port + "/products", HttpMethod.PUT, httpEntity, responseType);
        assertTrue(response.getBody() == Boolean.TRUE);
    }

    @And("store owner delete product with {string} and current product listing size is {int}")
    public void storeOwnerDeleteAllProductsAndCurrentProductListingSizeIs(String productCode, int size) {
        HttpEntity httpEntity = new HttpEntity<>(new HttpHeaders());
        ParameterizedTypeReference<Boolean> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<Boolean> response =
                testRestTemplate.exchange(baseUrl + port + "/products/" + productCode, HttpMethod.DELETE, httpEntity, responseType);
        assertTrue(response.getBody() == Boolean.TRUE);
        assertTrue(productRepository.findAll().size() == size);
    }

}
