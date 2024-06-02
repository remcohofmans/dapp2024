package com.example.springsoap;

import io.liquormenu.gt.webservice.Liquor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LiquorRestController {

    private final LiquorRepository liquorRepository;

    @Autowired
    public LiquorRestController(LiquorRepository liquorRepository) {
        this.liquorRepository = liquorRepository;
    }

    @GetMapping("/api/liquor-names")
    public List<String> getLiquorNames() {
        return liquorRepository.getLiquorCard().stream()
                .map(liquor -> liquor.getBrand() + " - $" + liquor.getPrice())
                .collect(Collectors.toList());
    }
}

