package ddwu.mobile.finalproject.ma02_20181022;

import java.io.Serializable;

public class TravelDto implements Serializable {

    private long _id;
    private String title;
    private String address;
    private String tel;
    private String mapx, mapy;
    private String imageLink;
    private String imageFileName;
    private String photoFile;
    private String memo;
    private String date;

    public TravelDto(){

    }

    public TravelDto(long _id, String title, String address, String tel, String mapx, String mapy, String imageFileName, String memo, String date, String photoFile) {
        this._id = _id;
        this.title = title;
        this.address = address;
        this.tel = tel;
        this.mapx = mapx;
        this.mapy = mapy;
        this.imageFileName = imageFileName;
        this.photoFile = photoFile;
        this.memo = memo;
        this.date = date;
    }

    public TravelDto(String title, String address, String tel, String mapx, String mapy, String imageFileName, String memo) {
        this.title = title;
        this.address = address;
        this.tel = tel;
        this.mapx = mapx;
        this.mapy = mapy;
        this.imageFileName = imageFileName;
        this.memo = memo;
    }

    public TravelDto(long _id, String title, String address, String tel, String mapx, String mapy, String imageFileName, String memo, String date) {
        this._id = _id;
        this.title = title;
        this.address = address;
        this.tel = tel;
        this.mapx = mapx;
        this.mapy = mapy;
        this.imageFileName = imageFileName;
        this.memo = memo;
        this.date= date;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getMapx() {
        return mapx;
    }

    public void setMapx(String mapx) {
        this.mapx = mapx;
    }

    public String getMapy() {
        return mapy;
    }

    public void setMapy(String mapy) {
        this.mapy = mapy;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPhotoFile() {
        return photoFile;
    }

    public void setPhotoFile(String photoFile) {
        this.photoFile = photoFile;
    }
}
