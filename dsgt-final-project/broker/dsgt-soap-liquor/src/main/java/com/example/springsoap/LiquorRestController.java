import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*")
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

    @CrossOrigin(origins = "http://localhost:8090")
    @GetMapping("/api/liquor-info")
    public List<Liquor> getLiquorInfo() {
        return liquorRepository.getLiquorCard();
    }
}
