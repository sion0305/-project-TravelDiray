package ddwu.mobile.finalproject.ma02_20181022;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class TravelXmlParserByArea {

    public enum TagType { NONE, ADDRESS, IMAGE, TEL, TITLE, MAPX, MAPY};

    final static String TAG_ITEM = "item";//대소문자 구별해서 중요!
    final static String TAG_TITLE = "title";
    final static String TAG_ADDR = "addr1";
    final static String TAG_IMAGE = "firstimage";
    final static String TAG_TEL = "tel";
    final static String TAG_X = "mapx";
    final static String TAG_Y = "mapy";

    public TravelXmlParserByArea(){

    }

    public ArrayList<TravelDto> parse(String xml){
        ArrayList<TravelDto> resultList = new ArrayList();
        TravelDto dto = null;
        TagType tagType = TagType.NONE;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals(TAG_ITEM)) {
                            dto = new TravelDto();
                        } else if (parser.getName().equals(TAG_TITLE)) {
                            if (dto != null) tagType = TagType.TITLE;
                        } else if (parser.getName().equals(TAG_ADDR)) {
                            if (dto != null) tagType = TagType.ADDRESS;
                        } else if (parser.getName().equals(TAG_IMAGE)) {
                            if (dto != null) tagType = TagType.IMAGE;
                        } else if (parser.getName().equals(TAG_TEL)) {
                            if (dto != null) tagType = TagType.TEL;
                        }else if (parser.getName().equals(TAG_X)) {
                            if (dto != null) tagType = TagType.MAPX;
                        } else if (parser.getName().equals(TAG_Y)) {
                            if (dto != null) tagType = TagType.MAPY;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals(TAG_ITEM)) {
                            resultList.add(dto);
                            dto = null;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        switch(tagType) {
                            case TITLE:
                                dto.setTitle(parser.getText());
                                break;
                            case ADDRESS:
                                dto.setAddress(parser.getText());
                                break;
                            case IMAGE:
                                dto.setImageLink(parser.getText());
                                break;
                            case TEL:
                                dto.setTel(parser.getText());
                                break;
                            case MAPX:
                                dto.setMapx(parser.getText());
                                break;
                            case MAPY:
                                dto.setMapy(parser.getText());
                                break;
                        }
                        tagType = TagType.NONE;
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }
}
