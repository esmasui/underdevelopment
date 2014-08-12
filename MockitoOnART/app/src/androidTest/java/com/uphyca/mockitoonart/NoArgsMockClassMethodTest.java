package com.uphyca.mockitoonart;

import org.mockito.Mock;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NoArgsMockClassMethodTest extends MockitoTestCase {

    @Mock
    TargetClass underTest;

    public void testThatWorkProperly() throws Exception {
        when(underTest.sayGoodbye()).thenReturn("Goodbye, Mockito");
        assertThat(underTest.sayGoodbye()).isEqualTo("Goodbye, Mockito");
        verify(underTest).sayGoodbye();
    }
}
