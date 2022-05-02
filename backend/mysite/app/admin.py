from django.contrib import admin
from . import models
# Register your models here.


admin.site.register(models.User)
admin.site.register(models.Subscribe)
admin.site.register(models.Block)
admin.site.register(models.Doc)
admin.site.register(models.Like)
admin.site.register(models.Post)
admin.site.register(models.Comment)
admin.site.register(models.Chat)
