package be.kuleuven.dsgt4;
import be.kuleuven.dsgt4.User;
import be.kuleuven.dsgt4.Liquor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

public class LiquorShop {
    protected WebClient.Builder webClientBuilder;
    protected String apiKey;
    protected String liquorShopId;
    protected String location;
    protected List<Liquor> liquors = new ArrayList<>();
    protected Map<Liquor, Integer> availableLiquors = new HashMap<>();


    public String getLiquorShopId(){return this.liquorShopId;}
    public String getLocation(){return this.location;}
    public List<Liquor> getLiquors(){return this.liquors;}

    public Map<Liquor, Integer> getAvailableLiquors() {
        return availableLiquors;
    }

    public void deleteLiquorFromStore(Liquor liquor){
      for (int i = 0; i < liquors.size(); i++)
      {
          if (liquor.getLiquorId().equals(liquors.get(i).getLiquorId())){
              liquors.remove(i);
          }
      }
    }

    public boolean decrementAmountOfLiquor(Liquor liquor) {
        if (availableLiquors.containsKey(liquor)) {
            int currentAmount = availableLiquors.get(liquor);
            if (currentAmount > 0) {
                availableLiquors.put(liquor, currentAmount - 1);
                return true;
            } else {
                System.out.println("The amount of " + liquor.getLiquorName() + " is already zero.");
                return false;
            }
        } else {
            System.out.println("Liquor not found.");
            return false;
        }
    }
}
