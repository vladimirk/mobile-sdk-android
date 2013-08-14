/*
 *    Copyright 2013 APPNEXUS INC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.appnexus.opensdk;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appnexus.opensdk.utils.Clog;

public class AdResponse {
    public AdRequester requester;
    public String body;
    public int height;
    public int width;
    private String type;
    boolean fail = false;
    final static String http_error = "HTTP_ERROR";
    boolean isMraid = false;

    private boolean isMediated = false;
    private String mediatedViewClassName;
    private String mediatedUID;
    private String mediatedParameter;

    private String mediatedFailURL;
    private String mediatedSuccessURL;

    public AdResponse(AdRequester requester, String body, Header[] headers) {
        this.requester = requester;
        if (body == null) {
            this.fail = true;
            Clog.clearLastResponse();
            return;
        } else if (body.equals(AdResponse.http_error)) {
            this.fail = true;
            Clog.clearLastResponse();
            return;
        } else if (body.length() == 0) {
            Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.response_blank));
            this.fail = true;
            Clog.clearLastResponse();
            return;
        }

        Clog.setLastResponse(body);

        Clog.d(Clog.httpRespLogTag,
                Clog.getString(R.string.response_body, body));
        if (headers != null) {
            for (Header h : headers) {
                Clog.v(Clog.httpRespLogTag,
                        Clog.getString(R.string.response_header, h.getName(),
                                h.getValue()));
            }
        }

        parseResponse(body);

        isMraid = getBody().contains("mraid.js");

    }

    private void parseResponse(String body) {
        JSONObject response;

        if (body.equals("RETRY") || body.equals("RETRY2")) {
            return;
        }
        try {
            response = new JSONObject(body);
            String status = response.getString("status");
            if (status.equals("error")) {
                String error = response.getString("errorMessage");
                Clog.e(Clog.httpRespLogTag,
                        Clog.getString(R.string.response_error, error));
                return;
            }
            JSONArray ads = response.getJSONArray("ads");


            if (ads.length() > 0) {
                // for now, just take the first ad
                JSONObject firstAd = ads.getJSONObject(0);
                // assume there's content
                height = firstAd.getInt("height");
                width = firstAd.getInt("width");
                this.body = firstAd.getString("content");
                type = firstAd.getString("type");
                if (this.body.equals("") || this.body == null){
                    Clog.e(Clog.httpRespLogTag,
                            Clog.getString(R.string.blank_ad));
                }
                return;
            }
        } catch (JSONException e) {
            Clog.e(Clog.httpRespLogTag,
                    Clog.getString(R.string.response_json_error, body));
            e.printStackTrace();
            return;
        }

        try {
            JSONArray mediated = response.getJSONArray("mediated");
            if (mediated.length() > 0) {
                JSONObject mediated_response = mediated.getJSONObject(0);
                mediatedViewClassName = mediated_response
                        .getString("android_class");
                height = mediated_response.getInt("height");
                width = mediated_response.getInt("width");
                mediatedUID = mediated_response.getString("id");
                mediatedParameter = mediated_response.getString("param");
                mediatedFailURL = mediated_response.getString("fail_ib");
                mediatedSuccessURL = mediated_response.getString("success_ib");
                isMediated = true;
                return;
            }
        } catch (JSONException e) {
            Clog.e(Clog.httpRespLogTag,
                    Clog.getString(R.string.response_json_error, body));
            e.printStackTrace();
            return;
        }
        Clog.w(Clog.httpRespLogTag,
                Clog.getString(R.string.response_no_ads));
    }

    public String getBody() {
        if (body == null)
            return "";
        return body;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    // banner, interstitial
    public String getType() {
        return type;
    }

    public boolean isMediated() {
        return isMediated;
    }

    public void setMediated(boolean isMediated) {
        this.isMediated = isMediated;
    }

    public String getMediatedUID() {
        return mediatedUID;
    }

    public String getMediatedViewClassName() {
        return mediatedViewClassName;
    }

    public String getParameter() {
        return mediatedParameter;
    }

    public String getMediatedFailURL() {
        return mediatedFailURL;
    }

    public void setMediatedFailURL(String mediatedFailURL) {
        this.mediatedFailURL = mediatedFailURL;
    }

    public String getMediatedSuccessURL() {
        return mediatedSuccessURL;
    }

    public void setMediatedSuccessURL(String mediatedSuccessURL) {
        this.mediatedSuccessURL = mediatedSuccessURL;
    }
}
