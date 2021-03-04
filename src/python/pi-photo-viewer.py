#!/usr/bin/python3

import requests
import json
import base64
import time
import tkinter as tk
from urllib.parse import quote_plus

import sseclient
import threading

from tkinter import Tk, Frame, Canvas, Label, YES, BOTH
from tkinter import messagebox
from PIL import ImageTk, Image
from io import BytesIO

import os
import logging

from subprocess import call, check_call
from gpiozero import Device, Button

## from gpiozero.pins.mock import MockFactory ##comment out for use on PI

# main app class
class Application:
    def __init__(self, parent):

        ## Device.pin_factory = MockFactory()   ##comment out for use on PI
        # on windows set this environment variable
        # GPIOZERO_PIN_FACTORY = mock
        # https://github.com/gpiozero/gpiozero/issues/591

        # set up some mock pins
        # https://gpiozero.readthedocs.io/en/stable/api_pins.html#mock-pins

        self.URL_ROOT = "http://gibson:8080/api/screen/"
        #self.URL_ROOT = "http://thornhill:8080/api/screen/"
        self.URL_CURRENT = self.URL_ROOT + "current";
        self.URL_NEXT = self.URL_ROOT + "nextPicture"
        self.URL_PREV = self.URL_ROOT + "prevPicture"
        self.URL_LIKE = self.URL_ROOT + "like"
        self.URL_DONT_LIKE = self.URL_ROOT + "dontlike"
        self.URL_DELETE = self.URL_ROOT + "delete"
        self.URL_HOME = self.URL_ROOT + "home"
        self.URL_SSE = self.URL_ROOT + "sse"

        DEFAULT_INTERVAL = 30000

        self.TRANSCOLOUR = "black"
        self.HOLD_TIME = 1
        self.RED_BUTTON = 4  ##pin 7             power button
        self.LIKE_BUTTON = 17  ##pin 11
        self.DONT_LIKE_BUTTON = 18  ##pin 12
        self.DELETE_BUTTON = 27  ##pin 13
        self.PREV_BUTTON = 23  ##pin 16
        self.NEXT_BUTTON = 22  ##pin 15
        self.HOME_BUTTON = 24  ##pin 18

        ##self.url = URL_ALL
        self.showTime = DEFAULT_INTERVAL

        # button handlers
        self.redButton = Button(self.RED_BUTTON, hold_time=self.HOLD_TIME)
        self.redButton.when_held = self.gpioButtonPressed
        ## self.btn_pin = Device.pin_factory.pin(self.RED_BUTTON)  ##comment out for use on PI

        self.likeButton = Button(self.LIKE_BUTTON, hold_time=self.HOLD_TIME)
        self.likeButton.when_held = self.gpioButtonPressed
        self.dontLikeButton = Button(self.DONT_LIKE_BUTTON, hold_time=self.HOLD_TIME)
        self.dontLikeButton.when_held = self.gpioButtonPressed
        self.deleteButton = Button(self.DELETE_BUTTON, hold_time=self.HOLD_TIME)
        self.deleteButton.when_held = self.gpioButtonPressed
        self.prevButton = Button(self.PREV_BUTTON, hold_time=self.HOLD_TIME)
        self.prevButton.when_held = self.gpioButtonPressed
        self.nextButton = Button(self.NEXT_BUTTON, hold_time=self.HOLD_TIME)
        self.nextButton.when_held = self.gpioButtonPressed
        self.homeButton = Button(self.HOME_BUTTON, hold_time=self.HOLD_TIME)
        self.homeButton.when_held = self.gpioButtonPressed

        #configure logging
        if os.path.exists('screen_debug.log'):
            os.remove('screen_debug.log')

        logging.basicConfig(
            level=logging.INFO,
            format=('[%(asctime)s] %(levelname)-8s %(name)-12s %(message)s'),
            filename=('screen_debug.log'),

        )

        # parent.wm_attributes('-transparentcolor', self.TRANSCOLOUR)
        parent.grid_rowconfigure(0, weight=1)
        parent.grid_columnconfigure(0, weight=1)

        self.canvas = Canvas(parent, bd=0, highlightthickness=0, background="white")
        self.canvas.grid(column=0, row=0, sticky="nsew")

        self.label = Label(
            parent, text="Loading", font=(None, 26), bg="white", fg="red"
        )
        self.label.grid(column=0, row=0, stick="sew")

        self.photo = ""

        self.interval = None
        self.loadingPicture = False

        # set up the server side event hook
        thread = threading.Thread(target=self.getSseHandle)
        thread.setDaemon(True)
        thread.start()

        self.showPicture(self.URL_NEXT)

    # keyboard handler
    def key(self, p2):
        if p2.keysym == "1":
            self.buttonPress(self.RED_BUTTON)
        elif p2.keysym == "2":
            self.buttonPress(self.LIKE_BUTTON)
        elif p2.keysym == "3":
            self.buttonPress(self.DONT_LIKE_BUTTON)
        elif p2.keysym == "4":
            self.buttonPress(self.DELETE_BUTTON)
        elif p2.keysym == "5":
            self.buttonPress(self.PREV_BUTTON)
        elif p2.keysym == "6":
            self.buttonPress(self.NEXT_BUTTON)
        elif p2.keysym == "7":
            self.buttonPress(self.HOME_BUTTON)
        elif p2.keysym == "Escape":
            quit()

    def gpioButtonPressed(self, button: Button):
        self.buttonPress(button.pin.number)

    def buttonPress(self, buttonNumber):
        try:
            if buttonNumber == self.RED_BUTTON:
                self.log("Shutdown button pressed")
                check_call(["sudo", "poweroff"])
            elif buttonNumber == self.LIKE_BUTTON:
                self.log("Like pressed")
                self.showPicture(self.URL_LIKE)
            elif buttonNumber == self.DONT_LIKE_BUTTON:
                self.log("Dont like button pressed")
                self.showPicture(self.URL_DONT_LIKE)
            elif buttonNumber == self.DELETE_BUTTON:
                self.log("Delete button pressed")
                self.showPicture(self.URL_DELETE)
            elif buttonNumber == self.PREV_BUTTON:
                self.log("Previous button pressed")
                self.showPicture(self.URL_PREV)
            elif buttonNumber == self.NEXT_BUTTON:
                self.log("Next button pressed")
                self.showPicture(self.URL_NEXT)
            elif buttonNumber == self.HOME_BUTTON:
                self.log("Home button pressed")
                self.showPicture(self.URL_HOME)
            else:
                self.log("unknown button pressed - " + str(buttonNumber))
        except Exception as e:
            self.log(e)

    # log to a label on screen
    def log(self, msg, tolabel=True):
        #print(msg)
        logging.info(msg)
        if tolabel:
            self.label.configure(text=msg)
            root.update()
        # self.debugText.set(msg)

    def getSseHandle(self):
        self.log("Getting SSE handle from " + self.URL_SSE, False)
        response = requests.get(self.URL_SSE, stream=True)
        self.log("Got SSE handle. Creating client", False)
        client = sseclient.SSEClient(response)
        self.log("Got client, hooking events handlers", False)
        for event in client.events():
            #self.log("Responding to messages")
            self.log(event.data, False)
            self.showPicture(self.URL_CURRENT)
    
    def showPicture(self, url, body=""):

        ##try to stop re-entry
        if self.loadingPicture:
            return

        self.loadingPicture = True

        self.log("Getting next picture from: " + url)

        if self.interval != None:
            root.after_cancel(self.interval)

        try:
            if body == "":
                picture = requests.get(url, timeout=30).content
            else:
                picture = requests.post(url, data=body, timeout=30).content

            content = picture.decode("utf-8")

            photo = json.loads(content)
            self.photo = photo  # keep a ref to the photo

            self.log("Got [" + photo["fullFileName"] + "] - loading...")
            b64 = photo["imageAsB64"]
            im = Image.open(BytesIO(base64.b64decode(b64)))
            owidth, oheight = im.size
            ir = oheight / owidth

            ##resize the image based on orientation and screen size
            # these never change...
            sw = root.winfo_screenwidth()
            sh = root.winfo_screenheight()
            sr = sh / sw

            if ir >= sr:
                nh = sh
                nw = (int)(owidth * (sh / oheight))
            else:
                nh = (int)(oheight * (sw / owidth))
                nw = sw

            im = im.resize((nw, nh), Image.ANTIALIAS)
            self.canvas.image = ImageTk.PhotoImage(im)

            # center the image
            l = (int)((sw - nw) / 2)
            t = (int)((sh - nh) / 2)

            # load the image
            self.log("Loading photo with label: " + photo["label"])
            self.canvas.create_image(l, t, anchor="nw", image=self.canvas.image)

            # set up a new call and return the interval so it can be cancelled
            self.interval = root.after(self.showTime, self.showPicture, self.URL_NEXT)
            ##return interval

        except Exception as e:
            self.log("Failed to get the picture [" + str(e) + "]")
            return root.after(2000, self.showPicture)
        finally:
            self.loadingPicture = False


# the starting point of the app
try:

    root = Tk()
    root.attributes("-fullscreen", True)
    root.config(cursor="none")

    app = Application(root)

    root.bind("<Key>", app.key)
    root.mainloop()

except Exception as e:
    logging.error(e)
