package de.uniwue.web.view;

import java.util.List;

import de.uniwue.compare.ContentType;
import de.uniwue.compare.token.Token;

public class Content extends Token {

	private final ContentType contentType;
	private final String varianceType;

	public Content(int begin, int end, String content, String contentTag, ContentType contentType, String varianceType, List<long[]> highlight) {
		super(begin, end, content, contentTag);
		this.contentType = contentType;
		this.varianceType = varianceType;
		this.highlight = highlight;
	}

	public Content(Token token, ContentType contentType, String varianceType, List<long[]> highlight) {
		this(token.getBegin(), token.getEnd(), token.getContent(), token.getContentTag(), contentType, varianceType, highlight);
		this.addAnnotation(token.getAnnotationsString());
	}

	public ContentType getContentType() {
		return contentType;
	}

	public String getVarianceType() {
		return varianceType;
	}
}
