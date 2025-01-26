package com.voluntaryapp.resource;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.voluntaryapp.database.DatabaseConnection;
import com.voluntaryapp.model.User;
import com.voluntaryapp.model.Mission;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


import com.voluntaryapp.database.DatabaseConnection;
import com.voluntaryapp.model.User;
import com.voluntaryapp.model.Mission;
import jakarta.ws.rs.*;
        import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.*;
        import java.util.ArrayList;
import java.util.List;

@Path("/volunteer")
public class VolunteerResource {

    @GET
    @Path("/get/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVolunteer(@PathParam("email") String email) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE email = ? AND role = 'VOLUNTEER'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User volunteer = new User();
                volunteer.setId(rs.getInt("id"));
                volunteer.setEmail(rs.getString("email"));
                volunteer.setName(rs.getString("name"));
                volunteer.setPhoneNumber(rs.getString("phone_number"));
                volunteer.setAddress(rs.getString("address"));
                volunteer.setRole("VOLUNTEER");
                return Response.ok(volunteer).build();
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
    public Response addVolunteer(User user) {
        if (user.getRole() == null || !user.getRole().equals("VOLUNTEER")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid role specified").build();
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO users (email, password, name, role, phone_number, address) " +
                    "VALUES (?, ?, ?, 'VOLUNTEER', ?, ?)";
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

    @GET
    @Path("/available-missions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAvailableMissions() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM missions WHERE volunteer_id IS NULL AND status = 'PENDING'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            List<Mission> missions = new ArrayList<>();
            while (rs.next()) {
                Mission mission = new Mission();
                mission.setId(rs.getInt("id"));
                mission.setRequesterId(rs.getInt("requester_id"));
                mission.setTitle(rs.getString("title"));
                mission.setDescription(rs.getString("description"));
                mission.setStatus(rs.getString("status"));
                missions.add(mission);
            }
            return Response.ok(missions).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/accept-mission/{missionId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response acceptMission(@PathParam("missionId") int missionId,
                                  @QueryParam("volunteerId") int volunteerId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE missions SET volunteer_id = ?, status = 'IN_PROGRESS' " +
                    "WHERE id = ? AND volunteer_id IS NULL AND status = 'PENDING'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, volunteerId);
            stmt.setInt(2, missionId);

            int updated = stmt.executeUpdate();
            if (updated > 0) {
                return Response.ok("Mission accepted successfully").build();
            }
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Mission not available").build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/complete-mission/{missionId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response completeMission(@PathParam("missionId") int missionId,
                                    @QueryParam("volunteerId") int volunteerId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE missions SET status = 'COMPLETED' " +
                    "WHERE id = ? AND volunteer_id = ? AND status = 'IN_PROGRESS'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, missionId);
            stmt.setInt(2, volunteerId);

            int updated = stmt.executeUpdate();
            if (updated > 0) {
                return Response.ok("Mission completed successfully").build();
            }
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Unable to complete mission").build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/my-missions/{volunteerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVolunteerMissions(@PathParam("volunteerId") int volunteerId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM missions WHERE volunteer_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, volunteerId);
            ResultSet rs = stmt.executeQuery();

            List<Mission> missions = new ArrayList<>();
            while (rs.next()) {
                Mission mission = new Mission();
                mission.setId(rs.getInt("id"));
                mission.setRequesterId(rs.getInt("requester_id"));
                mission.setVolunteerId(rs.getInt("volunteer_id"));
                mission.setTitle(rs.getString("title"));
                mission.setDescription(rs.getString("description"));
                mission.setStatus(rs.getString("status"));
                mission.setCreatedAt(rs.getString("created_at"));
                missions.add(mission);
            }
            return Response.ok(missions).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build();
        }
    }
}