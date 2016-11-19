package gppmds.wikilegis.controller;

import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import gppmds.wikilegis.exception.UserException;
import gppmds.wikilegis.model.User;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@RunWith(PowerMockRunner.class)
public class MockedTests {

    @Mock
    Context context;

    @Test
    public void testRegisterUserWithMaxLengthFirstName() throws Exception {

        RegisterUserController registerUserController = mock(RegisterUserController.class);
        PowerMockito.whenNew(RegisterUserController.class).withArguments(context)
                .thenReturn(registerUserController);

        User user = null;
        try{
            user = new User("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                    "Cardoso", "a@a.com", "123456", "123456");
        }catch(UserException e){
            e.printStackTrace();
        }
        when(registerUserController.postUser(user)).thenReturn("400");

        assertThat(RegisterUserController.getInstance(context).registerUser("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                "Cardoso", "a@a.com", "123456", "123456"), equalTo("400"));
    }

}
