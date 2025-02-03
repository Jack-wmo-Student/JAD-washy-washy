package MODEL.CLASS;

public class CartItem {
	private String booked_date;
	private TimeSlot time_slot;
	private Service service;
	
	// Constructors
	public CartItem() {
		
	}
	
	public CartItem(String booked_date, TimeSlot time_slot, Service service) {
		this.booked_date = booked_date;
		this.time_slot = time_slot;
		this.service = service;
	}
	
	// Getters
	public String getBookedDate() {
		return booked_date;
	}
	
	public TimeSlot getTimeslot() {
		return time_slot;
	}
	
	public Service getService() {
		return service;
	}
	
	// Setters
	public void setBookedDate(String booked_date) {
		this.booked_date = booked_date;
	}
	
	public void setTimeslot(TimeSlot time_slot) {
		this.time_slot = time_slot;
	}
	
	public void setService(Service service) {
		this.service = service;
	}
}

