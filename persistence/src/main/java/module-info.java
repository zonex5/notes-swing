module xyz.toway.notes.persistence {
    exports xyz.toway.notes.persistence;
    exports xyz.toway.notes.persistence.repository;

    requires java.sql;
    requires org.dizitart.no2;
    requires static lombok;
    requires org.dizitart.no2.mvstore;
}
