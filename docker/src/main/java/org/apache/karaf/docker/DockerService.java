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

import java.util.List;

public interface DockerService {

    List<Container> containers(String url) throws Exception;

    boolean isContainerExists(String name, String url) throws Exception;

    Container getContainerByName(String name, String url) throws Exception;

    Container getContainer(String name, String url) throws Exception;

    Info info(String url) throws Exception;

    Version version(String url) throws Exception;

    void bootstrap(String name, String url) throws Exception;

    void bootstrap(String name, String url, ContainerConfig config) throws Exception;

    void provision(String name, String url) throws Exception;

    void delete(String name, String url) throws Exception;

    void start(String name, String url) throws Exception;

    void stop(String name, String url) throws Exception;

    void restart(String name, String url) throws Exception;

    void pause(String name, String url) throws Exception;

    void unpause(String name, String url) throws Exception;

    String log(String name, String url) throws Exception;

    void commit(String name, String repo, String tag, String message, String url) throws Exception;

    List<Image> images(String url) throws Exception;

    void pull(String image, String tag, String url) throws Exception;

    void push(String image, String tag, String url) throws Exception;

    List<ImageSearch> search(String term, String url) throws Exception;

    void rmi(String image, String url) throws Exception;

}
