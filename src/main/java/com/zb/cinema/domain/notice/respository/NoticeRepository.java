package com.zb.cinema.domain.notice.respository;

import com.zb.cinema.domain.member.entity.Member;
import com.zb.cinema.domain.movie.entity.MovieCode;
import com.zb.cinema.domain.notice.entity.Notice;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

	Optional<Notice> findByNoticeMovieAndNoticeMember(MovieCode noticeMovie, Member noticeMember);

	Page<Notice> findAllByOrderByRegDt(Pageable pageable);

	Optional<Notice> findById(Long noticeId);

	Page<Notice> findByNoticeMovieCode(Long noticeMovie, Pageable pageable);

	@Query("select avg(m.starRating)from Notice m "
		+ "where m.noticeMovie.code = :movieCode")
	Double getByNoticeMovieCode(Long movieCode);

	void deleteAllById(Long noticeId);
}
