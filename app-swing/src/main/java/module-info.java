module xyz.toway.notes.ui {
    requires java.desktop;
    requires com.formdev.flatlaf;
    requires com.formdev.flatlaf.extras;
    requires org.fife.RSyntaxTextArea;
    requires com.github.weisj.jsvg;
    requires xyz.toway.notes.domain;
    requires xyz.toway.notes.service;

    requires static lombok;
    requires com.github.kwhat.jnativehook;

    uses xyz.toway.notes.domain.port.factory.SettingsRepositoryFactory;
    uses xyz.toway.notes.domain.port.factory.DatabaseRepositoryFactory;
    uses xyz.toway.notes.domain.port.factory.NoteRepositoryFactory;
    uses xyz.toway.notes.domain.port.factory.LastOpenedRepositoryFactory;
    uses xyz.toway.notes.domain.port.factory.GroupRepositoryFactory;
}
