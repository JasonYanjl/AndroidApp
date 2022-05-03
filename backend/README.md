在该文件夹下运行以下两条命令进行数据库迁移

```python
python manage.py makemigrations
python manage.py migrate
```

运行下面的命令启动后端

```python
python manage.py runserver 8000
```

运行下面的命令新建超级用户来管理数据库中的数据

```python
python manage.py createsuperuser
```

在127.0.0.1:8000/admin中登录即可查看数据库中数据。