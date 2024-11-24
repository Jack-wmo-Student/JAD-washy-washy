package model;

public class cartItem {
	private String bookedDate;
	private timeslot timeslot;
	private service service;
	
	// Constructors
	public cartItem() {
		
	}
	
	public cartItem(String bookedDate, timeslot timeslot, service service) {
		this.bookedDate = bookedDate;
		this.timeslot = timeslot;
		this.service = service;
	}
	
	// Getters
	public String getBookedDate() {
		return bookedDate;
	}
	
	public timeslot getTimeslot() {
		return timeslot;
	}
	
	public service getService() {
		return service;
	}
	
	// Setters
	public void setBookedDate(String bookedDate) {
		this.bookedDate = bookedDate;
	}
	
	public void setTimeslot(timeslot timeslot) {
		this.timeslot = timeslot;
	}
	
	public void setService(service service) {
		this.service = service;
	}
}

