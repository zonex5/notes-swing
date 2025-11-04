package xyz.toway.notes.infrastructure.persistence;

import xyz.toway.notes.domain.model.NoteModel;
import xyz.toway.notes.domain.types.ContentType;
import xyz.toway.notes.infrastructure.persistence.entity.NoteEntity;

public final class Mapper {

    public static NoteModel toModel(NoteEntity entity) {
        NoteModel model = new NoteModel();
        model.setId(entity.getId());
        model.setSyntax(entity.getSyntax());
        model.setGroupId(entity.getGroupId());
        model.setContent(entity.getContent());
        model.setTitle(entity.getTitle());
        model.setCreatedAt(entity.getCreatedAt());
        model.setUpdatedAt(entity.getUpdatedAt());
        model.setContentType(ContentType.TEXT);
        return model;
    }

    public static NoteEntity fromModel(NoteModel model) {
        NoteEntity entity = new NoteEntity();
        entity.setId(model.getId());
        entity.setGroupId(model.getGroupId());
        entity.setContent(model.getContent());
        entity.setTitle(model.getTitle());
        entity.setCreatedAt(model.getCreatedAt());
        entity.setUpdatedAt(model.getUpdatedAt());
        entity.setSyntax(model.getSyntax());
        entity.setType(model.getContentType());
        return entity;
    }
}

