package com.example.springsoap;

import io.winemenu.gt.webservice.Wine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class WineRestController {

    private final WineRepository WineRepository;

    @Autowired
    public WineRestController(WineRepository WineRepository) {
        this.WineRepository = WineRepository;
    }

    @GetMapping("/api/wine-names")
    public List<String> getWineNames() {
        return WineRepository.getWineCard().stream()
                .map(wine -> wine.getName() + " - $" + wine.getPrice())
                .collect(Collectors.toList());
    }
}

