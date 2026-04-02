package com.spring.Request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReviewRequest {

    private Integer userId;
    private Integer venueId;
    private String comment;
    private Integer rating;
}
