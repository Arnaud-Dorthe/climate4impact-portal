package esgfsearch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tools.Debug;
import tools.DiskCache;
import tools.HTTPTools;
import tools.LockOnQuery;
import tools.HTTPTools.WebRequestBadStatusException;
import tools.KVPKey;
import tools.MyXMLParser;
import tools.MyXMLParser.XMLElement;
import tools.JSONResponse;

public class Search {
  String searchEndPoint = null;
  String cacheLocation = null;
  public Search(String searchEndPoint, String cacheLocation) {
    this.searchEndPoint = searchEndPoint;
    this.cacheLocation = cacheLocation;
  }

  public JSONResponse getFacets(String facets, String query) {
    try{
      LockOnQuery.lock(facets+query);
      JSONResponse r = getFacetsImp(facets,query);
      LockOnQuery.release(facets+query);
      return r;
    }catch(Exception e){
      JSONResponse r = new JSONResponse();
      r.setException(e.getClass().getName(), e);
      
      e.printStackTrace();
      return r;
    }
    
  }
  
  public JSONResponse getFacetsImp(String facets,String query) throws JSONException {
    int searchLimit = 10;
    JSONResponse r = new JSONResponse();
    
    String esgfQuery = "facets=*&limit="+searchLimit+"&";
    
    if(facets!=null){
      esgfQuery = "facets="+facets+"&";
    }
    
    if(query!=null){
      Debug.println("QUERY is "+query);
       KVPKey kvp = HTTPTools.parseQueryString(query);
       SortedSet<String> kvpKeys = kvp.getKeys();
       for(String k : kvpKeys){
         Debug.println("KEY "+k+" = "+kvp.getValue(k));
         for(String value : kvp.getValue(k)){
           try {
            esgfQuery = esgfQuery+k+"="+URLEncoder.encode(value,"UTF-8")+"&";
          } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
         }
       }
    }
    
    Debug.println("Query is "+searchEndPoint+esgfQuery);
    
    String identifier = "ESGFSearch.getFacets"+esgfQuery;
    
    String XML = DiskCache.get(cacheLocation, identifier+".xml", 200000);
    if(XML == null){
      try {
        XML = HTTPTools.makeHTTPGetRequest(new URL(searchEndPoint+esgfQuery));
        DiskCache.set_2(cacheLocation,identifier+".xml",XML);
      } catch (MalformedURLException e2) {
        r.setException("MalformedURLException",e2);
        return r;
      } catch (WebRequestBadStatusException e2) {
        r.setException("WebRequestBadStatusException",e2);
        return r;
      } catch (IOException e2) {
        r.setException("IOException",e2);
        return r;
      }
    }
    
    
    
    MyXMLParser.XMLElement el = new MyXMLParser.XMLElement();
    
    try {
      el.parseString(XML);
    } catch (Exception e1) {
      r.setErrorMessage("Unable to parse XML");
      return r;
      // TODO Auto-generated catch block
      //e1.printStackTrace();
    }
    
    JSONObject facetsObj = new JSONObject();
  
    
    try {
      Vector<XMLElement> lst=el.get("response").getList("lst");
      
      for(XMLElement a : lst){
        
        try{
          if(a.getAttrValue("name").equals("facet_counts")){
            Vector<XMLElement> facet_counts=a.getList("lst");
            for(XMLElement facet_count : facet_counts){
              if(facet_count.getAttrValue("name").equals("facet_fields")){
                Vector<XMLElement> facet_fields=facet_count.getList("lst");
                for(XMLElement facet_field : facet_fields){
                 
                  Vector<XMLElement> facet_names=facet_field.getList("int");
                  SortedMap<String,Integer> sortedFacetElements = new TreeMap<String,Integer>();
                  for(XMLElement facet_name : facet_names){
                    sortedFacetElements.put(facet_name.getAttrValue("name"),Integer.parseInt(facet_name.getValue()));
                  }
                
                  JSONArray facet = new JSONArray();
                  
                  //int first = 0;
                  for (SortedMap.Entry<String, Integer> entry : sortedFacetElements.entrySet()){
                    //if(first <5){
                    //Debug.println(entry.getKey());
                    //first++;
                      facet.put(entry.getKey());//, entry.getValue());
                    //}
                  }
                  facetsObj.put(facet_field.getAttrValue("name"),facet);
                }
              }
            }
          }
        }catch(Exception e){
          r.setErrorMessage("No name attribute");
          return r;
        }
      }
      
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    JSONObject result = new JSONObject();
    JSONObject responseObj = new JSONObject();
    result.put("response",responseObj);
    
    responseObj.put("limit",searchLimit);
    
   
    
    JSONArray searchResults = new JSONArray(); 
    
    try {
      Vector<XMLElement> result1=el.get("response").getList("result");
      
      for(XMLElement a : result1){
        
        try{
          if(a.getAttrValue("name").equals("response")){
            responseObj.put("numfound",Integer.parseInt(a.getAttrValue("numFound")));
            Vector<XMLElement> doclist=a.getList("doc");
            
            for(XMLElement doc : doclist){
              JSONObject searchResult = new JSONObject();
              searchResults.put(searchResult);
              Vector<XMLElement> arrlist = doc.getList("arr");
              Vector<XMLElement> strlist = doc.getList("str");
              for(XMLElement arr : arrlist){
                String attrName = arr.getAttrValue("name");
                if(attrName.equals("url")){
                  String urlToCheck = arr.get("str").getValue().split("#")[0];
                  urlToCheck = urlToCheck.split("\\|")[0];
                  searchResult.put("url",urlToCheck);
                }
              }
              for(XMLElement str : strlist){
                String attrName = str.getAttrValue("name");
                if(attrName.equals("id")){
                  searchResult.put("id",str.getValue().split("\\|")[0]);
                  //
                }
              }
              
            }
          }
        }catch(Exception e){
          r.setErrorMessage("No name attribute");
          return r;
        }
      }
      
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    
        
    
    
  
    
    
    try {
      responseObj.put("results",searchResults );
    } catch (JSONException e) {
      r.setException("JSONException unable to put response", e);
      return r;
    }
    
    try {
      result.put("facets", facetsObj);
    } catch (JSONException e) {
      r.setException("JSONException unable to put facets", e);
      return r;
    }
    
   
    r.setMessage(result.toString());
  
    
    return r;
  }

 
  public JSONResponse checkURL(String query) {
    
    Debug.println("Checking: "+query);
    LockOnQuery.lock(query);

    JSONResponse r = _checkURL(query);
    
    LockOnQuery.release(query);
    Debug.println("Finished: "+query);
    
    return r;
  }
  
  public String getCatalog(String catalogURL) throws Exception{
  
    String response = DiskCache.get(cacheLocation, "dataset_"+catalogURL, 10*60);
    if(response!=null){
      Debug.println("CATALOG FROM CACHE "+catalogURL);
      return response;
    }
    boolean ISOK = false;
    String errorMessage = "";
    try {
      Debug.println("CATALOG GET "+catalogURL);
      response = HTTPTools.makeHTTPGetRequest(catalogURL);
      ISOK = true;
    } catch (WebRequestBadStatusException e) {
      Debug.println("CATALOG GET WebRequestBadStatusException");
      if(e.getStatusCode()==404){
        errorMessage = "Not found (404)";
      }else if(e.getStatusCode()==403){
        errorMessage = "Unauthorized (403)";
      }else{
        errorMessage = "Code ("+e.getStatusCode()+")";
      }
    } catch (IOException e) {
      Debug.println("CATALOG GET IOException");
      errorMessage = e.getMessage();
      
    }
    if(ISOK == false){
      throw new Exception("Unable to GET catalog "+catalogURL+" : "+errorMessage);
    }
    Debug.println("CATALOG GET SET");
    DiskCache.set_2(cacheLocation, "dataset_"+catalogURL, response);
    Debug.println("CATALOG GET SET DONE");
    return response;
  }
  
  public JSONResponse _checkURL(String query) {
    JSONObject jsonresult = new JSONObject();
    try{
      getCatalog(query);
      jsonresult.put("ok", "ok");
    }catch(Exception e){
      Debug.println(e.getMessage());
      try {
        jsonresult.put("ok", "false");
        jsonresult.put("message", e.getMessage());
      } catch (JSONException e1) {
      }
    }
    String message = jsonresult.toString();
    JSONResponse result = new JSONResponse();
    result.setMessage(message);
    return result;
  }


}