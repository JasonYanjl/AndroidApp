# Generated by Django 3.1.2 on 2022-05-03 06:39

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0004_doc_doc_name'),
    ]

    operations = [
        migrations.AddField(
            model_name='post',
            name='post_cnt_like',
            field=models.IntegerField(default=0),
        ),
    ]
