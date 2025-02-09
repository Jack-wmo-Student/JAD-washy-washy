package MODEL.CLASS;

import java.sql.Date;
import java.sql.Timestamp;

public class TransactionHistory {
    private int bookingId;
    private Date bookedDate;
    private String serviceName;
    private double price;
    private String paymentMethod;
    private String paymentStatus;
    private String bookingStatus;
    private Timestamp createdAt;

    // Default constructor
    public TransactionHistory() {
    }

    // Getters
    public int getBookingId() {
        return bookingId;
    }

    public Date getBookedDate() {
        return bookedDate;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getPrice() {
        return price;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public void setBookedDate(Date bookedDate) {
        this.bookedDate = bookedDate;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // Optional: Override toString() method for debugging
    @Override
    public String toString() {
        return "TransactionHistory{" +
                "bookingId=" + bookingId +
                ", bookedDate=" + bookedDate +
                ", serviceName='" + serviceName + '\'' +
                ", price=" + price +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", bookingStatus='" + bookingStatus + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}