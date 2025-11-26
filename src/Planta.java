/**
 * Representa uma planta no campo,
 * que pode ser consumida por coelhos como fonte de alimento.
 */
public class Planta implements EntidadeSimulavel{
    private boolean alive;
    private Location location;

    /**
     * Cria uma nova planta em uma determinada localização.
     *
     * @param location posição inicial da planta no campo
     */
    public Planta(Location location) {
        this.alive = true;
        this.location = location;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setDead() {
        alive = false;
        location = null;
    }

    public Location getLocation() {
        return location;
    }
}
