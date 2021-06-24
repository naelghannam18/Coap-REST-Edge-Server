package lb.edu.aust.ict355.CoAp_Edge_Client_Rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

@Path("/greenhouse")
public class SensorResource {
    @Inject
    DeviceManager deviceManager;
    // Handling REST GET Requests.
    // When Received a GET Request, The Edge client will respond with a JSON payload.
    // This Payload will contain a list of states that will be outputted by the Device Issuing the Request
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<States> hello() { return deviceManager.getMoistureState(); }}
