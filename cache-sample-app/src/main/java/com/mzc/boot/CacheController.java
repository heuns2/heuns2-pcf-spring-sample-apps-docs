package com.mzc.boot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CacheController {
	
    
    @GetMapping("/caches")
    public String getAllRegionNames() {
        return "ok";
    }

    
}
