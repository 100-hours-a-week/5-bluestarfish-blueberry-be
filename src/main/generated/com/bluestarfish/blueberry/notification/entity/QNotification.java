package com.bluestarfish.blueberry.notification.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNotification is a Querydsl query type for Notification
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotification extends EntityPathBase<Notification> {

    private static final long serialVersionUID = -10799838L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNotification notification = new QNotification("notification");

    public final com.bluestarfish.blueberry.comment.entity.QComment comment;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<com.bluestarfish.blueberry.notification.enumeration.NotiStatus> notiStatus = createEnum("notiStatus", com.bluestarfish.blueberry.notification.enumeration.NotiStatus.class);

    public final EnumPath<com.bluestarfish.blueberry.notification.enumeration.NotiType> notiType = createEnum("notiType", com.bluestarfish.blueberry.notification.enumeration.NotiType.class);

    public final com.bluestarfish.blueberry.user.entity.QUser receiver;

    public final com.bluestarfish.blueberry.room.entity.QRoom room;

    public final com.bluestarfish.blueberry.user.entity.QUser sender;

    public QNotification(String variable) {
        this(Notification.class, forVariable(variable), INITS);
    }

    public QNotification(Path<? extends Notification> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNotification(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNotification(PathMetadata metadata, PathInits inits) {
        this(Notification.class, metadata, inits);
    }

    public QNotification(Class<? extends Notification> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.comment = inits.isInitialized("comment") ? new com.bluestarfish.blueberry.comment.entity.QComment(forProperty("comment"), inits.get("comment")) : null;
        this.receiver = inits.isInitialized("receiver") ? new com.bluestarfish.blueberry.user.entity.QUser(forProperty("receiver")) : null;
        this.room = inits.isInitialized("room") ? new com.bluestarfish.blueberry.room.entity.QRoom(forProperty("room")) : null;
        this.sender = inits.isInitialized("sender") ? new com.bluestarfish.blueberry.user.entity.QUser(forProperty("sender")) : null;
    }

}

