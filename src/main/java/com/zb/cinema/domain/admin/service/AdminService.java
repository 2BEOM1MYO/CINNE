package com.zb.cinema.domain.admin.service;

import com.zb.cinema.domain.admin.entity.Auditorium;
import com.zb.cinema.domain.admin.entity.Schedule;
import com.zb.cinema.domain.admin.entity.Seat;
import com.zb.cinema.domain.admin.entity.Theater;
import com.zb.cinema.domain.admin.model.request.InputSchedule;
import com.zb.cinema.domain.admin.model.response.AdminMemberDto;
import com.zb.cinema.domain.admin.model.response.AuditoriumSchedule;
import com.zb.cinema.domain.admin.model.request.InputAuditorium;
import com.zb.cinema.domain.admin.model.request.InputTheater;
import com.zb.cinema.domain.admin.model.response.SeatModel;
import com.zb.cinema.domain.admin.repository.AuditoriumRepository;
import com.zb.cinema.domain.admin.repository.ScheduleRepository;
import com.zb.cinema.domain.admin.repository.SeatRepository;
import com.zb.cinema.domain.admin.repository.TheaterRepository;
import com.zb.cinema.global.jwt.TokenProvider;
import com.zb.cinema.domain.member.entity.Member;
import com.zb.cinema.domain.member.exception.MemberError;
import com.zb.cinema.domain.member.exception.MemberException;
import com.zb.cinema.domain.member.model.MemberDto;
import com.zb.cinema.domain.member.repository.MemberRepository;
import com.zb.cinema.domain.member.type.MemberType;
import com.zb.cinema.domain.movie.entity.Movie;
import com.zb.cinema.domain.movie.repository.MovieRepository;
import com.zb.cinema.domain.movie.type.ErrorCode;
import com.zb.cinema.domain.movie.type.MovieStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;
    private final ScheduleRepository scheduleRepository;
    private final SeatRepository seatRepository;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final AuditoriumRepository auditoriumRepository;

    //?????? ?????? ?????? (????????? / ???????????? / ????????????)
    public void setMovieScreeningStatus(Long movieCode, MovieStatus status,
        String token) {

        if (!isAdmin(token)) {
            throw new RuntimeException(
                ErrorCode.INVALID_ACCESS_MEMBER.getDescription());
        }
        Movie movie = movieRepository.findById(movieCode)
            .orElseThrow();

        //?????? ?????? ????????? ??? ????????????
        if (movie.getStatus() == status) {
            if (status == MovieStatus.STATUS_OVER) {
                throw new RuntimeException(
                    ErrorCode.MOVIE_ALREADY_NOT_SHOWING.getDescription());
            } else if (status == MovieStatus.STATUS_SHOWING) {
                throw new RuntimeException(
                    ErrorCode.MOVIE_ALREADY_SHOWING.getDescription());
            } else if (status == MovieStatus.STATUS_WILL) {
                throw new RuntimeException(
                    ErrorCode.MOVIE_ALREADY_WILL_SHOWING.getDescription());
            }
        }
        //??????????????? ????????? ?????? ?????? ???????????? ??????
        if (status == MovieStatus.STATUS_OVER) {
            List<Schedule> scheduleList =
                scheduleRepository.findAllByMovieAndEndDtAfter(movie, LocalDateTime.now());
            if (scheduleList.size() > 0) {
                throw new RuntimeException("?????? ????????? ???????????? ??????????????? ??????????????????.");
            }
            //?????? ?????? ?????? ??? ?????? ????????? ??????
            //1. ?????? ??????????????? ????????? ??????
            //2. ???????????? ??????
            List<Schedule> scheduleDeleteList =
                scheduleRepository.findAllByMovie(movie);
            List<Seat> seatList = new ArrayList<>();
            for (Schedule schedule : scheduleDeleteList) {
                List<Seat> tmpSeatList = seatRepository.findAllBySchedule(schedule);
                seatList.addAll(tmpSeatList);
                seatRepository.deleteAllInBatch(seatList);
            }
            scheduleRepository.deleteAllInBatch(scheduleDeleteList);
        }

        movie.setStatus(status);
        movieRepository.save(movie);
    }

    //?????? ????????? ?????? ??????
    public List<Movie> getMovieListByStatus(MovieStatus status) {
        List<Movie> movieList = movieRepository.findAllByStatus(status);
        if (movieList.size() < 1) {
            throw new RuntimeException(
                ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }
        return movieList;
    }

    //?????? ??????
    public Theater registerTheater(InputTheater inputTheater) {
        String area = inputTheater.getArea();
        String city = inputTheater.getCity();
        String name = inputTheater.getName();

        if (theaterRepository.countByAreaAndCityAndName(area, city, name) > 0) {
            throw new RuntimeException("?????? ???????????? ???????????????.");
        }

        Theater theater = Theater.builder()
            .area(area)
            .city(city)
            .name(name)
            .build();

        theaterRepository.save(theater);
        return theater;
    }

    //????????? ??????
    public Auditorium registerAuditorium(InputAuditorium inputAuditorium, String token) {
        //?????? ??????
        if (!isAdmin(token)) {
            throw new RuntimeException(
                ErrorCode.INVALID_ACCESS_MEMBER.getDescription());
        }
        Optional<Theater> optionalTheater = theaterRepository.findById(
            inputAuditorium.getTheaterId());
        if (!optionalTheater.isPresent()) {
            throw new RuntimeException(
                ErrorCode.THEATER_NOT_FOUND.getDescription());
        }
        Theater theater = optionalTheater.get();

        if (auditoriumRepository.findByTheaterAndAndName(theater,
            inputAuditorium.getName()).isPresent()) {
            throw new RuntimeException(
                ErrorCode.AUDITORIUM_ALREADY_EXIST.getDescription());
        }

        Auditorium auditorium = Auditorium.builder()
            .theater(theater)
            .name(inputAuditorium.getName())
            .capacity(inputAuditorium.getCapacity()).build();
        auditoriumRepository.save(auditorium);

        return auditorium;
    }

    //???????????? ??????
    public Schedule registerSchedule(InputSchedule inputSchedule, String token) {
        //?????? ??????
        if (!isAdmin(token)) {
            throw new RuntimeException(
                ErrorCode.INVALID_ACCESS_MEMBER.getDescription());
        }

        //????????? ??????
        Auditorium auditorium = auditoriumRepository.findById(
            inputSchedule.getAuditoriumId()).orElseThrow();

        //?????? ??????
        Movie movie = movieRepository.findById(inputSchedule.getMovieCode())
            .orElseThrow();

        if (movie.getStatus() != MovieStatus.STATUS_SHOWING) {
            throw new RuntimeException(
                ErrorCode.MOVIE_NOT_SHOWING.getDescription());
        }

        //?????? ??? ??????
        LocalDateTime startDt = LocalDateTime.parse(inputSchedule.getStartDt(),
            DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        LocalDateTime endDt = startDt.plusMinutes(movie.getRunTime());
        //?????? ???????????? ??????????????? ?????? (????????????-30 ~ ????????????+30) ???????????? ?????? ??????
        List<Schedule> scheduleList = scheduleRepository.findAllByStartDtBetweenAndAuditoriumOrEndDtBetweenAndAuditorium(
            startDt.minusMinutes(30), endDt.plusMinutes(30), auditorium,
            startDt.minusMinutes(30), endDt.plusMinutes(30), auditorium);
        if (scheduleList.size() > 0) {
            throw new RuntimeException(
                ErrorCode.SCHEDULE_ALREADY_EXIST.getDescription());
        }

        Schedule schedule = Schedule.builder()
            .auditorium(auditorium)
            .movie(movie)
            .startDt(startDt)
            .endDt(endDt)
            .build();

        //??????????????? ?????? ??????
        List<String> seatNmList = makeSeats(auditorium.getCapacity());

        List<Seat> seatList = new ArrayList<>();
        for (String seatNm : seatNmList) {
            seatList.add(Seat.builder()
                .auditorium(auditorium)
                .schedule(schedule)
                .seatNum(seatNm)
                .isUsing(false)
                .price(13000)
                .build());
        }

        scheduleRepository.save(schedule);

        seatRepository.saveAll(seatList);

        return schedule;
    }

    //?????? ??????
    public List<String> makeSeats(long capacity) {
        List<String> seatList = new ArrayList<>();

        long rowSize = capacity / 10;
        long restSize = capacity % 10;
        int i = 0;
        StringBuilder sb = new StringBuilder();
        for (; i < rowSize; i++) {
            sb.setLength(0);
            sb.append(Character.toString('A' + i));
            for (int j = 1; j <= 10; j++) {
                sb.setLength(1);
                sb.append(j);
                seatList.add(sb.toString());
            }
        }
        sb.setLength(0);
        sb.append(Character.toString('A' + i));
        for (int j = 1; j <= restSize; j++) {
            sb.setLength(1);
            sb.append(j);
            seatList.add(sb.toString());
        }

        return seatList;
    }

    public SeatModel setSeatPrice(String token, Long id, Long price) {
        if (!isAdmin(token)) {
            throw new RuntimeException(
                ErrorCode.INVALID_ACCESS_MEMBER.getDescription());
        }

        Seat seat = seatRepository.findById(id).orElseThrow();

        seat.setPrice(price);
        seatRepository.save(seat);
        return SeatModel.builder()
                .id(seat.getId())
                .seatNum(seat.getSeatNum())
                .price(seat.getPrice())
                .isUsing(seat.isUsing()).build();
    }

    //    ????????? ???????????? ??????
    public List<AuditoriumSchedule> getScheduleByMovie(Long movieCode) {
        Movie movie = movieRepository.findById(movieCode).orElseThrow();

        List<Schedule> scheduleList = scheduleRepository.findAllByMovie(movie);
        if (scheduleList.size() < 1) {
            throw new RuntimeException("?????? ????????? ?????? ????????? ????????????.");
        }

        List<AuditoriumSchedule> auditoriumSchedules = new ArrayList<>();
        for (Schedule item : scheduleList) {
            long theaterId = item.getAuditorium().getTheater().getId();
            Theater theater = theaterRepository.findById(theaterId).get();
            auditoriumSchedules.add(AuditoriumSchedule.builder()
                .theaterId(theaterId)
                .auditoriumNm(item.getAuditorium().getName())
                .theaterNm(theater.getArea() + " " + theater.getCity() + " " + theater.getName())
                .movieId(item.getMovie().getCode())
                .title(item.getMovie().getTitle())
                .startDt(item.getStartDt())
                .endDt(item.getEndDt())
                .build());
        }

        return auditoriumSchedules;
    }

    // ??????????????? ?????? ??????
    public List<SeatModel> getAuditoriumSeats(Long scheduleId) {

        Optional<Schedule> optionalAuditorium = scheduleRepository.findById(scheduleId);
        if (!optionalAuditorium.isPresent()) {
            throw new RuntimeException(
                ErrorCode.SCHEDULE_NOT_FOUND.getDescription());
        }
        Schedule schedule = optionalAuditorium.get();
        List<Seat> seats = seatRepository.findAllBySchedule(schedule);
        List<SeatModel> seatModels = new ArrayList<>();
        for (Seat seat : seats) {
            seatModels.add(SeatModel.builder()
                .id(seat.getId())
                .seatNum(seat.getSeatNum())
                .price(seat.getPrice())
                .isUsing(seat.isUsing())
                .build());
        }

        return seatModels;
    }

    //?????? ?????? ??????
    public void setMemberType(String token, String memberEmail, MemberType memberType) {

        if (!isAdmin(token)) {
            throw new RuntimeException(
                ErrorCode.INVALID_ACCESS_MEMBER.getDescription());
        }

        Member member = memberRepository.findByEmail(memberEmail).orElseThrow();
        if (member.getType() == memberType) {
            if (memberType == MemberType.ROLE_ADMIN) {
                throw new RuntimeException("?????? ??????????????? ?????????.");
            } else if (memberType == MemberType.ROLE_READWRITE) {
                throw new RuntimeException("?????? ?????? ?????? ?????????.");
            } else if (memberType == MemberType.ROLE_UN_ACCESSIBLE) {
                throw new RuntimeException("?????? ????????? ?????? ?????????.");
            }
        }

        member.setType(memberType);
        memberRepository.save(member);
    }

    //?????? ?????? ??????
    public List<AdminMemberDto> getAllMember(String token) {

        if (!isAdmin(token)) {
            throw new RuntimeException(
                ErrorCode.INVALID_ACCESS_MEMBER.getDescription());
        }

        List<Member> memberList = memberRepository.findAll();
        List<AdminMemberDto> adminMemberDtoList = new ArrayList<>();
        for (Member member : memberList) {
            adminMemberDtoList.add(AdminMemberDto.from(member));
        }

        return adminMemberDtoList;
    }

    //????????? ??????
    public MemberDto registerAdmin(String token, String email, String password, String name,
        String phone) {

        if (!isAdmin(token)) {
            throw new RuntimeException(
                ErrorCode.INVALID_ACCESS_MEMBER.getDescription());
        }

        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        if (optionalMember.isPresent()) {
            throw new MemberException(MemberError.MEMBER_ALREADY_EMAIL);
        }

        String pw = BCrypt.hashpw(password, BCrypt.gensalt());

        return MemberDto.from(memberRepository.save(
            Member.builder().email(email).password(pw).name(name).phone(phone)
                .regDt(LocalDateTime.now()).type(MemberType.ROLE_ADMIN).build()));
    }

    //???????????? ????????? ?????? ??????
    private boolean isAdmin(String token) {
        String email = "";
        email = tokenProvider.getUserPk(token);
        Member adminMember = memberRepository.findByEmail(email).get();

        if (adminMember.getType() == MemberType.ROLE_READWRITE) {
            return false;
        }
        return true;
    }
}
