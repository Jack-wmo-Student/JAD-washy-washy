package MODEL.CLASS;

public class Service {
	private int id;
	private int category_id;
	private int status_id;
	private String name;
	private double price;
	private int duration_in_hour;
	private String description;

	public Service(int id, int category_id, int status_id, String name, double price, int duration_in_hour,
			String description) {
		this.id = id;
		this.category_id = category_id;
		this.status_id = status_id;
		this.name = name;
		this.price = price;
		this.duration_in_hour = duration_in_hour;
		this.description = description;
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
}
