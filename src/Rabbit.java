import java.util.List;
import java.util.Iterator; 
import java.util.Random;

public class Rabbit extends Animal {
    private static final int BREEDING_AGE = 5;
    private static final int MAX_AGE = 40;
    private static final double BREEDING_PROBABILITY = 0.15;
    private static final int MAX_LITTER_SIZE = 5;
    private static final Random RAND = new Random();
    
    private static final int PLANT_FOOD_VALUE = 12; 
    
    private int foodLevel;

    public Rabbit(boolean randomAge, Field field, Location location) {
        super(randomAge, field, location);
        
        if (randomAge) {
            this.foodLevel = RAND.nextInt(PLANT_FOOD_VALUE) + 1;
        } else {
            this.foodLevel = PLANT_FOOD_VALUE;
        }
    }

    @Override
    public void act(List<Animal> newAnimals, Clima clima) {
        incrementAge();
        incrementHunger(); 

        if (!isAlive()) return;

        giveBirth(newAnimals, clima);

        moveToNewLocation();
    }


    private void moveToNewLocation() {
        Location newLocation = findFood(); 

        if (newLocation == null) { 
            Field field = getField();
            Iterator<Location> it = field.adjacentLocations(getLocation());
            while(it.hasNext()) {
                Location loc = it.next();
                if (field.getObjectAt(loc) == null) {
                    if (podeMover(loc)) {
                        newLocation = loc;
                        break;
                    }
                }
            }
        }

        if (newLocation != null) {
            setLocation(newLocation);
        } else {
            setDead();
        }
    }

    private void incrementHunger() {
        foodLevel--;
        if (foodLevel <= 0) {
            setDead();
        }
    }

    private Location findFood() {
        Field field = getField();
        Iterator<Location> it = field.adjacentLocations(getLocation());

        while (it.hasNext()) {
            Location where = it.next();
            Object obj = field.getObjectAt(where);

            if (obj instanceof Planta) {
                Planta planta = (Planta) obj;
                if (planta.isAlive() && podeMover(where)) {
                    planta.setDead(); 
                    foodLevel = PLANT_FOOD_VALUE; 
                    return where; 
                }
            }
        }
        return null; 
    }

    private void giveBirth(List<Animal> newAnimals, Clima clima) {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed(clima);
        for (int b = 0; b < births && !free.isEmpty(); b++) {
            Location loc = free.remove(0);
            newAnimals.add(new Rabbit(false, field, loc));
        }
    }

    private int breed(Clima clima) {
        double chance = BREEDING_PROBABILITY;
        
        if (clima == Clima.PRIMAVERA) {
            chance *= 1.25;
        } else if (clima == Clima.VERAO) {
            chance *= 0.85;
        } else if (clima == Clima.OUTONO) {
            chance *= 1.15;
        }
        
        if (canBreed() && RAND.nextDouble() <= chance) {
            return RAND.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return 0;
    }
    
    @Override
    protected int getBreedingAge() { 
        return BREEDING_AGE;
    }

    @Override
    protected int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected boolean podeMover(Location loc) {
        Object obj = getField().getObjectAt(loc);
        return !(obj instanceof Lago) && !(obj instanceof Montanha);
    }
}