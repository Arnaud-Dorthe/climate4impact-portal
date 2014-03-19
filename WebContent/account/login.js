
  var OpenIDProviders = {
      'SMHI-LIU-NSC':{
        name:'SMHI-LIU-NSC',
        openidprefix:'https://esg-dn1.nsc.liu.se/esgf-idp/openid/',
        createaccount:'https://esg-dn1.nsc.liu.se/esgf-web-fe/createAccount',
        accountinfo:'http://esg-dn1.nsc.liu.se/esgf-web-fe/accountsview',
        logocls:'logo_Sweden'},
      'PCMDI':       {
    	  name:'PCMDI'       ,
    	  openidprefix:'https://pcmdi9.llnl.gov/esgf-idp/openid/',
    	  createaccount:'https://pcmdi9.llnl.gov/esgf-web-fe/createAccount',
    	  accountinfo:'http://pcmdi9.llnl.gov/esgf-web-fe/accountsview',
    	  logocls:'logo_USA'},
      'CEDA':        {
    	  name:'BADC/CEDA'   ,
    	  openidprefix:'https://ceda.ac.uk/openid/',
    	  createaccount:'https://ceda.ac.uk/',        
    	  accountinfo:'https://ceda.ac.uk/',
    	  logocls:'logo_UK'},
      'DKRZ':        {
    	  name:'DKRZ'        ,
    	  openidprefix:'https://esgf-data.dkrz.de/esgf-idp/openid/',
    	  createaccount:'https://esgf-data.dkrz.de/esgf-web-fe/createAccount',
    	  accountinfo:'https://esgf-data.dkrz.de/esgf-web-fe/accountsview',
    	  logocls:'logo_Germany'},
	  /*'NCI':        {
    	  name:'NCI'        ,
    	  openidprefix:'https://esg2.nci.org.au/esgf-idp/openid/' ,
    	  createaccount:'https://esg2.nci.org.au/esgf-web-fe/createAccount',
    	  logocls:'logo_Germany'},*/
      'PIK':         {
    	  name:'PIK Potsdam' ,
    	  openidprefix:'https://esg.pik-potsdam.de/esgf-idp/openid/',
    	  createaccount:'https://esg.pik-potsdam.de/esgf-web-fe/createAccount',
    	  accountinfo:'https://esg.pik-potsdam.de/esgf-web-fe/accountsview',
    	  logocls:'logo_Germany'},
      
  };


 var getOpenIDProviderFromOpenId = function(openidIdentifier){
	 for(var id in OpenIDProviders){
		 var provider = OpenIDProviders[id];
		 if(openidIdentifier.indexOf(provider.openidprefix)!=-1){
			 return provider;
		 }
	 }
 }
  

var checkOpenIdCookie = function(a) {
  if (a.checked == false) {
    makeHTTPRequest("/impactportal/consumer?keepid=off");
  } else {
    makeHTTPRequest("/impactportal/consumer?keepid=on");
  }

};

$( document ).ready(function() {
  var dataNodeButtons = $("#datanodebuttons");
  var html='<ul>';
  for(var id in OpenIDProviders){
    html+='<li><button id="dnb_'+id+'" class="datanodebutton" onclick="openDialog(\''+id+'\');">'+OpenIDProviders[id].name+'</button></li>';// - '+ OpenIDProviders[id].url+ '&lt;username&gt;</li>' ;
    //alert(id.name);
  }
  html+='</ul>';
  dataNodeButtons.html(html);
  for(var id in OpenIDProviders){
 
    $("#dnb_"+id).button({icons: {primary: OpenIDProviders[id].logocls},text:true});
  }
  $('#login_button').button();
});

$(function() {

  
  
  var name = $("#name"),  allFields = $([]).add(name), tips = $(".validateTips");
  name.keyup(function(e) {
    if (e.keyCode == 13) {
      $('#composeidentifierbutton').click();
    }
  });
  name.on('input',function(){
    composeId();
  });
  
  // Following lines are for IE7 Fix:
  var namebusy=false;
  name.on('propertychange',function(){
	  if(namebusy == true)return;
	  namebusy = true;
	  composeId();
	  namebusy = false;
  });
  
  
  function updateTips(t) {
    tips.text(t).addClass("ui-state-highlight");
    setTimeout(function() {
      tips.removeClass("ui-state-highlight", 1500);
    }, 500);
  }
  function checkLength(o, n, min, max,showtips) {
    if (o.val().length > max || o.val().length < min) {
      if(showtips){
        o.addClass("ui-state-error");
        updateTips("*Length of " + n + " must be between " + min + " and " + max
            + " characters.");
      }
      return false;
    } else {
      return true;
    }
  }
  function checkRegexp(o, regexp, n,showtips) {
    if (!(regexp.test(o.val()))) {
      if(showtips){
        o.addClass("ui-state-error");
        updateTips(n);
      }
      return false;
    } else {
      return true;
    }
  }

  var composeId = function(showtips){
    name.removeClass("ui-state-error");
    tips.removeClass("ui-state-highlight");
    tips.text("");
    var bValid = true;
    bValid = bValid && checkLength(name, "username", 3, 30,showtips);
    bValid = bValid && checkRegexp(name, /^[a-z]([0-9a-z_.])+$/i,  "*Username may consist of a-z, 0-9, underscores, begin with a letter.",showtips);
   
    var dataCentreName = $('#dialog-form').dialog("option")["datacentre"];
    var username = name.val();
    var openid = "";
    var dataCentreOpenIdProvider = OpenIDProviders[dataCentreName].openidprefix;
  
    
    openid = dataCentreOpenIdProvider+username;

    $("#composedopenididentifier").text(openid);
    if (bValid) {
      return openid;
    }
    return undefined;
  };

  $("#dialog-form")
  .dialog(
      {
        autoOpen : false,
        height : 350,
        width : 500,
        modal : true,
        buttons : {
          Cancel : function() {

            $(this).dialog("close");
          },
          "Compose identifier" : {
            text : "Sign in",
            id : 'composeidentifierbutton',
            click : function() {
              var openid=composeId(true);
              if(openid){
                $('#openid_identifier_input').val(openid);
                $('#login_button').click();
                $(this).dialog("close");
              }
            }
          }
        }

      ,

      close : function() {
        allFields.val("").removeClass("ui-state-error");
      }
});
});
var openDialog = function(datacentre) {
  
  $('#dialog-form').dialog('open');
  $('#dialog-form').dialog("option", "datacentre", datacentre);
  $("#composedopenididentifier").text(OpenIDProviders[datacentre].openidprefix);
  if(OpenIDProviders[datacentre].createaccount){
	  $("#datacentreurl").html("- <a target=\"_blank\" href=\""+OpenIDProviders[datacentre].createaccount+"\">Create an account on this data node.</a><br/>" +
	  		"- <a href=\"http://localhost:8080/impactportal/help/howto.jsp?q=create_esgf_account\">Read why you will be directed to another website.</a>");
  }
}