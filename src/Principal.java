/**
 * Classe principal da aplicação.
 * Responsável por criar o simulador e iniciar a execução.
 */
public class Principal{
  public static void main(String[] args){
    Simulator simulator = new Simulator();
    simulator.runLongSimulation();
    //simulator.simulate(300);
  }
}
