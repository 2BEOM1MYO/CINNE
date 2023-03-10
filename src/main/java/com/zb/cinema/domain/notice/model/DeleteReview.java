package com.zb.cinema.domain.notice.model;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteReview {

	@NotBlank(message = "작성자 본인의 비밀번호를 입력해주세요.")
	private String password;

}
