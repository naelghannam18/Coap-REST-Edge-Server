package lb.edu.aust.ict355.CoAp_Edge_Client_Rest;

// Creating the Device Class that holds Device Information
// Each device will have as parameters an IP (String) and a port (Integer)
public class Device {
    // Initializing Parameters
    private String ip; private int port;
    // Creating Constructor
    public Device(String ip, int port) {
        this.ip = ip; this.port = port; }
    // Creating Setters and Getters
    public String getIp() { return ip; }
    public int getPort() {
        return port;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public void setPort(int port) {
        this.port = port;
    }
}
