package models;

public class Task {
    private String id;

    private String name;

    private int estimatedExecutionTime;

    private String domain;

    public Task(String id, String name, int estimatedExecutionTime, String domain) {
        this.id = id;
        this.name = name;
        this.estimatedExecutionTime = estimatedExecutionTime;
        this.domain = domain;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEstimatedExecutionTime() {
        return estimatedExecutionTime;
    }

    public void setEstimatedExecutionTime(int estimatedExecutionTime) {
        this.estimatedExecutionTime = estimatedExecutionTime;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;

        Task task = (Task) o;

        if (getEstimatedExecutionTime() != task.getEstimatedExecutionTime()) return false;
        if (getId() != null ? !getId().equals(task.getId()) : task.getId() != null) return false;
        if (getName() != null ? !getName().equals(task.getName()) : task.getName() != null) return false;
        return getDomain() != null ? getDomain().equals(task.getDomain()) : task.getDomain() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + getEstimatedExecutionTime();
        result = 31 * result + (getDomain() != null ? getDomain().hashCode() : 0);
        return result;
    }
}
