package com.ph.bittelasia.Model;

import java.io.Serializable;

public class VideoInfo implements Serializable {

    private int     no;
    private String  path;
    private String  name;
    private String  description;

    public VideoInfo(Builder builder)
    {
        this.no= builder.no;
        this.path=builder.path;
        this.name=builder.name;
        this.description=builder.description;
    }

    public int getNo() {
        return no;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name!=null?name:"no name";
    }

    public String getDescription() {
        return description!=null?description:"no description";
    }


    @Override
    public String toString() {
        return "VideoInfo{"+
                "no="+no+","+
                "path="+path+","+
                "name="+name+","+
                "description="+description+
                "}";
    }

    public static class Builder
    {
        private int     no;
        private String  name;
        private String  path;
        private String  description;

        public Builder no(int no)
        {
            this.no=no;
            return this;
        }

        public Builder name(String name)
        {
            this.name=name;
            return this;
        }

        public Builder desc(String desc)
        {
            this.description=desc;
            return this;
        }

        public Builder path(String path)
        {
            this.path=path;
            return this;
        }

        public VideoInfo buildVideoInfo()
        {
            return new VideoInfo(this);
        }
    }

}
