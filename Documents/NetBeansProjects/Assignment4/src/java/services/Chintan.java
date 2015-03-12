package services;

import Connect.Connect;

import java.sql.*;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author chintan
 *
 *
 */
@Path("/products")
public class Chintan {

    @GET
    @Produces("application/json")

    public Response getAll() {
        return Response.ok(getResult("select * from product")).build();

    }

    @GET
    @Path("{id}")
    public Response getOne(@PathParam("id") String id) {

        return Response.ok(getResult("select * from product where productid=?", String.valueOf(id))).build();

    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response add(JsonObject json) {

        String name = json.getString("name");
        String description = json.getString("description");
        String quantity = json.getString("quantity");
        int result = doUpdate("INSERT INTO product (name,description,quantity) VALUES ( ?, ?, ?)", name, description, quantity);

        if (result <= 0) {
            return Response.status(500).build();
        } else {
            return Response.ok(json).build();
        }
    }

    private String getResult(String query, String... parameters) {
        StringBuilder sb = new StringBuilder();
        JSONObject jObj = new JSONObject();

        try (Connection con = Connect.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement(query);
            for (int i = 1; i <= parameters.length; i++) {
                pstmt.setString(i, parameters[i - 1]);
            }
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                jObj.put("productid", rs.getInt("productid"));
                jObj.put("name", rs.getString("name"));
                jObj.put("description", rs.getString("description"));
                jObj.put("quantity", rs.getInt("quantity"));

                sb.append(jObj.toJSONString());
            }

        } catch (SQLException ex) {
            System.err.println("Error" + ex.getMessage());
        }

        return sb.toString();
    }

    @DELETE
    @Path("{id}")
    public Response deleteOne(@PathParam("id") String id) {
        int result = doUpdate("delete from product where productid=?", id);
        if (result <= 0) {
            return Response.status(500).build();
        } else {
            return Response.noContent().build();
        }

    }

    private int doUpdate(String query, String... params) {
        int numChanges = 0;
        try (Connection con = Connect.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            numChanges = pstmt.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(Chintan.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return numChanges;
    }

}
