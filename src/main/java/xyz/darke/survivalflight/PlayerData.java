package xyz.darke.survivalflight;

public class PlayerData {

    private int flightTimeRemaining;
    private boolean safeFallEffect;

    public PlayerData() {
        this.flightTimeRemaining = 0;
        this.safeFallEffect = false;
    }

    public PlayerData(int flightTime, boolean safeFall) {
        this.flightTimeRemaining = flightTime;
        this.safeFallEffect = safeFall;
    }

    public int getFlightTimeRemaining() {
        return flightTimeRemaining;
    }

    public void setFlightTimeRemaining(int flightTimeRemaining) {
        this.flightTimeRemaining = flightTimeRemaining;
    }

    public void addFlightTimeRemaining(int flightTimeToAdd) {
        this.flightTimeRemaining = this.flightTimeRemaining + flightTimeToAdd;
    }

    public boolean isSafeFallEffect() {
        return safeFallEffect;
    }

    public void setSafeFallEffect(boolean safeFallEffect) {
        this.safeFallEffect = safeFallEffect;
    }

    public void decrementFlightTime() {
        this.flightTimeRemaining = flightTimeRemaining - 1;
    }
}
