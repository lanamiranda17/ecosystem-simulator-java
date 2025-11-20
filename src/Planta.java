/**
 * Representa uma planta no campo,
 * que pode ser consumida por coelhos como fonte de alimento.
 */
public class Planta {

    private boolean alive;   
    private Field field;      
    private Location location;  

    public Planta(Field field, Location location) {
        this.alive = true;
        this.field = field;
        this.location = location;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setDead() {
        alive = false;
        if (location != null && field != null) {
            field.clear(location); 
            location = null;
            field = null;
        }
    }
}