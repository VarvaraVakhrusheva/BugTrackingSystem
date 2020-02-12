import java.util.HashSet;
import java.util.Set;

public class User {
    private String name;
    private Set<String> tasks;

    public User(String name) {
        this.name = name;
        this.tasks = new HashSet<>();
    }

    public User() {
        this.tasks = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getTasks() {
        return tasks;
    }
}
