package se.kodapan.io.http.wayback;

import com.sleepycat.persist.model.Persistent;
import org.apache.http.Header;

import java.util.Arrays;

/**
 * @author kalle
 * @since 2010-aug-19 00:33:38
 */
@Persistent
public class WaybackHeader implements Header {

  private String name;
  private String value;
  private WaybackHeaderElement[] elements;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public WaybackHeaderElement[] getElements() {
    return elements;
  }

  public void setElements(WaybackHeaderElement[] elements) {
    this.elements = elements;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    WaybackHeader that = (WaybackHeader) o;

    if (!Arrays.equals(elements, that.elements)) return false;
    if (name != null ? !name.equals(that.name) : that.name != null) return false;
    if (value != null ? !value.equals(that.value) : that.value != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (value != null ? value.hashCode() : 0);
    result = 31 * result + (elements != null ? Arrays.hashCode(elements) : 0);
    return result;
  }

  @Override
  public String toString() {
    return "WaybackHeader{" +
        "name='" + name + '\'' +
        ", value='" + value + '\'' +
        ", elements=" + (elements == null ? null : Arrays.asList(elements)) +
        '}';
  }
}
