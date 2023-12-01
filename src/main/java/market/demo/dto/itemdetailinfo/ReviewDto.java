package market.demo.dto.itemdetailinfo;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ReviewDto {
    private Long reviewId;
    private Long memberId;
    private String memberName;
    private int rating;
    private String comment;
    private Integer helpful;// 도움돼요
    private LocalDateTime reviewWriteDate;
    private List<String> imageUrls = new ArrayList<>();

}
