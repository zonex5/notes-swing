package xyz.toway.notes.persistence;

import lombok.Getter;
import org.dizitart.no2.Nitrite;

public class DatabaseContext {

    @Getter
    private Nitrite db;

}
