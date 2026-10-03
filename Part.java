import java.math.BigDecimal;

/**
 * Spare Part Entity.
 * Represents an item in the maintenance parts inventory.
 */
public class Part {

    public String partNumber;
    public String partName;
    public String category; // e.g. "Fluids", "Brakes", "Filters"
    public BigDecimal price;
    public int stockQuantity;

    public Part(String partNumber, String partName, String category, BigDecimal price, int stockQuantity) {
        this.partNumber = partNumber;
        this.partName = partName;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }
}
