import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// =================================================================================
// CLASE 1: Task (El objeto de la tarea)
// Nota: Se ha quitado la palabra "public" para que pueda estar en este archivo.
// =================================================================================
class Task {
    private int id;
    private String description;
    public boolean isCompleted; // CAMPO PÚBLICO INTENCIONAL para que SonarQube lo detecte
    private LocalDate dueDate;

    public Task(int id, String description, LocalDate dueDate) {
        this.id = id;
        this.description = description;
        this.isCompleted = false;
        this.dueDate = dueDate;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    @Override
    public String toString() {
        return "ID: " + id + ", Descripción: '" + description + '\'' +
                ", Completada: " + (isCompleted ? "Sí" : "No") +
                ", Fecha Límite: " + dueDate;
    }
}

// =================================================================================
// CLASE 2: TaskService (La lógica de negocio)
// Nota: Se ha quitado la palabra "public" para que pueda estar en este archivo.
// =================================================================================
class TaskService {
    private List<Task> tasks = new ArrayList<>();
    private int nextId = 1;

    public void addTask(String description) {
        if (description != null && !description.isEmpty()) {
            Task newTask = new Task(nextId++, description, LocalDate.now().plusDays(7));
            tasks.add(newTask);
            System.out.println("Tarea añadida con éxito."); // VIOLACIÓN: Usar System.out para logging
        }
    }

    public List<Task> getAllTasks() {
        return tasks;
    }

    // VIOLACIÓN: Código duplicado
    public Task findTaskById(int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }
        return null; // VIOLACIÓN: Retornar null
    }

    // VIOLACIÓN: Código duplicado
    public boolean deleteTask(int id) {
        Task taskToRemove = null;
        for (Task task : tasks) {
            if (task.getId() == id) { // Mismo bloque de búsqueda que en findTaskById
                taskToRemove = task;
                break;
            }
        }
        if (taskToRemove != null) {
            tasks.remove(taskToRemove);
            return true;
        }
        return false;
    }

    // VIOLACIÓN: Método con alta complejidad ciclomática
    public String getTaskPriority(int id) {
        Task task = findTaskById(id);
        if (task != null) {
            if (task.isCompleted()) {
                return "Completada";
            } else if (task.getDueDate().isBefore(LocalDate.now())) {
                return "Urgente - Vencida";
            } else if (task.getDueDate().isEqual(LocalDate.now())) {
                return "Alta - Vence Hoy";
            } else if (task.getDescription().toLowerCase().contains("importante")) {
                return "Alta";
            } else {
                return "Normal";
            }
        }
        return "No encontrada";
    }

    // VIOLACIÓN: Un método sin usar (código muerto)
    private void unusedMethod() {
        System.out.println("Este método nunca es llamado.");
    }
}

// =================================================================================
// CLASE 3: Main (El punto de entrada de la aplicación)
// Esta es la ÚNICA clase pública, por eso el archivo se llama Main.java.
// =================================================================================
public class Main {
    public static void main(String[] args) {
        TaskService service = new TaskService();
        Scanner scanner = new Scanner(System.in); // VIOLACIÓN: Scanner no se cierra (resource leak)
        boolean exit = false;

        while (!exit) {
            System.out.println("\n--- Menú de Tareas ---");
            System.out.println("1. Añadir tarea");
            System.out.println("2. Ver todas las tareas");
            System.out.println("3. Marcar tarea como completada");
            System.out.println("4. Eliminar tarea");
            System.out.println("5. Salir");
            System.out.print("Seleccione una opción: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Introduce la descripción de la tarea: ");
                    String desc = scanner.nextLine();
                    service.addTask(desc);
                    break;
                case 2:
                    System.out.println("\n--- Lista de Tareas ---");
                    service.getAllTasks().forEach(System.out::println);
                    break;
                case 3:
                    System.out.print("Introduce el ID de la tarea a completar: ");
                    int idToComplete = scanner.nextInt();
                    Task taskToComplete = service.findTaskById(idToComplete);
                    if (taskToComplete != null) {
                        taskToComplete.setCompleted(true);
                        System.out.println("Tarea completada.");
                    } else {
                        System.out.println("Error: Tarea no encontrada.");
                    }
                    break;
                case 4:
                    System.out.print("Introduce el ID de la tarea a eliminar: ");
                    int idToDelete = scanner.nextInt();
                    if (service.deleteTask(idToDelete)) {
                        System.out.println("Tarea eliminada.");
                    } else {
                        System.out.println("Error: Tarea no encontrada.");
                    }
                    break;
                case 5:
                    exit = true;
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opción no válida. Inténtalo de nuevo.");
            }
        }
        // El scanner.close(); debería ir aquí para solucionar el resource leak.
    }
}