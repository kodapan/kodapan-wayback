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

import com.sleepycat.persist.model.Persistent;
import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;

import java.util.Arrays;

/**
 * @author kalle
 * @since 2010-aug-19 00:33:43
 */
@Persistent
public class WaybackHeaderElement implements HeaderElement {

  private String name;
  private String value;
  private WaybackNameValuePair[] parameters;

  @Override
  public NameValuePair getParameterByName(String name) {
    for (WaybackNameValuePair parameter : getParameters()) {
      if (name.equals(parameter.getName())) {
        return parameter;
      }
    }
    return null;
  }

  @Override
  public int getParameterCount() {
    return getParameters() == null ? 0 : getParameters().length;
  }

  @Override
  public NameValuePair getParameter(int index) {
    return getParameters()[index];
  }

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

  public WaybackNameValuePair[] getParameters() {
    return parameters;
  }

  public void setParameters(WaybackNameValuePair[] parameters) {
    this.parameters = parameters;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    WaybackHeaderElement that = (WaybackHeaderElement) o;

    if (name != null ? !name.equals(that.name) : that.name != null) return false;
    if (!Arrays.equals(parameters, that.parameters)) return false;
    if (value != null ? !value.equals(that.value) : that.value != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (value != null ? value.hashCode() : 0);
    result = 31 * result + (parameters != null ? Arrays.hashCode(parameters) : 0);
    return result;
  }

  @Override
  public String toString() {
    return "WaybackHeaderElement{" +
        "name='" + name + '\'' +
        ", value='" + value + '\'' +
        ", parameters=" + (parameters == null ? null : Arrays.asList(parameters)) +
        '}';
  }
}
