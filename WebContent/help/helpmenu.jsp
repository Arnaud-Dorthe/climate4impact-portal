 <%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="impactservice.* "%>
<div  class="impacttopmenu datamenu">

<%
    String Home="/impactportal/";
		String[] menuElements={"help/howto.jsp","help/faq.jsp","help/contactexpert.jsp"};
		String[] menuNames   ={"HowTo","FAQ","Contact the Expert"};
				
		String pageName=request.getServletPath();
		
		boolean[] highLighted = new boolean[menuElements.length];

		for(int j=0;j<menuElements.length;j++){
			if(pageName.indexOf(menuElements[j])!=-1)highLighted[j]=true;else highLighted[j]=false;
		}
			out.print("<ul style=\"height:20px;\">");
		for(int j=0;j<menuElements.length;j++){
			out.print("<li ");
			if(highLighted[j])out.print("class=\"sel\"");
			out.print(">");
			out.print("<a href=\""+Home+menuElements[j]+"\">"+menuNames[j]+"</a>");
			out.print("</li>");
		}
		out.print("</ul>");
		%>
 
 </div>
 
 