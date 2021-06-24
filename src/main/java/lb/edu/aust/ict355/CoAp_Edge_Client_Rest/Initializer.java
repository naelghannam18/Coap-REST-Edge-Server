package lb.edu.aust.ict355.CoAp_Edge_Client_Rest;

import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

import javax.inject.Inject;

public class Initializer implements ApplicationEventListener {
    @Inject
    DeviceManager deviceManager;
    @Override
    public void onEvent(ApplicationEvent event) {
        if(event.getType() == ApplicationEvent.Type.INITIALIZATION_FINISHED){deviceManager.start(); }}
    @Override
    public RequestEventListener onRequest(RequestEvent requestEvent) {
        return null;
    }}
