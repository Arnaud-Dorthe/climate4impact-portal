<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="impactservice.* "%>
<div class="impacttopmenu datamenu">

<%

		String Home="/impactportal/";
		//String header=ImpactPages.createHeader(request.getServletPath());
		
		int numProducts = 0,numJobs = 0;
		String numProductsString="-",numJobsString = "-";
		
		try{
			numProducts = User.getUser(request).getShoppingCart().getNumProducts();
		}catch(Exception e){				
		}
		if(numProducts!=0){
			numProductsString = ""+numProducts;
		}
		
		try{
			numJobs = User.getUser(request).getProcessingJobList().getNumProducts();
		}catch(Exception e){				
		}
		if(numJobs!=0){
			numJobsString = ""+numJobs;
		}
				
		String pageName=request.getServletPath();
	

		//out.print(header); //returns file name and path
		%>

  <ul style="height:20px;">
  	<li <% if(pageName.indexOf("login.jsp")!=-1)out.print("class=\"sel\""); %>><a href="<%=Home%>account/login.jsp" >Account</a></li>
   	<li  <% if(pageName.indexOf("basket.jsp")!=-1)out.print("class=\"sel\""); %>><a href="<%=Home%>account/basket.jsp" >Basket <code id="baskettext2">(<%=numProductsString%>)</code></a></li>
  	<li  <% if(pageName.indexOf("jobs.jsp")!=-1)out.print("class=\"sel\""); %>><a href="<%=Home%>account/jobs.jsp" >Jobs <code id="jobnumber">(<%=numJobsString%>)</code></a></li>
   
  </ul>  
 </div>
 
 