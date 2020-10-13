package com.itel.healthadapter.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("healthadapter")
public interface HealthAdapterAPI {

    @PostMapping(path = "enrollment", consumes = "application/json", produces = "application/json")
    StatusReference enrollment(@RequestBody EnrollmentPayload payload);

    @PostMapping(path ="import", consumes = "application/json", produces = "application/json")
    StatusReference _import(@RequestParam("identifier") String identifier);
}