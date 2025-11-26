import java.util.List;
import java.util.Random;

/**
 * Classe base para todos os animais da simulação.
 * Controla idade, estado de vida, posição no campo e acesso ao terreno.
 */
public abstract class Animal implements EntidadeSimulavel{
    private int age;
    private boolean alive;
    private static final Random rand = new Random();
    private Location location;
    private EntidadeSimulavel terrenoEmbaixo;

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
     * @param location localização inicial do animal no campo
     */
    public Animal(boolean randomAge, Location location) {
        this.age = 0;
        this.alive = true;
        this.terrenoEmbaixo = null;
        if (randomAge) {
            this.age = rand.nextInt(getMaxAge());
        }
        // Nota: a posição inicial é definida, e a colocação no campo será feita pelo Simulator
    }

    protected boolean canBreed() {
        return age >= getBreedingAge();
    }

    public boolean isAlive() {
        return alive;
    }

    protected void setDead() {
        alive = false;
        // Solicita ao Simulator a remoção deste animal do campo
        Simulator.registerDeath(this);
        location = null;
        terrenoEmbaixo = null;
    }

    protected void incrementAge() {
        age++;
        if (age > getMaxAge()) {
            setDead();
        }
    }

    protected Field getField() {
        // Obtém o campo atual via Simulator
        return Simulator.getField();
    }

    protected Location getLocation() {
        return location;
    }

    protected void setLocation(Location newLocation) {
        // Atualiza apenas a posição interna; a manipulação do campo é feita no Simulator
        this.location = newLocation;
    }

    protected EntidadeSimulavel getTerrenoEmbaixo() {
        return terrenoEmbaixo;
    }

    protected void setTerrenoEmbaixo(EntidadeSimulavel obj) {
        this.terrenoEmbaixo = obj;
    }
}
