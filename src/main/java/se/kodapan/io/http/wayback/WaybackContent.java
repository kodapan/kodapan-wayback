package se.kodapan.io.http.wayback;

import com.sleepycat.persist.model.*;

/**
 * @author kalle
 * @since 2010-aug-19 18:20:53
 */
@Entity
public class WaybackContent {

  @PrimaryKey
  private long primaryKey;

  private byte[] content;

  public long getPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(long primaryKey) {
    this.primaryKey = primaryKey;
  }

  public byte[] getContent() {
    return content;
  }

  public void setContent(byte[] content) {
    this.content = content;
  }


}
