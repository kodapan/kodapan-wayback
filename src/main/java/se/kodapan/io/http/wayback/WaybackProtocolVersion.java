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

/**
 * @author kalle
 * @since 2010-aug-19 17:52:25
 */
@Persistent
public class WaybackProtocolVersion {

  /** Name of the protocol. */
  private String protocol;

  /** Major version number of the protocol */
  private int major;

  /** Minor version number of the protocol */
  private int minor;


  public String getProtocol() {
    return protocol;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  public int getMajor() {
    return major;
  }

  public void setMajor(int major) {
    this.major = major;
  }

  public int getMinor() {
    return minor;
  }

  public void setMinor(int minor) {
    this.minor = minor;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    WaybackProtocolVersion that = (WaybackProtocolVersion) o;

    if (major != that.major) return false;
    if (minor != that.minor) return false;
    if (protocol != null ? !protocol.equals(that.protocol) : that.protocol != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = protocol != null ? protocol.hashCode() : 0;
    result = 31 * result + major;
    result = 31 * result + minor;
    return result;
  }

  @Override
  public String toString() {
    return "WaybackProtocolVersion{" +
        "protocol='" + protocol + '\'' +
        ", major=" + major +
        ", minor=" + minor +
        '}';
  }
}
