package com.vertx.rest.constants;

public enum ContentType {

	APPLICATION_JSON("application/json"),
	TEXT_PLAIN("text/plain"),
	APPLICATION_XML("application/xml"),
	APPLICATION_ATOM_XML("application/atom+xml"),
	APPLICATION_XHTML_XML("application/xhtml+xml"),
	APPLICATION_SVG_XML("application/svg+xml"),
	APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded"),
	MULTIPART_FORM_DATA("multipart/form-data"),
	APPLICATION_OCTET_STREAM("application/octet-stream"),
	TEXT_XML("text/xml"),
	TEXT_HTML("text/html");

	String contentType;
	
	private ContentType(String contentType) {
		this.contentType=contentType;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	
}
