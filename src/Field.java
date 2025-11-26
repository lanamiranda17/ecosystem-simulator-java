import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

/**
 * Representa o campo em forma de grade onde animais, plantas e obstáculos são posicionados.
 */
public class Field {
    private EntidadeSimulavel[][] field;
    private int depth;
    private int width;

    /**
     * Cria um novo campo retangular.
     *
     * @param depth número de linhas do campo
     * @param width número de colunas do campo
     */
    public Field(int depth, int width) {
        this.depth = depth;
        this.width = width;
        field = new EntidadeSimulavel[depth][width];
    }

    public void clear() {
        for (int row = 0; row < depth; row++) {
            for (int col = 0; col < width; col++) {
                field[row][col] = null;
            }
        }
    }

    public void clear(Location location) {
        field[location.getRow()][location.getCol()] = null;
    }

    public void place(EntidadeSimulavel obj, int row, int col) {
        field[row][col] = obj;
    }

    /**
     * Posiciona um objeto em uma determinada localização do campo.
     *
     * @param obj objeto a ser colocado
     * @param location posição onde o objeto será inserido
     */
    public void place(EntidadeSimulavel obj, Location location) {
        field[location.getRow()][location.getCol()] = obj;
    }

    public EntidadeSimulavel getObjectAt(int row, int col) {
        return field[row][col];
    }

    public EntidadeSimulavel getObjectAt(Location location) {
        return field[location.getRow()][location.getCol()];
    }

    public int getDepth() {
        return depth;
    }

    public int getWidth() {
        return width;
    }

    public Iterator<Location> adjacentLocations(Location location) {
        int row = location.getRow();
        int col = location.getCol();
        List<Location> locations = new LinkedList<>();

        for (int roffset = -1; roffset <= 1; roffset++) {
            int nextRow = row + roffset;
            if (nextRow >= 0 && nextRow < depth) {
                for (int coffset = -1; coffset <= 1; coffset++) {
                    int nextCol = col + coffset;
                    if (nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                        locations.add(new Location(nextRow, nextCol));
                    }
                }
            }
        }
        Collections.shuffle(locations);
        return locations.iterator();
    }

    public List<Location> getFreeAdjacentLocations(Location location) {
        List<Location> free = new LinkedList<>();
        Iterator<Location> it = adjacentLocations(location);
        while (it.hasNext()) {
            Location loc = it.next();
            if (getObjectAt(loc) == null) {
                free.add(loc);
            }
        }
        return free;
    }

    public Location freeAdjacentLocation(Location location) {
        List<Location> free = getFreeAdjacentLocations(location);
        if (free.isEmpty()) {
            return null;
        } else {
            return free.get(0);
        }
    }

    public Location randomLocation() {
        int row = (int) (Math.random() * depth);
        int col = (int) (Math.random() * width);
        return new Location(row, col);
    }
}
