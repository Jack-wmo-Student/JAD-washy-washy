package MODEL;

/**
 * Custom exception class for handling DAO (Data Access Object) related exceptions.
 * This exception is thrown when database operations fail or when data validation fails.
 */
public class DAOException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Error codes for specific DAO exceptions
     */
    public enum ErrorCode {
        NOT_FOUND(1001, "Record not found"),
        DUPLICATE_ENTRY(1002, "Duplicate record"),
        VALIDATION_ERROR(1003, "Data validation error"),
        DATABASE_ERROR(1004, "Database error"),
        CONSTRAINT_VIOLATION(1005, "Constraint violation"),
        UNAUTHORIZED_OPERATION(1006, "Unauthorized operation");

        private final int code;
        private final String defaultMessage;

        ErrorCode(int code, String defaultMessage) {
            this.code = code;
            this.defaultMessage = defaultMessage;
        }

        public int getCode() {
            return code;
        }

        public String getDefaultMessage() {
            return defaultMessage;
        }
    }

    private final ErrorCode errorCode;

    /**
     * Constructs a DAOException with a message.
     * 
     * @param message the detail message
     */
    public DAOException(String message) {
        super(message);
        this.errorCode = ErrorCode.DATABASE_ERROR;
    }

    /**
     * Constructs a DAOException with a message and a cause.
     * 
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public DAOException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.DATABASE_ERROR;
    }

    /**
     * Constructs a DAOException with an error code and custom message.
     * 
     * @param errorCode the specific error code
     * @param message custom error message
     */
    public DAOException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructs a DAOException with an error code, message, and cause.
     * 
     * @param errorCode the specific error code
     * @param message custom error message
     * @param cause the cause of the exception
     */
    public DAOException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Gets the error code associated with this exception.
     * 
     * @return the error code
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        if (errorCode != null) {
            return String.format("[%d] %s", errorCode.getCode(), super.getMessage());
        }
        return super.getMessage();
    }

    /**
     * Creates a DAOException for when a record is not found.
     * 
     * @param entityName the name of the entity that wasn't found
     * @param identifier the identifier that was searched for
     * @return a new DAOException
     */
    public static DAOException notFound(String entityName, Object identifier) {
        String message = String.format("%s not found with identifier: %s", entityName, identifier);
        return new DAOException(ErrorCode.NOT_FOUND, message);
    }

    /**
     * Creates a DAOException for duplicate entry errors.
     * 
     * @param entityName the name of the entity that caused the duplicate
     * @param field the field that caused the duplicate
     * @param value the value that caused the duplicate
     * @return a new DAOException
     */
    public static DAOException duplicateEntry(String entityName, String field, Object value) {
        String message = String.format("Duplicate %s with %s: %s", entityName, field, value);
        return new DAOException(ErrorCode.DUPLICATE_ENTRY, message);
    }

    /**
     * Creates a DAOException for unauthorized operations.
     * 
     * @param operation the operation that was attempted
     * @return a new DAOException
     */
    public static DAOException unauthorized(String operation) {
        String message = String.format("Unauthorized to perform operation: %s", operation);
        return new DAOException(ErrorCode.UNAUTHORIZED_OPERATION, message);
    }
}