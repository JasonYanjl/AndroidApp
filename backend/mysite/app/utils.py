from django.http import HttpResponse
from . import models
import base64
import scrypt
import jwt
import json
import os
import hashlib
import time


def ToMd5(Str):
    HashLib = hashlib.md5()
    HashLib.update(Str.encode("utf-8"))
    return HashLib.hexdigest()


def EncryptPassword(Password):
    salt = "seasalt"
    key = scrypt.hash(Password, salt, 32768, 8, 1, 32)
    return base64.b64encode(key).decode("ascii")


def GenerateJWT(payload):
    salt = "jwtsalt"
    token = jwt.encode(payload, salt, algorithm="HS256")
    return token.decode()
    # return token


def DecodeJWT(token):
    salt = "jwtsalt"
    payload = jwt.decode(token, salt, algorithm=['HS256'])
    return payload


def time2str(timeint):
    timeint = timeint // 1000
    return time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(timeint))


BASEPATH = os.getcwd()
FileSavePath = os.path.join(BASEPATH, "UploadFiles")
if not os.path.exists(FileSavePath):
    os.makedirs(FileSavePath)