package com.bluestarfish.blueberry.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class RanksResponse {
    private List<Rank> ranks;
}
