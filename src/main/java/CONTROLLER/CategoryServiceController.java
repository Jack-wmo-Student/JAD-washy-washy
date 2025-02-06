
package CONTROLLER;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import MODEL.CLASS.Category;
import MODEL.CLASS.Service;
import MODEL.DAO.CategoryServiceDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class CategoryServiceController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public CategoryServiceController() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();

		// Check if categories and services are already in session
		@SuppressWarnings("unchecked")
		Map<Category, List<Service>> categoryServiceMap = (Map<Category, List<Service>>) session
				.getAttribute("categoryServiceMap");

		if (categoryServiceMap == null) {
			// Fetch from model if not already in session
			try {
				categoryServiceMap = CategoryServiceDAO.fetchCategoriesAndServices();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			session.setAttribute("categoryServiceMap", categoryServiceMap);
		}

		// Forward to homePage.jsp to display the data
		request.getRequestDispatcher("/pages/homePage.jsp").forward(request, response);
	}
}
