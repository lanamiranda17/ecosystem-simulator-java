import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Controla toda a simulação:
 * - mantém o campo (Field)
 * - controla o ciclo de passos
 * - cria, atualiza e remove animais
 * - administra o clima ao longo da simulação
 * - alimenta a interface gráfica (SimulatorView)
 */
public class Simulator {
    private static final int DEFAULT_DEPTH = 50;
    private static final int DEFAULT_WIDTH = 70;

    private static final double FOX_CREATION_PROBABILITY = 0.03;
    private static final double RABBIT_CREATION_PROBABILITY = 0.10;
    private static final double LOBO_CREATION_PROBABILITY = 0.01;

    /** Probabilidades usadas quando o mapa.txt não é encontrado. */
    private static final double LAGO_PROBABILITY = 0.05;
    private static final double MONTANHA_PROBABILITY = 0.03;

    /** Instância única do simulador para acesso global ao campo. */
    private static Simulator instance;

    private final List<Animal> animals;
    private final Field field;
    private int step;
    private final SimulatorView view;
    private final Random rand = new Random();
    private static final int PASSOS_POR_ESTACAO = 100;
    private Clima climaAtual;

    public Simulator() {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    public Simulator(int depth, int width) {
        if (depth <= 0 || width <= 0) {
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        animals = new ArrayList<>();
        field = new Field(depth, width);
        instance = this;  // registra a instância atual para acesso estático
        view = new SimulatorView(depth, width);

        // Registra cores para a interface gráfica
        view.setColor(Rabbit.class, Color.ORANGE);
        view.setColor(Fox.class, Color.BLUE);
        view.setColor(Planta.class, Color.GREEN);
        view.setColor(Lobo.class, Color.BLACK);
        view.setColor(Lago.class, Color.CYAN);
        view.setColor(Montanha.class, new Color(139, 69, 19));

        reset();
    }

    /**
     * Executa uma simulação longa de 500 passos.
     */
    public void runLongSimulation() {
        simulate(500);
    }

    /**
     * Executa a simulação por um número específico de passos.
     *
     * A execução para caso o campo deixe de ser viável
     * (por exemplo, apenas uma espécie restante).
     *
     * @param numSteps quantidade de passos a simular
     */
    public void simulate(int numSteps) {
        for (int i = 1; i <= numSteps && view.isViable(field); i++) {
            simulateOneStep();
        }
    }

    /**
     * Executa um único passo da simulação, incluindo:
     * - mudança de estação (clima)
     * - crescimento de novas plantas
     * - ação de cada animal (movimento, alimentação, reprodução)
     * - remoção de animais mortos
     * - atualização da interface gráfica
     */
    public void simulateOneStep() {
        step++;
        if (step % PASSOS_POR_ESTACAO == 0) {
            avancarClima();
        }

        List<Animal> newAnimals = new ArrayList<>();

        // Executa a ação de cada animal existente
        Iterator<Animal> it = animals.iterator();
        while (it.hasNext()) {
            Animal animal = it.next();
            animal.act(newAnimals, climaAtual);
            if (!animal.isAlive()) {
                it.remove();
            }
        }

        // Adiciona os novos animais nascidos à lista principal
        animals.addAll(newAnimals);
        view.showStatus(step, field);
    }

    /**
     * Reinicia a simulação:
     * - limpa o campo
     * - reinicia a contagem de passos
     * - define o clima para PRIMAVERA
     * - repopula o campo com animais, plantas e terrenos iniciais
     */
    public void reset() {
        step = 0;
        animals.clear();
        field.clear();
        climaAtual = Clima.PRIMAVERA;
        populate();
        spawnPlants(0.35);
        view.showStatus(step, field);
    }

    /**
     * Preenche o campo inicial usando o arquivo "mapa.txt".
     * O arquivo define posições de:
     *  - 'L' para lago
     *  - 'M' para montanha
     *
     * Se o arquivo não for encontrado, utiliza populateRandomly().
     */
    private void populate() {
        field.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader("mapa.txt"))) {
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null && row < field.getDepth()) {
                for (int col = 0; col < line.length() && col < field.getWidth(); col++) {
                    char c = line.charAt(col);
                    Location location = new Location(row, col);
                    if (c == 'L') {
                        field.place(new Lago(), location);
                    } else if (c == 'M') {
                        field.place(new Montanha(), location);
                    }
                }
                row++;
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler mapa.txt. Usando distribuição aleatória.");
            populateRandomly();
            return;
        }

        // Preenche animais nas células que permanecem vazias
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                if (field.getObjectAt(location) == null) {
                    double r = rand.nextDouble();
                    if (r <= LOBO_CREATION_PROBABILITY) {
                        Lobo lobo = new Lobo(true, location);
                        animals.add(lobo);
                        Simulator.registerMove(lobo, location);
                    } else if (r <= LOBO_CREATION_PROBABILITY + FOX_CREATION_PROBABILITY) {
                        Fox fox = new Fox(true, location);
                        animals.add(fox);
                        Simulator.registerMove(fox, location);
                    } else if (r <= LOBO_CREATION_PROBABILITY + FOX_CREATION_PROBABILITY + RABBIT_CREATION_PROBABILITY) {
                        Rabbit rabbit = new Rabbit(true, location);
                        animals.add(rabbit);
                        Simulator.registerMove(rabbit, location);
                    }
                    // (Caso contrário, permanece vazio ou será preenchido por planta posteriormente)
                }
            }
        }
    }

    /**
     * Preenche o campo inicial de forma totalmente aleatória 
     * (usado se o arquivo de mapa não existir).
     */
    private void populateRandomly() {
        field.clear();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                double r = rand.nextDouble();
                Location location = new Location(row, col);
                if (r <= LAGO_PROBABILITY) {
                    field.place(new Lago(), location);
                } else if (r <= LAGO_PROBABILITY + MONTANHA_PROBABILITY) {
                    field.place(new Montanha(), location);
                } else if (r <= LAGO_PROBABILITY + MONTANHA_PROBABILITY + LOBO_CREATION_PROBABILITY) {
                    Lobo lobo = new Lobo(true, location);
                    animals.add(lobo);
                    Simulator.registerMove(lobo, location);
                } else if (r <= LAGO_PROBABILITY + MONTANHA_PROBABILITY + LOBO_CREATION_PROBABILITY + FOX_CREATION_PROBABILITY) {
                    Fox fox = new Fox(true, location);
                    animals.add(fox);
                    Simulator.registerMove(fox, location);
                } else if (r <= LAGO_PROBABILITY + MONTANHA_PROBABILITY + LOBO_CREATION_PROBABILITY + FOX_CREATION_PROBABILITY + RABBIT_CREATION_PROBABILITY) {
                    Rabbit rabbit = new Rabbit(true, location);
                    animals.add(rabbit);
                    Simulator.registerMove(rabbit, location);
                }
                // Demais casos: célula permanece vazia (poderá receber planta em spawnPlants)
            }
        }
    }

    /**
     * Gera novas plantas aleatórias em células vazias.
     * Executado inicialemnte e depois a cada troca de estação.
     * Cada estação tem sua probabilidade de nascimento de plantas.
     */
    private void spawnPlants(double prob) {
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Location loc = new Location(row, col);

                if (field.getObjectAt(loc) == null) {
                    if (rand.nextDouble() <= prob) {
                        field.place(new Planta(loc), loc);
                    }
                }
            }
        }
    }


    /**
     * Alterna a estação do ano (ciclo de clima) após um determinado número de passos.
     */
    private void avancarClima() {
        switch (climaAtual) {
            case PRIMAVERA:
                climaAtual = Clima.VERAO;
                spawnPlants(0.15); // muitas plantas
                break;

            case VERAO:
                climaAtual = Clima.OUTONO;
                spawnPlants(0.08); 
                break;

            case OUTONO:
                climaAtual = Clima.INVERNO;
                spawnPlants(0.03); // quase nada
                break;

            case INVERNO:
                climaAtual = Clima.PRIMAVERA;
                spawnPlants(0.25); // renascimento da vida
                break;
        }
    }

    /**
     * Retorna o campo de simulação atualmente em uso.
     *
     * @return o objeto Field da simulação
     */
    public static Field getField() {
        return instance.field;
    }

    /**
     * Registra a movimentação de um animal no campo.
     * Esse método cuida de remover o animal da posição antiga, recolocar o terreno que estava abaixo (se houver),
     * e então posicioná-lo na nova localização, atualizando sua referência de posição.
     *
     * @param animal o animal que está se movendo
     * @param newLocation a nova localização para onde o animal deve ir
     */
    public static void registerMove(Animal animal, Location newLocation) {
        Location oldLoc = animal.getLocation();
        if (oldLoc != null) {
            // Deixar a posição antiga livre ou recolocar terreno original
            if (animal.getTerrenoEmbaixo() != null) {
                instance.field.place(animal.getTerrenoEmbaixo(), oldLoc);
            } else {
                instance.field.clear(oldLoc);
            }
        }
        // Verificar o que existe na nova posição (pode ser terreno como Lago/Montanha)
        EntidadeSimulavel objetoAlvo = instance.field.getObjectAt(newLocation);
        if (objetoAlvo instanceof Lago || objetoAlvo instanceof Montanha) {
            animal.setTerrenoEmbaixo(objetoAlvo);
        } else {
            animal.setTerrenoEmbaixo(null);
        }
        // Colocar o animal na nova posição e atualizá-lo
        instance.field.place(animal, newLocation);
        animal.setLocation(newLocation);
    }

    /**
     * Registra a morte de um animal, removendo-o do campo.
     * Recoloca o objeto de terreno que estava abaixo do animal (se houver) na posição em que ele morreu.
     *
     * @param animal o animal que morreu e deve ser removido do campo
     */
    public static void registerDeath(Animal animal) {
        Location loc = animal.getLocation();
        if (loc != null) {
            if (animal.getTerrenoEmbaixo() != null) {
                instance.field.place(animal.getTerrenoEmbaixo(), loc);
            } else {
                instance.field.clear(loc);
            }
        }
        // (A posição do animal agora fica ocupada pelo terreno original ou vazia)
    }

}
