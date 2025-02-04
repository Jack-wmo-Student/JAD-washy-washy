package MODEL.CLASS;

public class Booking {
    private int booking_id;
    private String service_name;
    private TimeSlot time_slot;
    private String booked_date; // Can use Date or LocalDate depending on your preference

    // Constructor
    public Booking() {
    	// Default constructor for Booking class
    }
    
    public Booking(int booking_id, String service_name, TimeSlot time_slot, String booked_date) {
        this.booking_id = booking_id;
        this.service_name = service_name;
        this.time_slot = time_slot;
        this.booked_date = booked_date;
    }
    
    // Getters and Setters
    public int getBookingId() {
        return booking_id;
    }

    public void setBookingId(int booking_id) {
        this.booking_id = booking_id;
    }

    public String getServiceName() {
        return service_name;
    }

    public void setServiceName(String service_name) {
        this.service_name = service_name;
    }

    public TimeSlot getTimeSlot() {
        return time_slot;
    }

    public void setTimeSlot(TimeSlot time_slot) {
        this.time_slot = time_slot;
    }

    public String getBookedDate() {
        return booked_date;
    }

    public void setBookedDate(String booked_date) {
        this.booked_date = booked_date;
    }
}
