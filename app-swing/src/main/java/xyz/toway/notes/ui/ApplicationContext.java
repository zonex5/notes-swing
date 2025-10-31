package xyz.toway.notes.ui;

import xyz.toway.notes.service.DatabaseService;
import xyz.toway.notes.service.NoteService;
import lombok.Getter;
import xyz.toway.notes.service.SettingsService;

@Getter
public final class ApplicationContext {

    private DatabaseService databaseService;
    private NoteService noteService;
    private SettingsService settingsService;

    public ApplicationContext(DatabaseService databaseService, NoteService noteService, SettingsService settingsService) {
        this.databaseService = databaseService;
        this.noteService = noteService;
        this.settingsService = settingsService;
    }
}
