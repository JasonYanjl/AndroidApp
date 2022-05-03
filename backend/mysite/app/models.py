from django.db import models


class User(models.Model):

    user_id = models.AutoField(primary_key=True)
    user_username = models.CharField(max_length=64, default='')
    user_password = models.CharField(max_length=64, default='')

    user_mail = models.CharField(max_length=256, default='')

    user_intro = models.CharField(max_length=10240, default='')
    user_avatarid = models.IntegerField(default=-1)

    user_loginJwt = models.CharField(max_length=1024, default='')
    user_verification = models.BooleanField(default=False)
    user_mailVerification = models.CharField(max_length=1024, default='')

    class Meta:
        verbose_name = "User"
        verbose_name_plural = "Users"


class Subscribe(models.Model):

    subscribe_id = models.AutoField(primary_key=True)
    subscribe_user_id = models.IntegerField(default=-1)
    subscribe_subscriber_id = models.IntegerField(default=-1)

    class Meta:
        verbose_name = "Subscribe"
        verbose_name_plural = "Subscribes"


class Block(models.Model):

    block_id = models.AutoField(primary_key=True)
    block_user_id = models.IntegerField(default=-1)
    block_blocker_id = models.IntegerField(default=-1)

    class Meta:
        verbose_name = "Block"
        verbose_name_plural = "Blocks"


class Doc(models.Model):
    doc_id = models.AutoField(primary_key=True)
    doc_user_id = models.IntegerField(default=-1)
    doc_name = models.CharField(default='', blank=True, max_length=128)
    doc_path = models.CharField(default='', max_length=128)

    class Meta:
        verbose_name = 'Doc'
        verbose_name_plural = 'Docs'


class Chat(models.Model):
    chat_id = models.AutoField(primary_key=True)
    chat_sender_id = models.IntegerField(default=-1)
    chat_receiver_id = models.IntegerField(default=-1)
    chat_content = models.CharField(default='', max_length=10240)
    chat_time = models.BigIntegerField(default=0)

    class Meta:
        verbose_name = 'Chat'
        verbose_name_plural = 'Chats'


class Like(models.Model):

    like_id = models.AutoField(primary_key=True)
    like_user_id = models.IntegerField(default=-1)
    like_post_id = models.IntegerField(default=-1)

    class Meta:
        verbose_name = 'Like'
        verbose_name_plural = 'Likes'


class Comment(models.Model):

    comment_id = models.AutoField(primary_key=True)
    comment_user_id = models.IntegerField(default=-1)
    comment_post_id = models.IntegerField(default=-1)
    comment_time = models.BigIntegerField(default=0)
    comment_text = models.CharField(default='', blank=True, max_length=10240)

    class Meta:
        verbose_name = 'Comment'
        verbose_name_plural = 'Comments'


class Post(models.Model):

    post_id = models.AutoField(primary_key=True)
    post_user_id = models.IntegerField(default=-1)
    post_file_id = models.IntegerField(default=-1)
    post_title = models.CharField(default='', blank=True, max_length=128)
    post_text = models.CharField(default='', blank=True, max_length=10240)
    post_type = models.IntegerField(default=-1)
    post_location = models.CharField(default='', blank=True, max_length=128)
    post_cnt_like = models.IntegerField(default=0)
    post_time = models.BigIntegerField(default=0)

    class Meta:
        verbose_name = 'Post'
        verbose_name_plural = 'Posts'
