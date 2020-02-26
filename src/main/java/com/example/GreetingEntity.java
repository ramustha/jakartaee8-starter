package com.example;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity
@NamedQueries({
        @NamedQuery(name = "GreetingEntity.findAll", query = "SELECT g FROM GreetingEntity g"),
})
public class GreetingEntity {
    @Id
    private Long id;
    @NotNull
    private String message;
    @Version
    private Integer version;

    public GreetingEntity() {
    }

    public GreetingEntity(@NotNull Long id, @NotNull String message) {
        this.id = id;
        this.message = message;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "GreetingEntity{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
