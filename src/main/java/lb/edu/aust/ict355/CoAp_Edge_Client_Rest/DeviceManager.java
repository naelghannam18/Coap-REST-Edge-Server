package lb.edu.aust.ict355.CoAp_Edge_Client_Rest;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import sun.rmi.runtime.Log;

import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// Creating the DeviceManager Class
// This Class will observe for, and handle Changes in Sensor states.
@Singleton
public class DeviceManager {
    // List of device that will contain the plant racks that we want to listen on
    private final List<Device> devices;
    // Creating constructor and adding the devices that we want to listen on
    public DeviceManager(){
        this.devices = Arrays.asList(new Device("192.168.1.110", 5683),
                                     new Device("192.168.1.110",5684),
                                     new Device("192.168.1.110",5685)); }
    // Start method that initiates the device manager to observe on the devices
    public void start(){
        observeForChanges(this.devices);
    }
    // Method that iterates over the list of devices.
    // For Every Device, will be created a COAP Client and will be observing on the soil moisture sensor.
    public void observeForChanges(List<Device> devices){
        // Iterating over the devices
        for (Device device : devices) {
            // Creating COAP Clients
            final CoapClient coapClient = new CoapClient(
                    "coap", device.getIp(), device.getPort(), "sensors", "soil-moisture");
            // We created a private class that will handle soil moisture changes.
            // This Class Will also Change the states of the other Sensors accordingly
            coapClient.observe(new observeListener(device)); }}
    /*
    Method That returns a boolean according to the time it was called at.
    Working Hours are between 9AM and 5PM
    The method will return true if we are during working hours
    The method will return false if we are out of working hours
    */
    public boolean UV_turnOnTime(){
        final Calendar instance = GregorianCalendar.getInstance();
        final int i = instance.get(Calendar.HOUR_OF_DAY);
        return i <= 17 && i >= 9 ; }

    // Method That Changes the The water Pump State
    public void changeWaterPumpState(boolean on){
        // If the Parameter passed is true, meaning the soil is moisturized, the water pump is turned off
        if (on){
            // Iterating the Devices and Creating Coap Clients accordingly
            // For Every Client, we issue a put method changing the state of the water pump pin to LOW
            for (Device device : devices) {
                final CoapClient coapClient = new CoapClient("coap",
                        device.getIp(), device.getPort(), "sensors", "water-pump");
                coapClient.put(new CoapHandler() {
                    @Override
                    public void onLoad(CoapResponse response) {
                        if (response.isSuccess()){
                            System.out.println("Turning OFF Water Pump."); }}
                    @Override
                    public void onError() { }
                }, "LOW", MediaTypeRegistry.TEXT_PLAIN); // Sending the LOW state
                }}
        // If the passed parameter is False, meaning the soil is not moisturized, the water pump is turned on.
        // Same as the Above Method
        else {
            for (Device device : devices) {
                final CoapClient coapClient = new CoapClient("coap",
                        device.getIp(), device.getPort(), "sensors",
                        "water-pump");
                coapClient.put(new CoapHandler() {
                    @Override
                    public void onLoad(CoapResponse response) {
                        if (response.isSuccess()){
                            System.out.println("Turning ON Water Pump."); }}
                    @Override
                    public void onError() {}
                }, "HIGH", MediaTypeRegistry.TEXT_PLAIN); // Sending the HIGH state
            }}}
    // Method that changes UV-Lights state
    public void changeUVLightState(){
            // Iterating The Devices
            // First we Check the State of The UV-Lights
            // Then We Check if the its working hours or not
            // If the Lights are on and we're in the Working hours, no action is required
            // Otherwise Turn the lights off
            // And Vice-Versa
            for (Device device : devices) {
                final CoapClient coapClient = new CoapClient("coap",
                        device.getIp(), device.getPort(), "sensors", "uv-light");
                    coapClient.get(new CoapHandler() {
                        @Override
                        public void onLoad(CoapResponse response) {
                            final String UV_State = response.getResponseText();
                            final boolean isWorkingHour = UV_turnOnTime();
                            System.out.printf("Response From UV_Lights: %s\n", UV_State);
                            System.out.printf("IS Working Hours: %s\n", isWorkingHour);
                            if (isWorkingHour){
                                if (UV_State.equalsIgnoreCase("HIGH")){ return; }
                                else if (UV_State.equalsIgnoreCase("LOW")){ turnUVON(); } }
                            else {
                                if (UV_State.equalsIgnoreCase("HIGH")) {turnUVOff();}
                                else { return;} }}
                        @Override
                        public void onError() { }});}}
    // Put Method To Turn On UV Lights
    public void turnUVON(){
        for (Device device : devices) {
            final CoapClient coapClient = new CoapClient("coap",
                    device.getIp(), device.getPort(), "sensors", "uv-light");
            coapClient.put(new CoapHandler() {
                @Override
                public void onLoad(CoapResponse response) {
                    if (response.isSuccess()){System.out.println("Turning ON UV Lights.\n");}}
                @Override
                public void onError() {}
            }, "HIGH", MediaTypeRegistry.TEXT_PLAIN);}}

    // Put Method To TurnOFF the UV Lights
    public void turnUVOff(){
        for (Device device : devices) {
            final CoapClient coapClient = new CoapClient("coap",
                    device.getIp(), device.getPort(), "sensors", "uv-light");
            coapClient.put(new CoapHandler() {
                @Override
                public void onLoad(CoapResponse response) {
                    if (response.isSuccess()) {
                        System.out.println("Turning OFF UV Lights.\n");
                    }
                }

                @Override
                public void onError() {
                }
            }, "LOW", MediaTypeRegistry.TEXT_PLAIN); }}

    // Get Method That will get the IP, Port And State of The Coap Servers.
    // Then returns them in a list
    public List<States> getMoistureState(){
        final List<States> states = new ArrayList<>();
        for (Device device : devices) {
            final CoapClient coapClient = new CoapClient
                    ("coap", device.getIp(), device.getPort(), "sensors", "soil-moisture");
            // Sending Get Request
            coapClient.get(new CoapHandler() {
                @Override
                public void onLoad(CoapResponse response) {
                    states.add(new States(device.getIp(), device.getPort(), response.getResponseText())); }
                @Override
                public void onError() {} } ); } return states; }

    // Class That will act upon Changed Moisture State
    private class observeListener implements CoapHandler{
        private final Device device;
        public observeListener(Device device){
            this.device = device;
        } // Constructor
        @Override
        public void onLoad(CoapResponse response) {
            // If Response is not successful, return.
            if (!response.isSuccess()){ return; }
            // Getting the Payload that contains the state of the moisture
            String payload = response.getResponseText();
            // Getting IP of Device That changed in state
            String deviceIP = device.getIp();
            // Getting the port of device That changed in state
            int port =device.getPort();
            // If the moisture state is HIGH, we are turning on the water pumps as well as checking for the UV-Lights
            if (payload.equalsIgnoreCase("high")){
                System.out.printf("Moisture Level: %s\n Turning off Water pump on rack: %s:%s.\n\n", payload, deviceIP, port);
                changeWaterPumpState(true); changeUVLightState(); }
            else{
                System.out.printf("Moisture Level: %s\n Turning on Water pump on rack: %s:%s.\n\n", payload, deviceIP, port);
                changeWaterPumpState(false); changeUVLightState(); }}
        @Override
        public void onError() {} }}
