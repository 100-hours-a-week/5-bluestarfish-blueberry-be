package com.bluestarfish.blueberry.common.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserRoom is a Querydsl query type for UserRoom
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserRoom extends EntityPathBase<UserRoom> {

    private static final long serialVersionUID = -2115951811L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserRoom userRoom = new QUserRoom("userRoom");

    public final BooleanPath camEnabled = createBoolean("camEnabled");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final TimePath<java.sql.Time> dayTime = createTime("dayTime", java.sql.Time.class);

    public final TimePath<java.sql.Time> goalTime = createTime("goalTime", java.sql.Time.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isActive = createBoolean("isActive");

    public final BooleanPath isHost = createBoolean("isHost");

    public final BooleanPath micEnabled = createBoolean("micEnabled");

    public final com.bluestarfish.blueberry.room.entity.QRoom room;

    public final BooleanPath speakerEnabled = createBoolean("speakerEnabled");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final com.bluestarfish.blueberry.user.entity.QUser user;

    public QUserRoom(String variable) {
        this(UserRoom.class, forVariable(variable), INITS);
    }

    public QUserRoom(Path<? extends UserRoom> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserRoom(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserRoom(PathMetadata metadata, PathInits inits) {
        this(UserRoom.class, metadata, inits);
    }

    public QUserRoom(Class<? extends UserRoom> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.room = inits.isInitialized("room") ? new com.bluestarfish.blueberry.room.entity.QRoom(forProperty("room")) : null;
        this.user = inits.isInitialized("user") ? new com.bluestarfish.blueberry.user.entity.QUser(forProperty("user")) : null;
    }

}

