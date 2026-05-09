# 习惯追踪打卡系统 (Habit Tracker)

## 技术栈
Java 17 + Spring Boot + Spring Data JPA + MySQL + Redis + Docker

## 功能
- 创建打卡计划（设定名称、描述、目标天数）
- 每日打卡（自动防重复打卡）
- 查看打卡记录（按计划查询）
- 连续打卡天数统计（日期回溯算法，动态计算）
- Redis 缓存热点计划列表（10 分钟过期 + 创建后主动清除缓存）
- Docker Compose 一键部署（Spring Boot + MySQL + Redis）

## 项目结构
- CampusserviceApplication.java — Spring Boot 主启动类
- HabitPlan.java — 打卡计划实体类（对应 habit_plan 表）
- CheckinRecord.java — 打卡记录实体类（对应 checkin_record 表）
- HabitPlanController.java — REST API 控制器（计划 CRUD + 打卡 + 连续天数）
- HabitPlanRepository.java — 计划数据访问层
- CheckinRecordRepository.java — 打卡记录数据访问层（含自定义查询）
- RedisConfig.java — Redis 序列化配置（解决 LocalDateTime 序列化问题）

## API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /plans | 查看所有打卡计划 |
| POST | /plans | 创建打卡计划 |
| POST | /plans/{planId}/checkin | 对某个计划打卡 |
| GET | /plans/{planId}/checkins | 查看某个计划的打卡记录 |
| GET | /plans/{planId}/streak | 查看某个计划的连续打卡天数 |

## 运行方式
1. 安装 MySQL，创建数据库 habit_tracker
2. 安装 Redis，默认端口 6379
3. 修改 application.properties 中的数据库密码
4. 运行 CampusserviceApplication.java
5. 浏览器访问 http://localhost:8080/plans
