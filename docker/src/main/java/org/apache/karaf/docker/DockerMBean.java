/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.karaf.docker;

import javax.management.MBeanException;
import javax.management.openmbean.TabularData;
import java.util.Map;

public interface DockerMBean {

    TabularData containers(String url) throws MBeanException;

    Map<String, String> info(String url) throws MBeanException;

    void provision(String name, String url) throws MBeanException;

    void delete(String name, String url) throws MBeanException;

    void start(String name, String url) throws MBeanException;

    void stop(String name, String url) throws MBeanException;

    String log(String name, String url) throws MBeanException;

    void commit(String name, String repo, String tag, String message, String url) throws MBeanException;

    TabularData images(String url) throws MBeanException;

    void pull(String image, String tag, String url) throws MBeanException;

}
