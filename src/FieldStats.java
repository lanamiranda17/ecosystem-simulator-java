import java.util.HashMap;
import java.util.Iterator;

/**
 * Mantém estatísticas sobre a população do campo,
 * como a contagem de cada tipo de objeto.
 */
public class FieldStats {
    private HashMap<Class, Counter> counters;
    private boolean countsValid;

    public FieldStats() {
        counters = new HashMap<Class, Counter>();
        countsValid = true;
    }

    public String getPopulationDetails(Field field) {
        StringBuffer buffer = new StringBuffer();
        if (!countsValid) {
            generateCounts(field);
        }
        
        Iterator<Class> keys = counters.keySet().iterator();
        while (keys.hasNext()) {
            Counter info = counters.get(keys.next());
            buffer.append(info.getName());
            buffer.append(": ");
            buffer.append(info.getCount());
            buffer.append(' ');
        }
        return buffer.toString();
    }

    public void reset() {
        countsValid = false;
        Iterator<Class> keys = counters.keySet().iterator();
        while (keys.hasNext()) {
            Counter counter = counters.get(keys.next());
            counter.reset();
        }
    }

    public void incrementCount(Class aClass) {
        Counter counter = counters.get(aClass);
        if (counter == null) {
            counter = new Counter(aClass.getName());
            counters.put(aClass, counter);
        }
        counter.increment();
    }

    public void countFinished() {
        countsValid = true;
    }

    public boolean isViable(Field field) {
        int nonZero = 0;
        if (!countsValid) {
            generateCounts(field);
        }
        
        Iterator<Class> keys = counters.keySet().iterator();
        while (keys.hasNext()) {
            Counter info = counters.get(keys.next());
            if (info.getCount() > 0) {
                nonZero++;
            }
        }
        return nonZero > 1;
    }

    private void generateCounts(Field field) {
        reset();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Object obj = field.getObjectAt(row, col);
                if (obj != null) {
                    incrementCount(obj.getClass());
                }
            }
        }
        countsValid = true;
    }
}