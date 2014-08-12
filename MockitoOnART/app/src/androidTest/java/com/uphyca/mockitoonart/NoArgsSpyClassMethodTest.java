package com.uphyca.mockitoonart;

import org.mockito.Spy;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NoArgsSpyClassMethodTest extends MockitoTestCase {

    @Spy
    TargetClass underTest;

    public void testThatWorkProperly() throws Exception {
        when(underTest.sayGoodbye()).thenReturn("Goodbye, Mockito");
        assertThat(underTest.sayGoodbye()).isEqualTo("Goodbye, Mockito");
        verify(underTest).sayGoodbye();
    }
}
