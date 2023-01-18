package com.zb.cinema.notice.controller;

import com.zb.cinema.notice.model.DeleteReview;
import com.zb.cinema.notice.model.ModifyReview;
import com.zb.cinema.notice.model.NoticeDto;
import com.zb.cinema.notice.model.ReviewAllList;
import com.zb.cinema.notice.model.ReviewByMovie;
import com.zb.cinema.notice.model.ReviewDetail;
import com.zb.cinema.notice.model.WriteReview;
import com.zb.cinema.notice.model.WriteReview.Request;
import com.zb.cinema.notice.service.NoticeService;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {

	private final NoticeService noticeService;

	@PostMapping
	@PreAuthorize("hasRole('READWRITE')")
	public WriteReview.Response writeReview(@RequestBody @Valid WriteReview.Request request) {
		return WriteReview.Response.from(
			noticeService.writeReview(request));

	}

	/*
	 	전체 목록 보기 (Pagenation)
	 */
	@GetMapping("/list")
	public List<ReviewAllList> getReviewALlList() {
		return noticeService.getNoticeList().stream().map(
			noticeDto -> ReviewAllList.builder().email(noticeDto.getEmail())
				.movieTitle(noticeDto.getMovieTitle()).contents(noticeDto.getContents())
				.regDt(noticeDto.getRegDt()).build()).collect(Collectors.toList());
	}

	/*
	 	후기 글 상세 보기
	 */
	@GetMapping("/detail/{noticeId}")
	public ReviewDetail getReviewDetail(@PathVariable Long noticeId) {
		return ReviewDetail.from(noticeService.getReviewDetail(noticeId));
	}

	/*
	 	영화 별 후기 리스트 보기
	 */
	@GetMapping("/list/{movieCode}")
	public List<ReviewByMovie> getReviewByMovie(@PathVariable Long movieCode) {

		return noticeService.getReviewByMovie(movieCode).stream().map(
			noticeDto -> ReviewByMovie.builder().email(noticeDto.getEmail())
				.movieTitle(noticeDto.getMovieTitle()).contents(noticeDto.getContents())
				.regDt(noticeDto.getRegDt()).build()).collect(Collectors.toList());
	}

	@PutMapping("/detail/{noticeId}")
	@PreAuthorize("hasRole('READWRITE')")
	public ModifyReview.Response modifyReview(@PathVariable Long noticeId,
		@RequestBody @Valid ModifyReview.Request request) {

		return ModifyReview.Response.from(
			noticeService.modifyReview(noticeId, request));
	}

	@DeleteMapping("/detail/{noticeId}")
	@PreAuthorize("hasRole('READWRITE')")
	public void deleteReview(@PathVariable Long noticeId, @RequestBody DeleteReview request) {
		noticeService.deleteReview(noticeId, request);
	}

}
