package model;

public class service {
    private int id;
    private int categoryId;
    private String name;
    private double price;
    private int durationInHour;
    private String description;

    public service(int id, int categoryId, String name, double price, int durationInHour, String description) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.price = price;
        this.durationInHour = durationInHour;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getDurationInHour() {
        return durationInHour;
    }

    public String getDescription() {
        return description;
    }
}
