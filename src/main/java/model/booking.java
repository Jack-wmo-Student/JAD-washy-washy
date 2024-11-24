package model;

public class booking {
    private int bookingId;
    private String serviceName;
    private timeslot timeslot;
    private String bookedDate; // Can use Date or LocalDate depending on your preference

    // Constructor
    public booking() {
    	// Default constructor for booking class
    }
    
    public booking(int bookingId, String serviceName, timeslot timeslot, String bookedDate) {
        this.bookingId = bookingId;
        this.serviceName = serviceName;
        this.timeslot = timeslot;
        this.bookedDate = bookedDate;
    }
    
    // Getters and Setters
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public timeslot getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(timeslot timeslot) {
        this.timeslot = timeslot;
    }

    public String getBookedDate() {
        return bookedDate;
    }

    public void setBookedDate(String bookedDate) {
        this.bookedDate = bookedDate;
    }
}
