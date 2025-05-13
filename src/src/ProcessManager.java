package src;
import java.util.*;
import java.io.IOException;  // Añadir este import


public class ProcessManager {
 private List<ProcessControlBlock> readyQueue = new LinkedList<>();
 private List<ProcessControlBlock> waitingQueue = new LinkedList<>();
 private List<ProcessControlBlock> terminatedProcesses = new ArrayList<>();
 private ProcessControlBlock runningProcess = null;
 private Map<String, Resource> resources = new HashMap<>();
 private int nextPid = 1;
 private String schedulingAlgorithm = "FCFS";
 private int quantum = 2;
 private Random random = new Random();
 private int simulationTime = 0;
 private int totalMemoryFrames = 64;
 private boolean[] memoryFrameAvailability = new boolean[totalMemoryFrames];
 private int frameSize = 64;


// Modificar el constructor
public ProcessManager(int frameSize) {
 resources.put("CPU", new Resource("CPU", 1));
 resources.put("Memory", new Resource("Memory", 4096));
 this.frameSize = frameSize;
 Arrays.fill(memoryFrameAvailability, false);
}

// Mantener el constructor predeterminado para compatibilidad
public ProcessManager() {
 resources.put("CPU", new Resource("CPU", 1));
 resources.put("Memory", new Resource("Memory", 4096));
 Arrays.fill(memoryFrameAvailability, false);
}


public void runSimulation(int maxProcesses, int maxSimulationTime) {
 Scanner scanner = new Scanner(System.in);
 System.out.println("Presione 'm' en cualquier momento para mostrar el menú de opciones.");
 
 while (simulationTime < maxSimulationTime && terminatedProcesses.size() < maxProcesses) {
   try {
     // Verificar si hay entrada de teclado disponible
     if (System.in.available() > 0) {
       char key = (char) System.in.read();
       if (key == 'm' || key == 'M') {
         showInteractiveMenu(scanner);
       }
       // Consumir cualquier otro carácter pendiente
       while (System.in.available() > 0) {
         System.in.read();
       }
     }
   } catch (IOException e) {
     System.out.println("Error al leer entrada: " + e.getMessage());
   }
   
   simulationStep();
   try {
     Thread.sleep(1000);
   } catch (InterruptedException e) {
     Thread.currentThread().interrupt();
   }
   simulationTime++;
 }
 System.out.println("\n--- Simulación Terminada ---");
 mostrarEstado();
 scanner.close();
}

private void showInteractiveMenu(Scanner scanner) {
 System.out.println("\n=== MENÚ DE CONTROL DE PROCESOS ===");
 System.out.println("1. Suspender un proceso");
 System.out.println("2. Finalizar un proceso");
 System.out.println("3. Continuar simulación");
 System.out.print("Seleccione una opción: ");
 
 int option = scanner.nextInt();
 switch (option) {
   case 1:
     suspendProcessMenu(scanner);
     break;
   case 2:
     terminateProcessMenu(scanner);
     break;
   case 3:
     System.out.println("Continuando simulación...");
     break;
   default:
     System.out.println("Opción no válida.");
 }
}

private void suspendProcessMenu(Scanner scanner) {
 System.out.println("\nProcesos disponibles para suspender:");
 
 // Mostrar proceso en ejecución si existe
 if (runningProcess != null) {
   System.out.println("0. Proceso en ejecución: " + runningProcess);
 }
 
 // Mostrar procesos en cola de listos
 for (int i = 0; i < readyQueue.size(); i++) {
   System.out.println((i + 1) + ". " + readyQueue.get(i));
 }
 
 System.out.print("Seleccione el número del proceso a suspender (-1 para cancelar): ");
 int selection = scanner.nextInt();
 
 if (selection == -1) {
   return;
 } else if (selection == 0 && runningProcess != null) {
   suspendProcess(runningProcess);
 } else if (selection > 0 && selection <= readyQueue.size()) {
   suspendProcess(readyQueue.get(selection - 1));
 } else {
   System.out.println("Selección no válida.");
 }
}

private void terminateProcessMenu(Scanner scanner) {
 System.out.println("\nProcesos disponibles para finalizar:");
 
 // Mostrar proceso en ejecución si existe
 if (runningProcess != null) {
   System.out.println("0. Proceso en ejecución: " + runningProcess);
 }
 
 // Mostrar procesos en cola de listos
 for (int i = 0; i < readyQueue.size(); i++) {
   System.out.println((i + 1) + ". " + readyQueue.get(i));
 }
 
 // Mostrar procesos en cola de espera
 for (int i = 0; i < waitingQueue.size(); i++) {
   System.out.println((i + readyQueue.size() + 1) + ". " + waitingQueue.get(i) + " (en espera)");
 }
 
 System.out.print("Seleccione el número del proceso a finalizar (-1 para cancelar): ");
 int selection = scanner.nextInt();
 
 if (selection == -1) {
   return;
 } else if (selection == 0 && runningProcess != null) {
   terminateProcess(runningProcess, "Terminado por usuario");
 } else if (selection > 0 && selection <= readyQueue.size()) {
   terminateProcess(readyQueue.get(selection - 1), "Terminado por usuario");
 } else if (selection > readyQueue.size() && selection <= readyQueue.size() + waitingQueue.size()) {
   terminateProcess(waitingQueue.get(selection - readyQueue.size() - 1), "Terminado por usuario");
 } else {
   System.out.println("Selección no válida.");
 }
}


 public void simulationStep() {
 if (random.nextDouble() < 0.2) {
 createRandomProcess();
 }
 schedule();
 dispatch();
 mostrarEstado();
 }


 public void createRandomProcess() {
 int priority = random.nextInt(5) + 1;
 int cpuBurst = random.nextInt(10) + 1;
 int memoryRequired = random.nextInt(512) + 64;
 createProcess(priority, cpuBurst, memoryRequired);
 }


 public ProcessControlBlock createProcess(int priority, int cpuBurst, int memoryRequired) {
 ProcessControlBlock process = new ProcessControlBlock(nextPid++, "READY", priority, cpuBurst, memoryRequired);
 int requiredFrames = (int) Math.ceil((double) memoryRequired / frameSize);
 List<Integer> allocatedFrames = allocateMemoryFrames(requiredFrames);
 if (allocatedFrames != null) {
 process.setMemoryFrames(allocatedFrames);
 readyQueue.add(process);
 System.out.println("Tiempo " + simulationTime + ": Proceso " + process.getPid() + " creado.");
 return process;
 } else {
 System.out.println("Tiempo " + simulationTime + ": No hay suficientes marcos de memoria para crear el proceso " + process.getPid());
 return null;
 }
 }


 public void terminateProcess(ProcessControlBlock process, String reason) {
 process.setState("TERMINATED");
 terminatedProcesses.add(process);
 if (process == runningProcess) {
 runningProcess = null;
 resources.get("CPU").release(1);
 }
 releaseMemoryFrames(process.getMemoryFrames());
 System.out.println("Tiempo " + simulationTime + ": Proceso " + process.getPid() + " terminado. Razón: " + reason);
 }


 public void suspendProcess(ProcessControlBlock process) {
 if (process.getState().equals("RUNNING")) {
 process.setState("WAITING");
 runningProcess = null;
 resources.get("CPU").release(1);
 waitingQueue.add(process);
 System.out.println("Tiempo " + simulationTime + ": Proceso " + process.getPid() + " suspendido.");
 } else if (process.getState().equals("READY")) {
 process.setState("WAITING");
 readyQueue.remove(process);
 waitingQueue.add(process);
 System.out.println("Tiempo " + simulationTime + ": Proceso " + process.getPid() + " suspendido.");
 } else {
 System.out.println("Tiempo " + simulationTime + ": No se puede suspender el proceso " + process.getPid() + " porque está en estado " + process.getState());
 }
 }


 public void resumeProcess(ProcessControlBlock process) {
 if (process.getState().equals("WAITING")) {
 process.setState("READY");
 waitingQueue.remove(process);
 readyQueue.add(process);
 System.out.println("Tiempo " + simulationTime + ": Proceso " + process.getPid() + " reanudado.");
 } else {
 System.out.println("Tiempo " + simulationTime + ": No se puede reanudar el proceso " + process.getState());
 }
 }


 public void schedule() {
 switch (schedulingAlgorithm) {
 case "FCFS":
 fcfsSchedule();
 break;
 case "SJF":
 sjfSchedule();
 break;
 case "RR":
 roundRobinSchedule();
 break;
 case "Priority":
 prioritySchedule();
 break;
 }
 }


 private void fcfsSchedule() {
 if (!readyQueue.isEmpty() && runningProcess == null && resources.get("CPU").getAvailableUnits() > 0) {
 runningProcess = readyQueue.remove(0);
 runningProcess.setState("RUNNING");
 resources.get("CPU").allocate(1);
 System.out.println("Tiempo " + simulationTime + ": Ejecutando proceso " + runningProcess.getPid() + " (FCFS)");
 }
 }


 private void sjfSchedule() {
 if (!readyQueue.isEmpty() && runningProcess == null && resources.get("CPU").getAvailableUnits() > 0) {
 ProcessControlBlock shortest = readyQueue.get(0);
 int shortestBurst = shortest.getCpuBurst();
 int shortestIndex = 0;
 for (int i = 1; i < readyQueue.size(); i++) {
 if (readyQueue.get(i).getCpuBurst() < shortestBurst) {
 shortest = readyQueue.get(i);
 shortestBurst = shortest.getCpuBurst();
 shortestIndex = i;
 }
 }
 runningProcess = readyQueue.remove(shortestIndex);
 runningProcess.setState("RUNNING");
 resources.get("CPU").allocate(1);
 System.out.println("Tiempo " + simulationTime + ": Ejecutando proceso " + runningProcess.getPid() + " (SJF)");
 }
 }


 private void roundRobinSchedule() {
    // Si hay un proceso en ejecución, verificamos si ha alcanzado su quantum
    if (runningProcess != null) {
        // En el RR el proceso actual ejecuta hasta que termine o se agote su quantum
        // El dispatch() ya se encargará de decrementar el cpuBurst en 1
        // Por lo tanto, aquí solo verificamos si debe volver a la cola
        if (runningProcess.getCpuBurst() > 0) {
            // El proceso sigue necesitando CPU, lo movemos al final de la cola
            runningProcess.setState("READY");
            readyQueue.add(runningProcess);
            System.out.println("Tiempo " + simulationTime + ": Proceso " + runningProcess.getPid() + 
                              " ha consumido su quantum, regresa a cola de listos");
            resources.get("CPU").release(1);
            runningProcess = null;
        }
    }

    // Si no hay ningún proceso en ejecución, traemos el siguiente de la cola de listos
    if (runningProcess == null && !readyQueue.isEmpty()) {
        runningProcess = readyQueue.remove(0);
        runningProcess.setState("RUNNING");
        resources.get("CPU").allocate(1);
        System.out.println("Tiempo " + simulationTime + ": Ejecutando proceso " + runningProcess.getPid() + " (Round Robin)");
    }
}


 private void prioritySchedule() {
 if (!readyQueue.isEmpty() && runningProcess == null && resources.get("CPU").getAvailableUnits() > 0) {
 ProcessControlBlock highestPriority = readyQueue.get(0);
 int highest = highestPriority.getPriority();
 int highestIndex = 0;
 for (int i = 1; i < readyQueue.size(); i++) {
 if (readyQueue.get(i).getPriority() < highest) {
 highestPriority = readyQueue.get(i);
 highest = highestPriority.getPriority();
 highestIndex = i;
 }
 }
 runningProcess = readyQueue.remove(highestIndex);
 runningProcess.setState("RUNNING");
 resources.get("CPU").allocate(1);
 System.out.println("Tiempo " + simulationTime + ": Ejecutando proceso " + runningProcess.getPid() + " (Prioridad)");
 }
 }


 public void dispatch() {
    if (runningProcess != null) {
        runningProcess.setCpuBurst(runningProcess.getCpuBurst() - 1);
        System.out.println("Tiempo " + simulationTime + ": Proceso " + runningProcess.getPid() + 
                         " - CPU Burst restante: " + runningProcess.getCpuBurst());
        
        // Si el proceso ha terminado
        if (runningProcess.getCpuBurst() <= 0) {
            terminateProcess(runningProcess, "Completado");
            runningProcess = null;
        }
        // En el caso de Round Robin, no hacemos nada aquí, ya que roundRobinSchedule() 
        // se encarga de la gestión del quantum
    }
}


 public List<Integer> allocateMemoryFrames(int requiredFrames) {
 List<Integer> allocatedFrames = new ArrayList<>();
 int availableFrames = 0;
 for (int i = 0; i < memoryFrameAvailability.length; i++) {
 if (!memoryFrameAvailability[i]) {
 availableFrames++;
 }
 }
 if (availableFrames >= requiredFrames) {
 int framesAllocated = 0;
 for (int i = 0; i < memoryFrameAvailability.length; i++) {
 if (!memoryFrameAvailability[i]) {
 memoryFrameAvailability[i] = true;
 allocatedFrames.add(i);
 framesAllocated++;
 if (framesAllocated == requiredFrames) {
 break;
 }
 }
 }
 return allocatedFrames;
 }
 return null;
 }


 public void releaseMemoryFrames(List<Integer> frames) {
 if (frames != null) {
 for (int frame : frames) {
 memoryFrameAvailability[frame] = false;
 }
 }
 }


 public void mostrarEstado() {
 System.out.println("\n--- Tiempo: " + simulationTime + " ---");
 System.out.println("Algoritmo: " + schedulingAlgorithm);
 System.out.println("Cola de Listos: " + readyQueue);
 System.out.println("Cola de Espera: " + waitingQueue);
 System.out.println("Proceso en Ejecución: " + (runningProcess != null ? runningProcess : "Ninguno"));
 if (runningProcess != null) {
 System.out.println("  - PID: " + runningProcess.getPid() +
 ", CPU Burst Restante: " + runningProcess.getCpuBurst());
 }
 System.out.println("Procesos Terminados: " + terminatedProcesses.size());
 System.out.println("CPU Disponible: " + resources.get("CPU").getAvailableUnits());
 System.out.println("Memoria Disponible: " + countAvailableMemory() + " MB");
 System.out.println("Marcos de Memoria Disponibles: " + countAvailableFrames() +
 " / " + totalMemoryFrames);
 }


 public int countAvailableMemory() {
 int availableFrames = countAvailableFrames();
 return availableFrames * frameSize;
 }


 public int countAvailableFrames() {
 int count = 0;
 for (boolean available : memoryFrameAvailability) {
 if (!available) {
 count++;
 }
 }
 return count;
 }


 public void setSchedulingAlgorithm(String schedulingAlgorithm) {
 this.schedulingAlgorithm = schedulingAlgorithm;
 }


 public void setQuantum(int quantum) {
 this.quantum = quantum;
 }
}