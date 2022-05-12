from django.shortcuts import render
from django.http import HttpResponse,HttpResponseRedirect,JsonResponse,QueryDict
from django.urls import reverse
from django.views.decorators.csrf import csrf_exempt
from . import models
import json
import random
import hashlib
import jwt
import time
from django.core.mail import send_mail
from .utils import *
# Create your views here.


def AddAdministrator():
    print('Add Administrator')
    try:
        NowUser = models.User.objects.get(user_username='Administrator')
        NowUser.user_verification = 1
        NowUser.user_password = EncryptPassword('123456')
        NowUser.save()
        print('Administrator exists')
    except:
        NowUser = models.User(user_username='Administrator', user_password=EncryptPassword('123456'),
                              user_mail='jasonvyan@163.com',
                              user_intro='',
                              user_avatarid=-1,
                              user_loginJwt='',
                              user_verification=1,
                              user_mailVerification='')
        NowUser.save()
        print('Created Administrator')


try:
    AddAdministrator()
except:
    pass


@csrf_exempt
def register(request):
    if (request.method == "POST"):
        try:
            Username = request.POST.get("username")
            Password = request.POST.get("password")
            Mail = request.POST.get("mail")
        except:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)

        PastUser = models.User.objects.filter(user_username=Username)
        if (PastUser.count() > 0):
            return HttpResponse(json.dumps({"errorMessage": "username exists"}),
                                content_type="application/json",
                                status=400)

        PastUser = models.User.objects.filter(user_mail=Mail)
        if (PastUser.count() > 0):
            return HttpResponse(json.dumps({"errorMessage": "mail exists"}),
                                content_type="application/json",
                                status=400)

        MailVerification = ToMd5(Username + str(random.randint(1, 1000000)))[-6:]
        print(MailVerification)
        NowUser = models.User(user_username=Username, user_password=EncryptPassword(Password),
                              user_mail=Mail,
                              user_intro='',
                              user_avatarid=-1,
                              user_loginJwt='',
                              user_verification=0,
                              user_mailVerification=MailVerification)
        NowUser.save()
        send_mail('Login Verification', MailVerification, 'jasonvyan@163.com',
                  [Mail], fail_silently=False)

        return JsonResponse({"userid": NowUser.user_id, "Message": 'success'})
    else:
        return HttpResponse(json.dumps({"errorMessage": "require POST"}),
                            content_type="application/json",
                            status=401)


@csrf_exempt
def register_verify(request):
    if (request.method == "POST"):
        try:
            Mail = request.POST.get('mail')
            Verification = request.POST.get("verification")
        except:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)

        try:
            NowUser = models.User.objects.get(user_mail=Mail)
            if NowUser.user_mailVerification == Verification:
                NowUser.user_verification = 1
                NowUser.user_mailVerification = ''
                NowUser.save()
                return JsonResponse({"message": 'success'})
            else:
                return HttpResponse(json.dumps({"errorMessage": "Verification invalid"}),
                                    content_type="application/json",
                                    status=400)
        except:
            return HttpResponse(json.dumps({"errorMessage": "Verification invalid"}),
                                content_type="application/json",
                                status=400)
    else:
        return HttpResponse(json.dumps({"errorMessage": "require POST"}),
                            content_type="application/json",
                            status=401)


@csrf_exempt
def login(request):
    if (request.method == "POST"):
        try:
            Username = request.POST.get("username")
            Password = request.POST.get("password")
        except:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)
        
        try:
            NowUser = models.User.objects.get(user_username=Username)
            if (NowUser.user_password != EncryptPassword(Password)):
                return HttpResponse(json.dumps({"Message": "Wrong password"}),
                                    content_type="application/json",
                                    status=400)
            jwt = GenerateJWT({"userid": NowUser.user_id,
                               "username": NowUser.user_username,
                               "userjwtrank": random.randint(1, 1e9)})
            NowUser.user_loginJwt = jwt
            NowUser.save()
            return JsonResponse({"Message": "success",
                                 "userid": NowUser.user_id,
                                 "username": NowUser.user_username,
                                 "jwt": NowUser.user_loginJwt})

        except:
            return HttpResponse(json.dumps({"Message": "No such user"}),
                                content_type="application/json",
                                status=400)

    else:
        return HttpResponse(json.dumps({"Message": "require POST"}),
                            content_type="application/json",
                            status=401)


@csrf_exempt
# @LoginCheck
def logout(request):
    if (request.method == "POST"):
        try:
            UserId = request.POST.get("userid")
            NowUser = models.User.objects.get(user_id=UserId)
        except:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)
        NowUser.user_loginJwt = ""
        NowUser.save()

        return JsonResponse({"Message": "success"})

    else:
        return HttpResponse(json.dumps({"Message": "require POST"}),
                            content_type="application/json",
                            status=401)


@csrf_exempt
# @LoginCheck
def hello_user(request):
    if (request.method == "GET"):
        try:
            # Token = request.META.get("HTTP_AUTHORIZATION")
            # Payload = DecodeJWT(Token)
            # username = Payload['username']
            userid = request.GET.get('userid')
            userid = int(userid)
            print(userid)
            NowUser = models.User.objects.get(user_id=userid)
            return JsonResponse({"userid": NowUser.user_id,
                                 "username": NowUser.user_username,
                                 "avatarid": NowUser.user_avatarid,
                                 "intro": NowUser.user_intro})
        except:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)
    else:
        return HttpResponse(json.dumps({"Message": "require GET"}),
                            content_type="application/json",
                            status=401)


@csrf_exempt
# @LoginCheck
def passwd(request):
    if (request.method == "POST"):
        try:
            UserId = request.POST.get("userid")
            OldPassword = request.POST.get("oldpassword")
            NewPassword = request.POST.get("newpassword")
            NowUser = models.User.objects.get(user_id=UserId)
        except:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)

        if NowUser.user_password != EncryptPassword(OldPassword):
            return HttpResponse(json.dumps({"Message": "Wrong Password"}),
                                content_type="application/json",
                                status=401)

        NowUser.user_password = EncryptPassword(NewPassword)
        NowUser.user_jwt = ""
        NowUser.save()

        try:
            NowAdmin = models.User.objects.get(user_username='Administrator')
            NowChat = models.Chat(chat_sender_id=NowAdmin.user_id,
                                  chat_receiver_id=NowUser.user_id,
                                  chat_content=f'修改密码成功',
                                  chat_time=int(round(time.time() * 1000)))
            NowChat.save()
        except:
            pass

        return JsonResponse({"Message": "success"})

    else:
        return HttpResponse(json.dumps({"Message": "require POST"}),
                            content_type="application/json",
                            status=401)


@csrf_exempt
# @LoginCheck
def modify(request):
    if (request.method == "POST"):
        try:
            UserId = request.POST.get("userid")
            Key = request.POST.get("key")
            Desc = request.POST.get("desc")
            NowUser = models.User.objects.get(user_id=UserId)
        except:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)

        if Key == 'avatarid':
            try:
                NowUser.user_avatarid = Desc
                NowUser.save()
                try:
                    NowAdmin = models.User.objects.get(user_username='Administrator')
                    NowChat = models.Chat(chat_sender_id=NowAdmin.user_id,
                                          chat_receiver_id=NowUser.user_id,
                                          chat_content=f'修改头像成功',
                                          chat_time=int(round(time.time() * 1000)))
                    NowChat.save()
                except:
                    pass
                return JsonResponse({"Message": "success"})
            except:
                return HttpResponse(json.dumps({"Message": "Error Params"}),
                                    content_type="application/json",
                                    status=401)
        elif Key == 'intro':
            try:
                NowUser.user_intro = Desc
                NowUser.save()
                try:
                    NowAdmin = models.User.objects.get(user_username='Administrator')
                    NowChat = models.Chat(chat_sender_id=NowAdmin.user_id,
                                          chat_receiver_id=NowUser.user_id,
                                          chat_content=f'修改简介成功',
                                          chat_time=int(round(time.time() * 1000)))
                    NowChat.save()
                except:
                    pass
                return JsonResponse({"Message": "success"})
            except:
                return HttpResponse(json.dumps({"Message": "Error Params"}),
                                    content_type="application/json",
                                    status=401)

        elif Key == 'username':
            PastUser = models.User.objects.filter(user_username=Desc)
            if (PastUser.count() > 0):
                return HttpResponse(json.dumps({"errorMessage": "username exists"}),
                                    content_type="application/json",
                                    status=400)

            try:
                NowUser.user_username = Desc
                NowUser.save()
                try:
                    NowAdmin = models.User.objects.get(user_username='Administrator')
                    NowChat = models.Chat(chat_sender_id=NowAdmin.user_id,
                                          chat_receiver_id=NowUser.user_id,
                                          chat_content=f'修改用户名成功',
                                          chat_time=int(round(time.time() * 1000)))
                    NowChat.save()
                except:
                    pass
                return JsonResponse({"Message": "success"})
            except:
                return HttpResponse(json.dumps({"Message": "Error Params"}),
                                    content_type="application/json",
                                    status=401)

    else:
        return HttpResponse(json.dumps({"Message": "require POST"}),
                            content_type="application/json",
                            status=401)


@csrf_exempt
# @LoginCheck
def subscribe(request):
    if (request.method == "POST"):
        try:
            UserId = request.POST.get("userid")
            subscriber_id = request.POST.get("subscriberid")
            NowUser = models.User.objects.get(user_id=UserId)
            NowSubscriber = models.User.objects.get(user_id=subscriber_id)
        except:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)
        PastSubscribe = models.Subscribe.objects.filter(subscribe_user_id=UserId, subscribe_subscriber_id=subscriber_id)
        if PastSubscribe.count() == 0:
            NowSubscribe = models.Subscribe(subscribe_user_id=UserId, subscribe_subscriber_id=subscriber_id)
            NowSubscribe.save()

            NowAdmin = models.User.objects.get(user_username='Administrator')
            NowChat = models.Chat(chat_sender_id=NowAdmin.user_id,
                                  chat_receiver_id=NowSubscriber.user_id,
                                  chat_content=f'{NowUser.user_username} 关注了您。',
                                  chat_time=int(round(time.time() * 1000)))
            NowChat.save()

        return JsonResponse({"Message": "success"})

    else:
        return HttpResponse(json.dumps({"Message": "require POST"}),
                            content_type="application/json",
                            status=401)


@csrf_exempt
# @LoginCheck
def unsubscribe(request):
    if (request.method == "POST"):
        try:
            UserId = request.POST.get("userid")
            subscriber_id = request.POST.get("subscriberid")
            NowUser = models.User.objects.get(user_id=UserId)
            NowSubscriber = models.User.objects.get(user_id=subscriber_id)
        except:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)
        PastSubscribe = models.Subscribe.objects.filter(subscribe_user_id=UserId, subscribe_subscriber_id=subscriber_id)
        if PastSubscribe.count() == 0:
            return HttpResponse(json.dumps({"Message": "haven't subscribe"}),
                                content_type="application/json",
                                status=401)
        for NowSubscribe in PastSubscribe:
            NowSubscribe.delete()
        return JsonResponse({"Message": "success"})

    else:
        return HttpResponse(json.dumps({"Message": "require POST"}),
                            content_type="application/json",
                            status=401)


@csrf_exempt
# @LoginCheck
def subscribelist(request):
    if (request.method == "GET"):
        Username = request.GET.get('username')

        if Username is None:
            return HttpResponse(json.dumps({"Message": "Require username"}),
                                content_type="application/json",
                                status=401)

        Username = str(Username)

        try:
            NowUser = models.User.objects.get(user_username=Username)
        except:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)

        PastSubscribe = models.Subscribe.objects.filter(subscribe_user_id=NowUser.user_id)

        res = []

        for NowSubscribe in PastSubscribe:
            NowSubscriber = models.User.objects.get(user_id=NowSubscribe.subscribe_subscriber_id)
            res.append({'userid': NowSubscriber.user_id,
                        'username': NowSubscriber.user_username})

        return JsonResponse({"list": res})

    else:
        return HttpResponse(json.dumps({"Message": "require GET"}),
                            content_type="application/json",
                            status=401)


@csrf_exempt
# @LoginCheck
def block(request):
    if (request.method == "POST"):
        try:
            UserId = request.POST.get("userid")
            blocker_id = request.POST.get("blockerid")
            NowUser = models.User.objects.get(user_id=UserId)
            NowBlocker = models.User.objects.get(user_id=blocker_id)
        except:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)

        PastBlock = models.Block.objects.filter(block_user_id=UserId, block_blocker_id=blocker_id)
        if PastBlock.count() == 0:
            NowBlock = models.Block(block_user_id=UserId, block_blocker_id=blocker_id)
            NowBlock.save()
        return JsonResponse({"Message": "success"})

    else:
        return HttpResponse(json.dumps({"Message": "require POST"}),
                            content_type="application/json",
                            status=401)


@csrf_exempt
# @LoginCheck
def unblock(request):
    if (request.method == "POST"):
        try:
            UserId = request.POST.get("userid")
            blocker_id = request.POST.get("blockerid")
            NowUser = models.User.objects.get(user_id=UserId)
            NowBlocker = models.User.objects.get(user_id=blocker_id)
        except:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)
        PastBlock = models.Block.objects.filter(block_user_id=UserId, block_blocker_id=blocker_id)
        if PastBlock.count() == 0:
            return HttpResponse(json.dumps({"Message": "haven't block"}),
                                content_type="application/json",
                                status=401)
        for NowBlock in PastBlock:
            NowBlock.delete()
        return JsonResponse({"Message": "success"})

    else:
        return HttpResponse(json.dumps({"Message": "require POST"}),
                            content_type="application/json",
                            status=401)


@csrf_exempt
# @LoginCheck
def blocklist(request):
    if (request.method == "GET"):
        Username = request.GET.get('username')

        if Username is None:
            return HttpResponse(json.dumps({"Message": "Require username"}),
                                content_type="application/json",
                                status=401)

        Username = str(Username)

        try:
            NowUser = models.User.objects.get(user_username=Username)
        except:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)

        PastBlock = models.Block.objects.filter(block_user_id=NowUser.user_id)

        res = []

        for NowBlock in PastBlock:
            NowBlocker = models.User.objects.get(user_id=NowBlock.block_blocker_id)
            res.append({'userid': NowBlocker.user_id,
                        'username': NowBlocker.user_username})

        return JsonResponse({"list": res})

    else:
        return HttpResponse(json.dumps({"Message": "require GET"}),
                            content_type="application/json",
                            status=401)