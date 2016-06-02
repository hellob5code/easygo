/** * com.upyun.api * Upload.java */package com.easygo.utils;import org.apache.http.HttpResponse;import org.apache.http.HttpStatus;import org.apache.http.StatusLine;import org.apache.http.client.ClientProtocolException;import org.apache.http.client.HttpClient;import org.apache.http.client.methods.HttpPost;import org.apache.http.impl.client.DefaultHttpClient;import org.apache.http.util.EntityUtils;import org.json.JSONException;import org.json.JSONObject;import java.io.File;import java.io.IOException;/** * Upload.java */public class Uploader {	/**	 * 上传文件到又拍云存储。 目前根据android系统版本的不同，只支持小文件上传。	 * 	 * @param policy	 * @param signature	 * @param bucket	 * @param sourceFile	 * @return	 * @throws UpYunException	 */	public static String upload(String policy, String signature, String bucket, String sourceFile)			throws UpYunException {		String returnStr = null;		if (bucket == null || bucket.equals("")) {			throw new UpYunException(10, "bucket can not be empty.");		}		if (sourceFile == null || sourceFile.equals("")) {			throw new UpYunException(11, "source file can not be empty.");		}		if (policy == null || policy.equals("")) {			throw new UpYunException(12, "policy can not be empty.");		}		if (signature == null || signature.equals("")) {			throw new UpYunException(13, "signature can not be empty.");		}		HttpClient httpclient = new DefaultHttpClient();		HttpPost httppost = new HttpPost("http://v0.api.upyun.com/" + bucket + "/");		try {			SimpleMultipartEntity sme = new SimpleMultipartEntity();			sme.addPart("policy", policy);			sme.addPart("signature", signature);			sme.addPart("file", new File(sourceFile));			httppost.setEntity(sme);			HttpResponse response = httpclient.execute(httppost);			StatusLine statusLine = response.getStatusLine();			int code = statusLine.getStatusCode();			String str = EntityUtils.toString(response.getEntity());			if (code != HttpStatus.SC_OK) {				JSONObject obj = new JSONObject(str);				String msg = obj.getString("message");				msg = new String(msg.getBytes("UTF-8"), "UTF-8");				String url = obj.getString("url");				long time = obj.getLong("time");				boolean isSigned = false;				String signString = "";				if (!obj.isNull("sign")) {					signString = obj.getString("sign");					isSigned = true;				} else if (!obj.isNull("non-sign")) {					signString = obj.getString("non-sign");					isSigned = false;				}				UpYunException exception = new UpYunException(code, msg);				exception.isSigned = isSigned;				exception.url = url;				exception.time = time;				exception.signString = signString;				throw exception;			} else {				JSONObject obj = new JSONObject(str);				returnStr = obj.getString("url");			}		} catch (ClientProtocolException e) {			e.printStackTrace();			throw new UpYunException(30, e.getMessage());		} catch (IOException e) {			e.printStackTrace();			throw new UpYunException(31, e.getMessage());		} catch (OutOfMemoryError e) {			e.printStackTrace();			throw new UpYunException(33, e.getMessage());		} catch (JSONException e) {			throw new UpYunException(32, e.getMessage());		}		return returnStr;	}}