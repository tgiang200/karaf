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
package org.apache.karaf.docker.command;

import org.apache.karaf.docker.ContainerConfig;
import org.apache.karaf.docker.HostConfig;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Command(scope = "docker", name = "bootstrap", description = "Create a Docker container based on a given configuration")
@Service
public class BootstrapCommand extends DockerCommandSupport {

    @Argument(index = 0, name = "name", description = "The name of the Docker container", required = true, multiValued = false)
    String name;

    @Option(name = "--hostname", description = "Hostname of the Docker container", required = false, multiValued = false)
    String hostname = "docker";

    @Option(name = "--user", description = "User of the Docker container", required = false, multiValued = false)
    String user = "";

    @Option(name = "--tty", description = "Enable TTY for the Docker container", required = false, multiValued = false)
    boolean tty = true;

    @Option(name = "--attachStdout", description = "Attach stdout for the Docker container", required = false, multiValued = false)
    boolean attachStdout = true;

    @Option(name = "--attachStderr", description = "Attach stderr for the Docker container", required = false, multiValued = false)
    boolean attachStderr = true;

    @Option(name = "--attachStdin", description = "Attach stdin for the Docker container", required = false, multiValued = false)
    boolean attachStdin = true;

    @Option(name = "--image", description = "Image to use for the Docker container", required = false, multiValued = false)
    String image = "karaf:4.2.0";

    @Option(name = "--cmd", description = "Command to execute when starting the Docker container", required = false, multiValued = false)
    String cmd = "/bin/karaf";

    @Option(name = "--workingDir", description = "Working directory of the Docker container", required = false, multiValued = false)
    String workingDir = "";

    @Option(name = "--openStdin", description = "Enable and open stdin for the Docker container", required = false, multiValued = false)
    boolean openStdin = true;

    @Option(name = "--stdinOnce", description = "Enable single use of std in the Docker container", required = false, multiValued = false)
    boolean stdinOnce = true;

    @Option(name = "--exposedPort", description = "Port to expose from the Docker container", required = false, multiValued = false)
    String exposedPort = "8101/tcp";

    @Option(name = "--hostPrivileged", description = "Set host config privileges for the Docker container", required = false, multiValued = false)
    boolean hostPrivileged = false;

    @Option(name = "--hostPublishAllPorts", description = "Expose all ports for the Docker container", required = false, multiValued = false)
    boolean hostPublishAllPorts = false;

    @Option(name = "--hostNetworkMode", description = "Define the network mode for the Docker container", required = false, multiValued = false)
    String hostNetworkMode = "bridge";

    @Option(name = "--hostPortBinding", description = "Define the port binding for the Docker container", required = false, multiValued = false)
    String hostPortBinding = "49153";

    @Override
    public Object execute() throws Exception {
        ContainerConfig containerConfig = new ContainerConfig();
        containerConfig.setTty(tty);
        containerConfig.setAttachStdin(attachStdin);
        containerConfig.setAttachStderr(attachStderr);
        containerConfig.setAttachStdout(attachStdout);
        containerConfig.setImage(image);
        containerConfig.setHostname(hostname);
        containerConfig.setUser(user);
        containerConfig.setCmd(new String[]{ cmd });
        containerConfig.setWorkingDir(workingDir);
        containerConfig.setOpenStdin(openStdin);
        containerConfig.setStdinOnce(stdinOnce);
        Map<String, Map<String, String>> exposedPorts = new HashMap<>();
        exposedPorts.put(exposedPort, new HashMap<>());
        containerConfig.setExposedPorts(exposedPorts);
        HostConfig hostConfig = new HostConfig();
        hostConfig.setPrivileged(hostPrivileged);
        hostConfig.setPublishAllPorts(hostPublishAllPorts);
        hostConfig.setNetworkMode(hostNetworkMode);
        hostConfig.setLxcConf(new String[]{});
        Map<String, String> binding = new HashMap<>();
        binding.put("0.0.0.0", hostPortBinding);
        Map<String, List<Map<String, String>>> portBindings = new HashMap<>();
        List<Map<String, String>> portBinding = new ArrayList<>();
        portBinding.add(binding);
        portBindings.put(exposedPort, portBinding);
        hostConfig.setPortBindings(portBindings);
        getDockerService().bootstrap(name, url, containerConfig);
        return null;
    }

}
