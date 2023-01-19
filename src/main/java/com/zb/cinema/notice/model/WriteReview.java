package com.zb.cinema.notice.model;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class WriteReview {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Request {

		@NotBlank(message = "영화 제목을 입력해주세요.")
		private String title;

		@NotBlank(message = "내용을 입력해주세요.")
		private String contents;

		@NotNull(message = "별점 갯수를 입력해주세요. (0 ~ 5)")
		private int starRating;

	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Response {

		private String title;
		private String email;
		private int starRating;
		private String contents;
		private LocalDateTime regDt;

		public static Response from(NoticeDto noticeDto) {

			return Response.builder().title(noticeDto.getMovieTitle()).email(noticeDto.getEmail())
				.starRating(noticeDto.getStarRating()).contents(noticeDto.getContents())
				.regDt(noticeDto.getRegDt()).build();
		}

	}

}
