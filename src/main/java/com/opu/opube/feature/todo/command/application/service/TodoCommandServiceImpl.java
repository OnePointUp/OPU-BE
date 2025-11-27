package com.opu.opube.feature.todo.command.application.service;

import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.member.query.service.MemberQueryService;
import com.opu.opube.feature.opu.command.application.service.MemberOpuCounterService;
import com.opu.opube.feature.opu.command.application.service.MemberOpuEventService;
import com.opu.opube.feature.opu.command.domain.aggregate.Opu;
import com.opu.opube.feature.opu.query.service.OpuQueryService;
import com.opu.opube.feature.todo.command.application.dto.request.OpuTodoCreateDto;
import com.opu.opube.feature.todo.command.application.dto.request.TodoCreateDto;
import com.opu.opube.feature.todo.command.application.dto.request.TodoStatusUpdateDto;
import com.opu.opube.feature.todo.command.application.dto.request.TodoUpdateDto;
import com.opu.opube.feature.todo.command.domain.aggregate.Routine;
import com.opu.opube.feature.todo.command.domain.aggregate.RoutineSchedule;
import com.opu.opube.feature.todo.command.domain.aggregate.Todo;
import com.opu.opube.feature.todo.command.domain.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.IsoFields;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoCommandServiceImpl implements TodoCommandService {

    private final TodoRepository todoRepository;
    private final MemberQueryService memberQueryService;
    private final MemberOpuCounterService memberOpuCounterService;
    private final MemberOpuEventService memberOpuEventService;
    private final OpuQueryService opuQueryService;

    @Override
    @Transactional
    public Long createTodo(Long memberId, TodoCreateDto todoCreateDto) {
        Member member = memberQueryService.getMember(memberId);

        Integer maxOrder = todoRepository.findMaxSortOrderByMemberIdAndDate(memberId, todoCreateDto.getScheduledDate());
        int newOrder = (maxOrder != null ? maxOrder : -1) + 1;

        Todo todo = Todo.toEntity(todoCreateDto, member, newOrder);
        Todo savedTodo = todoRepository.save(todo);
        return savedTodo.getId();
    }

    @Override
    @Transactional
    public Long createTodoByOpu(Long memberId, Long opuId, OpuTodoCreateDto opuTodoCreateDto) {
        Member member = memberQueryService.getMember(memberId);
        Opu opu = opuQueryService.getOpu(opuId);

        int newOrder = getOrder(memberId, opuTodoCreateDto.getScheduledDate());

        memberOpuEventService.createEvent(member, opu);
        Todo todo = Todo.toEntity(opu, opuTodoCreateDto, member, newOrder);
        Todo savedTodo = todoRepository.save(todo);

        return savedTodo.getId();
    }

    int getOrder(Long memberId,LocalDate date) {
        Integer maxOrder = todoRepository.findMaxSortOrderByMemberIdAndDate(memberId,date);
        return (maxOrder != null ? maxOrder : -1) + 1;
    }

    @Override
    public void createTodoByRoutine(Member member, Routine routine, RoutineSchedule schedule) {
        switch (routine.getFrequency()) {
            case DAILY -> createDailyTodo(member, routine);
            case WEEKLY -> createWeeklyTodo(member, routine, schedule);
            case BIWEEKLY -> createBiWeeklyTodo(member, routine, schedule);
            case MONTHLY -> createMonthlyTodo(member, routine, schedule);
            case YEARLY -> createYearlyTodo(member, routine, schedule);
            default -> throw new BusinessException(ErrorCode.UNSUPPORTED_FREQUENCY);
        }
    }

    // 매일 Todo
    private void createDailyTodo(Member member, Routine routine) {
        LocalDate start = routine.getStartDate();
        LocalDate end = routine.getEndDate();
        LocalDate today = start;

        while (!today.isAfter(end)) {
            saveTodo(member, routine, today, routine.getAlarmTime());
            today = today.plusDays(1);
        }
    }

    // 매주 Todo
    private void createWeeklyTodo(Member member, Routine routine, RoutineSchedule schedule) {
        LocalDate start = routine.getStartDate();
        LocalDate end = routine.getEndDate();
        Set<Integer> daysOfWeek = parseWeekDays(schedule.getWeekDays()); // 0~6

        LocalDate date = start;
        while (!date.isAfter(end)) {
            if (daysOfWeek.contains(date.getDayOfWeek().getValue() % 7)) { // DayOfWeek 1=월 ... 7=일
                saveTodo(member, routine, date, routine.getAlarmTime());
            }
            date = date.plusDays(1);
        }
    }

    private int getSundayStartWeek(LocalDate date) {
        // 0 = Sunday, 1 = Monday ... 6 = Saturday
        int dow = date.getDayOfWeek().getValue() % 7;

        // date - dow = Sunday of this week
        LocalDate sunday = date.minusDays(dow);

        return sunday.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    }

    // 2주마다 Todo
    private void createBiWeeklyTodo(Member member, Routine routine, RoutineSchedule schedule) {
        LocalDate start = routine.getStartDate();
        LocalDate end = routine.getEndDate();

        Set<Integer> targetWeekDays = parseWeekDays(schedule.getWeekDays()); // 0~6 (Sun=0)

        // 기준 주차 (ISO Week 기준)
        int baseWeekParity = getSundayStartWeek(start) % 2;

        LocalDate current = start;

        while (!current.isAfter(end)) {

            int currentWeekParity = getSundayStartWeek(current) % 2;
            int dow = current.getDayOfWeek().getValue() % 7; // Sunday=0 매핑 유지

            boolean isTargetDay = targetWeekDays.contains(dow);
            boolean isMatchingBiWeek = (currentWeekParity == baseWeekParity);

            if (isTargetDay && isMatchingBiWeek) {
                saveTodo(member, routine, current, routine.getAlarmTime());
            }

            current = current.plusDays(1);
        }
    }

    // 월별 Todo
    private void createMonthlyTodo(Member member, Routine routine, RoutineSchedule schedule) {
        LocalDate start = routine.getStartDate();
        LocalDate end = routine.getEndDate();

        LocalDate date = start;
        int currentMonth = -1;
        Set<Integer> monthDays = new HashSet<>();
        while (!date.isAfter(end)) {
            if (date.getMonthValue() != currentMonth) {
                monthDays = parseMonthDays(schedule.getMonthDays(), date);
                currentMonth = date.getMonthValue();
            }

            if (monthDays.contains(date.getDayOfMonth())) {
                saveTodo(member, routine, date, routine.getAlarmTime());
            }
            date = date.plusDays(1);
        }
    }

    // 년별 Todo
    private void createYearlyTodo(Member member, Routine routine, RoutineSchedule schedule) {
        LocalDate start = routine.getStartDate();
        LocalDate end = routine.getEndDate();
        Set<LocalDate> yearDays = parseYearDays(schedule.getDays(), start.getYear(), end.getYear());

        for (LocalDate date : yearDays) {
            if (!date.isBefore(start) && !date.isAfter(end)) {
                saveTodo(member, routine, date, routine.getAlarmTime());
            }
        }
    }

// --------------------------- Helper Methods ---------------------------

    // weekDays 문자열 -> Set<Integer> 변환
    private Set<Integer> parseWeekDays(String weekDays) {
        if (weekDays == null || weekDays.isBlank()) return Collections.emptySet();
        return Arrays.stream(weekDays.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
    }

    // monthDays 문자열 -> Set<Integer> 변환
    private Set<Integer> parseMonthDays(String monthDays, LocalDate start) {
        if (monthDays == null || monthDays.isBlank()) return Collections.emptySet();
        Set<Integer> result = new HashSet<>();
        for (String s : monthDays.split(",")) {
            s = s.trim();
            if ("L".equalsIgnoreCase(s)) {
                result.add(start.lengthOfMonth());
            } else {
                result.add(Integer.parseInt(s));
            }
        }
        return result;
    }

    // yearDays 문자열 -> Set<LocalDate> 변환
    private Set<LocalDate> parseYearDays(String days, int startYear, int endYear) {
        Set<LocalDate> result = new HashSet<>();
        if (days == null || days.isBlank()) return result;

        for (String range : days.split(",")) {
            String[] parts = range.split("-");
            if (parts.length == 2) {
                int month = Integer.parseInt(parts[0]);
                int day = Integer.parseInt(parts[1]);
                for (int year = startYear; year <= endYear; year++) {
                    if (day <= YearMonth.of(year, month).lengthOfMonth()) {
                        result.add(LocalDate.of(year, month, day));
                    }
                }
            }
        }
        return result;
    }

    private void saveTodo(Member member, Routine routine, LocalDate date, LocalTime time) {
        int sortOrder = getOrder(member.getId(), date);

        Todo todo = Todo.toEntity(member, routine, date, time, sortOrder);
        todoRepository.save(todo);
    }

    @Override
    @Transactional
    public void updateTodo(Long memberId, TodoUpdateDto dto, Long todoId) {
        Member member = memberQueryService.getMember(memberId);
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TODO_NOT_FOUND));

        if (!todo.isOwnedBy(member)) {
            throw new BusinessException(ErrorCode.TODO_FORBIDDEN);
        }

        // opu 인 경우 수정 불가
        if (todo.getOpu() != null) {
            throw new BusinessException(ErrorCode.OPU_TODO_CANNOT_BE_MODIFIED);
        }

        todo.patch(dto.getTitle(), dto.getScheduledDate(), dto.getScheduledTime());

    }

    @Override
    @Transactional
    public void updateStatus(Long memberId, TodoStatusUpdateDto dto, Long todoId) {
        Member member = memberQueryService.getMember(memberId);
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TODO_NOT_FOUND));

        if (!todo.isOwnedBy(member)) {
            throw new BusinessException(ErrorCode.TODO_FORBIDDEN);
        }

        // 이미 상태가 같은 경우 → 무시 (idempotent; 멱등성)
        if (todo.isCompleted() == dto.getCompleted()) {
            return;
        }

        // opu 인 todos 완료 시
        if (todo.getOpu() != null) {
            memberOpuCounterService.completeTodo(member, todo.getOpu());
            memberOpuEventService.completeEvent(member, todo.getOpu());
        }

        // routine 인 todos 완료 시

        todo.updateStatus(dto);
    }

    @Override
    @Transactional
    public void deleteTodo(Long memberId, Long todoId) {
        Member member = memberQueryService.getMember(memberId);
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TODO_NOT_FOUND));

        if (!todo.isOwnedBy(member)) {
            throw new BusinessException(ErrorCode.TODO_FORBIDDEN);
        }

        // todo 멱등성 관리

        todoRepository.delete(todo);
    }

    @Override
    @Transactional
    public void reorderTodo(Long memberId, int newOrder, Long todoId) {
        Member member = memberQueryService.getMember(memberId);
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TODO_NOT_FOUND));

        if (!todo.isOwnedBy(member)) {
            throw new BusinessException(ErrorCode.TODO_FORBIDDEN);
        }

        LocalDate date = todo.getScheduledDate();
        int oldOrder = todo.getSortOrder();
        if (oldOrder == newOrder) return; // idempotent 처리

        if (oldOrder < newOrder) {
            todoRepository.incrementSortOrderBetween(memberId, date, oldOrder + 1, newOrder, -1);
        } else {
            todoRepository.incrementSortOrderBetween(memberId, date, newOrder, oldOrder - 1, 1);
        }

        todo.setSortOrder(newOrder);
    }

    @Override
    @Transactional
    public void clearOpuFromTodos(Long opuId) {
        todoRepository.clearOpuFromTodos(opuId);
    }
}