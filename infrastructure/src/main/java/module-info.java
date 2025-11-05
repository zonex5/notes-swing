module xyz.toway.notes.infrastructure {
    requires xyz.toway.notes.domain;
    requires java.prefs;
    requires org.dizitart.no2;
    requires org.dizitart.no2.mvstore;
    requires org.slf4j;

    requires static lombok;
    requires org.dizitart.no2.jackson;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.datatype.jsr310;

    exports xyz.toway.notes.infrastructure.settings;
    exports xyz.toway.notes.infrastructure.persistence.factory;
    exports xyz.toway.notes.infrastructure.persistence.repository;

    opens xyz.toway.notes.infrastructure.persistence.entity
            to com.fasterxml.jackson.databind, org.dizitart.no2.jackson, org.dizitart.no2;

    provides xyz.toway.notes.domain.port.factory.DatabaseRepositoryFactory
            with xyz.toway.notes.infrastructure.persistence.factory.NtDatabaseRepositoryFactory;

    provides xyz.toway.notes.domain.port.factory.SettingsRepositoryFactory
            with xyz.toway.notes.infrastructure.settings.PreferencesSettingsRepositoryFactory;

    provides xyz.toway.notes.domain.port.factory.NoteRepositoryFactory
            with xyz.toway.notes.infrastructure.persistence.factory.NtNoteRepositoryFactory;

    provides xyz.toway.notes.domain.port.factory.LastOpenedRepositoryFactory
            with xyz.toway.notes.infrastructure.persistence.factory.NtLastOpenedRepositoryFactory;
}
