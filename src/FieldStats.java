import java.util.HashMap;
import java.util.Iterator;

public class FieldStats {
    private HashMap counters;
    private boolean countsValid;

    public FieldStats() {
        counters = new HashMap();
        countsValid = true;
    }

    public String getPopulationDetails(Field field) {
        StringBuffer buffer = new StringBuffer();
        if (!countsValid) {
            generateCounts(field);
        }
        Iterator keys = counters.keySet().iterator();
        while (keys.hasNext()) {
            Counter info = (Counter) counters.get(keys.next());
            buffer.append(info.getName());
            buffer.append(": ");
            buffer.append(info.getCount());
            buffer.append(' ');
        }
        return buffer.toString();
    }

    public void reset() {
        countsValid = false;
        Iterator keys = counters.keySet().iterator();
        while (keys.hasNext()) {
            Counter counter = (Counter) counters.get(keys.next());
            counter.reset();
        }
    }

    public void incrementCount(Class animalClass) {
        Counter counter = (Counter) counters.get(animalClass);
        if (counter == null) {
            counter = new Counter(animalClass.getName());
            counters.put(animalClass, counter);
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
        Iterator keys = counters.keySet().iterator();
        while (keys.hasNext()) {
            Counter info = (Counter) counters.get(keys.next());
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
