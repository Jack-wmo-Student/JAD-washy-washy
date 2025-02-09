package MODEL.CLASS;

public class ServiceStatistics {
    private int serviceId;
    private String serviceName;
    private double totalRevenue;
    private int totalBookings;
    private double profitMargin;
    private String timeframe;
    // New fields for trends
    private double previousRevenue;
    private int previousBookings;
    private double revenueGrowth;
    private double bookingGrowth;

    // Original constructor
    public ServiceStatistics(int serviceId, String serviceName, double totalRevenue, 
                           int totalBookings, double profitMargin, String timeframe) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.totalRevenue = totalRevenue;
        this.totalBookings = totalBookings;
        this.profitMargin = profitMargin;
        this.timeframe = timeframe;
    }

    // Original getters and setters
    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public int getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(int totalBookings) {
        this.totalBookings = totalBookings;
    }

    public double getProfitMargin() {
        return profitMargin;
    }

    public void setProfitMargin(double profitMargin) {
        this.profitMargin = profitMargin;
    }

    public String getTimeframe() {
        return timeframe;
    }

    public void setTimeframe(String timeframe) {
        this.timeframe = timeframe;
    }

    // New getters and setters for trend fields
    public double getPreviousRevenue() {
        return previousRevenue;
    }

    public void setPreviousRevenue(double previousRevenue) {
        this.previousRevenue = previousRevenue;
    }

    public int getPreviousBookings() {
        return previousBookings;
    }

    public void setPreviousBookings(int previousBookings) {
        this.previousBookings = previousBookings;
    }

    public double getRevenueGrowth() {
        return revenueGrowth;
    }

    public void setRevenueGrowth(double revenueGrowth) {
        this.revenueGrowth = revenueGrowth;
    }

    public double getBookingGrowth() {
        return bookingGrowth;
    }

    public void setBookingGrowth(double bookingGrowth) {
        this.bookingGrowth = bookingGrowth;
    }
}