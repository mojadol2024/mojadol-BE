package com.gnu.mojadol.dto;

import com.gnu.mojadol.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
    private int boardSeq;
    private int userSeq;
    private String commentText;
    private Integer parentCommentSeq;
    private int commentSeq;
    private int deleted_flag;
}
