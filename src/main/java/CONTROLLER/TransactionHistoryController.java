package CONTROLLER;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import MODEL.CLASS.TransactionHistory;
import MODEL.CLASS.User;
import MODEL.DAO.TransactionHistoryDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import DBACCESS.DBConnection;


public class TransactionHistoryController extends HttpServlet {
    private TransactionHistoryDAO transactionDAO = new TransactionHistoryDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/pages/login.jsp");
            return;
        }
        
        try {
            List<TransactionHistory> transactions = 
                transactionDAO.getUserTransactions(currentUser.getUserId());
            request.setAttribute("transactions", transactions);
            request.getRequestDispatcher("/pages/transactionHistory.jsp")
                  .forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + 
                "/pages/error.jsp?message=Failed to load transaction history");
        }
    }
}