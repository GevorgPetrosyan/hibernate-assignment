package com.egs.hibernate.rest;

import com.egs.hibernate.response.ResponseCountry;
import com.egs.hibernate.service.CountryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/country/")
@RequiredArgsConstructor
@Tag(name = "Country Resource", description = "The Country API with documentation annotations")
public class CountryResource {
    private final CountryService countryServiceImpl;

    @PostMapping("init")

    @Operation(summary = "Initialize countries")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Countries initialization is successfully done")})
    public void initiateCountries() {
        countryServiceImpl.storeAllCountries();
    }

    @GetMapping("findCountries")
    @Operation(summary = "Find countries with 10000 and more users")
    @ApiResponse(responseCode = "200", description = "Countries successfully found")
    public ResponseEntity<List<ResponseCountry>> findCountries() {
        List<ResponseCountry> responseCountries = countryServiceImpl.getCountryByUserCount();
        return ResponseEntity.status(HttpStatus.OK).body(responseCountries);

    }
}
