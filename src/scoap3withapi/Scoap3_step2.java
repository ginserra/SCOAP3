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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author Carla Carrubba
 * @author Giusi Inserra 
 */
public class Scoap3_step2 {

    static ArrayList listRecordsNOINFN;
    static ArrayList listFinalINFN;

    public static void executeStep2(String nameDirSource, String date) {
        

        String dir_destination=createDirDestination(date);

        File dir = new File(nameDirSource);
        String[] children = dir.list();
        if (children != null) {
            for (int i = 0; i < children.length; i++) {
                // Get filename of file or directory
                String filename = children[i];
                System.out.println("-------FILENAME---" + i + ")--->" + filename);

                String pathFile = nameDirSource + "/" + filename;

                findRecordsINFNandNotINFN(pathFile);
                createFile(listFinalINFN, Integer.toString(i), pathFile, "INFN", "PUBLICATIONINFN", dir_destination);
                createFile(listRecordsNOINFN, Integer.toString(i), pathFile, "OTHER", "PUBLICATIONOTHER",dir_destination);
            }
        }
    }

    private static String createDirDestination(String date) {
        String Dir = "MARCXML_SCOAP3_DIVISION_from_"+Scoap3withAPI.startDate+"_to_"+Scoap3withAPI.todayDate;

        File f = new File(Dir);

        boolean esiste = f.exists();

        if (!esiste) {
            boolean success = (new File(Dir)).mkdir();

            if (success) {
              
                File dirINFN = new File(Dir+"/INFN");
                if(!dirINFN.exists())
                    (new File(Dir+"/INFN")).mkdir();
                File dirOTHER = new File(Dir+"/OTHER");
                if(!dirOTHER.exists())
                    (new File(Dir+"/OTHER")).mkdir();


            } else {
                System.out.println("Impossible to create: " + Dir);
            }
        } else {
          
            File directoryINFN = new File(Dir+"/INFN");
            if (directoryINFN.exists()) {
                File[] filesINFN = directoryINFN.listFiles();
                for (File ffINFN : filesINFN) {
                    ffINFN.delete();
                }
            }
            File directoryOTHER = new File(Dir+"/OTHER");
            if (directoryOTHER.exists()) {
                File[] filesOTHER = directoryOTHER.listFiles();
                for (File ffOTHER : filesOTHER) {
                    ffOTHER.delete();
                }
            }
        }

        return Dir;
    }

    public static void findRecordsINFNandNotINFN(String pathFile) {

        int start = 0;
        int stop = 100;

        ArrayList listRecordsINFN = new ArrayList();
        try {

            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(new File(pathFile));

          
            Element root = document.getRootElement();
            String nomeRadice = root.getName();
          
          
            List children = root.getChildren();
            Iterator iterator = children.iterator();

          

            while (iterator.hasNext()) {

                
                Element itemRecord = (Element) iterator.next();
                String nomeTag = itemRecord.getName();
             
                List childrenRecord = itemRecord.getChildren();
                Iterator iteratorRecord = childrenRecord.iterator();
                while (iteratorRecord.hasNext()) {
                    Element childRecord = (Element) iteratorRecord.next();
                    String childNome = childRecord.getName();
             

                    if (childNome.equals("datafield")) {
                        List childrenDataField = childRecord.getChildren();
                        Iterator iteratorchildrenDataField = childrenDataField.iterator();
                        while (iteratorchildrenDataField.hasNext()) {
                            Element childDataField = (Element) iteratorchildrenDataField.next();
                            String childDataFieldNome = childDataField.getName();
             
                            String valueDataField = childDataField.getText();

                            if (valueDataField.toUpperCase().contains("INFN") || valueDataField.toUpperCase().contains("I.N.F.N.")
                                    || valueDataField.toUpperCase().contains("NATIONAL INSTITUTE OF NUCLEAR PHYSICS")
                                    || valueDataField.toUpperCase().contains("ISTITUTO NAZIONALE DI FISICA NUCLEARE")) {
             
                                listRecordsINFN.add(start);
                            }



                        }
                    }
                }

                start++;

            }



        } catch (Exception e) {
            System.err.println("Error in reading file");
            e.printStackTrace();
        }

        listFinalINFN = listRecordsINFN;
        listFinalINFN = getListNotDuplicate(listRecordsINFN);
       // System.out.println("num_records INFN--->" + listFinalINFN.size());
       

        listRecordsNOINFN = new ArrayList();


        int count = 0;

        if (listFinalINFN.size() > 0) {

            for (int i = 0; i < listFinalINFN.size(); i++) {
                int elem = (Integer) listFinalINFN.get(i);



                while (count < 100) {
                    if (count != elem) {
                      
                        listRecordsNOINFN.add(count);
                        count++;

                    } else {
                        count = elem + 1;

                        if (i == listFinalINFN.size() - 1) {

                            while (count < 100) {
                                
                                listRecordsNOINFN.add(count);
                                count++;
                            }
                        }


                        break;
                    }

                }


            }

        } else {

            for (int i = 0; i < start; i++) {
                listRecordsNOINFN.add(i);
            }

        }



       // System.out.println("num_records NO INFN--->" + listRecordsNOINFN.size());



    }

    public static ArrayList getListNotDuplicate(ArrayList listOriginal) {

        ArrayList listNuova = new ArrayList();

        if (listOriginal.size() > 1) {
            int k = 1;
            int j, i = 0;
            boolean duplicato;
            listNuova.add(listOriginal.get(0));

            for (i = 1; i < listOriginal.size(); i++) {

                duplicato = false;

                for (j = 0; j < i; j++) {

                    if (listOriginal.get(i) == listOriginal.get(j)) {
                        
                        duplicato = true;
                    }

                }
                if (!duplicato) {

                    listNuova.add(listOriginal.get(i));

                }


            }


            return listNuova;
        } else {
            return listOriginal;
        }
    }

    public static void createFile(ArrayList listRecords, String index, String pathFile, String type, String typeCollection, String dir_dest) {

        int start = 0;
       


        try {


         
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(new File(pathFile));

         
            Element root = document.getRootElement();
            String nomeRadice = root.getName();
         
         
            List children = root.getChildren();
            Iterator iterator = children.iterator();




            Document docMarc = new Document();
            Element rootMarc = new Element("collection");
            docMarc.setRootElement(rootMarc);


         

            while (iterator.hasNext()) {


                Element itemRecord = (Element) iterator.next();


                for (int j = 0; j < listRecords.size(); j++) {

                    if (start == (Integer) listRecords.get(j)) {
                     

                        Element elemCopy = (Element) itemRecord.clone();


                        //ADD TAG COLLECTION

                        Element datafield = new Element("datafield");
                        datafield.setAttribute(new Attribute("tag", "980"));
                        datafield.setAttribute(new Attribute("ind1", " "));
                        datafield.setAttribute(new Attribute("ind2", " "));
                        Element subfield = new Element("subfield");
                        subfield.setAttribute(new Attribute("code", "a"));
                        subfield.setText(typeCollection);
                        elemCopy.addContent(datafield);
                        datafield.addContent(subfield);
                   
                   
                        //ADD TAG LANGUAGE
                        
                        Element datafield_lang = new Element("datafield");
                        datafield_lang.setAttribute(new Attribute("tag", "041"));
                        datafield_lang.setAttribute(new Attribute("ind1", " "));
                        datafield_lang.setAttribute(new Attribute("ind2", " "));
                        Element subfield_lang = new Element("subfield");
                        subfield_lang.setAttribute(new Attribute("code", "a"));
                        subfield_lang.setText("eng");
                        elemCopy.addContent(datafield_lang);
                        datafield_lang.addContent(subfield_lang);
                        
                        
                        //ADD TAG AUDIENCE
                        
                        Element datafield_audience = new Element("datafield");
                        datafield_audience.setAttribute(new Attribute("tag", "042"));
                        datafield_audience.setAttribute(new Attribute("ind1", " "));
                        datafield_audience.setAttribute(new Attribute("ind2", " "));
                        Element subfield_audience = new Element("subfield");
                        subfield_audience.setAttribute(new Attribute("code", "a"));
                        subfield_audience.setText("Researchers");
                        elemCopy.addContent(datafield_audience);
                        datafield_audience.addContent(subfield_audience);
                        
                        //ADD TAG TYPE

                        Element datafield_type = new Element("datafield");
                        datafield_type.setAttribute(new Attribute("tag", "043"));
                        datafield_type.setAttribute(new Attribute("ind1", " "));
                        datafield_type.setAttribute(new Attribute("ind2", " "));
                        Element subfield_type = new Element("subfield");
                        subfield_type.setAttribute(new Attribute("code", "a"));
                        subfield_type.setText("info:eu-repo/semantics/article");
                        elemCopy.addContent(datafield_type);
                        datafield_type.addContent(subfield_type);

                        elemCopy.detach();
                      
                        String nomeTag = itemRecord.getName();
                        rootMarc.addContent(elemCopy);


                    }
                }

                start++;

            }

            XMLOutputter xmlOutput = new XMLOutputter();
            String newpathFile=dir_dest+"/" + type + "/Records" + type + "_" + index + ".xml";
            
            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(docMarc, new FileWriter(newpathFile));
            deleteFirstRow(newpathFile);

        } catch (Exception e) {
            System.err.println("Error in reading file");
            e.printStackTrace();
        }




    }
    
    public static void deleteFirstRow(String pathFile) {
        FileInputStream fstream = null;
        DataInputStream in = null;
        BufferedWriter out = null;

        try {
            
            fstream = new FileInputStream(pathFile);

            
            in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            StringBuilder fileContent = new StringBuilder();

            
            strLine = br.readLine();
            while ((strLine = br.readLine()) != null) {
            


            
                fileContent.append(strLine);
                fileContent.append(System.getProperty("line.separator"));

            }

            
            FileWriter fstreamWrite = new FileWriter(pathFile);
            out = new BufferedWriter(fstreamWrite);
            out.write(fileContent.toString());

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            
            try {
                fstream.close();
                out.flush();
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
}
