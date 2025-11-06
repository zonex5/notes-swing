package xyz.toway.notes.ui;

import xyz.toway.notes.service.DatabaseService;
import xyz.toway.notes.service.NoteService;
import lombok.Getter;
import xyz.toway.notes.service.SettingsService;
import xyz.toway.notes.service.UtilsService;

@Getter
public final class ApplicationContext {

    private final DatabaseService databaseService;
    private final NoteService noteService;
    private final SettingsService settingsService;
    private final UtilsService utilsService;

    public ApplicationContext(DatabaseService databaseService, NoteService noteService, SettingsService settingsService, UtilsService utilsService) {
        this.databaseService = databaseService;
        this.noteService = noteService;
        this.settingsService = settingsService;
        this.utilsService = utilsService;
    }
}
