import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static Map<String, User> usersMap = new HashMap<>();
    private static Map<String, Project> projectsMap = new HashMap<>();
    private static Map<String, Task> tasksMap = new HashMap<>();
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static final String DEFAULT_PATH = "src/main/java/json.json";

    public static void main(String[] args) {
        JSONParser parser = new JSONParser();
        try {
            System.out.println("Введите адрес файла с данными:");
            String path = reader.readLine();
            if (path == null || path.isEmpty()) {
                System.out.println("Вы не ввели адрес файла. Используется стандартный файл, хранящийся по адресу: " +
                        DEFAULT_PATH);
                path = DEFAULT_PATH;
            }
            Object obj;
            try {
                obj = parser.parse(new FileReader(path));
            } catch (FileNotFoundException e) {
                System.out.println("Указан неверный адрес. Используется стандартный файл, хранящийся по адресу: " +
                        DEFAULT_PATH);
                path = DEFAULT_PATH;
                obj = parser.parse(new FileReader(path));
            }
            JSONObject jsonObject = (JSONObject) obj;

            JSONArray users = (JSONArray) jsonObject.get("users");
            JSONArray projects = (JSONArray) jsonObject.get("projects");
            JSONArray tasks = (JSONArray) jsonObject.get("tasks");

            JsonParserHelper.initUserMap(users, usersMap);
            JsonParserHelper.initProjectMap(projects, projectsMap);
            JsonParserHelper.initTaskMap(tasks, tasksMap);

            String s = "";
            while (!s.equals("q")) {
                switch (s) {
                    case "1":
                        getAllUsers();
                        break;
                    case "2":
                        getAllProjects();
                        break;
                    case "3":
                        System.out.println("Введите название проекта, список задач которого хотите получить");
                        String project = reader.readLine();
                        getTasksByProject(project);
                        break;
                    case "4":
                        System.out.println("Введите имя исполнителя, список задач которого хотите получить");
                        String user = reader.readLine();
                        getTasksByUser(user);
                        break;
                    case "5"://new user
                        createNewUser();
                        break;
                    case "6": //new project
                        createNewProject();
                        break;
                    case "7"://new task
                        createTask();
                        break;
                    case "8"://delete user
                        deleteUser();
                        break;
                    case "9"://delete project
                        deleteProject();
                        break;
                    case "10"://delete task
                        deleteTask();
                        break;
                    default:
                        break;
                }
                System.out.println("Если вы хотите получить список всех пользователей, введите 1 \n" +
                        "Если вы хотите получить список всех проектов, введите 2 \n" +
                        "Если вы хотите получить список всех задач в проекте, введите 3 \n" +
                        "Если вы хотите получить список всех задач, назначенных на конкретного исполнителя, введите 4 \n" +
                        "Если вы хотите создать нового пользователя, введите 5 \n" +
                        "Если вы хотите создать новый проект, введите 6 \n" +
                        "Если вы хотите создать новую задачу, введите 7 \n" +
                        "Если вы хотите удалить пользователя, введите 8 \n" +
                        "Если вы хотите удалить проект, введите 9 \n" +
                        "Если вы хотите удалить задачу, введите 10 \n" +
                        "Для выхода введите q \n");
                s = reader.readLine();
            }
            JsonParserHelper.saveFile(path, usersMap, projectsMap, tasksMap);
        } catch (Exception e) {
            System.out.println("Файл с данными поврежден или не существует.");
            e.printStackTrace();
        }
    }

    private static void deleteTask() throws IOException {
        System.out.println("Введите название задачи, которую хотите удалить");
        Task taskToDelete = tasksMap.get(getTaskFromConsoleToDelete());
        System.out.println("Вы уверены, что хотите удалить задачу " + taskToDelete.getName() +
                "? Да - нажмите 1.");
        if (reader.readLine().equals("1")) {
            usersMap.get(taskToDelete.getUserKey()).getTasks().remove(taskToDelete.getName());
            projectsMap.get(taskToDelete.getProject()).getTasks().remove(taskToDelete.getName());
            tasksMap.remove(taskToDelete.getName());
            System.out.println("Задача удалена.");
        }
    }

    private static void deleteProject() throws IOException {
        System.out.println("Введите название проекта, который хотите удалить");
        Project projectToDelete = projectsMap.get(getProjectFromConsole());
        if (!projectToDelete.getTasks().isEmpty()) {
            System.out.println("Проект невозможно удалить. В нем есть незавершенные задачи.");
        } else {
            System.out.println("Вы уверены, что хотите удалить проект " + projectToDelete.getProjectName() +
                    "? Да - нажмите 1.");
            if (reader.readLine().equals("1")) {
                projectsMap.remove(projectToDelete.getProjectName());
                System.out.println("Проект удален.");
            }
        }
    }

    private static void deleteUser() throws IOException {
        System.out.println("Введите имя пользователя, которого хотите удалить");
        User userToDelete = usersMap.get(getUserFromConsole());
        if (!userToDelete.getTasks().isEmpty()) {
            System.out.println("Пользователя невозможно удалить. У него есть незавершенные задачи.");
        } else {
            System.out.println("Вы уверены, что хотите удалить пользователя " + userToDelete.getName() +
            "? Да - нажмите 1.");
            if (reader.readLine().equals("1")) {
                usersMap.remove(userToDelete.getName());
                System.out.println("Пользователь удален.");
            }
        }
    }

    private static void createNewProject() throws IOException {
        System.out.println("Введите название проекта");
        String projectName = reader.readLine();
        while (projectsMap.containsKey(projectName)) {
            System.out.println("Проект с таким названием уже существует. Введите другое название.");
            projectName = reader.readLine();
        }
        projectsMap.put(projectName, new Project(projectName));
        System.out.println("Проект создан.");
    }

    private static void createNewUser() throws IOException {
        System.out.println("Введите имя пользователя");
        String userName = reader.readLine();
        while (usersMap.containsKey(userName)) {
            System.out.println("Пользователь с таким именем уже существует. Введите другое имя.");
            userName = reader.readLine();
        }
        usersMap.put(userName, new User(userName));
        System.out.println("Пользователь добавлен.");
    }

    private static void createTask() throws IOException {
        System.out.println("Введите название проекта, к которому относится задача");
        String project = getProjectFromConsole();
        System.out.println("Введите название задачи");
        String name = getTaskFromConsole();
        System.out.println("Введите тему задачи");
        String theme = reader.readLine();
        System.out.println("Введите тип задачи");
        String type = reader.readLine();
        System.out.println("Введите приоритет задачи (1 - низкий, 2 - средний, 3 - высокий):");
        int priority = Integer.parseInt(reader.readLine());
        while (priority != 1 && priority != 2 && priority != 3) {
            System.out.println("Неверное значение. Введите приоритет задачи в диапазоне 1-3 (от низкого к высокому):");
            priority = Integer.parseInt(reader.readLine());
        }
        System.out.println("Введите имя исполнителя");
        String userKey = getUserFromConsole();
        System.out.println("Введите описание задачи");
        String description = reader.readLine();
        Task t = new Task();
        t.setProject(project);
        t.setName(name);
        t.setTheme(theme);
        t.setType(type);
        t.setPriority(priority);
        t.setUserKey(userKey);
        t.setDescription(description);
        tasksMap.put(name, t);
        User u = usersMap.get(userKey);
        u.getTasks().add(name);
        Project p = projectsMap.get(project);
        p.getTasks().add(name);
        System.out.println("Задача создана.");
    }

    private static String getProjectFromConsole() throws IOException {
        String project = reader.readLine();
        while (!projectsMap.containsKey(project)) {
            System.out.println("Такого проекта не существует. Выберите проект из существующих:");
            for (String p : projectsMap.keySet()) {
                System.out.println(p);
            }
            project = reader.readLine();
        }
        return project;
    }

    private static void getAllUsers() {
        for (String s : usersMap.keySet()) {
            System.out.println(s);
        }
    }

    private static void getAllProjects() {
        for (String s : projectsMap.keySet()) {
            System.out.println(s);
        }
    }

    private static void getTasksByProject(String project) throws IOException {
        while (!projectsMap.containsKey(project)) {
            System.out.println("Такого проекта не существует. Выберите проект из существующих:");
            for (String p : projectsMap.keySet()) {
                System.out.println(p);
            }
            project = reader.readLine();
        }
        Project p = projectsMap.get(project);
        for (String task : p.getTasks()) {
            System.out.println(task);
        }
    }

    private static void getTasksByUser(String user) throws IOException {
        while (!usersMap.containsKey(user)) {
            System.out.println("Такого пользователя не существует. Выберите пользователя из существующих:");
            for (String u : usersMap.keySet()) {
                System.out.println(u);
            }
            user = reader.readLine();
        }
        User u = usersMap.get(user);
        for (String task : u.getTasks()) {
            System.out.println(task);
        }
    }

    private static String getUserFromConsole() throws IOException {
        String userKey = reader.readLine();
        while (!usersMap.containsKey(userKey)) {
            System.out.println("Такого пользователя не существует. Выберите исполнителя из существующих:");
            for (String u : usersMap.keySet()) {
                System.out.println(u);
            }
            userKey = reader.readLine();
        }
        return userKey;
    }

    private static String getTaskFromConsole() throws IOException {
        String name = reader.readLine();
        while (tasksMap.containsKey(name)) {
            System.out.println("Задача с таким названием уже существует. Введите новое название:");
            name = reader.readLine();
        }
        return name;
    }

    private static String getTaskFromConsoleToDelete() throws IOException {
        String name = reader.readLine();
        while (!tasksMap.containsKey(name)) {
            System.out.println("Такой задачи не существует. Выберите задачу из существующих:");
            for (String t : tasksMap.keySet()) {
                System.out.println(t);
            }
            name = reader.readLine();
        }
        return name;
    }
}
