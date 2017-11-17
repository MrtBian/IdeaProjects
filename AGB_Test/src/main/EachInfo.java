package main;

public class EachInfo {
    private String EPC;
    private double RSSI;
    private double Phase;
    private long Time;

    public EachInfo() {
    }

    public EachInfo(String EPC, double RSSI, double phase, long time) {
        this.EPC = EPC;
        this.RSSI = RSSI;
        Phase = phase;
        Time = time;
    }

    public String getEPC() {
        return EPC;
    }

    public void setEPC(String EPC) {
        this.EPC = EPC;
    }

    public double getRSSI() {
        return RSSI;
    }

    public void setRSSI(double RSSI) {
        this.RSSI = RSSI;
    }

    public double getPhase() {
        return Phase;
    }

    public void setPhase(double phase) {
        Phase = phase;
    }

    public long getTime() {
        return Time;
    }

    public void setTime(long time) {
        Time = time;
    }

}
