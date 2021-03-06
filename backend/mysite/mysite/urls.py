"""mysite URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/2.1/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.conf.urls import url, include
from django.contrib import admin
from django.urls import path, re_path
from app import views, views_file, views_chat, views_discover
import re


# Check End
    
urlpatterns = [
    url(r'^app/', include('app.urls', namespace='app')),
    path('admin/', admin.site.urls),

    # ------

    path('api/register', views.register, name='Register'),
    path('api/register/verify', views.register_verify, name='RegisterVerify'),
    path('api/user/login', views.login, name='Login'),
    path('api/user/logout', views.logout, name='Logout'),
    path('api/user/hello-user', views.hello_user, name="HelloUser"),
    path('api/user/passwd', views.passwd, name="Passwd"),
    path('api/user/modify', views.modify, name="Modify"),
    path('api/user/subscribe', views.subscribe, name="Subscribe"),
    path('api/user/unsubscribe', views.unsubscribe, name="Unsubscribe"),
    path('api/user/subscribelist', views.subscribelist, name="Subscribelist"),
    path('api/user/block', views.block, name="Block"),
    path('api/user/unblock', views.unblock, name="Unblock"),
    path('api/user/blocklist', views.blocklist, name="Blocklist"),

    # ------

    path('api/file/upload', views_file.upload, name="Upload"),
    path('api/file/download', views_file.download, name="Download"),
    path('api/file/filename', views_file.getfilename, name="GetFilename"),
    # ------

    path('api/chat/get', views_chat.chat_get, name="Chatget"),

    # ------

    path('api/discover/post', views_discover.discover_post, name="DiscoverPost"),
    path('api/discover/like', views_discover.discover_like, name="DiscoverLike"),
    path('api/discover/dislike', views_discover.discover_dislike, name="DiscoverDislike"),
    path('api/discover/collectlike', views_discover.discover_collectlike, name="DiscoverCollectlike"),
    path('api/discover/comment', views_discover.discover_comment, name="DiscoverComment"),
    path('api/discover/cancelcomment', views_discover.discover_cancelcomment, name="DiscoverCancelcomment"),
    path('api/discover/collectcomment', views_discover.discover_collectcomment, name="DiscoverCollectcomment"),
    path('api/discover/get', views_discover.discover_get, name="DiscoverGet"),
    path('api/discover/search', views_discover.discover_search, name="DiscoverSearch"),
    path('api/discover/allpost', views_discover.discover_allpost, name="DiscoverAllPost"),
    path('api/discover/detail', views_discover.discover_deatil, name="DiscoverDetail"),

]
