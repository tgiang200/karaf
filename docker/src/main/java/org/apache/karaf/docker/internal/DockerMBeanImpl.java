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
package org.apache.karaf.docker.internal;

import org.apache.karaf.docker.*;

import javax.management.MBeanException;
import javax.management.openmbean.*;
import java.util.HashMap;
import java.util.Map;

public class DockerMBeanImpl implements DockerMBean {

    private DockerService dockerService;

    public void setDockerService(DockerService dockerService) {
        this.dockerService = dockerService;
    }

    @Override
    public TabularData containers(String url) throws MBeanException {
        try {
            CompositeType containerType = new CompositeType("container", "Docker Container",
                    new String[]{"Id", "Names", "Command", "Created", "Image", "Status"},
                    new String[]{"Container ID", "Container Names", "Command run in the container", "Container creation time", "Image used by the container", "Current container status"},
                    new OpenType[]{SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, SimpleType.LONG, SimpleType.STRING, SimpleType.STRING});
            TabularType tableType = new TabularType("containers", "Docker containers", containerType, new String[]{ "Id" });
            TabularData table = new TabularDataSupport(tableType);
            for (Container container : dockerService.containers(url)) {
                CompositeData data = new CompositeDataSupport(containerType,
                        new String[]{"Id", "Names", "Command", "Created", "Image", "Status"},
                        new Object[]{container.getId(), container.getNames(), container.getCommand(), container.getCreated(), container.getImage(), container.getStatus()});
                table.put(data);
            }
            return table;
        } catch (Exception e) {
            throw new MBeanException(e);
        }
    }

    @Override
    public Map<String, String> info(String url) throws MBeanException {
        try {
            Info info = dockerService.info(url);
            Map<String, String> infoMap = new HashMap<>();
            infoMap.put("Containers", new Integer(info.getContainers()).toString());
            infoMap.put("Debug", new Boolean(info.isDebug()).toString());
            infoMap.put("Driver", info.getDriver());
            infoMap.put("ExecutionDriver", info.getExecutionDriver());
            infoMap.put("IPv4Forwarding", new Boolean(info.isIpv4Forwarding()).toString());
            infoMap.put("Images", new Integer(info.getImages()).toString());
            infoMap.put("IndexServerAddress", info.getIndexServerAddress());
            infoMap.put("InitPath", info.getInitPath());
            infoMap.put("InitSha1", info.getInitSha1());
            infoMap.put("KernelVersion", info.getKernelVersion());
            infoMap.put("MemoryLimit", new Boolean(info.isMemoryLimit()).toString());
            infoMap.put("NEventsListener", new Boolean(info.isnEventsListener()).toString());
            infoMap.put("NFd", new Integer(info.getNfd()).toString());
            infoMap.put("NGoroutines", new Integer(info.getNgoroutines()).toString());
            infoMap.put("SwapLimit", new Boolean(info.isSwapLimit()).toString());
            return infoMap;
        } catch (Exception e) {
            throw new MBeanException(null, e.getMessage());
        }
    }

    @Override
    public void provision(String name, String url) throws MBeanException {
        try {
            dockerService.provision(name, url);
        } catch (Throwable t) {
            throw new MBeanException(null, t.getMessage());
        }
    }

    @Override
    public void delete(String name, String url) throws MBeanException {
        try {
            dockerService.delete(name, url);
        } catch (Throwable t) {
            throw new MBeanException(null, t.getMessage());
        }
    }

    @Override
    public void start(String name, String url) throws MBeanException {
        try {
            dockerService.start(name, url);
        } catch (Throwable t) {
            throw new MBeanException(null, t.getMessage());
        }
    }

    @Override
    public void stop(String name, String url) throws MBeanException {
        try {
            dockerService.stop(name, url);
        } catch (Throwable t) {
            throw new MBeanException(null, t.getMessage());
        }
    }

    @Override
    public String log(String name, String url) throws MBeanException {
        try {
            return dockerService.log(name, url);
        } catch (Throwable t) {
            throw new MBeanException(null, t.getMessage());
        }
    }

    @Override
    public void commit(String name, String repo, String tag, String message, String url) throws MBeanException {
        try {
            dockerService.commit(name, repo, url, tag, message);
        } catch (Throwable t) {
            throw new MBeanException(null, t.getMessage());
        }
    }

    @Override
    public TabularData images(String url) throws MBeanException {
        try {
            CompositeType type = new CompositeType("Image", "Docker Image",
                    new String[]{ "Id", "Created", "RepoTags", "Size"},
                    new String[]{ "Image Id", "Image Creation Date", "Image repository and tag", "Image size"},
                    new OpenType[]{ SimpleType.STRING, SimpleType.LONG, SimpleType.STRING, SimpleType.LONG});
            TabularType tableType = new TabularType("Images", "List of Docker Image", type, new String[]{ "Id" });
            TabularData table = new TabularDataSupport(tableType);
            for (Image image : dockerService.images(url)) {
                CompositeData data = new CompositeDataSupport(type,
                        new String[]{ "Id", "Created", "RepoTags", "Size"},
                        new Object[]{ image.getId(), image.getCreated(), image.getRepoTags(), image.getSize() });
                table.put(data);
            }
            return table;
        } catch (Throwable t) {
            throw new MBeanException(null, t.getMessage());
        }
    }

    @Override
    public void pull(String image, String tag, String url) throws MBeanException {
        try {
            dockerService.pull(image, tag, url);
        } catch (Throwable t) {
            throw new MBeanException(null, t.getMessage());
        }
    }
}
