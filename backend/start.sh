#!/bin/bash

echo "========================================"
echo "学习室管理系统 - 后端启动脚本"
echo "========================================"

# 检查Java
if ! command -v java &> /dev/null; then
    echo "错误: 未检测到Java，请先安装Java 8或更高版本"
    exit 1
fi

# 检查Maven
if ! command -v mvn &> /dev/null; then
    echo "错误: 未检测到Maven，请先安装Maven"
    echo "安装方法: brew install maven (macOS)"
    exit 1
fi

echo "Java版本: $(java -version 2>&1 | head -1)"

# 默认使用H2内存数据库（开发测试，推荐）
PROFILE="h2"

if [ "$1" == "mysql" ]; then
    PROFILE="default"
    echo ""
    echo "⚠️  使用MySQL数据库模式"
    echo "请确保："
    echo "  1. MySQL已启动"
    echo "  2. 已创建数据库: CREATE DATABASE study_room CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    echo "  3. 已修改 src/main/resources/application.yml 中的数据库用户名和密码"
    echo ""
    echo "💡  如果只是快速测试，建议使用默认的H2内存数据库模式"
    echo "   运行方式: ./start.sh  （不带mysql参数）"
else
    echo ""
    echo "✅ 使用H2内存数据库模式（开发测试，推荐）"
    echo "   - 无需安装MySQL"
    echo "   - 数据在内存中，重启后丢失"
    echo "   - H2控制台: http://localhost:8080/h2-console"
    echo "   - JDBC URL: jdbc:h2:mem:studyroom"
fi

echo ""
echo "正在编译项目..."
cd "$(dirname "$0")"
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ 错误: 项目编译失败"
    exit 1
fi

echo ""
echo "🚀 启动后端服务 (端口: 8080)..."
echo "按 Ctrl+C 停止服务"
echo ""

if [ "$PROFILE" == "h2" ]; then
    mvn spring-boot:run -Dspring-boot.run.profiles=h2
else
    mvn spring-boot:run
fi
