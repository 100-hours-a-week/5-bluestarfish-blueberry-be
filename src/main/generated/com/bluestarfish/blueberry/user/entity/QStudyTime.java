package com.bluestarfish.blueberry.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStudyTime is a Querydsl query type for StudyTime
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStudyTime extends EntityPathBase<StudyTime> {

    private static final long serialVersionUID = 1455004031L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStudyTime studyTime = new QStudyTime("studyTime");

    public final DateTimePath<java.util.Date> date = createDateTime("date", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final TimePath<java.sql.Time> time = createTime("time", java.sql.Time.class);

    public final QUser user;

    public QStudyTime(String variable) {
        this(StudyTime.class, forVariable(variable), INITS);
    }

    public QStudyTime(Path<? extends StudyTime> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStudyTime(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStudyTime(PathMetadata metadata, PathInits inits) {
        this(StudyTime.class, metadata, inits);
    }

    public QStudyTime(Class<? extends StudyTime> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

