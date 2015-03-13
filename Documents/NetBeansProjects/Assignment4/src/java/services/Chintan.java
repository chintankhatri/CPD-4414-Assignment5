package services;

import Connect.Connect;

import java.sql.*;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;


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

    private static JsonArray getResult(String query, String... parameters) {
        JsonArray json = null;
        try {
            Connection con = Connect.getConnection();
            PreparedStatement pst = con.prepareStatement(query);
            for (int i = 0; i < parameters.length; i++) {
                pst.setString(i + 1, parameters[i]);
            }
            ResultSet results = pst.executeQuery();

            JsonArrayBuilder array = Json.createArrayBuilder();
            while (results.next()) {
                array.add(Json.createObjectBuilder()
                        
                        .add("name", results.getString("name"))
                        .add("description",results.getString("description"))
                        .add("quantity", results.getString("quantity"))
                );
            }
            json = array.build();
        } catch (SQLException ex) {
            Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
        }
        return json;
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
    @PUT
    @Path("{id}")
    public Response updateOne(@PathParam("id")String id,@PathParam("name")String name,@PathParam("description")String description,@PathParam("quantity")String quantity){
    
      int result = doUpdate("update product set name=?,description=?,quantity=? where productid=?", name,description,quantity,id);
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
