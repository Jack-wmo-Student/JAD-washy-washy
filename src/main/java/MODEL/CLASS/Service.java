package MODEL.CLASS;

public class Service {
    private int id;
    private int category_id;
    private int status_id;
    private String name;
    private double price;
    private int duration_in_hour;
    private String description;
    private String image_url; // New field for image URL

    public Service(int id, int category_id, int status_id, String name, double price, int duration_in_hour,
                   String description, String image_url) {
        this.id = id;
        this.category_id = category_id;
        this.status_id = status_id;
        this.name = name;
        this.price = price;
        this.duration_in_hour = duration_in_hour;
        this.description = description;
        this.image_url = image_url; // Assigning the new field
    }

    public int getId() {
        return id;
    }

    public int getCategoryId() {
        return category_id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getDurationInHour() {
        return duration_in_hour;
    }

    public String getDescription() {
        return description;
    }

    public int getStatusId() {
        return status_id;
    }

    public void setStatusId(int status_id) {
        this.status_id = status_id;
    }

    public String getImageUrl() {  // Getter for image_url
        return image_url;
    }

    public void setImageUrl(String image_url) {  // Setter for image_url
        this.image_url = image_url;
    }
}