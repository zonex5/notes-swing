package xyz.toway.notes.persistence.entity;

import lombok.Data;
import org.dizitart.no2.repository.annotations.Id;

import java.util.UUID;

@Data
public class NoteGroupEntity {

    @Id
    private String id;

    private String name;

    public NoteGroupEntity() {
        this.id = UUID.randomUUID().toString();
    }

    public NoteGroupEntity(String name) {
        this();
        this.name = name;
    }

    public NoteGroupEntity(String id, String name) {
        this(name);
        this.id = id;
    }
}
