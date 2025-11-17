import java.util.List;
import java.util.Random;

public abstract class Animal {
    private int age;
    private boolean alive;
    private static final Random rand = new Random();
    private Field field;
    private Location location;
    private Object terrenoEmbaixo;

   public abstract void act(List<Animal> newAnimals, Clima clima);
    protected abstract int getBreedingAge();
    protected abstract int getMaxAge();
    protected abstract boolean podeMover(Location loc);

    public Animal(boolean randomAge, Field field, Location location) {
        this.age = 0;
        this.alive = true;
        this.field = field;
        this.terrenoEmbaixo = null;
        setLocation(location); 
        if (randomAge) {
            this.age = rand.nextInt(getMaxAge()); 
        }
    }

    protected boolean canBreed() {
        return age >= getBreedingAge();
    }

    public boolean isAlive() {
        return alive;
    }

    protected void setDead() {
        alive = false;
        if (location != null && field != null) {
            if (terrenoEmbaixo != null) {
                field.place(terrenoEmbaixo, location);
            } else {
                field.clear(location);
            }
            location = null;
            field = null;
        }
    }

    protected void incrementAge() {
        age++;
        if (age > getMaxAge()) {
            setDead();
        }
    }

    protected Field getField() {
        return field;
    }

    protected Location getLocation() {
        return location;
    }

    protected void setLocation(Location newLocation) {
        if (location != null && field != null) {
            if (terrenoEmbaixo != null) {
                field.place(terrenoEmbaixo, location);
            } else {
                field.clear(location);
            }
        }

        this.location = newLocation;
        
        if (field != null && newLocation != null) {
            Object objAtNewLocation = field.getObjectAt(newLocation);
            
            if (objAtNewLocation instanceof Lago || objAtNewLocation instanceof Montanha) {
                this.terrenoEmbaixo = objAtNewLocation;
            } else {
                this.terrenoEmbaixo = null; 
            }
            
            field.place(this, newLocation);
        }
    }
}