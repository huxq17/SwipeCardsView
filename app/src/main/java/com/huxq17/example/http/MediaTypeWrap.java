package com.huxq17.example.http;


import okhttp3.MediaType;

/**
 * 进行post请求时
 * @author huxq17
 *
 */
public class MediaTypeWrap {
	/**
	 * form表单数据被编码为key/value格式发送到服务器（表单默认的提交数据的格式）
	 */
	public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType
			.parse("application/x-www-form-urlencoded; charset=utf-8");
	/**
	 * 二进制流数据（如常见的文件下载）
	 */
	public static final MediaType MEDIA_TYPE_DOWNLOAD = MediaType
			.parse("application/octet-stream; charset=utf-8");
	/**
	 * JSON数据格式
	 */
	public static final MediaType MEDIA_TYPE_JSON = MediaType
			.parse("application/json; charset=utf-8");
	/**
	 * 纯文本格式 
	 */
	public static final MediaType MEDIA_TYPE_TEXT = MediaType
			.parse("text/plain; charset=utf-8");
	/**
	 * 表单中进行文件上传
	 */
	public static final MediaType MEDIA_TYPE_UPLOAD = MediaType
			.parse("multipart/form-data; charset=utf-8");
}
