/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package server;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author angal
 */
public class newWServer extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
   protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    response.setContentType("application/json;charset=UTF-8");
    try (PrintWriter out = response.getWriter()) {
        JSONObject responseJson = new JSONObject();
        
        // Read fire, drone, and fire truck positions from the database
        JSONArray fireArray = readFirePositionsFromDatabase();
        JSONArray droneArray = readDronePositionsFromDatabase();
        JSONArray fireTruckArray = readFireTruckPositionsFromDatabase();
        
        // Add the fetched data to the JSON response
        responseJson.put("fire", fireArray);
        responseJson.put("drone", droneArray);
        responseJson.put("fireTruck", fireTruckArray);
        
        // Send the JSON response to the client
        out.print(responseJson.toString());
    }
}

private JSONArray readFirePositionsFromDatabase() {
    JSONArray fireArray = new JSONArray();
    
    try {
        // Connect to the database
        Connection conn = getConnection();
        
        // Execute the query to fetch fire positions
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM fire");
        ResultSet rs = stmt.executeQuery();
        
        // Iterate over the result set and add fire positions to the JSON array
        while (rs.next()) {
            JSONObject fireObj = new JSONObject();
            fireObj.put("id", rs.getInt("id"));
            fireObj.put("isActive", rs.getInt("isActive"));
            fireObj.put("intensity", rs.getInt("intensity"));
            fireObj.put("burningAreaRadius", rs.getBigDecimal("burningAreaRadius"));
            fireObj.put("xpos", rs.getInt("xpos"));
            fireObj.put("ypos", rs.getInt("ypos"));
            
            fireArray.put(fireObj);
        }
        
        // Close the database resources
        rs.close();
        stmt.close();
        conn.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    return fireArray;
}

// Similar methods can be implemented for reading drone and fire truck positions from the database


    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    String truckName = request.getParameter("truckName");
    int designatedFireId = Integer.parseInt(request.getParameter("designatedFireId"));
    
    // Insert fire truck position into the database
    insertFireTruckPosition(truckName, designatedFireId);
    
    // Send a success response to the client
    response.setContentType("text/plain");
    response.getWriter().write("Fire truck position inserted successfully");
}

private void insertFireTruckPosition(String truckName, int designatedFireId) {
    try {
        // Connect to the database
        Connection conn = getConnection();
        
        // Execute the query to insert fire truck position
        PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO firetrucks (name, designatedFireId) VALUES (?, ?)");
        stmt.setString(1, truckName);
        stmt.setInt(2, designatedFireId);
        stmt.executeUpdate();
        
        // Close the database resources
        stmt.close();
        conn.close();
        } catch (SQLException e) {
        e.printStackTrace();
    }
}

// Utility method to establish a database connection
private Connection getConnection() throws SQLException {
    String DB_URL = "jdbc:mysql://localhost:3307/ibdms_server";
    String USER = "root";
    String PASS = "AMRodgy@1243";
    
    return DriverManager.getConnection(DB_URL, USER, PASS);
}

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private JSONArray readDronePositionsFromDatabase() {
    JSONArray droneArray = new JSONArray();

    try {
        // Connect to the database
        Connection conn = getConnection();

        // Execute the query to fetch drone positions
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM drone");
        ResultSet rs = stmt.executeQuery();

        // Iterate over the result set and add drone positions to the JSON array
        while (rs.next()) {
            JSONObject droneObj = new JSONObject();
            droneObj.put("id", rs.getInt("id"));
            droneObj.put("name", rs.getString("name"));
            droneObj.put("xpos", rs.getInt("xpos"));
            droneObj.put("ypos", rs.getInt("ypos"));

            droneArray.put(droneObj);
        }

        // Close the database resources
        rs.close();
        stmt.close();
        conn.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return droneArray;
}

private JSONArray readFireTruckPositionsFromDatabase() {
    JSONArray fireTruckArray = new JSONArray();

    try {
        // Execute the query to fetch fire truck positions
        try ( // Connect to the database
                Connection conn = getConnection(); // Execute the query to fetch fire truck positions
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM firetrucks")) {
            ResultSet rs = stmt.executeQuery();
            
            // Iterate over the result set and add fire truck positions to the JSON array
            while (rs.next()) {
                JSONObject fireTruckObj = new JSONObject();
                fireTruckObj.put("id", rs.getInt("id"));
                fireTruckObj.put("name", rs.getString("name"));
                fireTruckObj.put("designatedFireId", rs.getInt("designatedFireId"));
                
                fireTruckArray.put(fireTruckObj);
            }
            
            // Close the database resources
            rs.close();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return fireTruckArray;
}

}
