package com.jtframework.utils;

import com.alibaba.fastjson.JSONObject;

/**
 * 中英翻译
 */
public class TranslateUtils {
    public enum Source{
        YOUDAO("有道","http://fanyi.youdao.com/translate?&doctype=json&type=AUTO&i=%s"),
        BING("Bing","http://api.microsofttranslator.com/v2/Http.svc/Translate?appId=A4D660A48A6A97CCA791C34935E4C02BBB1BEC1C&from=&to=en&text=%s");

        private String name;

        private String url;

        private Source(String name,String url){
            this.name = name;
            this.url = url;
        }
    }

    /**
     * 获取中文翻译
     * @param zhWord
     * @return
     * @throws Exception
     */
    public static String getLanguageTranslate(String zhWord) throws Exception{
        return getLanguageTranslate(zhWord,Source.YOUDAO);

    }

    /**
     * 获取中文翻译
     * @param zhWord
     * @param source
     * @return
     * @throws Exception
     */
    public static String getLanguageTranslate(String zhWord,Source source) throws Exception{
        /**
         *         {"type":"ZH_CN2EN","errorCode":0,"elapsedTime":0,"translateResult":[[{"src":"测试","tgt":"test"}]]}
         */
        zhWord = zhWord.replaceAll("[\\pP\\p{Punct}]","");
        String result = null;
        if(source.equals(Source.BING)){
            result = HttpClientUtils.executeGet(source.url.replace("%s",zhWord));
            result = result.substring(result.indexOf("\">")+2,result.indexOf("</string>"));
        }else {
            JSONObject dataObject = HttpClientUtils.get(source.url.replace("%s",zhWord));
            result = dataObject.getJSONArray("translateResult").getJSONArray(0).getJSONObject(0).getString("tgt");
        }
        return result.replaceAll("[\\pP\\p{Punct}]","");
    }


}
