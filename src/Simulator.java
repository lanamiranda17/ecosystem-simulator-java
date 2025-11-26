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

    private final ArrayList<Animal> animals;
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

        if (width <= 0 || depth <= 0) {
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        animals = new ArrayList<>();
        field = new Field(depth, width);
        view = new SimulatorView(depth, width);

        // Registra cores para a GUI
        view.setColor(Rabbit.class, Color.ORANGE);
        view.setColor(Fox.class, Color.BLUE);
        view.setColor(Planta.class, Color.GREEN);
        view.setColor(Lobo.class, Color.BLACK);
        view.setColor(Lago.class, Color.CYAN);
        view.setColor(Montanha.class, new Color(139, 69, 19));

        reset();
    }

    /**
     * Executa uma simulação longa fixa de 500 passos.
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
        spawnPlants(0.15);
        for (int i = 1; i <= numSteps && view.isViable(field); i++) {
            simulateOneStep();
        }
    }

    /**
     * Executa um único passo da simulação.
     * Aqui ocorrem:
     * - avanço da estação
     * - crescimento de plantas
     * - movimentação, alimentação e reprodução dos animais
     * - remoção dos animais mortos
     * - atualização da interface gráfica
     */
    public void simulateOneStep() {
        step++;

        if (step % PASSOS_POR_ESTACAO == 0) {
            avancarClima();
        }

        List<Animal> newAnimals = new ArrayList<>();

        for (Iterator<Animal> it = animals.iterator(); it.hasNext();) {
            Animal animal = it.next();
            animal.act(newAnimals, climaAtual);

            if (!animal.isAlive()) {
                it.remove();
            }
        }

        animals.addAll(newAnimals);
        view.showStatus(step, field);
    }

    /**
     * Reinicia completamente a simulação:
     * - limpa o campo
     * - redefine o passo
     * - volta o clima para PRIMAVERA
     * - recria todos os animais e terrenos usando o populate()
     */
    public void reset() {
        step = 0;
        animals.clear();
        field.clear();
        climaAtual = Clima.PRIMAVERA;
        populate();
        view.showStatus(step, field);
    }

    /**
     * Preenche o campo inicial usando o arquivo mapa.txt.
     *
     * O arquivo define onde existem:
     * - 'L' → lago
     * - 'M' → montanha
     *
     * Caso o arquivo não seja encontrado, cai no populateRandomly().
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

                    if (c == 'L') field.place(new Lago(), location);
                    else if (c == 'M') field.place(new Montanha(), location);
                }

                row++;
            }

        } catch (IOException e) {
            System.err.println("Erro ao ler mapa.txt. Usando mapa aleatório.");
            populateRandomly();
            return;
        }

        // Preenche animais nas células livres
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {

                Location location = new Location(row, col);

                if (field.getObjectAt(location) == null) {
                    double r = rand.nextDouble();

                    if (r <= LOBO_CREATION_PROBABILITY) {
                        animals.add(new Lobo(true, field, location));
                    }
                    else if (r <= LOBO_CREATION_PROBABILITY + FOX_CREATION_PROBABILITY) {
                        animals.add(new Fox(true, field, location));
                    }
                    else if (r <= LOBO_CREATION_PROBABILITY + FOX_CREATION_PROBABILITY + RABBIT_CREATION_PROBABILITY) {
                        animals.add(new Rabbit(true, field, location));
                    }
                }
            }
        }
    }

    /**
     * Cria um mapa totalmente aleatório caso o mapa.txt não exista.
     */
    private void populateRandomly() {

        field.clear();

        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {

                double r = rand.nextDouble();
                Location location = new Location(row, col);

                if (r <= LAGO_PROBABILITY) {
                    field.place(new Lago(), location);
                }
                else if (r <= LAGO_PROBABILITY + MONTANHA_PROBABILITY) {
                    field.place(new Montanha(), location);
                }
                else if (r <= LAGO_PROBABILITY + MONTANHA_PROBABILITY + LOBO_CREATION_PROBABILITY) {
                    animals.add(new Lobo(true, field, location));
                }
                else if (r <= LAGO_PROBABILITY + MONTANHA_PROBABILITY + LOBO_CREATION_PROBABILITY + FOX_CREATION_PROBABILITY) {
                    animals.add(new Fox(true, field, location));
                }
                else if (r <= LAGO_PROBABILITY + MONTANHA_PROBABILITY + LOBO_CREATION_PROBABILITY + FOX_CREATION_PROBABILITY + RABBIT_CREATION_PROBABILITY) {
                    animals.add(new Rabbit(true, field, location));
                }
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
                        field.place(new Planta(field, loc), loc);
                    }
                }
            }
        }
    }


    /**
     * Alterna a estação do ano conforme o número de passos definidos.
     */
    private void avancarClima() {

    switch (climaAtual) {
        case PRIMAVERA:
            climaAtual = Clima.VERAO;
            spawnPlants(0.40); // muitas plantas
            break;

        case VERAO:
            climaAtual = Clima.OUTONO;
            spawnPlants(0.25); 
            break;

        case OUTONO:
            climaAtual = Clima.INVERNO;
            spawnPlants(0.1); // quase nada
            break;

        case INVERNO:
            climaAtual = Clima.PRIMAVERA;
            spawnPlants(0.80); // renascimento da vida
            break;
    }
}

}
