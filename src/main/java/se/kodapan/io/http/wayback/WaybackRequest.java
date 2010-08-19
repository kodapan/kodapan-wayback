package se.kodapan.io.http.wayback;

import com.sleepycat.persist.model.KeyField;
import com.sleepycat.persist.model.Persistent;

import java.util.Date;

/**
 * @author kalle
 * @since 2010-aug-18 20:18:23
 */
@Persistent
public class WaybackRequest {

  @KeyField(1)
  private String URI;
  @KeyField(2)
  private String method;
  @KeyField(3)
  private String content;
  @KeyField(4)
  private String headers;

  public String getURI() {
    return URI;
  }

  public void setURI(String URI) {
    this.URI = URI;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getHeaders() {
    return headers;
  }

  public void setHeaders(String headers) {
    this.headers = headers;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    WaybackRequest that = (WaybackRequest) o;

    if (URI != null ? !URI.equals(that.URI) : that.URI != null) return false;
    if (content != null ? !content.equals(that.content) : that.content != null) return false;
    if (headers != null ? !headers.equals(that.headers) : that.headers != null) return false;
    if (method != null ? !method.equals(that.method) : that.method != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = URI != null ? URI.hashCode() : 0;
    result = 31 * result + (method != null ? method.hashCode() : 0);
    result = 31 * result + (headers != null ? headers.hashCode() : 0);
    result = 31 * result + (content != null ? content.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "WaybackRequest{" +
        "URI='" + URI + '\'' +
        ", method='" + method + '\'' +
        ", headers='" + headers + '\'' +
        ", content='" + content + '\'' +
        '}';
  }
}
