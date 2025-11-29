import java.util.HashMap;
import java.util.Iterator;

/**
 * Mantém estatísticas sobre a população presente no campo de simulação.
 * 
 * A classe registra quantos objetos de cada tipo existem no Field,
 * permitindo contabilizar espécies, atualizar números a cada passo
 * e verificar se a simulação ainda é viável.
 */
public class FieldStats {

    /** Mapeia cada classe para seu respectivo contador. */
    private HashMap<Class<? extends EntidadeSimulavel>, Counter> counters;
    private final HashMap<Class<? extends EntidadeSimulavel>, String> nomePT = new HashMap<>();

    /** Indica se as contagens atuais ainda refletem o estado do Field. */
    private boolean countsValid;

    public FieldStats() {
        counters = new HashMap<Class<? extends EntidadeSimulavel>, Counter>();
        countsValid = true;

        // Labels em português
        nomePT.put(Fox.class, "Raposa");
        nomePT.put(Rabbit.class, "Coelho");
        nomePT.put(Planta.class, "Planta");
        nomePT.put(Lago.class, "Lago");
        nomePT.put(Lobo.class, "Lobo");
        nomePT.put(Montanha.class, "Montanha");
    }

    /**
     * Retorna uma string simples contendo o nome e a quantidade
     * de cada tipo encontrado no campo.
     *
     * @param field campo da simulação
     * @return texto com estatísticas organizadas
     */
    public String getPopulationDetails(Field field) {
        StringBuffer buffer = new StringBuffer();

        if (!countsValid) {
            generateCounts(field);
        }
        
        Iterator<Class<? extends EntidadeSimulavel>> keys = counters.keySet().iterator();
        while (keys.hasNext()) {
            Counter info = counters.get(keys.next());
            buffer.append(info.getName());
            buffer.append(": ");
            buffer.append(info.getCount());
            buffer.append(' ');
        }
        return buffer.toString();
    }

    /**
     * Limpa as contagens, marcando-as como inválidas.
     * Deve ser chamado antes de recalcular estatísticas.
     */
    public void reset() {
        countsValid = false;
        Iterator<Class<? extends EntidadeSimulavel>> keys = counters.keySet().iterator();
        while (keys.hasNext()) {
            Counter counter = counters.get(keys.next());
            counter.reset();
        }
    }

    public void incrementCount(Class<? extends EntidadeSimulavel> aClass) {
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

    /**
     * Verifica se a simulação ainda contém mais de um tipo
     * de entidade com população maior que zero.
     *
     * @param field campo da simulação
     * @return true se houver mais de uma espécie viva
     */
    public boolean isViable(Field field) {
        int nonZero = 0;

        if (!countsValid) {
            generateCounts(field);
        }
        
        Iterator<Class<? extends EntidadeSimulavel>> keys = counters.keySet().iterator();
        while (keys.hasNext()) {
            Counter info = counters.get(keys.next());
            if (info.getCount() > 0) {
                nonZero++;
            }
        }
        return nonZero > 1;
    }

    /**
     * Recalcula as quantidades de todas as classes presentes no campo.
     *
     * @param field campo da simulação
     */
    private void generateCounts(Field field) {
        reset();

        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                EntidadeSimulavel obj = field.getObjectAt(row, col);
                if (obj != null) {
                    incrementCount(obj.getClass());
                }
            }
        }

        countsValid = true;
    }

    /**
     * Retorna uma versão simples do texto de estatísticas,
     * com o nome da classe e sua quantidade.
     *
     * @param field campo analisado
     * @return texto resumo da população
     */
    public String getPopulationText(Field field) {
        if (!countsValid) generateCounts(field);
        StringBuilder sb = new StringBuilder();

        for (Class<? extends EntidadeSimulavel> cls : counters.keySet()) {
            Counter c = counters.get(cls);

            // usa nome traduzido
            String nome = nomePT.getOrDefault(cls, cls.getSimpleName());

            sb.append(nome)
            .append(": ")
            .append(c.getCount())
            .append("   ");
        }
        return sb.toString();
    }


    /**
     * Retorna a lista de classes atualmente registradas nos contadores.
     * Usado pela legenda visual.
     *
     * @return lista contendo todas as classes contadas
     */
    public java.util.List<Class<? extends EntidadeSimulavel>> getOrderedClasses() {
        return new java.util.ArrayList<>(counters.keySet());
    }
}
