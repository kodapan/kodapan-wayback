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
 * @since 2010-aug-18 20:00:56
 */
@Entity
public class WaybackResponse {

  @PrimaryKey
  private long primaryKey;

  @SecondaryKey(relate = Relationship.MANY_TO_ONE)
  private WaybackRequest request;

  private Long revision;

  private WaybackStatusLine statusLine;

  private WaybackHeader[] headers;

  private String locale;

  private long contentLength;


  @SecondaryKey(
      relate = Relationship.ONE_TO_ONE,
      relatedEntity = WaybackContent.class)
  private long content;

  public WaybackHeader getFirstHeader(String name) {
    for (WaybackHeader header  : getHeaders()) {
      if (name.equals(header.getName())) {
        return header;
      }
    }
    return null;
  }

  public long getContent() {
    return content;
  }

  public void setContent(long content) {
    this.content = content;
  }

  public long getContentLength() {
    return contentLength;
  }

  public void setContentLength(long contentLength) {
    this.contentLength = contentLength;
  }

  public WaybackHeader[] getHeaders() {
    return headers;
  }

  public void setHeaders(WaybackHeader[] headers) {
    this.headers = headers;
  }

  public long getPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(long primaryKey) {
    this.primaryKey = primaryKey;
  }

  public WaybackRequest getRequest() {
    return request;
  }

  public void setRequest(WaybackRequest request) {
    this.request = request;
  }

  public WaybackStatusLine getStatusLine() {
    return statusLine;
  }

  public void setStatusLine(WaybackStatusLine statusLine) {
    this.statusLine = statusLine;
  }

  public String getLocale() {
    return locale;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }

  public Long getRevision() {
    return revision;
  }

  public void setRevision(Long revision) {
    this.revision = revision;
  }


}
