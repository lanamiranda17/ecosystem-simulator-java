import java.util.List;
import java.util.Random;

/**
 * Classe base para todos os animais da simulação.
 * Controla idade, estado de vida, posição no campo e acesso ao terreno.
 */
public abstract class Animal {
    private int age;
    private boolean alive;
    private static final Random rand = new Random();
    private Field field;
    private Location location;
    private Object terrenoEmbaixo;

    /**
     * Executa um passo da simulação para este animal.
     * A implementação concreta define o comportamento (mover, comer, reproduzir, etc.).
     *
     * @param newAnimals lista onde novos animais gerados neste passo devem ser adicionados
     * @param clima clima atual da simulação
     */
    public abstract void act(List<Animal> newAnimals, Clima clima);
    
    /**
     * Retorna a idade mínima para que o animal possa se reproduzir.
     *
     * @return idade mínima de reprodução
     */
    protected abstract int getBreedingAge();
    
    /**
     * Retorna a idade máxima que o animal pode atingir antes de morrer de velhice.
     *
     * @return idade máxima
     */
    protected abstract int getMaxAge();
  
    protected abstract boolean podeMover(Location loc);
    
    /**
     * Cria um novo animal em uma posição do campo.
     *
     * @param randomAge se true, inicia o animal com idade aleatória
     * @param field campo em que o animal será inserido
     * @param location localização inicial do animal no campo
     */
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