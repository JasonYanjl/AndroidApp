from django.shortcuts import render
from django.http import HttpResponse, HttpResponseRedirect, JsonResponse, QueryDict, FileResponse
from django.urls import reverse
from django.views.decorators.csrf import csrf_exempt
from . import models
import json
import time
from .utils import *


@csrf_exempt
# @LoginCheck
def chat_get(request):
    if request.method == 'GET':
        senderid = request.GET.get('senderid')
        receiverid = request.GET.get('receiverid')

        if senderid is None:
            return HttpResponse(json.dumps({"Message": "Require senderid"}),
                                content_type="application/json",
                                status=401)

        if receiverid is None:
            return HttpResponse(json.dumps({"Message": "Require receiverid"}),
                                content_type="application/json",
                                status=401)

        senderid = int(senderid)
        receiverid = int(receiverid)

        try:
            NowSender = models.User.objects.get(user_id=senderid)
            NowReceiver = models.User.objects.get(user_id=receiverid)
        except:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)

        NowChatSet1 = models.Chat.objects.filter(chat_sender_id=senderid, chat_receiver_id=receiverid)
        NowChatSet2 = models.Chat.objects.filter(chat_sender_id=receiverid, chat_receiver_id=senderid)
        NowChatSet = NowChatSet1.union(NowChatSet2)

        NowChatSet = NowChatSet.order_by("-chat_time")

        res = []

        for NowChat in NowChatSet:
            if NowChat.chat_sender_id == NowReceiver.user_id:
                tmpmy = 1
            else:
                tmpmy = 0
            res.append({'content': NowChat.chat_content,
                        'time': time2str(NowChat.chat_time),
                        'my': tmpmy})

        return JsonResponse({"list": res,
                             "sendername": NowSender.user_username,
                             "receivername": NowSender.user_username})

    else:
        return HttpResponse(json.dumps({"Message": "require GET"}),
                            content_type="application/json",
                            status=401)
