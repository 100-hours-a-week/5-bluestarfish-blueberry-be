package com.bluestarfish.blueberry.notification.dto;

import com.bluestarfish.blueberry.notification.entity.Notification;
import com.bluestarfish.blueberry.notification.enumeration.NotiStatus;
import com.bluestarfish.blueberry.notification.enumeration.NotiType;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeDto {
    private Long id;
    private Long receiverId;
    private NotiType notiType;
    private NotiStatus notiStatus;
    private Long commentId;
    private Long roomId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    public static NoticeDto from(Notification notification) {
        return NoticeDto.builder()
                .id(notification.getId())
                .receiverId(notification.getReceiver().getId())
                .notiType(notification.getNotiType())
                .notiStatus(notification.getNotiStatus())
                .commentId(notification.getComment().getId())
                .roomId(notification.getRoom().getId())
                .build();
    }
}
