# 学习室管理系统 - 后端

## 技术栈

- **Java**: 1.8+
- **Spring Boot**: 2.7.18
- **Spring Data JPA**: ORM框架
- **数据库**: MySQL 8.0 / H2 (内存数据库)
- **构建工具**: Maven

## 项目结构

```
backend/
├── src/main/java/com/studyroom/
│   ├── StudyRoomApplication.java      # 主启动类
│   ├── config/                        # 配置类
│   │   ├── CorsConfig.java            # CORS跨域配置
│   │   └── DataInitializer.java       # 数据初始化（启动时自动创建mock数据）
│   ├── controller/                    # 控制器层（REST API）
│   │   ├── StudentController.java     # 学员管理 API
│   │   ├── KnowledgeController.java   # 知识点管理 API
│   │   ├── PlanController.java        # 周计划管理 API
│   │   ├── RecitationController.java  # 背诵检查 API
│   │   ├── DictationController.java   # 默写检查 API
│   │   ├── HomeworkController.java    # 作业检查 API
│   │   └── ConstantsController.java   # 常量和背诵选项 API
│   ├── dto/                           # 数据传输对象
│   │   ├── PlanDTO.java               # 计划DTO
│   │   ├── ConstantsDTO.java          # 常量DTO
│   │   └── RecitationOptionDTO.java   # 背诵选项DTO
│   ├── entity/                        # 实体类（JPA映射）
│   │   ├── Student.java               # 学员
│   │   ├── Knowledge.java             # 知识点
│   │   ├── Plan.java                  # 周计划
│   │   ├── PlanTask.java              # 计划任务
│   │   ├── Recitation.java            # 背诵记录
│   │   ├── Dictation.java             # 默写记录
│   │   ├── Homework.java              # 作业记录
│   │   └── TextbookRecitation.java    # 教材背诵内容
│   ├── repository/                    # 数据访问层
│   │   ├── StudentRepository.java
│   │   ├── KnowledgeRepository.java
│   │   ├── PlanRepository.java
│   │   ├── RecitationRepository.java
│   │   ├── DictationRepository.java
│   │   ├── HomeworkRepository.java
│   │   └── TextbookRecitationRepository.java
│   └── service/                       # 业务逻辑层
│       ├── StudentService.java
│       ├── KnowledgeService.java
│       ├── PlanService.java
│       ├── RecitationService.java
│       ├── DictationService.java
│       ├── HomeworkService.java
│       └── TextbookRecitationService.java
├── src/main/resources/
│   ├── application.yml                # 主配置（MySQL）
│   ├── application-h2.yml             # H2数据库配置
│   └── init.sql                       # 数据库初始化脚本
├── pom.xml                            # Maven配置
├── start.sh                           # 启动脚本
└── README.md
```

## API接口列表

### 学员管理
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/students` | 获取学员列表（支持name、grade参数搜索） |
| GET | `/api/students/{id}` | 获取单个学员 |
| POST | `/api/students` | 新增学员 |
| PUT | `/api/students/{id}` | 更新学员 |
| DELETE | `/api/students/{id}` | 删除学员 |

### 知识点管理
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/knowledge` | 获取知识点列表（支持grade、subject筛选） |
| GET | `/api/knowledge/{id}` | 获取单个知识点 |
| POST | `/api/knowledge` | 新增知识点 |
| PUT | `/api/knowledge/{id}` | 更新知识点 |
| POST | `/api/knowledge/{id}/toggle-important` | 切换重点标记 |
| DELETE | `/api/knowledge/{id}` | 删除知识点 |

### 周计划管理
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/plans` | 获取所有计划 |
| GET | `/api/plans/{id}` | 获取单个计划 |
| GET | `/api/plans/student/{studentId}` | 获取学员的计划 |
| POST | `/api/plans` | 新增计划 |
| PUT | `/api/plans/{id}` | 更新计划 |
| DELETE | `/api/plans/{id}` | 删除计划 |

### 背诵检查
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/recitations` | 获取所有记录 |
| GET | `/api/recitations/{id}` | 获取单个记录 |
| GET | `/api/recitations/student/{studentId}` | 获取学员的背诵记录 |
| POST | `/api/recitations` | 新增记录 |
| PUT | `/api/recitations/{id}` | 更新记录 |
| DELETE | `/api/recitations/{id}` | 删除记录 |

### 默写检查
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/dictations` | 获取所有记录 |
| GET | `/api/dictations/{id}` | 获取单个记录 |
| GET | `/api/dictations/student/{studentId}` | 获取学员的默写记录 |
| POST | `/api/dictations` | 新增记录 |
| PUT | `/api/dictations/{id}` | 更新记录 |
| DELETE | `/api/dictations/{id}` | 删除记录 |

### 作业检查
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/homeworks` | 获取所有记录 |
| GET | `/api/homeworks/{id}` | 获取单个记录 |
| GET | `/api/homeworks/student/{studentId}` | 获取学员的作业记录 |
| GET | `/api/homeworks/date/{date}` | 获取指定日期的作业记录 |
| POST | `/api/homeworks` | 新增记录 |
| PUT | `/api/homeworks/{id}` | 更新记录 |
| DELETE | `/api/homeworks/{id}` | 删除记录 |

### 常量和配置
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/constants` | 获取系统常量（年级、学科、学习阶段、背诵状态） |
| GET | `/api/constants/recitation-options?subject=语文&grade=七年级` | 获取教材背诵选项 |
| GET | `/api/constants/recitation-options/{id}` | 获取单个背诵选项 |

## 快速开始

### 方式一：使用H2内存数据库（开发测试，推荐）

H2是内存数据库，无需安装MySQL，数据在应用重启后会重置，适合开发测试。

1. **运行启动脚本**：
```bash
cd backend
chmod +x start.sh
./start.sh
# 或者手动运行
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

2. **访问H2控制台**：http://localhost:8080/h2-console
   - JDBC URL: `jdbc:h2:mem:studyroom`
   - 用户名: `sa`
   - 密码: （空）

### 方式二：使用MySQL数据库（生产环境）

1. **安装并启动MySQL**

2. **创建数据库**：
```sql
CREATE DATABASE study_room CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **修改配置**：编辑 `src/main/resources/application.yml`，修改数据库用户名和密码：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/study_room?useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

4. **启动应用**：
```bash
cd backend
mvn spring-boot:run
# 或者
./start.sh mysql
```

## 数据初始化

首次启动时，系统会自动初始化示例数据：
- **4个示例学员**：张三（七年级）、李四（七年级）、王五（八年级）、赵六（九年级）
- **6个示例知识点**：涵盖七年级到九年级的数学、语文、英语
- **2个示例周计划**：包含每日学习任务
- **4个示例背诵记录**：包含熟练、不熟、未完成三种状态
- **4个示例默写记录**：包含正确率统计
- **6个示例作业记录**：包含完成/未完成状态
- **教材背诵数据**：语文和英语各12个年级，每个年级8个背诵内容

## 前端集成

前端项目已配置为调用后端API，API地址为：`http://localhost:8080/api`

前端关键文件：
- `src/services/storage.js`：封装了所有API调用
- `src/services/textbookRecitations.js`：教材背诵内容调用

## 部署说明

### 开发环境
1. 安装Java 8+
2. 安装Maven
3. 运行 `mvn spring-boot:run`

### 生产环境
1. 打包应用：`mvn clean package -DskipTests`
2. 运行Jar包：
```bash
java -jar target/study-room-1.0.0.jar
```
3. 使用MySQL数据库，确保数据持久化

## 注意事项

1. **后端端口**：默认8080，可在 `application.yml` 中修改
2. **CORS配置**：已配置允许所有来源访问（开发环境），生产环境建议限制具体域名
3. **数据持久化**：使用MySQL时数据会持久化，使用H2时数据在应用重启后丢失
4. **表结构**：Spring Data JPA会自动创建和更新表结构，无需手动执行DDL
