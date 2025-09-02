package com.platform.scrapper.api;

import com.platform.scrapper.request.AddWebsiteRequest;
import com.platform.scrapper.response.WebsiteResponse;
import com.platform.scrapper.service.ScrapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ScrapperController {
    
    private final ScrapperService scrapperService;
    
    // Website Management API - Only this API is needed for scraping
    @PostMapping("/websites")
    public ResponseEntity<WebsiteResponse> addWebsite(@Valid @RequestBody AddWebsiteRequest request) {
        WebsiteResponse response = scrapperService.addWebsite(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/websites")
    public ResponseEntity<List<WebsiteResponse>> getAllWebsites() {
        List<WebsiteResponse> websites = scrapperService.getAllWebsites();
        return ResponseEntity.ok(websites);
    }
}
