package org.piaohao.act.jfinal.db;

import act.db.EntityClassRepository;
import act.util.AnnotatedClassFinder;
import org.osgl.$;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.Entity;
import javax.persistence.Table;

@Singleton
public final class EntityFinder {

    private final EntityClassRepository repo;

    @Inject
    public EntityFinder(EntityClassRepository repo) {
        this.repo = $.notNull(repo);
    }

    @AnnotatedClassFinder(Entity.class)
    public void foundEntity(Class<?> entityClass) {
        repo.registerModelClass(entityClass);
    }

    @AnnotatedClassFinder(Table.class)
    public void foundTable(Class<?> tableClass) {
        repo.registerModelClass(tableClass);
    }

}