/*
 * Copyright 2020, GeoSolutions Sas.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
*/
package it.geosolutions.mapstore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class UploadPluginControllerTest {
    UploadPluginController controller;
    
    @Before
    public void setUp() {
        controller = new UploadPluginController();
    }
    
    @Test
    public void testUploadValidBundle() throws IOException {
        ServletContext context = Mockito.mock(ServletContext.class);
        controller.setContext(context);
        File tempConfig = TestUtils.copyToTemp(ConfigControllerTest.class.getResourceAsStream("/pluginsConfig.json"));
        Mockito.when(context.getRealPath(Mockito.contains("pluginsConfig.json"))).thenReturn(tempConfig.getAbsolutePath());
        File tempExtensions = TestUtils.copyToTemp(ConfigControllerTest.class.getResourceAsStream("/extensions.json"));
        File tempDist = TestUtils.getDataDir();
        Mockito.when(context.getRealPath(Mockito.contains("extensions.json"))).thenReturn(tempExtensions.getAbsolutePath());
        Mockito.when(context.getRealPath(Mockito.contains("dist/extensions/"))).thenAnswer(new Answer<String>() {

            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                String path = (String)invocation.getArguments()[0];
                return tempDist.getAbsolutePath()  + File.separator + path.substring("dist/extensions/".length());
            }
            
        });
        InputStream zipStream = UploadPluginControllerTest.class.getResourceAsStream("/plugin.zip");
        String result = controller.uploadPlugin(zipStream);
        assertEquals("{\"name\":\"My\",\"dependencies\":[\"Toolbar\"],\"extension\":true}", result);
        String extensions = TestUtils.getContent(tempExtensions);
        assertEquals("{\"MyPlugin\":{\"bundle\":\"dist/extensions/My/myplugin.js\"}}", extensions);
        tempConfig.delete();
        tempExtensions.delete();
    }
    
    @Test
    public void testCustomBundlesPath() throws IOException {
        ServletContext context = Mockito.mock(ServletContext.class);
        controller.setContext(context);
        File tempConfig = TestUtils.copyToTemp(ConfigControllerTest.class.getResourceAsStream("/pluginsConfig.json"));
        Mockito.when(context.getRealPath(Mockito.contains("pluginsConfig.json"))).thenReturn(tempConfig.getAbsolutePath());
        File tempExtensions = TestUtils.copyToTemp(ConfigControllerTest.class.getResourceAsStream("/extensions.json"));
        File tempDist = TestUtils.getDataDir();
        controller.setBundlesPath("custom");
        Mockito.when(context.getRealPath(Mockito.contains("extensions.json"))).thenReturn(tempExtensions.getAbsolutePath());
        Mockito.when(context.getRealPath(Mockito.contains("custom/"))).thenAnswer(new Answer<String>() {

            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                String path = (String)invocation.getArguments()[0];
                return tempDist.getAbsolutePath() + File.separator + path.substring("custom/".length());
            }
            
        });
        InputStream zipStream = UploadPluginControllerTest.class.getResourceAsStream("/plugin.zip");
        controller.uploadPlugin(zipStream);
        assertTrue(new File(tempDist.getAbsolutePath() + File.separator + "My" + File.separator + "myplugin.js").exists());
        tempConfig.delete();
        tempExtensions.delete();
    }
    
    @Test
    public void testUploadInvalidBundle() throws IOException {
        ServletContext context = Mockito.mock(ServletContext.class);
        controller.setContext(context);
        File tempConfig = TestUtils.copyToTemp(ConfigControllerTest.class.getResourceAsStream("/pluginsConfig.json"));
        Mockito.when(context.getRealPath(Mockito.contains("pluginsConfig.json"))).thenReturn(tempConfig.getAbsolutePath());
        File tempExtensions = TestUtils.copyToTemp(ConfigControllerTest.class.getResourceAsStream("/extensions.json"));
        File tempDist = TestUtils.getDataDir();
        Mockito.when(context.getRealPath(Mockito.contains("extensions.json"))).thenReturn(tempExtensions.getAbsolutePath());
        Mockito.when(context.getRealPath(Mockito.contains("dist/extensions/"))).thenAnswer(new Answer<String>() {

            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                String path = (String)invocation.getArguments()[0];
                return tempDist.getAbsolutePath()  + File.separator + path.substring("dist/extensions/".length());
            }
            
        });
        InputStream zipStream = UploadPluginControllerTest.class.getResourceAsStream("/invalid.zip");
        try {
            controller.uploadPlugin(zipStream);
            fail();
        } catch(IOException e) {
            assertNotNull(e);
        }
        
    }
}
