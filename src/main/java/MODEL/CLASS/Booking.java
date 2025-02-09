package MODEL.CLASS;

import java.time.LocalDateTime;

public class Booking {
	private int booking_id;
	private String service_name;
	private TimeSlot time_slot;
	private String booked_date; // Can use Date or LocalDate depending on your preference

	public Booking(int booking_id, String service_name, TimeSlot time_slot, String booked_date) {
		super();
		this.booking_id = booking_id;
		this.time_slot = time_slot;
		this.booked_date = booked_date;
		this.service_name = service_name;
	}

	public int getBooking_id() {
		return booking_id;
	}

	public void setBooking_id(int booking_id) {
		this.booking_id = booking_id;
	}

	public String getService_name() {
		return service_name;
	}

	public void setService_name(String service_name) {
		this.service_name = service_name;
	}

	public TimeSlot getTime_slot() {
		return time_slot;
	}

	public void setTime_slot(TimeSlot time_slot) {
		this.time_slot = time_slot;
	}

	public String getBooked_date() {
		return booked_date;
	}

	public void setBooked_date(String booked_date) {
		this.booked_date = booked_date;
	}

	// Constructor
	public Booking() {
		// Default constructor for Booking class
	}
}
