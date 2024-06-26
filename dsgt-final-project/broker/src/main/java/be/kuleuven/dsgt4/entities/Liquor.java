package be.kuleuven.dsgt4;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Liquor implements Comparable<Liquor> {
    private UUID liquorId;
    private int price;
    private String liquorName;

    public Liquor() {}

    public Liquor(UUID liquorId, int price, String liquorName) {

        this.liquorId = liquorId;
        this.price = price;
        this.liquorName = liquorName;
    }


    public int getPrice() {
        return this.price;
    }

    public UUID getLiquorId() {
        return this.liquorId;
    }

    public String getLiquorName() {
        return this.liquorName;
    }

    public Map<String, String> getAsMap() {
        Map<String, String> map = new HashMap<>();
        map.put("liquorId", liquorId.toString());
        map.put("price", Integer.toString(price));
        map.put("liquorName", liquorName);
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Liquor)) {
            return false;
        }
        var other = (Liquor) o;
        return (this.liquorName.equals(other.liquorName)
                && this.liquorId.equals(other.liquorId)
                && (this.price == other.price));
    }

    @Override
    public int compareTo(Liquor o) {
        return Integer.compare(this.price, o.price);
    }

    @Override
    public String toString() {
        return "Beverage{" +
                ", liquorId=" + liquorId +
                ", price=" + price +
                ", liquorName='" + liquorName + '\'' +
                '}';
    }
}
