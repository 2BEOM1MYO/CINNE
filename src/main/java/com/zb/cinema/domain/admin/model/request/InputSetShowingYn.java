package com.zb.cinema.domain.admin.model.request;

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
public class InputSetShowingYn {

    private Long movieCode;

    private boolean showingYn;
}
