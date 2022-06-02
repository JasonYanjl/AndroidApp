from django.shortcuts import render
from django.http import HttpResponse, HttpResponseRedirect, JsonResponse, QueryDict, FileResponse
from django.urls import reverse
from django.views.decorators.csrf import csrf_exempt
from . import models
import json
import time
from django.db.models import Q
from .utils import *


@csrf_exempt
# @LoginCheck
def discover_post(request):
    if (request.method == "POST"):
        try:
            UserId = request.POST.get("userid")
            FileId = request.POST.get("fileid")
            Title = request.POST.get("title")
            Text = request.POST.get("text")
            Type = request.POST.get("type")
            Location = request.POST.get("location")
            NowUser = models.User.objects.get(user_id=UserId)
        except:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)
        NowPost = models.Post(post_user_id=NowUser.user_id,
                              post_file_id=FileId,
                              post_title=Title,
                              post_text=Text,
                              post_type=Type,
                              post_location=Location,
                              post_cnt_like=0,
                              post_time=int(round(time.time() * 1000)))
        NowPost.save()

        PastSubscribe = models.Subscribe.objects.filter(subscribe_subscriber_id=NowUser.user_id)
        if PastSubscribe.count() != 0:
            try:
                NowAdmin = models.User.objects.get(user_username='Administrator')
                for NowSubscribe in PastSubscribe:
                    NowChat = models.Chat(chat_sender_id=NowAdmin.user_id,
                                          chat_receiver_id=NowSubscribe.subscribe_user_id,
                                          chat_content=f'{NowUser.user_username} 发布了新动态。',
                                          chat_time=int(round(time.time() * 1000)))
                    NowChat.save()
            except:
                pass

        return JsonResponse({"postid": NowPost.post_id})

    else:
        return HttpResponse(json.dumps({"Message": "require POST"}),
                            content_type="application/json",
                            status=401)


@csrf_exempt
# @LoginCheck
def discover_like(request):
    if (request.method == "POST"):
        try:
            UserId = request.POST.get("userid")
            post_id = request.POST.get("postid")
            NowUser = models.User.objects.get(user_id=UserId)
        except:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)
        PastLike = models.Like.objects.filter(like_user_id=UserId, like_post_id=post_id)
        if PastLike.count() == 0:
            NowLike = models.Like(like_user_id=UserId, like_post_id=post_id)
            NowLike.save()

            try:
                NowAdmin = models.User.objects.get(user_username='Administrator')
                NowPost = models.Post.objects.get(post_id=post_id)
                NowPost.post_cnt_like = NowPost.post_cnt_like + 1
                NowPost.save()

                NowChat = models.Chat(chat_sender_id=NowAdmin.user_id,
                                      chat_receiver_id=NowPost.post_user_id,
                                      chat_content=f'{NowUser.user_username} 赞了您的动态。',
                                      chat_time=int(round(time.time() * 1000)))
                NowChat.save()
            except:
                pass

        return JsonResponse({"postid":post_id, "Message": "success"})

    else:
        return HttpResponse(json.dumps({"Message": "require POST"}),
                            content_type="application/json",
                            status=401)


@csrf_exempt
# @LoginCheck
def discover_dislike(request):
    if (request.method == "POST"):
        try:
            UserId = request.POST.get("userid")
            post_id = request.POST.get("postid")
            NowUser = models.User.objects.get(user_id=UserId)
        except:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)
        PastLike = models.Like.objects.filter(like_user_id=UserId, like_post_id=post_id)
        if PastLike.count() == 0:
            return HttpResponse(json.dumps({"Message": "haven't like"}),
                                content_type="application/json",
                                status=401)
        for NowLike in PastLike:
            NowPost = models.Post.objects.get(post_id=NowLike.like_post_id)
            NowPost.post_cnt_like = NowPost.post_cnt_like - 1
            NowPost.save()

            NowLike.delete()

        return JsonResponse({"postid": post_id, "Message": "success"})

    else:
        return HttpResponse(json.dumps({"Message": "require POST"}),
                            content_type="application/json",
                            status=401)


@csrf_exempt
# @LoginCheck
def discover_collectlike(request):
    if (request.method == "GET"):
        post_id = request.GET.get('postid')

        if post_id is None:
            return HttpResponse(json.dumps({"Message": "Require postid"}),
                                content_type="application/json",
                                status=401)

        post_id = int(post_id)

        try:
            NowPost = models.Post.objects.get(post_id=post_id)
        except:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)

        PastLike = models.Like.objects.filter(like_post_id=post_id)

        res = []

        for NowLike in PastLike:
            NowLiker= models.User.objects.get(user_id=NowLike.like_user_id)
            res.append({'userid': NowLiker.user_id,
                        'username': NowLiker.user_username})

        return JsonResponse({"postid": post_id, "list": res})

    else:
        return HttpResponse(json.dumps({"Message": "require GET"}),
                            content_type="application/json",
                            status=401)


@csrf_exempt
# @LoginCheck
def discover_comment(request):
    if (request.method == "POST"):
        try:
            UserId = request.POST.get("userid")
            post_id = request.POST.get("postid")
            text = request.POST.get("text")
            post_id = int(post_id)
            NowUser = models.User.objects.get(user_id=UserId)
            NowPost = models.Post.objects.get(post_id=post_id)
        except:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)

        NowComment = models.Comment(comment_user_id=UserId,
                                    comment_post_id=post_id,
                                    comment_time=int(round(time.time() * 1000)),
                                    comment_text=text)
        NowComment.save()

        try:
            NowAdmin = models.User.objects.get(user_username='Administrator')

            NowChat = models.Chat(chat_sender_id=NowAdmin.user_id,
                                  chat_receiver_id=NowPost.post_user_id,
                                  chat_content=f'{NowUser.user_username} 评论了您的动态。',
                                  chat_time=int(round(time.time() * 1000)))
            NowChat.save()
        except:
            pass

        return JsonResponse({"postid":post_id, "commentid": NowComment.comment_id})

    else:
        return HttpResponse(json.dumps({"Message": "require POST"}),
                            content_type="application/json",
                            status=401)


@csrf_exempt
# @LoginCheck
def discover_cancelcomment(request):
    if (request.method == "POST"):
        try:
            UserId = request.POST.get("userid")
            comment_id = request.POST.get("commentid")
            comment_id = int(comment_id)
            NowUser = models.User.objects.get(user_id=UserId)
            NowComment = models.Comment.objects.get(comment_id=comment_id)
        except:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)

        if NowUser.user_id != NowComment.comment_user_id:
            return HttpResponse(json.dumps({"Message": "Error userid"}),
                                content_type="application/json",
                                status=401)
        NowComment.delete()

        return JsonResponse({"commentid": comment_id, "Message": "success"})

    else:
        return HttpResponse(json.dumps({"Message": "require POST"}),
                            content_type="application/json",
                            status=401)


@csrf_exempt
# @LoginCheck
def discover_collectcomment(request):
    if (request.method == "GET"):
        post_id = request.GET.get('postid')

        if post_id is None:
            return HttpResponse(json.dumps({"Message": "Require postid"}),
                                content_type="application/json",
                                status=401)

        post_id = int(post_id)

        try:
            NowPost = models.Post.objects.get(post_id=post_id)
        except:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)

        PastComment = models.Comment.objects.filter(comment_post_id=post_id)
        PastComment = PastComment.order_by("-comment_time")

        res = []

        for NowComment in PastComment:
            NowCommenter = models.User.objects.get(user_id=NowComment.comment_user_id)
            res.append({'userid': NowCommenter.user_id,
                        'username': NowCommenter.user_username,
                        'avatarid': NowCommenter.user_avatarid,
                        'intro': NowCommenter.user_intro,
                        'commentid': NowComment.comment_id,
                        'text': NowComment.comment_text,
                        'time': time2str(NowComment.comment_time)})

        return JsonResponse({"postid": post_id, "list": res})

    else:
        return HttpResponse(json.dumps({"Message": "require GET"}),
                            content_type="application/json",
                            status=401)


@csrf_exempt
# @LoginCheck
def discover_get(request):
    if (request.method == "GET"):
        user_id = request.GET.get('userid')
        subscribe = request.GET.get('subscribe')
        Sort = request.GET.get('sort')

        if user_id is None or subscribe is None:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)

        try:
            user_id = int(user_id)
            subscribe = int(subscribe)
            NowUser = models.User.objects.get(user_id=user_id)
        except:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)

        if Sort is None:
            Sort = 'time'
        else:
            try:
                Sort = str(Sort)
            except:
                return HttpResponse(json.dumps({"Message": "Error Params"}),
                                    content_type="application/json",
                                    status=401)

        if subscribe == 0:
            NowRes = models.Post.objects.all()
            if NowRes is not None:
                PastBlock = models.Block.objects.filter(block_user_id=user_id)
                for NowBlock in PastBlock:
                    NowRes = NowRes.filter(~Q(post_user_id=NowBlock.block_blocker_id))

        elif subscribe == 1:
            NowRes = None
            PastSubscribe = models.Subscribe.objects.filter(subscribe_user_id=user_id)
            for NowSubscribe in PastSubscribe:
                PastBlock = models.Block.objects.filter(block_user_id=user_id, block_blocker_id=NowSubscribe.subscribe_subscriber_id)
                if PastBlock.count() > 0:
                    continue
                if NowRes is None:
                    NowRes = models.Post.objects.filter(post_user_id=NowSubscribe.subscribe_subscriber_id)
                else:
                    tmpRes = models.Post.objects.filter(post_user_id=NowSubscribe.subscribe_subscriber_id)
                    NowRes = NowRes.union(tmpRes)
        else:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)

        # if NowRes is not None:
        #     PastBlock = models.Block.objects.filter(block_user_id=user_id)
        #     for NowBlock in PastBlock:
        #         NowRes = NowRes.filter(~Q(post_user_id=NowBlock.block_blocker_id))

        if Sort == 'time':
            if NowRes is not None:
                NowRes = NowRes.order_by("-post_time")
        elif Sort == 'like':
            if NowRes is not None:
                NowRes = NowRes.order_by("-post_cnt_like")
        else:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)

        res = []

        if NowRes is not None:
            for NowPost in NowRes:
                try:
                    NowPoster = models.User.objects.get(user_id=NowPost.post_user_id)
                    tmpSub = 0
                    tmpBlock = 0
                    PastSubscribe = models.Subscribe.objects.filter(subscribe_user_id=NowUser.user_id,
                                                                    subscribe_subscriber_id=NowPoster.user_id)
                    if PastSubscribe.count() > 0:
                        tmpSub = 1
                    PastBlock = models.Block.objects.filter(block_user_id=NowUser.user_id,
                                                            block_blocker_id=NowPoster.user_id)
                    if PastBlock.count()>0:
                        tmpBlock = 1

                    tmpAvatarFilename = ""

                    try:
                        NowFile = models.Doc.objects.get(doc_id=NowPoster.user_avatarid)
                        tmpAvatarFilename = NowFile.doc_name
                    except:
                        pass

                    filename = ""

                    try:
                        NowFile = models.Doc.objects.get(doc_id=NowPost.post_file_id)
                        filename = NowFile.doc_name
                    except:
                        pass

                    res.append({"postid": NowPost.post_id,
                             "userid": NowPoster.user_id,
                             "username": NowPoster.user_username,
                                "avatarid": NowPoster.user_avatarid,
                                "avatarfilename": tmpAvatarFilename,
                                "intro": NowPoster.user_intro,
                             "fileid": NowPost.post_file_id,
                                "filename": filename,
                             "title": NowPost.post_title,
                             "text": NowPost.post_text,
                             "type": NowPost.post_type,
                             "location": NowPost.post_location,
                             "time": time2str(NowPost.post_time),
                             "subscribe": tmpSub,
                             "block": tmpBlock})
                except:
                    pass

        return JsonResponse({"list": res})

    else:
        return HttpResponse(json.dumps({"Message": "require GET"}),
                            content_type="application/json",
                            status=401)


@csrf_exempt
# @LoginCheck
def discover_search(request):
    if (request.method == "GET"):
        user_id = request.GET.get('userid')
        search_type = request.GET.get('searchtype')
        search_text = request.GET.get('searchtext')
        post_type = request.GET.get('posttype')
        Sort = request.GET.get('sort')

        if search_type is None or search_text is None:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)

        try:
            search_type = str(search_type)
            search_text = str(search_text)
            search_text_list = search_text.split(' ')
            user_id = int(user_id)
            NowUser = models.User.objects.get(user_id=user_id)
        except:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)

        if Sort is None:
            Sort = 'time'
        else:
            try:
                Sort = str(Sort)
            except:
                return HttpResponse(json.dumps({"Message": "Error Params"}),
                                    content_type="application/json",
                                    status=401)
        NowRes = models.Post.objects.all()
        if search_type == "title":
            for tmp_text in search_text_list:
                NowRes = NowRes.filter(post_title__contains=tmp_text)
        elif search_type == "text":
            for tmp_text in search_text_list:
                NowRes = NowRes.filter(post_text__contains=tmp_text)
        elif search_type == "username":
            PastUser = models.User.objects.all()
            for tmp_text in search_text_list:
                PastUser = PastUser.filter(user_username__contains=tmp_text)

            NowRes = None

            for NowPostUser in PastUser:
                if NowRes is None:
                    NowRes = models.Post.objects.filter(post_user_id=NowPostUser.user_id)
                else:
                    tmpRes = models.Post.objects.filter(post_user_id=NowPostUser.user_id)
                    NowRes = NowRes.union(tmpRes)

        else:
            return HttpResponse(json.dumps({"Message": "Error Params search type"}),
                                content_type="application/json",
                                status=401)

        if post_type is None:
            pass
        else:
            try:
                post_type = int(post_type)
            except:
                return HttpResponse(json.dumps({"Message": "Error Params post type"}),
                                    content_type="application/json",
                                    status=401)

            NowRes = NowRes.filter(post_type=post_type)

        if Sort == 'time':
            if NowRes is not None:
                NowRes = NowRes.order_by("-post_time")
        elif Sort == 'like':
            if NowRes is not None:
                NowRes = NowRes.order_by("-post_cnt_like")
        else:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)

        res = []

        if NowRes is not None:
            for NowPost in NowRes:
                try:
                    NowPoster = models.User.objects.get(user_id=NowPost.post_user_id)
                    tmpSub = 0
                    tmpBlock = 0
                    PastSubscribe = models.Subscribe.objects.filter(subscribe_user_id=NowUser.user_id,
                                                                    subscribe_subscriber_id=NowPoster.user_id)
                    if PastSubscribe.count() > 0:
                        tmpSub = 1
                    PastBlock = models.Block.objects.filter(block_user_id=NowUser.user_id,
                                                            block_blocker_id=NowPoster.user_id)
                    if PastBlock.count() > 0:
                        tmpBlock = 1

                    tmpAvatarFilename = ""

                    try:
                        NowFile = models.Doc.objects.get(doc_id=NowPoster.user_avatarid)
                        tmpAvatarFilename = NowFile.doc_name
                    except:
                        pass

                    filename = ""

                    try:
                        NowFile = models.Doc.objects.get(doc_id=NowPost.post_file_id)
                        filename = NowFile.doc_name
                    except:
                        pass

                    res.append({"postid": NowPost.post_id,
                                "userid": NowPoster.user_id,
                                "username": NowPoster.user_username,
                                "avatarid": NowPoster.user_avatarid,
                                "avatarfilename": tmpAvatarFilename,
                                "intro": NowPoster.user_intro,
                                "fileid": NowPost.post_file_id,
                                "filename": filename,
                                "title": NowPost.post_title,
                                "text": NowPost.post_text,
                                "type": NowPost.post_type,
                                "time": time2str(NowPost.post_time),
                                "location": NowPost.post_location,
                                "subscribe": tmpSub,
                                "block": tmpBlock})
                except:
                    pass

        return JsonResponse({"list": res})

    else:
        return HttpResponse(json.dumps({"Message": "require GET"}),
                            content_type="application/json",
                            status=401)


@csrf_exempt
# @LoginCheck
def discover_allpost(request):
    if (request.method == "GET"):
        user_id = request.GET.get('userid')
        poster_id = request.GET.get('posterid')

        if user_id is None or poster_id is None:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)

        try:
            user_id = int(user_id)
            poster_id = int(poster_id)
            NowUser = models.User.objects.get(user_id=user_id)
            NowPoster = models.User.objects.get(user_id=poster_id)
        except:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)

        NowRes = models.Post.objects.filter(post_user_id=NowPoster.user_id)
        NowRes = NowRes.order_by("-post_time")

        res = []

        if NowRes is not None:
            for NowPost in NowRes:
                try:
                    tmpSub = 0
                    tmpBlock = 0
                    PastSubscribe = models.Subscribe.objects.filter(subscribe_user_id=NowUser.user_id,
                                                                    subscribe_subscriber_id=NowPoster.user_id)
                    if PastSubscribe.count() > 0:
                        tmpSub = 1
                    PastBlock = models.Block.objects.filter(block_user_id=NowUser.user_id,
                                                            block_blocker_id=NowPoster.user_id)
                    if PastBlock.count() > 0:
                        tmpBlock = 1

                    tmpAvatarFilename = ""

                    try:
                        NowFile = models.Doc.objects.get(doc_id=NowPoster.user_avatarid)
                        tmpAvatarFilename = NowFile.doc_name
                    except:
                        pass

                    filename = ""

                    try:
                        NowFile = models.Doc.objects.get(doc_id=NowPost.post_file_id)
                        filename = NowFile.doc_name
                    except:
                        pass

                    res.append({"postid": NowPost.post_id,
                                "userid": NowPoster.user_id,
                                "username": NowPoster.user_username,
                                "avatarid": NowPoster.user_avatarid,
                                "avatarfilename": tmpAvatarFilename,
                                "intro": NowPoster.user_intro,
                                "fileid": NowPost.post_file_id,
                                "filename": filename,
                                "title": NowPost.post_title,
                                "text": NowPost.post_text,
                                "type": NowPost.post_type,
                                "time": time2str(NowPost.post_time),
                                "location": NowPost.post_location,
                                "subscribe": tmpSub,
                                "block": tmpBlock})
                except:
                    pass

        return JsonResponse({"list": res})

    else:
        return HttpResponse(json.dumps({"Message": "require GET"}),
                            content_type="application/json",
                            status=401)


@csrf_exempt
# @LoginCheck
def discover_deatil(request):
    if (request.method == "GET"):
        post_id = request.GET.get('postid')

        if post_id is None:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)

        try:
            post_id = int(post_id)
            NowPost = models.Post.objects.get(post_id=post_id)
            NowUser = models.User.objects.get(user_id=NowPost.post_user_id)
        except:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)

        tmpAvatarFilename = ""

        try:
            NowFile = models.Doc.objects.get(doc_id=NowUser.user_avatarid)
            tmpAvatarFilename = NowFile.doc_name
        except:
            pass

        filename = ""

        try:
            NowFile = models.Doc.objects.get(doc_id=NowPost.post_file_id)
            filename = NowFile.doc_name
        except:
            pass

        return JsonResponse({"postid": NowPost.post_id,
                             "userid": NowUser.user_id,
                             "username": NowUser.user_username,
                             "avatarid": NowUser.user_avatarid,
                             "avatarfilename": tmpAvatarFilename,
                             "intro": NowUser.user_intro,
                             "fileid": NowPost.post_file_id,
                             "filename": filename,
                             "title": NowPost.post_title,
                             "text": NowPost.post_text,
                             "type": NowPost.post_type,
                             "time": time2str(NowPost.post_time),
                             "location": NowPost.post_location})

    else:
        return HttpResponse(json.dumps({"Message": "require GET"}),
                            content_type="application/json",
                            status=401)
