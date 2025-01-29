package MODEL.CLASS;

public class TimeSlot {
    private int time_slot_id;
    private String time_range; // e.g., "8am-9am"

    // Constructors
    public TimeSlot() {
    	// Default constructor for empty TimeSlot object
    }
    
    public TimeSlot(int time_slot_id, String time_range) {
        this.time_slot_id = time_slot_id;
        this.time_range = time_range;
    }

    // Getters and Setters
    public int getTimeSlotId() {
        return time_slot_id;
    }

    public void setTimeSlotId(int time_slot_id) {
        this.time_slot_id = time_slot_id;
    }

    public String getTimeRange() {
        return time_range;
    }

    public void setTimeRange(String time_range) {
        this.time_range = time_range;
    }
}