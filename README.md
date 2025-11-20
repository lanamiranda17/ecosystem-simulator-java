# **Simulador de Raposas e Coelhos**

## **Autores**
- Arthur Soares Marques
- João Vitor Givisiez Lessa
- Lana da Silva Miranda
- Pyetro Augusto Malaquias

### **Disciplina**: GAC106 – Práticas de Programação Orientada a Objetos

---

## **Objetivo**
Este trabalho visa praticar conceitos de **Programação Orientada a Objetos** (POO) com a implementação de um simulador de **Raposas e Coelhos**, onde o ambiente simula a interação predador/presa. A proposta é expandir a simulação para incluir novos atores, condições climáticas e restrições no movimento, além de melhorar a **interface gráfica (GUI)**.

---

## **Tecnologias Utilizadas**
- **Java**: Linguagem de programação principal.
- **Swing**: Biblioteca gráfica para a interface do usuário.
- **Java Collections**: Para gerenciar a simulação e os objetos.

---

## **Estrutura do Projeto**
- **Principal.java**: Inicia a simulação.
- **Simulator.java**: Controla o ciclo de vida da simulação.
- **SimulatorView.java**: Exibe a simulação no GUI.
- **Field.java**: Representa o campo de simulação.
- **Animal.java** (e subclasses como `Rabbit`, `Fox`, `Lobo`): Modela os animais.
- **Lago.java**, **Montanha.java**: Representam obstáculos no campo.
- **Location.java**: Define a localização dos objetos.
- **Clima.java**: Controla as condições climáticas.

---

## **Como Executar**
1. Clone o repositório:
    `git clone <https://github.com/ArthurDp78/ProjetoPPOO>`

2. Compile o código:
    `javac *.java`

3. Execute a simulação:
    `java Principal`

---
