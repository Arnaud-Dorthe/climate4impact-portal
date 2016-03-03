package tokenapi;

import impactservice.MessagePrinters;

import java.io.IOException;
import java.security.cert.X509Certificate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import tools.Debug;
import tools.JSONResponse;

/**
 * Servlet implementation class CLIPCTokenAPI
 */
public class CLIPCTokenAPI extends HttpServlet {
  private static final long serialVersionUID = 1L;
  /*
   * Required steps for configuration of functioning clipc token service
   * Step 1: Generate a public/private key
   * openssl genrsa -des3 -out clipc.key 4096

   * 
   */

  /**
   * @see HttpServlet#HttpServlet()
   */
  public CLIPCTokenAPI() {
    super();
    // TODO Auto-generated constructor stub
  }

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // TODO Auto-generated method stub
    JSONResponse jsonResponse = new JSONResponse(request);

    String expectedIssuerDN   = "CN=knmi_clipc_ca_tokenapi, OU=RDWDT, O=KNMICLIPCCA";
    String expectedSubjectDN  = "CN=clipctokenapiformaris_20160303, OU=MARIS, O=MARIS";
    String issuerDN=null;
    String subjectDN=null;
    // org.apache.catalina.authenticator.SSLAuthenticator
    X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
    if (null != certs && certs.length > 0) {
      X509Certificate cert = certs[0];
      issuerDN = cert.getIssuerDN().toString();
      subjectDN = cert.getSubjectDN().toString();
    }
    String errorMessage=null;
    boolean authorizationOK=false;
    boolean authenticationOK = false;
    if(issuerDN!=null && subjectDN!=null){
      Debug.println("CLIPCTokenAPI request received");
      Debug.println("issuerDN : ["+issuerDN+"]");
      Debug.println("subjectDN: ["+subjectDN+"]");
      authenticationOK = true;
      if(issuerDN.equals(expectedIssuerDN)){
        if(subjectDN.equals(expectedSubjectDN)){
          authorizationOK = true;
        }else{
          errorMessage="subjectDN Mismatch!";
        }
      }else{
        errorMessage="issuerDN Mismatch!";
      }
    }else{
      errorMessage="No client authentication certificate found.";
    }
    
    

    JSONObject jsonobj = new JSONObject();

    if(authenticationOK==false&&authorizationOK==false){
      jsonResponse.setErrorMessage(errorMessage, 401, null, null, null);
      MessagePrinters.emailFatalErrorMessage("clipctokenapi 401", errorMessage);
    }
    if(authenticationOK==true&&authorizationOK==false){
      jsonResponse.setErrorMessage(errorMessage, 403, null, null, null);  
      MessagePrinters.emailFatalErrorMessage("clipctokenapi 403", errorMessage);
    }

    if(jsonResponse.hasError()==false){
      try {
        if(authenticationOK && authorizationOK){
          //Now generate a key for maris for a specfic user
          jsonobj.put("status", "ok");
        }
      } catch (JSONException e) {
      }
      jsonResponse.setMessage(jsonobj);
    }
    try {
      jsonResponse.print(response);
    } catch (Exception e1) {

    }
  }

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // TODO Auto-generated method stub
  }

}
