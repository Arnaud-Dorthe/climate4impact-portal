<!-- Header -->

<%
	String Home="/impactportal/";
    out.println("<div id=\"bodycontainer\">");
%>
  <link rel="icon" type="image/png" href="/impactportal/favicon.ico">
  <div class="eimpactheader" style="z-index:2;"> 
    <div class="logo"><a href="https://is.enes.org/"><img border=0 src="<%=Home%>images/is-enes2-logo.png"/></a></div>
    
  
    <div style="float: right; margin-top: 0.5em;">
      <a target="_blank" href="https://is.enes.org/">IS-ENES</a> | <a href="<%=Home%>help/contactexpert.jsp">Contact</a> | <a href="<%=Home%>account/login.jsp">Login</a>
    </div>
    <h1 style="padding-top:26px;"><a href="<%=Home%>index.jsp">
    <!-- <span>IS-ENES e-Impact Portal</span> -->
  
    <span >ENES Portal Interface for <br/>the Climate Impact Communities</span>
    
    </a></h1>
   
  </div>
  
  <div class="impactheader"><span></span> </div>
 
  
  <%="<div class=\"c1\"><div class=\"c2\"><div class=\"impactcontainer\">"%>
 	<jsp:include page="mainmenu.jsp" />
