package lb.edu.aust.ict355.CoAp_Edge_Client_Rest;
import org.glassfish.jersey.server.ResourceConfig;
import javax.ws.rs.ApplicationPath;

// Main Application Path
@ApplicationPath("/api")
public class MyApplication extends ResourceConfig {
    public MyApplication() {
        packages(true, "lb.edu.aust.ict355");
        register(Initializer.class);
        register(new DependencyBinder()); }}