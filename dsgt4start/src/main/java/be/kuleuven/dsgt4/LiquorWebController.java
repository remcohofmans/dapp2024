package be.kuleuven.dsgt4;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
public class LiquorWebController {

    @GetMapping("/liquors/names")
    public String listLiquorNames(Model model) {
        String url = "http://localhost:8090/api/liquor-names";
        RestTemplate restTemplate = new RestTemplate();
        List<String> liquorNames = restTemplate.getForObject(url, List.class);
        model.addAttribute("liquorNames", liquorNames);
        return "liquorNames";
    }
}

