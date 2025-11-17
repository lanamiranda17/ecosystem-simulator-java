import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Simulator {
    private static final int DEFAULT_DEPTH = 50;
    private static final int DEFAULT_WIDTH = 50;

    private static final double FOX_CREATION_PROBABILITY = 0.03;
    private static final double RABBIT_CREATION_PROBABILITY = 0.10;
    private static final double PLANT_CREATION_PROBABILITY = 0.20;
    private static final double LOBO_CREATION_PROBABILITY = 0.01;
    private static final double LAGO_PROBABILITY = 0.05; 
    private static final double MONTANHA_PROBABILITY = 0.03; 

    private final ArrayList<Animal> animals;
    private final Field field;
    private int step;
    private final SimulatorView view;
    private final Random rand = new Random();

    private static final int PASSOS_POR_ESTACAO = 100;
    private Clima climaAtual;

    public Simulator() { this(DEFAULT_DEPTH, DEFAULT_WIDTH); }

    public Simulator(int depth, int width) {
        if (width <= 0 || depth <= 0) {
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        animals = new ArrayList<>();
        field = new Field(depth, width);
        view = new SimulatorView(depth, width);
        view.setColor(Rabbit.class, Color.ORANGE);
        view.setColor(Fox.class, Color.BLUE);
        view.setColor(Planta.class, Color.GREEN);
        view.setColor(Lobo.class, Color.BLACK);
        view.setColor(Lago.class, Color.CYAN);
        view.setColor(Montanha.class, new Color(139, 69, 19));
        reset();
    }

    public void runLongSimulation() { simulate(500); }

    public void simulate(int numSteps) {
        for (int i = 1; i <= numSteps && view.isViable(field); i++) {
            simulateOneStep();
        }
    }

    public void simulateOneStep() {
        step++;
        
        if (step % PASSOS_POR_ESTACAO == 0) {
            avancarClima();
        }
        
        spawnPlants();
        List<Animal> newAnimals = new ArrayList<>();

        for (Iterator<Animal> it = animals.iterator(); it.hasNext();) {
            Animal animal = it.next();
            animal.act(newAnimals, climaAtual);
            if (!animal.isAlive()) it.remove();
        }

        animals.addAll(newAnimals);
        view.showStatus(step, field);
        
        /*try {
            // Pausa por 100 milissegundos (0.1 segundos)
            // Aumente este número se ainda estiver rápido (ex: 200)
            // Diminua se ficar muito lento (ex: 50)
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // Apenas ignora se a "pausa" for interrompida
        }*/
    }

    public void reset() {
        step = 0;
        animals.clear();
        field.clear();
        climaAtual = Clima.PRIMAVERA;
        populate();
        view.showStatus(step, field);
    }
    
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
            System.err.println("Erro ao ler o 'mapa.txt'. O mapa está na pasta correta?");
            System.err.println("Usando mapa aleatório como fallback...");
            populateRandomly(); 
            return; 
        }

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

    private void populateRandomly() {
        field.clear();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                double r = rand.nextDouble();
                Location location = new Location(row, col);

                if(r <= LAGO_PROBABILITY) {
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

    private void spawnPlants() {
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                if (field.getObjectAt(location) == null) {
                    if (rand.nextDouble() <= PLANT_CREATION_PROBABILITY) {
                        Planta planta = new Planta(field, location);
                        field.place(planta, location);
                    }
                }
            }
        }
    }

    private void avancarClima() {
        switch (climaAtual) {
            case PRIMAVERA:
                climaAtual = Clima.VERAO;
                break;
            case VERAO:
                climaAtual = Clima.OUTONO;
                break;
            case OUTONO:
                climaAtual = Clima.INVERNO;
                break;
            case INVERNO:
                climaAtual = Clima.PRIMAVERA;
                break;
        }
    }
}