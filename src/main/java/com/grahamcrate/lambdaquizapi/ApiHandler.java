package com.grahamcrate.lambdaquizapi;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 *
 * @author grahamcrate
 */
public class ApiHandler extends AbstractHandler implements RequestStreamHandler {

    public static final String QUESTION_1 = System.getenv("QUESTION_1");
    public static final String QUESTION_2 = System.getenv("QUESTION_2");
    public static final String QUESTION_3 = System.getenv("QUESTION_3");
    public static final String QUESTION_4 = System.getenv("QUESTION_4");
    public static final String QUESTION_5 = System.getenv("QUESTION_5");
    
    public static final String OPTIONS_1 = System.getenv("OPTIONS_1");
    public static final String OPTIONS_2 = System.getenv("OPTIONS_2");
    public static final String OPTIONS_3 = System.getenv("OPTIONS_3");
    public static final String OPTIONS_4 = System.getenv("OPTIONS_4");
    public static final String OPTIONS_5 = System.getenv("OPTIONS_5");

    public static final String ANSWER_1 = System.getenv("ANSWER_1");
    public static final String ANSWER_2 = System.getenv("ANSWER_2");
    public static final String ANSWER_3 = System.getenv("ANSWER_3");
    public static final String ANSWER_4 = System.getenv("ANSWER_4");
    public static final String ANSWER_5 = System.getenv("ANSWER_5");

    public static final String SUCCESS_MSG = System.getenv("SUCCESS_MSG");

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

        LambdaLogger logger = null;

        logger = context.getLogger();

        JSONObject jsonResponse = new JSONObject();

        try {
            InboundRequest iReq = getInboundRequest(inputStream);
            switch (iReq.getPath()) {
                case "/questions":
                    jsonResponse = createSuccessResponse(getQuestions());
                    break;
                case "/answers":
                    String answer1 = iReq.getBody().get("answer1").toString();
                    String answer2 = iReq.getBody().get("answer2").toString();
                    String answer3 = iReq.getBody().get("answer3").toString();
                    String answer4 = iReq.getBody().get("answer4").toString();
                    String answer5 = iReq.getBody().get("answer5").toString();

                    jsonResponse = createSuccessResponse(checkAnswers(answer1, answer2,
                            answer3, answer4, answer5));
                    break;

            }

        } catch (ParseException | IOException ex) {
            jsonResponse = createErrorResponse(ex.getMessage());
        }

        writeResponse(jsonResponse, outputStream);
    }

    private Map<String,Object> getQuestions() {
        Map<String,Object> answersResponse = new HashMap<>();
        answersResponse.put("question1", QUESTION_1);
        answersResponse.put("question2", QUESTION_2);
        answersResponse.put("question3", QUESTION_3);
        answersResponse.put("question4", QUESTION_4);
        answersResponse.put("question5", QUESTION_5);
        
        answersResponse.put("options1", OPTIONS_1);
        answersResponse.put("options2", OPTIONS_2);
        answersResponse.put("options3", OPTIONS_3);
        answersResponse.put("options4", OPTIONS_4);
        answersResponse.put("options5", OPTIONS_5);

        return answersResponse;
    }

    private Map<String,Object> checkAnswers(String answer1, String answer2,
            String answer3, String answer4, String answer5) {
        
        int incorrectAnswers = 0;
        
        if(!answer1.equals(ANSWER_1)) {
            incorrectAnswers++;
        }
        if(!answer2.equals(ANSWER_2)) {
            incorrectAnswers++;
        }
        if(!answer3.equals(ANSWER_3)) {
            incorrectAnswers++;
        }
        if(!answer4.equals(ANSWER_4)) {
            incorrectAnswers++;
        }
        if(!answer5.equals(ANSWER_5)) {
            incorrectAnswers++;
        }
        
        Map<String,Object> checkResponse = new HashMap<>();
        checkResponse.put("inconnectAnswers", incorrectAnswers);
        
        if(incorrectAnswers == 0) {
            checkResponse.put("message", SUCCESS_MSG);
        }
        
        return checkResponse;
    }
}
