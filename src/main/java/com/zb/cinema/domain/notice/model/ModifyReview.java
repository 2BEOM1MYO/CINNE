package com.zb.cinema.domain.notice.model;

import io.swagger.annotations.ApiModel;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ModifyReview {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@ApiModel(value = "후기 수정 입력 정보")
	public static class Request {

		@NotBlank(message = "수정 할 내용을 입력해주세요.")
		private String contents;

		@NotNull(message = "수정 할 별점 갯수를 입력해주세요. (0 ~ 5)")
		private int starRating;

	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@ApiModel(value = "후기 수정 응답 정보")
	public static class Response {

		private String title;
		private String email;
		private int starRating;
		private String contents;
		private LocalDateTime updateDt;

		public static ModifyReview.Response from(NoticeDto noticeDto) {

			return ModifyReview.Response.builder().title(noticeDto.getMovieTitle()).email(noticeDto.getEmail())
				.starRating(noticeDto.getStarRating()).contents(noticeDto.getContents())
				.updateDt(noticeDto.getUpdateDt()).build();
		}

	}

}
