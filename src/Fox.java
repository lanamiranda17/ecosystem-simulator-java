import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * Representa uma raposa na simulação.
 * Raposas caçam coelhos para se alimentar, podem se reproduzir e morrer de fome ou velhice.
 */
public class Fox extends Animal {
    private static final int BREEDING_AGE = 10;
    private static final int MAX_AGE = 150;
    // Reduce fox reproduction and litter size to ease pressure on rabbits
    private static final double BREEDING_PROBABILITY = 0.04;
    private static final int MAX_LITTER_SIZE = 2;
    // Lower food value so foxes need to hunt more often and are less efficient
    private static final int RABBIT_FOOD_VALUE = 20; 
    private static final Random rand = new Random();

    private int foodLevel;

    public Fox(boolean randomAge, Field field, Location location) {
        super(randomAge, field, location);
        foodLevel = RABBIT_FOOD_VALUE;
        if (randomAge) {
            foodLevel = rand.nextInt(RABBIT_FOOD_VALUE) + 1;
        }
    }

    @Override
    public void act(List<Animal> newAnimals, Clima clima) {
        incrementAge();
        incrementHunger(clima);
        if (!isAlive()) return;

        giveBirth(newAnimals, clima);

        moveToNewLocation();
    }

    private void incrementHunger(Clima clima) {
        foodLevel--; 
        
        if (clima == Clima.INVERNO) {
            foodLevel--;
        }
        
        if (foodLevel <= 0) setDead();
    }

    @Override
    protected boolean podeMover(Location loc) {
        Object obj = getField().getObjectAt(loc);
        return !(obj instanceof Montanha);
    }

    private void moveToNewLocation() {
        Location newLocation = findFood(); 
        
        if (newLocation == null) { 
            Field field = getField();
            Iterator<Location> it = field.adjacentLocations(getLocation());
            while(it.hasNext()) {
                Location loc = it.next();
                
                if (podeMover(loc)) {
                    Object obj = field.getObjectAt(loc);
                    if (obj == null || obj instanceof Lago) {
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

    /**
     * Tenta encontrar comida (coelhos) nas posições adjacentes.
     *
     * @return localização de um coelho para ser caçado ou null se nada for encontrado
     */
    private Location findFood() {
        Field field = getField();
        Iterator<Location> it = field.adjacentLocations(getLocation());
        while (it.hasNext()) {
            Location where = it.next();
            
            if (podeMover(where)) {
                Object obj = field.getObjectAt(where);
                if (obj instanceof Rabbit) {
                    Rabbit rabbit = (Rabbit) obj;
                    if (rabbit.isAlive()) {
                        rabbit.setDead();               
                        foodLevel = RABBIT_FOOD_VALUE; 
                        return where;
                    }
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
            newAnimals.add(new Fox(false, field, loc));
        }
    }

    /**
     * Calcula o número de filhotes gerados nesta reprodução,
     * considerando a probabilidade definida e o clima.
     *
     * @param clima clima atual da simulação
     * @return quantidade de novos filhotes gerados
     */
    private int breed(Clima clima) {
        if (canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            return rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return 0;
    }

    @Override
    protected int getBreedingAge() { return BREEDING_AGE; }

    @Override
    protected int getMaxAge() { return MAX_AGE; }
}