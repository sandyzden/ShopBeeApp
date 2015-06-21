/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.app.shopbee;

import android.net.Uri;

import com.app.shopbee.util.ParserUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class Config {
    // General configuration

    // Is this an internal dogfood build?
    public static final boolean IS_DOGFOOD_BUILD = false;

    // Warning messages for dogfood build
    public static final String DOGFOOD_BUILD_WARNING_TITLE = "Test build";
    public static final String DOGFOOD_BUILD_WARNING_TEXT = "This is a test build.";


    public static final boolean DEBUG = true;

    public static final int CONFERENCE_YEAR = 2014;

    // GCM config
    public static final String GCM_SERVER_PROD_URL = "";
    public static final String GCM_SERVER_URL = "";

    // the GCM sender ID is the ID of the app in Google Cloud Console
    public static final String GCM_SENDER_ID = "";

    // The registration api KEY in the gcm server (configured in the GCM
    // server's AuthHelper.java file)
    public static final String GCM_API_KEY = "";


}
