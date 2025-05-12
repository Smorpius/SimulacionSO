package src;
import java.util.Scanner;


public class Main {
 public static void main(String[] args) {
 ProcessManager pm = new ProcessManager();
 Scanner scanner = new Scanner(System.in);


 System.out.println("Simulador de Gestión de Procesos");
 System.out.println("Elige el algoritmo de planificación:");
 System.out.println("1. FCFS");
 System.out.println("2. SJF");
 System.out.println("3. Round Robin");
 System.out.println("4. Prioridad");
 System.out.print("Ingresa el número del algoritmo: ");
 int choice = scanner.nextInt();
 scanner.nextLine();


 switch (choice) {
 case 1:
 pm.setSchedulingAlgorithm("FCFS");
 break;
 case 2:
 pm.setSchedulingAlgorithm("SJF");
 break;
 case 3:
 pm.setSchedulingAlgorithm("RR");
 System.out.print("Ingresa el quantum para Round Robin: ");
 int quantum = scanner.nextInt();
 scanner.nextLine();
 pm.setQuantum(quantum);
 break;
 case 4:
 pm.setSchedulingAlgorithm("Priority");
 break;
 default:
 System.out.println("Algoritmo no válido. Usando FCFS por defecto.");
 pm.setSchedulingAlgorithm("FCFS");
 }


 pm.runSimulation(20, 30);
 }
}