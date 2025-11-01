module xyz.toway.notes.infrastructure {
    requires xyz.toway.notes.domain;
    requires java.prefs;
    requires org.dizitart.no2;
    requires org.dizitart.no2.mvstore;
    requires org.slf4j;

    requires static lombok;

    exports xyz.toway.notes.settings;
    exports xyz.toway.notes.persistence;

    provides xyz.toway.notes.domain.port.factory.DatabaseRepositoryFactory
            with xyz.toway.notes.persistence.NtDatabaseRepositoryFactory;

    provides xyz.toway.notes.domain.port.factory.SettingsRepositoryFactory
            with xyz.toway.notes.settings.PreferencesSettingsRepositoryFactory;
}
