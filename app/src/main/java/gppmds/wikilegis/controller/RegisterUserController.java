package gppmds.wikilegis.controller;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import gppmds.wikilegis.dao.api.PostRequest;
import gppmds.wikilegis.exception.UserException;
import gppmds.wikilegis.model.User;


public class RegisterUserController {

    private static RegisterUserController instance = null;
    private Context context;

    private RegisterUserController(final Context contextParameter) {
        this.context = contextParameter;
    }

    public static RegisterUserController getInstance(final Context context) {
        if (instance == null) {
            instance = new RegisterUserController(context);
        } else {
			/* ! Nothing To Do. */
        }
        return instance;
    }

    public String registerUser(final String firstName,
                               final String lastName,
                               final String email,
                               final String password,
                               final String passwordConfirmation) throws UserException,
            JSONException{

        String registerStatus;
        User user = null;

        try{
            user = new User(firstName, lastName, email, password, passwordConfirmation);
            registerStatus = postUser(user);

        } catch (UserException e){
            String exceptionMessage = e.getMessage();
            registerStatus = exceptionMessage;
        } catch(ExecutionException e){
            String exceptionMessage = e.getMessage();
            registerStatus = exceptionMessage;
        } catch(InterruptedException e){
            String exceptionMessage = e.getMessage();
            registerStatus = exceptionMessage;
        }

        return registerStatus;
    }

    private JSONObject setJSON(User user) throws JSONException {
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("email", user.getEmail());
        jsonParam.put("first_name", user.getFirstName());
        jsonParam.put("last_name", user.getLastName());
        jsonParam.put("password", user.getPassword());
        return jsonParam;
    }

    public String postUser(User user) throws ExecutionException, InterruptedException,
            JSONException{

        JSONObject userJson = null;
        String registerStatus;

        userJson = setJSON(user);

        PostRequest postRequest = new PostRequest(context,
                "http://wikilegis-staging.labhackercd.net/api/user/create/");

        postRequest.execute(userJson.toString(),"application/json").get();

        Log.d("Response", postRequest.getResponse() + "");

        registerStatus = String.valueOf(postRequest.getResponse());

        return registerStatus;
    }
}
