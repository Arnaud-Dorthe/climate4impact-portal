var Basket = function(){
	var t = this;
	
	var basketElement = undefined;
	
	this.removeId = function(id){
		var doneFunction = function(json){
			if(json.error){
				alert(json.error);
		    }else{
				adjustNumberOfDataSetsDisplayedInMenuBar(json);
		    }
/*			if(basketElement){
			  t.populateBasketList(basketElement);
			}
*/		}
	
		$.ajax({
			type: "GET",
			url: impactService,
			data: { service:'basket',request:'removeFromList',id:id }  ,
			success:doneFunction,
			dataType: 'json'
		});
	};
	
	this.requestBasket = function(callbackfunction,failedfunction){
		$.ajax({
    		type: "GET",
    		url: impactService,
    		data: { service:'basket',request:'getoverview' }  ,
    		success:callbackfunction,
    		dataType: 'json'
		});
	};
    
    this.populateBasketList = function(jqelement){
      if(!jqelement)return;
    	if(jqelement){
    		basketElement = jqelement;
    		jqelement.html("Loading basket...");
    	}
    	
   		var passFn = function(json){
   			jqelement.html(t.createBasketHTMLFromJSON(json));
   		}
   		t.requestBasket(passFn,passFn);
   		/*var failFn = function(msg){
   			var data = {};
   			data.responseText='Unable to retrieve basket from server';
   			passFn(data);
   		}*/
    };
	  
    
    this.createBasketSelectorHTMLFromJSON = function(json){
    	return t.createBasketHTMLFromJSON(json);
    };
    
    
  var makeHTMLFromJSONChilds = function(children,viewerurl,browserurl){
    var html="";
    for(var j=0;j<children.length;j++){
      var view = "-"; //Either view or browse
      var get = "-";
      var subset = "-";
      var remove = "";
      var child = children[j];
      if(child.type=='file'){
        if(child.httpurl){
          get="<a target='_blank' href=\""+child.httpurl+"\">get</a>";
        }
        if(child.dapurl){
          view="<a href='"+viewerurl+"dataset="+URLEncode(child.dapurl)+"'>view</a>";
          subset="subset";
        }
      }else{
        if(!child.children){
          view="<a href='"+browserurl+"catalog="+URLEncode(child.catalogurl)+"'>browse</a>";
        }
      }
      remove="<a href=\"#\" onclick='basket.removeId(\""+child.id+"\");return false;'>X</a>";
      html+="<tr>";
      html+="<td>"+child.date+"</td><td>"+child.text+"</td><td>"+view+"</td><td>"+get+"</td><td>"+subset+"</td><td>"+remove+"</td>";
      html+="</tr>";
      if(child.children){
        html+=makeHTMLFromJSONChilds(child.children,viewerurl,browserurl);
      }
      
    }
    return html;
  }
	this.createBasketHTMLFromJSON = function(json){
		if(json.error){
			return "An error occured while retrieving the basket: "+json.error;
		}
		var viewerurl = json.viewer;
		var browserurl = json.browser;
		html='<table class="basket">'
		html+="<td style=\"width:150px;background-color:#DDD;\"><b>Added on:</b></td>";
		html+="<td style=\"width:600px;background-color:#DDD;\"><b>Identifier</b></td>";
		html+="<td style=\"background-color:#DDD;\"><b>View</b></td>";
		html+="<td style=\"background-color:#DDD;\"><b>Get</b></td>";
		html+="<td style=\"background-color:#DDD;\"><b>Subset</b></td>";
	  	html+="<td style=\"background-color:#DDD;\"><b>X</b></td>";
	  	html+="</tr>";
	  html += makeHTMLFromJSONChilds(json.children,viewerurl,browserurl);
		html+='</table>'
		return html;
	};
	
	this.postIdentifiersToBasket = function(options){
	  console.log("postIdentifiersToBasket");
		var doneFunction = function(json){
			if(json.error){
				if(json.statuscode){
					if(json.statuscode==401){
						generateLoginDialog(function(){basket.postIdentifiersToBasket(options);})
						return;
					}
				}
				customalert(json.error);
				
			}
			adjustNumberOfDataSetsDisplayedInMenuBar(json);
			console.log("postIdentifiersToBasket Done");
	    try{
        if(basketWidget){
          basketWidget.reload();
        }
      }catch(e){
       //console.log(e);
      }
		};
		 //alert(dump(options));
		$.ajax({
			type: "POST",
			url: '/impactportal/ImpactService?service=basket&mode=add',
			data: options,
			success:doneFunction,
			dataType: 'json'
			});
	};
};

//Singleton basket is always there...
var basket = new Basket();

