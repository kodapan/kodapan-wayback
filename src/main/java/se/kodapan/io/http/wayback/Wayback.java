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

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.*;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * @author kalle
 * @since 2010-aug-18 20:20:36
 */
public class Wayback {

  private static final Logger log = LoggerFactory.getLogger(Wayback.class);

  private static final String storeName = "wayback";

  private HttpClient httpClient = new DefaultHttpClient();

  private File path;


  private Environment environment;
  private EntityStore entityStore;
  private int cacheMB;
  private boolean readOnly;

  private PrimaryIndex<Long, WaybackResponse> waybackResponses;
  private PrimaryIndex<Long, WaybackContent> waybackContents;
  private SecondaryIndex<WaybackRequest, Long, WaybackResponse> waybackResponsesByRequest;

  public void open() throws IOException {

    log.info("Opening BDB...");

    cacheMB = Integer.valueOf(System.getProperty(Wayback.class.getName() + ".cacheMB", "5"));
    readOnly = Boolean.valueOf(System.getProperty(Wayback.class.getName() + ".readOnly", "false"));

    if (!path.exists()) {
      log.info("Creating directory " + path.getAbsolutePath());
      if (!path.mkdirs()) {
        throw new IOException("Could not create directory " + path.getAbsolutePath());
      }

      EnvironmentConfig envConfig = new EnvironmentConfig();
      envConfig.setAllowCreate(true);
      envConfig.setTransactional(false);
      envConfig.setLocking(true);
      envConfig.setReadOnly(false);


      log.info("Creating environment " + envConfig.toString());

      environment = new Environment(path, envConfig);

      StoreConfig storeConfig = new StoreConfig();
      storeConfig.setAllowCreate(true);
      storeConfig.setTransactional(false);
      storeConfig.setReadOnly(false);

      log.info("Creating store '" + storeName + "' " + storeConfig.toString());

      entityStore = new EntityStore(environment, storeName, storeConfig);

      entityStore.close();
      environment.close();

      log.info("BDB has been created");

    }

    // open

    EnvironmentConfig envConfig = new EnvironmentConfig();
    envConfig.setAllowCreate(true);
    envConfig.setTransactional(false);
    envConfig.setLocking(false);
    envConfig.setReadOnly(readOnly);
    envConfig.setCacheSize(cacheMB * 1024 * 1024); //


    log.info("Opening environment " + envConfig.toString());

    environment = new Environment(path, envConfig);

    StoreConfig storeConfig = new StoreConfig();
    storeConfig.setAllowCreate(true);
    storeConfig.setTransactional(false);
    storeConfig.setReadOnly(readOnly);

    log.info("Opening store '" + storeName + "' " + storeConfig.toString());

    entityStore = new EntityStore(environment, storeName, storeConfig);

    waybackResponses = entityStore.getPrimaryIndex(Long.class, WaybackResponse.class);
    waybackContents = entityStore.getPrimaryIndex(Long.class, WaybackContent.class);
    waybackResponsesByRequest = entityStore.getSecondaryIndex(waybackResponses, WaybackRequest.class, "request");

    log.info("BDB has been opened.");


  }

  public void close() throws IOException {

    log.info("Closing BDB...");

    entityStore.close();
    environment.close();

    entityStore = null;
    environment = null;

    log.info("BDB has been closed");

  }

  public HttpResponse execute(HttpGet get, Long oldestRevision) throws IOException {

    final HttpResponse httpResponse;

    WaybackRequest request = new WaybackRequest();
    request.setMethod("GET");
    request.setURI(get.getURI().toString());
    request.setContent("");

    StringBuilder headers = new StringBuilder();
    for (Header header : get.getAllHeaders()) {
      headers.append(header.getName());
      headers.append(": ");
      if (header.getValue() != null) {
        headers.append(header.getValue());
      }
      headers.append("\n");
    }

    request.setHeaders(headers.toString());

    WaybackResponse waybackResponse = seek(request);

    if (waybackResponse == null || waybackResponse.getRevision() < oldestRevision) {
      waybackResponse = factory(get, request);
    }

    return  httpResponseFactory(waybackResponse);
  }

  public HttpResponse execute(HttpPost post, Long oldestRevision) throws IOException {

    final HttpResponse httpResponse;

    WaybackRequest request = new WaybackRequest();
    request.setMethod("POST");
    request.setURI(post.getURI().toString());


    StringWriter sw = new StringWriter((int)post.getEntity().getContentLength() * 2);
    InputStream in = post.getEntity().getContent();
    byte[] buf = new byte[49152];
    int read;
    while ((read = in.read(buf)) > 0) {
      if (read == buf.length) {
        sw.write(Hex.encodeHex(buf));
      } else {
        byte[] last = new byte[read];
        System.arraycopy(buf, 0, last, 0, read);
        sw.write(Hex.encodeHex(last));
      }
    }
    in.close();

    request.setContent(sw.toString());

    StringBuilder headers = new StringBuilder();
    for (Header header : post.getAllHeaders()) {
      headers.append(header.getName());
      headers.append(": ");
      if (header.getValue() != null) {
        headers.append(header.getValue());
      }
      headers.append("\n");
    }

    request.setHeaders(headers.toString());


    WaybackResponse waybackResponse = seek(request);

    if (waybackResponse == null || waybackResponse.getRevision() < oldestRevision) {
      waybackResponse = factory(post, request);
    }
    return httpResponseFactory(waybackResponse);
  }

  private WaybackResponse seek(WaybackRequest request) {
    WaybackResponse waybackResponse = null;
    EntityCursor<WaybackResponse> cursor = waybackResponsesByRequest.entities(request, true, request, true);
    WaybackResponse tmp;
    while ((tmp = cursor.next()) != null) {
      // move to last
      // todo it's silly to load the full responses,
      // todo it would be better if there was a new entity with revision and id that acted as secondary index.
      if (waybackResponse == null || waybackResponse.getRevision() > tmp.getRevision()) {
        waybackResponse = tmp;
      }
    }
    cursor.close();
    return waybackResponse;
  }


  private WaybackResponse factory(HttpUriRequest method, WaybackRequest request) throws IOException {
    HttpResponse httpResponse;
    WaybackResponse waybackResponse;
    httpResponse = httpClient.execute(method);

    ByteArrayOutputStream baos = new ByteArrayOutputStream(49152);
    InputStream in = httpResponse.getEntity().getContent();
    byte[] buf = new byte[49152];
    int read;
    while ((read = in.read(buf)) > 0) {
      baos.write(buf, 0, read);
    }
    in.close();
    WaybackContent content = new WaybackContent();
    content.setContent(baos.toByteArray());
    waybackContents.put(content);

    waybackResponse = waybackResponseFactory(request, httpResponse);
    waybackResponse.setContent(content.getPrimaryKey());
    waybackResponses.put(waybackResponse);

    return waybackResponse;
  }


  private WaybackResponse waybackResponseFactory(WaybackRequest waybackRequest, HttpResponse httpResponse) {

    WaybackResponse waybackResponse = new WaybackResponse();
    waybackResponse.setRevision(System.currentTimeMillis());
    waybackResponse.setRequest(waybackRequest);

    if (httpResponse.getLocale() != null) {
      waybackResponse.setLocale(httpResponse.getLocale().toString());
    }

    // status line
    if (httpResponse.getStatusLine() != null) {
      waybackResponse.setStatusLine(new WaybackStatusLine());
      waybackResponse.getStatusLine().setReasonPhrase(httpResponse.getStatusLine().getReasonPhrase());
      waybackResponse.getStatusLine().setStatusCode(httpResponse.getStatusLine().getStatusCode());
      if (httpResponse.getStatusLine().getProtocolVersion() != null) {
        waybackResponse.getStatusLine().setProtocolVersion(new WaybackProtocolVersion());
        waybackResponse.getStatusLine().getProtocolVersion().setProtocol(httpResponse.getStatusLine().getProtocolVersion().getProtocol());
        waybackResponse.getStatusLine().getProtocolVersion().setMajor(httpResponse.getStatusLine().getProtocolVersion().getMajor());
        waybackResponse.getStatusLine().getProtocolVersion().setMinor(httpResponse.getStatusLine().getProtocolVersion().getMinor());
      }
    }


    // headers
    waybackResponse.setHeaders(new WaybackHeader[httpResponse.getAllHeaders().length]);
    for (int headerIndex = 0; headerIndex < httpResponse.getAllHeaders().length; headerIndex++) {
      Header header = httpResponse.getAllHeaders()[headerIndex];
      WaybackHeader waybackHeader = new WaybackHeader();
      waybackHeader.setName(header.getName());
      waybackHeader.setValue(header.getValue());
      if (header.getElements() != null) {
        waybackHeader.setElements(new WaybackHeaderElement[header.getElements().length]);
        for (int elementIndex = 0; elementIndex < header.getElements().length; elementIndex++) {
          HeaderElement headerElement = header.getElements()[elementIndex];
          WaybackHeaderElement waybackHeaderElement = new WaybackHeaderElement();
          waybackHeaderElement.setName(headerElement.getName());
          waybackHeaderElement.setValue(headerElement.getValue());
          if (headerElement.getParameterCount() > 0) {
            waybackHeaderElement.setParameters(new WaybackNameValuePair[headerElement.getParameterCount()]);
            for (int parameterIndex = 0; parameterIndex < headerElement.getParameterCount(); parameterIndex++) {
              NameValuePair parameter = headerElement.getParameter(parameterIndex);
              WaybackNameValuePair waybackParameter = new WaybackNameValuePair();
              waybackParameter.setName(parameter.getName());
              waybackParameter.setValue(parameter.getValue());
              waybackHeaderElement.getParameters()[parameterIndex] = waybackParameter;
            }
          }
          waybackHeader.getElements()[elementIndex] = waybackHeaderElement;
        }
      }
      waybackResponse.getHeaders()[headerIndex] = waybackHeader;
    }

    return waybackResponse;

  }

  private HttpResponse httpResponseFactory(final WaybackResponse waybackResponse) {

    ProtocolVersion protocolVersion = new ProtocolVersion(
        waybackResponse.getStatusLine().getProtocolVersion().getProtocol(),
        waybackResponse.getStatusLine().getProtocolVersion().getMajor(),
        waybackResponse.getStatusLine().getProtocolVersion().getMinor());

    StatusLine statusLine = new BasicStatusLine(
        protocolVersion,
        waybackResponse.getStatusLine().getStatusCode(),
        waybackResponse.getStatusLine().getReasonPhrase());


    HttpResponse httpResponse = new BasicHttpResponse(statusLine);

    for (WaybackHeader header : waybackResponse.getHeaders()) {
      httpResponse.setHeader(new BasicHeader(header.getName(), header.getValue()));
    }

    if (waybackResponse.getLocale() != null) {
      httpResponse.setLocale(new Locale(waybackResponse.getLocale()));
    }

    httpResponse.setEntity(new HttpEntity() {
      @Override
      public boolean isRepeatable() {
        return true;
      }

      @Override
      public boolean isChunked() {
        return false;
      }

      @Override
      public long getContentLength() {
        return waybackResponse.getContentLength();
      }

      @Override
      public Header getContentType() {
        return waybackResponse.getFirstHeader("Content-Type");
      }

      @Override
      public Header getContentEncoding() {
        return waybackResponse.getFirstHeader("Content-Encoding");
      }

      @Override
      public InputStream getContent() throws IOException, IllegalStateException {
        return new ByteArrayInputStream(waybackContents.get(waybackResponse.getContent()).getContent());
      }

      @Override
      public void writeTo(OutputStream outstream) throws IOException {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean isStreaming() {
        return false;
      }

      @Override
      public void consumeContent() throws IOException {

      }
    });

    return httpResponse;
  }

  public File getPath() {
    return path;
  }

  public void setPath(File path) {
    this.path = path;
  }

  public int getCacheMB() {
    return cacheMB;
  }

  public void setCacheMB(int cacheMB) {
    this.cacheMB = cacheMB;
  }

  public boolean isReadOnly() {
    return readOnly;
  }

  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }
}
