package dasniko.codegolf;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.extern.log4j.Log4j;

import java.util.Collections;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Log4j
public class CodeGolfHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {

    @Override
    public AwsProxyResponse handleRequest(AwsProxyRequest request, Context context) {
        String username = request.getBody();
        log.info("Received event for user: " + username);

        CodeGolf codeGolf = new CodeGolf();
        String result;
        try {
            result = codeGolf.play(username);
        } catch (Exception e) {
            result = e.getMessage();
        }
        log.info("Result: " + result);

        return new AwsProxyResponse(200, Collections.singletonMap("Content-Type", "text/plain"), result);
    }

}
