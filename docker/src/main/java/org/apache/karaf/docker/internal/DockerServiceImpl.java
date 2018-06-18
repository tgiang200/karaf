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

import java.io.*;
import java.util.*;

public class DockerServiceImpl implements DockerService {

    private File storageLocation;

    public File getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(File storageLocation) {
        this.storageLocation = storageLocation;
    }

    @Override
    public List<Container> containers(String url) throws Exception {
        DockerClient dockerClient = new DockerClient(url);
        return dockerClient.containers(true);
    }

    @Override
    public boolean isContainerExists(String name, String url) throws Exception {
        return (this.getContainerByName(name, url) != null);
    }

    @Override
    public Container getContainerByName(String name, String url) throws Exception {
        List<Container> containers = this.containers(url);
        for (Container container : containers) {
            if (container.getNames().contains(name)) {
                return container;
            }
        }
        return null;
    }

    @Override
    public Info info(String url) throws Exception {
        DockerClient dockerClient = new DockerClient(url);

        return dockerClient.info();
    }

    @Override
    public Version version(String url) throws Exception {
        DockerClient dockerClient = new DockerClient(url);
        return dockerClient.version();
    }

    @Override
    public void bootstrap(String name, String url) throws Exception {
        // create a default Karaf docker container configuration
        ContainerConfig config = new ContainerConfig();
        config.setTty(true);
        config.setAttachStdin(true);
        config.setAttachStderr(true);
        config.setAttachStdout(true);
        config.setImage("karaf:4.2.0");
        config.setHostname("docker");
        config.setUser("");
        config.setCmd(new String[]{ "/bin/karaf" });
        config.setWorkingDir("");
        config.setOpenStdin(true);
        config.setStdinOnce(true);
        Map<String, Map<String, String>> exposedPorts = new HashMap<>();
        exposedPorts.put("8101/tcp", new HashMap<>());
        config.setExposedPorts(exposedPorts);

        HostConfig hostConfig = new HostConfig();
        hostConfig.setPrivileged(false);
        hostConfig.setPublishAllPorts(false);

        // getting the container storage
        File containerStorage = new File(storageLocation, name);
        if (containerStorage.exists()) {
            hostConfig.setBinds(new String[]{ containerStorage.getAbsolutePath() + ":/opt/apache-karaf" });
        }

        hostConfig.setNetworkMode("bridge");
        hostConfig.setLxcConf(new String[]{});
        Map<String, String> binding = new HashMap<>();
        binding.put("0.0.0.0", "49153");
        Map<String, List<Map<String, String>>> portBindings = new HashMap<>();
        List<Map<String, String>> portBinding = new ArrayList<>();
        portBinding.add(binding);
        portBindings.put("8101/tcp", portBinding);
        hostConfig.setPortBindings(portBindings);

        config.setHostConfig(hostConfig);

        bootstrap(name, url, config);
    }

    @Override
    public void bootstrap(String name, String url, ContainerConfig config) throws Exception {
        if (isContainerExists(name, url)) {
            throw new IllegalStateException("Docker container " + name + " already exists");
        }

        // creating the docker container
        DockerClient dockerClient = new DockerClient(url);

        dockerClient.createContainer(config, name);
    }

    @Override
    public void provision(String name, String url) throws Exception {
        if (isContainerExists(name, url)) {
            throw new IllegalArgumentException("Docker container " + name + " already exists");
        }

        // creating the container
        File containerStorage = new File(storageLocation, name);
        containerStorage.mkdirs();

        // copy the current Karaf instance in the Docker container
        File currentKarafBase = new File(System.getProperty("karaf.base"));
        copy(currentKarafBase, containerStorage);

        // creating the Karaf Docker container
        DockerClient dockerClient = new DockerClient(url);
        ContainerConfig config = new ContainerConfig();
        config.setTty(true);
        config.setAttachStdout(true);
        config.setAttachStderr(true);
        config.setAttachStdin(true);
        config.setImage("karaf:4.2.0");
        config.setHostname("docker");
        config.setUser("");
        config.setCmd(new String[]{ "/bin/karaf" });
        config.setWorkingDir("");
        config.setOpenStdin(true);
        config.setStdinOnce(true);
        Map<String, Map<String, String>> exposedPorts = new HashMap<>();
        Map<String, String> exposedPort = new HashMap<>();
        exposedPorts.put("8101/tcp", exposedPort);
        config.setExposedPorts(exposedPorts);

        HostConfig hostConfig = new HostConfig();
        hostConfig.setPrivileged(false);
        hostConfig.setPublishAllPorts(false);

        // getting the dock
        File dock = new File(storageLocation, name);
        if (dock.exists()) {
            hostConfig.setBinds(new String[]{dock.getAbsolutePath() + ":/opt/apache-karaf"});
        }

        hostConfig.setNetworkMode("bridge");
        hostConfig.setLxcConf(new String[]{});
        Map<String, List<Map<String, String>>> portBindings = new HashMap<>();
        List<Map<String, String>> portBinding = new LinkedList<>();
        Map<String, String> binding = new HashMap<>();
        binding.put("0.0.0.0", "49153");
        portBinding.add(binding);
        portBindings.put("8101/tcp", portBinding);
        hostConfig.setPortBindings(portBindings);

        config.setHostConfig(hostConfig);

        dockerClient.createContainer(config, name);
    }

    @Override
    public void start(String name, String url) throws Exception {
        if (!isContainerExists(name, url)) {
            throw new IllegalArgumentException("Docker container " + name + " doesn't exist");
        }
        DockerClient dockerClient = new DockerClient(url);
        dockerClient.startContainer(getContainerByName(name, url).getId(), null);
    }

    @Override
    public void stop(String name, String url) throws Exception {
        if (!isContainerExists(name, url)) {
            throw new IllegalArgumentException("Docker container " + name + " doesn't exist");
        }
        DockerClient dockerClient = new DockerClient(url);
        dockerClient.stopContainer(getContainerByName(name, url).getId(), 30);
    }

    @Override
    public void restart(String name, String url) throws Exception {
        if (!isContainerExists(name, url)) {
            throw new IllegalArgumentException("Docker container " + name + " doesn't exist");
        }
        DockerClient dockerClient = new DockerClient(url);
        dockerClient.restartContainer(getContainerByName(name, url).getId(), 30);
    }

    @Override
    public void pause(String name, String url) throws Exception {
        if (!isContainerExists(name, url)) {
            throw new IllegalArgumentException("Docker container " + name + " doesn't exist");
        }
        DockerClient dockerClient = new DockerClient(url);
        dockerClient.pauseContainer(getContainerByName(name, url).getId());
    }

    @Override
    public void unpause(String name, String url) throws Exception {
        if (!isContainerExists(name, url)) {
            throw new IllegalArgumentException("Docker container " + name + " doesn't exist");
        }
        DockerClient dockerClient = new DockerClient(url);
        dockerClient.unpauseContainer(getContainerByName(name, url).getId());
    }

    @Override
    public void delete(String name, String url) throws Exception {
        if (!isContainerExists(name, url)) {
            throw new IllegalArgumentException("Docker container " + name + " doesn't exist");
        }
        DockerClient dockerClient = new DockerClient(url);
        dockerClient.removeContainer(getContainerByName(name, url).getId(), true, true);
        File containerStorage = new File(storageLocation, name);
        if (containerStorage.exists()) {
            containerStorage.delete();
        }
    }

    @Override
    public String log(String name, String url) throws Exception {
        if (!isContainerExists(name, url)) {
            throw new IllegalArgumentException("Docker container " + name + " doesn't exist");
        }
        DockerClient dockerClient = new DockerClient(url);
        String log = dockerClient.containerLog(getContainerByName(name, url).getId(), true, true, true, true);
        return log;
    }

    @Override
    public void commit(String name, String repo, String tag, String message, String url) throws Exception {
        if (!isContainerExists(name, url)) {
            throw new IllegalArgumentException("Docker container " + name + " doesn't exist");
        }
        DockerClient dockerClient = new DockerClient(url);
        dockerClient.commitContainer(getContainerByName(name, url).getId(), null, message, repo, tag);
    }

    @Override
    public List<Image> images(String url) throws Exception {
        DockerClient dockerClient = new DockerClient(url);
        return dockerClient.images(true);
    }

    @Override
    public void pull(String image, String tag, String url) throws Exception {
        DockerClient dockerClient = new DockerClient(url);
        dockerClient.pullImage(image, tag, true);
    }

    @Override
    public List<ImageSearch> search(String term, String url) throws Exception {
        DockerClient dockerClient = new DockerClient(url);
        return dockerClient.searchImage(term);
    }

    @Override
    public Container getContainer(String name, String url) throws Exception {
        DockerClient dockerClient = new DockerClient(url);
        return dockerClient.getContainer(name);
    }

    @Override
    public void push(String image, String tag, String url) throws Exception {
        DockerClient dockerClient = new DockerClient(url);
        dockerClient.pushImage(image, tag, true);
    }

    @Override
    public void rmi(String image, String url) throws Exception {
        DockerClient dockerClient = new DockerClient(url);
        dockerClient.removeImage(image, true, false);
    }

    private void copy(File source, File destination) throws IOException {
        if (source.getName().equals("docker")) {
            // ignore inner docker
            return;
        }
        if (source.getName().equals("cache.lock")) {
            // ignore cache.lock file
            return;
        }
        if (source.getName().equals("lock")) {
            // ignore lock file
            return;
        }
        if (source.getName().matches("transaction_\\d+\\.log")) {
            // ignore active txlog files
            return;
        }
        if (source.isDirectory()) {
            if (!destination.exists()) {
                destination.mkdirs();
            }
            String[] children = source.list();
            for (String child : children) {
                if (!child.contains("instances") && !child.contains("lib"))
                    copy(new File(source, child), new File(destination, child));
            }
        } else {
            try (
                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(destination)
            ) {
                new StreamUtils().copy(in, out);
            }
        }
    }

    class StreamUtils {

        public StreamUtils() {
        }

        public void close(Closeable... closeables) {
            for (Closeable c : closeables) {
                try {
                    if (c != null) {
                        c.close();
                    }
                } catch (IOException e) {
                    // Ignore
                }
            }
        }

        public void close(Iterable<Closeable> closeables) {
            for (Closeable c : closeables) {
                try {
                    if (c != null) {
                        c.close();
                    }
                } catch (IOException e) {
                    // Ignore
                }
            }
        }

        public void copy(final InputStream input, final OutputStream output) throws IOException {
            byte[] buffer = new byte[1024 * 16];
            int n;
            while ((n = input.read(buffer)) > 0) {
                output.write(buffer, 0, n);
            }
            output.flush();
        }

    }

}
