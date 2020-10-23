package com.itel.healthadapter.api;

import healthAdapter.itelData.ImportPayload;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("healthadapter")
public interface HealthAdapterAPI {

    @PostMapping(path = "enrollment", consumes = "application/json", produces = "application/json")
    StatusReference enrollment(@RequestBody EnrollmentPayload payload);

    @PostMapping(path ="importExisting", consumes = "application/json", produces = "application/json")
    //StatusReference _import(@RequestParam("resourceid") String resourceid, @RequestParam("identifier") String identifier);
    StatusReference _importExisting(@RequestBody String importpayloadJson);
}