/** The MIT License (MIT)
*
* Copyright (c) 2014 INFN Division of Catania
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
*/


package scoap3withapi;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.soap.Text;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

/**
 *
 * @author Carla Carrubba
 * @author Giusi Inserra 
 */
public class Scoap3withAPI {

    /**
     * @param args the command line arguments
     */
   
        static GregorianCalendar gc = new GregorianCalendar();
        
        static int anno = gc.get(Calendar.YEAR);
        static int mese = gc.get(Calendar.MONTH) + 1;
        static int giorno = gc.get(Calendar.DATE);
        static int h=gc.getTime().getHours();
        static int m=gc.getTime().getMinutes();
        static int s=gc.getTime().getSeconds();
        
        
        static String startDate;
        static String todayDate=anno+"-"+mese+"-"+giorno+":"+h+":"+m+":"+s;
     
    public static void main(String[] args) {
      
        int jrec = 0;  //param "offset" for query
        int rg = 100;  //param "number record" for query
        String publickey="my-public-key";
        String privatekey="my-private-key";
        
        startDate="2014-12-23"; //param start haversting date
        
        String date=startDate+"-%3E9999-01-01"; //param "datacreated" for query
       
         
        
        String Dir = "MARCXML_SCOAP3_from_"+startDate+"_to_"+todayDate; //output directory

        File f = new File(Dir);

        boolean esiste = f.exists();

        if (!esiste) {
            boolean success = (new File(Dir)).mkdir();
            System.out.println("creata");
        }
        else{
            File directory= new File(Dir);
            
                File[] files = directory.listFiles();
                for (File ff : files) {
                    ff.delete();
                }
            }
        
        
        int numF = getNumRec(publickey,privatekey,date,jrec, rg);
        for (int i = 0; i < numF; i++) {
            
            System.out.println("PARAMS QUERY-->"+date+"  -- "+jrec +"  -- "+rg);
            
            writeFilesScoap3(publickey,privatekey,date,jrec, rg);
            jrec = jrec + rg + 1;
        }

      
     
        int nFiles =  f.listFiles().length;
        
        System.out.println("N File=>"+nFiles);
     
        for(int i=0; i<nFiles;i++){
            File [] files=f.listFiles();
            String name=files[i].getName();
                modifySCOAP3_MARCXML(name,date); //modify the scoap3 MARCXML schema into openaccessrepository MARCXML schema
        }
        
        
        Scoap3_step2.executeStep2("MARCXML_SCOAP3_FINAL_"+date, date); //division of infn publications from other publications
        
        deleteDirectory(new File("MARCXML_SCOAP3_FINAL_"+date));
        deleteDirectory(new File("MARCXML_SCOAP3_from_"+startDate+"_to_"+todayDate));
       

    } 
    
    
    
    public static boolean deleteDirectory(File path) {
            
        
        if(path.exists()) {
              File[] files = path.listFiles();
              for(int i=0; i<files.length; i++) {
                    if(files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    }
                    else {
                        files[i].delete();
                    }
              }
      }
      return(path.delete());
}

    public static HttpMethod callAPISCOAP3(String publickey, String privatekey,String date,int jrec, int num_rec) {
       
        HttpMethod method =null;
        
        
        try {
      
            
          String signature= HmacSha1Signature.calculateRFC2104HMAC("http://api.scoap3.org/search?apikey="+publickey+"&jrec=" + jrec + "&of=xm&p=datecreated%3A"+date+ "&rg=" + num_rec,privatekey);
        
          method=new GetMethod("http://api.scoap3.org/search?apikey="+publickey+"&jrec=" + jrec + "&of=xm&p=datecreated%3A"+date+ "&rg=" + num_rec+"&signature="+signature);
            
          
        } catch (SignatureException ex) {
            Logger.getLogger(Scoap3withAPI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Scoap3withAPI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(Scoap3withAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
          return method;

    }
    
    
    

    public static int getNumRec(String publickey,String privatekey,String date,int jrec, int num_rec) {


        HttpClient client = new HttpClient();
        HttpMethod method = callAPISCOAP3(publickey,privatekey,date,jrec, num_rec);



        double numRec = 0;
        int numFor = 0;
        String responseXML = null;
        BufferedReader br = null;
        try {
            client.executeMethod(method);
        } catch (IOException ex) {
            Logger.getLogger(Scoap3withAPI.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (method.getStatusCode() == HttpStatus.SC_OK) {
            try {
                method.getResponseBody();


                responseXML = convertStreamToString(method.getResponseBodyAsStream());

                System.out.println("RESPONSE XML "+responseXML);
                numRec = Double.parseDouble(responseXML.split("Results:")[1].split("-->")[0].replace(" ", ""));



                System.out.println("NUM REC=>" + numRec / 100);


                numFor = (int) Math.ceil(numRec / 100);


                System.out.println("NUM REC=>" + numFor);



                method.releaseConnection();




            } catch (IOException ex) {
                Logger.getLogger(Scoap3withAPI.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return numFor;
    }

    public static void writeFilesScoap3(String publickey,String privatekey,String date,int jrec, int num_rec) {
        String responseXML = null;
        HttpClient client = new HttpClient();
        HttpMethod method = callAPISCOAP3(publickey,privatekey,date,jrec, num_rec);

        try {
            client.executeMethod(method);

            if (method.getStatusCode() == HttpStatus.SC_OK) {

              
               responseXML = convertStreamToString(method.getResponseBodyAsStream());
            
                FileWriter fw = new FileWriter("MARCXML_SCOAP3_from_"+startDate+"_to_"+todayDate+"/marcXML_scoap3_" + jrec + "_" + num_rec + ".xml");

                fw.append(responseXML);

                fw.close();


            }
        } catch (IOException e) {
            e.printStackTrace();

        } finally {

            method.releaseConnection();

        }


    }
    
    
    
    

    public static String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {

            Writer writer = new StringWriter();



            char[] buffer = new char[1024];

            try {

                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                int n;

                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);

                }

            } finally {

                is.close();

            }

            return writer.toString();

        } else {

            return "";

        }

    }



  
  public static void modifySCOAP3_MARCXML(String filename, String date) {

        


        try {

            String pathFile="MARCXML_SCOAP3_from_"+startDate+"_to_"+todayDate+"/"+filename;

            SAXBuilder builder = new SAXBuilder();

            Document document = builder.build(new File(pathFile));



            Element root = document.getRootElement();
          
          
            
            List children = root.getChildren();
            Iterator iterator = children.iterator();


            
            while (iterator.hasNext()) {
                Element itemRecord = (Element) iterator.next();
          
                
                List childrenRecord = itemRecord.getChildren();
                Iterator iteratorRecord = childrenRecord.iterator();
         
                int counttag_856=0;
                while (iteratorRecord.hasNext()) {
                    Element childRecord = (Element) iteratorRecord.next();
                    String childName = childRecord.getName();


                    if (childName.equals("datafield")) {

                        String attributeTag = childRecord.getAttribute("tag").getValue();

                        
                        
                        
                        if (attributeTag.equals("856")) {
                            
                            
                            
                           counttag_856++;
                    



                            List childrenDataField = childRecord.getChildren();
                            Iterator iteratorchildrenDataField = childrenDataField.iterator();
                            
                            while (iteratorchildrenDataField.hasNext()) {
                                
                                Element childDataField = (Element) iteratorchildrenDataField.next();
                                String childDataFieldNome = childDataField.getName();

                                String valueDataField = childDataField.getText();




                                String attribute_code = childDataField.getAttribute("code").getValue();
                                if (attribute_code.equals("s")) {

                    
                                    iteratorchildrenDataField.remove();
                                }
                                 if (attribute_code.equals("x")) {

                    
                                    iteratorchildrenDataField.remove();
                                }
                                if (attribute_code.equals("u")) {
                                        childDataField.setAttribute("code", "a");

                                }

                                
                                
                                if(counttag_856==3){
                                     if (valueDataField.contains("subformat")) {

                                        iteratorRecord.remove();
                                
                                 }
                                }
                            }

                            childRecord.setAttribute("tag", "FFT");
                            childRecord.setAttribute("ind1", "");

                       
                        }
                         
                    }
                    if (childName.equals("controlfield")) {

                        iteratorRecord.remove();

                    }
                }


            }
            
            
        String Dir_final = "MARCXML_SCOAP3_FINAL_"+date;

        File f = new File(Dir_final);

        boolean esiste = f.exists();

        if (!esiste) {
            boolean success = (new File(Dir_final)).mkdir();
         
        }

            XMLOutputter xmlOutput = new XMLOutputter();

           
            xmlOutput.setFormat(Format.getPrettyFormat());
      
            xmlOutput.output(document, new FileWriter(Dir_final+"/final_"+filename));


        } catch (Exception e) {
            System.err.println("Error in reading file");
            e.printStackTrace();
        }


    }
    }
