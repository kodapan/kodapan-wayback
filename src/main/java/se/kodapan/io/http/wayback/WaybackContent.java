/*
 * Copyright 2010 Kodapan
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

package se.kodapan.io.http.wayback;

import com.sleepycat.persist.model.*;

import java.util.Arrays;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    WaybackContent content1 = (WaybackContent) o;

    if (primaryKey != content1.primaryKey) return false;
    if (!Arrays.equals(content, content1.content)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = (int) (primaryKey ^ (primaryKey >>> 32));
    result = 31 * result + (content != null ? Arrays.hashCode(content) : 0);
    return result;
  }

  @Override
  public String toString() {
    return "WaybackContent{" +
        "primaryKey=" + primaryKey +
        '}';
  }
}
