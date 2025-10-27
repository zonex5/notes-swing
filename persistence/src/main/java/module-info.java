module com.example.notes.persistence {
    requires com.example.notes.domain;
    requires java.sql;
    requires com.h2database;

    exports com.example.notes.persistence;
}
