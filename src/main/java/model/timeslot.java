package model;

public class timeslot {
    private int timeSlotId;
    private String timeRange; // e.g., "8am-9am"

    // Constructors
    public timeslot() {
    	// Default constructor for empty timeslot object
    }
    
    public timeslot(int timeSlotId, String timeRange) {
        this.timeSlotId = timeSlotId;
        this.timeRange = timeRange;
    }

    // Getters and Setters
    public int getTimeSlotId() {
        return timeSlotId;
    }

    public void setTimeSlotId(int timeSlotId) {
        this.timeSlotId = timeSlotId;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }
}