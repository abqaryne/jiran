package com.jiran.app.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.jiran.app.util.Resource;

import org.junit.Test;

public class ResourceTest {

    @Test
    public void successCarriesData() {
        Resource<String> resource = Resource.success("ok");
        assertEquals(Resource.Status.SUCCESS, resource.status);
        assertEquals("ok", resource.data);
        assertNull(resource.message);
    }

    @Test
    public void errorCarriesMessage() {
        Resource<String> resource = Resource.error("failed");
        assertEquals(Resource.Status.ERROR, resource.status);
        assertEquals("failed", resource.message);
        assertNull(resource.data);
    }
}
