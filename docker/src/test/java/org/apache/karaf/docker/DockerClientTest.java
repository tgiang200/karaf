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

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * DockerClient factory test.
 */
public class DockerClientTest {

    private DockerClient dockerClient;

    @Before
    public void setup() {
        dockerClient = new DockerClient(null);
    }

    @Test
    public void testDockerInfo() throws Exception {
        Info info = dockerClient.info();
        System.out.println("Info:");
        System.out.println("\tDriver: " + info.getDriver());
        System.out.println("\tDriver Status: " + info.getDriverStatus());
        System.out.println("\tExecution Driver: " + info.getExecutionDriver());
        System.out.println("\tIndex Server Address: "  + info.getIndexServerAddress());
        System.out.println("\tInit Path: " + info.getInitPath());
        System.out.println("\tInit SHA1: " + info.getInitSha1());
        System.out.println("\tKernel Version: " + info.getKernelVersion());
        System.out.println("\tContainers: " + info.getContainers());
        System.out.println("\tImages: " + info.getImages());
        System.out.println("\tNFD: " + info.getNfd());
        System.out.println("\tNGoRoutines: " + info.getNgoroutines());
        System.out.println("\tMemory Limit enabled: " + info.isMemoryLimit());
        System.out.println("\tSwap Limit enabled: " + info.isSwapLimit());
    }

    @Test
    public void testDockerVersion() throws Exception {
        Version version = dockerClient.version();
        System.out.println("Version:");
        System.out.println("\tAPI version: " + version.getApiVersion());
        System.out.println("\tArch: " + version.getArch());
        System.out.println("\tBuild Time: " + version.getBuildTime());
        System.out.println("\tExperimental: " + version.getExperimental());
        System.out.println("\tGit Commit: " + version.getGitCommit());
        System.out.println("\tGo Version: " + version.getGoVersion());
        System.out.println("\tKernel Version: " + version.getKernelVersion());
        System.out.println("\tOS: " + version.getOs());
        System.out.println("\tVersion: " + version.getVersion());
    }

    @Test
    public void testContainers() throws Exception {
        for (Container container : dockerClient.containers(true)) {
            System.out.println("Container " + container.getId());
        }
    }

}