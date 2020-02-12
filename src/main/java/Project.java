import java.util.HashSet;
import java.util.Set;

public class Project {
    private String projectName;
    private Set<String> tasks;

    public Project(String projectName) {
        this.projectName = projectName;
        this.tasks = new HashSet<>();
    }

    public Project() {
        this.tasks = new HashSet<>();
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Set<String> getTasks() {
        return tasks;
    }
}
