#!/usr/bin/env python
# coding=utf-8
import sys
import os
import requests
import subprocess


red_color = "#7c0000"
green_color = "#007c00"


def __send_notification(skype_id, skype_key, chat_id, msg):
    def __get_access_token(id, secret):
        url = 'https://login.microsoftonline.com/common/oauth2/v2.0/token'
        data = {'client_id': id,
                'scope': 'https://api.botframework.com/.default',
                'grant_type': 'client_credentials',
                'client_secret': secret}
        try:
            r = requests.post(url, data=data)
            if r.status_code == requests.codes.ok:
                return r.json()['access_token']
        except Exception as e:
            print e
    def __send_msg(message, chat_id, token):
        url = ('https://apis.skype.com/v2/conversations/{}/activities'.format(chat_id))
        data = {'message': {'content': message}}
        headers = {'Authorization': 'Bearer {}'.format(token)}
        if token:
            try:
                return requests.post(url, json=data, headers=headers)
            except Exception as e:
                print e

    token = __get_access_token(skype_id, skype_key)
    __send_msg(msg, chat_id, token)


def commit_hash():
  return subprocess.check_output(["git", "rev-parse", "--short", "HEAD"]).strip()


def commit_msg():
  return subprocess.check_output(["git", "log", "-1", "--pretty=%B"]).strip()


def commit_author():
  return subprocess.check_output(["git", "log", "-1", "--pretty=%an"]).strip()


def colorize(color, s):
  return "<b><font color='" + color + "'>" + s + "</font></b>"


def failed_msg():
  return colorize(red_color, "failed")


def success_msg():
  return colorize(green_color, "complete")


def notify(skype_id, skype_key, skype_chat_id, company_repo, branch, build_id, build_num, success):
  msg = '[PG travis] #{build_num} <b>{branch}</b> "{commit_msg}" @{commit_author} ({commit_hash}) - {msg}'\
         .format(build_num=build_num, branch=branch, commit_msg=commit_msg(), commit_author=commit_author(), 
                 commit_hash=commit_hash(), msg=(success_msg() if int(success) else failed_msg()))
  report = "<a href=\"https://travis-ci.org/" + company_repo + "/builds/" + str(build_id) + "\">See report</a>"
  __send_notification(skype_id, skype_key, skype_chat_id, msg + "\n" + report)


notify(sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4], sys.argv[5], sys.argv[6] , sys.argv[7], sys.argv[8])
