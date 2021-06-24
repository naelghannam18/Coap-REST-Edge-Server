package lb.edu.aust.ict355.CoAp_Edge_Client_Rest;

// Data Arriving from Get Request represent states of plant racks
// The date will be converted into a state object
// A state object will hold the Address of the rack, port and state.
public class States {
    private String ipAdd, state; private int port;

    public States(String ipAdd, int port, String state) {
        this.ipAdd = ipAdd; this.port = port; this.state = state; }

    public String getIpAdd() { return ipAdd; }
    public void setIpAdd(String ipAdd) { this.ipAdd = ipAdd;}
    public int getPort() { return port; }
    public void setPort(int port) {this.port = port;}
    public String getState() {return state;}
    public void setState(String state) { this.state = state; }}
