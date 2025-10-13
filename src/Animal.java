import java.util.List;
import java.util.Random;

public abstract class Animal {
    private int age;
    private boolean alive;
    private static final Random rand = new Random();
    private Field field;
    private Location location;

    abstract public void act(List<Animal> newAnimals);
    abstract protected int getBreedingAge();
    abstract protected int getMaxAge();

    public boolean canBreed(){
        return age>= getBreedingAge();
    }
    public boolean isAlive(){
        return alive;
    }
    public Animal(boolean randomAge,Field field , Location location){
        age = 0;
        alive = true;
        this.field = field;
        this.location = location;
        if(randomAge) {
            age = rand.nextInt(getMaxAge());
        }
    }
    public void setDead(){
        alive = false;
        if(location != null){
            field.clear();
            location = null;
            field = null;
        }
    }
    private void incrementAge()
    {
        age++;
        if(age > getMaxAge()) {
            setDead();
        }
    }
}
