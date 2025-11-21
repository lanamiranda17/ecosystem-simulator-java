import java.awt.*;
import javax.swing.*;
import java.util.HashMap;

/**
 * Interface gráfica principal da simulação.
 * 
 * Responsável por:
 * - Exibir o campo onde os animais se movem.
 * - Mostrar o passo atual da simulação.
 * - Exibir estatísticas da população.
 * - Renderizar uma legenda visual com as cores de cada classe.
 */
public class SimulatorView extends JFrame {

    private static final Color EMPTY_COLOR = Color.white;
    private static final Color UNKNOWN_COLOR = Color.gray;
    private final String STEP_PREFIX = "Step: ";
    
    private JLabel stepLabel;
    private JLabel populationLabel;
    
    /** Painel inferior contendo a legenda visual da simulação. */
    private JPanel legendPanel;    
    
    private FieldView fieldView;
    private HashMap<Class, Color> colors;
    private FieldStats stats;

    /**
     * Constrói a janela da simulação, inicializando a área do campo,
     * a legenda e o painel de estatísticas.
     *
     * @param height altura do campo de simulação
     * @param width  largura do campo de simulação
     */
    public SimulatorView(int height, int width) {
        stats = new FieldStats();
        colors = new HashMap<>();

        setTitle("Fox and Rabbit Simulation");

        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        populationLabel = new JLabel("", JLabel.CENTER);

        fieldView = new FieldView(height, width);

        // Painel da legenda
        legendPanel = new JPanel();
        legendPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        legendPanel.setBackground(Color.WHITE);

        // Layout principal
        Container contents = getContentPane();
        contents.setLayout(new BorderLayout());

        contents.add(stepLabel, BorderLayout.NORTH);
        contents.add(fieldView, BorderLayout.CENTER);

        // Painel inferior: estatísticas + legenda
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(populationLabel, BorderLayout.NORTH);
        bottomPanel.add(legendPanel, BorderLayout.SOUTH);

        contents.add(bottomPanel, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }

    public void setColor(Class<?> cls, Color color) {
        colors.put(cls, color);
    }

    private Color getColor(Class<?> cls) {
        return colors.getOrDefault(cls, UNKNOWN_COLOR);
    }

    /**
     * Método auxiliar usado por FieldStats para consultar a cor de um tipo.
     *
     * @param cls classe consultada
     * @return cor correspondente
     */
    public Color getColorFromOutside(Class<?> cls) {
        return getColor(cls);
    }

    /**
     * Atualiza toda a interface gráfica:
     * - redesenha a matriz de células
     * - recalcula estatísticas
     * - atualiza a legenda visual
     *
     * @param step passo atual da simulação
     * @param field campo contendo os objetos da simulação
     */
    public void showStatus(int step, Field field) {

        if (!isVisible()) setVisible(true);

        stepLabel.setText(STEP_PREFIX + step);

        stats.reset();
        fieldView.preparePaint();

        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {

                Object obj = field.getObjectAt(row, col);

                if (obj != null) {
                    stats.incrementCount(obj.getClass());
                    fieldView.drawMark(col, row, getColor(obj.getClass()));
                } else {
                    fieldView.drawMark(col, row, EMPTY_COLOR);
                }
            }
        }

        stats.countFinished();

        // Atualiza texto simples das estatísticas
        populationLabel.setText(stats.getPopulationText(field));

        // Atualiza legenda visual
        updateLegendPanel(field);

        fieldView.repaint();
    }

    /**
     * Testa se a simulação ainda contém mais de um tipo ativo.
     *
     * @param field campo da simulação
     * @return true se ainda houver dois ou mais tipos vivos
     */
    public boolean isViable(Field field) {
        return stats.isViable(field);
    }

    /**
     * Atualiza a legenda visual exibindo:
     * - um quadrado colorido
     * - o nome da classe correspondente
     *
     * Mantém a ordem definida por FieldStats.
     *
     * @param field campo usado para determinar quais classes aparecem
     */
    private void updateLegendPanel(Field field) {
        legendPanel.removeAll();

        for (Class<?> cls : stats.getOrderedClasses()) {

            Color col = getColor(cls);
            
            JPanel square = new JPanel();
            square.setPreferredSize(new Dimension(12, 12));
            square.setBackground(col);
            square.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

            JLabel lbl = new JLabel(" " + cls.getSimpleName());

            JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            item.setBackground(Color.WHITE);
            item.add(square);
            item.add(lbl);

            legendPanel.add(item);
        }

        legendPanel.revalidate();
        legendPanel.repaint();
    }

    /**
     * Painel responsável por desenhar visualmente o campo.
     * Renderiza cada célula como um quadrado colorido.
     */
    private class FieldView extends JPanel {
        private final int GRID_VIEW_SCALING_FACTOR = 6;

        private int gridWidth, gridHeight;
        private int xScale, yScale;
        private Dimension size;
        private Graphics g;
        private Image fieldImage;

        public FieldView(int height, int width) {
            gridHeight = height;
            gridWidth = width;
            size = new Dimension(0, 0);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(
                gridWidth * GRID_VIEW_SCALING_FACTOR,
                gridHeight * GRID_VIEW_SCALING_FACTOR
            );
        }

        /**
         * Prepara o buffer de imagem para renderização.
         * Deve ser chamado antes de desenhar cada frame.
         */
        public void preparePaint() {
            if (!size.equals(getSize())) {
                size = getSize();
                fieldImage = createImage(size.width, size.height);
                g = fieldImage.getGraphics();

                xScale = Math.max(size.width / gridWidth, GRID_VIEW_SCALING_FACTOR);
                yScale = Math.max(size.height / gridHeight, GRID_VIEW_SCALING_FACTOR);
            }
        }

        /**
         * Desenha uma célula colorida no campo.
         *
         * @param x coluna da célula
         * @param y linha da célula
         * @param color cor usada na renderização
         */
        public void drawMark(int x, int y, Color color) {
            g.setColor(color);
            g.fillRect(x * xScale, y * yScale, xScale - 1, yScale - 1);
        }

        @Override
        public void paintComponent(Graphics g) {
            if (fieldImage != null) {
                g.drawImage(fieldImage, 0, 0, null);
            }
        }
    }
}
