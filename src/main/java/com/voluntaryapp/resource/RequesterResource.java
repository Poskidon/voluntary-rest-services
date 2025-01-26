package com.voluntaryapp.resource;

import com.voluntaryapp.database.DatabaseConnection;
import com.voluntaryapp.model.User;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Path("/requester")
public class RequesterResource {

    @GET
    @Path("/get/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRequester(@PathParam("email") String email) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE email = ? AND role = 'REQUESTER'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                user.setName(rs.getString("name"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setAddress(rs.getString("address"));
                user.setRole("REQUESTER");
                return Response.ok(user).build();
            }
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addRequester(User user) {
        if (user.getRole() == null || !user.getRole().equals("REQUESTER")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid role specified").build();
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO users (email, password, name, role, phone_number, address) " +
                    "VALUES (?, ?, ?, 'REQUESTER', ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getName());
            stmt.setString(4, user.getPhoneNumber());
            stmt.setString(5, user.getAddress());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                    return Response.status(Response.Status.CREATED).entity(user).build();
                }
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build();
        }
    }
}