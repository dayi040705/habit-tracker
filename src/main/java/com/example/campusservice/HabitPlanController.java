package com.example.campusservice;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/plans")
public class HabitPlanController {

    private final HabitPlanRepository planRepository;
    private final CheckinRecordRepository checkinRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public HabitPlanController(HabitPlanRepository planRepository,
                               CheckinRecordRepository checkinRepository,
                               RedisTemplate<String, Object> redisTemplate) {
        this.planRepository = planRepository;
        this.checkinRepository = checkinRepository;
        this.redisTemplate = redisTemplate;
    }

    // 查看所有计划（带 Redis 缓存）
    @GetMapping
    public List<HabitPlan> getAllPlans() {
        String cacheKey = "all_plans";
        // 1. 先从 Redis 查
        List<HabitPlan> cached = (List<HabitPlan>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached; // 命中缓存，直接返回
        }
        // 2. 缓存没命中，查数据库
        List<HabitPlan> plans = planRepository.findAll();
        // 3. 把结果存到 Redis，并设置 10 分钟过期
        redisTemplate.opsForValue().set(cacheKey, plans, 10, TimeUnit.MINUTES);
        return plans;
    }

    // 创建计划（创建后清除缓存，保证数据一致）
    @PostMapping
    public HabitPlan createPlan(@RequestBody HabitPlan plan) {
        HabitPlan saved = planRepository.save(plan);
        redisTemplate.delete("all_plans"); // 清除缓存
        return saved;
    }

    // 打卡
    @PostMapping("/{planId}/checkin")
    public String checkin(@PathVariable Long planId, @RequestParam(required = false) String note) {
        LocalDate today = LocalDate.now();
        CheckinRecord existing = checkinRepository.findByPlanIdAndCheckinDate(planId, today);
        if (existing != null) {
            return "今天已经打过卡了，明天再来吧！";
        }
        CheckinRecord record = new CheckinRecord();
        record.setPlanId(planId);
        record.setCheckinDate(today);
        record.setNote(note);
        checkinRepository.save(record);
        return "打卡成功！";
    }

    // 查看某个计划的所有打卡记录
    @GetMapping("/{planId}/checkins")
    public List<CheckinRecord> getCheckins(@PathVariable Long planId) {
        return checkinRepository.findByPlanId(planId);
    }

    // 统计连续打卡天数
    @GetMapping("/{planId}/streak")
    public int getStreak(@PathVariable Long planId) {
        LocalDate today = LocalDate.now();
        int streak = 0;
        LocalDate cursor = today;
        while (true) {
            CheckinRecord record = checkinRepository.findByPlanIdAndCheckinDate(planId, cursor);
            if (record != null) {
                streak++;
                cursor = cursor.minusDays(1);
            } else {
                break;
            }
        }
        return streak;
    }
}