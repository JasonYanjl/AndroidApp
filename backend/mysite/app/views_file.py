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
def upload(request):
    if request.method == "POST":
        try:
            NowFile = request.FILES.get("file", None)
            NowUserId = request.POST.get("userid")
            NowType = request.POST.get("type")
        except:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)

        if NowFile is None:
            return HttpResponse(json.dumps({"Message": "no file"}),
                                content_type="application/json",
                                status=401)

        Filename = NowFile.name

        NowDoc = models.Doc(doc_user_id=NowUserId,
                            doc_name=Filename)
        FileDir = FileSavePath
        NowSavePath = os.path.join(FileDir, str(time.time_ns()) + '_' + Filename)
        NowDoc.doc_path = NowSavePath

        try:
            if not os.path.exists(FileDir):
                os.makedirs(FileDir)
            with open(NowSavePath, 'wb+') as f:
                for chunk in NowFile.chunks():
                    f.write(chunk)
        except Exception as e:
            return HttpResponse(json.dumps({"success": 0,
                                            "Message": "upload fail"}),
                                content_type="application/json",
                                status=401)

        NowDoc.save()

        return JsonResponse({'success': 1,
                             "fileid": NowDoc.doc_id,
                             'Message': "success"})

    else:
        return HttpResponse(json.dumps({"Message": "require POST"}),
                            content_type="application/json",
                            status=401)


@csrf_exempt
# @LoginCheck
def download(request):
    if request.method == 'GET':
        try:
            NowFileid = request.GET.get("fileid")
            NowFileid = int(NowFileid)
        except:
            return HttpResponse(json.dumps({"Message": "Error Params"}),
                                content_type="application/json",
                                status=401)

        try:
            NowDoc = models.Doc.objects.get(doc_id=NowFileid)
        except:
            return HttpResponse(json.dumps({"Message": "File does not exist"}),
                                content_type="application/json",
                                status=401)
        try:
            NowFile = open(NowDoc.doc_path, 'rb')
            response = FileResponse(NowFile)
            response['Content-Disposition'] = f'attachment;filename="{NowDoc.doc_name}"'
        except:
            return HttpResponse(json.dumps({"Message": "Cannot download"}),
                                content_type="application/json",
                                status=401)
        return response

    else:
        return HttpResponse(json.dumps({"Message": "require GET"}),
                            content_type="application/json",
                            status=401)
