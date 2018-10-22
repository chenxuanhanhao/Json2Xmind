package com.naah69;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmind.core.*;

import java.io.*;
import java.util.List;
import java.util.Set;

/**
 * json2xmind
 *
 * @author naah
 * @date 2018-10-22 下午1:00
 * @desc
 */
public class Json2xmind {
    private static Logger log= LoggerFactory.getLogger(Json2xmind.class);
    private static IWorkbook workbook;
    public static void main(String[] args) throws IOException, CoreException {
        String jsonPath="/Users/naah/Documents/projects/json2xmind/src/main/resources/example.json";
        String rootName="Naah";
        String savePath="/Users/naah/Documents/projects/json2xmind/src/main/resources/example.xmind";

        log.info("read JSON file:{}",jsonPath);
        String json = readJson(jsonPath);

        if (json==null||"".equals(json)){
            log.error("error:json is blank");
            System.exit(1);
        }

        JSONObject jsonObject=JSONObject.parseObject(json);
        log.info("init xmind");
        ITopic root=initXmind(rootName);

        log.info("parse json to xmind,start...");
        parseJson2Xmind(jsonObject, root);
        log.info("parse json to xmind,success...");

        workbook.save(savePath);
    }

    /**
     * json2xmind
     * @param jsonObject
     * @param root
     */
    private static void parseJson2Xmind(JSONObject jsonObject, ITopic root) {
        Set<String> keys = jsonObject.keySet();

        for (String key : keys) {
            log.info("parse key:{}",key);
            Object obj = jsonObject.get(key);
            if (obj instanceof  JSONObject)
            {
                JSONObject keyJsonObj=(JSONObject)obj;
                ITopic topic = workbook.createTopic();
                topic.setTitleText(key);
                root.add(topic);
                if (keyJsonObj.keySet()==null){
                    topic.setTitleText(key+":"+keyJsonObj.getString(key));
                }else{
                    parseJson2Xmind(keyJsonObj,topic);
                }

            }else if (obj instanceof JSONArray){
                JSONArray array=(JSONArray)obj;
                List<JSONObject> jsonObjectsList = array.toJavaList(JSONObject.class);
                ITopic topic = workbook.createTopic();
                topic.setTitleText(key);
                root.add(topic);
                int i=1;
                for (JSONObject listObj : jsonObjectsList) {
                    ITopic subTopic = workbook.createTopic();
                    subTopic.setTitleText(key+i++);
                    topic.add(subTopic);
                    if (listObj.keySet()==null){
                        topic.setTitleText(key+":"+listObj.getString(key));
                    }else{
                        parseJson2Xmind(listObj,subTopic);
                    }

                }
            }else{
                ITopic topic = workbook.createTopic();
                topic.setTitleText(key+":"+obj.toString());
                root.add(topic);

            }
        }
    }

    /**
     * init Xmind
     * @param rootName name of rootTopic
     * @return
     */
    private static ITopic initXmind(String rootName) {
        IWorkbookBuilder builder = Core.getWorkbookBuilder();//初始化builder
        workbook = builder.createWorkbook();
        ISheet sheet = workbook.getPrimarySheet();
        ITopic rootTopic = sheet.getRootTopic();
        rootTopic.setTitleText(rootName);
        return rootTopic;
    }

    /**
     * read Json
     * @param jsonPath
     * @return
     */
    private static String readJson(String jsonPath) {
            StringBuilder sb=new StringBuilder();
        try(BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(new File(jsonPath))))) {
            String line=null;
            while((line=br.readLine())!=null){
                sb.append(line);
            }
        }catch (Exception e){
            log.error("read json error",e);
            System.exit(1);
        }
        return sb.toString();
    }
}
