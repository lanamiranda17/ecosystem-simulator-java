import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * Representa um lobo na simulação.
 * Lobo atua como grande predador, podendo caçar coelhos e raposas.
 */
public class Lobo extends Animal {

    private static final int BREEDING_AGE = 18;
    private static final int MAX_AGE = 200;
    private static final double BREEDING_PROBABILITY = 0.01; 
    private static final int MAX_LITTER_SIZE = 1;
    private static final int FOX_FOOD_VALUE = 25;    
    private static final int RABBIT_FOOD_VALUE = 15;  
    private static final Random RAND = new Random();

    private int foodLevel;

    /**
     * Cria um lobo. Pode iniciar com idade aleatória e nível de comida cheio.
     *
     * @param randomAge se true, o lobo inicia com idade aleatória (caso contrário, idade 0)
     * @param location posição inicial do lobo no campo
     */
    public Lobo(boolean randomAge, Location location) {
        super(randomAge, location);
        if (randomAge) {
            foodLevel = RAND.nextInt(FOX_FOOD_VALUE) + 1;
        } else {
            foodLevel = FOX_FOOD_VALUE;
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
        if (foodLevel <= 0) {
            setDead();
        }
    }

    @Override
    protected boolean podeMover(Location loc) {
        return true;
    }

    private void moveToNewLocation() {
        Location newLocation = findFood();
        if (newLocation == null) {
            Field field = getField();
            Iterator<Location> it = field.adjacentLocations(getLocation());
            while (it.hasNext()) {
                Location loc = it.next();
                if (podeMover(loc)) {
                    Object obj = field.getObjectAt(loc);
                    if (!(obj instanceof Animal)) {
                        newLocation = loc;
                        break;
                    }
                }
            }
        }

        if (newLocation != null) {
            Simulator.registerMove(this, newLocation);
        } else {
            setDead();
        }
    }

    /**
     * Procura alimento nas posições adjacentes ao lobo.
     * O lobo pode comer raposas e coelhos. Ao encontrar uma presa viva,
     * ela é abatida, o nível de comida do lobo é restaurado conforme o tipo da presa,
     * e a localização onde a presa foi encontrada é retornada.
     *
     * @return a localização onde a presa foi encontrada;
     *         ou null se nenhuma presa estiver disponível nas redondezas
     */
    private Location findFood() {
        Field field = getField();
        Iterator<Location> it = field.adjacentLocations(getLocation());
        while (it.hasNext()) {
            Location where = it.next();
            if (podeMover(where)) {
                Object obj = field.getObjectAt(where);
                if (obj instanceof Fox) {
                    Fox fox = (Fox) obj;
                    if (fox.isAlive()) {
                        fox.setDead();
                        foodLevel = FOX_FOOD_VALUE;
                        return where;
                    }
                }
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
            Lobo young = new Lobo(false, loc);
            Simulator.registerMove(young, loc);
            newAnimals.add(young);
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
        if (canBreed() && RAND.nextDouble() <= BREEDING_PROBABILITY) {
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
}
