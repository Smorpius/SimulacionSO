package src;
import java.util.Scanner;


public class Main {
 public static void main(String[] args) {
 // Especificar el tamaño del marco de memoria
 int frameSize = 128; // Ahora podemos personalizar el tamaño del marco
 
 ProcessManager pm = new ProcessManager(frameSize);
 
 System.out.println("Simulador de Gestión de Procesos");
 System.out.println("Utilizando algoritmo Round Robin");
 System.out.println("Tamaño de marco de memoria: " + frameSize + " MB");
 
 // Configurar directamente el algoritmo Round Robin
 pm.setSchedulingAlgorithm("RR");
 
 // Establecer un quantum de 3 unidades
 int quantum = 3;
 pm.setQuantum(quantum);
 System.out.println("Quantum configurado: " + quantum);
 
 // Ejecutar la simulación
 pm.runSimulation(20, 30);
 }
}