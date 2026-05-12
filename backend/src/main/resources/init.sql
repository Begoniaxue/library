-- 学习室管理系统数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS study_room 
    DEFAULT CHARACTER SET utf8mb4 
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE study_room;

-- 注意：Spring Data JPA会自动创建表结构，此脚本仅用于手动创建数据库
-- 如果需要手动创建表，可以使用以下SQL（可选）

/*
-- 学员表
CREATE TABLE IF NOT EXISTS students (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    grade VARCHAR(50) NOT NULL,
    study_stage VARCHAR(50) NOT NULL,
    contact VARCHAR(100),
    created_at DATETIME,
    updated_at DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 知识点表
CREATE TABLE IF NOT EXISTS knowledge (
    id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    grade VARCHAR(50) NOT NULL,
    subject VARCHAR(50) NOT NULL,
    content TEXT,
    is_important BOOLEAN DEFAULT FALSE,
    created_at DATETIME,
    updated_at DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 周计划表
CREATE TABLE IF NOT EXISTS plans (
    id VARCHAR(36) PRIMARY KEY,
    student_id VARCHAR(36) NOT NULL,
    week_start DATE NOT NULL,
    week_end DATE NOT NULL,
    created_at DATETIME,
    updated_at DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 计划任务表
CREATE TABLE IF NOT EXISTS plan_tasks (
    id VARCHAR(36) PRIMARY KEY,
    plan_id VARCHAR(36) NOT NULL,
    day VARCHAR(20) NOT NULL,
    content VARCHAR(500) NOT NULL,
    knowledge VARCHAR(200),
    completed BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (plan_id) REFERENCES plans(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 背诵记录表
CREATE TABLE IF NOT EXISTS recitations (
    id VARCHAR(36) PRIMARY KEY,
    student_id VARCHAR(36) NOT NULL,
    subject VARCHAR(50) NOT NULL,
    content TEXT,
    status VARCHAR(50) NOT NULL,
    check_date DATE NOT NULL,
    remark TEXT,
    created_at DATETIME,
    updated_at DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 默写记录表
CREATE TABLE IF NOT EXISTS dictations (
    id VARCHAR(36) PRIMARY KEY,
    student_id VARCHAR(36) NOT NULL,
    subject VARCHAR(50) NOT NULL,
    content TEXT,
    correct_count INT,
    total_count INT,
    accuracy DOUBLE,
    errors TEXT,
    check_date DATE NOT NULL,
    created_at DATETIME,
    updated_at DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 作业记录表
CREATE TABLE IF NOT EXISTS homeworks (
    id VARCHAR(36) PRIMARY KEY,
    student_id VARCHAR(36) NOT NULL,
    subject VARCHAR(50) NOT NULL,
    content TEXT,
    completed BOOLEAN DEFAULT FALSE,
    date DATE NOT NULL,
    created_at DATETIME,
    updated_at DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 教材背诵内容表
CREATE TABLE IF NOT EXISTS textbook_recitations (
    id VARCHAR(36) PRIMARY KEY,
    subject VARCHAR(50) NOT NULL,
    grade VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    category VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
*/
