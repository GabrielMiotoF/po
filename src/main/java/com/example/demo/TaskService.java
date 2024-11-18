package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public Tarefa createTask(Tarefa task) {
        task.setCreationDate(LocalDate.now());
        task.setStatus("To Do");
        return taskRepository.save(task);
    }

    public Tarefa updateTask(Long id, Tarefa updatedTask) {
        Optional<Tarefa> optionalTask = taskRepository.findById(id);

        Tarefa existingTask = optionalTask.get();
        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setPriority(updatedTask.getPriority());
        existingTask.setStatus(updatedTask.getStatus());
        existingTask.setDueDate(updatedTask.getDueDate());

        return taskRepository.save(existingTask);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public List<Tarefa> getAllTasks() {
        return taskRepository.findAll();
    }

    public Map<String, List<Tarefa>> getTasksByColumn() {
        return taskRepository.findAll().stream()
                .sorted((t1, t2) -> t1.getPriority().compareTo(t2.getPriority())) // Ordena por prioridade
                .collect(Collectors.groupingBy(Tarefa::getStatus));
    }

    public List<Tarefa> getOverdueTasks() {
        LocalDate today = LocalDate.now();
        return taskRepository.findAll().stream()
                .filter(task -> task.getDueDate() != null && task.getDueDate().isBefore(today) && !"Done".equals(task.getStatus()))
                .collect(Collectors.toList());
    }

    public Tarefa moveTask(Long id) {
        Tarefa task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with id: " + id));

        switch (task.getStatus()) {
            case "To Do":
                task.setStatus("In Progress");
                break;
            case "In Progress":
                task.setStatus("Done");
                break;
            default:
                throw new IllegalStateException("Cannot move task with status: " + task.getStatus());
        }

        return taskRepository.save(task);
    }


}
