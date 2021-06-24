package lb.edu.aust.ict355.CoAp_Edge_Client_Rest;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import javax.inject.Singleton;

public class DependencyBinder extends AbstractBinder {
    @Override
    protected void configure() {
        // Binding the DeviceManager Class to the SingletonClass
        // The Will allow the initialization of the Device Manager Only Once
        // And we will be able to inject it in other classes
        bind(DeviceManager.class).to(DeviceManager.class).in(Singleton.class); }}



