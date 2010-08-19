package se.kodapan.io.http.wayback;

import com.sleepycat.persist.model.Persistent;

/**
 * @author kalle
 * @since 2010-aug-19 17:51:32
 */
@Persistent
public class WaybackStatusLine {

  private WaybackProtocolVersion protocolVersion;
  private String reasonPhrase;
  private int statusCode;


  public WaybackProtocolVersion getProtocolVersion() {
    return protocolVersion;
  }

  public void setProtocolVersion(WaybackProtocolVersion protocolVersion) {
    this.protocolVersion = protocolVersion;
  }

  public String getReasonPhrase() {
    return reasonPhrase;
  }

  public void setReasonPhrase(String reasonPhrase) {
    this.reasonPhrase = reasonPhrase;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    WaybackStatusLine that = (WaybackStatusLine) o;

    if (statusCode != that.statusCode) return false;
    if (protocolVersion != null ? !protocolVersion.equals(that.protocolVersion) : that.protocolVersion != null) return false;
    if (reasonPhrase != null ? !reasonPhrase.equals(that.reasonPhrase) : that.reasonPhrase != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = protocolVersion != null ? protocolVersion.hashCode() : 0;
    result = 31 * result + (reasonPhrase != null ? reasonPhrase.hashCode() : 0);
    result = 31 * result + statusCode;
    return result;
  }

  @Override
  public String toString() {
    return "WaybackStatusLine{" +
        "protocolVersion=" + protocolVersion +
        ", reasonPhrase='" + reasonPhrase + '\'' +
        ", statusCode=" + statusCode +
        '}';
  }
}
