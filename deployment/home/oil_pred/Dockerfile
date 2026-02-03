# 使用官方的 Python 镜像作为基础镜像
FROM python:3.9-slim

# 设置工作目录
WORKDIR /app

# 复制项目文件到容器中
COPY . /app

# 安装依赖库
RUN pip3 install -i https://pypi.tuna.tsinghua.edu.cn/simple --upgrade pip
RUN pip3 install -i https://pypi.tuna.tsinghua.edu.cn/simple -r requirements.txt

# 暴露 Flask 默认端口
EXPOSE 5000

# 设置环境变量，避免 Python 缓存和警告
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

# 启动 Flask 服务
CMD ["python", "app.py"]
