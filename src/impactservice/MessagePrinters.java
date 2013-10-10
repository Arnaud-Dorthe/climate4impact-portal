package impactservice;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.jsp.JspWriter;

import tools.DebugConsole;
import tools.SendMail;

public class MessagePrinters {
  public static void printWarningMessage(JspWriter out,Exception exception) throws IOException{
    printWarningMessage(out,"default",exception);
  }
  public static void emailFatalErrorException(String subject,Exception exception) throws IOException{
    try {
      String[] to={Configuration.getEmailToSendFatalErrorMessages()};
      final Writer result = new StringWriter();
      final PrintWriter printWriter = new PrintWriter(result);
      exception.printStackTrace(printWriter);
      String msg=exception.getMessage()+"\n\n"+result.toString();
      DebugConsole.errprintln(msg);
      SendMail.sendMail(to,Configuration.getEmailToSendFatalErrorMessages(),"[CLIMATE4IMPACT:"+subject+"]", msg);
      
    } catch (Exception e) {
      DebugConsole.errprintln(e.getMessage());
    }

  }
  public static void emailFatalErrorException(String subject,Throwable exception) throws IOException{
    try {
      String[] to={Configuration.getEmailToSendFatalErrorMessages()};
      final Writer result = new StringWriter();
      final PrintWriter printWriter = new PrintWriter(result);
      exception.printStackTrace(printWriter);
      String msg=exception.getMessage()+"\n\n"+result.toString();
      DebugConsole.errprintln(msg);
      SendMail.sendMail(to,Configuration.getEmailToSendFatalErrorMessages(),"[CLIMATE4IMPACT:"+subject+"]", msg);
      
    } catch (Exception e) {
      DebugConsole.errprintln(e.getMessage());
    }

  }
  
  public static void emailFatalErrorMessage(String subject,String message) throws IOException{
    try {
      String[] to={Configuration.getEmailToSendFatalErrorMessages()};
      DebugConsole.errprintln(message);
      SendMail.sendMail(to,Configuration.getEmailToSendFatalErrorMessages(),"[CLIMATE4IMPACT: FATAL ERROR]: "+subject, message);
      
    } catch (Exception e) {
      DebugConsole.errprintln(e.getMessage());
    }

  }
  public static void printWarningMessage(JspWriter out,String subject,Exception exception) throws IOException{
 
    emailFatalErrorException(subject,exception);
    out.print("<p class=\"error\"><img src=\""+Configuration.getHomeURL()+"/images/warning.png\"/><br/>Warning:<br/>");
    out.print(tools.HTMLParser.textToHTML(exception.getMessage()));
    out.print("</p><br/>");
  }
}
