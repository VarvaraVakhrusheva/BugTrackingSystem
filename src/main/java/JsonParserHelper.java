import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class JsonParserHelper {
    private static ObjectMapper mapper = new ObjectMapper();

    public static void initUserMap(JSONArray users, Map<String, User> usersMap) throws JsonProcessingException {
        Iterator<Object> iterator = users.iterator();
        while (iterator.hasNext()) {
            User user = mapper.readValue(iterator.next().toString(), User.class);
            usersMap.put(user.getName(), user);
        }
    }

    public static void initProjectMap(JSONArray projects, Map<String, Project> projectsMap) throws JsonProcessingException {
        Iterator<Object> iterator = projects.iterator();
        while (iterator.hasNext()) {
            Project project = mapper.readValue(iterator.next().toString(), Project.class);
            projectsMap.put(project.getProjectName(), project);
        }
    }

    public static void initTaskMap(JSONArray tasks, Map<String, Task> tasksMap) throws JsonProcessingException {
        Iterator<Object> iterator = tasks.iterator();
        while (iterator.hasNext()) {
            Task task = mapper.readValue(iterator.next().toString(), Task.class);
            if (task.getUserKey() != null && !task.getUserKey().isEmpty() && task.getProject() != null &&
                    !task.getProject().isEmpty()) {
                tasksMap.put(task.getName(), task);
            } else {
                throw new RuntimeException("Попытка добавить задание без исполнителя и/или вне проекта.");
            }
        }
    }

    static void saveFile(String path, Map<String, User> usersMap, Map<String, Project> projectMap,
                         Map<String, Task> taskMap) {
        JSONObject json = new JSONObject();
        if (!usersMap.isEmpty()) {
            JSONArray usersArray = new JSONArray();
            for (User user : usersMap.values()) {
                try {
                    usersArray.add(mapper.writeValueAsString(user));
                } catch (IOException e) {
                    System.err.println("Ошибка при сохранении файла. Проблема с пользователем.");
                    e.printStackTrace();
                }
            }
            json.put("users", usersArray);
        }
        if (!projectMap.isEmpty()) {
            JSONArray projectsArray = new JSONArray();
            for (Project project : projectMap.values()) {
                try {
                    projectsArray.add(mapper.writeValueAsString(project));
                } catch (IOException e) {
                    System.err.println("Ошибка при сохранении файла. Проблема с проектом.");
                    e.printStackTrace();
                }
            }
            json.put("projects", projectsArray);
        }
        if (!taskMap.isEmpty()) {
            JSONArray taskArray = new JSONArray();
            for (Task task : taskMap.values()) {
                try {
                    taskArray.add(mapper.writeValueAsString(task));
                } catch (IOException e) {
                    System.err.println("Ошибка при сохранении файла. Проблема с задачей.");
                    e.printStackTrace();
                }
            }
            json.put("tasks", taskArray);
        }
        try (FileWriter file = new FileWriter(path)) {
            file.write(json.toJSONString());
            file.flush();
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении файла.");
            e.printStackTrace();
        }
    }
}
