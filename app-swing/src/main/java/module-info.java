module xyz.toway.notes.ui {
    requires java.desktop;
    requires com.formdev.flatlaf;
    requires com.formdev.flatlaf.extras;
    requires org.fife.RSyntaxTextArea;
    requires com.github.weisj.jsvg;
    requires xyz.toway.notes.domain;
    requires xyz.toway.notes.service;

    requires static lombok;

    uses xyz.toway.notes.domain.port.DatabaseRepository;
    uses xyz.toway.notes.domain.port.SettingsRepository;
    uses xyz.toway.notes.domain.port.factory.SettingsRepositoryFactory;
    uses xyz.toway.notes.domain.port.factory.DatabaseRepositoryFactory;
}
