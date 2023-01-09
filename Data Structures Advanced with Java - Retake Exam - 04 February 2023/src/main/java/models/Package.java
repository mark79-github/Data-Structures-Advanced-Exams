package models;

import java.time.LocalDateTime;

public class Package {
    private String id;

    private String name;

    private String version;

    private LocalDateTime releaseDate;

    public Package(String id, String name, String version, LocalDateTime releaseDate) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.releaseDate = releaseDate;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LocalDateTime getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDateTime releaseDate) {
        this.releaseDate = releaseDate;
    }
}
