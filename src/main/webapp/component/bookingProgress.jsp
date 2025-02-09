<!-- bookingProgress.jsp -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Booking Progress</title>
    <style>
        .progress-container {
            max-width: 800px;
            margin: 40px auto;
            padding: 20px;
        }
        
        .progress-tracker {
            position: relative;
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            padding: 20px 0;
        }
        
        /* Progress line */
        .progress-tracker::before {
            content: '';
            position: absolute;
            top: 35px;
            left: 0;
            width: 100%;
            height: 4px;
            background-color: #e0e0e0;
            z-index: 1;
        }
        
        /* Completed progress line */
        .progress-tracker::after {
            content: '';
            position: absolute;
            top: 35px;
            left: 0;
            width: var(--progress-width);
            height: 4px;
            background-color: #4CAF50;
            transition: width 0.3s ease;
            z-index: 2;
        }
        
        .progress-step {
            position: relative;
            z-index: 3;
            width: 70px;
            text-align: center;
        }
        
        .step-icon {
            width: 30px;
            height: 30px;
            border-radius: 50%;
            background-color: #fff;
            border: 3px solid #e0e0e0;
            margin: 0 auto 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: all 0.3s ease;
        }
        
        .step-icon.completed {
            background-color: #4CAF50;
            border-color: #4CAF50;
        }
        
        .step-icon.current {
            border-color: #4CAF50;
            border-width: 4px;
        }
        
        .step-label {
            font-size: 12px;
            color: #666;
            margin-top: 5px;
        }
        
        .step-icon.completed + .step-label {
            color: #4CAF50;
        }
        
        /* Cancelled state */
        .cancelled .progress-tracker::after {
            background-color: #f44336;
        }
        
        .cancelled .step-icon.completed {
            background-color: #f44336;
            border-color: #f44336;
        }
    </style>
</head>
<body>
    <div class="progress-container ${bookingStatus eq 'Cancelled' ? 'cancelled' : ''}">
        <h2>Booking Progress</h2>
        
        <%
        String currentStatus = (String)request.getAttribute("bookingStatus");
        String[] stages = {"Awaiting Service", "Cleaning in Progress", "Service Completed", "Pending Payment", "Transaction Completed"};
        int currentStageIndex = -1;
        
        // Find current stage index
        for(int i = 0; i < stages.length; i++) {
            if(stages[i].equals(currentStatus)) {
                currentStageIndex = i;
                break;
            }
        }
        
        // Calculate progress width for CSS variable
        double progressWidth = currentStageIndex == -1 ? 0 : 
            (currentStageIndex / (double)(stages.length - 1)) * 100;
        %>
        
        <div class="progress-tracker" style="--progress-width: <%= progressWidth %>%">
            <% for(int i = 0; i < stages.length; i++) { %>
                <div class="progress-step">
                    <div class="step-icon <%= i <= currentStageIndex ? "completed" : "" %> 
                                        <%= i == currentStageIndex ? "current" : "" %>">
                        <% if(i <= currentStageIndex) { %>
                            &#10003; <!-- Checkmark -->
                        <% } else { %>
                            <%= i + 1 %>
                        <% } %>
                    </div>
                    <div class="step-label"><%= stages[i] %></div>
                </div>
            <% } %>
        </div>
        
        <% if("Cancelled".equals(currentStatus)) { %>
            <div class="cancelled-message" style="color: #f44336; text-align: center; margin-top: 20px;">
                <strong>This booking has been cancelled</strong>
            </div>
        <% } %>
    </div>
    
    <!-- Optional: Add auto-refresh for status updates -->
    <script>
        // Refresh the page every 30 seconds to check for status updates
        setTimeout(function() {
            window.location.reload();
        }, 30000);
    </script>
</body>
</html>