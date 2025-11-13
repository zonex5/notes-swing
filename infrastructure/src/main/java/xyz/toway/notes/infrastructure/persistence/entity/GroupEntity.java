package xyz.toway.notes.infrastructure.persistence.entity;

import lombok.Data;
import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;

@Data
@Entity
public class GroupEntity {
    @Id()
    private String id;
    private String title;
    private String icon;
}
