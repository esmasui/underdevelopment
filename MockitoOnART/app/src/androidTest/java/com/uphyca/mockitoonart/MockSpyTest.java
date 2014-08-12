package com.uphyca.mockitoonart;

import org.mockito.Spy;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MockSpyTest extends MockitoTestCase {

    @Spy
    TargetClass underTest;

    public void testThatWorkProperly() throws Exception {
        when(underTest.sayHello("foo")).thenReturn("Hello, Mockito");
        assertThat(underTest.sayHello("foo")).isEqualTo("Hello, Mockito");
        verify(underTest).sayHello("foo");
    }
}
