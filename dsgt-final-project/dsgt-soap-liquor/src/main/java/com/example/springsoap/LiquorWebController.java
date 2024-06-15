package com.example.springsoap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LiquorWebController {

    private final LiquorRepository liquorRepository;

    @Autowired
    public LiquorWebController(LiquorRepository liquorRepository) {
        this.liquorRepository = liquorRepository;
    }

    @GetMapping("/")
    public String listLiquors(Model model) {
        model.addAttribute("liquors", liquorRepository.getLiquorCard());
        return "liquors";
    }
}
