module xyz.toway.notes.domain {
    exports xyz.toway.notes.domain.port;
    exports xyz.toway.notes.domain.port.factory;
    exports xyz.toway.notes.domain.model;
    exports xyz.toway.notes.domain.types;

    requires static lombok;
    requires xyz.toway.notes.domain;
}
