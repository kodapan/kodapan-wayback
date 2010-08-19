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

import junit.framework.TestCase;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kalle
 * @since 2010-aug-19 00:49:05
 */
public class TestWayback extends TestCase {

  @Test
  public void testGET() throws Exception {

    Wayback wayback = new Wayback();
    wayback.setPath(new File("target/tmp/" + System.currentTimeMillis()));
    wayback.open();

    long now = System.currentTimeMillis();

    HttpResponse response1 = wayback.execute(new HttpGet("http://www.google.com/"), now);
    HttpResponse response2 = wayback.execute(new HttpGet("http://www.google.com/"), now);

//    assertEquals(response1, response2);

    now = System.currentTimeMillis();

    HttpResponse response3 = wayback.execute(new HttpGet("http://www.google.com/"), now);

//    assertNotSame(response3, response2);

    System.currentTimeMillis();

    response1.getEntity().consumeContent();
    response2.getEntity().consumeContent();
    response3.getEntity().consumeContent();
    

    wayback.close();
  }


  @Test
  public void testPOST() throws Exception {

    Wayback wayback = new Wayback();
    wayback.setPath(new File("target/tmp/" + System.currentTimeMillis()));
    wayback.open();

    long now = System.currentTimeMillis();

    HttpPost post = new HttpPost();
    post.setURI(new URI("http://www.jsonlint.com/ajax/validate"));

    List<NameValuePair> pairs = new ArrayList<NameValuePair>();
    pairs.add(new BasicNameValuePair("json", "{ foo : 'bar' }"));
    pairs.add(new BasicNameValuePair("reformat", "yes"));
    UrlEncodedFormEntity form = new UrlEncodedFormEntity(pairs);
    post.setEntity(form);

    HttpResponse response1 = wayback.execute(post, now);
    HttpResponse response2 = wayback.execute(post, now);

//    assertEquals(response1, response2);

    now = System.currentTimeMillis();

    HttpResponse response3 = wayback.execute(post, now);

//    assertNotSame(response3, response2);

    System.currentTimeMillis();

    response1.getEntity().consumeContent();
    response2.getEntity().consumeContent();
    response3.getEntity().consumeContent();

    wayback.close();
  }

}
