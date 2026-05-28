package com.spring.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReviewRequest {

    private Integer venueId;
    private String comment;
    private Integer rating;
}
