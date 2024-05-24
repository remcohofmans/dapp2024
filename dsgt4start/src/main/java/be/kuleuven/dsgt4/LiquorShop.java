package be.kuleuven.dsgt4;
import be.kuleuven.dsgt4.User;
import be.kuleuven.dsgt4.Liquor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

public abstract class LiquorShop {
    protected WebClient.Builder webClientBuilder;
    protected String apiKey;
    protected String liquorShopId;
    protected String location;
    //protected List<Beverage> beverages = new ArrayList<>();

    protected Map<Liquor, Integer> beverageMap = new HashMap<>();

    public Map<Liquor, Integer> getBeverages(){return this.beverageMap;};

    public String getLiquorShopId(){return this.liquorShopId;};

    public String getLocation(){return this.location;};



}
