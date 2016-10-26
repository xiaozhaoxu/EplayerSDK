package com.ibrightech.eplayer.sdk.common.entity;

import com.ibrightech.eplayer.sdk.common.entity.system.DocumentItem;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * Created by zhaoxu2014 on 16/7/1.
 */
public class MyXMLContentHandler extends DefaultHandler {
    private String tagName = null;//当前解析的元素标签
    public TreeMap<String, DocumentItem> sliderPPTMap = new TreeMap<String, DocumentItem>(new Comparator<String>() {
        @Override
        public int compare(String s, String s2) {
            try {
                int intger_s = Integer.valueOf(s);
                int intger_s2 = Integer.valueOf(s2);
                if (intger_s < intger_s2) {
                    return -1;
                } else if (intger_s == intger_s2) {
                    return 0;
                } else {
                    return 1;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }

        }
    });
    String page = "";
    String artwork = "";
    String thumbnail = "";

    //接收文档开始的通知。当遇到文档的开头的时候，调用这个方法，可以在其中做一些预处理的工作。
    @Override
    public void startDocument() throws SAXException {

    }

    //接收元素开始的通知。当读到一个开始标签的时候，会触发这个方法。其中namespaceURI表示元素的命名空间；
    //localName表示元素的本地名称（不带前缀）；qName表示元素的限定名（带前缀）；atts 表示元素的属性集合
    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {

        if (!localName.equals("title"))
            this.tagName = localName;
    }

    //接收字符数据的通知。该方法用来处理在XML文件中读到的内容，第一个参数用于存放文件的内容，
    //后面两个参数是读到的字符串在这个数组中的起始位置和长度，使用new String(ch,start,length)就可以获取内容。
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        if (tagName != null) {
            String data = new String(ch, start, length);
            if (tagName.equals("page")) {
                page = data;
            } else if (tagName.equals("artwork")) {
                artwork = data;
            } else if (tagName.equals("thumbnail")) {
                thumbnail = data;
            }
        }
    }

    //接收文档的结尾的通知。在遇到结束标签的时候，调用这个方法。其中，uri表示元素的命名空间；
    //localName表示元素的本地名称（不带前缀）；name表示元素的限定名（带前缀）
    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {

        if (localName.equals("slide")) {

            DocumentItem item = new DocumentItem();
            item.page = page;
            item.url = artwork;
            item.thumbUrl = thumbnail;

            sliderPPTMap.put(page, item);
            this.page = null;
            this.artwork = null;
            this.thumbnail = null;
        }

        this.tagName = null;
    }
}
