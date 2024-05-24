package be.kuleuven.dsgt4;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Liquor implements Comparable<Liquor> {
    private String liquorShop;
    private UUID beverageId;
    private int price;
    private String beverageName;

    public Liquor() {}

    public Liquor(String liquorShop, UUID beverageId, int price, String beverageName) {
        this.liquorShop = liquorShop;
        this.beverageId = beverageId;
        this.price = price;
        this.beverageName = beverageName;
    }

    public String getLiquorShop() {
        return this.liquorShop;
    }

    public int getPrice() {
        return this.price;
    }

    public UUID getBeverageId() {
        return this.beverageId;
    }

    public String getBeverageName() {
        return this.beverageName;
    }

    public Map<String, String> getAsMap() {
        Map<String, String> map = new HashMap<>();
        map.put("liquorShop", liquorShop);
        map.put("beverageId", beverageId.toString());
        map.put("price", Integer.toString(price));
        map.put("beverageName", beverageName);
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Liquor)) {
            return false;
        }
        var other = (Liquor) o;
        return (this.beverageName.equals(other.beverageName)
                && this.beverageId.equals(other.beverageId)
                && (this.price == other.price)
                && this.liquorShop.equals(other.liquorShop));
    }

    @Override
    public int hashCode() {
        return Objects.hash(liquorShop, beverageId, price, beverageName);
    }

    @Override
    public int compareTo(Liquor o) {
        return Integer.compare(this.price, o.price);
    }

    @Override
    public String toString() {
        return "Beverage{" +
                "liquorShop='" + liquorShop + '\'' +
                ", beverageId=" + beverageId +
                ", price=" + price +
                ", beverageName='" + beverageName + '\'' +
                '}';
    }
}
