package com.uphyca.mockitoonart;

import org.mockito.Mock;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NoArgsInterfaceMethodTest extends MockitoTestCase {

    @Mock
    TargetInterface underTest;

    public void testThatWorkProperly() throws Exception {
        try {
            when(underTest.sayGoodbye()).thenReturn("Goodbye, Mockito");
            //Failed with Mockito 1.9.5/dexmaker-mockito 1.1
            fail();
        }catch (IllegalArgumentException expected) {
        }
        //assertThat(underTest.sayGoodbye()).isEqualTo("Goodbye, Mockito");
        //verify(underTest).sayGoodbye();
    }
}
