package com.voluntaryapp.resource;

import com.voluntaryapp.database.DatabaseConnection;
import com.voluntaryapp.model.User;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.*;

@Path("/auth")
public class AuthResource {

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(
            @FormParam("email") String email,
            @FormParam("password") String password
    ) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                user.setName(rs.getString("name"));
                user.setRole(rs.getString("role"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setAddress(rs.getString("address"));
                return Response.ok(user).build();
            }
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid credentials").build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(User user) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String checkQuery = "SELECT id FROM users WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, user.getEmail());
            if (checkStmt.executeQuery().next()) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("Email already exists").build();
            }

            String query = "INSERT INTO users (email, password, name, role, phone_number, address) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getName());
            stmt.setString(4, user.getRole());
            stmt.setString(5, user.getPhoneNumber());
            stmt.setString(6, user.getAddress());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                    return Response.status(Response.Status.CREATED)
                            .entity(user).build();
                }
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build();
        }
    }
}